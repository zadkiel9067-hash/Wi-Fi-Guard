package com.wifiguard.app.scan;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.core.content.ContextCompat;

import com.wifiguard.app.model.Security;
import com.wifiguard.app.model.WifiNetwork;
import com.wifiguard.app.util.OuiLookup;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Scans for nearby WiFi networks. Uses real Android WifiManager when available;
 * falls back to deterministic simulated data otherwise (emulator, missing
 * permissions, or device that throttled the request).
 */
public class WifiScanner {

    public interface Listener {
        void onNetworks(List<WifiNetwork> networks, boolean simulated);
        void onError(String message);
    }

    private final Context appCtx;
    private final WifiManager wifiManager;
    private BroadcastReceiver receiver;
    private Listener listener;
    private final Handler main = new Handler(Looper.getMainLooper());

    public WifiScanner(Context ctx) {
        this.appCtx = ctx.getApplicationContext();
        this.wifiManager = (WifiManager) appCtx.getSystemService(Context.WIFI_SERVICE);
    }

    public boolean hasPermissions() {
        boolean loc = ContextCompat.checkSelfPermission(appCtx, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
        boolean nearby = true;
        if (Build.VERSION.SDK_INT >= 33) {
            nearby = ContextCompat.checkSelfPermission(appCtx, "android.permission.NEARBY_WIFI_DEVICES")
                    == PackageManager.PERMISSION_GRANTED;
        }
        return loc && nearby;
    }

    public void startScan(Listener l) {
        this.listener = l;

        if (wifiManager == null) {
            emit(simulate(), true);
            return;
        }

        if (!hasPermissions()) {
            emit(simulate(), true);
            return;
        }

        unregister();
        receiver = new BroadcastReceiver() {
            @Override public void onReceive(Context c, Intent i) {
                boolean ok = i.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, true);
                List<WifiNetwork> out = readResults();
                if (!ok || out.isEmpty()) {
                    emit(simulate(), true);
                } else {
                    emit(out, false);
                }
                unregister();
            }
        };
        IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        appCtx.registerReceiver(receiver, filter);

        boolean started;
        try {
            started = wifiManager.startScan();
        } catch (SecurityException e) {
            started = false;
        }

        if (!started) {
            // Some devices return false but still deliver cached results.
            main.postDelayed(() -> {
                if (receiver != null) {
                    List<WifiNetwork> out = readResults();
                    if (out.isEmpty()) emit(simulate(), true);
                    else emit(out, false);
                    unregister();
                }
            }, 1500);
        }

        // Safety timeout
        main.postDelayed(() -> {
            if (receiver != null) {
                List<WifiNetwork> out = readResults();
                if (out.isEmpty()) emit(simulate(), true);
                else emit(out, false);
                unregister();
            }
        }, 8000);
    }

    @SuppressLint("MissingPermission")
    private List<WifiNetwork> readResults() {
        List<WifiNetwork> out = new ArrayList<>();
        try {
            List<ScanResult> raw = wifiManager.getScanResults();
            for (ScanResult sr : raw) {
                String ssidStr = sr.SSID == null ? "" : sr.SSID;
                WifiNetwork n = new WifiNetwork(
                        ssidStr,
                        sr.BSSID,
                        sr.level,
                        sr.frequency,
                        Security.fromCapabilities(sr.capabilities),
                        ssidStr.isEmpty()
                );
                n.vendor = OuiLookup.lookup(sr.BSSID);
                out.add(n);
            }
        } catch (SecurityException ignored) {}
        return out;
    }

    private void emit(List<WifiNetwork> list, boolean simulated) {
        EvilTwinDetector.analyze(list);
        if (listener != null) {
            main.post(() -> listener.onNetworks(list, simulated));
        }
    }

    public void stopScan() {
        unregister();
    }

    private void unregister() {
        if (receiver != null) {
            try { appCtx.unregisterReceiver(receiver); } catch (Exception ignored) {}
            receiver = null;
        }
    }

    /* --------- Simulation fallback --------- */

    private static final String[] SIM_NAMES = {
            "Home_5G", "Home_5G", "MTN-Pars-Free", "Mokhaberat", "FreeWiFi",
            "iPhone Salar", "OFFICE", "Office", "CafeNet", "Neighbour-2.4",
            "Hidden", "ParsNet"
    };

    private List<WifiNetwork> simulate() {
        Random r = new Random(System.currentTimeMillis() / 9000L);
        List<WifiNetwork> list = new ArrayList<>();
        int count = 7 + r.nextInt(5);
        for (int i = 0; i < count; i++) {
            String ssid = SIM_NAMES[r.nextInt(SIM_NAMES.length)];
            // Inject deliberate evil twin cases
            if (i == 2) ssid = "Home_5G";
            if (i == 3) ssid = "Hone_5G"; // typo-twin
            String bssid = randomBssid(r);
            int level = -30 - r.nextInt(60);
            int freq = r.nextBoolean() ? (2412 + r.nextInt(11) * 5) : (5180 + r.nextInt(20) * 5);
            Security sec;
            if ("FreeWiFi".equals(ssid) || (i == 2 && r.nextBoolean())) sec = Security.OPEN;
            else if (i % 5 == 0) sec = Security.WPA;
            else if (i % 7 == 0) sec = Security.WEP;
            else if (i % 3 == 0) sec = Security.WPA3;
            else sec = Security.WPA2;
            boolean hidden = "Hidden".equals(ssid) || ssid.isEmpty();
            WifiNetwork n = new WifiNetwork(hidden ? "" : ssid, bssid, level, freq, sec, hidden);
            n.vendor = OuiLookup.lookup(bssid);
            list.add(n);
        }
        return list;
    }

    private String randomBssid(Random r) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            if (i > 0) sb.append(':');
            sb.append(String.format("%02X", r.nextInt(256)));
        }
        return sb.toString();
    }
          }
