package com.pds.fast.ui.common.ruler

import android.app.Service
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Vibrator
import android.util.AttributeSet
import android.view.*
import android.widget.Scroller
import com.pds.fast.ui.common.assist.dp22px
import com.pds.fast.ui.common.assist.dp2px
import com.pds.fast.ui.common.assist.getScreenWidth
import kotlin.math.abs

class SceneRulerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var mLineWidth = 0
    private var mLineSpace = 0
    private var mSmallLineHeight = 0
    private var mLongLineHeight = 0

    private lateinit var mLinePaint: Paint
    private lateinit var mCenterPaint: Paint
    private lateinit var mTextPaint: Paint
    private lateinit var mLineRect: RectF
    private lateinit var mCenterRect: RectF
    private lateinit var mRulerHelper: RulerHelper
    private lateinit var mScroller: Scroller
    private lateinit var velocityTracker: VelocityTracker
    private lateinit var vibrator: Vibrator

    private var scrollSelected: ScrollSelected? = null

    private var mCountWidth = 0
    private var mPressUp = false
    private var isFling = false
    private var mMinVelocity = 0
    private var startX = 0f
    private var mMarginLeft = -1
    private var mPaddingRight = -1
    private var models: ArrayList<SceneItemModel>? = null

    init {
        init(context)
    }

    private fun init(context: Context) {
        initDistanceForDp()

        vibrator = context.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
        mLineRect = RectF()
        mCenterRect = RectF()
        mTextPaint = Paint()
        mTextPaint.isAntiAlias = true
        mTextPaint.textSize = 16f.dp22px()
        mTextPaint.color = Color.parseColor("#666666")
        mTextPaint.textAlign = Paint.Align.CENTER
        mLinePaint = Paint()
        mLinePaint.isAntiAlias = true
        mLinePaint.strokeWidth = mLineWidth.toFloat()
        mLinePaint.style = Paint.Style.FILL
        mLinePaint.color = Color.parseColor("#a6a6a6")
        mCenterPaint = Paint()
        mCenterPaint.isAntiAlias = true
        mCenterPaint.strokeWidth = 4f.dp22px()
        mCenterPaint.style = Paint.Style.FILL
        mCenterPaint.color = Color.parseColor("#fa3123")

        mRulerHelper = RulerHelper(object : ScrollChange {
            override fun startScroll(distance: Int) {
                doScroll(-distance, 300)
                postInvalidate()
            }
        })

        mScroller = Scroller(context)
        velocityTracker = VelocityTracker.obtain()
        mMinVelocity = ViewConfiguration.get(context).scaledMinimumFlingVelocity
    }

    private fun initDistanceForDp() {
        mLineWidth = 1f.dp2px()
        mLineSpace = ((context.getScreenWidth() - 40f.dp2px()) / 40f).toInt()
        mSmallLineHeight = 10f.dp2px()
        mLongLineHeight = 16f.dp2px()
    }

    fun setScope(list: ArrayList<SceneItemModel>) {
        models = list
        val array = ArrayList<String>()
        for (s in list) {
            array.add(s.title)
        }
        mRulerHelper.setScope(array)
        val counts = mRulerHelper.counts
        mCountWidth = counts * mLineSpace + counts * mLineWidth
        invalidate()
    }

    fun addScope(item: SceneItemModel) {
        if (models.isNullOrEmpty()) {
            models = ArrayList()
        }
        if (models!!.contains(item)) {
            setCurrent(item)
            return
        }
        models!!.add(item)
        val array = ArrayList<String>()
        for (s in models!!) {
            array.add(s.title)
        }
        mRulerHelper.destroy()
        mRulerHelper.setScope(array)
        val counts = mRulerHelper.counts
        mCountWidth = counts * mLineSpace + counts * mLineWidth
        setCurrent(item)
    }

    fun setCurrent(int: Int) {
        mRulerHelper.setCurrentText(int)
        scrollSelected?.selected(models?.get(int))
        invalidate()
    }

    fun setCurrent(item: SceneItemModel) {
        if (models.isNullOrEmpty()) return
        val index = mRulerHelper.currentIndex
        val indexOf = models!!.indexOf(item)
        if (indexOf != -1) {
            mRulerHelper.setCurrentText(indexOf)
            scrollSelected?.selected(models?.get(indexOf))
            val dx = (index - indexOf) * mLineSpace + (index - indexOf) * mLineWidth
            mScroller.startScroll(mScroller.currX, mScroller.currY, -dx * 10, 0, 0)
            invalidate()
        }
    }

    fun setScrollSelected(scrollSelected: ScrollSelected) {
        this.scrollSelected = scrollSelected
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val mode = MeasureSpec.getMode(heightMeasureSpec)
        if (mode == MeasureSpec.AT_MOST) {
            heightSize = 71f.dp2px()
        }
        setMeasuredDimension(widthSize, heightSize)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mRulerHelper.counts > 0) {
            val layoutParams = layoutParams as ViewGroup.MarginLayoutParams
            if (mMarginLeft == -1) {
                mMarginLeft = layoutParams.leftMargin
                mPaddingRight = layoutParams.rightMargin
                mRulerHelper.centerPointX = width / 2
            }
            drawRuler(canvas)
        }
    }

    private fun drawRuler(canvas: Canvas) {
        for (index in 0..mRulerHelper.counts) {
            val longLine = mRulerHelper.isLongLine(index)
            val lineCount = mLineWidth * index
            mLineRect.left = (index * mLineSpace + lineCount + mMarginLeft).toFloat()
            mLineRect.top = getStartY(longLine).toFloat()
            mLineRect.right = mLineRect.left + mLineWidth
            mLineRect.bottom = endY.toFloat()
            if (longLine) {
                if (!mRulerHelper.isFull) mRulerHelper.addPoint(mLineRect.left.toInt())
                val text = mRulerHelper.getTextByIndex(index / 10)
                mTextPaint.isFakeBoldText = index / 10 == mRulerHelper.currentIndex
                mTextPaint.color = Color.parseColor(if (index / 10 == mRulerHelper.currentIndex) "#1A1A1A" else "#666666")
                mTextPaint.textSize = (if (index / 10 == mRulerHelper.currentIndex) 18f else 16f).dp22px()
                canvas.drawText(text, mLineRect.centerX(), mLineRect.bottom + 26f.dp22px(), mTextPaint)
            }
            canvas.drawRoundRect(mLineRect, 1f.dp22px(), 1f.dp22px(), mLinePaint)
            mLineRect.setEmpty()
        }
        mCenterRect.left = ((measuredWidth shr 1) - 2f.dp2px() + mScroller.currX).toFloat()
        mCenterRect.top = 6f.dp22px()
        mCenterRect.right = mCenterRect.left + 4f.dp2px()
        mCenterRect.bottom = endY.toFloat()
        canvas.drawRoundRect(mCenterRect, 2f.dp22px(), 2f.dp22px(), mCenterPaint)
    }

    private fun getStartY(isLong: Boolean): Int {
        return if (isLong) {
            endY - mLongLineHeight
        } else {
            endY - mSmallLineHeight
        }
    }

    private val endY: Int
        get() = 28f.dp2px()

    override fun onTouchEvent(event: MotionEvent): Boolean {
        velocityTracker.addMovement(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mScroller.forceFinished(true)
                mPressUp = false
                isFling = false
                startX = event.x
            }
            MotionEvent.ACTION_MOVE -> {
                mPressUp = false
                val distance = event.x - startX
                if (-1f != distance) {
                    doScroll((-distance).toInt(), 0)
                    invalidate()
                }
                startX = event.x
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                mPressUp = true
                velocityTracker.computeCurrentVelocity(1000)
                val xVelocity = velocityTracker.xVelocity
                if (abs(xVelocity) >= mMinVelocity) {
                    isFling = true
                    val finalX = mScroller.currX
                    val centerPointX = mRulerHelper.centerPointX
                    val velocityX = (xVelocity * 0.01).toInt()
                    mScroller.fling(
                        finalX, 0, -velocityX, 0, -centerPointX - 10, centerPointX + 10, 0, 0
                    )
                    invalidate()
                } else {
                    isFling = false
                    scrollFinish()
                }
                velocityTracker.clear()
            }
            else -> {}
        }
        return true
    }

    private fun scrollFinish() {
        val finalX = mScroller.finalX
        val centerPointX = mRulerHelper.centerPointX
        val currentX = centerPointX + finalX
        val scrollDistance = mRulerHelper.getScrollDistance(currentX)
        mScroller.startScroll(mScroller.finalX, mScroller.finalY, -scrollDistance, 0, 300)
        invalidate()
        scrollSelected?.selected(models?.get(mRulerHelper.currentIndex))
    }

    private fun doScroll(dx: Int, duration: Int) {
        mScroller.startScroll(mScroller.finalX, mScroller.finalY, dx, 0, duration)
        vibrate(duration)
    }

    private fun vibrate(duration: Int) {
        if (mScroller.currX == mScroller.finalX) return
        var start = 0
        var end = 0
        for (index in 0..mRulerHelper.counts) {
            val point = index * mLineSpace + mLineWidth * index + mMarginLeft - width / 2f
            if (mScroller.currX < mScroller.currY) {
                if (mScroller.currX >= point) {
                    start = index
                }
                if (mScroller.finalX >= point) {
                    end = index
                }
            } else {
                if (mScroller.currX >= point) {
                    start = index
                }
                if (mScroller.finalX >= point) {
                    end = index
                }
            }
        }
        if (vibrator.hasVibrator() && duration == 0 && abs(end - start) > 0) {
            vibrator.vibrate(30)
        }
    }

    override fun computeScroll() {
        if (mScroller.computeScrollOffset()) {
            if (mScroller.currX == mScroller.finalX && mPressUp && isFling) {
                mPressUp = false
                isFling = false
                scrollFinish()
            }
            scrollTo(mScroller.currX, 0)
            invalidate()
        }
        super.computeScroll()
    }

    fun destroy() {
        velocityTracker.recycle()
        mRulerHelper.destroy()
    }

    class RulerHelper(private var scrollChange: ScrollChange?) {
        private var texts: MutableList<String> = ArrayList()
        private var mPoints: MutableList<Int> = ArrayList()
        private var currentText = ""
        var currentLine = -1
        var currentIndex = 0
        var counts = 0
        var centerPointX = 0

        fun isLongLine(index: Int): Boolean {
            val lineIndex = index / 10
            if (currentLine != lineIndex) {
                currentLine = lineIndex
                return true
            }
            return false
        }

        fun setScope(list: List<String>) {
            counts = (list.size - 1) * 10
            texts.addAll(list)
        }

        fun getTextByIndex(index: Int): String {
            return if (index < 0 || index >= texts.size) "" else texts[index]
        }

        fun setCurrentText(index: Int) {
            if (index >= 0 && index < texts.size) {
                currentText = texts[index]
                currentIndex = index
            }
        }

        fun getScrollDistance(x: Int): Int {
            for (i in mPoints.indices) {
                val pointX = mPoints[i]
                if (0 == i && x < pointX) {
                    setCurrentText(0)
                    return x - pointX
                } else if (i == mPoints.size - 1 && x > pointX) {
                    setCurrentText(texts.size - 1)
                    return x - pointX
                } else {
                    if (i + 1 < mPoints.size) {
                        val nextX = mPoints[i + 1]
                        if (x in (pointX + 1)..nextX) {
                            val distance = (nextX - pointX) / 2
                            val dis = x - pointX
                            return if (dis > distance) {
                                setCurrentText(i + 1)
                                x - nextX
                            } else {
                                setCurrentText(i)
                                x - pointX
                            }
                        }
                    }
                }
            }
            setCurrentText(0)
            return 0
        }

        fun addPoint(x: Int) {
            mPoints.add(x)
            scrollChange ?: return
            if (mPoints.size == texts.size) {
                val index = texts.indexOf(currentText)
                if (index < 0) return
                val currentX = mPoints[index]
                if (currentX < 0) return
                scrollChange!!.startScroll(centerPointX - currentX)
            }
        }

        val isFull: Boolean
            get() = texts.size == mPoints.size

        fun destroy() {
            mPoints.clear()
            texts.clear()
            scrollChange = null
        }
    }

    interface ScrollChange {
        fun startScroll(distance: Int)
    }

    interface ScrollSelected {
        fun selected(selected: SceneItemModel?)
    }
}