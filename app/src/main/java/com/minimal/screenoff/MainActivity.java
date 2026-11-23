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
                "AMOLED Blackout uses a pure black (#000000) overlay and minimum window brightness. " +
                "It does not hold the display awake, so Android's normal screen timeout can turn the panel off.\n\n" +
                "1. Allow Display over other apps.\n" +
                "2. Add the Screen Off tile to Quick Settings.\n" +
                "3. Start your video/audio, then tap the tile.\n" +
                "4. Leave the phone untouched; normal screen timeout will switch the display off.\n" +
                "5. Wake the phone and double-tap the black screen to restore it.\n\n" +
                "Battery savings depend on your display and timeout settings. Audio playback depends on the media app; this app cannot guarantee background playback."
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
