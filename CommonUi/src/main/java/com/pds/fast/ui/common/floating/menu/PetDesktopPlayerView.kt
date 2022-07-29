package com.pds.fast.ui.common.floating.menu

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.view.WindowManager
import com.kuaiyin.player.v2.ui.pet.GlobalPetFloatingView
import com.pds.fast.assist.utils.dp2px

class PetDesktopPlayerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : PetItemBaseView(context, attrs) {

    override fun injectOverride(parent: ViewGroup, index: Int): PetItemBaseView = this.apply {
        check()
        parent.addView(this)
    }

    init {
        setOnClickListener {
            if (parentView is GlobalPetFloatingView) (parentView as GlobalPetFloatingView).doDoubleClickLogic()
            val isListenToSongs = "" == text.toString()
            val px = 44f.dp2px()
            var py = (parentView?.height ?: 0) / 2
            val params = parentView?.layoutParams
            if (params is WindowManager.LayoutParams) py += params.y
        }
    }

    fun check() {

    }
}