package com.minimal.screenoff;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(40, 40, 40, 40);

        TextView title = new TextView(this);
        title.setText("Screen Off Play");
        title.setTextSize(22f);
        title.setGravity(Gravity.CENTER);
        layout.addView(title);

        TextView info = new TextView(this);
        info.setText(
                "Screen Off mode uses Android Accessibility to request a device lock. " +
                "It does not acquire a CPU wake lock; Android remains responsible for normal power management.\n\n" +
                "Setup:\n" +
                "1. Enable Screen Off Play under Android Accessibility settings.\n" +
                "2. Add the Screen Off tile to Quick Settings.\n" +
                "3. Start media playback, open Quick Settings and tap Screen Off.\n" +
                "4. Android receives the lock request. Use your normal method to wake and unlock the phone.\n\n" +
                "Screen Off Play does not force the CPU to stay awake. Background playback depends on Android, device power settings, and the media app; this app does not bypass playback restrictions.\n\n" +
                "The older AMOLED black overlay remains available as a separate legacy mode."
        );
        info.setTextSize(15f);
        info.setGravity(Gravity.CENTER);
        info.setPadding(0, 24, 0, 24);
        layout.addView(info);

        Button accessibilityButton = new Button(this);
        accessibilityButton.setText("Enable Screen Off Play Accessibility");
        accessibilityButton.setOnClickListener(v ->
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
        layout.addView(accessibilityButton);

        Button overlayButton = new Button(this);
        overlayButton.setText("Start legacy AMOLED blackout");
        overlayButton.setOnClickListener(v -> {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName())
                );
                startActivity(intent);
            } else {
                OverlayController.show(this);
            }
        });
        layout.addView(overlayButton);

        setContentView(layout);
    }
}
