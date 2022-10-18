package com.pds.fast.ui.common.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.pds.fast.ui.common.R

class CustomView8 : View {
    private var paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var bitmap: Bitmap
    private var maskFilter1: MaskFilter = BlurMaskFilter(50f, BlurMaskFilter.Blur.NORMAL)
    private var maskFilter2: MaskFilter = BlurMaskFilter(50f, BlurMaskFilter.Blur.INNER)
    private var maskFilter3: MaskFilter = BlurMaskFilter(50f, BlurMaskFilter.Blur.OUTER)
    private var maskFilter4: MaskFilter = BlurMaskFilter(50f, BlurMaskFilter.Blur.SOLID)

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )


    init {
        paint.color = Color.WHITE
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        bitmap = BitmapFactory.decodeResource(resources, R.drawable.what_the_fuck)
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),1000)
    }

    /**
     * MaskFilter 有两种： BlurMaskFilter 和 EmbossMaskFilter。
     *
     * 到现在已经有两个 setXxxFilter(filter) 了。前面有一个 setColorFilter(filter) ，是对每个像素的颜色进行过滤；而这里的 setMaskFilter(filter) 则是基于整个画面来进行过滤
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        paint.maskFilter = maskFilter1
        canvas.drawBitmap(bitmap, 100f, 50f, paint)

        paint.maskFilter = maskFilter2
        canvas.drawBitmap(bitmap, (bitmap.width + 200).toFloat(), 50f, paint)

        // 设置的是在绘制层上方的附加效果。
        paint.maskFilter = maskFilter3
        canvas.drawBitmap(bitmap, 100f, (bitmap.height + 100).toFloat(), paint)

        paint.maskFilter = maskFilter4
        canvas.drawBitmap(
            bitmap,
            (bitmap.width + 200).toFloat(),
            (bitmap.height + 100).toFloat(),
            paint
        )
    }
}
