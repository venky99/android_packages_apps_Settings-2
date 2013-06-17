/*
 * Copyright (C) 2013 The LiquidSmooth Project
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

package com.android.settings.liquid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.util.Helpers;

public class TabletSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String KEY_FORCE_DUAL_PANE = "force_dual_pane";
    private static final String KEY_USER_MODE_UI = "user_mode_ui";
    private static final String KEY_HIDE_EXTRAS = "hide_extras";
    
    private Preference mLcdDensity;
    private CheckBoxPreference mDualPane;
    private ListPreference mUserModeUI;
    private CheckBoxPreference mHideExtras;

    private int newDensityValue;
    DensityChanger densityFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.tablet_settings);
        PreferenceScreen prefSet = getPreferenceScreen();

        mLcdDensity = findPreference("lcd_density_setup");
        mLcdDensity.setOnPreferenceChangeListener(this);
        String currentProperty = SystemProperties.get("ro.sf.lcd_density");
        try {
            newDensityValue = Integer.parseInt(currentProperty);
        } catch (Exception e) {
            getPreferenceScreen().removePreference(mLcdDensity);
        }
        mLcdDensity.setSummary(getResources().getString(R.string.current_lcd_density) + currentProperty);

        mDualPane = (CheckBoxPreference) findPreference(KEY_FORCE_DUAL_PANE);
        mDualPane.setOnPreferenceChangeListener(this);
        boolean preferDualPane = getResources().getBoolean(
                com.android.internal.R.bool.preferences_prefer_dual_pane);
        boolean dualPaneMode = Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.DUAL_PANE_PREFS, (preferDualPane ? 1 : 0)) == 1;
        mDualPane.setChecked(dualPaneMode);

        mHideExtras = (CheckBoxPreference) findPreference(KEY_HIDE_EXTRAS);
        mHideExtras.setChecked(Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.HIDE_EXTRAS_SYSTEM_BAR, 0) == 1);

        mUserModeUI = (ListPreference) findPreference(KEY_USER_MODE_UI);
        int uiMode = Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.CURRENT_UI_MODE, 0);
        mUserModeUI.setValue(Integer.toString(Settings.System.getInt(
                getActivity().getContentResolver(), Settings.System.USER_UI_MODE, uiMode)));
        mUserModeUI.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mDualPane) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.DUAL_PANE_PREFS,
                    (Boolean) newValue ? 1 : 0);
            return true;
        } else if (preference == mUserModeUI) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.USER_UI_MODE, Integer.parseInt((String) newValue));
            Helpers.restartSystemUI();
            return true;
        } else if (preference == mHideExtras) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.HIDE_EXTRAS_SYSTEM_BAR,
                    ((CheckBoxPreference) preference).isChecked() ? 1 : 0);
            return true;  
        }
        return false;
    }
}
