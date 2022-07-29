package com.pds.fast.ui.common.floating.menu

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import com.kuaiyin.player.v2.ui.pet.GlobalPetFloatingView
import com.pds.fast.assist.Assist
import com.pds.fast.ui.common.R

class PetLaunchAppView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : PetItemBaseView(context, attrs) {

    override fun injectOverride(parent: ViewGroup, index: Int): PetItemBaseView = this.apply {
        setUi("#FFB1B1", "#9B3B3B", R.string.launch_kuaiyin)
        parent.addView(this)
    }

    init {
        setOnClickListener {
            val pv = parentView
            if (pv is GlobalPetFloatingView) pv.doDoubleClickLogic()
            setTopApp()
        }
    }

    @SuppressLint("MissingPermission")
    private fun setTopApp() {
        if (!Assist.isRunningForeground(context)) {
            val activityManager = context.getSystemService(Activity.ACTIVITY_SERVICE) as ActivityManager
            activityManager.getRunningTasks(100).forEach {
                if (it.topActivity?.packageName.equals(context.packageName))
                    activityManager.moveTaskToFront(it.id, 0);
            }
        }
    }
}