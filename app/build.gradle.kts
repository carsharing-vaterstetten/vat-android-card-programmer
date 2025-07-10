plugins {
    id("com.android.application")
}

android {
    namespace = "de.dentrassi.vat.nfc.programmer"
    compileSdk = 36

    defaultConfig {
        applicationId = "de.dentrassi.vat.nfc.programmer"
        minSdk = 29
        targetSdk = 36
        versionCode = 320
        versionName = "0.3.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {

    implementation("androidx.annotation:annotation:1.9.1")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.recyclerview:recyclerview:1.4.0")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.assertj:assertj-core:3.27.3")

    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    implementation("com.google.guava:guava:33.4.8-android")
    implementation("com.opencsv:opencsv:5.11.2")
    implementation("com.google.code.gson:gson:2.13.1")
}

afterEvaluate {
    tasks.withType(JavaCompile::class) {
        options.compilerArgs.add("-Xlint:unchecked")
        options.compilerArgs.add("-Xlint:deprecation")
    }
}