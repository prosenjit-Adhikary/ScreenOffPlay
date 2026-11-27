package com.minimal.screenoff;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

public class BlackoutTileService extends TileService {
    @Override
    public void onStartListening() {
        updateTileState();
    }

    @Override
    @SuppressLint("StartActivityAndCollapseDeprecated")
    public void onClick() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            // GLOBAL_ACTION_LOCK_SCREEN is unavailable before Android 9.
            if (Settings.canDrawOverlays(this)) {
                OverlayController.toggle(this);
                updateTileState();
            } else {
                openApp();
            }
            return;
        }

        if (!ScreenLockAccessibilityService.isConnected()) {
            openApp();
            return;
        }

        boolean started = ScreenLockAccessibilityService.lockDisplayAndKeepSystemRunning(this);
        if (!started) {
            openApp();
        }
        updateTileState();
    }

    @SuppressLint("StartActivityAndCollapseDeprecated")
    private void openApp() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    this, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            startActivityAndCollapse(pendingIntent);
        } else {
            startActivityAndCollapse(intent);
        }
    }

    private void updateTileState() {
        Tile tile = getQsTile();
        if (tile != null) {
            boolean active = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
                    ? ScreenLockAccessibilityService.isActive(this)
                    : OverlayController.isShowing();
            tile.setState(active ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
            tile.updateTile();
        }
    }
}
