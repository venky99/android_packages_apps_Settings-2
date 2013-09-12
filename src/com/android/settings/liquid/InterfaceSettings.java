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
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import android.text.Spannable;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.IWindowManager;
import android.view.Window;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.android.settings.R;
import com.android.settings.Utils;
import android.widget.EditText;
import com.android.settings.SettingsPreferenceFragment;

public class InterfaceSettings extends SettingsPreferenceFragment
    implements OnPreferenceChangeListener {

    public static final String TAG = "UserInterface";
    private static final String ADVANCED_SETTINGS = "interface_advanced";
    private static final String KEY_CARRIER_LABEL = "custom_carrier_label";
    private static final String KEY_HARDWARE_KEYS = "hardware_keys";
    private static final String KEY_NOTIF_STYLE = "notification_style";
    private static final String KEY_USE_ALT_RESOLVER = "use_alt_resolver";
    private static final String KEY_RECENTS_RAM_BAR = "recents_ram_bar";
    private static final String KEY_VIBRATION_MULTIPLIER = "vibrator_multiplier";
    private static final String KEY_LOW_BATTERY_WARNING_POLICY = "pref_low_battery_warning_policy";
    private static final String KEY_HALO_OPTIONS = "halo_options";
    private static final String KEY_TABLET_SETTINGS = "tablet_settings";

    private CheckBoxPreference mUseAltResolver;
    private PreferenceCategory mAdvanced;
    private Preference mCustomLabel;
    private Preference mNotifStyle;
    private Preference mHaloOptions;
    private Preference mTabletSettings;
    private Preference mRamBar;
    private ListPreference mVibrationMultiplier;
    private ListPreference mLowBatteryWarning;

    private String mCustomLabelText = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.interface_settings);
        PreferenceScreen prefs = getPreferenceScreen();

        mAdvanced = (PreferenceCategory) prefs.findPreference(ADVANCED_SETTINGS);

        mUseAltResolver = (CheckBoxPreference) findPreference(KEY_USE_ALT_RESOLVER);
        mUseAltResolver.setOnPreferenceChangeListener(this);
        mUseAltResolver.setChecked(Settings.System.getInt(
                getActivity().getContentResolver(),
                Settings.System.ACTIVITY_RESOLVER_USE_ALT, 0) == 1);

        mCustomLabel = findPreference(KEY_CARRIER_LABEL);
        mCustomLabel.setOnPreferenceClickListener(mCustomLabelClicked);
        updateCustomLabelTextSummary();

        mRamBar = findPreference(KEY_RECENTS_RAM_BAR);
        mRamBar.setOnPreferenceChangeListener(this);
        updateRamBar();
 
        mLowBatteryWarning = (ListPreference) findPreference(KEY_LOW_BATTERY_WARNING_POLICY);
        mLowBatteryWarning.setOnPreferenceChangeListener(this);
        int lowBatteryWarning = Settings.System.getInt(getActivity().getContentResolver(),
                                    Settings.System.POWER_UI_LOW_BATTERY_WARNING_POLICY, 0);
        mLowBatteryWarning.setValue(String.valueOf(lowBatteryWarning));
        mLowBatteryWarning.setSummary(mLowBatteryWarning.getEntry());

        mVibrationMultiplier = (ListPreference) findPreference(KEY_VIBRATION_MULTIPLIER);
        mVibrationMultiplier.setOnPreferenceChangeListener(this);
        String currentValue = Float.toString(Settings.System.getFloat(getActivity()
                .getContentResolver(), Settings.System.VIBRATION_MULTIPLIER, 1));
        mVibrationMultiplier.setValue(currentValue);
        mVibrationMultiplier.setSummary(currentValue);

        // Only show the hardware keys config on a device that does not have a navbar
        IWindowManager windowManager = IWindowManager.Stub.asInterface(
                ServiceManager.getService(Context.WINDOW_SERVICE));

        final boolean hasNavBarByDefault = getResources().getBoolean(
                com.android.internal.R.bool.config_showNavigationBar);

        if (hasNavBarByDefault) {
            // Let's assume they don't have hardware keys
            mAdvanced.removePreference(findPreference(KEY_HARDWARE_KEYS));
        }
        // Load preferences
        mNotifStyle = findPreference(KEY_NOTIF_STYLE);
        mHaloOptions = findPreference(KEY_HALO_OPTIONS);
        mTabletSettings = findPreference(KEY_TABLET_SETTINGS);
    }

    private void updateCustomLabelTextSummary() {
        mCustomLabelText = Settings.System.getString(getActivity().getContentResolver(),
                Settings.System.CUSTOM_CARRIER_LABEL);
        if (mCustomLabelText == null || mCustomLabelText.length() == 0) {
            mCustomLabel.setSummary(R.string.custom_carrier_label_notset);
        } else {
            mCustomLabel.setSummary(mCustomLabelText);
        }
    }

    private void updateRamBar() {
        int ramBarMode = Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.RECENTS_RAM_BAR_MODE, 0);
        if (ramBarMode != 0) {
            mRamBar.setSummary(getResources().getString(R.string.ram_bar_color_enabled));
        } else {
            mRamBar.setSummary(getResources().getString(R.string.ram_bar_color_disabled));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateRamBar();
    }

    @Override
    public void onPause() {
        super.onResume();
        updateRamBar();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mVibrationMultiplier) {
            String currentValue = (String) newValue;
            float val = Float.parseFloat(currentValue);
            Settings.System.putFloat(getActivity().getContentResolver(),
                    Settings.System.VIBRATION_MULTIPLIER, val);
            mVibrationMultiplier.setSummary(currentValue);
            return true;
        } else if (preference == mLowBatteryWarning) {
            int lowBatteryWarning = Integer.valueOf((String) newValue);
            int index = mLowBatteryWarning.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.POWER_UI_LOW_BATTERY_WARNING_POLICY,
                    lowBatteryWarning);
            mLowBatteryWarning.setSummary(mLowBatteryWarning.getEntries()[index]);
            return true;
        } else if (preference == mUseAltResolver) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.ACTIVITY_RESOLVER_USE_ALT,
                    (Boolean) newValue ? 1 : 0);
            return true;
        }
        return false;
    }

    public OnPreferenceClickListener mCustomLabelClicked = new OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle(R.string.custom_carrier_label_title);
            alert.setMessage(R.string.custom_carrier_label_explain);

            // Set an EditText view to get user input
            final EditText input = new EditText(getActivity());
            input.setText(mCustomLabelText != null ? mCustomLabelText : "");
            alert.setView(input);
            alert.setPositiveButton(getResources().getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String value = ((Spannable) input.getText()).toString();
                    Settings.System.putString(getActivity().getContentResolver(),
                            Settings.System.CUSTOM_CARRIER_LABEL, value);
                    updateCustomLabelTextSummary();
                    Intent i = new Intent();
                    i.setAction("com.android.settings.LABEL_CHANGED");
                    mContext.sendBroadcast(i);
                }
            });
            alert.setNegativeButton(getResources().getString(R.string.cancel),
                    new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                }
            });

            alert.show();
            return true;
        }
    };
}
