/*
 * Copyright (C) 2017 Cosmic-OS Project
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

package com.cosmic.settings.fragments;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

import com.android.settings.R;
import com.android.internal.logging.MetricsLogger;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

import com.android.internal.logging.MetricsProto.MetricsEvent;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class LogoSettings extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    public static final String TAG = "LogoSettings";

    private static final String KEY_COSMIC_LOGO_COLOR = "status_bar_cosmic_logo_color";
    private static final String KEY_COSMIC_LOGO_STYLE = "status_bar_cosmic_logo_style";

    private ColorPickerPreference mCosmicLogoColor;
    private ListPreference mCosmicLogoStyle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.cosmic_logo);

        PreferenceScreen prefSet = getPreferenceScreen();

        	// Cosmic logo color
        	mCosmicLogoColor =
            (ColorPickerPreference) prefSet.findPreference(KEY_COSMIC_LOGO_COLOR);
        	mCosmicLogoColor.setOnPreferenceChangeListener(this);
        	int intColor = Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_COSMIC_LOGO_COLOR, 0xffffffff);
       		String hexColor = String.format("#%08x", (0xffffffff & intColor));
            mCosmicLogoColor.setSummary(hexColor);
            mCosmicLogoColor.setNewPreviewColor(intColor);

            mCosmicLogoStyle = (ListPreference) findPreference(KEY_COSMIC_LOGO_STYLE);
            int cosmicLogoStyle = Settings.System.getIntForUser(getContentResolver(),
                    Settings.System.STATUS_BAR_COSMIC_LOGO_STYLE, 0,
                    UserHandle.USER_CURRENT);
            mCosmicLogoStyle.setValue(String.valueOf(cosmicLogoStyle));
            mCosmicLogoStyle.setSummary(mCosmicLogoStyle.getEntry());
            mCosmicLogoStyle.setOnPreferenceChangeListener(this);
    }

	@Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mCosmicLogoColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.STATUS_BAR_COSMIC_LOGO_COLOR, intHex);
            return true;
        } else if (preference == mCosmicLogoStyle) {
                int cosmicLogoStyle = Integer.valueOf((String) newValue);
                int index = mCosmicLogoStyle.findIndexOfValue((String) newValue);
                Settings.System.putIntForUser(
                        getContentResolver(), Settings.System.STATUS_BAR_COSMIC_LOGO_STYLE, cosmicLogoStyle,
                        UserHandle.USER_CURRENT);
                mCosmicLogoStyle.setSummary(
                        mCosmicLogoStyle.getEntries()[index]);
                return true;
        }
        return false;
    }


    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.GALAXY;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
