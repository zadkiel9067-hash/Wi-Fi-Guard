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

import java.util.List;

public class HistoryFragment extends Fragment {

    private RecyclerView recycler;
    private TextView tvEmpty;
    private Button btnClear;
    private HistoryAdapter adapter;
    private ScanHistoryStore store;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        recycler = v.findViewById(R.id.recycler);
        tvEmpty = v.findViewById(R.id.tv_empty);
        btnClear = v.findViewById(R.id.btn_clear);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        store = new ScanHistoryStore(requireContext());
        adapter = new HistoryAdapter(store.getAll());
        recycler.setAdapter(adapter);
        refresh();

        btnClear.setOnClickListener(view -> {
            store.clear();
            adapter.setData(store.getAll());
            refresh();
        });
    }

    private void refresh() {
        List<ScanSession> data = store.getAll();
        adapter.setData(data);
        tvEmpty.setVisibility(data.isEmpty() ? View.VISIBLE : View.GONE);
        recycler.setVisibility(data.isEmpty() ? View.GONE : View.VISIBLE);
        btnClear.setVisibility(data.isEmpty() ? View.GONE : View.VISIBLE);
    }
}
