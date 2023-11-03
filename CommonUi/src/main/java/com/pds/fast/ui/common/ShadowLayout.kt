package com.pds.fast.ui.common

import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.BlurMaskFilter.Blur.NORMAL
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Style.FILL
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.FrameLayout
import com.pds.fast.ui.common.assist.dp22px

class ShadowLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
    private var paint: Paint = Paint()
    private val rect = RectF()
    private var maskRadius = 6f.dp22px()
    private var rectRadius = 6f.dp22px()


    override fun onDraw(canvas: Canvas) {
        canvas.save()
        val horizontalPadding = (paddingLeft + paddingRight) / 2f
        val verticalPadding = (paddingTop + paddingBottom) / 2f
        rect.left = horizontalPadding
        rect.top = verticalPadding
        rect.right = measuredWidth - horizontalPadding
        rect.bottom = measuredHeight - verticalPadding
        canvas.drawRoundRect(rect, rectRadius, rectRadius, paint)
        canvas.restore()
        super.onDraw(canvas)
    }

    init {
        clipChildren = false
        clipToPadding = false
        setWillNotDraw(false)
        paint = Paint()
        val a = context.obtainStyledAttributes(attrs, R.styleable.ShadowLayout)
        val color = a.getColor(R.styleable.ShadowLayout_shadowBg, Color.parseColor("#30000000"))
        rectRadius = a.getDimension(R.styleable.ShadowLayout_shadowBgRadius,rectRadius)
        a.recycle()
        paint.color = color
        paint.style = FILL
        paint.maskFilter = BlurMaskFilter(maskRadius, NORMAL)
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }
}