package com.CS360.stocksense.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.CS360.stocksense.JoinOrganizationView;
import com.CS360.stocksense.R;
import com.CS360.stocksense.auth.SupabaseRepository;

public class SettingsFragment extends PreferenceFragmentCompat {
    private SupabaseRepository repository;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        repository = new SupabaseRepository(requireActivity());
        // Find "Change Organization" Preference
        changeOrganization();
    }

    private void changeOrganization(){
        Log.d("SettingsFragment", "onCreatePreferences() triggered");
        Preference changeOrgPref = findPreference("change_organization");
        String orgId = repository.getOrganization();
        changeOrgPref.setSummary("Current Organization: " + orgId);
        if (changeOrgPref != null) {
            changeOrgPref.setOnPreferenceClickListener(preference -> {
                Log.d(this.getClass().getSimpleName(), "Change Organization clicked");
                Intent intent = new Intent(getActivity(), JoinOrganizationView.class);
                startActivity(intent);
                return true;
            });
        }
    }
}