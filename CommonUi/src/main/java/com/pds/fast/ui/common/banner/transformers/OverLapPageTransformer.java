package com.pds.fast.ui.common.banner.transformers;

import android.view.View;

import androidx.core.view.ViewCompat;

public class OverLapPageTransformer extends BasePageTransformer {

    private float scaleValue = 0.6F;
    private float alphaValue = 1f;

    public OverLapPageTransformer() {
    }

    public OverLapPageTransformer(float scaleValue, float alphaValue) {
        this.scaleValue = scaleValue;
        this.alphaValue = alphaValue;
    }

    @Override
    public void handleInvisiblePage(View view, float position) {
        view.setAlpha(1);
        if (position > 0) {
            position = -position;
        }
        ViewCompat.setTranslationZ(view, position);
        view.setScaleY(scaleValue);
    }

    @Override
    public void handleLeftPage(View view, float position) {
        view.setAlpha(1 + position * (1 - alphaValue));
        ViewCompat.setTranslationZ(view, position);
        float scale = Math.max(scaleValue, 1 - 0.4f * -position);
        view.setScaleY(scale);
    }

    @Override
    public void handleRightPage(View view, float position) {
        view.setAlpha(1 - position * (1 - alphaValue));
        ViewCompat.setTranslationZ(view, -position);
        float scale = Math.max(scaleValue, 1 - 0.4f * position);
        view.setScaleY(scale);
    }
}
