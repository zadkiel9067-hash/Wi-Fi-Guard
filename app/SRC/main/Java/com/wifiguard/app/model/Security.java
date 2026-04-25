package com.wifiguard.app.model;

public enum Security {
    OPEN, WEP, WPA, WPA2, WPA3, UNKNOWN;

    public static Security fromCapabilities(String caps) {
        if (caps == null) return UNKNOWN;
        String s = caps.toUpperCase();
        if (s.contains("WPA3") || s.contains("SAE")) return WPA3;
        if (s.contains("WPA2") || s.contains("RSN")) return WPA2;
        if (s.contains("WPA")) return WPA;
        if (s.contains("WEP")) return WEP;
        if (s.contains("ESS") && !s.contains("WPA") && !s.contains("WEP")) return OPEN;
        return UNKNOWN;
    }

    public String displayName() {
        switch (this) {
            case OPEN: return "亘丕夭";
            case WEP: return "WEP";
            case WPA: return "WPA";
            case WPA2: return "WPA2";
            case WPA3: return "WPA3";
            default: return "賳丕賲卮禺氐";
        }
    }
}
