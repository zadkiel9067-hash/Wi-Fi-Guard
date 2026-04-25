package com.wifiguard.app.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class SignalBarsView extends View {

    private int bars = 0;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public SignalBarsView(Context context) { super(context); }
    public SignalBarsView(Context context, @Nullable AttributeSet attrs) { super(context, attrs); }

    public void setBars(int bars) {
        this.bars = Math.max(0, Math.min(4, bars));
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth(), h = getHeight();
        int gap = 4;
        int barW = (w - gap * 3) / 4;
        for (int i = 0; i < 4; i++) {
            float bh = h * (i + 1) / 4f;
            float x = i * (barW + gap);
            float y = h - bh;
            paint.setColor(i < bars ? 0xFF22D3EE : 0x33FFFFFF);
            canvas.drawRoundRect(x, y, x + barW, h, 3f, 3f, paint);
        }
    }
                                 }
