package com.pds.fast.ui.common.floating.menu

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import com.kuaiyin.player.v2.ui.pet.manager.PetWindowHelper.Companion.addViewToWindow
import com.kuaiyin.player.v2.ui.pet.manager.PetWindowHelper.Companion.removeViewToWindowImmediate
import com.pds.fast.assist.Assist
import com.pds.fast.assist.utils.dp2px
import com.pds.fast.ui.common.R
import com.pds.fast.ui.common.floating.manager.SCREEN_W

class PetTipsView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

    private var isSlideRight: Boolean = false
    private var callbackGone: Runnable? = null

    private var parentView: ViewGroup? = null
    private var isAddToWindow = false
    private var tipsView = TextView(context).apply {
        setTextColor(Color.parseColor("#333333"))
        typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        textSize = 12f
    }

    fun setCallbackGone(callbackGone: Runnable?) {
        this.callbackGone = callbackGone
    }

    fun setSlideRight(isSlideRight: Boolean) {
        this.isSlideRight = isSlideRight

        tipsView.layoutParams = (tipsView.layoutParams as LayoutParams).apply {
            gravity = (if (isSlideRight) Gravity.RIGHT else Gravity.LEFT).or(Gravity.BOTTOM)
        }
    }

    init {
        addView(tipsView)
    }

    fun inject(parent: FrameLayout) = this.apply {
        parentView = parent
    }

    fun doPetTips(content: String?, check: Boolean) {
        if (check && Assist.isRunningForeground(context)) return
        if (content.isNullOrBlank()) return
        removeCallbacks(hideRunnable)
        tipsView.apply {
            setBackgroundResource(if (isSlideRight) R.mipmap.icon_bubble_right else R.mipmap.icon_bubble)
            val endP = if (isSlideRight) 29f.dp2px() else 19f.dp2px()
            setPadding(17f.dp2px(), 8f.dp2px(), endP, 19f.dp2px())
            text = content
        }
        setPadding(if (isSlideRight) 0 else 12f.dp2px(), 0, 0, 0)
        if (!isAddToWindow) addTopsToWindow(content)
        postDelayed(hideRunnable, AUTO_HIDE_TIME)
    }

    private fun addTopsToWindow(content: String) {
        parentView?.let {
            val params = it.layoutParams
            if (params is WindowManager.LayoutParams) {
                val lines = content.split("\n").size
                val th = getContentH(lines)
                val py = params.y - th - 10f.dp2px()
                val px = if (isSlideRight) 0 else 10f.dp2px()
                buildWmParams().apply {
                    isAddToWindow = addViewToWindow(px, py.toInt(), this@PetTipsView, this)
                }
            }
        }
    }

    private fun getContentH(lines: Int): Float {
        val h = getTextH()
        return h * lines + (tipsView.lineSpacingMultiplier * 4f.dp2px() + tipsView.lineSpacingExtra.dp2px()) * (lines - 1)
    }

    private fun buildWmParams() =
        WindowManager.LayoutParams((getContentH(1) + 43f.dp2px()).toInt(), WindowManager.LayoutParams.WRAP_CONTENT).apply {
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else WindowManager.LayoutParams.TYPE_PHONE
            format = PixelFormat.TRANSPARENT
            gravity = if (isSlideRight) Gravity.RIGHT or Gravity.TOP else Gravity.LEFT or Gravity.TOP
            flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            width = SCREEN_W
            height = WindowManager.LayoutParams.WRAP_CONTENT
        }

    fun getTips() = tipsView.text.toString()

    private fun getTextH(): Float {
        val paint = Paint()
        paint.textSize = tipsView.textSize
        paint.typeface = tipsView.typeface
        val fontMetrics = paint.fontMetrics
        return fontMetrics.bottom - fontMetrics.top
    }

    fun doPetTips(content: String?) {
        doPetTips(content, true)
    }

    fun hideTips() {
        removeCallbacks(hideRunnable)
        removeViewToWindowImmediate(this)
        isAddToWindow = false
        callbackGone?.run()
    }

    private val hideRunnable = Runnable {
        removeViewToWindowImmediate(this)
        isAddToWindow = false
        callbackGone?.run()
    }

    companion object {
        private const val AUTO_HIDE_TIME = 3_000L
    }
}