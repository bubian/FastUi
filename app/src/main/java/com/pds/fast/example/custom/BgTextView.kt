package com.pds.fast.example.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.AttributeSet

class BgTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : androidx.appcompat.widget.AppCompatTextView(context, attrs) {

    var mMatrix: Matrix = Matrix()

    override fun draw(canvas: Canvas) {
        canvas.concat(matrix)
        super.draw(canvas)
    }

}