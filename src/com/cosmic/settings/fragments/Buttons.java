package com.cosmic.settings.fragments;

import android.content.ContentResolver;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.ListPreference;
import android.widget.Toast;

import com.android.internal.util.cosmic.CosmicUtils;
import com.android.settings.R;
import com.android.settings.cosmic.CustomSettingsPreferenceFragment;

public class Buttons extends CustomSettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static final String TAG = "Buttons";
    private static final String VOLUME_BUTTON_MUSIC_CONTROL = "volume_button_music_control";
    private static final String TORCH_POWER_BUTTON_GESTURE = "torch_power_button_gesture";
    private ListPreference mTorchPowerButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.buttons);
        addCustomPreference(findPreference(VOLUME_BUTTON_MUSIC_CONTROL), SYSTEM_TWO_STATE, STATE_OFF);

        PreferenceScreen prefSet = getPreferenceScreen();
        if (!CosmicUtils.deviceSupportsFlashLight(getContext())) {
            Preference toRemove = prefSet.findPreference(TORCH_POWER_BUTTON_GESTURE);
            if (toRemove != null) {
                prefSet.removePreference(toRemove);
            }
        } else {
            mTorchPowerButton = (ListPreference) findPreference(TORCH_POWER_BUTTON_GESTURE);
            mTorchPowerButton.setOnPreferenceChangeListener(this);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mTorchPowerButton) {
            int torchPowerButtonValue = Integer.valueOf((String) objValue);
            boolean doubleTapCameraGesture = Settings.Secure.getInt(resolver,
                    Settings.Secure.CAMERA_DOUBLE_TAP_POWER_GESTURE_DISABLED, 0) == 0;
            if (torchPowerButtonValue == 1 && doubleTapCameraGesture) {
                // if double-tap for torch is enabled, switch off double-tap for camera
                Settings.Secure.putInt(resolver,
                        Settings.Secure.CAMERA_DOUBLE_TAP_POWER_GESTURE_DISABLED, 1);
                Toast.makeText(getActivity(),
                        (R.string.torch_power_button_gesture_dt_toast),
                        Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return false;
    }
}
