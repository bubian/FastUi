package com.pds.fast.ui.common.behavior

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.widget.OverScroller
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout
import com.pds.fast.ui.common.assist.dp2px
import java.lang.reflect.Field
import kotlin.math.pow

class ZoomAppBarBehavior(context: Context?, attrs: AttributeSet?) : AppBarLayout.Behavior(context, attrs) {

    private var appBarLayout: AppBarLayout? = null
    private var resizeView: View? = null

    private var appBarHeight = 0
    private var resizeViewHeight = 0
    private var totalDy = 0f
    private var animating = false
    private var downY = 0f
    private var scalingSelf = false
    private var recorderAnimator: ValueAnimator? = null
    private var onPullRefreshListener: OnPullRefreshListener? = null

    override fun onLayoutChild(parent: CoordinatorLayout, child: AppBarLayout, layoutDirection: Int): Boolean {
        val handled = super.onLayoutChild(parent, child, layoutDirection)
        if (resizeView == null) {
            child.clipChildren = false
            appBarLayout = child
            // 换成自己需要缩放的view
            // resizeView = parent.findViewById(R.id.ivHeader)
        }
        initial(child)
        return handled
    }

    private var onGestureListener: SimpleOnGestureListener = object : SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            if (null == e2) return false
            val y = e2.y
            when (e2.action) {
                MotionEvent.ACTION_MOVE -> {
                    scalingSelf = true
                    totalDy = y - downY
                    totalDy = totalDy.coerceAtMost(TARGET_HEIGHT.dp2px().toFloat())
                    totalDy = 0f.coerceAtLeast(totalDy)
                    refreshHeight(appBarLayout)
                    return true
                }
            }
            return false
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            resizeView?.let {
                if (velocityY < -1000 && it.height > resizeViewHeight) {
                    stopAppBarLayoutFling(appBarLayout)
                }
            }
            return false
        }
    }
    private var gestureDetector: GestureDetector? = null
    override fun onTouchEvent(parent: CoordinatorLayout, child: AppBarLayout, ev: MotionEvent): Boolean {
        if (gestureDetector == null) {
            gestureDetector = GestureDetector(parent.context, onGestureListener)
        }
        val result = super.onTouchEvent(parent, child, ev)
        if (ev.action == MotionEvent.ACTION_UP) {
            if (scalingSelf) {
                recovery(appBarLayout)
            }
            scalingSelf = false
        }
        return gestureDetector?.onTouchEvent(ev) ?: false
    }

    override fun onInterceptTouchEvent(parent: CoordinatorLayout, child: AppBarLayout, ev: MotionEvent): Boolean {
        val result = super.onInterceptTouchEvent(parent, child, ev)
        if (ev.action == MotionEvent.ACTION_DOWN) {
            downY = ev.y
        }
        return result
    }

    private fun stopAppBarLayoutFling(appBarLayout: AppBarLayout?) {
        //通过反射拿到HeaderBehavior中的flingRunnable变量
        try {
            var flingRunnableField: Field? = null
            var scrollerField: Field? = null
            try {
                this.javaClass.superclass?.superclass?.let {
                    flingRunnableField = it.getDeclaredField("mFlingRunnable")
                    scrollerField = it.getDeclaredField("mScroller")
                }
            } catch (e: NoSuchFieldException) {
                this.javaClass.superclass?.superclass?.superclass?.let {
                    flingRunnableField = it.getDeclaredField("flingRunnable")
                    scrollerField = it.getDeclaredField("scroller")
                }

            }
            flingRunnableField?.isAccessible = true
            scrollerField?.isAccessible = true

            val flingRunnable = flingRunnableField?.get(this)
            if (flingRunnable is Runnable) {
                appBarLayout?.removeCallbacks(flingRunnable)
                flingRunnableField?.set(this, null)
            }

            val overScroller = scrollerField?.get(this)
            if (overScroller is OverScroller && overScroller.isFinished) {
                overScroller.abortAnimation()
            }
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }

    override fun onStartNestedScroll(
        parent: CoordinatorLayout,
        child: AppBarLayout,
        directTargetChild: View,
        target: View,
        nestedScrollAxes: Int,
        type: Int
    ): Boolean {
        animating = true
        return super.onStartNestedScroll(parent, child, directTargetChild, target, nestedScrollAxes, type)
    }

    override fun onNestedPreFling(
        coordinatorLayout: CoordinatorLayout,
        child: AppBarLayout,
        target: View,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        if (velocityY > 100) {
            animating = false
        }
        return super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY)
    }

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: AppBarLayout,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        if (resizeView != null && (dy < 0 && child.bottom >= appBarHeight || dy > 0 && child.bottom > appBarHeight)) {
            scale(child, dy)
        } else {
            super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
        }
    }

    override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, abl: AppBarLayout, target: View, type: Int) {
        recovery(abl)
        super.onStopNestedScroll(coordinatorLayout, abl, target, type)
    }

    private fun initial(abl: AppBarLayout) {
        if (animating || scalingSelf || recorderAnimator != null && recorderAnimator?.isRunning != false) return

        appBarHeight = abl.height
        resizeViewHeight = resizeView?.height ?: 0
        abl.tag = appBarHeight
    }

    private fun scale(abl: AppBarLayout?, dy: Int) {
        totalDy += -dy.toFloat()
        totalDy = totalDy.coerceAtMost(TARGET_HEIGHT.dp2px().toFloat())
        totalDy = 0f.coerceAtLeast(totalDy)
        refreshHeight(abl)
    }

    private fun refreshHeight(abl: AppBarLayout?) {
        val adjustDy = adjustDy(totalDy)
        resizeView?.let {
            it.layoutParams.height = (resizeViewHeight + adjustDy).toInt()
            it.requestLayout()
        }
        abl?.bottom = (appBarHeight + adjustDy).toInt()
    }

    private fun recovery(abl: AppBarLayout?) {
        if (totalDy > 0) {
            if (animating || scalingSelf) {
                recorderAnimator = ValueAnimator.ofFloat(totalDy, 0f).setDuration(200)
                recorderAnimator?.addUpdateListener { animation: ValueAnimator ->
                    totalDy = animation.animatedValue as Float
                    refreshHeight(abl)
                }
                recorderAnimator?.start()
                if (totalDy > DP_PULL_REFRESH_HEIGHT.dp2px()) {
                    onPullRefreshListener?.onRefresh()
                }
            } else {
                totalDy = 0f
                refreshHeight(abl)
            }
        }
    }

    fun setOnPullRefreshListener(onPullRefreshListener: OnPullRefreshListener?) {
        this.onPullRefreshListener = onPullRefreshListener
    }

    interface OnPullRefreshListener {
        fun onRefresh()
    }

    companion object {
        private const val TAG = "ZoomAppBarBehavior"
        private const val TARGET_HEIGHT = 340f
        private const val DP_PULL_REFRESH_HEIGHT = 100f

        /**
         * 模拟拖动时的阻尼
         * @param dy
         * @return
         */
        private fun adjustDy(dy: Float): Double {
            return dy.toDouble().pow(0.93)
        }
    }
}