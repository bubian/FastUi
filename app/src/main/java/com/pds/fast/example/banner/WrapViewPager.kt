package com.pds.fast.example.banner

import android.content.Context
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class WrapViewPager(context: Context) : ViewPager(context) {

    private var mInitialMotionX = 0f

    override fun onTouchEvent(ev: MotionEvent): Boolean {

        if (ev.action == MotionEvent.ACTION_DOWN && ev.edgeFlags != 0) {
            return false;
        }

        if (adapter == null || adapter!!.count == 0) {
            return false;
        }

        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                mInitialMotionX = ev.x
            }
        }
        return if (ev.action == MotionEvent.ACTION_MOVE || ev.action == MotionEvent.ACTION_UP) {
            super.onTouchEvent(
                MotionEvent.obtain(
                    ev.downTime,
                    ev.eventTime,
                    ev.action,
                    ev.pointerCount,
                    mInitialMotionX + (ev.x - mInitialMotionX) / 2,
                    ev.y,
                    ev.pressure,
                    ev.size,
                    ev.metaState,
                    ev.xPrecision,
                    ev.yPrecision,
                    ev.deviceId,
                    ev.edgeFlags
                )
            )
        } else super.onTouchEvent(ev)
    }
}