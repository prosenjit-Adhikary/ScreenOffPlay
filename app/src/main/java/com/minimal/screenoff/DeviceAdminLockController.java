package com.minimal.screenoff;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.PowerManager;

/** Locks the device only after the user has explicitly activated SofP as a Device Admin. */
public final class DeviceAdminLockController {
    private static final String PREFS = "screen_lock_state";
    private static final String KEY_ACTIVE = "active";
    private static final long WAKE_LOCK_TIMEOUT_MS = 2L * 60L * 60L * 1000L;

    private static PowerManager.WakeLock wakeLock;
    private static BroadcastReceiver screenOnReceiver;
    private static Context appContext;

    private DeviceAdminLockController() {}

    public static ComponentName getAdminComponent(Context context) {
        return new ComponentName(context, ScreenLockAdminReceiver.class);
    }

    public static boolean isAdminActive(Context context) {
        DevicePolicyManager manager =
                (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        return manager != null && manager.isAdminActive(getAdminComponent(context));
    }

    public static synchronized boolean lockDisplay(Context context) {
        Context app = context.getApplicationContext();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P || !isAdminActive(app)) {
            return false;
        }

        DevicePolicyManager manager =
                (DevicePolicyManager) app.getSystemService(Context.DEVICE_POLICY_SERVICE);
        PowerManager power =
                (PowerManager) app.getSystemService(Context.POWER_SERVICE);
        if (manager == null || power == null) return false;

        try {
            if (wakeLock == null) {
                wakeLock = power.newWakeLock(
                        PowerManager.PARTIAL_WAKE_LOCK, app.getPackageName() + ":SofP");
                wakeLock.setReferenceCounted(false);
            }
            wakeLock.acquire(WAKE_LOCK_TIMEOUT_MS);
            registerScreenOnReceiver(app);
            app.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                    .edit().putBoolean(KEY_ACTIVE, true).apply();
            manager.lockNow();
            return true;
        } catch (RuntimeException error) {
            setInactive(app);
            return false;
        }
    }

    public static boolean isActive(Context context) {
        boolean recorded = context.getApplicationContext()
                .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getBoolean(KEY_ACTIVE, false);
        return recorded && isAdminActive(context);
    }

    private static void registerScreenOnReceiver(Context app) {
        if (screenOnReceiver != null) return;
        appContext = app;
        screenOnReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
                    setInactive(context.getApplicationContext());
                }
            }
        };
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            app.registerReceiver(screenOnReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            app.registerReceiver(screenOnReceiver, filter);
        }
    }

    private static synchronized void setInactive(Context app) {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
        app.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putBoolean(KEY_ACTIVE, false).apply();
    }
}
