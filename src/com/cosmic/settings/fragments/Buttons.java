package com.cosmic.settings.fragments;

import android.os.Bundle;

import com.android.settings.R;
import com.android.settings.cosmic.CustomSettingsPreferenceFragment;

public class Buttons extends CustomSettingsPreferenceFragment {
    private static final String TAG = "Buttons";
    private static final String VOLUME_BUTTON_MUSIC_CONTROL = "volume_button_music_control";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addCustomPreference(findPreference(VOLUME_BUTTON_MUSIC_CONTROL), SYSTEM_TWO_STATE, STATE_OFF);
        addPreferencesFromResource(R.xml.buttons);
    }
}
