package com.pds.fast.ui.common.drawable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;

import com.pds.fast.ui.common.assist.NumberExKt;

public class DynamicItemBgDrawable extends Drawable {

    private final float dp20 = NumberExKt.dp22px(20);
    private LinearGradient lg;
    private final int[] bg = new int[]{Color.WHITE, Color.parseColor("#FAFAFA")};
    private final float[] position = new float[]{0.1f, 0.1f};
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int tmpH = -1;

    public DynamicItemBgDrawable() {
        //int[] bg = new int[]{Color.WHITE, Color.parseColor("#FAFAFA")};
    }

    @Override
    public void draw(Canvas canvas) {
        Rect rect = getBounds();
        int h = rect.bottom - rect.top;
        float cap = dp20 / h;
        if (null == lg || tmpH != h) {
            lg = build(rect, cap);
            tmpH = h;
        }
        paint.setShader(lg);
        canvas.drawRect(rect, paint);
    }

    private LinearGradient build(Rect rect, float cap) {
        position[0] = 1 - cap;
        position[1] = 1;
        return new LinearGradient(0, 0, 0, rect.bottom, bg, position, Shader.TileMode.REPEAT);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }
}
