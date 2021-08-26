package com.pds.fast.ui.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class FastProgressView extends View {
    private int radius;
    private int strokeWidth;

    private final Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final RectF assistRectF = new RectF();
    private final Path path = new Path();

    private float progress;

    private int progressTop;

    public FastProgressView(Context context) {
        super(context);
        init(context, null);
    }

    public FastProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FastProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FastProgressView, 0, 0);
        int bgColor = a.getColor(R.styleable.FastProgressView_bg_Color, Color.RED);
        int progressColor = a.getColor(R.styleable.FastProgressView_progress_Color, Color.BLUE);
        radius = a.getDimensionPixelSize(R.styleable.FastProgressView_android_radius, 0);
        strokeWidth = a.getDimensionPixelSize(R.styleable.FastProgressView_android_strokeWidth, 0);
        int strokeColor = a.getColor(R.styleable.FastProgressView_android_strokeColor, Color.TRANSPARENT);
        a.recycle();

        bgPaint.setColor(bgColor);
        progressPaint.setColor(progressColor);

        strokePaint.setStrokeWidth(strokeWidth);
        strokePaint.setColor(strokeColor);
        strokePaint.setStyle(Paint.Style.STROKE);
        progressTop = 2 * radius;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public float getProgress() {
        return progress;
    }

    public void setBaseAttr(int bgColor, int progressColor, int radius) {
        bgPaint.setColor(bgColor);
        progressPaint.setColor(progressColor);
        this.radius = radius;
        progressTop = 2 * radius;
    }

    public void setStrokeAttr(int strokeWidth, int strokeColor) {
        strokePaint.setStrokeWidth(strokeWidth);
        strokePaint.setColor(strokeColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int height = getHeight();
        int width = getWidth();

        int top = height >= progressTop ? 0 : height - progressTop;
        assistRectF.set(0, top, width, height);
        // 剪切画布
        if (radius > 0) {
            path.addRoundRect(assistRectF, radius, radius, Path.Direction.CW);
            canvas.clipPath(path);
        }
        // 绘制背景
        canvas.drawRect(0, 0, width, height, bgPaint);
        // 绘制进度
        float progressSize = progress > 0.99f ? width : (progress * width);
        canvas.drawRect(0, 0, progressSize, height, progressPaint);
        // 绘制描边
        if (strokeWidth > 0) {
            canvas.drawRect(assistRectF, strokePaint);
        }
    }
}

