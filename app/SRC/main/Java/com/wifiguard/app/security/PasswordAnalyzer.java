package com.wifiguard.app.security;

import android.graphics.Color;

import com.wifiguard.app.model.PasswordAnalysis;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PasswordAnalyzer {

    private static final Set<String> COMMON = new HashSet<>(Arrays.asList(
            "password", "12345678", "123456789", "1234567890", "qwerty", "qwerty123",
            "admin", "admin123", "letmein", "welcome", "iloveyou", "monkey", "dragon",
            "master", "abc123", "111111", "000000", "1qaz2wsx", "password1",
            "internet", "wifi", "wifipassword", "router", "default", "support",
            "khanevadeh", "khoneh", "iran1234", "tehran", "mtn-pars-free",
            "passw0rd", "p@ssw0rd", "welcome1", "trustno1"
    ));

    /** Estimated guesses per second from a modern offline cracker. */
    private static final double GUESSES_PER_SECOND = 1e10;

    public static PasswordAnalysis analyze(String pw) {
        PasswordAnalysis a = new PasswordAnalysis();
        if (pw == null) pw = "";
        a.length = pw.length();
        for (int i = 0; i < pw.length(); i++) {
            char c = pw.charAt(i);
            if (c >= 'a' && c <= 'z') a.hasLower = true;
            else if (c >= 'A' && c <= 'Z') a.hasUpper = true;
            else if (c >= '0' && c <= '9') a.hasDigit = true;
            else a.hasSymbol = true;
        }

        int pool = 0;
        if (a.hasLower) pool += 26;
        if (a.hasUpper) pool += 26;
        if (a.hasDigit) pool += 10;
        if (a.hasSymbol) pool += 33;
        if (pool == 0) pool = 1;

        a.entropyBits = a.length * (Math.log(pool) / Math.log(2));
        a.isCommon = COMMON.contains(pw.toLowerCase());

        // Score: scale entropy with penalties
        double e = a.entropyBits;
        if (a.isCommon) e *= 0.1;
        if (a.length < 8) e *= 0.5;
        a.strengthScore = (int) Math.max(0, Math.min(100, e * 1.1));

        if (a.strengthScore < 28) {
            a.strengthLabel = "亘爻蹖丕乇 囟毓蹖賮";
            a.strengthColor = Color.parseColor("#EF4444");
        } else if (a.strengthScore < 50) {
            a.strengthLabel = "囟毓蹖賮";
            a.strengthColor = Color.parseColor("#F59E0B");
        } else if (a.strengthScore < 70) {
            a.strengthLabel = "賲鬲賵爻胤";
            a.strengthColor = Color.parseColor("#FBBF24");
        } else if (a.strengthScore < 90) {
            a.strengthLabel = "賯賵蹖";
            a.strengthColor = Color.parseColor("#22C55E");
        } else {
            a.strengthLabel = "亘爻蹖丕乇 賯賵蹖";
            a.strengthColor = Color.parseColor("#10B981");
        }

        // Estimate crack time (offline)
        double combos = Math.pow(2, a.entropyBits);
        double seconds = combos / GUESSES_PER_SECOND / 2.0;
        a.estimatedCrackTime = humanTime(seconds);

        // Suggestions
        if (a.length < 12) a.suggestions.add("胤賵賱 乇賲夭 乇丕 亘賴 丨丿丕賯賱 郾鄄 讴丕乇丕讴鬲乇 亘乇爻丕賳蹖丿.");
        if (!a.hasUpper) a.suggestions.add("丕夭 丨乇賵賮 亘夭乇诏 丕賳诏賱蹖爻蹖 丕爻鬲賮丕丿賴 讴賳蹖丿.");
        if (!a.hasDigit) a.suggestions.add("丕毓丿丕丿 乇丕 亘賴 乇賲夭 丕囟丕賮賴 讴賳蹖丿.");
        if (!a.hasSymbol) a.suggestions.add("讴丕乇丕讴鬲乇賴丕蹖 賵蹖跇賴 (! @ # $ %) 丕囟丕賮賴 讴賳蹖丿.");
        if (a.isCommon) a.suggestions.add("丕蹖賳 乇賲夭 丿乇 賮賴乇爻鬲 乇賲夭賴丕蹖 乇丕蹖噩 賵噩賵丿 丿丕乇丿. 讴丕賲賱丕賸 鬲睾蹖蹖乇卮 丿賴蹖丿.");
        if (a.suggestions.isEmpty()) a.suggestions.add("乇賲夭 卮賲丕 丿乇 賵囟毓蹖鬲 禺賵亘蹖 丕爻鬲. 賴乇 趩賳丿 賲丕賴 蹖讴鈥屫ㄘж� 鬲睾蹖蹖乇卮 丿賴蹖丿.");

        return a;
    }

    private static String humanTime(double seconds) {
        if (seconds < 1) return "讴賲鬲乇 丕夭 蹖讴 孬丕賳蹖賴";
        if (seconds < 60) return ((int) seconds) + " 孬丕賳蹖賴";
        if (seconds < 3600) return ((int) (seconds / 60)) + " 丿賯蹖賯賴";
        if (seconds < 86400) return ((int) (seconds / 3600)) + " 爻丕毓鬲";
        if (seconds < 2592000) return ((int) (seconds / 86400)) + " 乇賵夭";
        if (seconds < 31536000) return ((int) (seconds / 2592000)) + " 賲丕賴";
        double years = seconds / 31536000.0;
        if (years < 1000) return ((int) years) + " 爻丕賱";
        if (years < 1e6) return String.format("%.1f 賴夭丕乇 爻丕賱", years / 1000);
        if (years < 1e9) return String.format("%.1f 賲蹖賱蹖賵賳 爻丕賱", years / 1e6);
        return "賲蹖賱蹖丕乇丿賴丕 爻丕賱";
    }
}
