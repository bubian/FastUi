package com.pds.fast.ui.common.floating.menu

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import com.kuaiyin.player.v2.ui.pet.GlobalPetFloatingView
import com.pds.fast.ui.common.floating.MainCatImageView
import com.kuaiyin.player.v2.ui.pet.manager.PetWindowHelper.Companion.addViewToWindow
import com.kuaiyin.player.v2.ui.pet.manager.PetWindowHelper.Companion.removeViewToWindowImmediate
import com.pds.fast.ui.common.floating.manager.SCREEN_W
import com.pds.fast.ui.common.floating.manager.shouldDealBackKey
import com.pds.fast.ui.common.floating.other.SimpleAnimatorListener
import com.pds.fast.assist.utils.dp2px
import com.pds.fast.ui.common.floating.data.MinePetModel
import com.pds.fast.ui.common.floating.other.TouchProxy
import kotlin.math.cos
import kotlin.math.sin

class PetMenuView(
    context: Context, private val menuCat: MainCatImageView, val gpfView: GlobalPetFloatingView, minePetModel: MinePetModel
) : ViewGroup(context) {
    private val views: MutableList<PetItemBaseView> = mutableListOf()
    private var petDesktopPlayerView: PetDesktopPlayerView
    private var animator: ValueAnimator? = null
    private var isAddToWindow = false
    private var progress: Float = 0f
    private var touchProxy: TouchProxy

    init {
        isFocusableInTouchMode = true
        touchProxy = TouchProxy(SimpleTouchEventListener())
        views.add(PetSignInView(context).inject(this, gpfView, 0))
        petDesktopPlayerView = PetDesktopPlayerView(context).inject(this, gpfView, 1) as PetDesktopPlayerView
        views.add(petDesktopPlayerView)
        views.add(PetLaunchAppView(context).inject(this, gpfView, 2))
        views.add(PetCloseView(context, minePetModel).inject(this, gpfView, 3))
        setOnTouchListener { v, event ->
            touchProxy.onTouchEvent(v, event)
            true
        }
    }

    fun doDoubleClickLogic(isOpen: Boolean) {
        petDesktopPlayerView.check()
        doMenuAnimator(isOpen)
    }

    private var isMenuOen: Boolean = false
    private fun doMenuAnimator(isOpen: Boolean) {
        isMenuOen = isOpen
        // val isRight = menuCat.isMenuOpenRight() || menuCat.isSlideRight()
        (if (isOpen) ValueAnimator.ofFloat(1f, 0f) else ValueAnimator.ofFloat(0f, 1f)).apply {
            animator = this
            duration = 150
            addUpdateListener { vl: ValueAnimator ->
                // 方案1 - 动画duration = 250 测得
                // onAnimationStart=1659022835402
                // onAnimationEnd  =1659022835665
                // total =  263
                progress = vl.animatedValue as Float
                this@PetMenuView.requestLayout()
                // 方案2 - 动画duration = 250 测得
                // onAnimationStart=1659019900334
                // onAnimationEnd  =1659019900722
                // total =  388
                // views.forEachIndexed { index, ch -> ch.open(index, vl.animatedValue as Float, isRight, isOpen) }

                // 总结：方案1比方案2 快 -> 125ms
            }
            addListener(object : SimpleAnimatorListener() {
                override fun onAnimationStart(animation: Animator?) {
                    Log.d(TAG, "onAnimationStart=${System.currentTimeMillis()}")
                    if (!isOpen) addTopsToWindow()
                }

                override fun onAnimationEnd(animation: Animator?) {
                    Log.d(TAG, "onAnimationEnd=${System.currentTimeMillis()}")
                    if (isOpen) removeTopsFromWindow()
                }

                override fun onAnimationCancel(animation: Animator?) {
                    if (isOpen) removeTopsFromWindow()
                }
            })
            start()
        }
    }

    fun removeTopsFromWindow() {
        if (isAddToWindow) {
            isAddToWindow = removeViewToWindowImmediate(this)
        }
    }

    private fun addTopsToWindow() {
        if (isAddToWindow) return
        val params = gpfView.layoutParams
        val gpfW = gpfView.width
        val gpfH = gpfView.height

        if (params is WindowManager.LayoutParams) {
            val menuY = (gpfH - MENU_SIZE_H) / 2 + params.y
            val menuX =
                if (menuCat.isMenuOpenRight() || menuCat.isSlideRight()) SCREEN_W - MENU_SIZE_W - (91f.dp2px() - PetItemBaseView.VIEW_SIZE) / 2 else (gpfW - MENU_SIZE_W) / 2
            isAddToWindow = addViewToWindow(menuX, menuY, this@PetMenuView, buildMenuWmParams().apply {
                width = MENU_SIZE_W
                height = MENU_SIZE_H
            })
        }
    }

    fun isRunningOfAnimator() = animator?.isRunning == true

    companion object {
        val MENU_SIZE_H = 211f.dp2px()
        val MENU_SIZE_W = 151f.dp2px()
        private val M_PI = Math.PI / 180.toDouble()

        private const val TAG = "PetMenuView"
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        for (index in 0 until childCount) measureChild(getChildAt(index), widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(MENU_SIZE_W, MENU_SIZE_H)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (childCount < 1) return
        val isRight = menuCat.isMenuOpenRight() || menuCat.isSlideRight()
        for (index in 0 until childCount) {
            var catCenterX = if (isRight) MENU_SIZE_W - PetItemBaseView.VIEW_SIZE else PetItemBaseView.VIEW_SIZE / 2
            val catCenterY = (MENU_SIZE_H - PetItemBaseView.VIEW_SIZE) / 2

            val angle = 40 * index - 80
            var rx = cos(Math.PI / 180.toDouble() * angle) * PetItemBaseView.ringRadius * progress
            val rY = sin(Math.PI / 180.toDouble() * angle) * PetItemBaseView.ringRadius * progress

            if (isMenuOen) rx = if (isRight) rx + PetItemBaseView.DP_25 * progress else rx - PetItemBaseView.DP_25 * progress
            if (isMenuOen) catCenterX = if (isRight) catCenterX + PetItemBaseView.DP_25 else catCenterX - PetItemBaseView.DP_25

            getChildAt(index).apply {
                val left = if (isRight) catCenterX - rx else catCenterX + rx
                val top = catCenterY + rY
                layout(left.toInt(), top.toInt(), (left + measuredWidth).toInt(), (top + measuredHeight).toInt())
            }
        }
    }

    private inner class SimpleTouchEventListener : TouchProxy.OnTouchEventListener {

        override fun onUp(x: Int, y: Int) {
            gpfView.doDoubleClickLogic()
        }
    }

    private fun buildMenuWmParams(gy: Int = Gravity.LEFT or Gravity.TOP) =
        WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT).apply {
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else WindowManager.LayoutParams.TYPE_PHONE
            format = PixelFormat.TRANSPARENT
            gravity = gy
            flags = if (shouldDealBackKey()) {
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            } else {
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            }
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
        }

    fun detachedFromWindow() {
        removeTopsFromWindow()
    }
}