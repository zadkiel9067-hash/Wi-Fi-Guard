package com.wifiguard.app.ui;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wifiguard.app.R;
import com.wifiguard.app.model.WifiNetwork;
import com.wifiguard.app.ui.views.SignalBarsView;

import java.util.List;

public class NetworkAdapter extends RecyclerView.Adapter<NetworkAdapter.VH> {

    public interface OnClick { void onClick(WifiNetwork n); }

    private List<WifiNetwork> data;
    private final OnClick onClick;

    public NetworkAdapter(List<WifiNetwork> data, OnClick onClick) {
        this.data = data;
        this.onClick = onClick;
    }

    public void setData(List<WifiNetwork> d) {
        this.data = d;
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_network, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        WifiNetwork n = data.get(position);
        h.tvSsid.setText(n.displaySsid());
        h.tvDetail.setText(String.format("%s 鈥� %s 鈥� %d dBm", n.security.displayName(), n.band(), n.level));
        h.signal.setBars(n.signalBars());
        if (n.isEvilTwin) {
            h.tvBadge.setVisibility(View.VISIBLE);
            h.tvBadge.setText(R.string.suspicious);
            h.tvBadge.setBackgroundColor(Color.parseColor("#7F1D1D"));
        } else if (n.riskScore >= 60) {
            h.tvBadge.setVisibility(View.VISIBLE);
            h.tvBadge.setText(R.string.warning);
            h.tvBadge.setBackgroundColor(Color.parseColor("#92400E"));
        } else {
            h.tvBadge.setVisibility(View.GONE);
        }
        h.itemView.setOnClickListener(view -> onClick.onClick(n));
    }

    @Override public int getItemCount() { return data == null ? 0 : data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvSsid, tvDetail, tvBadge;
        SignalBarsView signal;
        VH(@NonNull View v) {
            super(v);
            tvSsid = v.findViewById(R.id.tv_ssid);
            tvDetail = v.findViewById(R.id.tv_detail);
            tvBadge = v.findViewById(R.id.tv_badge);
            signal = v.findViewById(R.id.signal);
        }
    }
  }
