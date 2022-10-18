package com.pds.fast.ui.common.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.graphics.Canvas.ALL_SAVE_FLAG
import com.pds.fast.ui.common.R

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-25 16:28
 * Email：pengdaosong@medlinker.com
 * Description:
 */
class CustomView3 @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        paintText.color = Color.WHITE
        paintText.textSize = 40f
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),2300)
    }

    // LightingColorFilter :参数里的 mul 和 add 都是和颜色值格式相同的 int 值，其中 mul 用来和目标像素相乘，相乘后的值除以0xff，相当于乘了一个[0, 1]的系数，add 用来和目标像素相加：
    // R' = R * mul.R / 0xff + add.R
    // G' = G * mul.G / 0xff + add.G
    // B' = B * mul.B / 0xff + add.B
    // R' = R * mul.R / 0xff + add.R
    private val lightingColorFilter = LightingColorFilter(0x00ffff, 0x000000) // 红色被移除
    private var bitmap = BitmapFactory.decodeResource(resources, R.mipmap.place)

    // PorterDuffColorFilter: 的作用是使用一个指定的颜色和一种指定的 PorterDuff.Mode 来与绘制对象进行合成。
    // 它的构造方法是 PorterDuffColorFilter(int color, PorterDuff.Mode mode) 其中的 color 参数是指定的颜色， mode 参数是指定的 Mode。
    // 同样也是 PorterDuff.Mode ，不过和 ComposeShader 不同的是，PorterDuffColorFilter 作为一个 ColorFilter，只能指定一种颜色作为源，而不是一个 Bitmap。
    private val porterDuffColorFilter = PorterDuffColorFilter(0x00ffff, PorterDuff.Mode.SRC_OVER)

    // ColorMatrixColorFilter 使用一个 ColorMatrix 来对颜色进行处理。 ColorMatrix 这个类，内部是一个 4x5 的矩阵：
    // [ a, b, c, d, e,
    //  f, g, h, i, j,
    //  k, l, m, n, o,
    //  p, q, r, s, t ]

    // 计算规则

    // R’ = a*R + b*G + c*B + d*A + e;
    // G’ = f*R + g*G + h*B + i*A + j;
    // B’ = k*R + l*G + m*B + n*A + o;
    // A’ = p*R + q*G + r*B + s*A + t;
    private val colorMatrixColorFilter = ColorMatrixColorFilter(ColorMatrix())

    private fun colorFilter(canvas: Canvas){
        // 模拟简单的光照效果
        canvas.drawText("shader：LightingColorFilter",50f,65f,paintText)
        paint.colorFilter = lightingColorFilter
        canvas.drawBitmap(bitmap, 0f,100f,paint)
    }

    // Xfermode 指的是你要绘制的内容和 Canvas 的目标位置的内容应该怎样结合计算出最终的颜色。
    // 但通俗地说，其实就是要你以绘制的内容作为源图像，以 View 中已有的内容作为目标图像，选取一个 PorterDuff.Mode 作为绘制内容的颜色处理方案

    private var xfermode1: Xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC)
    // 保留覆盖源像素的目标像素，丢弃其余的源像素和目标像素
    private var xfermode2: Xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
    private var xfermode3: Xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
    private val bitmap1: Bitmap = BitmapFactory.decodeResource(resources, R.mipmap.batman)
    private val bitmap2: Bitmap = BitmapFactory.decodeResource(resources, R.mipmap.batman_logo)

    // 要想使用 setXfermode() 正常绘制，必须使用离屏缓存 (Off-screen Buffer) 把内容绘制在额外的层上，再把绘制好的内容贴回 View 中
    // 使用离屏缓冲有两种方式：
    // Canvas.saveLayer():saveLayer(): 可以做短时的离屏缓冲。使用方法很简单，在绘制代码的前后各加一行代码，在绘制之前保存，绘制之后恢复
    // View.setLayerType(): View.setLayerType() 是直接把整个 View 都绘制在离屏缓冲中。 setLayerType(LAYER_TYPE_HARDWARE) 是使用 GPU 来缓冲， setLayerType(LAYER_TYPE_SOFTWARE) 是直接直接用一个 Bitmap 来缓冲。
    // 使用 Xfermode 来绘制的内容，除了注意使用离屏缓冲，还应该注意控制它的透明区域不要太小，要让它足够覆盖到要和它结合绘制的内容，否则得到的结果很可能不是你想要的。我用图片来具体说明一下：查看：img/PorterDuff/Xfermode.jpg
    // 参考：https://developer.android.google.cn/guide/topics/graphics/hardware-accel.html
    private fun setXfermode(canvas: Canvas){
        val saved = canvas.saveLayer(null, null, ALL_SAVE_FLAG)
        canvas.drawText("shader：PorterDuffXfermode",50f,1500f,paintText)
        paint.reset()
        // 目标像素
        canvas.drawBitmap(bitmap1, 0f, 1500f, paint)
        paint.xfermode = xfermode1
        // 源像素
        canvas.drawBitmap(bitmap2, 0f, 1500f, paint)
        paint.xfermode = null

        canvas.drawBitmap(bitmap1, bitmap1.width + 100f, 1500f, paint)
        paint.xfermode = xfermode2
        canvas.drawBitmap(bitmap2, bitmap1.width + 100f, 1500f, paint)
        paint.xfermode = null

        canvas.drawBitmap(bitmap1, 0f, bitmap1.height + 1500f, paint)
        paint.xfermode = xfermode3
        canvas.drawBitmap(bitmap2, 0f, bitmap1.height + 1500f, paint)
        paint.xfermode = null

        canvas.restoreToCount(saved)
    }
    override fun onDraw(canvas: Canvas) {
        colorFilter(canvas)
        setXfermode(canvas)
    }
}