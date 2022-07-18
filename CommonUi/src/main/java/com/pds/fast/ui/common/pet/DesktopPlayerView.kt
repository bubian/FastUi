package com.pds.fast.ui.common.pet

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.pds.fast.ui.common.Shapes
import com.pds.fast.ui.common.assist.dp22px
import com.pds.fast.ui.common.assist.dp2px

class DesktopPlayerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        layoutParams = FrameLayout.LayoutParams(206f.dp2px(), 58f.dp2px()).apply {
            marginStart = 101f.dp2px()
            gravity = Gravity.CENTER_VERTICAL
        }
        background = Shapes.Builder(Shapes.RECTANGLE)
            .setSolid(Color.BLACK)
            .setCornerRadius(30f.dp22px())
            .build()
    }

    fun inject(parent: FrameLayout) = this.apply {
        parent.addView(this)
    }
}