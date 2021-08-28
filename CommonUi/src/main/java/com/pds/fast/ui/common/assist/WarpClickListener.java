package com.pds.fast.ui.common.assist;

import android.view.View;

public abstract class WarpClickListener implements View.OnClickListener {

    private long mLastClickTime;
    private long timeInterval = 1500L;

    public WarpClickListener(long timeInterval) {
        this.timeInterval = timeInterval;
    }

    public WarpClickListener() {
    }

    @Override
    public void onClick(View v) {
        if(enableClick()){
            long nowTime = System.currentTimeMillis();
            if (nowTime - mLastClickTime > timeInterval) {
                onSingleClick(v);
                mLastClickTime = nowTime;
            }
        }
    }

    protected abstract void onSingleClick(View v);

    protected boolean enableClick(){
        return true;
    }
}