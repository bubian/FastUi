package com.pds.fast.ui.common.progress

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.annotation.FloatRange
import com.pds.fast.ui.common.R
import com.pds.fast.ui.common.assist.dp2px
import kotlin.math.abs

class SimpleRingProgress1 @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var bgPaint: Paint? = null
    private var strokePaint: Paint? = null
    private var remainProgressPaint: Paint? = null
    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var progress: Float = 0f
    private var progressRingMax = 0
    private val startPosition: Int

    private var valueAnimator: ValueAnimator? = null
    private var animationTime = 1_000L

    private val rectF = RectF()

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.SimpleRingProgress1, 0, 0)
        val bgColor = a.getColor(R.styleable.SimpleRingProgress1_srp1_bgColor, Color.TRANSPARENT)
        if (bgColor != Color.TRANSPARENT) {
            bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = bgColor }
        }

        val progressSize =
            a.getDimensionPixelSize(R.styleable.SimpleRingProgress1_srp1_progressSize, 2f.dp2px())
        progressPaint.apply {
            style = Paint.Style.STROKE
            color = a.getColor(R.styleable.SimpleRingProgress1_srp1_progressColor, Color.RED)
            strokeWidth = progressSize.toFloat()
            strokeCap = Paint.Cap.ROUND
        }

        val remainProgressColor =
            a.getColor(R.styleable.SimpleRingProgress1_srp1_remainProgressColor, Color.TRANSPARENT)
        if (remainProgressColor != Color.TRANSPARENT) {
            remainProgressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                color = remainProgressColor
                strokeWidth = progressSize.toFloat()
            }
        }

        val strokeColor =
            a.getColor(R.styleable.SimpleRingProgress1_srp1_strokeColor, Color.TRANSPARENT)
        val sw = a.getDimensionPixelSize(R.styleable.SimpleRingProgress1_srp1_strokeWidth, 0)

        progressRingMax = progressSize
        if (sw > 0 && strokeColor != Color.TRANSPARENT) {
            progressRingMax += sw
            strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = progressRingMax.toFloat()
                color = strokeColor
                strokeCap = Paint.Cap.ROUND
            }
        }
        animationTime =
            a.getInteger(R.styleable.SimpleRingProgress1_srp1_animationTime, 1_000).toLong()
        startPosition = a.getInt(R.styleable.SimpleRingProgress1_srp1_startPosition, 3)
        a.recycle()
    }

    fun setProgress(progress: Float) {
        this.progress = progress
        invalidate()
    }

    fun setProgressWithAnimation(@FloatRange(from = 0.0, to = 1.0) progress: Float) {
        setProgressWithAnimation(0f, progress)
    }

    fun setProgressWithAnimation(startProgress: Float, endProgress: Float) {
        valueAnimator?.cancel()
        valueAnimator = ValueAnimator.ofFloat(progressCheck(startProgress), progressCheck(endProgress))
                .setDuration(abs(animationTime))
        valueAnimator?.addUpdateListener {
            this.progress = it.animatedValue as Float
            invalidate()
        }
        valueAnimator?.start()
    }

    private fun progressCheck(progress: Float): Float = when {
        progress < 0.001 -> 0f
        progress > 0.999 -> 1f
        else -> progress
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        valueAnimator?.cancel()
    }

    override fun draw(canvas: Canvas) {
        bgPaint?.let {
            canvas.drawCircle(
                width / 2f, height / 2f,
                (width - paddingLeft - paddingEnd - progressRingMax) / 2f, it
            )
        }
        super.draw(canvas)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val radius = (width - paddingLeft - paddingEnd) / 2f - progressRingMax / 2

        val xCenter = width / 2f
        val yCenter = height / 2f
        rectF.set(xCenter - radius, yCenter - radius, xCenter + radius, yCenter + radius)

        val angle = progress * 360
        val addAngle = startPosition * 90f
        remainProgressPaint?.let {
            canvas.drawArc(rectF, angle + addAngle, 360f - angle, false, it)
        }

        if (angle > 1) {
            strokePaint?.let {
                canvas.drawArc(rectF, addAngle, angle, false, it)
            }
            canvas.drawArc(rectF, addAngle, angle, false, progressPaint)
        }
    }
}