package com.pds.fast.ui.common.banner;

import android.view.View;

public abstract class OnDoubleClickListener implements View.OnClickListener {

    private int mThrottleFirstTime = 1000;
    private long mLastClickTime = 0;

    public OnDoubleClickListener() {
    }

    public OnDoubleClickListener(int throttleFirstTime) {
        mThrottleFirstTime = throttleFirstTime;
    }

    @Override
    public void onClick(View v) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - mLastClickTime > mThrottleFirstTime) {
            mLastClickTime = currentTime;
            onNoDoubleClick(v);
        }
    }

    public abstract void onNoDoubleClick(View v);
}
