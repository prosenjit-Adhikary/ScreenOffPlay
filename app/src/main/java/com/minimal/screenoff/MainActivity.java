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
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(64, 64, 64, 64);

        TextView info = new TextView(this);
        info.setText("1. Grant 'Display over other apps' permission.\n"
                + "2. Edit your Control Center toggles and add 'Screen Off'.\n"
                + "3. Play any YouTube video and tap 'Screen Off' in Control Center.");
        info.setTextSize(15f);
        layout.addView(info);

        Button btn = new Button(this);
        btn.setText("Grant Overlay Permission / Test Now");
        btn.setOnClickListener(v -> {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName())
                );
                startActivity(intent);
            } else {
                Toast.makeText(this, "Double-tap screen to exit", Toast.LENGTH_SHORT).show();
                OverlayController.show(this);
            }
        });
        layout.addView(btn);

        setContentView(layout);
    }
}
