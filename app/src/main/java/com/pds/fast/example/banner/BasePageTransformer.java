
package com.pds.fast.example.banner;

import android.util.Log;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

public abstract class BasePageTransformer implements ViewPager.PageTransformer {

    protected float boundary = 1f;
    private int pageIndex;

    public void setPageIndex(int index) {
        pageIndex = index;
    }

    @Override
    public void transformPage(View view, float position) {
        ViewPager viewPager;
        if (view.getParent() instanceof ViewPager) {
            viewPager = (ViewPager) view.getParent();
        } else {
            return;
        }
        Log.d("BasePageTransformer", " position=" + position);
        position = getRealPosition(viewPager, view) / 3f;
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