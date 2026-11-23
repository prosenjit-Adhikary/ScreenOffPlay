package com.minimal.screenoff;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

public final class OverlayController {

    private static View overlayView;
    private static long lastTapTime = 0L;

    private OverlayController() {}

    public static synchronized void toggle(Context context) {
        if (overlayView != null) {
            hide(context);
        } else {
            show(context);
        }
    }

    public static synchronized void show(Context context) {
        if (overlayView != null) return;
        Context appCtx = context.getApplicationContext();
        if (!Settings.canDrawOverlays(appCtx)) return;

        WindowManager wm = (WindowManager) appCtx.getSystemService(Context.WINDOW_SERVICE);
        if (wm == null) return;

        FrameLayout root = new FrameLayout(appCtx);
        root.setBackgroundColor(Color.BLACK);
        root.setKeepScreenOn(true);

        TextView hint = new TextView(appCtx);
        hint.setText("Double-tap anywhere to unlock");
        hint.setTextColor(0xFF444444);
        hint.setTextSize(14f);
        FrameLayout.LayoutParams textParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
        );
        root.addView(hint, textParams);
        hint.animate().alpha(0f).setStartDelay(1500).setDuration(500).start();

        root.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                long now = SystemClock.elapsedRealtime();
                if (now - lastTapTime < 400L) {
                    hide(appCtx);
                } else {
                    lastTapTime = now;
                    hint.animate().cancel();
                    hint.setAlpha(1f);
                    hint.animate().alpha(0f).setStartDelay(1000).setDuration(400).start();
                }
            }
            return true;
        });

        int flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                flags,
                PixelFormat.OPAQUE
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            params.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }

        params.screenBrightness = 0.01f;

        wm.addView(root, params);
        overlayView = root;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController insetsController = root.getWindowInsetsController();
            if (insetsController != null) {
                insetsController.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                insetsController.setSystemBarsBehavior(
                        WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                );
            }
        }
    }

    public static synchronized void hide(Context context) {
        if (overlayView == null) return;
        WindowManager wm = (WindowManager) context.getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        if (wm != null) {
            try {
                wm.removeViewImmediate(overlayView);
            } catch (Exception ignored) {}
        }
        overlayView = null;
    }

    public static boolean isShowing() {
        return overlayView != null;
    }
}
