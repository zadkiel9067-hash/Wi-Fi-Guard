package com.wifiguard.app.model;

import java.util.ArrayList;
import java.util.List;

public class PasswordAnalysis {
    public int strengthScore;     // 0..100
    public String strengthLabel;  // "亘爻蹖丕乇 囟毓蹖賮", ...
    public int strengthColor;     // ARGB
    public double entropyBits;
    public int length;
    public boolean hasLower, hasUpper, hasDigit, hasSymbol;
    public boolean isCommon;
    public String estimatedCrackTime;
    public List<String> suggestions = new ArrayList<>();
}
