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
        if (overlayView != null || !Settings.canDrawOverlays(context)) return;

        Context appContext = context.getApplicationContext();
        WindowManager windowManager =
                (WindowManager) appContext.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager == null) return;

        FrameLayout root = new FrameLayout(appContext);
        root.setBackgroundColor(Color.BLACK);

        TextView hint = new TextView(appContext);
        hint.setText("Double-tap to restore");
        hint.setTextColor(0xFF555555);
        hint.setTextSize(14f);
        FrameLayout.LayoutParams textParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
        );
        root.addView(hint, textParams);
        hint.animate().alpha(0f).setStartDelay(1800).setDuration(500).start();

        root.setOnTouchListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                long now = SystemClock.elapsedRealtime();
                if (now - lastTapTime <= 400L) {
                    hide(appContext);
                } else {
                    lastTapTime = now;
                    hint.animate().cancel();
                    hint.setAlpha(1f);
                    hint.animate().alpha(0f).setStartDelay(1000).setDuration(400).start();
                }
            }
            return true;
        });

        // Keep the window visible so Android does not apply the normal screen
        // timeout and lock the phone while the user is listening to media.
        // The black overlay and minimum window brightness remain in effect.
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
        params.screenBrightness = 0.0f;

        try {
            windowManager.addView(root, params);
            overlayView = root;
            lastTapTime = 0L;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                WindowInsetsController controller = root.getWindowInsetsController();
                if (controller != null) {
                    controller.hide(WindowInsets.Type.statusBars()
                            | WindowInsets.Type.navigationBars());
                    controller.setSystemBarsBehavior(
                            WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                    );
                }
            }
        } catch (WindowManager.BadTokenException | IllegalStateException | SecurityException ignored) {
            overlayView = null;
        }
    }

    public static synchronized void hide(Context context) {
        View current = overlayView;
        if (current == null) return;

        overlayView = null;
        lastTapTime = 0L;
        WindowManager windowManager =
                (WindowManager) context.getApplicationContext()
                        .getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            try {
                windowManager.removeViewImmediate(current);
            } catch (IllegalArgumentException | IllegalStateException ignored) {
                // The window may already have been removed by the system.
            }
        }
    }

    public static synchronized boolean isShowing() {
        return overlayView != null;
    }
}
