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

/** Uses Accessibility only for the global lock action; never reads app/window content. */
public class ScreenLockAccessibilityService extends AccessibilityService {
    private static final long CPU_WAKE_TIMEOUT_MS = 2L * 60L * 60L * 1000L;
    private static volatile ScreenLockAccessibilityService activeService;
    private PowerManager.WakeLock cpuWakeLock;
    private BroadcastReceiver screenOnReceiver;

    public static boolean isEnabled(Context context) {
        String enabled = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (TextUtils.isEmpty(enabled)) return false;
        ComponentName expected = new ComponentName(context, ScreenLockAccessibilityService.class);
        TextUtils.SimpleStringSplitter splitter = new TextUtils.SimpleStringSplitter(':');
        splitter.setString(enabled);
        while (splitter.hasNext()) {
            String component = splitter.next();
            if (expected.flattenToString().equalsIgnoreCase(component)
                    || expected.flattenToShortString().equalsIgnoreCase(component)) return true;
        }
        return false;
    }

    public static boolean lockScreenAndKeepCpuActive() {
        ScreenLockAccessibilityService service = activeService;
        return service != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
                && service.performLockAction();
    }

    @Override protected void onServiceConnected() {
        super.onServiceConnected();
        activeService = this;
    }

    @Override public void onAccessibilityEvent(AccessibilityEvent event) {
        // Intentionally ignore events; no text, node, or window content is accessed.
    }

    @Override public void onInterrupt() { }

    private synchronized boolean performLockAction() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (pm == null) return false;
        try {
            if (cpuWakeLock == null) {
                cpuWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                        getPackageName() + ":SofP");
                cpuWakeLock.setReferenceCounted(false);
            }
            cpuWakeLock.acquire(CPU_WAKE_TIMEOUT_MS);
            registerScreenOnReceiver();
            boolean locked = performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN);
            if (!locked) releaseCpuWakeLock();
            return locked;
        } catch (RuntimeException error) {
            releaseCpuWakeLock();
            return false;
        }
    }

    private void registerScreenOnReceiver() {
        if (screenOnReceiver != null) return;
        screenOnReceiver = new BroadcastReceiver() {
            @Override public void onReceive(Context context, Intent intent) {
                if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) releaseCpuWakeLock();
            }
        };
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(screenOnReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(screenOnReceiver, filter);
        }
    }

    private synchronized void releaseCpuWakeLock() {
        if (cpuWakeLock != null && cpuWakeLock.isHeld()) cpuWakeLock.release();
    }

    @Override public boolean onUnbind(Intent intent) {
        if (activeService == this) activeService = null;
        releaseCpuWakeLock();
        unregisterScreenOnReceiver();
        return super.onUnbind(intent);
    }

    @Override public void onDestroy() {
        if (activeService == this) activeService = null;
        releaseCpuWakeLock();
        unregisterScreenOnReceiver();
        super.onDestroy();
    }

    private void unregisterScreenOnReceiver() {
        if (screenOnReceiver == null) return;
        try { unregisterReceiver(screenOnReceiver); }
        catch (IllegalArgumentException ignored) { }
        screenOnReceiver = null;
    }
}
