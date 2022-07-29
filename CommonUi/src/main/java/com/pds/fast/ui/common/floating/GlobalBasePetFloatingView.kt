package com.pds.fast.ui.common.floating

import android.content.Context
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.FrameLayout
import com.pds.fast.ui.common.floating.manager.onBackPressed
import com.pds.fast.ui.common.floating.manager.shouldDealBackKey
import com.pds.fast.ui.common.floating.other.TouchProxy

open class GlobalBasePetFloatingView(context: Context) : FrameLayout(context) {

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_UP && shouldDealBackKey()) {
            if (event.keyCode == KeyEvent.KEYCODE_BACK || event.keyCode == KeyEvent.KEYCODE_HOME) return onBackPressed()
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        ev?.let { if (ev.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_OUTSIDE) if (isMenuOpen()) doDoubleClickLogic() }
        return super.onTouchEvent(ev)
    }

    protected inner class SimpleTouchEventListener : TouchProxy.OnTouchEventListener {

        override fun onMove(x: Int, y: Int, dx: Int, dy: Int) {
            doTouchMoveLogin(x, y, dx, dy)
        }

        override fun onUp(x: Int, y: Int) {
            doTouchUp(x, y)
        }

        override fun onClick() {
            doClickLogic()
        }

        override fun onDoubleClick() {
            doDoubleClickLogic()
        }
    }

    protected fun getLP(): WindowManager.LayoutParams = layoutParams as WindowManager.LayoutParams
    protected open fun doTouchMoveLogin(mx: Int, my: Int, dx: Int, dy: Int) {}
    protected open fun doClickLogic() {}
    open fun doDoubleClickLogic() {}
    protected open fun doTouchUp(ux: Int, uy: Int) {}
    protected open fun isMenuOpen() = false
}