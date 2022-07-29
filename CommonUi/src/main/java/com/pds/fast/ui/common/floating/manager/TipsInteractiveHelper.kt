package com.pds.fast.ui.common.floating.manager

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.view.View
import com.pds.fast.ui.common.floating.MainCatImageView
import com.pds.fast.ui.common.floating.MainCatImageView.Companion.CAT_SLIDE_CAP
import com.pds.fast.ui.common.floating.menu.PetTipsView
import com.pds.fast.ui.common.floating.other.SimpleALCallbacks
import com.pds.fast.ui.common.floating.other.SimpleAnimatorListener
import com.pds.fast.assist.Assist
import com.pds.fast.assist.utils.HH_MM_SS
import com.pds.fast.assist.utils.isRangeDate
import com.pds.fast.assist.utils.stamp2Date
import com.pds.fast.assist.utils.time2Date
import com.pds.fast.ui.common.R
import com.pds.fast.ui.common.floating.data.MinePetModel
import kotlin.random.Random

class TipsInteractiveHelper(private val petTipsView: PetTipsView, private val menuCat: MainCatImageView, private val model: MinePetModel) {

    private var isFirstOpenPet = false
    private var isRegister = false
    private var isTipsMorning = false
    private var isTipsListenSong = false
    private var isTipsSign = false
    private var isCanTips = true
    private var isRemoveMsg = false
    private var isLastPlayerShow = false
    private var isRunningForeground = false
    private var isInAnimation = false
    private var lastRandomNum = -1

    fun isInAnimation() = isInAnimation

    private val handler = Handler(Looper.getMainLooper())

    init {
        petTipsView.setCallbackGone {
            menuCat.resetCatImage()
        }
    }

    fun register() {
        if (isRegister) return
        isRegister = true
        Assist.application.unregisterActivityLifecycleCallbacks(callbacks)
        Assist.application.registerActivityLifecycleCallbacks(callbacks)
    }

    private val callbacks = object : SimpleALCallbacks() {

        override fun onActivityResumed(activity: Activity) {
            handler.removeCallbacksAndMessages(null)
            petTipsView.hideTips()
            if (!isRunningForeground) {
                isRunningForeground = true
            }
        }

        override fun onActivityStopped(activity: Activity) {
            handler.removeCallbacksAndMessages(null)
            handler.postDelayed(tipsRunnable, 500L)
        }
    }

    private val tipsRunnable = Runnable {
        isRunningForeground = Assist.isRunningForeground(Assist.application)
        if (!isRunningForeground) {
            if (isFirstOpenPet) doFirstOpenCatPetLogic(model)
            if (isNeedMorningTips()) {
                if (isFirstOpenPet) handler.postDelayed(morningTipsRunnable, AUTO_TIME_CAP)
                else callbackMorningTips()
            } else if (isNeedListenSongTips()) {
                if (isFirstOpenPet) handler.postDelayed(listenSongTipsRunnable, AUTO_TIME_CAP)
                else callbackListenSongTips()
            } else if (isNeedSignTips()) {
                if (isFirstOpenPet) handler.postDelayed(signTipsRunnable, AUTO_TIME_CAP)
                else callbackSignTips()
            }
            isFirstOpenPet = false
        }
    }

    fun setCatPetStates() {
        isCanTips = true
        if (menuCat.isSlide()) {
            petTipsView.setSlideRight(!menuCat.isSlideLeft())
            if (isRemoveMsg) {
                handler.removeCallbacks(tipsRunnable)
                handler.postDelayed(tipsRunnable, DELAY)
            }
        } else {
            isCanTips = false
            petTipsView.hideTips()
        }
    }

    fun doMenuClose() {
        if (isRemoveMsg) {
            handler.removeCallbacks(tipsRunnable)
            handler.postDelayed(tipsRunnable, DELAY)
        }
        isRemoveMsg = false
    }

    fun doClickTipsLogic() {
        if (Assist.isRunningForeground(Assist.application)) return
        if (isInAnimation) return
        val tips = petTipsView.getTips()
        val petTipsV = petTipsView.isAttachedToWindow
        if (petTipsV && tips == "") return
        if (tips.contains("主人别挠了") && petTipsV) {
            petTipsView.hideTips()
            doPetAnimation()
            return
        }
        model.cfg?.clickNotice?.let {
            val len = it.size
            if (len < 0) return
            var rn = Random.nextInt(0, len)
            if (lastRandomNum == rn) rn = if (rn in 0 until (len - 1)) rn + 1 else rn - 1
            lastRandomNum = rn
            petTipsView.doPetTips(it.getOrNull(rn), false)
        }
    }

    private var pp: Int = 0
    private fun tmpTest() {
        pp = if (pp == R.raw.cat_lie) R.raw.cat_wave else R.raw.cat_lie
        menuCat.setCatImage(pp)
    }

    private var lastRandomLocation = -1
    private fun doPetAnimation() {
        PetCatFloatingManager.getPetView()?.let { it ->
            val catW = menuCat.width.toFloat()
            val an = if (menuCat.isSlideLeft()) ObjectAnimator.ofFloat(menuCat, "translationX", -catW)
            else ObjectAnimator.ofFloat(menuCat, "translationX", catW)
            an.duration = 150

            val len = 6
            val split = SCREEN_H / 3
            val cap = (split - it.height) / 2
            var r = Random.nextInt(0, len)
            val isMoveRight = r >= 3
            if (lastRandomLocation == r) r = if (r in 0 until (len - 1)) r + 1 else r - 1
            lastRandomLocation = r

            an.addListener(object : SimpleAnimatorListener() {
                override fun onAnimationEnd(animation: Animator?) {
                    val tx: Float?
                    val mx = if (isMoveRight) {
                        menuCat.visibility = View.INVISIBLE
                        tx = if (menuCat.isSlideLeft()) 2 * catW else null
                        SCREEN_W - it.width + CAT_SLIDE_CAP
                    } else {
                        tx = if (menuCat.isSlideLeft()) null else -2 * catW
                        -CAT_SLIDE_CAP
                    }
                    it.doMove(mx, cap + (r % 3) * split)
                    tx?.apply { menuCat.translationX = this }
                    menuCat.visibility = View.VISIBLE
                }
            })

            val an1 = if (isMoveRight) ObjectAnimator.ofFloat(menuCat, "translationX", 1f)
            else ObjectAnimator.ofFloat(menuCat, "translationX", 1f)

            an1.duration = 200
            an1.addListener(object : SimpleAnimatorListener() {
                override fun onAnimationEnd(animation: Animator?) {
                    isInAnimation = false
                    if (isRemoveMsg) handler.postDelayed(tipsRunnable, DELAY)
                }

                override fun onAnimationCancel(animation: Animator?) {
                    isInAnimation = false
                }
            })

            isInAnimation = true
            val set = AnimatorSet()
            set.playSequentially(an, an1)
            set.start()
        }
    }

    private fun doFirstOpenCatPetLogic(minePetModel: MinePetModel) {
        val backDesktopNotice = minePetModel.cfg?.backDesktopNotice
        if (!backDesktopNotice.isNullOrBlank()) petTipsView.doPetTips(backDesktopNotice)
    }

    private fun isNeedMorningTips(): Boolean {
        val s = model.cfg?.amStart
        val e = model.cfg?.amEnd
        if (s.isNullOrBlank() || e.isNullOrBlank()) return false

        val start = s.time2Date(HH_MM_SS)
        val end = e.time2Date(HH_MM_SS)
        val p = System.currentTimeMillis().stamp2Date(HH_MM_SS)?.isRangeDate(start, end) == true && isScreenOn() && !isScreenLocked()
        // 本应该执行的tips，但是由于其它原因导致没有执行，isRemoveMsg用于下次是否需要重新检测
        if (p && !isCanTips) isRemoveMsg = true
        return p && isCanTips && menuCat.isSlide()
    }

    private fun isNeedListenSongTips(): Boolean {
        val s = model.cfg?.listenMusicStart
        val e = model.cfg?.listenMusicEnd
        if (s.isNullOrBlank() || e.isNullOrBlank()) return false
        val start = s.time2Date(HH_MM_SS)
        val end = e.time2Date(HH_MM_SS)
        val p = System.currentTimeMillis().stamp2Date(HH_MM_SS)?.isRangeDate(start, end) == true && !isTipsListenSong
                && isScreenOn() && !isScreenLocked()

        if (p && !isCanTips) isRemoveMsg = true
        return p && isCanTips && menuCat.isSlide()
    }

    private fun isNeedSignTips(): Boolean {
        val p = !isTipsSign && isScreenOn() && !isScreenLocked()
        if (p && !isCanTips) isRemoveMsg = true
        return p && isCanTips && menuCat.isSlide()
    }

    private val morningTipsRunnable = Runnable { if (isNeedMorningTips()) callbackMorningTips() }
    private val listenSongTipsRunnable = Runnable { if (isNeedListenSongTips()) callbackListenSongTips() }
    private val signTipsRunnable = Runnable { if (isNeedSignTips()) callbackSignTips() }

    private fun callbackMorningTips() {
        if (isInAnimation) return
        isTipsMorning = true
        if (!isTipsListenSong && isNeedListenSongTips()) {
            handler.removeCallbacksAndMessages(null)
            handler.postDelayed(listenSongTipsRunnable, AUTO_TIME_CAP)
        } else if (!isTipsSign && isNeedSignTips()) {
            handler.removeCallbacksAndMessages(null)
            handler.postDelayed(signTipsRunnable, AUTO_TIME_CAP)
        }
        petTipsView.doPetTips("就斤斤计较")
        menuCat.setCatImage(R.raw.cat_wave)
    }

    private fun callbackListenSongTips() {
        if (isInAnimation) return
        isTipsListenSong = true

        petTipsView.doPetTips("解决哈哈哈哈哈")
        menuCat.setCatImage(R.raw.cat_lie)
        if (!isTipsSign && isNeedSignTips()) {
            handler.removeCallbacksAndMessages(null)
            handler.postDelayed(signTipsRunnable, AUTO_TIME_CAP)
        }
    }

    private fun callbackSignTips() {
        if (isInAnimation) return
        isTipsSign = true
        petTipsView.doPetTips("")
    }

    private fun isScreenOn(): Boolean {
        val pm: PowerManager = Assist.application.getSystemService(Context.POWER_SERVICE) as PowerManager
        //如果为true，则表示屏幕“亮”了，否则屏幕“暗”了。
        return pm.isScreenOn
    }

    private fun isScreenLocked(): Boolean {
        val mKeyguardManager = Assist.application.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        return mKeyguardManager.isKeyguardLocked;
    }

    fun destroy() {
        Assist.application.unregisterActivityLifecycleCallbacks(callbacks)
        isRegister = false
        handler.removeCallbacksAndMessages(null)
        petTipsView.hideTips()
    }

    companion object {
        private const val AUTO_TIME_CAP = 180_000L // 3分钟
        private const val DELAY = 3_000L
    }
}