package com.minimal.screenoff;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.PowerManager;
import android.view.accessibility.AccessibilityEvent;

/**
 * Locks the display through Android's public accessibility global action while
 * holding a bounded partial wake lock so the CPU can continue running.
 */
public class ScreenLockAccessibilityService extends AccessibilityService {
    private static final String PREFS = "screen_lock_state";
    private static final String KEY_ACTIVE = "active";
    private static final long MAX_WAKE_LOCK_MS = 30L * 60L * 1000L;

    private static volatile ScreenLockAccessibilityService instance;
    private PowerManager.WakeLock wakeLock;
    private boolean receiverRegistered;

    private final BroadcastReceiver screenStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
                releaseSystemWakeLock();
                setActive(false);
                instance = ScreenLockAccessibilityService.this;
            }
        }
    };

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        instance = this;

        if (!receiverRegistered) {
            IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(screenStateReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
            } else {
                registerReceiver(screenStateReceiver, filter);
            }
            receiverRegistered = true;
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // No window content is inspected or collected.
    }

    @Override
    public void onInterrupt() {
        releaseSystemWakeLock();
        setActive(false);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        releaseSystemWakeLock();
        setActive(false);
        instance = null;
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        if (receiverRegistered) {
            try {
                unregisterReceiver(screenStateReceiver);
            } catch (IllegalArgumentException ignored) {
                // Receiver was already unregistered by the system.
            }
            receiverRegistered = false;
        }
        releaseSystemWakeLock();
        setActive(false);
        if (instance == this) instance = null;
        super.onDestroy();
    }

    public static boolean isConnected() {
        return instance != null;
    }

    public static boolean isActive(Context context) {
        ScreenLockAccessibilityService service = instance;
        boolean recordedActive = context.getApplicationContext()
                .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getBoolean(KEY_ACTIVE, false);
        return recordedActive && service != null
                && service.wakeLock != null && service.wakeLock.isHeld();
    }

    public static boolean lockDisplayAndKeepSystemRunning(Context context) {
        ScreenLockAccessibilityService service = instance;
        if (service == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return false;
        }

        PowerManager powerManager =
                (PowerManager) service.getSystemService(Context.POWER_SERVICE);
        if (powerManager == null) return false;

        try {
            if (service.wakeLock == null) {
                service.wakeLock = powerManager.newWakeLock(
                        PowerManager.PARTIAL_WAKE_LOCK,
                        service.getPackageName() + ":ScreenOffSystemRunning"
                );
                service.wakeLock.setReferenceCounted(false);
            }
            // Safety cap: prevent an abandoned mode from holding the CPU awake forever.
            service.wakeLock.acquire(MAX_WAKE_LOCK_MS);

            boolean locked = service.performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN);
            if (locked) {
                service.setActive(true);
                return true;
            }
        } catch (RuntimeException ignored) {
            // Permission may have been revoked or the system may reject the action.
        }

        service.releaseSystemWakeLock();
        service.setActive(false);
        return false;
    }

    private void releaseSystemWakeLock() {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

    private void setActive(boolean active) {
        getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putBoolean(KEY_ACTIVE, active).apply();
    }
}
