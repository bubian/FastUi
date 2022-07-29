package com.kuaiyin.player.v2.ui.pet.manager

import android.app.Activity
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.inspector.WindowInspector
import com.pds.fast.assist.Assist
import com.pds.fast.ui.common.floating.manager.canDrawOverlays
import com.pds.fast.ui.common.floating.manager.shouldDealBackKey

class PetWindowHelper {
    companion object {
        @JvmStatic
        fun addViewToWindow(x: Int, y: Int, view: View, params: WindowManager.LayoutParams = buildWmParams(Gravity.LEFT or Gravity.TOP)): Boolean {
            if (!canDrawOverlays()) {
                return false
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) if (WindowInspector.getGlobalWindowViews().contains(view)) return true
            else if (view.isAttachedToWindow || null != view.parent) return true

            try {
                val wm = Assist.application.getSystemService(Activity.WINDOW_SERVICE) as WindowManager
                params.x = x
                params.y = y
                wm.addView(view, params)
                return true
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return isContains(view)
        }

        @JvmStatic
        fun removeViewToWindowImmediate(view: View): Boolean {
            try {
                val wm =Assist.application.getSystemService(Activity.WINDOW_SERVICE) as WindowManager
                wm.removeViewImmediate(view)
                return false
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return !isContains(view)
        }

        fun isContains(view: View) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) WindowInspector.getGlobalWindowViews().contains(view)
        else (view.isAttachedToWindow || null != view.parent)

        @JvmStatic
        fun removeViewToWindow(view: View): Boolean {
            try {
                val wm = Assist.application.getSystemService(Activity.WINDOW_SERVICE) as WindowManager
                wm.removeView(view)
                return true
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return !isContains(view)
        }

        @JvmStatic
        fun buildWmParams(gy: Int = Gravity.LEFT or Gravity.TOP) =
            WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT).apply {
                type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                else WindowManager.LayoutParams.TYPE_PHONE
                format = PixelFormat.TRANSPARENT
                gravity = gy
                flags = if (shouldDealBackKey()) {
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                } else {
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                }
                width = WindowManager.LayoutParams.WRAP_CONTENT
                height = WindowManager.LayoutParams.WRAP_CONTENT
            }
    }
}