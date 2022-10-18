package com.pds.fast.ui.common.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.pds.fast.ui.common.R

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-25 15:57
 * Email：pengdaosong@medlinker.com
 * Description:
 */
class CustomView2 @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        paintText.color = Color.WHITE
        paintText.textSize = 40f
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),400)
    }

    // 用 Bitmap 来着色
    private var bitmap = BitmapFactory.decodeResource(resources, R.mipmap.batman)
    private val bitmapShader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

    // 第二个 Shader：从上到下的线性渐变（由透明到黑色）
    private val bitmap1 = BitmapFactory.decodeResource(resources, R.mipmap.batman_logo)
    private val shader1 = BitmapShader(bitmap1, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    // 混合着色器
    // ComposeShader() 在硬件加速下是不支持两个相同类型的 Shader 的，所以这里也需要关闭硬件加速才能看到效果
    // PorterDuff.Mode 一共有 17 个，参考：https://developer.android.google.cn/reference/android/graphics/PorterDuff.Mode.html
    // 17中效果请查看"img/PorterDuff/"目录图片
    private val composeShader = ComposeShader(bitmapShader,shader1, PorterDuff.Mode.SRC_OVER)

    // 绘制的图片没有在中间，是因为图片绘制的起点是View的左上角，而圆不是。
    override fun onDraw(canvas: Canvas) {
        canvas.drawText("shader：ComposeShader",50f,65f,paintText)
        paint.shader = composeShader
        canvas.drawCircle(150f, 250f, 150f, paint)
    }
}