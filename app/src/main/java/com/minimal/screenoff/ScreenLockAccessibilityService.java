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
 * Requests Android to lock the device through the public accessibility global action.
 * It subscribes to no accessibility events and cannot retrieve window content.
 * A bounded partial wake lock keeps the CPU awake temporarily after an explicit
 * user-requested lock; Android and the media app still control playback policy.
 */
public class ScreenLockAccessibilityService extends AccessibilityService {
    private static final String PREFS = "screen_lock_state";
    private static final String KEY_ACTIVE = "active";
    private static final long WAKE_LOCK_TIMEOUT_MS = 2L * 60L * 60L * 1000L;

    private static volatile ScreenLockAccessibilityService instance;
    private PowerManager.WakeLock wakeLock;
    private boolean receiverRegistered;

    private final BroadcastReceiver screenStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
                setActive(false);
            }
        }
    };

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        instance = this;
        setActive(false);

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
        // No accessibility events are requested by the service configuration.
    }

    @Override
    public void onInterrupt() {
        setActive(false);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        setActive(false);
        if (instance == this) instance = null;
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
        return recordedActive && service != null;
    }

    public static boolean lockDisplay(Context context) {
        ScreenLockAccessibilityService service = instance;
        if (service == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return false;
        }

        try {
            service.acquireCpuWakeLock();
            boolean locked = service.performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN);
            service.setActive(locked);
            return locked;
        } catch (RuntimeException ignored) {
            // The system may reject the action if accessibility state changed.
            service.setActive(false);
            return false;
        }
    }

    private synchronized void acquireCpuWakeLock() {
        PowerManager powerManager =
                (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (powerManager == null) {
            throw new IllegalStateException("PowerManager is unavailable");
        }
        if (wakeLock == null) {
            wakeLock = powerManager.newWakeLock(
                    PowerManager.PARTIAL_WAKE_LOCK,
                    getPackageName() + ":ScreenOffPlay"
            );
            wakeLock.setReferenceCounted(false);
        }
        if (!wakeLock.isHeld()) {
            // Safety timeout prevents an indefinite CPU hold if the screen-on
            // broadcast or service teardown is missed by the operating system.
            wakeLock.acquire(WAKE_LOCK_TIMEOUT_MS);
        }
    }

    private synchronized void releaseCpuWakeLock() {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

    private void setActive(boolean active) {
        if (!active) {
            releaseCpuWakeLock();
        }
        getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putBoolean(KEY_ACTIVE, active).apply();
    }
}
