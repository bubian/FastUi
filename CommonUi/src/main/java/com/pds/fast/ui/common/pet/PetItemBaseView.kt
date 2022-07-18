package com.pds.fast.ui.common.pet

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import com.pds.fast.ui.common.Shapes
import com.pds.fast.ui.common.assist.dp2px
import kotlin.math.cos
import kotlin.math.sin

open class PetItemBaseView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    androidx.appcompat.widget.AppCompatTextView(context, attrs, defStyleAttr) {

    init {
        layoutParams = FrameLayout.LayoutParams(43f.dp2px(), 43f.dp2px())
        typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        gravity = Gravity.CENTER
        textSize = 12f
        visibility = GONE
    }

    fun inject(parent: FrameLayout, catPet: View, index: Int) = this.apply {
        if (index < 0) return@apply
        layoutInitPosition(parent, catPet, index)
        injectOverride(parent, index)
    }

    open fun injectOverride(parent: FrameLayout, index: Int) = this.apply {}

    fun open(catView: View) {

    }

    private fun layoutInitPosition(parent: FrameLayout, catPet: View, index: Int) {

    }

    open fun layoutPosition(parent: FrameLayout, catPet: View, index: Int) {
        val catPetCenterX = catPet.x / 2
        val catPetCenterY = catPet.y / 2
        val rX = cos(Math.PI / 180.toDouble() * 80f) * ringRadius
        val rY = sin(Math.PI / 180.toDouble() * 80f) * ringRadius

        Log.e("PetItemBaseView", "rx=$rX ry=$rY")

//        x = (rX + catPetCenterX).toFloat()
//        y = (catPetCenterY - rY).toFloat()


        val ms = when (index) {
            0 -> 50f.dp2px()
            1 -> 100f.dp2px()
            2 -> 100f.dp2px()
            3 -> 50f.dp2px()
            else -> 0
        }

        val tm = when (index) {
            0 -> 10f.dp2px()
            1 -> 50f.dp2px()
            2 -> 110f.dp2px()
            3 -> 150f.dp2px()
            else -> 0
        }

        layoutParams = (layoutParams as FrameLayout.LayoutParams).apply {
            marginStart = ms
            topMargin = tm
        }
        Log.e("PetItemBaseView", "x=$x y=$y")
    }

    fun setUi(bgColor: String, color: String, textResId: Int) {
        background = Shapes.Builder(Shapes.OVAL)
            .setSolid(Color.parseColor(bgColor))
            .setStroke(2f.dp2px(), Color.WHITE, 0, 0)
            .build()
        text = context.resources.getString(textResId)
        setTextColor(Color.parseColor(color))
    }

    companion object {
        @JvmStatic
        private val ringRadius = 169f.dp2px()
    }
}