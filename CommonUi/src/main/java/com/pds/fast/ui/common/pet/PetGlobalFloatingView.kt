package com.pds.fast.ui.common.pet

import android.animation.ValueAnimator
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import com.pds.fast.ui.common.R
import com.pds.fast.ui.common.assist.dp2px
import kotlin.math.abs

class PetGlobalFloatingView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {


    private var last = 0F
    private val screenW = getScreenWidth(context)
    private val screenH = getScreenHeight(context)

    private val menuCat = ImageView(context).apply {
        layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        scaleType = ImageView.ScaleType.CENTER
        setImageResource(R.mipmap.cat)
        setOnTouchListener(object : OnCombineEventListener() {
            override fun onClick() {
                callOnClick()
            }

            override fun onDoubleClick() {

            }

            override fun onLongPress() {}

            override fun onMove(dx: Int, dy: Int) {
                this@PetGlobalFloatingView.apply {
                    val realX = x + dx
                    val realY = y + dy
                    x = if (realX < 0) 0F else realX.coerceAtMost(screenW - width.toFloat())
                    y = if (realY < 0) 0F else realY.coerceAtMost(screenH - height.toFloat())
                }
            }

            override fun upMove(x: Int, y: Int) {
                if (parent == null) return
                moveSide(x, y)
            }
        })
        addView(this)
    }

    private fun moveSide(rawX: Int, rawY: Int) {
        this@PetGlobalFloatingView.apply {
            val tmpX = x
            val tmpY = y

            val xBy: Float = if (rawX >= screenW / 2) screenW - width.toFloat() - tmpX else -tmpX
            var yBy = 0f

            if (rawY >= screenH - LIMIT || rawY <= LIMIT) yBy = if (rawY <= LIMIT) -tmpY + LIMIT else screenH - LIMIT - height - tmpY

            val animator = ValueAnimator.ofFloat(0f, 1f)
            animator.duration = 300
            animator.interpolator = DecelerateInterpolator()
            // animator.addListener(this)
            val finalY = yBy
            animator.addUpdateListener { animation: ValueAnimator ->
                translationX = tmpX + xBy * animation.animatedValue as Float
                if (finalY != 0f) translationY = tmpY + finalY * animation.animatedValue as Float
            }
            animator.start()
        }
    }

    companion object {
        @JvmStatic
        private val LIMIT = 100f.dp2px()

        @JvmStatic
        fun getScreenWidth(var0: Context): Int {
            return var0.resources.displayMetrics.widthPixels
        }

        @JvmStatic
        fun getScreenHeight(var0: Context): Int {
            return var0.resources.displayMetrics.heightPixels
        }
    }
}