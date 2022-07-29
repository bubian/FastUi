package com.pds.fast.ui.common.floating.menu

import android.content.Context
import android.view.ViewGroup
import com.pds.fast.ui.common.R
import com.pds.fast.ui.common.floating.data.MinePetModel

class PetCloseView constructor(context: Context, val model: MinePetModel) : PetItemBaseView(context) {

    override fun injectOverride(parent: ViewGroup, index: Int): PetItemBaseView = this.apply {
        setUi("#DFC4FF", "#7539BC", R.string.close)
        parent.addView(this)
    }
}