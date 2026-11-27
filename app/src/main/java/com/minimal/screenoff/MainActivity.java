package com.minimal.screenoff;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MainActivity extends Activity {
    private RadioButton blackScreenOption;
    private RadioButton screenOffOption;
    private TextView status;
    private Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildScreen();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshStatus();
    }

    private void buildScreen() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.setPadding(40, 36, 40, 36);

        TextView title = new TextView(this);
        title.setText("Screen Off Play");
        title.setTextSize(24f);
        title.setGravity(Gravity.CENTER);
        layout.addView(title);

        TextView intro = new TextView(this);
        intro.setText("Choose the screen behavior you want. No special access is requested during installation. The permission for your selected mode is explained and requested only when you continue.");
        intro.setTextSize(15f);
        intro.setPadding(0, 20, 0, 16);
        layout.addView(intro);

        RadioGroup modes = new RadioGroup(this);
        modes.setOrientation(RadioGroup.VERTICAL);

        blackScreenOption = new RadioButton(this);
        blackScreenOption.setId(View.generateViewId());
        blackScreenOption.setText("Black Screen (AMOLED)");
        modes.addView(blackScreenOption);

        TextView blackDescription = new TextView(this);
        blackDescription.setText("Displays a pure-black overlay and keeps the display awake. Double-tap the overlay to restore the screen. Android asks for Display over other apps access only when you start this mode.");
        blackDescription.setTextSize(13f);
        blackDescription.setPadding(36, 0, 0, 12);
        modes.addView(blackDescription);

        screenOffOption = new RadioButton(this);
        screenOffOption.setId(View.generateViewId());
        screenOffOption.setText("Screen Off / Lock");
        modes.addView(screenOffOption);

        TextView lockDescription = new TextView(this);
        lockDescription.setText("Asks Android to lock the device. Available on Android 9 (API 28)+. You must explicitly enable the app's Accessibility service in Android Settings when you choose to start this mode. The service does not read screen content.");
        lockDescription.setTextSize(13f);
        lockDescription.setPadding(36, 0, 0, 12);
        modes.addView(lockDescription);
        layout.addView(modes);

        modes.setOnCheckedChangeListener((group, checkedId) -> {
            if (blackScreenOption != null && checkedId == blackScreenOption.getId()) {
                DisplayModeManager.setMode(this, DisplayModeManager.BLACK_SCREEN);
            } else if (screenOffOption != null && checkedId == screenOffOption.getId()
                    && DisplayModeManager.isScreenOffSupported()) {
                DisplayModeManager.setMode(this, DisplayModeManager.SCREEN_OFF);
            }
            refreshStatus();
        });

        if (!DisplayModeManager.isScreenOffSupported()) {
            screenOffOption.setEnabled(false);
            lockDescription.setText("Unavailable on this Android version. Android 8 (API 26–27) supports Black Screen only.");
        }

        startButton = new Button(this);
        startButton.setText("Continue with selected mode");
        startButton.setOnClickListener(v -> startSelectedMode());
        layout.addView(startButton);

        status = new TextView(this);
        status.setTextSize(13f);
        status.setPadding(0, 20, 0, 0);
        layout.addView(status);

        setContentView(layout);
        String selected = DisplayModeManager.getMode(this);
        modes.check(DisplayModeManager.SCREEN_OFF.equals(selected)
                ? screenOffOption.getId() : blackScreenOption.getId());
    }

    private void startSelectedMode() {
        String selected = DisplayModeManager.getMode(this);
        if (DisplayModeManager.SCREEN_OFF.equals(selected)) {
            if (!ScreenLockAccessibilityService.isConnected()) {
                status.setText("To use Screen Off / Lock, enable Screen Off Play in Android Accessibility settings. No accessibility access is enabled automatically.");
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                return;
            }
            if (!ScreenLockAccessibilityService.lockDisplay(this)) {
                status.setText("Android did not accept the lock request. Check that the Accessibility service is still enabled, then try again.");
            }
        } else if (!Settings.canDrawOverlays(this)) {
            status.setText("To use Black Screen, allow Screen Off Play to display over other apps. This special access is optional and is used only for the selected Black Screen mode.");
            openOverlaySettings();
            return;
        } else {
            OverlayController.show(this);
        }
        refreshStatus();
    }

    private void openOverlaySettings() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    private void refreshStatus() {
        if (status == null) return;
        String selected = DisplayModeManager.getMode(this);
        if (DisplayModeManager.SCREEN_OFF.equals(selected)) {
            status.setText("Selected: Screen Off / Lock\n" +
                    (ScreenLockAccessibilityService.isConnected()
                            ? "Accessibility service is enabled. Tap Continue to lock the device."
                            : "Accessibility access is not enabled. Tap Continue to open Android Settings."));
        } else {
            status.setText("Selected: Black Screen (AMOLED)\n" +
                    (Settings.canDrawOverlays(this)
                            ? "Display over other apps access is enabled. Tap Continue to show the black screen."
                            : "Display over other apps access is not enabled. Tap Continue to open Android Settings."));
        }
        if (blackScreenOption != null) {
            blackScreenOption.setChecked(DisplayModeManager.BLACK_SCREEN.equals(selected));
        }
        if (screenOffOption != null) {
            screenOffOption.setChecked(DisplayModeManager.SCREEN_OFF.equals(selected));
        }
    }
}
