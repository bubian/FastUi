package com.pds.fast.assist.utils

import android.app.Activity
import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context

fun getCurrentActivityName(context: Context): String? {
    val am: ActivityManager = context.getSystemService(Activity.ACTIVITY_SERVICE) as ActivityManager
    val taskInfo: List<ActivityManager.RunningTaskInfo> = am.getRunningTasks(1)
    val componentInfo: ComponentName? = taskInfo[0].topActivity
    return componentInfo?.className
}
