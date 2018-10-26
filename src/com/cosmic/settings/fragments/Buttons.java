package com.cosmic.settings.fragments;

import android.os.Bundle;

import com.android.settings.R;
import com.android.settings.cosmic.CustomSettingsPreferenceFragment;

public class Buttons extends CustomSettingsPreferenceFragment {
    private static final String TAG = "Buttons";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.buttons);
    }
}
