package com.wifiguard.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.List;

public class EvilTwinsFragment extends Fragment {

    private TextView tvEmpty;
    private RecyclerView recycler;
    private NetworkAdapter adapter;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_evil_twins, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        tvEmpty = v.findViewById(R.id.tv_empty);
        recycler = v.findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NetworkAdapter(new ArrayList<>(), n -> NetworkDetailDialog.show(getParentFragmentManager(), n));
        recycler.setAdapter(adapter);

        ScanHistoryStore store = new ScanHistoryStore(requireContext());
        List<ScanSession> all = store.getAll();
        List<WifiNetwork> suspicious = new ArrayList<>();
        if (!all.isEmpty()) {
            for (WifiNetwork n : all.get(0).networks) {
                if (n.isEvilTwin || n.riskScore >= 60) suspicious.add(n);
            }
        }
        adapter.setData(suspicious);
        tvEmpty.setVisibility(suspicious.isEmpty() ? View.VISIBLE : View.GONE);
        recycler.setVisibility(suspicious.isEmpty() ? View.GONE : View.VISIBLE);
    }
}
