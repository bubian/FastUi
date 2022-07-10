package com.pds.fast.ui.common.ruler

import android.app.Service
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Vibrator
import android.text.TextPaint
import android.util.AttributeSet
import com.pds.fast.ui.common.assist.dp22px
import com.pds.fast.ui.common.assist.dp2px
import kotlin.math.abs

class SceneScrollPicker @JvmOverloads constructor(
    context: Context?, attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : BasePickerView<SceneItemModel>(context, attrs, defStyleAttr) {

    private var vibrator: Vibrator? = null
    private val mTextPaint: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val mCenterPaint: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val mLinePaint: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val mMinTextSize = 16f.dp22px()
    private val mMaxTextSize = 18f.dp22px()
    private val mSmallLineHeight = 10f.dp22px()
    private val mLongLineHeight = 16f.dp22px()
    private var mLineWidth = 1f.dp22px()
    private val mStartColor = Color.parseColor("#1A1A1A")
    private val mEndColor = Color.parseColor("#666666")
    private val mCenterRect: RectF = RectF()
    private val mLineRect: RectF = RectF()
    private var maxLineWidth = -1
    private var mLineSpace = 0F
    private var mOffset = 0

    init {
        mTextPaint.style = Paint.Style.FILL
        mTextPaint.textAlign = Paint.Align.CENTER
        mCenterPaint.strokeWidth = 4f.dp22px()
        mCenterPaint.style = Paint.Style.FILL
        mCenterPaint.color = Color.parseColor("#fa3123")
        mLinePaint.strokeWidth = mLineWidth.toFloat()
        mLinePaint.style = Paint.Style.FILL
        mLinePaint.color = Color.parseColor("#a6a6a6")
        visibleItemCount = 4
        isDrawAllItem = true
        isInertiaScroll = false
        vibrator = context?.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (maxLineWidth < 0) {
            maxLineWidth = itemWidth
            mOffset = if (visibleItemCount % 2 == 0) maxLineWidth shr 1 else 0
        }
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

    override fun drawItem(
        canvas: Canvas?,
        data: MutableList<SceneItemModel>,
        position: Int,
        relative: Int,
        moveLength: Float,
        top: Float
    ) {
        val text: CharSequence = data[position].title
        if (relative == -1) {
            if (moveLength < 0) {
                mTextPaint.textSize = mMinTextSize.toFloat()
                mTextPaint.isFakeBoldText = false
            } else {
                mTextPaint.textSize = mMinTextSize.toFloat()
                mTextPaint.isFakeBoldText = false
            }
        } else if (relative == 0) {
            mTextPaint.textSize = mMaxTextSize.toFloat()
            mTextPaint.isFakeBoldText = true
        } else if (relative == 1) {
            if (moveLength > 0) {
                mTextPaint.textSize = mMinTextSize.toFloat()
                mTextPaint.isFakeBoldText = false
            } else {
                mTextPaint.textSize = mMinTextSize.toFloat()
                mTextPaint.isFakeBoldText = false
            }
        } else {
            mTextPaint.textSize = mMinTextSize.toFloat()
            mTextPaint.isFakeBoldText = false
        }

        val x: Float = top
        canvas?.save()
        canvas?.translate(x - mOffset, 0F)
        var end = 0F
        mLineSpace = (itemWidth - 10 * mLineWidth) / 10F
        for (detail in 0..11) {
            val longLine = detail == 5
            if (detail == 0 || detail == 10) {
                end += mLineWidth
                continue
            } else {
                end += mLineSpace + mLineWidth
            }
            mLineRect.left = end - mLineWidth
            mLineRect.top = getStartY(longLine)
            mLineRect.right = end
            mLineRect.bottom = endY.toFloat()
            if (longLine) {
                mTextPaint.isFakeBoldText = relative == 0
                mTextPaint.color = if (relative == 0) mStartColor else mEndColor
                mTextPaint.textSize = (if (relative == 0) 18f else 16f).dp22px()
                canvas?.drawText(text.toString(), mLineRect.centerX(), mLineRect.bottom + 26f.dp22px(), mTextPaint)
            }
            canvas?.drawRoundRect(mLineRect, 1f.dp22px(), 1f.dp22px(), mLinePaint)
        }
        canvas?.restore()
    }

    private fun getStartY(isLong: Boolean): Float {
        return if (isLong) endY - mLongLineHeight else endY - mSmallLineHeight
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (data.isNullOrEmpty()) return
        mCenterRect.apply {
            left = (measuredWidth shr 1) - 2f.dp22px()
            top = 6f.dp22px()
            right = mCenterRect.left + 4f.dp22px()
            bottom = endY.toFloat()
        }
        canvas.drawRoundRect(mCenterRect, 2f.dp22px(), 2f.dp22px(), mCenterPaint)
    }

    private val endY: Int
        get() = 28f.dp2px()

    fun addItem(item: SceneItemModel) {
        selectedPosition = if (!data.isNullOrEmpty() && data.contains(item)) {
            data.indexOf(item)
        } else {
            data.add(item)
            data.indexOf(item)
        }
    }

    override fun onMove(from: Float, to: Float) {
        super.onMove(from, to)
        if (from.toInt() == to.toInt()) return
        mLastDown += to - from
        vibrator?.let {
            if (it.hasVibrator() && abs(mLastDown) > mLineWidth + mLineSpace) {
                it.vibrate(30)
                if (mLastDown > 0) {
                    mLastDown -= mLineSpace + mLineWidth
                } else {
                    mLastDown += mLineSpace + mLineWidth
                }
            }
        }
    }
}