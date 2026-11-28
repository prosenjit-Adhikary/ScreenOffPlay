package com.minimal.screenoff;

import java.util.Locale;

/** Dependency-free regression tests; run with the repository CI javac/java test step. */
public final class AccessibilitySettingsMatcherTest {
    private static final String FULL =
            "com.minimal.screenoff/com.minimal.screenoff.ScreenLockAccessibilityService";
    private static final String SHORT = "com.minimal.screenoff/.ScreenLockAccessibilityService";
    private static int assertions;

    private AccessibilitySettingsMatcherTest() { }

    public static void main(String[] args) {
        check(true, "1", "com.other/.Service:" + FULL, FULL, SHORT,
                "enabled service with global Accessibility on");
        check(false, "0", FULL, FULL, SHORT, "global Accessibility switch off");
        check(false, "1", "com.other/.Service", FULL, SHORT, "SofP service absent");
        check(true, "1", SHORT, FULL, SHORT, "Android short component notation");
        check(true, "1", FULL.toUpperCase(Locale.ROOT), FULL, SHORT,
                "case-insensitive component comparison");
        check(false, "1", null, FULL, SHORT, "null enabled-services setting");
        check(false, "1", "  ", FULL, SHORT, "empty enabled-services setting");
        check(false, "1", FULL, null, SHORT, "missing expected full component");
        System.out.println("AccessibilitySettingsMatcherTest: " + assertions + " assertions passed.");
    }

    private static void check(boolean expected, String accessibilityEnabled, String enabledServices,
            String expectedFull, String expectedShort, String scenario) {
        boolean actual = AccessibilitySettingsMatcher.isServiceEnabled(accessibilityEnabled,
                enabledServices, expectedFull, expectedShort);
        assertions++;
        if (actual != expected) {
            throw new AssertionError(scenario + ": expected " + expected + " but got " + actual);
        }
    }
}
