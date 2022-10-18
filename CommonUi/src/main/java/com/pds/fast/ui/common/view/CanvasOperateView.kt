package com.pds.fast.ui.common.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.pds.fast.ui.common.R

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-26 11:44
 * Email：pengdaosong@medlinker.com
 * Description:
 */

class CanvasOperateView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr) {

    private val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.mouth)
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintLine = Paint(Paint.ANTI_ALIAS_FLAG)

    private var w = 0
    private var h = 0

    // private val matrix by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED){}
    var mMatrix : Matrix = Matrix()

    private val camera = Camera()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),2600)
    }

    init {
        paintText.color = Color.WHITE
        paintText.textSize = 40f

        paintLine.color = Color.WHITE
        paintLine.style = Paint.Style.STROKE

        w = bitmap.width
        h = bitmap.height

    }

    override fun onDraw(canvas: Canvas) {
        // 改变这行代码的位置，可以观察到canvas.save和canvas.restore的效果
        // canvas.clipRect(100f,500f,w.toFloat(),400f + h)

        canvas.drawText("原图",100f,30f,paintText)
        canvas.drawBitmap(bitmap,100f,50f,paint)

        canvas.drawText("clipRect(100f,500f,w.toFloat(),400f + h)",100f,475f,paintText)
        canvas.drawRect(100f,500f,100f + w,500f + h,paintLine)
        canvas.drawRect(100f,500f,50f + w,450f + h,paintLine)

        canvas.save()
        // clipPath 和 clipRect用法一样，只是把参数换成了 Path ，所以能裁切的形状更多一些。
        canvas.clipRect(100f,500f,w.toFloat(),400f + h)
        canvas.drawBitmap(bitmap,100f,500f,paint)
        canvas.restore()

        // 去掉canvas.save来感受一下translate的效果
        canvas.drawText("translate(100f,550f) and skew(0f,0.3f)",100f,535f + h,paintText)
        canvas.save()

//        canvas.translate(100f,550f)
//        canvas.skew(0f,0.3f)

        mMatrix.postTranslate(100f,550f)
        mMatrix.postSkew(0f,0.3f)
        canvas.concat(mMatrix)  // 使用 Canvas.setMatrix(matrix) 或 Canvas.concat(matrix) 来把几何变换应用到 Canvas

        canvas.drawBitmap(bitmap,0f,0f + h,paint)
        // 恢复绘制范围
        canvas.restore()

        canvas.drawText("setPolyToPoly",100f,1550f,paintText)

        val pointsSrc: FloatArray = floatArrayOf(100f, 1600f, 100f + w, 1600f, 100f, 1600f + h, 100f + w, 1600f + h)
        val pointsDst: FloatArray = floatArrayOf(
            100f - 10,
            1600f + 50,
            100f + w + 120,
            1600f - 90,
            100f + 20,
            1600f + h + 30,
            100f + w + 20,
            1600f + h + 60
        )

        mMatrix.reset()
        // 通过多点的映射的方式来直接设置变换。「多点映射」的意思就是把指定的点移动到给出的位置，从而发生形变。例如：(0, 0) -> (100, 100) 表示把 (0, 0) 位置的像素移动到 (100, 100) 的位置，这个是单点的映射，单点映射可以实现平移。而多点的映射，就可以让绘制内容任意地扭曲。
        // 参数里，src 和 dst 是源点集合目标点集；srcIndex 和 dstIndex 是第一个点的偏移；pointCount 是采集的点的个数（个数不能大于 4，因为大于 4 个点就无法计算变换了）
        mMatrix.setPolyToPoly(pointsSrc,0,pointsDst,0,4)
        canvas.save()
        canvas.concat(mMatrix)
        canvas.drawBitmap(bitmap,100f,1600f,paint)
        canvas.restore()

        canvas.drawText("Camera三维变换",100f,2050f,paintText)
        canvas.save()
        // Camera 和 Canvas 一样也需要保存和恢复状态才能正常绘制，不然在界面刷新之后绘制就会出现问题
        // 在 Camera 中，相机的默认位置是 (0, 0, -8)（英寸）。8 x 72 = 576，所以它的默认位置是 (0, 0, -576)（像素
        camera.save()
        camera.rotateY(-10f)
        camera.applyToCanvas(canvas)
        camera.restore()

        canvas.drawBitmap(bitmap,100f,2060f,paint)
        canvas.restore()

        // Camera.setLocation(x, y, z) 设置虚拟相机的位置,它的参数的单位不是像素，而是 inch，英寸。英寸和像素的换算单位在 Skia 中被写死为了 72 像素

    }
}