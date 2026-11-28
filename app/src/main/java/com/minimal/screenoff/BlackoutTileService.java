package com.minimal.screenoff;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

public class BlackoutTileService extends TileService {
    @Override public void onStartListening() { updateTileState(); }

    @Override @SuppressLint("StartActivityAndCollapseDeprecated")
    public void onClick() {
        if (!ScreenLockAccessibilityService.isEnabled(this)
                || !ScreenLockAccessibilityService.lockScreenAndKeepCpuActive()) openApp();
        updateTileState();
    }

    @SuppressLint("StartActivityAndCollapseDeprecated")
    private void openApp() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            PendingIntent pi = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            startActivityAndCollapse(pi);
        } else startActivityAndCollapse(intent);
    }

    private void updateTileState() {
        Tile tile = getQsTile();
        if (tile == null) return;
        tile.setState(Tile.STATE_INACTIVE);
        tile.updateTile();
    }
}
