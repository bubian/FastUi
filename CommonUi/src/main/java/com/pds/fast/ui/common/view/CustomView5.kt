package com.pds.fast.ui.common.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-25 19:42
 * Email：pengdaosong@medlinker.com
 * Description:
 */
class CustomView5 @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        paint.color = Color.WHITE
        paintText.color = Color.WHITE
        paintText.textSize = 40f
    }
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),300)
    }

    // draw() 是绘制过程的总调度方法。一个 View 的整个绘制过程都发生在 draw() 方法里。前面讲到的背景、主体、子 View 、滑动相关以及前景的绘制，它们其实都是在 draw() 方法里的。
    // onDraw() dispatchDraw() onDrawForeground() 这三个方法在 draw() 中被依次调用
    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
    }

    private val pathEffect =  DashPathEffect(floatArrayOf(10f,5f), 10f)
    // 第1步：背景  它的绘制发生在一个叫 drawBackground() 的方法里，但这个方法是 private 的
    // 第2步：onDraw
    // 第2步：onDrawForeground
    // 第4～5步滑动边缘渐变和滑动条以及前景，放在onDrawForeground
    override fun onDraw(canvas: Canvas) {
        // 设置图像的抖动
        paint.isDither = true
        // 图像在放大绘制的时候，默认使用的是最近邻插值过滤，这种算法简单，但会出现马赛克现象；而如果开启了双线性过滤，就可以让结果图像显得更加平滑
        paint.isFilterBitmap = true

        canvas.drawText("shader：setPathEffect",50f,65f,paintText)
        paint.style = Paint.Style.STROKE
        // PathEffect 分为两类，单一效果的 CornerPathEffect DiscretePathEffect DashPathEffect PathDashPathEffect ，和组合效果的 SumPathEffect ComposePathEffect。
        paint.pathEffect = pathEffect
        canvas.drawCircle(150f, 150f, 50f, paint)
    }
    // API 23 才引入
    override fun onDrawForeground(canvas: Canvas?) {
        super.onDrawForeground(canvas)
    }
}