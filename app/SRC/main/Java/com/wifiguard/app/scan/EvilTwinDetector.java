package com.wifiguard.app.scan;

import com.wifiguard.app.model.Security;
import com.wifiguard.app.model.WifiNetwork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EvilTwinDetector {

    /**
     * Marks suspicious networks (evil twins / impersonation) and assigns risk scores.
     * Algorithm:
     *   - Same/similar SSID with different security types 鈫� suspicious.
     *   - Open network with same name as a protected one 鈫� suspicious.
     *   - Levenshtein distance 鈮� 1 between SSIDs (after homoglyph normalization) 鈫� suspicious.
     */
    public static void analyze(List<WifiNetwork> networks) {
        // Group by normalized SSID
        Map<String, List<WifiNetwork>> groups = new HashMap<>();
        for (WifiNetwork n : networks) {
            String norm = normalize(n.ssid);
            if (norm.isEmpty()) continue;
            groups.computeIfAbsent(norm, k -> new ArrayList<>()).add(n);
        }

        // Same-name groups: if 鈮�2 with different security 鈫� all suspicious
        for (Map.Entry<String, List<WifiNetwork>> e : groups.entrySet()) {
            List<WifiNetwork> g = e.getValue();
            if (g.size() < 2) continue;
            boolean diffSec = false;
            boolean hasOpen = false;
            boolean hasProtected = false;
            for (WifiNetwork n : g) {
                if (n.security == Security.OPEN) hasOpen = true;
                else hasProtected = true;
            }
            for (int i = 0; i < g.size() && !diffSec; i++) {
                for (int j = i + 1; j < g.size() && !diffSec; j++) {
                    if (g.get(i).security != g.get(j).security) diffSec = true;
                }
            }
            if (diffSec || (hasOpen && hasProtected)) {
                for (WifiNetwork n : g) {
                    n.isEvilTwin = true;
                    n.reason = "賳丕賲 鬲讴乇丕乇蹖 亘丕 鬲賳馗蹖賲丕鬲 丕賲賳蹖鬲蹖 賲鬲賮丕賵鬲";
                }
            }
        }

        // Levenshtein-based fuzzy matches across distinct normalized SSIDs
        List<String> keys = new ArrayList<>(groups.keySet());
        for (int i = 0; i < keys.size(); i++) {
            for (int j = i + 1; j < keys.size(); j++) {
                String a = keys.get(i);
                String b = keys.get(j);
                if (Math.abs(a.length() - b.length()) > 1) continue;
                if (Math.min(a.length(), b.length()) < 4) continue;
                if (levenshtein(a, b, 2) <= 1) {
                    for (WifiNetwork n : groups.get(a)) {
                        n.isEvilTwin = true;
                        if (n.reason == null) n.reason = "賳丕賲 賲卮丕亘賴 卮亘讴賴 丿蹖诏乇 (丕丨鬲賲丕賱 噩毓賱)";
                    }
                    for (WifiNetwork n : groups.get(b)) {
                        n.isEvilTwin = true;
                        if (n.reason == null) n.reason = "賳丕賲 賲卮丕亘賴 卮亘讴賴 丿蹖诏乇 (丕丨鬲賲丕賱 噩毓賱)";
                    }
                }
            }
        }

        // Risk score per network
        for (WifiNetwork n : networks) {
            int risk = 0;
            switch (n.security == null ? Security.UNKNOWN : n.security) {
                case OPEN: risk += 80; break;
                case WEP: risk += 70; break;
                case WPA: risk += 40; break;
                case WPA2: risk += 15; break;
                case WPA3: risk += 5; break;
                default: risk += 30;
            }
            if (n.hidden) risk += 20;
            if (n.isEvilTwin) risk += 30;
            if (risk > 100) risk = 100;
            n.riskScore = risk;
            if (n.reason == null) {
                if (n.security == Security.OPEN) n.reason = "卮亘讴賴 亘丿賵賳 乇賲夭";
                else if (n.security == Security.WEP) n.reason = "乇賲夭賳诏丕乇蹖 囟毓蹖賮 WEP";
            }
        }
    }

    /** Lowercase + homoglyph normalization (VV鈫扺, 0鈫抩, 1鈫抣, |鈫抣, etc.). */
    private static String normalize(String s) {
        if (s == null) return "";
        String x = s.toLowerCase().trim();
        x = x.replace("vv", "w")
             .replace("rn", "m")
             .replace("0", "o")
             .replace("1", "l")
             .replace("|", "l")
             .replace("!", "i")
             .replace("$", "s")
             .replace("@", "a")
             .replace("3", "e")
             .replace("5", "s");
        StringBuilder sb = new StringBuilder();
        for (char c : x.toCharArray()) {
            if (c >= 'a' && c <= 'z') sb.append(c);
            else if (c >= '0' && c <= '9') sb.append(c);
            else if (Character.isLetterOrDigit(c)) sb.append(c);
        }
        return sb.toString();
    }

    /** Bounded Levenshtein distance; returns >limit if exceeded. */
    private static int levenshtein(String a, String b, int limit) {
        int m = a.length(), n = b.length();
        if (Math.abs(m - n) > limit) return limit + 1;
        int[] prev = new int[n + 1];
        int[] cur = new int[n + 1];
        for (int j = 0; j <= n; j++) prev[j] = j;
        for (int i = 1; i <= m; i++) {
            cur[0] = i;
            int rowMin = cur[0];
            for (int j = 1; j <= n; j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                cur[j] = Math.min(Math.min(cur[j - 1] + 1, prev[j] + 1), prev[j - 1] + cost);
                if (cur[j] < rowMin) rowMin = cur[j];
            }
            if (rowMin > limit) return limit + 1;
            int[] tmp = prev; prev = cur; cur = tmp;
        }
        return prev[n];
    }
                    }
