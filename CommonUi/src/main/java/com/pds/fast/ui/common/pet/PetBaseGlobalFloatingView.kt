package com.pds.fast.ui.common.pet

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.pds.fast.assist.utils.dp22px
import com.pds.fast.ui.common.R
import com.pds.fast.ui.common.assist.dp2px
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

open class PetBaseGlobalFloatingView(context: Context) : ViewGroup(context) {

    // 以猫（x，y）距离子圆形view的半径
    protected var radius = 0
    protected var isSlideLeft = false
    private val cap = 40f.dp2px()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (childCount <= 1) return

        for (index in 0 until childCount step 1) {
            val cv = getChildAt(index) ?: continue
            measureChild(cv, widthMeasureSpec, heightMeasureSpec)
        }

        val catView = findViewById<View>(R.id.pet_cat_view) ?: return
        if (catView.visibility == GONE) return
        val catViewW: Int = catView.measuredWidth
        val catViewH: Int = catView.measuredHeight

        val w: Int
        val h: Int
        val catTipsView = findViewById<View>(R.id.pet_tips_view)
        if (null != catTipsView && catTipsView.visibility == VISIBLE) {
            val catTipsViewW: Int = catTipsView.measuredWidth
            val catTipsViewH: Int = catTipsView.measuredHeight
            w = catViewW + catTipsViewW + paddingStart + paddingEnd - cap
            h = catViewH + catTipsViewH + paddingTop + paddingBottom
        } else {
            w = ((catViewW + PetItemBaseView.VIEW_SIZE) / 2 + radius + paddingStart + paddingEnd)
            h = (PetItemBaseView.VIEW_SIZE + 2 * radius + paddingTop + paddingBottom)
        }
        setMeasuredDimension(catViewW.coerceAtLeast(w), catViewH - 20f.dp2px())
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val catView = findViewById<View>(R.id.pet_cat_view)
        if (null == catView || catView.visibility == GONE) return
        val catViewH = catView.measuredHeight
        val catViewW = catView.measuredWidth

        val catL: Int
        val catR: Int
        val catT: Int = (measuredHeight - catViewH) / 2
        val catB: Int = catT + catViewH

        if (isSlideLeft) {
            catL = l
            catR = catL + catView.measuredWidth
        } else {
            catR = r
            catL = measuredWidth - catViewW
        }

        Log.e("66666", "r=$r catR =$catR catL=$catL catT=$catT catB=$catB")
        catView.layout(catL, catT, catR, catB)

        val catTipsView = findViewById<View>(R.id.pet_tips_view)
        if (null != catTipsView && catTipsView.visibility == VISIBLE) {
            if (isSlideLeft) {
                val left = catR - cap
                catTipsView.layout(catR - cap, t, left + catTipsView.measuredWidth, t + catTipsView.measuredHeight)
            } else {
                val right = catL + cap
                catTipsView.layout(right - catTipsView.measuredWidth, t, right, t + catTipsView.measuredHeight)
            }
            return
        }
        var num = 0
        for (index in 0 until childCount step 1) {
            val childView = getChildAt(index)
            if (childView !is PetItemBaseView) continue


            val catCenterX = (catR - catL) / 2
            val catCenterY = (catB - catT) / 2

            val angle = 80 - 40 * num

            var wrapX = 0
            var wrapY = 0
            if (radius > 0) {
                wrapX = if (isSlideLeft) (-20f).dp2px() else (-40f).dp2px()
                wrapY = 25f.dp2px()
            }

            var rx = cos(Math.PI / 180.toDouble() * angle) * radius + wrapX
            val ry = sin(Math.PI / 180.toDouble() * angle) * radius + wrapY


            val cx = (if (isSlideLeft) catCenterX + rx else catCenterX - rx).toInt()
            val cy = (catCenterY - ry).toInt()

            childView.layout(cx, cy, cx + childView.measuredWidth, cy + childView.measuredHeight)
            num++
        }
    }
}