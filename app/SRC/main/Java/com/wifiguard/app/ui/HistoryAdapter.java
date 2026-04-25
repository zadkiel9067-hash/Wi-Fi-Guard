package com.wifiguard.app.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wifiguard.app.R;
import com.wifiguard.app.model.ScanSession;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.VH> {

    private List<ScanSession> data;
    private final SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd HH:mm", new Locale("fa"));

    public HistoryAdapter(List<ScanSession> data) { this.data = data; }

    public void setData(List<ScanSession> d) { this.data = d; notifyDataSetChanged(); }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_session, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        ScanSession s = data.get(position);
        h.tvTime.setText(fmt.format(new Date(s.timestamp)));
        h.tvCount.setText(h.itemView.getContext().getString(R.string.history_summary, s.networks.size(), s.suspiciousCount));
    }

    @Override public int getItemCount() { return data == null ? 0 : data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTime, tvCount;
        VH(@NonNull View v) { super(v);
            tvTime = v.findViewById(R.id.tv_time);
            tvCount = v.findViewById(R.id.tv_count);
        }
    }
}
