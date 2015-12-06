package com.github.wksb.wkebapp;

import android.content.Context;

/**
 * This Class handles the Settings of this Application
 *
 * @author Projekt-Seminar "Schnitzeljagd World-heritage" 2015/2016 des Clavius Gymnasiums Bamberg
 * @version 1.0
 * @since 2015-12-06
 */
public class Settings {

    /** Default boolean, if it is the first Launch of the Application */
    private static final boolean DEFAULT_IS_FIRST_LAUNCH = true;

    private Settings(){}

    /**
     * Check if this Run is the fist Launch of the Application
     * @param context {@link Context} of the Application
     * @return true if this is the first Launch, false otherwise
     */
    public static boolean isFirstAppLaunch(Context context) {
        return context.getSharedPreferences("MISCELLANEOUS", Context.MODE_PRIVATE).getBoolean("IS_FIRST_APP_LAUNCH", DEFAULT_IS_FIRST_LAUNCH);
    }

    /**
     * Set if this and future Runs are the first Launch of the Application
     * @param context {@link Context} of the Application
     * @param isFirstLaunch The new value if this and future Runs are the first Launch of the Application
     */
    public static void setIsFirstLaunch(Context context, boolean isFirstLaunch) {
        context.getSharedPreferences("MISCELLANEOUS", Context.MODE_PRIVATE).edit().putBoolean("IS_FIRST_APP_LAUNCH", isFirstLaunch).commit();
    }
}
