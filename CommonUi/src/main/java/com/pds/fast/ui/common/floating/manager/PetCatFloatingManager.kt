package com.pds.fast.ui.common.floating.manager

import android.app.Activity
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.WindowManager
import android.view.inspector.WindowInspector
import com.kuaiyin.player.v2.ui.pet.GlobalPetFloatingView
import com.pds.fast.ui.common.floating.MainCatImageView
import com.pds.fast.ui.common.floating.menu.PetItemBaseView
import com.pds.fast.assist.Assist
import com.pds.fast.assist.utils.Handlers
import com.pds.fast.assist.utils.dp2px
import com.pds.fast.ui.common.floating.data.MinePetModel

object PetCatFloatingManager {

    private var minePetModel: MinePetModel? = null
    private var petView: GlobalPetFloatingView? = null
    private val windowManager: WindowManager = Assist.application.getSystemService(Activity.WINDOW_SERVICE) as WindowManager
    private var wmParams: WindowManager.LayoutParams = buildParams()

    fun getWmParams(): WindowManager.LayoutParams {
        val p = petView?.layoutParams
        if (p is WindowManager.LayoutParams) wmParams = p
        return wmParams
    }

    fun getPetView() = petView

    fun doCatPet(isOpen: Boolean): Boolean {
        if (isOpen) {
            if (!canDrawOverlays()) return false
            if (null == minePetModel && !isRequestData) {
                Handlers.MAIN_HANDLER.removeCallbacks(requestDataRunnable)
                lastRequestTimestamp = System.currentTimeMillis()
                startRequestData(lastRequestTimestamp)
            } else doCatPetLogic(minePetModel)
            return true
        } else {
            return removePetFromWindow()
        }
    }

    fun removePetFromWindow(): Boolean {
        if (null == petView) return false
        try {
            windowManager.removeViewImmediate(petView)
            val p = petView?.layoutParams
            if (p is WindowManager.LayoutParams) wmParams = p
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    private fun doCatPetLogic(minePetModel: MinePetModel?) {
        if (!canDrawOverlays()) return
        PetCatFloatingManager.minePetModel = minePetModel
        addPetToWindow()
    }

    private fun addPetToWindow() {
        val model = minePetModel ?: return
        petView?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) if (WindowInspector.getGlobalWindowViews().contains(it)) return
            else if (it.isAttachedToWindow || null != it.parent) return
            val p = it.layoutParams
            if (p is WindowManager.LayoutParams) wmParams = p
        }
        if (null == petView) petView = GlobalPetFloatingView(Assist.application, model)
        try {
            windowManager.addView(petView, wmParams)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun buildParams() = WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT).apply {
        type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else WindowManager.LayoutParams.TYPE_PHONE
        format = PixelFormat.TRANSPARENT
        gravity = Gravity.LEFT or Gravity.TOP
        flags = if (shouldDealBackKey()) {
            //自己处理返回按键
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        } else {
            //参考：http://www.shirlman.com/tec/20160426/362
            //设置WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE会导致RootView监听不到返回按键的监听失效 系统处理返回按键
            // WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE 设置后窗口外的事件将不会接受到
            // WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS: 当在右边时候，会展示在屏幕外
            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        }
        width = WindowManager.LayoutParams.WRAP_CONTENT
        height = WindowManager.LayoutParams.WRAP_CONTENT
        x = -MainCatImageView.CAT_SLIDE_CAP
        y = SCREEN_H / 2 - 80f.dp2px()
    }

    fun updatePetWindow(wmParams: WindowManager.LayoutParams) {
        var cy = wmParams.y
        val minY = PetItemBaseView.ringRadius / 2
        // 112为猫咪的高度
        val maxY = SCREEN_H - 112f.dp2px() - PetItemBaseView.ringRadius / 2
        if (cy < minY) cy = minY else if (cy > maxY) cy = maxY
        wmParams.y = cy
        windowManager.updateViewLayout(petView, wmParams)
    }

    private var isRequestData = false
    private var lastRequestTimestamp = 0L

    fun requestData(delay: Long) {
        if (isRequestData && delay < 10) return
        Handlers.MAIN_HANDLER.removeCallbacks(requestDataRunnable)
        lastRequestTimestamp = System.currentTimeMillis()
        Handlers.MAIN_HANDLER.postDelayed(requestDataRunnable, delay)
    }

    private var requestDataRunnable = Runnable {
        startRequestData(lastRequestTimestamp)
    }

    private fun startRequestData(timestamp: Long) {
        doCatPetLogic(null)
    }
}