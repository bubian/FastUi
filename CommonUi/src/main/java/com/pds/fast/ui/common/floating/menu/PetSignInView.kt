package com.pds.fast.ui.common.floating.menu

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import com.kuaiyin.player.v2.ui.pet.GlobalPetFloatingView
import com.pds.fast.ui.common.R

class PetSignInView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : PetItemBaseView(context, attrs) {

    override fun injectOverride(parent: ViewGroup, index: Int): PetItemBaseView = this.apply {
        setUi("#FFEFB9", "#9E8223", R.string.sign_in)
        parent.addView(this)
    }

    init {
        setOnClickListener {
            val pv = parentView
            if (pv is GlobalPetFloatingView) pv.doDoubleClickLogic()
        }
    }
}