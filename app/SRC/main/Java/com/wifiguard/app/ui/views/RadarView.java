package com.wifiguard.app.ui.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

public class RadarView extends View {

    private final Paint ringPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint sweepPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float angle = 0f;
    private ValueAnimator anim;
    private boolean running = false;

    public RadarView(Context context) { super(context); init(); }
    public RadarView(Context context, @Nullable AttributeSet attrs) { super(context, attrs); init(); }

    private void init() {
        ringPaint.setStyle(Paint.Style.STROKE);
        ringPaint.setStrokeWidth(2f);
        ringPaint.setColor(0x553B82F6);
        dotPaint.setColor(0xFF3B82F6);
        sweepPaint.setStyle(Paint.Style.FILL);
    }

    public void start() {
        if (running) return;
        running = true;
        anim = ValueAnimator.ofFloat(0f, 360f);
        anim.setDuration(1800);
        anim.setRepeatCount(ValueAnimator.INFINITE);
        anim.setInterpolator(new LinearInterpolator());
        anim.addUpdateListener(a -> {
            angle = (float) a.getAnimatedValue();
            invalidate();
        });
        anim.start();
    }

    public void stop() {
        running = false;
        if (anim != null) { anim.cancel(); anim = null; }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth(), h = getHeight();
        float cx = w / 2f, cy = h / 2f;
        float radius = Math.min(w, h) / 2f - 8f;

        // Background glow
        Paint glow = new Paint(Paint.ANTI_ALIAS_FLAG);
        glow.setShader(new RadialGradient(cx, cy, radius, 0x223B82F6, 0x00000000, Shader.TileMode.CLAMP));
        canvas.drawCircle(cx, cy, radius, glow);

        // Concentric rings
        for (int i = 1; i <= 4; i++) {
            canvas.drawCircle(cx, cy, radius * i / 4f, ringPaint);
        }
        // Cross
        canvas.drawLine(cx - radius, cy, cx + radius, cy, ringPaint);
        canvas.drawLine(cx, cy - radius, cx, cy + radius, ringPaint);

        if (running) {
            canvas.save();
            canvas.rotate(angle, cx, cy);
            sweepPaint.setShader(new SweepGradient(cx, cy,
                    new int[]{0x00000000, 0x6622D3EE, 0x0022D3EE},
                    new float[]{0f, 0.15f, 0.3f}));
            canvas.drawCircle(cx, cy, radius, sweepPaint);
            canvas.restore();
        }

        // Center dot
        dotPaint.setColor(Color.parseColor("#22D3EE"));
        canvas.drawCircle(cx, cy, 6f, dotPaint);
    }
}
