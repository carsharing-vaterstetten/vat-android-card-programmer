package de.dentrassi.vat.nfc.programmer.config;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.core.app.ActivityScenario;

import de.dentrassi.vat.nfc.programmer.MainActivity;
import de.dentrassi.vat.nfc.programmer.R;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;

@RunWith(AndroidJUnit4.class)
public class ConfigurationCompatibilityTest {
    @Test
    public void createsAndClearsDemoAcrossActivityRestarts() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> ((androidx.viewpager2.widget.ViewPager2)
                    activity.findViewById(R.id.view_pager)).setCurrentItem(3, false));
            onView(withId(R.id.createDemoConfig)).perform(click());
            onView(withText(android.R.string.ok)).inRoot(isDialog()).perform(click());
            onView(withId(R.id.vatKeyA)).check(matches(withText(R.string.key_present)));
            scenario.recreate();
            scenario.onActivity(activity -> ((androidx.viewpager2.widget.ViewPager2)
                    activity.findViewById(R.id.view_pager)).setCurrentItem(3, false));
            onView(withId(R.id.vatKeyB)).check(matches(withText(R.string.key_present)));
            onView(withId(R.id.clearConfig)).perform(click());
            onView(withText(android.R.string.cancel)).inRoot(isDialog()).perform(click());
            onView(withId(R.id.clearConfig)).check(matches(isDisplayed()));
            onView(withId(R.id.clearConfig)).perform(click());
            onView(withText(android.R.string.ok)).inRoot(isDialog()).perform(click());
            scenario.recreate();
            scenario.onActivity(activity -> ((androidx.viewpager2.widget.ViewPager2)
                    activity.findViewById(R.id.view_pager)).setCurrentItem(3, false));
            onView(withId(R.id.createDemoConfig)).check(matches(isDisplayed()));
            onView(withId(R.id.vatKeyA)).check(matches(withText(R.string.key_missing)));
        }
    }

    @Test
    public void importsExistingJsonAndPreservesItsFormat() throws Exception {
        final Configuration configuration = ConfigurationStore.load(new StringReader(
                "{\"organizations\":{\"VAT\":{\"keys\":{\"a\":\"FFEEDDCCBBAA\",\"b\":\"AABBCCDDEEFF\"}}}}"));
        assertSampleKeys(configuration);
        assertStoredConfiguration(configuration);
    }

    @Test
    public void demoSurvivesStorage() throws Exception {
        assertStoredConfiguration(Configuration.demo());
    }

    @Test
    public void importsEncryptedConfiguration() throws Exception {
        final String encrypted = "U2FsdGVkX1+kMBwcdE2etvIfCs1ppFWTFaEI/5m05UzRQPiEqObLrNrXHoHvJhdU\n"
                + "8uHIBLtYx+hV0pBCsG8UBJP2ZEh6NaG33ISkUyy4EjhTB1QhHfa8+8LnIl8TboYl\n"
                + "pRldtysfENYcbwXoFvbEHCRJsLlPkqp5GDqIpw6aiMHQCcCEj2FWppJY/IEEXJIS\n"
                + "w3SFKfqcaBkBhMm7WAsU3iAg8vpEscM84ZjffFytdnJXQ+BgnIsxBPQOGf3t/CCx\n";
        final Configuration configuration = ConfigurationStore.load(new ByteArrayInputStream(
                ConfigurationStore.decryptFromOpenSsl(encrypted, "foobar")));
        assertNotNull(configuration.getKeysFor("VAT"));
    }

    private static void assertStoredConfiguration(final Configuration configuration) throws Exception {
        final Path path = Files.createTempFile(InstrumentationRegistry.getInstrumentation()
                .getTargetContext().getCacheDir().toPath(), "configuration-", ".json");
        try {
            configuration.store(path);
            final String json = new String(Files.readAllBytes(path), java.nio.charset.StandardCharsets.UTF_8);
            assertTrue(json.contains("\"organizations\""));
            assertTrue(json.contains("\"keys\""));
            assertTrue(json.contains("\"a\":\"FFEEDDCCBBAA\""));
            assertTrue(json.contains("\"b\":\"AABBCCDDEEFF\""));
            assertSampleKeys(ConfigurationStore.load(path));
        } finally {
            Files.deleteIfExists(path);
        }
    }

    private static void assertSampleKeys(final Configuration configuration) {
        assertNotNull(configuration.getKeysFor("VAT"));
        assertEquals("FFEEDDCCBBAA", configuration.getKeysFor("VAT").getA().toString());
        assertEquals("AABBCCDDEEFF", configuration.getKeysFor("VAT").getB().toString());
    }
}
