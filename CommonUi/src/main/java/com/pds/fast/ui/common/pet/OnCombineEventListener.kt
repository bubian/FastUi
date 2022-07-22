package com.pds.fast.ui.common.pet

import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import com.pds.fast.ui.common.assist.dp22px
import kotlin.math.abs

abstract class OnCombineEventListener : View.OnTouchListener {
    private var mClickCount = 0
    private var mDownX = 0
    private var mDownY = 0
    private var mLastDownTime: Long = 0
    private var mFirstClick: Long = 0
    private var mSecondClick: Long = 0
    private var mLastDouble: Long = 0
    private var isDrag = false
    private var isDoubleClick = false
    private val mBaseHandler: Handler = Handler(Looper.getMainLooper())

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isDrag = false
                mLastDownTime = System.currentTimeMillis()
                mDownX = event.x.toInt()
                mDownY = event.y.toInt()
                mClickCount++
                mBaseHandler.removeCallbacks(mSingleClickTask)
                if (!isDoubleClick) mBaseHandler.postDelayed(mLongPressTask, MAX_LONG_PRESS_TIME.toLong())

                if (1 == mClickCount) mFirstClick = System.currentTimeMillis()
                else if (mClickCount == 2) {
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
                val mMoveX = event.x.toInt()
                val mMoveY = event.y.toInt()
                val absMx = abs(mMoveX - mDownX)
                val absMy = abs(mMoveY - mDownY)
                if (absMx > MIN_DISTANCE || absMy > MIN_DISTANCE) {
                    mBaseHandler.removeCallbacks(mLongPressTask)
                    mBaseHandler.removeCallbacks(mSingleClickTask)
                    isDoubleClick = false
                    isDrag = true
                    mClickCount = 0
                    mMoveTask(mMoveX, mMoveY, mMoveX - mDownX, mMoveY - mDownY)
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                val mLastUpTime = System.currentTimeMillis()
                if (!isDrag) {
                    if (mLastUpTime - mLastDownTime <= MAX_LONG_PRESS_TIME) {
                        mBaseHandler.removeCallbacks(mLongPressTask)
                        if (!isDoubleClick && mLastDownTime - mLastDouble > MAX_LONG_PRESS_TIME) {
                            mBaseHandler.postDelayed(mSingleClickTask, MAX_SINGLE_CLICK_TIME.toLong())
                        }
                    } else mClickCount = 0

                } else {
                    mClickCount = 0
                    mUpTask(event.rawX.toInt(), event.rawY.toInt())
                }
                if (isDoubleClick) isDoubleClick = false
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

    private fun mMoveTask(x: Int, y: Int, dx: Int, dy: Int) {
        onMove(x, y, dx, dy)
    }

    private fun mUpTask(x: Int, y: Int) {
        upMove(x, y)
    }

    private val mLongPressTask = Runnable {
        mClickCount = 0
        onLongPress()
    }

    abstract fun onClick()
    abstract fun onDoubleClick()
    abstract fun onLongPress()
    abstract fun onMove(x: Int, y: Int, dx: Int, dy: Int)
    abstract fun upMove(x: Int, y: Int)

    companion object {
        private const val MAX_LONG_PRESS_TIME = 400
        private const val MAX_SINGLE_CLICK_TIME = 220

        @JvmStatic
        private val MIN_DISTANCE = 1f.dp22px() * 4
    }
}