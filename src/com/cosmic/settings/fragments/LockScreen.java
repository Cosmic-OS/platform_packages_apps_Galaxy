package com.cosmic.settings.fragments;

import android.os.Bundle;

import com.android.settings.R;
import com.android.settings.cosmic.CustomSettingsPreferenceFragment;

public class LockScreen extends CustomSettingsPreferenceFragment {
    private static final String TAG = "LockScreen";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.lock_screen);
    }
}
