package com.dh.preference;

import android.content.Context;
import android.content.SharedPreferences;

import com.dh.home.AppType;

public class AppTypePreference {

    private static final String TAG = "AppTypePreference";
    private static final String CALENDAR_PKG = "calendar_pkg";

    private SharedPreferences mPrefs;

    private static AppTypePreference instance;

    private static final String HAS_CONFIG_SYSTEM_APP_ICON = "has_config_system_app_icon";
    private Boolean mHasConfigSystemAppIcon = null;

    private AppTypePreference(Context context) {
        this.mPrefs = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
    }

    public void saveHasConfigSystemAppIcon(boolean hasConfigSystemAppIcon) {
        mHasConfigSystemAppIcon = hasConfigSystemAppIcon;
        mPrefs.edit().putBoolean(HAS_CONFIG_SYSTEM_APP_ICON, hasConfigSystemAppIcon).commit();
    }

    public boolean getHasConfigSystemAppIcon() {
        if (mHasConfigSystemAppIcon == null) {
            mHasConfigSystemAppIcon = mPrefs.getBoolean(HAS_CONFIG_SYSTEM_APP_ICON, false);
        }
        return mHasConfigSystemAppIcon;
    }

    public synchronized static AppTypePreference getInstance(Context context) {
        if (instance == null) {
            instance = new AppTypePreference(context);
        }
        return instance;
    }

    public SharedPreferences.Editor getEditor() {
        return mPrefs.edit();
    }

    public void saveAppType(String className, String apptype) {
        mPrefs.edit().putString(className, apptype).putString(apptype, className).apply();
    }

    public String getAppType(String className) {
        return mPrefs.getString(className, "");
    }

    public String getClassNameFromAppType(AppType apptype) {
        return mPrefs.getString(apptype.getValue(), "");
    }

    public void saveCalendarPkg(String pkg) {
        mPrefs.edit().putString(CALENDAR_PKG, pkg).commit();
    }

    public String getCalendarPkg() {
        return mPrefs.getString(CALENDAR_PKG, "");
    }


}
