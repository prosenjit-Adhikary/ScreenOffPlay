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
                "Blackout mode keeps the display awake at very low brightness with a black overlay, " +
                "so the current app can remain in the foreground.\n\n" +
                "1. Allow Display over other apps.\n" +
                "2. Add the Screen Off tile to Quick Settings.\n" +
                "3. Start a video, then tap the tile.\n" +
                "4. Double-tap the black screen to restore it.\n\n" +
                "This does not physically power off the display or guarantee YouTube background playback."
        );
        info.setTextSize(15f);
        info.setGravity(Gravity.CENTER);
        info.setPadding(0, 24, 0, 24);
        layout.addView(info);

        Button btn = new Button(this);
        btn.setText("Allow overlay / Start blackout");
        btn.setOnClickListener(v -> {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName())
                );
                startActivity(intent);
            } else {
                OverlayController.show(this);
                Toast.makeText(this, "Double-tap to restore the screen", Toast.LENGTH_SHORT).show();
            }
        });
        layout.addView(btn);
        setContentView(layout);
    }
}
