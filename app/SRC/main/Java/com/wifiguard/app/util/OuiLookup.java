package com.wifiguard.app.util;

import java.util.HashMap;
import java.util.Map;

public class OuiLookup {
    private static final Map<String, String> OUI = new HashMap<>();
    static {
        OUI.put("00:1A:2B", "Cisco");
        OUI.put("00:1B:63", "Apple");
        OUI.put("00:23:6C", "Apple");
        OUI.put("00:25:00", "Apple");
        OUI.put("3C:5A:B4", "Google");
        OUI.put("F4:F5:E8", "Google");
        OUI.put("00:50:F2", "Microsoft");
        OUI.put("00:1D:0F", "TP-Link");
        OUI.put("14:CC:20", "TP-Link");
        OUI.put("D8:0D:17", "TP-Link");
        OUI.put("00:14:6C", "Netgear");
        OUI.put("E0:46:9A", "Netgear");
        OUI.put("00:24:01", "D-Link");
        OUI.put("28:10:7B", "D-Link");
        OUI.put("00:1F:33", "Linksys");
        OUI.put("48:F8:B3", "Linksys");
        OUI.put("00:11:32", "Synology");
        OUI.put("00:1E:E5", "Huawei");
        OUI.put("00:25:9E", "Huawei");
        OUI.put("4C:54:99", "Huawei");
        OUI.put("00:1B:11", "Samsung");
        OUI.put("E8:50:8B", "Samsung");
        OUI.put("00:0C:42", "Routerboard/Mikrotik");
        OUI.put("4C:5E:0C", "Mikrotik");
        OUI.put("00:E0:4C", "Realtek");
    }

    public static String lookup(String bssid) {
        if (bssid == null || bssid.length() < 8) return null;
        String prefix = bssid.substring(0, 8).toUpperCase();
        return OUI.get(prefix);
    }
          }
