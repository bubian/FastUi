package com.pds.fast.ui.common.banner.transformers;

import android.util.Log;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

public abstract class BasePageTransformer implements ViewPager.PageTransformer {

    protected float boundary = 1f;

    // https://developer.android.google.cn/training/animation/screen-slide?hl=zh-cn
    @Override
    public void transformPage(View view, float position) {
        ViewPager viewPager;
        if (view.getParent() instanceof ViewPager) {
            viewPager = (ViewPager) view.getParent();
        } else {
            return;
        }
        position = getRealPosition(viewPager, view);
        Log.d("test:", "position=" + position + " view2 = " + view.toString());
        if (position < -boundary) {
            handleInvisiblePage(view, position);
        } else if (position <= 0.0f) {
            handleLeftPage(view, position);
        } else if (position <= boundary) {
            handleRightPage(view, position);
        } else {
            handleInvisiblePage(view, position);
        }
    }

    private float getRealPosition(ViewPager viewPager, View page) {
        int width = viewPager.getMeasuredWidth() - viewPager.getPaddingLeft() - viewPager.getPaddingRight();
        return (float) (page.getLeft() - viewPager.getScrollX() - viewPager.getPaddingLeft()) / width;
    }

    public abstract void handleInvisiblePage(View view, float position);

    public abstract void handleLeftPage(View view, float position);

    public abstract void handleRightPage(View view, float position);
}