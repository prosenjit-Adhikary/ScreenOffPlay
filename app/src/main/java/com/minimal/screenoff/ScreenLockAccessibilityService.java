package com.minimal.screenoff;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.view.accessibility.AccessibilityEvent;

/**
 * Requests Android to lock the device through the public accessibility global action.
 * The service does not hold a wake lock or prevent normal Android power management.
 */
public class ScreenLockAccessibilityService extends AccessibilityService {
    private static final String PREFS = "screen_lock_state";
    private static final String KEY_ACTIVE = "active";

    private static volatile ScreenLockAccessibilityService instance;
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
        setActive(false);
    }

    @Override
    public boolean onUnbind(Intent intent) {
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
            boolean locked = service.performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN);
            service.setActive(locked);
            return locked;
        } catch (RuntimeException ignored) {
            // The system may reject the action if accessibility state changed.
            service.setActive(false);
            return false;
        }
    }

    private void setActive(boolean active) {
        getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putBoolean(KEY_ACTIVE, active).apply();
    }
}
