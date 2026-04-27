package com.wifiguard.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wifiguard.app.R;
import com.wifiguard.app.data.ScanHistoryStore;
import com.wifiguard.app.model.ScanSession;
import com.wifiguard.app.model.WifiNetwork;
import com.wifiguard.app.scan.WifiScanner;
import com.wifiguard.app.ui.views.RadarView;

import java.util.ArrayList;
import java.util.List;

public class ScannerFragment extends Fragment {

    private RadarView radar;
    private Button btnScan;
    private TextView tvStatus;
    private TextView tvCount;
    private RecyclerView recycler;
    private NetworkAdapter adapter;
    private WifiScanner scanner;
    private ScanHistoryStore history;
    private boolean scanning = false;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scanner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        radar = v.findViewById(R.id.radar);
        btnScan = v.findViewById(R.id.btn_scan);
        tvStatus = v.findViewById(R.id.tv_status);
        tvCount = v.findViewById(R.id.tv_count);
        recycler = v.findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NetworkAdapter(new ArrayList<>(), n -> NetworkDetailDialog.show(getParentFragmentManager(), n));
        recycler.setAdapter(adapter);

        scanner = new WifiScanner(requireContext());
        history = new ScanHistoryStore(requireContext());

        btnScan.setOnClickListener(view -> {
            if (scanning) stopScan(); else startScan();
        });
    }

    private void startScan() {
        scanning = true;
        btnScan.setText(R.string.stop_scan);
        tvStatus.setText(R.string.scanning_in_progress);
        radar.start();
        scanner.startScan(new WifiScanner.Listener() {
            @Override public void onNetworks(List<WifiNetwork> networks, boolean simulated) {
                onResult(networks, simulated);
            }
            @Override public void onError(String message) {
                tvStatus.setText(message);
                stopScan();
            }
        });
    }

    private void stopScan() {
        scanning = false;
        btnScan.setText(R.string.start_scan);
        radar.stop();
        scanner.stopScan();
    }

    private void onResult(List<WifiNetwork> networks, boolean simulated) {
        adapter.setData(networks);
        int suspicious = 0;
        for (WifiNetwork n : networks) if (n.isEvilTwin || n.riskScore >= 60) suspicious++;
        tvCount.setText(getString(R.string.found_networks, networks.size(), suspicious));
        tvStatus.setText(simulated ? getString(R.string.scan_simulated) : getString(R.string.scan_real));
        ScanSession s = new ScanSession();
        s.timestamp = System.currentTimeMillis();
        s.networks = new ArrayList<>(networks);
        s.suspiciousCount = suspicious;
        history.add(s);
        stopScan();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        if (scanner != null) scanner.stopScan();
        radar.stop();
    }
          }
