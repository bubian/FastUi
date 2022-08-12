package com.pds.fast.ui.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

public class AutoFitViewPager extends ViewPager {
    public AutoFitViewPager(Context context) {
        this(context, null);
    }

    public AutoFitViewPager(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        addOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                requestLayout();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private int getChildHeight(int heightMeasureSpec, View currentPageView) {
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int mHeight;
        if (hMode == MeasureSpec.EXACTLY) {
            mHeight = height;
        } else if (hMode == MeasureSpec.AT_MOST) {
            mHeight = Math.min(currentPageView.getMeasuredHeight(), height);
        } else {
            mHeight = currentPageView.getMeasuredHeight();
        }
        return MeasureSpec.makeMeasureSpec(mHeight, hMode);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        View currentPageView = getChildAt(getCurrentItem());
        if (currentPageView != null && hMode != MeasureSpec.EXACTLY) {
            currentPageView.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            setMeasuredDimension(getMeasuredWidth(), getChildHeight(heightMeasureSpec, currentPageView));
        }
    }
}
