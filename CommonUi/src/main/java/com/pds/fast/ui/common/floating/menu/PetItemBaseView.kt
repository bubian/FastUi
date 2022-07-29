package com.pds.fast.ui.common.floating.menu

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.pds.fast.assist.utils.dp2px
import com.pds.fast.ui.common.Shapes
import kotlin.math.cos
import kotlin.math.sin

open class PetItemBaseView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    androidx.appcompat.widget.AppCompatTextView(context, attrs, defStyleAttr) {

    protected var parentView: View? = null

    init {
        layoutParams = FrameLayout.LayoutParams(VIEW_SIZE, VIEW_SIZE)
        typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        gravity = Gravity.CENTER
        textSize = 12f
    }

    fun setUi(bgColor: String, color: String, textResId: Int) {
        background = Shapes.Builder(Shapes.OVAL)
            .setSolid(Color.parseColor(bgColor))
            .setStroke(2f.dp2px(), Color.WHITE, 0, 0)
            .build()
        text = context.resources.getString(textResId)
        setTextColor(Color.parseColor(color))
    }

    fun inject(parent: ViewGroup, catPet: View, index: Int) = this.apply {
        parentView = catPet
        if (index < 0) return@apply
        injectOverride(parent, index)
    }

    open fun injectOverride(parent: ViewGroup, index: Int) = this.apply {}

    fun open(index: Int, progress: Float, isRight: Boolean, isOen: Boolean) {
        var catCenterX = if (isRight) PetMenuView.MENU_SIZE_W - VIEW_SIZE else VIEW_SIZE / 2
        val catCenterY = (PetMenuView.MENU_SIZE_H - VIEW_SIZE) / 2

        val angle = 40 * index - 80
        var rx = cos(Math.PI / 180.toDouble() * angle) * ringRadius * progress
        val rY = sin(Math.PI / 180.toDouble() * angle) * ringRadius * progress

        if (isOen) rx = if (isRight) rx + DP_25 * progress else rx - DP_25 * progress
        if (isOen) catCenterX = if (isRight) catCenterX + DP_25 else catCenterX - DP_25

        this.x = if (isRight) (catCenterX - rx).toFloat() else (catCenterX + rx).toFloat()
        this.y = (catCenterY + rY).toFloat()
    }

    companion object {
        @JvmStatic
        val ringRadius = 84f.dp2px()

        @JvmStatic
        val VIEW_SIZE = 43f.dp2px()

        @JvmStatic
        val DP_25 = 25f.dp2px()
    }
}