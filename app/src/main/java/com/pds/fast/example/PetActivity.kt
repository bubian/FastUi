package com.pds.fast.example

import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import com.pds.fast.assist.utils.dp22px
import com.pds.fast.ui.common.page.BaseAppCompatActivity
import com.pds.fast.ui.common.pet.PetGlobalFloatingView

class PetActivity : BaseAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val petView = PetGlobalFloatingView(this).apply {
            setBackgroundColor(Color.BLUE)
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        setContentView(petView)
        petView.x = 50f.dp22px()
        petView.y = 200f.dp22px()

    }
}