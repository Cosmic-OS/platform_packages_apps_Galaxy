/*
 * Copyright (C) 2016 Brett Rogers
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

package com.cosmic.galaxy.statusbar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.internal.logging.MetricsProto.MetricsEvent;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import net.cosmic.colorpicker.preference.ColorPickerPreference;

public class StatusBarLogo extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String STATUS_BAR_COSMIC_LOGO_SHOW = "status_bar_cosmic_logo_show";
    private static final String STATUS_BAR_COSMIC_LOGO_SHOW_ON_LOCK_SCREEN = "status_bar_cosmic_logo_show_on_lock_screen";
    private static final String KEY_COSMIC_LOGO_STYLE = "status_bar_cosmic_logo_style";
    private static final String KEY_COSMIC_LOGO_COLOR = "status_bar_cosmic_logo_color";
    private static final String STATUS_BAR_COSMIC_LOGO_COLOR_DARK_MODE = "status_bar_cosmic_logo_color_dark_mode";
    private static final String PREF_NUMBER_OF_NOTIFICATION_ICONS = "logo_number_of_notification_icons";
    private static final String PREF_HIDE_LOGO = "logo_hide_logo";

    private static final int DEFAULT_COLOR = 0xffffffff;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

    private SwitchPreference mShowLogo;
    private SwitchPreference mShowLogoKeyguard;
    private ListPreference mLogoStyle;
    private ColorPickerPreference mLogoColor;
    private ColorPickerPreference mLogoColorDarkMode;
    private SwitchPreference mHideLogo;
    private ListPreference mNumberOfNotificationIcons;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refreshSettings();
    }

    public void refreshSettings() {
        PreferenceScreen prefs = getPreferenceScreen();
        if (prefs != null) {
            prefs.removeAll();
        }

        addPreferencesFromResource(R.xml.status_bar_logo);
        mResolver = getActivity().getContentResolver();

        int intColor;
        String hexColor;

        mShowLogo =
                (SwitchPreference) findPreference(STATUS_BAR_COSMIC_LOGO_SHOW);
        mShowLogo.setChecked(Settings.System.getInt(mResolver,
                "status_bar_cosmic_logo_show", 1) == 1);
        mShowLogo.setOnPreferenceChangeListener(this);

        mShowLogoKeyguard =
                (SwitchPreference) findPreference(STATUS_BAR_COSMIC_LOGO_SHOW_ON_LOCK_SCREEN);
        mShowLogoKeyguard.setChecked(Settings.System.getInt(mResolver,
                "status_bar_cosmic_logo_show_on_lock_screen", 0) == 1);
        mShowLogoKeyguard.setOnPreferenceChangeListener(this);

        mLogoStyle = (ListPreference) findPreference(KEY_COSMIC_LOGO_STYLE);
        int LogoStyle = Settings.System.getInt(mResolver,
                "status_bar_cosmic_logo_style", 1);
        mLogoStyle.setValue(String.valueOf(LogoStyle));
        mLogoStyle.setSummary(mLogoStyle.getEntry());
        mLogoStyle.setOnPreferenceChangeListener(this);

        mHideLogo =
                (SwitchPreference) findPreference(PREF_HIDE_LOGO);
        mHideLogo.setChecked(Settings.System.getInt(mResolver,
               "status_bar_cosmic_logo_hide_logo", 1) == 1);
        mHideLogo.setOnPreferenceChangeListener(this);

        mNumberOfNotificationIcons =
                (ListPreference) findPreference(PREF_NUMBER_OF_NOTIFICATION_ICONS);
        int numberOfNotificationIcons = Settings.System.getInt(mResolver,
               "status_bar_cosmic_logo_number_of_notification_icons", 4);
        mNumberOfNotificationIcons.setValue(String.valueOf(numberOfNotificationIcons));
        mNumberOfNotificationIcons.setSummary(mNumberOfNotificationIcons.getEntry());
        mNumberOfNotificationIcons.setOnPreferenceChangeListener(this);

        // logo color
        mLogoColor =
            (ColorPickerPreference) findPreference(KEY_COSMIC_LOGO_COLOR);
        mLogoColor.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(mResolver,
                "status_bar_cosmic_logo_color", 0xffffffff);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mLogoColor.setSummary(hexColor);
        mLogoColor.setNewPreviewColor(intColor);
        mLogoColor.setAlphaSliderVisible(true);

        // logo color dark mode
        mLogoColorDarkMode =
                (ColorPickerPreference) findPreference(STATUS_BAR_COSMIC_LOGO_COLOR_DARK_MODE);
        mLogoColorDarkMode.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(mResolver,
                "status_bar_cosmic_logo_color_dark_mode", 0xff000000);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mLogoColorDarkMode.setSummary(hexColor);
        mLogoColorDarkMode.setNewPreviewColor(intColor);
        mLogoColorDarkMode.setAlphaSliderVisible(true);

        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MENU_RESET, 0, R.string.reset)
                .setIcon(R.drawable.ic_action_reset)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_RESET:
                showDialogInner(DLG_RESET);
                return true;
             default:
                return super.onContextItemSelected(item);
        }
    }
    
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mShowLogo) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    "status_bar_cosmic_logo_show",
                    value ? 1 : 0);
            return true;
        } else if (preference == mShowLogoKeyguard) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    "status_bar_cosmic_logo_show_on_lock_screen",
                    value ? 1 : 0);
            return true;
        } else if (preference == mLogoStyle) {
            int LogoStyle = Integer.valueOf((String) newValue);
            int index = mLogoStyle.findIndexOfValue((String) newValue);
            Settings.System.putInt(
                    mResolver, "status_bar_cosmic_logo_style", LogoStyle);
            mLogoStyle.setSummary(mLogoStyle.getEntries()[index]);
            return true;
        } else if (preference == mHideLogo) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    "status_bar_cosmic_logo_hide_logo",
                    value ? 1 : 0);
            return true;
        } else if (preference == mNumberOfNotificationIcons) {
            int intValue = Integer.valueOf((String) newValue);
            int index = mNumberOfNotificationIcons.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    "status_bar_cosmic_logo_number_of_notification_icons",
                    intValue);
            preference.setSummary(mNumberOfNotificationIcons.getEntries()[index]);
            return true;
        } else if (preference == mLogoColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    "status_bar_cosmic_logo_color", intHex);
            return true;
        } else if (preference == mLogoColorDarkMode) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    "status_bar_cosmic_logo_color_dark_mode", intHex);
            return true;
        }
        return false;
    }
    
    private void showDialogInner(int id) {
        DialogFragment newFragment = MyAlertDialogFragment.newInstance(id);
        newFragment.setTargetFragment(this, 0);
        newFragment.show(getFragmentManager(), "dialog " + id);
    }

    public static class MyAlertDialogFragment extends DialogFragment {

        public static MyAlertDialogFragment newInstance(int id) {
            MyAlertDialogFragment frag = new MyAlertDialogFragment();
            Bundle args = new Bundle();
            args.putInt("id", id);
            frag.setArguments(args);
            return frag;
        }

        StatusBarLogo getOwner() {
            return (StatusBarLogo) getTargetFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int id = getArguments().getInt("id");
            switch (id) {
                case DLG_RESET:
                    return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.reset)
                    .setMessage(R.string.reset_color_title)
                    .setNegativeButton(R.string.cancel, null)
                    .setNeutralButton(R.string.reset_android_title,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    "status_bar_cosmic_logo_show", 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    "status_bar_cosmic_logo_show_on_lock_screen", 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    "status_bar_cosmic_logo_style", 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    "status_bar_cosmic_logo_hide_logo", 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    "status_bar_cosmic_logo_number_of_notification_icons", 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    "status_bar_cosmic_logo_color",
                                    DEFAULT_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                    "status_bar_cosmic_logo_color_dark_mode",
                                    0xff000000);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.reset_cosmic_title,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    "status_bar_cosmic_logo_show", 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    "status_bar_cosmic_logo_show_on_lock_screen", 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    "status_bar_cosmic_logo_style", 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    "status_bar_cosmic_logo_hide_logo", 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    "status_bar_cosmic_logo_number_of_notification_icons", 4);
                            Settings.System.putInt(getOwner().mResolver,
                                    "status_bar_cosmic_logo_color",
                                    0xffffffff);
                            Settings.System.putInt(getOwner().mResolver,
                                    "status_bar_cosmic_logo_color_dark_mode",
                                    0xffff0000);
                            getOwner().refreshSettings();
                        }
                    })
                    .create();
            }
            throw new IllegalArgumentException("unknown id " + id);
        }

        @Override
        public void onCancel(DialogInterface dialog) {

        }
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.GALAXY;
    }
}
