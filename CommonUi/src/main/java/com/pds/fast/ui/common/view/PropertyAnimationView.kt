package com.pds.fast.ui.common.view

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import com.pds.fast.ui.common.R

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-26 15:07
 * Email：pengdaosong@medlinker.com
 * Description:
 */
// Android 里动画是有一些分类的：动画可以分为两类：Animation 和 Transition；
// 其中 Animation 又可以再分为 View Animation（补间动画） 和 Property Animation（属性动画） 两类： View Animation 是纯粹基于 framework 的绘制转变
class PropertyAnimationView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.mouth)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),400)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(bitmap,100f,50f,paint)
    }

    fun viewAnimation(){
        val alpha = AnimationUtils.loadAnimation(context, R.anim.alpha_anim)
        startAnimation(alpha)
    }
    fun viewAnimation1(){
        val alpha = AnimationUtils.loadAnimation(context, R.anim.alpha_anim)
        val translate =  AnimationUtils.loadAnimation(context, R.anim.alpha_anim)
        val set = AnimationSet(false)
        set.addAnimation(alpha)
        set.addAnimation(translate)
        startAnimation(set)
    }

    fun propertyAnimation(){
        animate()
            .translationX(500f)
            .withLayer() // 动画开始前setLayerType(LAYER_TYPE_HARDWARE, null)，动画结束后setLayerType(LAYER_TYPE_NONE, null)
    }

}