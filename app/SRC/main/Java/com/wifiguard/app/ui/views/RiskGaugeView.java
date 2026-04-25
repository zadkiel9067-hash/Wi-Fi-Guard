package com.wifiguard.app.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class RiskGaugeView extends View {

    private int risk = 0;
    private final Paint bg = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint fg = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint text = new Paint(Paint.ANTI_ALIAS_FLAG);

    public RiskGaugeView(Context c) { super(c); init(); }
    public RiskGaugeView(Context c, @Nullable AttributeSet a) { super(c, a); init(); }

    private void init() {
        bg.setStyle(Paint.Style.STROKE);
        bg.setStrokeWidth(20f);
        bg.setColor(0x33FFFFFF);
        bg.setStrokeCap(Paint.Cap.ROUND);
        fg.setStyle(Paint.Style.STROKE);
        fg.setStrokeWidth(20f);
        fg.setStrokeCap(Paint.Cap.ROUND);
        text.setColor(0xFFFFFFFF);
        text.setTextAlign(Paint.Align.CENTER);
        text.setFakeBoldText(true);
    }

    public void setRisk(int r) {
        this.risk = Math.max(0, Math.min(100, r));
        if (risk < 30) fg.setColor(0xFF22C55E);
        else if (risk < 60) fg.setColor(0xFFFBBF24);
        else if (risk < 80) fg.setColor(0xFFF59E0B);
        else fg.setColor(0xFFEF4444);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth(), h = getHeight();
        float pad = 30f;
        RectF rect = new RectF(pad, pad, w - pad, h - pad);
        canvas.drawArc(rect, 135, 270, false, bg);
        float sweep = 270f * (risk / 100f);
        canvas.drawArc(rect, 135, sweep, false, fg);
        text.setTextSize(Math.min(w, h) * 0.28f);
        canvas.drawText(String.valueOf(risk), w / 2f, h / 2f + text.getTextSize() / 3f, text);
    }
}
