package com.pds.fast.example.banner

import android.content.Context
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class WrapViewPager(context: Context) : ViewPager(context) {
//
//    override fun onTouchEvent(ev: MotionEvent): Boolean {
//        if (ev.action == MotionEvent.ACTION_MOVE){
//            ev.addBatch(ev.eventTime, ev.x / 2, ev.y, ev.pressure, ev.size, ev.metaState)
//        }
//        return super.onTouchEvent(ev)
//    }
}