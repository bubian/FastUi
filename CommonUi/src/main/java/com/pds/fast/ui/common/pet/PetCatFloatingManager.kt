package com.pds.fast.ui.common.pet

import android.app.Activity
import android.graphics.PixelFormat
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager
import com.pds.fast.assist.PermissionUtils
import com.pds.fast.assist.ScreenUtils
import com.pds.fast.ui.common.page.BaseAppCompatActivity

object PetCatFloatingManager {
    fun doCatPetLogic(context: Activity, code: Int) {
        val canDrawOverlays: Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else true

        if (!canDrawOverlays) {
            PermissionUtils.requestSettingCanDrawOverlays(context, code)
            return
        }
        showPwtView(context)
    }

    private var tipsInteractiveHelper: TipsInteractiveHelper = TipsInteractiveHelper()

    fun showPwtView(context: Activity) {
        val wmParams = WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT).apply {
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else WindowManager.LayoutParams.TYPE_PHONE
            format = PixelFormat.TRANSPARENT
            gravity = Gravity.LEFT or Gravity.TOP
            flags = if (PetGlobalFloatingViewNew.shouldDealBackKey()) {
                //自己处理返回按键
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            } else {
                //参考：http://www.shirlman.com/tec/20160426/362
                //设置WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE会导致RootView监听不到返回按键的监听失效 系统处理返回按键
                //   WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE 设置后窗口外的事件将不会接受到
                // WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS: 当在右边时候，会展示在屏幕外
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            }
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            y = ScreenUtils.getAppScreenHeight(context) / 2
        }
        val petView = PetGlobalFloatingViewNew(context, wmParams)
        tipsInteractiveHelper.register(context.application,petView)
        val windowManager = context.getSystemService(BaseAppCompatActivity.WINDOW_SERVICE) as WindowManager
        petView.isFocusableInTouchMode = true
        windowManager.addView(petView, wmParams)
    }
}