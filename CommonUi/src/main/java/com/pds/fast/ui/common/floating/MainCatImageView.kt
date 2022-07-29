package com.pds.fast.ui.common.floating

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieDrawable
import com.kuaiyin.player.v2.ui.pet.GlobalPetFloatingView
import com.pds.fast.assist.Assist
import com.pds.fast.assist.utils.dp2px
import com.pds.fast.ui.common.R
import com.pds.fast.ui.common.floating.manager.PetCatFloatingManager
import com.pds.fast.ui.common.floating.manager.SCREEN_W
import com.pds.fast.ui.common.floating.manager.isMoreThanHalf

class MainCatImageView(context: Context, val parent: ViewGroup) : LottieAnimationView(context) {

    init {
        LottieCompositionFactory.fromRawResSync(Assist.application, R.raw.cat_move)?.value?.apply {
            setComposition(this)
        }
    }

    private var catPetStates = STATE_CAT_INIT
        set(value) {
            field = value
            when (value) {
                STATE_CAT_SLIDE_RIGHT, STATE_CAT_SLIDE_LEFT -> {
                    setCatImage(R.raw.cat_left)
                    setGravity(if (isSlideLeft()) Gravity.LEFT else Gravity.RIGHT)
                    wrapLocation()
                }
                STATE_CAT_MOVE -> setCatImage(R.raw.cat_move)
                STATE_CAT_OPEN_LEFT, STATE_CAT_OPEN_RIGHT -> {
                    setCatImage(R.mipmap.cat)
                    wrapLocation()
                }
            }
        }

    private fun wrapLocation() {
        if (parent is GlobalPetFloatingView) {
            val params = parent.layoutParams
            if (params is WindowManager.LayoutParams) {
                params.x = when {
                    isSlideLeft() -> -CAT_SLIDE_CAP
                    isSlideRight() -> SCREEN_W - CAT_MENU_OPEN_W + CAT_SLIDE_CAP
                    isMenuOpenLeft() -> 0
                    else -> SCREEN_W - CAT_MENU_OPEN_W
                }
                PetCatFloatingManager.updatePetWindow(params)
            }
        }
    }

    private var imageResIdTmp: Int = 0

    fun inject() = this.apply {
        layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        scaleType = ScaleType.CENTER
        repeatCount = LottieDrawable.INFINITE
        parent.addView(this)
        catPetStates = STATE_CAT_SLIDE_LEFT
    }

    fun setCatImage(imageResId: Int) {
        val sx = if (isSlideRight() || isMenuOpenRight()) -1f else 1f
        if (isSlide() || isMenuOpen()) scaleX = sx
        if (imageResIdTmp == imageResId && (scaleX == sx || imageResId != R.raw.cat_left || imageResId != R.mipmap.cat)) return
        imageResIdTmp = imageResId
        when (imageResId) {
            R.mipmap.cat, R.mipmap.cat_right -> {
//                pauseAnimation()
//                clearAnimation()
                setImageResource(imageResId)
            }
            else -> {
//                setImageDrawable(null)
//                clearAnimation()
                setAnimation(imageResId)
                playAnimation()
            }
        }
    }

    fun resetCatImage() {
        if (isMove() || isMenuOpen()) return
        if (imageResIdTmp == R.raw.cat_left) return
        setCatImage(R.raw.cat_left)
    }

    fun setGravity(g: Int) {
        layoutParams = (layoutParams as FrameLayout.LayoutParams).apply { gravity = g }
    }

    fun getLp() = layoutParams as FrameLayout.LayoutParams
    fun setLp(params: FrameLayout.LayoutParams) {
        layoutParams = params
    }

    companion object {

        @JvmStatic
        val CAT_SLIDE_W = 48f.dp2px() // 左边附贴时图片的宽度
        val CAT_SLIDE_CAP = 38f.dp2px()
        val CAT_MENU_OPEN_W = 91f.dp2px()

        private const val STATE_CAT_INIT = 0
        private const val STATE_CAT_SLIDE_LEFT = 1
        private const val STATE_CAT_SLIDE_RIGHT = 2
        private const val STATE_CAT_MOVE = 3
        private const val STATE_CAT_OPEN_LEFT = 4
        private const val STATE_CAT_OPEN_RIGHT = 5
    }

    fun isSlideLeft() = catPetStates == STATE_CAT_SLIDE_LEFT
    fun isSlideRight() = catPetStates == STATE_CAT_SLIDE_RIGHT
    fun isSlide() = isSlideLeft() || isSlideRight()

    fun isMenuOpenLeft() = catPetStates == STATE_CAT_OPEN_LEFT
    fun isMenuOpenRight() = catPetStates == STATE_CAT_OPEN_RIGHT
    fun isMenuOpen() = isMenuOpenLeft() || isMenuOpenRight()

    fun isMove() = catPetStates == STATE_CAT_MOVE

    fun setMenuOpen(isLeft: Boolean) {
        catPetStates = if (isLeft) STATE_CAT_OPEN_LEFT else STATE_CAT_OPEN_RIGHT
    }

    fun setSlide(isLeft: Boolean) {
        catPetStates = if (isLeft) STATE_CAT_SLIDE_LEFT else STATE_CAT_SLIDE_RIGHT
    }

    fun setMove(mx: Int) {
        scaleX = if (isMoreThanHalf(mx)) -1f else 1f
        catPetStates = STATE_CAT_MOVE
    }
}