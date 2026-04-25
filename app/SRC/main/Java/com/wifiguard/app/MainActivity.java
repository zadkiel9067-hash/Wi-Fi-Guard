package com.wifiguard.app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.wifiguard.app.ui.EvilTwinsFragment;
import com.wifiguard.app.ui.HistoryFragment;
import com.wifiguard.app.ui.ScannerFragment;
import com.wifiguard.app.ui.SecurityFragment;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQ_PERMS = 101;

    @Override
    protected void attachBaseContext(android.content.Context newBase) {
        // Force Persian locale + RTL.
        Configuration cfg = new Configuration(newBase.getResources().getConfiguration());
        Locale fa = new Locale("fa");
        Locale.setDefault(fa);
        cfg.setLocale(fa);
        cfg.setLayoutDirection(fa);
        super.attachBaseContext(newBase.createConfigurationContext(cfg));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView nav = findViewById(R.id.bottom_nav);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_scanner) return swap(new ScannerFragment());
            if (id == R.id.nav_evil) return swap(new EvilTwinsFragment());
            if (id == R.id.nav_security) return swap(new SecurityFragment());
            if (id == R.id.nav_history) return swap(new HistoryFragment());
            return false;
        });

        if (savedInstanceState == null) {
            swap(new ScannerFragment());
            nav.setSelectedItemId(R.id.nav_scanner);
        }

        requestRuntimePerms();
    }

    private boolean swap(Fragment f) {
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.fragment_container, f);
        tx.commit();
        return true;
    }

    private void requestRuntimePerms() {
        java.util.List<String> req = new java.util.ArrayList<>();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            req.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (Build.VERSION.SDK_INT >= 33
                && ActivityCompat.checkSelfPermission(this, "android.permission.NEARBY_WIFI_DEVICES")
                != PackageManager.PERMISSION_GRANTED) {
            req.add("android.permission.NEARBY_WIFI_DEVICES");
        }
        if (!req.isEmpty()) {
            ActivityCompat.requestPermissions(this, req.toArray(new String[0]), REQ_PERMS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
                       }
