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
        layout.setPadding(48, 48, 48, 48);

        TextView title = new TextView(this);
        title.setText("Screen Off Play");
        title.setTextSize(22f);
        title.setGravity(Gravity.CENTER);
        layout.addView(title);

        TextView info = new TextView(this);
        info.setText(
                "AMOLED Blackout keeps a pure black (#000000) overlay visible at minimum window brightness. " +
                "The overlay keeps the display awake to prevent the normal screen timeout from locking the phone.\n\n" +
                "1. Allow Display over other apps.\n" +
                "2. Add the Screen Off tile to Quick Settings.\n" +
                "3. Start media playback, then tap the tile.\n" +
                "4. The screen stays black and the phone does not auto-lock from screen timeout.\n" +
                "5. Double-tap the black screen to restore it. The power button can still lock the phone manually.\n\n" +
                "This is a black-screen mode, not a physical display power-off. The display remains powered. " +
                "Media playback is still controlled by Android and the media app; this app cannot bypass YouTube or other apps' playback restrictions."
        );
        info.setTextSize(15f);
        info.setGravity(Gravity.CENTER);
        info.setPadding(0, 24, 0, 24);
        layout.addView(info);

        Button btn = new Button(this);
        btn.setText("Allow overlay permission");
        btn.setOnClickListener(v -> {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName())
                );
                startActivity(intent);
            } else {
                Toast.makeText(this,
                        "Add the Screen Off tile to Quick Settings, then use it over your media app.",
                        Toast.LENGTH_LONG).show();
            }
        });
        layout.addView(btn);
        setContentView(layout);
    }
}
