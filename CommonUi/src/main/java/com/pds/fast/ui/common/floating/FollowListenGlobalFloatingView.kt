package com.pds.fast.ui.common.floating

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.TextView
import com.pds.fast.ui.common.R
import com.pds.fast.ui.common.Shapes
import com.pds.fast.ui.common.assist.dp22px
import com.pds.fast.ui.common.assist.dp2px
import kotlin.math.abs

class FollowListenGlobalFloatingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    companion object {
        private val LIMIT = 100f.dp22px()

        @JvmStatic
        private var TRANSLATEY = 0F
    }

    fun getWidth(var0: Context): Int {
        return var0.resources.displayMetrics.widthPixels
    }


    fun getHeight(var0: Context): Int {
        return var0.resources.displayMetrics.heightPixels
    }

    private val ivCurrentRoomName: TextView

    private var last = 0F

    init {
        LayoutInflater.from(context).inflate(R.layout.global_followlisten_floating, this)
        findViewById<View>(R.id.ll_current_room).apply {
            background = Shapes.Builder(Shapes.RECTANGLE).setSolid(Color.WHITE).setCornerRadius(22f.dp22px()).build()
        }

        ivCurrentRoomName = findViewById<TextView>(R.id.iv_current_room_name).apply {
            ellipsize = TextUtils.TruncateAt.MARQUEE
            marqueeRepeatLimit = -1
            isSingleLine = true
            isSelected = true
        }


        setPadding(10f.dp2px(), 6f.dp2px(), 10f.dp2px(), 14f.dp2px())
        background = resources.getDrawable(R.drawable.icon_lr_shadow)

        setOnTouchListener(object : OnCombineEventListener() {
            override fun onClick() {

            }

            override fun onDoubleClick() {

            }

            override fun onMove(dx: Float, dy: Float) {
                moving(dx, dy)
            }

            override fun upMove() {
                last = 0F
                moveSide()
            }

            override fun upDown() {
                last = TRANSLATEY
            }
        })
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        translationY = TRANSLATEY
    }

    private fun moving(dx: Float, dy: Float) {
        translationX = when {
            dx > 0F -> 0F
            abs(dx) > getWidth(context) - this.width -> -(getWidth(context) - this.width).toFloat()
            else -> dx
        }
        translationY = when {
            last + dy > 0F -> 0F
            abs(last + dy) > getHeight(context) - LIMIT -> -(getHeight(context) - LIMIT).toFloat()
            else -> last + dy
        }
    }

    private fun moveSide() {
        val x: Float = translationX
        val y: Float = translationY
        var ty: Float = translationY
        if (ty > 0) ty = 0F else if (abs(ty) > getHeight(context) - LIMIT) ty = -(getHeight(context) - LIMIT).toFloat()

        ValueAnimator.ofFloat(1f, 0f).apply {
            duration = 300
            interpolator = DecelerateInterpolator()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(p0: Animator?) {
                    translationX = 0f
                    translationY = ty
                }

                override fun onAnimationCancel(p0: Animator?) {
                    translationX = 0f
                    translationY = ty
                }
            })
            addUpdateListener { animation: ValueAnimator ->
                translationX = x * animation.animatedValue as Float
                translationY = (y - ty) * animation.animatedValue as Float + ty
            }
            start()
        }
    }

    abstract class OnCombineEventListener : OnTouchListener {
        companion object {
            private const val MAX_LONG_PRESS_TIME = 400
            private const val MAX_SINGLE_CLICK_TIME = 220
            private const val MIN_DISTANCE = 8
        }

        private var mClickCount = 0
        private var mDownX = 0F
        private var mDownY = 0F
        private var mLastDownTime: Long = 0
        private var mFirstClick: Long = 0
        private var mSecondClick: Long = 0
        private var mLastDouble: Long = 0
        private var isDrag = false
        private var isDoubleClick = false
        private val mBaseHandler = Handler(Looper.getMainLooper())

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    upDown()
                    isDrag = false
                    mLastDownTime = System.currentTimeMillis()
                    mDownX = event.rawX
                    mDownY = event.rawY
                    mClickCount++
                    mBaseHandler.removeCallbacks(mSingleClickTask)
                    if (!isDoubleClick) {
                        mBaseHandler.postDelayed(mLongPressTask, MAX_LONG_PRESS_TIME.toLong())
                    }
                    if (1 == mClickCount) {
                        mFirstClick = System.currentTimeMillis()
                    } else if (mClickCount == 2) {
                        mSecondClick = System.currentTimeMillis()
                        if (mSecondClick - mFirstClick <= MAX_LONG_PRESS_TIME) {
                            mLastDouble = mSecondClick
                            mDoubleClickTask()
                        }
                    } else if (mClickCount > 2) {
                        mClickCount = 1
                        mFirstClick = System.currentTimeMillis()
                        return true
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    val mMoveX = event.rawX
                    val mMoveY = event.rawY
                    val absMx = abs(mMoveX - mDownX)
                    val absMy = abs(mMoveY - mDownY)
                    if (absMx > MIN_DISTANCE || absMy > MIN_DISTANCE) {
                        mBaseHandler.removeCallbacks(mLongPressTask)
                        mBaseHandler.removeCallbacks(mSingleClickTask)
                        isDoubleClick = false
                        isDrag = true
                        mClickCount = 0
                        mMoveTask(mMoveX - mDownX, mMoveY - mDownY)
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    val mLastUpTime = System.currentTimeMillis()
                    if (!isDrag) {
                        if (mLastUpTime - mLastDownTime <= MAX_LONG_PRESS_TIME) {
                            mBaseHandler.removeCallbacks(mLongPressTask)
                            if (!isDoubleClick && mLastDownTime - mLastDouble > MAX_LONG_PRESS_TIME) {
                                mBaseHandler.postDelayed(
                                    mSingleClickTask,
                                    MAX_SINGLE_CLICK_TIME.toLong()
                                )
                            }
                        } else {
                            mClickCount = 0
                        }
                    } else {
                        mClickCount = 0
                        mUpTask()
                    }
                    if (isDoubleClick) {
                        isDoubleClick = false
                    }
                }
            }
            return true
        }

        private val mSingleClickTask = Runnable {
            mClickCount = 0
            onClick()
        }

        private fun mDoubleClickTask() {
            isDoubleClick = true
            mFirstClick = 0
            mSecondClick = 0
            mClickCount = 0
            mBaseHandler.removeCallbacks(mSingleClickTask)
            mBaseHandler.removeCallbacks(mLongPressTask)
            onDoubleClick()
        }

        private fun mMoveTask(x: Float, y: Float) {
            onMove(x, y)
        }

        private fun mUpTask() {
            upMove()
        }

        private val mLongPressTask = Runnable {
            mClickCount = 0
        }

        abstract fun onClick()
        abstract fun onDoubleClick()
        abstract fun onMove(dx: Float, dy: Float)
        abstract fun upMove()
        abstract fun upDown()
    }

    override fun setTranslationY(translationY: Float) {
        TRANSLATEY = translationY
        super.setTranslationY(translationY)
    }
}