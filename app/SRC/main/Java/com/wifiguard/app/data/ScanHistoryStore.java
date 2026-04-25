package com.wifiguard.app.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.wifiguard.app.model.ScanSession;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ScanHistoryStore {
    private static final String PREFS = "wifi-guard-prefs";
    private static final String KEY = "wifi-guard:history";
    private static final int MAX = 50;

    private final SharedPreferences prefs;

    public ScanHistoryStore(Context ctx) {
        this.prefs = ctx.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public List<ScanSession> getAll() {
        List<ScanSession> list = new ArrayList<>();
        String raw = prefs.getString(KEY, null);
        if (raw == null) return list;
        try {
            JSONArray arr = new JSONArray(raw);
            for (int i = 0; i < arr.length(); i++) {
                list.add(ScanSession.fromJson(arr.getJSONObject(i)));
            }
        } catch (Exception ignored) {}
        Collections.sort(list, new Comparator<ScanSession>() {
            @Override public int compare(ScanSession a, ScanSession b) {
                return Long.compare(b.timestamp, a.timestamp);
            }
        });
        return list;
    }

    public void add(ScanSession s) {
        List<ScanSession> all = getAll();
        all.add(0, s);
        if (all.size() > MAX) all = all.subList(0, MAX);
        try {
            JSONArray arr = new JSONArray();
            for (ScanSession ss : all) arr.put(ss.toJson());
            prefs.edit().putString(KEY, arr.toString()).apply();
        } catch (Exception ignored) {}
    }

    public void clear() {
        prefs.edit().remove(KEY).apply();
    }
}
