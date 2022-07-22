package com.pds.fast.ui.common.pet

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import com.pds.fast.ui.common.R
import com.pds.fast.ui.common.assist.dp2px

class PetTipsView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    androidx.appcompat.widget.AppCompatTextView(context, attrs, defStyleAttr) {
    init {
        visibility = GONE
        setTextColor(Color.parseColor("#333333"))
        typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        text = "主人今天还没有签到哦\n双击我去快速签到"
        layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            bottomMargin = 145f.dp2px()
        }
    }

    fun inject(parent: FrameLayout) = this.apply {
        parent.addView(this)
    }

    fun doPetTips(isSlideRight: Boolean) {
        removeCallbacks(hideRunnable)
        setBackgroundResource(if (isSlideRight) R.mipmap.icon_bubble_right else R.mipmap.icon_bubble)
        setPadding(17f.dp2px(), 8f.dp2px(), 17f.dp2px(), 19f.dp2px())
//        catView.layoutParams = (catView.layoutParams as FrameLayout.LayoutParams).apply {
//            if (isSlideRight) {
//                marginStart = 113f.dp2px()
//                marginStart = 0
//            } else {
//                marginStart = 13f.dp2px()
//                marginEnd = 0
//            }
//        }
        visibility = VISIBLE
        postDelayed(hideRunnable, AUTO_HIDE_TIME)
    }

    fun hideTips() {
        visibility = GONE
    }

    private val hideRunnable = Runnable { visibility = GONE }

    companion object {
        private const val AUTO_HIDE_TIME = 3_000L
    }
}