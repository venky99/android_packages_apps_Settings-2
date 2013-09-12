/*
 * Copyright (C) 2012 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.liquid.quicksettings;

import com.android.settings.R;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

/**
 * THIS CLASS'S DATA MUST BE KEPT UP-TO-DATE WITH THE DATA IN
 * com.android.systemui.statusbar.phone.QuickSettingsController
 */
public class QuickSettingsUtil {
    /**
     * START OF DATA MATCHING BLOCK
     */
    public static final String TILE_USER = "toggleUser";
    public static final String TILE_BATTERY = "toggleBattery";
    public static final String TILE_SETTINGS = "toggleSettings";
    public static final String TILE_WIFI = "toggleWifi";
    public static final String TILE_GPS = "toggleGPS";
    public static final String TILE_BLUETOOTH = "toggleBluetooth";
    public static final String TILE_BRIGHTNESS = "toggleBrightness";
    public static final String TILE_SOUND = "toggleSound";
    public static final String TILE_SYNC = "toggleSync";
    public static final String TILE_WIFIAP = "toggleWifiAp";
    public static final String TILE_SCREENTIMEOUT = "toggleScreenTimeout";
    public static final String TILE_MOBILEDATA = "toggleMobileData";
    public static final String TILE_MOBILENETWORK = "toggleMobileNetwork";
    public static final String TILE_LOCKSCREEN = "toggleLockScreen";
    public static final String TILE_NETWORKMODE = "toggleNetworkMode";
    public static final String TILE_AUTOROTATE = "toggleAutoRotate";
    public static final String TILE_AIRPLANE = "toggleAirplane";
    public static final String TILE_TORCH = "toggleFlashlight";
    public static final String TILE_SLEEP = "toggleSleepMode";
    public static final String TILE_LTE = "toggleLte";
    public static final String TILE_WIMAX = "toggleWimax";
    public static final String TILE_PROFILE = "toggleProfile";
    public static final String TILE_REBOOT = "toggleReboot";
    public static final String TILE_NFC = "toggleNfc";
    public static final String TILE_FCHARGE = "toggleFCharge";
    public static final String TILE_QUIETHOURS = "toggleQuietHours";
    public static final String TILE_VOLUME = "toggleVolume";
    public static final String TILE_EXPANDED_DESKTOP = "toggleExpandedDesktop";
    public static final String TILE_CUSTOMSHORTCUT = "toggleCustomShortcut";
    public static final String TILE_MUSIC = "toggleMusic";

    private static final String TILE_DELIMITER = "|";
    protected static ArrayList<String> TILES_DEFAULT = new ArrayList<String>();

    static {
        TILES_DEFAULT.add(TILE_USER);
        TILES_DEFAULT.add(TILE_BRIGHTNESS);
        TILES_DEFAULT.add(TILE_SETTINGS);
        TILES_DEFAULT.add(TILE_WIFI);
        TILES_DEFAULT.add(TILE_MOBILEDATA);
        TILES_DEFAULT.add(TILE_BATTERY);
        TILES_DEFAULT.add(TILE_AIRPLANE);
        TILES_DEFAULT.add(TILE_BLUETOOTH);
    }

    /**
     * END OF DATA MATCHING BLOCK
     */

    // Keep sorted according to titleResId's string value
    public static final LinkedHashMap<String, TileInfo> TILES = new LinkedHashMap<String, TileInfo>();
    static {
        TILES.put(TILE_AIRPLANE, new QuickSettingsUtil.TileInfo(
                TILE_AIRPLANE, R.string.title_tile_airplane,
                "com.android.systemui:drawable/ic_qs_airplane_off", true));
        TILES.put(TILE_BATTERY, new QuickSettingsUtil.TileInfo(
                TILE_BATTERY, R.string.title_tile_battery,
                "com.android.systemui:drawable/ic_qs_battery_neutral", true));
        TILES.put(TILE_BLUETOOTH, new QuickSettingsUtil.TileInfo(
                TILE_BLUETOOTH, R.string.title_tile_bluetooth,
                "com.android.systemui:drawable/ic_qs_bluetooth_neutral", true));
        TILES.put(TILE_BRIGHTNESS, new QuickSettingsUtil.TileInfo(
                TILE_BRIGHTNESS, R.string.title_tile_brightness,
                "com.android.systemui:drawable/ic_qs_brightness_auto_off", true));
        TILES.put(TILE_CUSTOMSHORTCUT, new QuickSettingsUtil.TileInfo(
                TILE_CUSTOMSHORTCUT, R.string.title_tile_customshortcut,
                "com.android.systemui:drawable/ic_qs_shortcut_andy", false));
        TILES.put(TILE_EXPANDED_DESKTOP, new QuickSettingsUtil.TileInfo(
                TILE_EXPANDED_DESKTOP, R.string.title_tile_expanded_desktop,
                "com.android.systemui:drawable/ic_qs_expanded_desktop_off", true));
        TILES.put(TILE_FCHARGE, new QuickSettingsUtil.TileInfo(
                TILE_FCHARGE, R.string.title_tile_fcharge,
                "com.android.systemui:drawable/ic_qs_fcharge_off", true));
        TILES.put(TILE_GPS, new QuickSettingsUtil.TileInfo(
                TILE_GPS, R.string.title_tile_gps,
                "com.android.systemui:drawable/ic_qs_gps_neutral", true));
        TILES.put(TILE_LOCKSCREEN, new QuickSettingsUtil.TileInfo(
                TILE_LOCKSCREEN, R.string.title_tile_lockscreen,
                "com.android.systemui:drawable/ic_qs_lock_screen_on", true));
        TILES.put(TILE_LTE, new QuickSettingsUtil.TileInfo(
                TILE_LTE, R.string.title_tile_lte,
                "com.android.systemui:drawable/ic_qs_lte_off", true));
        TILES.put(TILE_MUSIC, new QuickSettingsUtil.TileInfo(
                TILE_MUSIC, R.string.title_tile_music,
                "@*android:drawable/ic_media_play", true));
        TILES.put(TILE_MOBILEDATA, new QuickSettingsUtil.TileInfo(
                TILE_MOBILEDATA, R.string.title_tile_mobiledata,
                "com.android.systemui:drawable/ic_qs_mobildata_off", true));
        TILES.put(TILE_MOBILENETWORK, new QuickSettingsUtil.TileInfo(
                TILE_MOBILENETWORK, R.string.title_tile_mobilenetwork,
                "com.android.systemui:drawable/ic_qs_signal_4", true));
        TILES.put(TILE_NETWORKMODE, new QuickSettingsUtil.TileInfo(
                TILE_NETWORKMODE, R.string.title_tile_networkmode,
                "com.android.systemui:drawable/ic_qs_2g3g_on", true));
        TILES.put(TILE_NFC, new QuickSettingsUtil.TileInfo(
                TILE_NFC, R.string.title_tile_nfc,
                "com.android.systemui:drawable/ic_qs_nfc_off", true));
        TILES.put(TILE_AUTOROTATE, new QuickSettingsUtil.TileInfo(
                TILE_AUTOROTATE, R.string.title_tile_autorotate,
                "com.android.systemui:drawable/ic_qs_auto_rotate", true));
        TILES.put(TILE_SLEEP, new QuickSettingsUtil.TileInfo(
                TILE_SLEEP, R.string.title_tile_power,
                "com.android.systemui:drawable/ic_qs_power", true));
        TILES.put(TILE_PROFILE, new QuickSettingsUtil.TileInfo(
                TILE_PROFILE, R.string.title_tile_profile,
                "com.android.systemui:drawable/ic_qs_profiles", true));
        TILES.put(TILE_QUIETHOURS, new QuickSettingsUtil.TileInfo(
                TILE_QUIETHOURS, R.string.title_tile_quiet_hours,
                "com.android.systemui:drawable/ic_qs_quiet_hours_off", true));
        TILES.put(TILE_REBOOT, new QuickSettingsUtil.TileInfo(
                TILE_REBOOT, R.string.title_tile_reboot,
                "com.android.systemui:drawable/ic_qs_reboot", true));
        TILES.put(TILE_SCREENTIMEOUT, new QuickSettingsUtil.TileInfo(
                TILE_SCREENTIMEOUT, R.string.title_tile_screen_timeout,
                "com.android.systemui:drawable/ic_qs_screen_timeout_off", true));
        TILES.put(TILE_SETTINGS, new QuickSettingsUtil.TileInfo(
                TILE_SETTINGS, R.string.title_tile_settings,
                "com.android.systemui:drawable/ic_qs_settings", true));
        TILES.put(TILE_SOUND, new QuickSettingsUtil.TileInfo(
                TILE_SOUND, R.string.title_tile_sound,
                "com.android.systemui:drawable/ic_qs_ring_on", true));
        TILES.put(TILE_SYNC, new QuickSettingsUtil.TileInfo(
                TILE_SYNC, R.string.title_tile_sync,
                "com.android.systemui:drawable/ic_qs_sync_off", true));
        TILES.put(TILE_TORCH, new QuickSettingsUtil.TileInfo(
                TILE_TORCH, R.string.title_tile_torch,
                "com.android.systemui:drawable/ic_qs_torch_off", true));
        TILES.put(TILE_USER, new QuickSettingsUtil.TileInfo(
                TILE_USER, R.string.title_tile_user,
                "com.android.systemui:drawable/ic_qs_default_user", true));
        TILES.put(TILE_WIFI, new QuickSettingsUtil.TileInfo(
                TILE_WIFI, R.string.title_tile_wifi,
                "com.android.systemui:drawable/ic_qs_wifi_4", true));
        TILES.put(TILE_VOLUME, new QuickSettingsUtil.TileInfo(
                TILE_VOLUME, R.string.title_tile_volume,
                "com.android.systemui:drawable/ic_qs_volume", true));
        TILES.put(TILE_WIFIAP, new QuickSettingsUtil.TileInfo(
                TILE_WIFIAP, R.string.title_tile_wifiap,
                "com.android.systemui:drawable/ic_qs_wifi_ap_neutral", true));
// These toggles are not available yet.  Comment out for now
//        TILES.put(TILE_WIMAX, new QuickSettingsUtil.TileInfo(
//                TILE_WIMAX, R.string.title_tile_wimax,
//                "com.android.systemui:drawable/stat_wimax_on"));
    }

    public static String getCurrentTiles(Context context) {
        String tiles = Settings.System.getString(context.getContentResolver(),
                Settings.System.QUICK_SETTINGS_TILES);
        if (tiles == null) {
            tiles = TextUtils.join(TILE_DELIMITER, TILES_DEFAULT);
        }
        return tiles;
    }

    public static void saveCurrentTiles(Context context, String tiles) {
        Settings.System.putString(context.getContentResolver(),
                Settings.System.QUICK_SETTINGS_TILES, tiles);
    }

    public static void resetTiles(Context context) {
        String defaultTiles = TextUtils.join(TILE_DELIMITER, TILES_DEFAULT);
        Settings.System.putString(context.getContentResolver(),
                Settings.System.QUICK_SETTINGS_TILES, defaultTiles);
    }

    public static String mergeInNewTileString(String oldString, String newString) {
        ArrayList<String> oldList = getTileListFromString(oldString);
        ArrayList<String> newList = getTileListFromString(newString);
        ArrayList<String> mergedList = new ArrayList<String>();

        // add any items from oldlist that are in new list
        for (String tile : oldList) {
            if (newList.contains(tile)) {
                mergedList.add(tile);
            }
        }

        // append anything in newlist that isn't already in the merged list to
        // the end of the list
        for (String tile : newList) {
            if (!mergedList.contains(tile)) {
                mergedList.add(tile);
            }
        }

        // return merged list
        return getTileStringFromList(mergedList);
    }

    public static ArrayList<String> getTileListFromString(String tiles) {
        return new ArrayList<String>(Arrays.asList(tiles.split("\\|")));
    }

    public static String getTileStringFromList(ArrayList<String> tiles) {
        if (tiles == null || tiles.size() <= 0) {
            return "";
        } else {
            String s = tiles.get(0);
            for (int i = 1; i < tiles.size(); i++) {
                s += TILE_DELIMITER + tiles.get(i);
            }
            if (s.startsWith("|")) s = s.substring(1);
            return s;
        }
    }

    public static class TileInfo {
        private String mId;
        private int mTitleResId;
        private String mIcon;
        private boolean mSingleton;
        private int occurences = 0;

        public TileInfo(String id, int titleResId, String icon, boolean singleton) {
            mId = id;
            mTitleResId = titleResId;
            mIcon = icon;
            mSingleton = singleton;
        }

        public boolean isSingleton() {
            return mSingleton;
        }

        public String getId() {
            return mId;
        }

        public int getTitleResId() {
            return mTitleResId;
        }

        public String getIcon() {
            return mIcon;
        }

        public void setOccurences(int nb){
            this.occurences = nb;
        }

        public int getOccurences(){
            return occurences;
        }
    }
}
