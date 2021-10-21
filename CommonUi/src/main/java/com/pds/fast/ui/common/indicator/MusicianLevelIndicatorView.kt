package com.pds.fast.ui.common.indicator

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.pds.fast.ui.common.R

class MusicianLevelIndicatorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val indicatorStartDistance: Int
    private val indicatorEndDistance: Int
    private val bgColor: Int
    private val fgColor: Int
    private val lineSize: Int
    private val indicatorSize: Int
    private val textSize: Float
    private val textNormalColor: Int
    private val textCurrentColor: Int
    private val bgLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bgRingPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val indicatorPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var lineColors: IntArray =
        intArrayOf(Color.parseColor("#1A1B1F"), Color.parseColor("#232529"))

    private var textColors: IntArray =
        intArrayOf(Color.parseColor("#F8FAFF"), Color.parseColor("#D8DCE2"))
    private var textCurrentColors: IntArray =
        intArrayOf(Color.parseColor("#FFEACB"), Color.parseColor("#FFC854"))

    private var linearGradient: LinearGradient? = null

    private var lvWidth = 0
    private var lvHeight = 0

    private var roundLinearGradient: LinearGradient? = null

    private val lineRect = RectF()
    private val rect = Rect()
    private var texts: Array<String> =
        arrayOf("V1", "V2", "V3", "V4", "V5", "V6", "V7", "V8", "V9", "V10")
    private var levelNum = 10
    private val bitmap: Bitmap

    var currentIndicatorIndex = 2
    private val offsetY = 3f
    private val offsetX = 1.5f

    private var currentIndicatorX = 0f
    private var currentIndicatorY = 0f

    private val typeface = Typeface.create("specific.ttf", Typeface.BOLD_ITALIC)

    init {

        indicatorStartDistance = dip2px(15f)
        indicatorEndDistance = indicatorStartDistance
        bgColor = Color.parseColor("#1C1D21")
        fgColor = Color.parseColor("#FFC854")
        lineSize = dip2px(4f)
        indicatorSize = dip2px(11f)
        textSize = sp2px(12)
        textNormalColor = Color.parseColor("#FF0000")
        textCurrentColor = Color.parseColor("#00FF00")

        bgLinePaint.color = bgColor
        indicatorPaint.color = fgColor
        bgRingPaint.color = Color.parseColor("#1C1D21")

        textPaint.textSize = textSize
        textPaint.typeface = typeface

        roundLinearGradient = LinearGradient(
            (indicatorSize / 2).toFloat(),
            0f,
            (indicatorSize / 2).toFloat(),
            indicatorSize.toFloat(),
            Color.parseColor("#FFC854"),
            Color.parseColor("#FFEACB"),
            Shader.TileMode.CLAMP
        )
        val option = BitmapFactory.Options()
        option.outWidth = dip2px(13f)
        option.outHeight = dip2px(13f)
        bitmap = BitmapFactory.decodeResource(resources, R.mipmap.point, option)
    }

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val w = MeasureSpec.getSize(widthMeasureSpec)
        val h = MeasureSpec.getSize(heightMeasureSpec)

        val lcs = lineColors
        if ((lvHeight != w || lvHeight != h) && null != lcs) {
            linearGradient = LinearGradient(
                0f, 0f,
                w.toFloat(), lineSize.toFloat(), lcs, null, Shader.TileMode.CLAMP
            )
        }
        lvWidth = w
        lvHeight = h
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        bgLinePaint.shader = linearGradient
        val yr = paddingTop + indicatorSize / 2 + offsetY
        val half = lineSize / 2

        lineRect.set(
            paddingLeft.toFloat(), yr - half,
            (width - paddingEnd).toFloat(), yr + half
        )

        val dp5 = dip2px(5f).toFloat()
        // 画背景直线
        canvas.drawRoundRect(lineRect, dp5, dp5, bgLinePaint)

        val average =
            (width - paddingLeft - paddingEnd - indicatorEndDistance - indicatorStartDistance - indicatorSize) * 1f / (levelNum - 1)
        // 画背景圆点
        drawBgRing(canvas, average)
        // 画前景圆点
        drawFgRing(canvas, average)
        // 绘制文字
        drawText(canvas, average)

    }

    private fun drawBgRing(canvas: Canvas, average: Float) {
        val indicatorSizeHalf = indicatorSize / 2f
        val start = paddingLeft + indicatorStartDistance + indicatorSizeHalf + offsetX
        val yr = paddingTop + indicatorSize / 2f + offsetY
        for (i in 0 until levelNum step 1) {
            val xr = i * average + start
            canvas.drawCircle(xr, yr, indicatorSizeHalf, bgRingPaint)
        }
    }

    private fun drawFgRing(canvas: Canvas, average: Float) {
        if (currentIndicatorIndex <= 0) {
            return
        }
        val indicatorSizeHalf = indicatorSize / 2f
        val start = paddingLeft + indicatorStartDistance + indicatorSizeHalf
        val top = paddingTop
        val left = (currentIndicatorIndex - 1) * average + start - indicatorSizeHalf
        canvas.drawBitmap(bitmap, left, top.toFloat(), indicatorPaint)
    }


    private fun drawText(canvas: Canvas, average: Float) {
        for (index in texts.indices) {
            drawCellText(canvas, average, texts[index], index)
        }
    }

    private fun drawCellText(
        canvas: Canvas,
        average: Float,
        text: String,
        index: Int
    ) {
        val indicatorSizeHalf = indicatorSize / 2f
        val start = paddingLeft + indicatorStartDistance + indicatorSizeHalf + offsetX
        val yr = paddingTop + indicatorSize + offsetY
        val xr = index * average + start

        val isCurrentLevel = (index + 1) == currentIndicatorIndex

        val length = text.length
        textPaint.getTextBounds(text, 0, length, rect)
        val wHalf = rect.width() / 2f
        val b = yr + dip2px(10f) + indicatorSize / 2f

        val textGradient = LinearGradient(
            wHalf,
            0f,
            wHalf,
            rect.height().toFloat(),
            if (isCurrentLevel) textCurrentColors else textColors,
            null,
            Shader.TileMode.CLAMP
        )
        textPaint.shader = textGradient
        canvas.drawText(text, 0, length, xr - wHalf, b, textPaint)
    }

    private fun dip2px(dp: Float): Int {
        return (dp * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
    }

    private fun sp2px(sp: Int): Float {
        return sp * Resources.getSystem().displayMetrics.scaledDensity + 0.5f
    }

    fun getCurrentIndicatorX(): Float {
        return currentIndicatorX
    }

    fun getCurrentIndicatorY(): Float {
        return currentIndicatorY
    }
}