package com.pds.fast.ui.common.pet

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import com.pds.fast.ui.common.R
import com.pds.fast.ui.common.Shapes
import com.pds.fast.ui.common.assist.dp2px

class PetSignInView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : PetItemBaseView(context, attrs) {

    override fun injectOverride(parent: ViewGroup, index: Int): PetItemBaseView = this.apply {
        setUi("#FFEFB9", "#9E8223", R.string.sign_in)
        parent.addView(this)
    }

}