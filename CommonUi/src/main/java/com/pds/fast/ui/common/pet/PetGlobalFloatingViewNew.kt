package com.pds.fast.ui.common.pet

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.*
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.content.res.AppCompatResources
import com.pds.fast.assist.ScreenUtils
import com.pds.fast.ui.common.R
import com.pds.fast.ui.common.assist.dp2px
import com.pds.fast.ui.common.page.BaseAppCompatActivity

@SuppressLint("ClickableViewAccessibility", "ViewConstructor")
class PetGlobalFloatingViewNew(context: Context, val wmLayoutParams: WindowManager.LayoutParams) :
    PetBaseGlobalFloatingView(context) {

    private val screenW = getScreenWidth(context)
    private val screenH = getScreenHeight(context)
    private val moveDrawable = AppCompatResources.getDrawable(context, R.mipmap.icon_cat_move)
    private val catDrawable = AppCompatResources.getDrawable(context, R.mipmap.cat)
    private val catRightDrawable = AppCompatResources.getDrawable(context, R.mipmap.cat_right)
    private val catLeftDrawable = AppCompatResources.getDrawable(context, R.mipmap.icon_cat_left)
    private val catIconRightDrawable = AppCompatResources.getDrawable(context, R.mipmap.icon_cat_right)

    private var catPetStates = STATE_CAT_INIT
        set(value) {
            field = value
            when (value) {
                STATE_CAT_SLIDE_RIGHT -> {
                    isSlideLeft = false
                }
                STATE_CAT_SLIDE_LEFT -> {
                    isSlideLeft = true
                }
                STATE_CAT_OPEN_LEFT, STATE_CAT_OPEN_RIGHT, STATE_CAT_MOVE -> {
                    petTipsView.hideTips()
                }
            }
        }

    private var animator: ValueAnimator? = null

    private var touchProxy: TouchProxy
    private var petTipsView: PetTipsView

    private val menuCat = CatImageView(context).inject(this)

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        if (null != ev && ev.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_OUTSIDE && isOpen()) doDoubleClickLogic()
        return super.onTouchEvent(ev)
    }

    private val views: MutableList<PetItemBaseView> = mutableListOf()

    init {
        catPetStates = STATE_CAT_SLIDE_LEFT
        setBackgroundColor(Color.BLUE)
        views.add(PetSignInView(context).inject(this, menuCat, 0))
        views.add(PetDesktopPlayerView(context).inject(this, menuCat, 1))
        views.add(PetLaunchAppView(context).inject(this, menuCat, 2))
        views.add(PetCloseView(context).inject(this, menuCat, 3))
        petTipsView = PetTipsView(context).inject(this)
        touchProxy = TouchProxy(object : TouchProxy.OnTouchEventListener {

            override fun onMove(x: Int, y: Int, dx: Int, dy: Int) {
                if (x < MIN_MOVE && y < MIN_MOVE) return
                if (petTipsView.visibility == VISIBLE) return
                if (catPetStates == STATE_CAT_OPEN_LEFT || catPetStates == STATE_CAT_OPEN_RIGHT) return
                if (catPetStates != MIN_MOVE) menuCat.setImageDrawable(moveDrawable)
                wmLayoutParams.x += dx
                wmLayoutParams.y += dy
                catPetStates = STATE_CAT_MOVE
                // resetBorderline(wmLayoutParams)
                val windowManager = context.getSystemService(BaseAppCompatActivity.WINDOW_SERVICE) as WindowManager
                windowManager.updateViewLayout(this@PetGlobalFloatingViewNew, wmLayoutParams)
            }

            override fun onUp(x: Int, y: Int) {
                if (catPetStates == STATE_CAT_MOVE) moveSide(x, y)
            }

            override fun onDown(x: Int, y: Int) {}

            override fun onClick() {
                doClickLogic()
            }

            override fun onDoubleClick() {
                doDoubleClickLogic()
            }
        })

        menuCat.setOnTouchListener { v, event ->
            touchProxy.onTouchEvent(v, event)
        }
    }

    private fun doClickLogic() {
        if (isOpen()) {
            doDoubleClickLogic()
            return
        }
        val isSlideRight = catPetStates == STATE_CAT_SLIDE_RIGHT
        petTipsView.doPetTips(isSlideRight)
    }

    private fun doDoubleClickLogic() {
        if (catPetStates != STATE_CAT_SLIDE_LEFT && catPetStates != STATE_CAT_SLIDE_RIGHT
            && catPetStates != STATE_CAT_OPEN_LEFT && catPetStates != STATE_CAT_OPEN_RIGHT
        ) return

        if (animator?.isRunning == true) return
        val isOpen = isOpen()

        val iconDrawable = when (catPetStates) {
            STATE_CAT_SLIDE_LEFT -> {
                catPetStates = STATE_CAT_OPEN_LEFT
                catDrawable
            }
            STATE_CAT_SLIDE_RIGHT -> {
                catPetStates = STATE_CAT_OPEN_RIGHT
                catRightDrawable
            }
            STATE_CAT_OPEN_LEFT -> {
                catPetStates = STATE_CAT_SLIDE_LEFT
                catLeftDrawable
            }
            STATE_CAT_OPEN_RIGHT -> {
                catPetStates = STATE_CAT_SLIDE_RIGHT
                catIconRightDrawable
            }
            else -> null
        }
        if (null != iconDrawable) {
            val an = if (!isOpen) {
                radius = 0
                ValueAnimator.ofInt(0, PetItemBaseView.ringRadius)
            } else {
                radius = PetItemBaseView.ringRadius
                ValueAnimator.ofInt(PetItemBaseView.ringRadius, 0)
            }
            animator = an
            an.interpolator = DecelerateInterpolator()
            an.duration = 150
            an.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) {
                    menuCat.setImageDrawable(iconDrawable)
                    if (!isOpen) {
                        views.forEach { it.visibility = VISIBLE }
                    }
                }

                override fun onAnimationEnd(animation: Animator?) {
                    if (isOpen) {
                        views.forEach { it.visibility = GONE }
                    }
                    menuCat.setImageDrawable(iconDrawable)
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationRepeat(animation: Animator?) {
                }

            })
            an.addUpdateListener { vl: ValueAnimator ->
                radius = vl.animatedValue as Int
                Log.e("111", " radius = $radius")
                this@PetGlobalFloatingViewNew.requestLayout()
            }
            an.start()
        }
    }

    private fun isOpen() = catPetStates == STATE_CAT_OPEN_RIGHT || catPetStates == STATE_CAT_OPEN_LEFT

    /**
     * 获取屏幕长边的长度 不包含statusBar
     *
     * @return
     */
    private val screenLongSideLength: Int
        get() = if (ScreenUtils.isPortrait(context)) {
            ScreenUtils.getAppScreenHeight(context)
        } else {
            ScreenUtils.getAppScreenWidth(context)
        }

    /**
     * 获取屏幕短边的长度 不包含statusBar
     *
     * @return
     */
    private val screenShortSideLength: Int
        get() = if (ScreenUtils.isPortrait(context)) {
            ScreenUtils.getAppScreenWidth(context)
        } else {
            ScreenUtils.getAppScreenHeight(context)
        }

    /**
     * 限制边界 调用的时候必须保证是在控件能获取到宽高德前提下
     */
    private fun resetBorderline(windowLayoutParams: WindowManager.LayoutParams?) {
        //如果是系统模式或者手动关闭动态限制边界
        if (!restrictBorderline()) {
            return
        }

        val mMonitorViewHeight = width
        val mMonitorViewWidth = height

        if (windowLayoutParams != null) {
            if (ScreenUtils.isPortrait(context)) {
                if (windowLayoutParams.y >= screenLongSideLength - mMonitorViewHeight) {
                    windowLayoutParams.y = screenLongSideLength - mMonitorViewHeight
                }
            } else {
                if (windowLayoutParams.y >= screenShortSideLength - mMonitorViewHeight) {
                    windowLayoutParams.y = screenShortSideLength - mMonitorViewHeight
                }
            }

            if (ScreenUtils.isPortrait(context)) {
                if (windowLayoutParams.x >= screenShortSideLength - mMonitorViewWidth) {
                    windowLayoutParams.x = screenShortSideLength - mMonitorViewWidth
                }
            } else {
                if (windowLayoutParams.x >= screenLongSideLength - mMonitorViewWidth) {
                    windowLayoutParams.x = screenLongSideLength - mMonitorViewWidth
                }
            }

            if (windowLayoutParams.y <= 0) windowLayoutParams.y = 0
            if (windowLayoutParams.x <= 0) windowLayoutParams.x = 0
        }
    }

    private val al = object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator?) {
        }

        override fun onAnimationEnd(animation: Animator?) {
            val iconDrawable = if (wmLayoutParams.x > ScreenUtils.getAppScreenWidth(context) / 2) {
                catPetStates = STATE_CAT_SLIDE_RIGHT
                catIconRightDrawable
            } else {
                catPetStates = STATE_CAT_SLIDE_LEFT
                catLeftDrawable
            }
            menuCat.setImageDrawable(iconDrawable)
        }

        override fun onAnimationCancel(animation: Animator?) {

        }

        override fun onAnimationRepeat(animation: Animator?) {
        }

    }

    private fun moveSide(rawX: Int, rawY: Int) {

        val tmpX = wmLayoutParams.x.toFloat()
        val tmpY = wmLayoutParams.y.toFloat()

        // 53f.dp2px() 左边附贴时图片的宽度
        val xBy: Float = if (rawX >= screenW / 2) screenW - 53f.dp2px() - tmpX else -tmpX
        var yBy = 0f
        if (rawY >= screenH - LIMIT || rawY <= LIMIT) yBy = if (rawY <= LIMIT) -tmpY + LIMIT else screenH - LIMIT - height - tmpY

        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 300
        animator.interpolator = DecelerateInterpolator()
        animator.addListener(al)

        animator.addUpdateListener { animation: ValueAnimator ->

            wmLayoutParams.x = (tmpX + xBy * animation.animatedValue as Float).toInt()
            wmLayoutParams.y = (tmpY + yBy * animation.animatedValue as Float).toInt()

            val windowManager = context.getSystemService(BaseAppCompatActivity.WINDOW_SERVICE) as WindowManager
            windowManager.updateViewLayout(this@PetGlobalFloatingViewNew, wmLayoutParams)
        }
        animator.start()
    }

    /**
     * 是否限制布局边界
     *
     * @return
     */
    private fun restrictBorderline(): Boolean {
        return true
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_UP && shouldDealBackKey()) {
            //监听返回键
            if (event.keyCode == KeyEvent.KEYCODE_BACK || event.keyCode == KeyEvent.KEYCODE_HOME) {
                return onBackPressed()
            }
        }
        return super.dispatchKeyEvent(event)
    }

    private fun onBackPressed(): Boolean {
        return false
    }

    companion object {
        @JvmStatic
        private val LIMIT = 100f.dp2px()
        private val MIN_MOVE = 3f.dp2px()

        private const val STATE_CAT_INIT = 0
        private const val STATE_CAT_SLIDE_LEFT = 1
        private const val STATE_CAT_SLIDE_RIGHT = 2
        private const val STATE_CAT_MOVE = 3
        private const val STATE_CAT_OPEN_LEFT = 4
        private const val STATE_CAT_OPEN_RIGHT = 5

        @JvmStatic
        fun getScreenWidth(var0: Context): Int {
            return var0.resources.displayMetrics.widthPixels
        }

        @JvmStatic
        fun getScreenHeight(var0: Context): Int {
            return var0.resources.displayMetrics.heightPixels
        }

        @JvmStatic
        // 默认不自己处理返回按键
        fun shouldDealBackKey(): Boolean {
            return false
        }
    }

    fun doMorningTipsLogic() {
        doPetTips("主人早上好\n今天又是元气满满的一天！")
    }

    private fun doPetTips(tip: String?) {
        petTipsView.text = tip
        petTipsView.doPetTips(catPetStates == STATE_CAT_SLIDE_RIGHT)
    }
}