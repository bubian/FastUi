package com.pds.fast.ui.common.page

import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

class WrapKeyboardActivity : BaseActivity() {
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (isShouldHideKeyboard(v, ev)) {
                val imm = getSystemService(INPUT_METHOD_SERVICE)
                if (imm is InputMethodManager) {
                    imm.hideSoftInputFromWindow(v?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    // 根据 EditText 所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘
    private fun isShouldHideKeyboard(v: View?, event: MotionEvent): Boolean {
        if (v is EditText) {
            val l = intArrayOf(0, 0)
            v.getLocationInWindow(l)
            val left = l[0]
            val top = l[1]
            val bottom = top + v.getHeight()
            val right = left + v.getWidth()
            return !(event.x > left && event.x < right && event.y > top && event.y < bottom)
        }
        return false
    }
}