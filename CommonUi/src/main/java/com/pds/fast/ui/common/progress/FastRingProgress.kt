package com.pds.fast.ui.common.progress

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import com.pds.fast.ui.common.R
import kotlin.math.abs
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
    private var progressGradient: SweepGradient? = null
    private val customMatrix = Matrix()
    private val rectF = RectF()
    private var wh = 0f
    private var animationTime = 1_000L
    private var valueAnimator: ValueAnimator? = null

    companion object {
        private const val RIGHT = 0
        private const val BOTTOM = 1
        private const val LEFT = 2
        private const val TOP = 3
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
        createRingGradient()
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

    fun getProgress(): Float {
        return progress
    }

    fun setWH(wh: Float) {
        this.wh = wh
        createRingGradient()
        rectF.set(nodeRadius.toFloat(), nodeRadius.toFloat(), wh - nodeRadius.toFloat(), wh - nodeRadius.toFloat())
    }

    fun setAnimationTime(time: Long){
        animationTime = time
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        valueAnimator?.cancel()
    }

    fun setProgressWithAnimation(progress: Float) {
        setProgressWithAnimation(0f, progress)
    }

    fun setProgressWithAnimation(startProgress: Float, endProgress: Float) {
        valueAnimator?.cancel()
        valueAnimator = ValueAnimator.ofFloat(startProgress, endProgress).setDuration(abs(animationTime))
        valueAnimator?.addUpdateListener {
            this.progress = it.animatedValue as Float
            invalidate()
        }
        valueAnimator?.start()
    }

    fun setRingGradientColor(intArray: IntArray) {
        ringGradientColor = intArray
        createRingGradient()
    }

    private fun createRingGradient() {
        progressGradient = SweepGradient(wh / 2, wh / 2, ringGradientColor, null)
        val rotateAngle = (90 * startPosition - 5).toFloat()
        customMatrix.setRotate(rotateAngle, wh / 2, wh / 2)
        progressGradient?.setLocalMatrix(customMatrix)
        progressPaint.shader = progressGradient
    }

    override fun onDraw(canvas: Canvas) {
        val radius: Int = width / 2 - nodeRadius
        var realProgressAngle = progress * 360

        if (realProgressAngle > 360) {
            realProgressAngle = 360f
        }

        var wrapProgressAngle = realProgressAngle + (startPosition + 1) * 90

        if (wrapProgressAngle > 360) {
            wrapProgressAngle -= 360
        }

        val wHalf = width / 2
        val hHalf = height / 2

        var nodeX = 0f
        var nodeY = 0f

        when {
            wrapProgressAngle < 90 -> {
                // sin30°为列:Math.sin(30*Math.PI/180),思路为 PI 相当于 π,而此时的 PI 在角度值里相当 于180°,所以 Math.PI/180得到的结果就是1°,然后再乘以30就......
                val angle = (Math.PI / 180.toDouble() * (90 - wrapProgressAngle)).toFloat()
                val v = sin(angle) * radius
                val v1 = cos(angle) * radius
                nodeX = wHalf + v1
                nodeY = hHalf - v
            }
            wrapProgressAngle == 90f -> {
                nodeX = (wHalf + radius).toFloat()
                nodeY = (hHalf).toFloat()
            }
            wrapProgressAngle > 90 && wrapProgressAngle < 180 -> {
                val angle = (Math.PI / 180.toDouble() * (180 - wrapProgressAngle)).toFloat()
                val v = sin(angle) * radius
                val v1 = cos(angle) * radius
                nodeX = wHalf + v
                nodeY = hHalf + v1
            }
            wrapProgressAngle == 180f -> {
                nodeX = (wHalf).toFloat()
                nodeY = (hHalf + radius).toFloat()
            }
            wrapProgressAngle > 180 && wrapProgressAngle < 270 -> {
                val angle = (Math.PI / 180.toDouble() * (270 - wrapProgressAngle)).toFloat()
                val v = sin(angle) * radius
                val v1 = cos(angle) * radius
                nodeX = wHalf - v1
                nodeY = hHalf + v
            }
            wrapProgressAngle == 270f -> {
                nodeX = nodeRadius.toFloat()
                nodeY = (hHalf).toFloat()
            }
            wrapProgressAngle > 270 -> {
                val angle = (Math.PI / 180.toDouble() * (360 - wrapProgressAngle)).toFloat()
                val v = sin(angle) * radius
                val v1 = cos(angle) * radius
                nodeX = wHalf - v
                nodeY = hHalf - v1
            }
        }
        canvas.drawArc(rectF, realProgressAngle + startPosition * 90f, (1 - progress) * 360f, false, bgPaint)
        if (realProgressAngle > 0.001) {
            canvas.drawArc(rectF, startPosition * 90f, realProgressAngle, false, progressPaint)
            canvas.drawPoint(nodeX, nodeY, nodePaint)
        }
    }

    private fun dip2px(dpValue: Float): Float {
        val density = context.resources.displayMetrics.density
        return (dpValue * density + 0.5).toFloat()
    }
}