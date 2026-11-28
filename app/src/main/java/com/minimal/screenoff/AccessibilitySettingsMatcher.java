package com.minimal.screenoff;

/** Pure helper for interpreting Android's enabled Accessibility service settings. */
public final class AccessibilitySettingsMatcher {
    private AccessibilitySettingsMatcher() { }

    public static boolean isServiceEnabled(String accessibilityEnabled, String enabledServices,
            String expectedFullComponent, String expectedShortComponent) {
        if (!"1".equals(accessibilityEnabled) || enabledServices == null
                || enabledServices.trim().isEmpty()
                || expectedFullComponent == null || expectedShortComponent == null) {
            return false;
        }

        for (String component : enabledServices.split(":")) {
            if (expectedFullComponent.equalsIgnoreCase(component)
                    || expectedShortComponent.equalsIgnoreCase(component)) {
                return true;
            }
        }
        return false;
    }
}
