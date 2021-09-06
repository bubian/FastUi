package com.pds.fast.ui.common.progress

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.pds.fast.ui.common.R
import kotlin.math.cos
import kotlin.math.sin

class FastRingProgress @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var bgColor = Color.TRANSPARENT
    private var ringGradientColor: IntArray
    private var nodeColor = Color.RED
    private var nodeRadius = 0
    private var strokeWidth = 0
    private var progress = 0F
    private var startPosition = 0
    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val nodePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var progressGradient: SweepGradient
    private val customMatrix = Matrix()
    private val rectF = RectF()
    private var wh = 0f

    companion object {
        private const val LEFT = 0
        private const val RIGHT = 1
        private const val TOP = 2
        private const val BOTTOM = 3
    }

    private fun isTransparent(color: Int): Boolean {
        return color == Color.TRANSPARENT;
    }

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.FastRingProgress, 0, 0)
        startPosition =
            typedArray.getInt(R.styleable.FastRingProgress_frp_startPosition, LEFT)

        nodeRadius =
            typedArray.getDimensionPixelSize(R.styleable.FastRingProgress_frp_nodeRadius, 0)
        strokeWidth =
            typedArray.getDimensionPixelSize(R.styleable.FastRingProgress_frp_strokeWidth, 0)

        bgColor = typedArray.getColor(R.styleable.FastRingProgress_frp_bgColor, Color.TRANSPARENT)
        nodeColor =
            typedArray.getColor(R.styleable.FastRingProgress_frp_nodeColor, Color.TRANSPARENT)
        val startColor =
            typedArray.getColor(R.styleable.FastRingProgress_frp_startColor, Color.TRANSPARENT)
        val middleColor =
            typedArray.getColor(R.styleable.FastRingProgress_frp_middleColor, Color.TRANSPARENT)
        val endColor =
            typedArray.getColor(R.styleable.FastRingProgress_frp_endColor, Color.TRANSPARENT)

        typedArray.recycle()

        val gradientColor = arrayListOf<Int>()
        if (!isTransparent(startColor)) gradientColor.add(startColor)
        if (!isTransparent(middleColor)) gradientColor.add(middleColor)
        if (!isTransparent(endColor)) gradientColor.add(endColor)
        ringGradientColor = gradientColor.toIntArray()

        wh = dip2px(45f)

        bgPaint.color = bgColor
        bgPaint.style = Paint.Style.STROKE
        bgPaint.strokeWidth = strokeWidth.toFloat()

        nodePaint.color = nodeColor
        nodePaint.strokeWidth = (2 * nodeRadius).toFloat()
        nodePaint.strokeCap = Paint.Cap.ROUND

        progressGradient = SweepGradient(wh / 2, wh / 2, ringGradientColor, null)

        customMatrix.setRotate(265f, wh / 2, wh / 2)
        progressGradient.setLocalMatrix(customMatrix)

        progressPaint.shader = progressGradient
        progressPaint.style = Paint.Style.STROKE
        progressPaint.strokeCap = Paint.Cap.ROUND
        progressPaint.strokeWidth = strokeWidth.toFloat()

        rectF.set(
            nodeRadius.toFloat(),
            nodeRadius.toFloat(),
            wh - nodeRadius.toFloat(),
            wh - nodeRadius.toFloat()
        )
    }

    fun setProgress(progress: Float) {
        this.progress = progress
    }

    override fun onDraw(canvas: Canvas) {
        val radius: Int = width / 2 - nodeRadius
        val progressAngle = progress * 360

        var nodeX = 0f
        var nodeY = 0f

        if (progressAngle < 90) {
            val angle = Math.PI / 180.toDouble() * (90 - progressAngle)
            val v = sin(angle).toFloat() * radius
            val v1 = cos(angle).toFloat() * radius
            nodeX = width / 2 + v1
            nodeY = height / 2 - v

        } else if (progressAngle == 90f) {
            nodeX = (width / 2 + radius).toFloat()
            nodeY = (height / 2).toFloat()
        } else if (progressAngle > 90 && progressAngle < 180) {
            val angle = (Math.PI / 180.toDouble() * (180 - progressAngle)).toFloat()
            val v = sin(angle) * radius
            val v1 = cos(angle) * radius
            nodeX = width / 2 + v
            nodeY = height / 2 + v1
        } else if (progressAngle == 180f) {
            nodeX = (width / 2).toFloat()
            nodeY = (height / 2 + radius).toFloat()
        } else if (progressAngle > 180 && progressAngle < 270) {
            val angle = (Math.PI / 180.toDouble() * (270 - progressAngle)).toFloat()
            val v = sin(angle) * radius
            val v1 = cos(angle) * radius
            nodeX = width / 2 - v1
            nodeY = height / 2 + v
        } else if (progressAngle == 270f) {
            nodeX = nodeRadius.toFloat()
            nodeY = (height / 2).toFloat()
        } else if (progressAngle > 270) {
            val angle = (Math.PI / 180.toDouble() * (360 - progressAngle)).toFloat()
            val v = sin(angle) * radius
            val v1 = cos(angle) * radius
            nodeX = width / 2 - v
            nodeY = height / 2 - v1
        }
        canvas.drawArc(rectF, progressAngle - 90f, (1 - progress) * 360f, false, bgPaint)
        canvas.drawArc(rectF, -90f, progressAngle, false, progressPaint)
        canvas.drawPoint(nodeX, nodeY, nodePaint)
    }

    private fun dip2px(dpValue: Float): Float {
        val density = context.resources.displayMetrics.density
        return (dpValue * density + 0.5).toFloat()
    }
}