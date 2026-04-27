package com.wifiguard.app.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.wifiguard.app.R;
import com.wifiguard.app.model.PasswordAnalysis;
import com.wifiguard.app.security.PasswordAnalyzer;
import com.wifiguard.app.ui.views.RiskGaugeView;

public class SecurityFragment extends Fragment {

    private EditText etPw;
    private LinearProgressIndicator progress;
    private TextView tvLabel, tvEntropy, tvCrack, tvLength;
    private LinearLayout llSuggestions;
    private RiskGaugeView gauge;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_security, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        etPw = v.findViewById(R.id.et_password);
        progress = v.findViewById(R.id.progress_strength);
        tvLabel = v.findViewById(R.id.tv_label);
        tvEntropy = v.findViewById(R.id.tv_entropy);
        tvCrack = v.findViewById(R.id.tv_crack);
        tvLength = v.findViewById(R.id.tv_length);
        llSuggestions = v.findViewById(R.id.ll_suggestions);
        gauge = v.findViewById(R.id.gauge);

        etPw.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                update(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
        update("");
    }

    private void update(String pw) {
        PasswordAnalysis a = PasswordAnalyzer.analyze(pw);
        progress.setProgress(a.strengthScore, true);
        progress.setIndicatorColor(a.strengthColor);
        tvLabel.setText(a.strengthLabel);
        tvLabel.setTextColor(a.strengthColor);
        tvEntropy.setText(getString(R.string.entropy_format, a.entropyBits));
        tvCrack.setText(getString(R.string.crack_format, a.estimatedCrackTime));
        tvLength.setText(getString(R.string.length_format, a.length));
        gauge.setRisk(100 - a.strengthScore);

        llSuggestions.removeAllViews();
        for (String s : a.suggestions) {
            TextView tv = new TextView(getContext());
            tv.setText("鈥� " + s);
            tv.setTextColor(0xFFE2E8F0);
            tv.setTextSize(14f);
            tv.setPadding(0, 8, 0, 8);
            llSuggestions.addView(tv);
        }
    }
  }
