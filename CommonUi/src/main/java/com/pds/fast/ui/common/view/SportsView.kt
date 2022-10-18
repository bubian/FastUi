package com.pds.fast.ui.common.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-26 15:26
 * Email：pengdaosong@medlinker.com
 * Description:
 */

class SportsView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr) {

    private val arcRectF = RectF(30f,300f,300f,100f)
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    companion object{
        const val PROGRESS = "progress"
        const val COLOR = "color"
    }
    private var progress = 0f
        set(value) {
            Log.e("SportsView","progress = $progress")
            field = value
            invalidate()
        }

    private var color = 0xffff0000
        set(value) {
            Log.e("SportsView","color = $progress")
            field = value
            invalidate()
        }

    init {
        paint.color = Color.WHITE
        paint.strokeWidth = 40f
        paint.style = Paint.Style.STROKE
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),400)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawArc(arcRectF, 135f, progress * 2.7f, false, paint)
    }

}