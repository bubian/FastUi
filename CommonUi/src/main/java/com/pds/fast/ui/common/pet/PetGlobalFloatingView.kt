package com.pds.fast.ui.common.pet

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import com.pds.fast.assist.ScreenUtils
import com.pds.fast.ui.common.R
import com.pds.fast.ui.common.assist.dp2px
import com.pds.fast.ui.common.page.BaseAppCompatActivity

@SuppressLint("ClickableViewAccessibility")
class PetGlobalFloatingView(context: Context, val wmLayoutParams: WindowManager.LayoutParams) :
    FrameLayout(context) {

    private val screenW = getScreenWidth(context)
    private val screenH = getScreenHeight(context)

    private var catPetStates = STATE_CAT_INIT

    private val menuCat = ImageView(context).apply {
        layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply { gravity = Gravity.CENTER_VERTICAL }
        scaleType = ImageView.ScaleType.CENTER
        setImageResource(R.mipmap.icon_cat_left)
        addView(this)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }

    private val views: MutableList<PetItemBaseView> = mutableListOf()

    init {
        catPetStates = STATE_CAT_SLIDE_LEFT
        views.add(PetSignInView(context).inject(this, menuCat, 0))
        views.add(PetDesktopPlayerView(context).inject(this, menuCat, 1))
        views.add(PetLaunchAppView(context).inject(this, menuCat, 2))
        views.add(PetCloseView(context).inject(this, menuCat, 3))

        val mTouchProxy = TouchProxy(object : TouchProxy.OnTouchEventListener {
            override fun onMove(x: Int, y: Int, dx: Int, dy: Int) {
                if (x < MIN_MOVE && y < MIN_MOVE) return
                wmLayoutParams.x += dx
                wmLayoutParams.y += dy
                if (catPetStates != MIN_MOVE) menuCat.setImageResource(R.mipmap.icon_cat_move)
                catPetStates = STATE_CAT_MOVE
                resetBorderline(wmLayoutParams)
                val windowManager = context.getSystemService(BaseAppCompatActivity.WINDOW_SERVICE) as WindowManager
                windowManager.updateViewLayout(this@PetGlobalFloatingView, wmLayoutParams)
            }

            override fun onUp(x: Int, y: Int) {
                if (catPetStates == STATE_CAT_MOVE) moveSide(x, y)
            }

            override fun onDown(x: Int, y: Int) {

            }

        })
        setOnTouchListener { v, event -> mTouchProxy.onTouchEvent(v, event) }

        setOnClickListener { catView ->
            if (catPetStates != STATE_CAT_SLIDE_LEFT && catPetStates != STATE_CAT_SLIDE_RIGHT) return@setOnClickListener

            val isLeft = catPetStates == STATE_CAT_SLIDE_LEFT
            val catResId = if (isLeft) R.mipmap.cat else R.mipmap.cat_right
            menuCat.setImageResource(catResId)
            views.forEach { it.open(catView) }
        }
    }

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
            val catResId = if (wmLayoutParams.x > ScreenUtils.getAppScreenWidth(context) / 2) {
                catPetStates = STATE_CAT_SLIDE_RIGHT
                R.mipmap.icon_cat_right
            } else {
                catPetStates = STATE_CAT_SLIDE_LEFT
                R.mipmap.icon_cat_left
            }
            menuCat.setImageResource(catResId)
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
            windowManager.updateViewLayout(this@PetGlobalFloatingView, wmLayoutParams)
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

        const val angle = 40

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
}