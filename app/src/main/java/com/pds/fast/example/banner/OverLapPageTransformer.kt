package com.pds.fast.example.banner

import android.view.View

class OverLapPageTransformer : BasePageTransformer() {
    companion object {
        const val LEFT = 1
        const val CENTER = 2
        const val RIGHT = 2
    }

    private var scaleValue = 0.6f
    private val mask = 0f
    private val mask1 = 0.08f

    override fun handleInvisiblePage(view: View, position: Float) {
        if (view !is SquareBannerView) return
        view.alpha = if (position > 1 || position < -1) 0f else 1f
        var tmpPosition = position
        if (position > 0) tmpPosition = -position
        view.setBannerScale(0.6f, if (position < -1) LEFT else RIGHT)
        view.showMaskView(true)
        view.setBannerTranslationZ(tmpPosition)
    }

    override fun handleLeftPage(view: View, position: Float) {
        if (view !is SquareBannerView) return
        view.alpha = 1f
        view.showMaskView(position < -mask1 || position > mask1)
        val scale = Math.max(scaleValue, 1 + position)
        view.setBannerScale(scale, LEFT)
        view.setBannerTranslationZ(position)

    }

    override fun handleRightPage(view: View, position: Float) {
        if (view !is SquareBannerView) return
        view.alpha = 1f
        view.showMaskView(position < -mask1 || position > mask1)
        val scale = Math.max(scaleValue, 1 - position)
        view.setBannerScale(scale, RIGHT)
        view.setBannerTranslationZ(-position)
        // view.setBannerGravity(RelativeLayout.ALIGN_PARENT_END)
    }
}