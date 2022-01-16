package com.pds.fast.ui.common.banner;

import android.content.Context;
import android.widget.Scroller;

public class FastBannerScroller extends Scroller {

    //值越大，滑动越慢
    private int mDuration = 800;

    FastBannerScroller(Context context, int duration) {
        super(context);
        mDuration = duration;
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        super.startScroll(startX, startY, dx, dy, mDuration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, mDuration);
    }
}
