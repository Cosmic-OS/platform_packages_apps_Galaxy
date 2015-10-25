/*
 * Copyright (C) 2016 Cosmic-OS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cosmic.galaxy.buttons;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.Settings;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.PreferenceCategory;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.SwitchPreference;

import com.android.internal.logging.MetricsProto.MetricsEvent;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class VolumeCategory extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String RAS_VOLUME_KEYS_CURSOR_CONTROL = "ras_volume_keys_cursor_control";

    private ListPreference volumeKeysCursorControlListPref;

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.GALAXY;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.cosmic_volume);

        // volume keys cursor control
        volumeKeysCursorControlListPref = (ListPreference) findPreference(
                RAS_VOLUME_KEYS_CURSOR_CONTROL);
        if (volumeKeysCursorControlListPref != null) {
            volumeKeysCursorControlListPref.setOnPreferenceChangeListener(this);
            volumeKeysCursorControlListPref
                    .setValue(Integer.toString(Settings.System.getInt(getContentResolver(),
                            RAS_VOLUME_KEYS_CURSOR_CONTROL,
                            0)));
            volumeKeysCursorControlListPref.setSummary(volumeKeysCursorControlListPref.getEntry());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        // volume keys cursor control
        if (preference == volumeKeysCursorControlListPref) {
            int volumeKeyCursorControlValue = Integer.parseInt((String) newValue);
            Settings.System.putInt(getContentResolver(), RAS_VOLUME_KEYS_CURSOR_CONTROL,
                    volumeKeyCursorControlValue);
            int volumeKeyCursorControlIndex = volumeKeysCursorControlListPref
                    .findIndexOfValue((String) newValue);
            volumeKeysCursorControlListPref.setSummary(
                    volumeKeysCursorControlListPref.getEntries()[volumeKeyCursorControlIndex]);
            return true;
        }
        return false;
    }
}

