package com.wifiguard.app.model;

import java.io.Serializable;

public class WifiNetwork implements Serializable {
    public String ssid;
    public String bssid;
    public int level;          // dBm
    public int frequency;      // MHz
    public Security security;
    public boolean hidden;
    public boolean isEvilTwin;
    public int riskScore;      // 0..100
    public String reason;      // why marked suspicious
    public String vendor;      // OUI vendor

    public WifiNetwork() {}

    public WifiNetwork(String ssid, String bssid, int level, int frequency, Security security, boolean hidden) {
        this.ssid = ssid;
        this.bssid = bssid;
        this.level = level;
        this.frequency = frequency;
        this.security = security;
        this.hidden = hidden;
    }

    /** Signal bars 0..4 */
    public int signalBars() {
        if (level >= -55) return 4;
        if (level >= -65) return 3;
        if (level >= -75) return 2;
        if (level >= -85) return 1;
        return 0;
    }

    public String band() {
        if (frequency >= 2400 && frequency <= 2500) return "2.4 诏蹖诏丕賴乇鬲夭";
        if (frequency >= 4900 && frequency <= 5900) return "5 诏蹖诏丕賴乇鬲夭";
        if (frequency >= 5925) return "6 诏蹖诏丕賴乇鬲夭";
        return "鈥�";
    }

    public String displaySsid() {
        return (ssid == null || ssid.isEmpty()) ? "(倬賳賴丕賳)" : ssid;
    }
}
