package com.minimal.screenoff;

import android.content.Context;
import android.os.Build;

public final class DisplayModeManager {
    public static final String BLACK_SCREEN = "BLACK_SCREEN";
    public static final String SCREEN_OFF = "SCREEN_OFF";
    private static final String PREFS = "display_mode_preferences";
    private static final String KEY_MODE = "selected_mode";

    private DisplayModeManager() {}

    public static String getMode(Context context) {
        String mode = context.getApplicationContext()
                .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getString(KEY_MODE, BLACK_SCREEN);
        if (SCREEN_OFF.equals(mode) && Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return BLACK_SCREEN;
        }
        return mode;
    }

    public static void setMode(Context context, String mode) {
        String safeMode = SCREEN_OFF.equals(mode) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
                ? SCREEN_OFF : BLACK_SCREEN;
        context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putString(KEY_MODE, safeMode).apply();
    }

    public static boolean isScreenOffSupported() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P;
    }
}
