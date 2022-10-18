package com.pds.fast.ui.common.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class CustomView9 : View {
    private var paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var pathPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var path = Path()
    private var path1 = Path()
    private var path2 = Path()
    private var path3 = Path()

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        path.moveTo(50f, 100f)
        path.rLineTo(50f, 100f)
        path.rLineTo(80f, -150f)
        path.rLineTo(100f, 100f)
        path.rLineTo(70f, -120f)
        path.rLineTo(150f, 80f)

        pathPaint.style = Paint.Style.STROKE
        pathPaint.color = Color.WHITE
        paint.color = Color.WHITE
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),800)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 使用 Paint.getFillPath() 获取实际绘制的 Path

        // 第一处：获取 Path
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.strokeWidth = 0f
        // 首先解答第一个问题：「实际 Path」。所谓实际 Path ，指的就是 drawPath() 的绘制内容的轮廓，要算上线条宽度和设置的 PathEffect。
        //
        //默认情况下（线条宽度为 0、没有 PathEffect），原 Path 和实际 Path 是一样的；而在线条宽度不为 0 （并且模式为 STROKE 模式或 FLL_AND_STROKE ），
        // 或者设置了 PathEffect 的时候，实际 Path 就和原 Path 不一样了：
        paint.getFillPath(path, path1)
        canvas.drawPath(path, paint)

        canvas.save()
        canvas.translate(500f, 0f)
        canvas.drawPath(path1, pathPaint)
        canvas.restore()

        canvas.save()
        canvas.translate(0f, 200f)
        paint.style = Paint.Style.STROKE
        // 第二处：设置 Style 为 STROKE 后再获取 Path
        paint.getFillPath(path, path2)
        canvas.drawPath(path, paint)
        canvas.restore()

        canvas.save()
        canvas.translate(500f, 200f)
        canvas.drawPath(path2, pathPaint)
        canvas.restore()

        canvas.save()
        canvas.translate(0f, 400f)
        paint.strokeWidth = 40f
        // 第三处：Style 为 STROKE 并且线条宽度为 40 时的 Path
        paint.getFillPath(path, path3)
        canvas.drawPath(path, paint)
        canvas.restore()

        canvas.save()
        canvas.translate(500f, 400f)
        canvas.drawPath(path3, pathPaint)
        canvas.restore()
    }
}
