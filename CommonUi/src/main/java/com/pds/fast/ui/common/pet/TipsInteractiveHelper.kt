package com.pds.fast.ui.common.pet

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.pds.fast.ui.common.assist.HH_MM_SS
import com.pds.fast.ui.common.assist.isRangeDate
import com.pds.fast.ui.common.assist.stamp2Date
import com.pds.fast.ui.common.assist.time2Date


class TipsInteractiveHelper {


    private var foregroundActivityNum = 0
    fun isForeground() = foregroundActivityNum != 0

    private var isFirstOpenPet = false
    private var morningTipsNum = 0

    private var isRegister = false

    private var catPetView: PetGlobalFloatingViewNew? = null

    private val handler = Handler(Looper.getMainLooper())

    fun register(context: Application, catPetView: PetGlobalFloatingViewNew) {
        if (isRegister) return
        isRegister = true
        this.catPetView = catPetView
        context.registerActivityLifecycleCallbacks(callbacks)
        context.registerActivityLifecycleCallbacks(callbacks)
    }

    private val callbacks = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

        }

        override fun onActivityStarted(activity: Activity) {

        }

        override fun onActivityResumed(activity: Activity) {
            handler.removeCallbacks(tipsRunnable)
        }

        override fun onActivityPaused(activity: Activity) {

        }

        override fun onActivityStopped(activity: Activity) {
            handler.removeCallbacks(tipsRunnable)
            handler.postDelayed(tipsRunnable, 1_000)
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

        }

        override fun onActivityDestroyed(activity: Activity) {

        }

    }

    // SystemUtil.isRunningForeground(Apps.getAppContext()
    private val tipsRunnable = Runnable {

        val start = "11:20:00".time2Date(HH_MM_SS)
        val end = "11:60:00".time2Date(HH_MM_SS)

        if (morningTipsNum == 0 && System.currentTimeMillis().stamp2Date(HH_MM_SS)?.isRangeDate(start, end) == true) {
            catPetView?.doMorningTipsLogic()
        }
    }
}