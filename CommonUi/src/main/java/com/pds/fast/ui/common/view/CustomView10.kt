package com.pds.fast.ui.common.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class CustomView10 : View {
    private var paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var pathPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var textPath = Path()
    private var text = "Hello HenCoder"

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        paint.textSize = 120f
        paint.color = Color.WHITE
        pathPaint.color = Color.WHITE
        // 「文字的 Path」。文字的绘制，虽然是使用 Canvas.drawText() 方法，但其实在下层，文字信息全是被转化成图形，对图形进行绘制的。
        //  getTextPath() 方法，获取的就是目标文字所对应的 Path 。这个就是所谓「文字的 Path」。
        paint.getTextPath(text, 0, text.length, 50f, 400f, textPath)

        pathPaint.style = Paint.Style.STROKE
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),300)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawText(text, 50f, 200f, paint)
        canvas.drawPath(textPath, pathPaint)
    }
}
