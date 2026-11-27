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
        String mode = DisplayModeManager.getMode(this);
        if (DisplayModeManager.SCREEN_OFF.equals(mode)) {
            if (!DisplayModeManager.isScreenOffSupported()
                    || !ScreenLockAccessibilityService.isConnected()) {
                openApp();
                return;
            }
            if (!ScreenLockAccessibilityService.lockDisplay(this)) openApp();
        } else {
            if (!Settings.canDrawOverlays(this)) {
                openApp();
                return;
            }
            OverlayController.toggle(this);
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
        if (tile == null) return;
        boolean active;
        if (DisplayModeManager.SCREEN_OFF.equals(DisplayModeManager.getMode(this))) {
            active = ScreenLockAccessibilityService.isActive(this);
        } else {
            active = OverlayController.isShowing();
        }
        tile.setState(active ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        tile.updateTile();
    }
}
