package com.pds.fast.ui.common.pet

import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import androidx.core.view.marginTop
import androidx.core.view.setPadding
import com.pds.fast.ui.common.R
import com.pds.fast.ui.common.assist.dp2px


class CatImageView(context: Context) : androidx.appcompat.widget.AppCompatImageView(context) {

    fun inject(parent: ViewGroup) = this.apply {
        id = R.id.pet_cat_view
        // setBackgroundColor(Color.RED)
        scaleType = ScaleType.CENTER
        setImageResource(R.mipmap.icon_cat_left)
        setPadding(0, 10f.dp2px(), 0, 0)
        parent.addView(this, ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 217f.dp2px()))
    }
}