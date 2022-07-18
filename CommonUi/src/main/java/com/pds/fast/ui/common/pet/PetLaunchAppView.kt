package com.pds.fast.ui.common.pet

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.FrameLayout
import com.pds.fast.ui.common.R
import com.pds.fast.ui.common.Shapes
import com.pds.fast.ui.common.assist.dp2px

class PetLaunchAppView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : PetItemBaseView(context, attrs) {

    override fun injectOverride(parent: FrameLayout, index: Int): PetItemBaseView = this.apply {
        setUi("#FFB1B1", "#9B3B3B", R.string.launch_kuaiyin)
        parent.addView(this)
    }
}