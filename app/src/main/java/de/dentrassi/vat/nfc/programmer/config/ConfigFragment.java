package de.dentrassi.vat.nfc.programmer.config;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import de.dentrassi.vat.nfc.programmer.MainActivity;
import de.dentrassi.vat.nfc.programmer.R;
import de.dentrassi.vat.nfc.programmer.databinding.ConfigFragmentBinding;
import de.dentrassi.vat.nfc.programmer.nfc.Keys;

public class ConfigFragment extends Fragment {

    private ConfigFragmentBinding binding;

    public ConfigFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(final @NonNull LayoutInflater inflater, final @Nullable ViewGroup container, final @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.config_fragment, container, false);

        this.binding = ConfigFragmentBinding.bind(view);

        this.binding.importConfig.setOnClickListener(this::onImportConfig);
        this.binding.createDemoConfig.setOnClickListener(v -> new AlertDialog.Builder(requireContext())
                .setTitle(R.string.create_demo_configuration)
                .setMessage(R.string.message_demo_configuration)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    final FragmentActivity activity = getActivity();
                    if (activity instanceof MainActivity) {
                        ((MainActivity) activity).createDemoConfiguration();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show());
        this.binding.clearConfig.setOnClickListener(v -> new AlertDialog.Builder(requireContext())
                .setTitle(R.string.clear_configuration)
                .setMessage(R.string.message_clear_configuration)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    final FragmentActivity activity = getActivity();
                    if (activity instanceof MainActivity) {
                        ((MainActivity) activity).clearConfiguration();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show());

        configChanged();

        return view;
    }

    protected void onImportConfig(final View view) {
        final FragmentActivity activity = getActivity();

        String password = "";
        if (this.binding.importPassword.getText() != null) {
            password = this.binding.importPassword.getText().toString();
        }

        if (activity instanceof MainActivity) {
            ((MainActivity) activity).importConfig(password);
        }
    }

    public void configChanged() {
        final FragmentActivity activity = getActivity();

        if (activity instanceof MainActivity) {
            setConfig(((MainActivity) activity).getConfiguration());
        }
    }

    private void setConfig(@NonNull final Configuration configuration) {

        final Keys keys = configuration.getKeysFor("VAT");
        this.binding.createDemoConfig.setVisibility(keys == null ? View.VISIBLE : View.GONE);
        this.binding.clearConfig.setVisibility(configuration.getOrganizations().isEmpty() ? View.GONE : View.VISIBLE);
        if (keys == null) {
            this.binding.vatKeyA.setText(R.string.key_missing);
            this.binding.vatKeyB.setText(R.string.key_missing);
        } else {
            this.binding.vatKeyA.setText(R.string.key_present);
            this.binding.vatKeyB.setText(R.string.key_present);
        }

    }

}
