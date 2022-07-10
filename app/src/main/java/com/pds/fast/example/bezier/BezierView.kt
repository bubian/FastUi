package com.pds.fast.example.bezier

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import com.pds.fast.assist.utils.dp22px

class BezierView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paint1 = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path = Path()

    init {
        paint.color = Color.RED
        paint1.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = 1f.dp22px()
    }

    override fun onDraw(canvas: Canvas) {

        path.reset()
        path.moveTo(60f.dp22px(), 0f)
        path.quadTo(70f.dp22px(), 50f.dp22px(), 60f.dp22px(), 100f.dp22px())

        path.lineTo(220f.dp22px(), 100f.dp22px())
        path.quadTo(290f.dp22px(), 50f.dp22px(), 220f.dp22px(), 0f)
        path.lineTo(60f.dp22px(), 0f)
        canvas.drawCircle(0f, 50f.dp22px(), 80f.dp22px(), paint1)

        canvas.drawPath(path, paint)
    }
}