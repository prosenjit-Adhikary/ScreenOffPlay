package com.minimal.screenoff;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ScreenLockAccessibilityServiceTest {
    private static final String FULL = "com.minimal.screenoff/com.minimal.screenoff.ScreenLockAccessibilityService";
    private static final String SHORT = "com.minimal.screenoff/.ScreenLockAccessibilityService";

    @Test
    public void enabledWhenGlobalAccessibilityAndExactServiceAreEnabled() {
        assertTrue(ScreenLockAccessibilityService.isServiceEnabled(
                "1", "com.other/.Service:" + FULL, FULL, SHORT));
    }

    @Test
    public void disabledWhenGlobalAccessibilitySwitchIsOff() {
        assertFalse(ScreenLockAccessibilityService.isServiceEnabled(
                "0", FULL, FULL, SHORT));
    }

    @Test
    public void disabledWhenSofpServiceIsNotInEnabledList() {
        assertFalse(ScreenLockAccessibilityService.isServiceEnabled(
                "1", "com.other/.Service", FULL, SHORT));
    }

    @Test
    public void acceptsAndroidShortComponentNotation() {
        assertTrue(ScreenLockAccessibilityService.isServiceEnabled(
                "1", SHORT, FULL, SHORT));
    }

    @Test
    public void componentComparisonIsCaseInsensitive() {
        assertTrue(ScreenLockAccessibilityService.isServiceEnabled(
                "1", FULL.toUpperCase(), FULL, SHORT));
    }

    @Test
    public void rejectsNullOrEmptyServiceList() {
        assertFalse(ScreenLockAccessibilityService.isServiceEnabled("1", null, FULL, SHORT));
        assertFalse(ScreenLockAccessibilityService.isServiceEnabled("1", "  ", FULL, SHORT));
    }
}
