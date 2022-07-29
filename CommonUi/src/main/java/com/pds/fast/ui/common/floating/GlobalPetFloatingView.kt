package com.kuaiyin.player.v2.ui.pet

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.view.animation.DecelerateInterpolator
import com.pds.fast.assist.Assist
import com.pds.fast.ui.common.floating.manager.PetCatFloatingManager.updatePetWindow
import com.pds.fast.ui.common.floating.manager.SCREEN_W
import com.pds.fast.ui.common.floating.manager.TipsInteractiveHelper
import com.pds.fast.ui.common.floating.manager.isMoreThanHalf
import com.pds.fast.ui.common.floating.GlobalBasePetFloatingView
import com.pds.fast.ui.common.floating.other.SimpleAnimatorListener
import com.pds.fast.ui.common.floating.MainCatImageView
import com.pds.fast.ui.common.floating.data.MinePetModel
import com.pds.fast.ui.common.floating.menu.PetMenuView
import com.pds.fast.ui.common.floating.menu.PetTipsView
import com.pds.fast.ui.common.floating.other.TouchProxy

class GlobalPetFloatingView(context: Context, minePetModel: MinePetModel) :
    GlobalBasePetFloatingView(context) {

    private var touchProxy: TouchProxy
    private var petTipsView: PetTipsView

    private val menuCat = MainCatImageView(context, this).inject()

    private var tipsInteractiveHelper: TipsInteractiveHelper
    private var isExecuteSlideAnimator = false
    private var petMenuView = PetMenuView(context, menuCat, this, minePetModel)

    init {
        isFocusableInTouchMode = true
        petTipsView = PetTipsView(context).inject(this)
        tipsInteractiveHelper = TipsInteractiveHelper(petTipsView, menuCat, minePetModel)
        touchProxy = TouchProxy(SimpleTouchEventListener())
        menuCat.setOnTouchListener { v, event ->
            if (!isExecuteSlideAnimator && !tipsInteractiveHelper.isInAnimation()) touchProxy.onTouchEvent(v, event)
            true
        }
    }

    fun doMove(x: Int, y: Int) {
        menuCat.setSlide(!isMoreThanHalf(x))
        tipsInteractiveHelper.setCatPetStates()
        doOnlyMove(x, y)
    }

    private fun doOnlyMove(mx: Int, my: Int) {
        getLP().apply {
            x = mx
            y = my
            updatePetWindow(this)
        }
    }

    private val slideAnimator = object : SimpleAnimatorListener() {
        override fun onAnimationEnd(animation: Animator?) {
            menuCat.setSlide(!isMoreThanHalf(getLP().x))
            tipsInteractiveHelper.setCatPetStates()
        }
    }

    private fun moveSide(rawX: Int, rawY: Int) {
        val lp = getLP()
        val tmpX = lp.x.toFloat()
        // val tmpY = lp.y.toFloat()
        val xBy: Float = if (isMoreThanHalf(rawX)) SCREEN_W - MainCatImageView.CAT_SLIDE_W - tmpX else -tmpX
        // var yBy = 0f
        // if (rawY >= SCREEN_W) yBy = if (rawY <= LIMIT) -tmpY + LIMIT else SCREEN_W - LIMIT - height - tmpY
        val wh = menuCat.height / 2
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 300
        animator.interpolator = DecelerateInterpolator()
        animator.addListener(slideAnimator)

        animator.addUpdateListener { animation: ValueAnimator ->
            lp.x = (tmpX + xBy * animation.animatedValue as Float).toInt()
            // lp.y = (tmpY + yBy * animation.animatedValue as Float).toInt()
            lp.y = rawY - wh
            updatePetWindow(lp)
        }
        animator.addListener(object : SimpleAnimatorListener() {
            override fun onAnimationEnd(animation: Animator?) {
                isExecuteSlideAnimator = false
            }

            override fun onAnimationCancel(animation: Animator?) {
                isExecuteSlideAnimator = false
            }
        })
        isExecuteSlideAnimator = true
        animator.start()
    }

    override fun doTouchMoveLogin(mx: Int, my: Int, dx: Int, dy: Int) {
        if (menuCat.isMenuOpen()) return
        menuCat.setMove(mx)
        getLP().apply {
            x += dx
            y += dy
            updatePetWindow(this)
        }
        tipsInteractiveHelper.setCatPetStates()
    }

    override fun doClickLogic() {
        if (menuCat.isMenuOpen()) {
            doDoubleClickLogic()
            return
        }
        tipsInteractiveHelper.doClickTipsLogic()
    }

    override fun doDoubleClickLogic() {
        if (Assist.isRunningForeground(Assist.application)) return
        val isOpen = menuCat.isMenuOpen()
        if (!isOpen && !menuCat.isSlide() || petMenuView.isRunningOfAnimator()) return

        when {
            menuCat.isSlideLeft() -> menuCat.setMenuOpen(true)
            menuCat.isSlideRight() -> menuCat.setMenuOpen(false)
            menuCat.isMenuOpenLeft() -> menuCat.setSlide(true)
            menuCat.isMenuOpenRight() -> menuCat.setSlide(false)
        }
        tipsInteractiveHelper.setCatPetStates()
        if (isOpen) tipsInteractiveHelper.doMenuClose()
        petMenuView.doDoubleClickLogic(isOpen)
    }

    override fun doTouchUp(ux: Int, uy: Int) {
        if (menuCat.isMove()) moveSide(ux, uy)
    }

    override fun isMenuOpen() = menuCat.isMenuOpen()

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        tipsInteractiveHelper.destroy()
        petMenuView.removeTopsFromWindow()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        tipsInteractiveHelper.register()
    }

    companion object {
        private const val TAG = "GlobalPetFloatingView"
    }
}