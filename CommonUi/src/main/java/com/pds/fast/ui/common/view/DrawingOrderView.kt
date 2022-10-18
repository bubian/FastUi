package com.pds.fast.ui.common.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-26 14:50
 * Email：pengdaosong@medlinker.com
 * Description:
 */

class DrawingOrderView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),10)
    }

    // 在绘制过程中，每一个 ViewGroup 会先调用自己的 onDraw() 来绘制完自己的主体之后再去绘制它的子 View
    override fun onDraw(canvas: Canvas?) {

    }

    // 这里说的「绘制子 View」是通过另一个绘制方法的调用来发生的，这个绘制方法叫做：dispatchDraw()。
    // 也就是说，在绘制过程中，每个 View 和 ViewGroup 都会先调用 onDraw() 方法来绘制主体，再调用 dispatchDraw() 方法来绘制子 View。
    // 只要重写 dispatchDraw()，并在 super.dispatchDraw() 的下面写上你的绘制代码，这段绘制代码就会发生在子 View 的绘制之后，从而让绘制内容盖住子 View 了。
    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
    }


    // 前景的支持是在 Android 6.0（也就是 API 23）才加入的；之前其实也有，不过只支持 FrameLayout，而直到 6.0 才把这个支持放进了 View 类里。
}