package com.cosmic.settings.fragments;

import android.os.Bundle;
import com.android.settings.R;
import com.android.settings.cosmic.CustomSettingsPreferenceFragment;

import com.android.settings.SettingsPreferenceFragment;

public class MiscSettings extends CustomSettingsPreferenceFragment {

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.misc_settings);

    }
}
