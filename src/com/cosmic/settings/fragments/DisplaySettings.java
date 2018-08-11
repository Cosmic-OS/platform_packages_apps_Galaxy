package com.cosmic.settings.fragments;

import android.os.Bundle;
import com.android.settings.R;
import com.android.settings.cosmic.CustomSettingsPreferenceFragment;

import com.android.settings.SettingsPreferenceFragment;

public class MiscSettings extends CustomSettingsPreferenceFragment {

    private static final String SMART_PIXELS = "smart_pixels";

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.display_settings);
        updateSmartPixelsPreference();
    }

    private void updateSmartPixelsPreference() {
        PreferenceScreen prefSet = getPreferenceScreen();
        boolean enableSmartPixels = getContext().getResources().
                getBoolean(com.android.internal.R.bool.config_enableSmartPixels);
        Preference smartPixels = findPreference(SMART_PIXELS);
         if (!enableSmartPixels){
            prefSet.removePreference(smartPixels);
        }
    }
}
