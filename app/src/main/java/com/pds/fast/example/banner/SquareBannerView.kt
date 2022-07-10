package com.pds.fast.example.banner

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.viewpager.widget.ViewPager
import com.pds.fast.assist.glide.Glider
import com.pds.fast.ui.R
import com.pds.fast.ui.common.Shapes
import com.pds.fast.ui.common.assist.dp22px

class SquareBannerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    RelativeLayout(context, attrs, defStyleAttr) {

    private var bannerImageView: ImageView
    private var playNumBg: View
    private var playNum: TextView
    private var vMask: View
    private var rlBanner: RelativeLayout

    init {
        val view: View = inflate(context, R.layout.item_over_lap_banner, this)
        bannerImageView = view.findViewById(R.id.iv_thumb)
        playNum = view.findViewById(R.id.play_num)
        vMask = view.findViewById(R.id.v_mask)
        playNumBg = view.findViewById(R.id.play_num_bg)
        rlBanner = view.findViewById(R.id.rl_banner)
        playNumBg.background = Shapes.Builder(Shapes.RECTANGLE)
            .setGradientColors(intArrayOf(Color.parseColor("#00000000"), Color.parseColor("#40000000")))
            .setGradientAngle(270f)
            .setCornerRadii(0f, 0f, 8f.dp22px(), 8f.dp22px())
            .build()
    }

    fun bindData(model: BannerModel?, position: Int) {
        model?.let {
            val url = "https://photo.tuchong.com/" + it.userId.toString() + "/f/" + it.imgId.toString() + ".jpg"
            Glider.loadRoundCorner(bannerImageView, url, 8f.dp22px())
            playNum.text = it.num
        }
    }

    fun showMaskView(isShow: Boolean?) {
        val showMask = if (isShow == true) VISIBLE else View.GONE
        val showPlay = if (isShow == true) GONE else VISIBLE
        vMask.visibility = showMask
        playNum.visibility = showPlay
    }

    fun setBannerScale(scale: Float, direction: Int = 0) {
        rlBanner.pivotX = when (direction) {
            OverLapPageTransformer.LEFT -> 0f
            OverLapPageTransformer.RIGHT -> rlBanner.width.toFloat()
            else -> (rlBanner.width / 2).toFloat()
        }
        rlBanner.pivotY = (rlBanner.height / 2).toFloat()
        rlBanner.scaleX = scale
        rlBanner.scaleY = scale
    }

    fun setBannerTranslationZ(translationZ: Float) {
        ViewCompat.setTranslationZ(this, translationZ)
    }

    fun setBannerGravity(rule: Int) {
        val params = rlBanner.layoutParams
        if (params !is LayoutParams) return
        params.addRule(rule, R.id.root_banner)
        rlBanner.layoutParams = params
    }

//    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
//        super.onLayout(changed, l, t, r, b)
//        if (parent is ViewPager) {
//            val viewPager = parent as ViewPager
//            if (viewPager.parent is OverLapBanner) {
//                val banner = viewPager.parent as OverLapBanner
//                val childView = banner.primaryItem
//                if (null != childView) {
//                    val realL = l - childView.left
//                    var position = realL * 1f / measuredWidth
//                    var scale: Float = if (realL >= 0) {
//                        Math.max(0.6f, 1 - position)
//                    } else {
//                        Math.max(0.6f, 1 + position)
//                    }
//                    Log.d("test:", "left = $realL  measuredWidth = $measuredWidth")
//                    alpha = if (Math.abs(realL) < measuredWidth) 1f else 0f
//                    setBannerScale(scale, if (realL < 0) OverLapPageTransformer.LEFT else OverLapPageTransformer.RIGHT)
//                    if (position > 0) {
//                        position = -position
//                    }
//                    setBannerTranslationZ(position)
//                }
//
//            }
//
//        }
//    }
}