package com.pds.fast.example

import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager
import androidx.annotation.RequiresApi
import com.pds.fast.assist.PermissionUtils
import com.pds.fast.assist.ScreenUtils
import com.pds.fast.ui.common.assist.dp22px
import com.pds.fast.ui.common.assist.dp2px
import com.pds.fast.ui.common.page.BaseAppCompatActivity
import com.pds.fast.ui.common.pet.PetGlobalFloatingView


class PetActivity : BaseAppCompatActivity() {

    companion object {
        const val CODE = 1000
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!Settings.canDrawOverlays(this)) {
            PermissionUtils.requestSettingCanDrawOverlays(this, CODE)
        } else {
            showPwtView()
        }
    }

    private fun showPwtView() {
        val wmParams = WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT).apply {
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else WindowManager.LayoutParams.TYPE_PHONE
            format = PixelFormat.TRANSPARENT
            gravity = Gravity.LEFT or Gravity.TOP
            flags = if (PetGlobalFloatingView.shouldDealBackKey()) {
                //自己处理返回按键
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            } else {
                //参考：http://www.shirlman.com/tec/20160426/362
                //设置WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE会导致RootView监听不到返回按键的监听失效 系统处理返回按键
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            }
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            y = ScreenUtils.getAppScreenHeight(this@PetActivity) / 2
        }
        val petView = PetGlobalFloatingView(this, wmParams)
        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        petView.isFocusableInTouchMode = true
        windowManager.addView(petView, wmParams)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CODE && resultCode == RESULT_OK) {
            showPwtView()
        }
    }
}