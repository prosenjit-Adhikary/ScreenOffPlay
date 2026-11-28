package com.minimal.screenoff;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
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
        title.setText("SofP");
        title.setTextSize(24f);
        title.setGravity(Gravity.CENTER);
        layout.addView(title);

        TextView intro = new TextView(this);
        intro.setText("Choose the screen behavior you want. No special access is requested during installation. The permission for your selected mode is explained and requested only when you continue. SofP does not use an Accessibility service. Close the black overlay before opening a banking or payment app; SofP does not bypass app security checks.");
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
        blackDescription.setText("Displays a pure-black overlay and keeps the display awake. Double-tap the overlay to restore the screen. Android asks for Display over other apps access only when you start this mode. Close the overlay before opening banking or payment apps.");
        blackDescription.setTextSize(13f);
        blackDescription.setPadding(36, 0, 0, 12);
        modes.addView(blackDescription);

        screenOffOption = new RadioButton(this);
        screenOffOption.setId(View.generateViewId());
        screenOffOption.setText("Screen Off / Lock");
        modes.addView(screenOffOption);

        TextView lockDescription = new TextView(this);
        lockDescription.setText("Asks Android to lock the device and temporarily keeps the CPU awake for up to 2 hours. Available on Android 9 (API 28)+. You must explicitly approve SofP as a Device Admin when you choose to start this mode. SofP uses only Android's force-lock policy and does not enable Accessibility access.");
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
            if (!DeviceAdminLockController.isAdminActive(this)) {
                status.setText("To use Screen Off / Lock, approve SofP as a Device Admin. This is requested only for the lock feature.");
                Intent adminIntent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                adminIntent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                        new ComponentName(this, ScreenLockAdminReceiver.class));
                adminIntent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                        "SofP uses the force-lock capability only when you explicitly tap Screen Off / Lock.");
                startActivity(adminIntent);
                return;
            }
            if (!DeviceAdminLockController.lockDisplay(this)) {
                status.setText("Android did not accept the lock request. Check that SofP is still an active Device Admin, then try again.");
            }
        } else if (!Settings.canDrawOverlays(this)) {
            status.setText("To use Black Screen, allow SofP to display over other apps. This special access is optional and is used only for the selected Black Screen mode.");
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
                    (DeviceAdminLockController.isAdminActive(this)
                            ? "SofP Device Admin is active. Tap Continue to lock the device."
                            : "Device Admin approval is not enabled. Tap Continue to review Android's confirmation."));
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
