package com.pds.fast.ui.common.pet

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import com.pds.fast.ui.common.R

class PetCloseView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : PetItemBaseView(context, attrs) {

    override fun injectOverride(parent: ViewGroup, index: Int): PetItemBaseView = this.apply {
        setUi("#DFC4FF", "#7539BC", R.string.close)
        parent.addView(this)
    }
}