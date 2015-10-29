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

package com.cosmic.settings.fragments;

import android.content.Context;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.PreferenceCategory;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;
 
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.MetricsProto.MetricsEvent;
import com.android.settings.Utils;

public class StatusBarSettings extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String FORCE_EXPANDED_NOTIFICATIONS = "force_expanded_notifications";

    private SwitchPreference mForceExpanded;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.statusbar_settings);
        final PreferenceScreen prefScreen = getPreferenceScreen();

     mForceExpanded = (SwitchPreference) findPreference(FORCE_EXPANDED_NOTIFICATIONS);
        mForceExpanded.setOnPreferenceChangeListener(this);
        int ForceExpanded = Settings.System.getInt(getContentResolver(),
                FORCE_EXPANDED_NOTIFICATIONS, 0);
        mForceExpanded.setChecked(ForceExpanded != 0);
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.GALAXY;
    }

    @Override
     public void onResume() {
         super.onResume();
     }

    @Override
     public boolean onPreferenceChange(Preference preference, Object newValue) {
       if (preference == mForceExpanded) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getContentResolver(), FORCE_EXPANDED_NOTIFICATIONS,
                    value ? 1 : 0);
            return true;
          }
          return false;
     }
}
