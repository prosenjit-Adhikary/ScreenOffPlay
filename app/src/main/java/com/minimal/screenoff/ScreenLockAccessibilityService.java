package com.minimal.screenoff;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;

/**
 * Uses Accessibility only for Android's global lock action.
 * This service subscribes to no accessibility events and never reads app/window content.
 */
public class ScreenLockAccessibilityService extends AccessibilityService {
    private static final long CPU_WAKE_TIMEOUT_MS = 2L * 60L * 60L * 1000L;
    private static volatile ScreenLockAccessibilityService activeService;

    private PowerManager.WakeLock cpuWakeLock;
    private BroadcastReceiver screenOnReceiver;

    public static boolean isEnabled(Context context) {
        String accessibilityEnabled = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ACCESSIBILITY_ENABLED);
        String enabledServices = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        ComponentName expected = new ComponentName(context, ScreenLockAccessibilityService.class);
        return isServiceEnabled(accessibilityEnabled, enabledServices,
                expected.flattenToString(), expected.flattenToShortString());
    }

    static boolean isServiceEnabled(String accessibilityEnabled, String enabledServices,
            String expectedFullComponent, String expectedShortComponent) {
        if (!"1".equals(accessibilityEnabled) || enabledServices == null
                || enabledServices.trim().isEmpty()) {
            return false;
        }

        for (String component : enabledServices.split(":")) {
            if (expectedFullComponent.equalsIgnoreCase(component)
                    || expectedShortComponent.equalsIgnoreCase(component)) {
                return true;
            }
        }
        return false;
    }

    public static boolean lockScreenAndKeepCpuActive() {
        ScreenLockAccessibilityService service = activeService;
        return service != null
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
                && service.performLockAction();
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        activeService = this;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // Intentionally empty: eventTypes is omitted from XML (default 0).
    }

    @Override
    public void onInterrupt() {
        releaseCpuWakeLock();
    }

    private synchronized boolean performLockAction() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return false;

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (powerManager == null) return false;

        try {
            if (cpuWakeLock == null) {
                cpuWakeLock = powerManager.newWakeLock(
                        PowerManager.PARTIAL_WAKE_LOCK, getPackageName() + ":SofP");
                cpuWakeLock.setReferenceCounted(false);
            }

            cpuWakeLock.acquire(CPU_WAKE_TIMEOUT_MS);
            registerScreenOnReceiver();

            boolean lockRequested = performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN);
            if (!lockRequested) releaseCpuWakeLock();
            return lockRequested;
        } catch (RuntimeException error) {
            releaseCpuWakeLock();
            return false;
        }
    }

    private void registerScreenOnReceiver() {
        if (screenOnReceiver != null) return;

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
                    releaseCpuWakeLock();
                }
            }
        };

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(receiver, filter);
        }
        // Publish only after registration succeeds so a failed registration can be retried.
        screenOnReceiver = receiver;
    }

    private synchronized void releaseCpuWakeLock() {
        if (cpuWakeLock != null && cpuWakeLock.isHeld()) {
            cpuWakeLock.release();
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (activeService == this) activeService = null;
        releaseCpuWakeLock();
        unregisterScreenOnReceiver();
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        if (activeService == this) activeService = null;
        releaseCpuWakeLock();
        unregisterScreenOnReceiver();
        super.onDestroy();
    }

    private void unregisterScreenOnReceiver() {
        BroadcastReceiver receiver = screenOnReceiver;
        if (receiver == null) return;
        screenOnReceiver = null;
        try {
            unregisterReceiver(receiver);
        } catch (IllegalArgumentException ignored) {
            // The receiver may already have been unregistered by the system.
        }
    }
}
