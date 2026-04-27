package com.wifiguard.app.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.wifiguard.app.R;
import com.wifiguard.app.model.WifiNetwork;
import com.wifiguard.app.ui.views.RiskGaugeView;

public class NetworkDetailDialog extends DialogFragment {

    private static WifiNetwork pending;

    public static void show(FragmentManager fm, WifiNetwork n) {
        pending = n;
        new NetworkDetailDialog().show(fm, "detail");
    }

    @Override @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_network_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        WifiNetwork n = pending;
        if (n == null) { dismissAllowingStateLoss(); return; }
        ((TextView) v.findViewById(R.id.tv_ssid)).setText(n.displaySsid());
        ((TextView) v.findViewById(R.id.tv_bssid)).setText(n.bssid != null ? n.bssid : "鈥�");
        ((TextView) v.findViewById(R.id.tv_security)).setText(n.security.displayName());
        ((TextView) v.findViewById(R.id.tv_band)).setText(n.band());
        ((TextView) v.findViewById(R.id.tv_signal)).setText(n.level + " dBm");
        ((TextView) v.findViewById(R.id.tv_vendor)).setText(n.vendor != null ? n.vendor : "鈥�");
        TextView reason = v.findViewById(R.id.tv_reason);
        if (n.reason != null) {
            reason.setVisibility(View.VISIBLE);
            reason.setText(n.reason);
        } else {
            reason.setVisibility(View.GONE);
        }
        ((RiskGaugeView) v.findViewById(R.id.gauge)).setRisk(n.riskScore);
        v.findViewById(R.id.btn_close).setOnClickListener(view -> dismissAllowingStateLoss());
    }
         }
