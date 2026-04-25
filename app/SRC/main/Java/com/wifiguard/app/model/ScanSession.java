package com.wifiguard.app.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ScanSession {
    public long timestamp;
    public List<WifiNetwork> networks = new ArrayList<>();
    public int suspiciousCount;

    public JSONObject toJson() throws JSONException {
        JSONObject o = new JSONObject();
        o.put("timestamp", timestamp);
        o.put("suspiciousCount", suspiciousCount);
        JSONArray arr = new JSONArray();
        for (WifiNetwork n : networks) {
            JSONObject j = new JSONObject();
            j.put("ssid", n.ssid);
            j.put("bssid", n.bssid);
            j.put("level", n.level);
            j.put("frequency", n.frequency);
            j.put("security", n.security == null ? "UNKNOWN" : n.security.name());
            j.put("hidden", n.hidden);
            j.put("isEvilTwin", n.isEvilTwin);
            j.put("riskScore", n.riskScore);
            j.put("reason", n.reason);
            j.put("vendor", n.vendor);
            arr.put(j);
        }
        o.put("networks", arr);
        return o;
    }

    public static ScanSession fromJson(JSONObject o) throws JSONException {
        ScanSession s = new ScanSession();
        s.timestamp = o.optLong("timestamp");
        s.suspiciousCount = o.optInt("suspiciousCount");
        JSONArray arr = o.optJSONArray("networks");
        if (arr != null) {
            for (int i = 0; i < arr.length(); i++) {
                JSONObject j = arr.getJSONObject(i);
                WifiNetwork n = new WifiNetwork();
                n.ssid = j.optString("ssid", "");
                n.bssid = j.optString("bssid", "");
                n.level = j.optInt("level");
                n.frequency = j.optInt("frequency");
                try {
                    n.security = Security.valueOf(j.optString("security", "UNKNOWN"));
                } catch (Exception e) {
                    n.security = Security.UNKNOWN;
                }
                n.hidden = j.optBoolean("hidden");
                n.isEvilTwin = j.optBoolean("isEvilTwin");
                n.riskScore = j.optInt("riskScore");
                n.reason = j.optString("reason", null);
                n.vendor = j.optString("vendor", null);
                s.networks.add(n);
            }
        }
        return s;
    }
    }
