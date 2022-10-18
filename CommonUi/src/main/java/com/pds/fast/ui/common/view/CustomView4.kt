package com.pds.fast.ui.common.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-25 18:40
 * Email：pengdaosong@medlinker.com
 * Description:
 */

class CustomView4 @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path = Path()
    private val path1 = Path()
    private val path2 = Path()
    private val path3 = Path()

    init {
        paintText.color = Color.WHITE
        paint.color = Color.WHITE
        paintText.textSize = 40f

        path.moveTo(50f, 760f)
        path.rLineTo(500f, 0f)
        path.rLineTo(-150f, 200f)

        path1.moveTo(50f, 1000f)
        path1.rLineTo(500f, 0f)
        path1.rLineTo(-150f, 200f)

        path2.moveTo(50f, 1250f)
        path2.rLineTo(500f, 0f)
        path2.rLineTo(-150f, 200f)

        path2.moveTo(50f, 1500f)
        path2.rLineTo(500f, 0f)
        path2.rLineTo(-150f, 200f)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),1750)
    }

    //  默认情况下，线条宽度为 0，但你会发现，这个时候它依然能够画出线，线条的宽度为 1 像素。那么它和线条宽度为 1 有什么区别呢？
    // 其实这个和后面要讲的一个「几何变换」有关：你可以为 Canvas 设置 Matrix 来实现几何变换（如放大、缩小、平移、旋转），
    // 在几何变换之后 Canvas 绘制的内容就会发生相应变化，包括线条也会加粗，例如 2 像素宽度的线条在 Canvas 放大 2 倍后会被以 4 像素宽度来绘制。
    // 而当线条宽度被设置为 0 时，它的宽度就被固定为 1 像素，就算 Canvas 通过几何变换被放大，它也依然会被以 1 像素宽度来绘制。Google 在文档中把线条宽度为 0 时称作「hairline mode（发际线模式）」。
    override fun onDraw(canvas: Canvas) {
        canvas.drawText("shader：setStrokeWidth",50f,65f,paintText)

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        canvas.drawCircle(150f, 200f, 100f, paint)
        paint.strokeWidth = 5f
        canvas.drawCircle(400f, 200f, 100f, paint)
        paint.strokeWidth = 40f
        canvas.drawCircle(650f, 200f, 100f, paint)

        canvas.drawText("shader：setStrokeCap",50f,365f,paintText)
        // 设置线头的形状。线头形状有三种：BUTT 平头、ROUND 圆头、SQUARE 方头。默认为 BUTT。
        paint.reset()
        paint.strokeWidth = 40f
        paint.color = Color.WHITE
        paint.strokeCap = Paint.Cap.BUTT
        paint.style = Paint.Style.FILL
        canvas.drawLine(50f,450f,500f,450f,paint)
        canvas.drawText("BUTT(默认)",550f,475f,paintText)

        paint.strokeCap = Paint.Cap.ROUND
        canvas.drawLine(50f,525f,500f,525f,paint)
        canvas.drawText("ROUND",550f,550f,paintText)

        paint.strokeCap = Paint.Cap.SQUARE
        canvas.drawLine(50f,600f,500f,600f,paint)
        canvas.drawText("SQUARE",550f,625f,paintText)
        paint.strokeWidth = 2f
        paint.color = Color.RED
        canvas.drawLine(50f,400f,50f,650f,paint)
        canvas.drawLine(500f,400f,500f,650f,paint)

        canvas.drawText("shader：setStrokeJoin",50f,715f,paintText)
        // 设置拐角的形状。有三个值可以选择：MITER 尖角、 BEVEL 平角和 ROUND 圆角。默认为 MITER。

        paint.reset()
        paint.style = Paint.Style.STROKE
        paint.color = Color.WHITE
        paint.strokeWidth = 40f

        // MITER 型连接点有一个额外的规则：当尖角过长时，自动改用 BEVEL 的方式来渲染连接点
        paint.strokeJoin = Paint.Join.MITER
        // setStrokeMiter：对于 setStrokeJoin() 的一个补充，它用于设置 MITER 型拐角的延长线的最大值
//        paint.strokeMiter = 5f
        canvas.drawPath(path, paint)
        canvas.drawText("MITER(默认)",600f,785f,paintText)

        paint.strokeJoin = Paint.Join.BEVEL
        canvas.drawPath(path1, paint)
        canvas.drawText("BEVEL",600f,1025f,paintText)

        //  canvas.translate(300f, 0f)
        paint.strokeJoin = Paint.Join.ROUND
        canvas.drawPath(path2, paint)
        canvas.drawText("ROUND",600f,1285f,paintText)

        paint.strokeJoin = Paint.Join.MITER
        // setStrokeMiter：对于 setStrokeJoin() 的一个补充，它用于设置 MITER 型拐角的延长线的最大值
        paint.strokeMiter = 0.5f
        canvas.drawPath(path3, paint)
        canvas.drawText("MITER(strokeMiter = 0.5)",600f,1525f,paintText)
    }


}