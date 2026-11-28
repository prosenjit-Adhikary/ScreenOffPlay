package com.minimal.screenoff;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    private TextView status;
    private Button actionButton;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.setPadding(40, 36, 40, 36);

        TextView title = new TextView(this);
        title.setText("SofP");
        title.setTextSize(25f);
        title.setGravity(Gravity.CENTER);
        layout.addView(title);

        TextView intro = new TextView(this);
        intro.setText("First use: tap the button below if you choose to enable SofP. Android Settings will open and you must switch it on yourself. Installation does not ask for or enable special access.");
        intro.setTextSize(15f);
        intro.setPadding(0, 22, 0, 18);
        layout.addView(intro);

        TextView privacy = new TextView(this);
        privacy.setText("SofP requests zero Accessibility events and cannot retrieve screen content. Accessibility is used only for Android's Lock Screen action. SofP does not inspect, click, block, or interfere with payment and banking apps.");
        privacy.setTextSize(13f);
        privacy.setPadding(0, 0, 0, 20);
        layout.addView(privacy);

        actionButton = new Button(this);
        actionButton.setOnClickListener(v -> onMainAction());
        layout.addView(actionButton);
        status = new TextView(this);
        status.setTextSize(13f);
        status.setPadding(0, 20, 0, 0);
        layout.addView(status);
        setContentView(layout);
        refreshStatus();
    }

    @Override protected void onResume() {
        super.onResume();
        refreshStatus();
    }

    private void onMainAction() {
        if (!ScreenLockAccessibilityService.isEnabled(this)) {
            status.setText("Android Accessibility Settings opened. Find SofP and enable it only if you agree.");
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            return;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            status.setText("Screen lock requires Android 9 (API 28) or newer.");
            return;
        }
        boolean success = ScreenLockAccessibilityService.lockScreenAndKeepCpuActive();
        status.setText(success
                ? "Screen lock requested. CPU wake lock remains active until screen-on or the 2-hour safety timeout."
                : "Accessibility service is enabled but not connected. Reopen Settings and try again.");
    }

    private void refreshStatus() {
        if (status == null || actionButton == null) return;
        boolean enabled = ScreenLockAccessibilityService.isEnabled(this);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            actionButton.setEnabled(false);
            actionButton.setText("Android 9+ required");
            status.setText("This screen-lock action requires Android 9 (API 28)+.");
        } else if (enabled) {
            actionButton.setEnabled(true);
            actionButton.setText("Screen Off + Keep CPU Active");
            status.setText("Accessibility is enabled by your choice. Tap to lock the screen.");
        } else {
            actionButton.setEnabled(true);
            actionButton.setText("Enable SofP Accessibility");
            status.setText("Not enabled. Nothing was enabled or prompted during installation.");
        }
    }
}
