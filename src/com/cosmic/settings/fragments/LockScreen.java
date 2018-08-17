package com.cosmic.settings.fragments;

import android.os.Bundle;

import com.android.settings.R;
import com.android.settings.cosmic.CustomSettingsPreferenceFragment;

public class LockScreen extends CustomSettingsPreferenceFragment {
    private static final String TAG = "LockScreen";
    private static final String LOCKSCREEN_PIN_SCRAMBLE_LAYOUT = "lockscreen_scramble_pin_layout";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.lock_screen);
        addCustomPreference(findPreference(LOCKSCREEN_PIN_SCRAMBLE_LAYOUT), SYSTEM_TWO_STATE, STATE_OFF);
    }
}
