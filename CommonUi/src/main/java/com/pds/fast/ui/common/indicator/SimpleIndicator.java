package com.pds.fast.ui.common.indicator;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

public class SimpleIndicator extends View {

    private static final float DEFAULT_RADIUS = dip2px(5);
    private static final float DEFAULT_GAP = dip2px(4);
    private static final int DEFAULT_SELECTED_COLOR = Color.parseColor("#FFA2A4A6");
    private static final int DEFAULT_NORMAL_COLOR = Color.parseColor("#FFE1E3E6");

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float radius = DEFAULT_RADIUS;
    private float gap = DEFAULT_GAP;
    private int selectedColor = DEFAULT_SELECTED_COLOR;
    private int normalColor = DEFAULT_NORMAL_COLOR;

    private int size, position;

    public SimpleIndicator(@NonNull @NotNull Context context) {
        super(context);
    }

    public SimpleIndicator(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = size > 1 ? (int) Math.max(0, size * radius + (size - 1) * gap) : 0;
        int height = (int) radius;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (size <= 1) return;
        for (int i = 0; i < size; i++) {
            float cx = radius / 2 + (radius + gap) * i;
            float cy = radius / 2;
            paint.setColor(i == position ? selectedColor : normalColor);
            canvas.drawCircle(cx, cy, radius / 2, paint);
        }
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void setGap(float gap) {
        this.gap = gap;
    }

    public void setSelectedColor(int selectedColor) {
        this.selectedColor = selectedColor;
    }

    public void setNormalColor(int normalColor) {
        this.normalColor = normalColor;
    }

    public void setSize(int size) {
        this.size = size;
        requestLayout();
    }

    public void setPosition(int position) {
        this.position = position;
        invalidate();
    }


    public static int dip2px(float dp) {
        return (int)(dp * Resources.getSystem().getDisplayMetrics().density + 0.5F);
    }

}