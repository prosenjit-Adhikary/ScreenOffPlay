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
        intro.setText("Choose how Screen Off Play should work. Your choice is saved and used by the Quick Settings tile. You can change it here any time.");
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
        blackDescription.setText("Shows a pure-black overlay. The display stays awake; double-tap the overlay to restore it. Requires Display over other apps permission.");
        blackDescription.setTextSize(13f);
        blackDescription.setPadding(36, 0, 0, 12);
        modes.addView(blackDescription);

        screenOffOption = new RadioButton(this);
        screenOffOption.setId(View.generateViewId());
        screenOffOption.setText("Screen Off / Lock");
        modes.addView(screenOffOption);

        TextView lockDescription = new TextView(this);
        lockDescription.setText("Asks Android to lock the device. Available on Android 9 (API 28) and later. Requires enabling Screen Off Play in Accessibility settings. Android controls CPU sleep and media playback.");
        lockDescription.setTextSize(13f);
        lockDescription.setPadding(36, 0, 0, 12);
        modes.addView(lockDescription);
        layout.addView(modes);

        modes.setOnCheckedChangeListener((group, checkedId) -> {
            if (blackScreenOption != null && checkedId == blackScreenOption.getId()) DisplayModeManager.setMode(this, DisplayModeManager.BLACK_SCREEN);
            if (screenOffOption != null && checkedId == screenOffOption.getId() && DisplayModeManager.isScreenOffSupported()) {
                DisplayModeManager.setMode(this, DisplayModeManager.SCREEN_OFF);
            }
            refreshStatus();
        });

        if (!DisplayModeManager.isScreenOffSupported()) {
            screenOffOption.setEnabled(false);
            lockDescription.setText("Unavailable on this Android version. Android 8 (API 26–27) supports Black Screen mode only.");
        }

        Button applyButton = new Button(this);
        applyButton.setText("Start selected mode");
        applyButton.setOnClickListener(v -> startSelectedMode());
        layout.addView(applyButton);

        Button accessibilityButton = new Button(this);
        accessibilityButton.setText("Accessibility settings");
        accessibilityButton.setOnClickListener(v ->
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
        layout.addView(accessibilityButton);

        Button overlayButton = new Button(this);
        overlayButton.setText("Display over other apps permission");
        overlayButton.setOnClickListener(v -> openOverlaySettings());
        layout.addView(overlayButton);

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
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                return;
            }
            ScreenLockAccessibilityService.lockDisplay(this);
        } else if (!Settings.canDrawOverlays(this)) {
            openOverlaySettings();
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
        status.setText("Selected mode: " +
                (DisplayModeManager.SCREEN_OFF.equals(selected) ? "Screen Off / Lock" : "Black Screen (AMOLED)") +
                "\nThis selection is saved on this device and also controls the Quick Settings tile.");
        if (blackScreenOption != null) blackScreenOption.setChecked(DisplayModeManager.BLACK_SCREEN.equals(selected));
        if (screenOffOption != null) screenOffOption.setChecked(DisplayModeManager.SCREEN_OFF.equals(selected));
    }
}
