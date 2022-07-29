package com.pds.fast.ui.common.floating.manager

import android.os.Build
import android.provider.Settings
import com.pds.fast.assist.Assist
import com.pds.fast.assist.ScreenUtils


val SCREEN_W = ScreenUtils.getAppScreenWidth(Assist.application)
val SCREEN_H = ScreenUtils.getAppScreenHeight(Assist.application)

/**
 * 是否限制布局边界
 *
 * @return
 */
private fun restrictBorderline() = true

fun shouldDealBackKey() = false

fun onBackPressed() = false

fun isMoreThanHalf(rawX: Int) = rawX >= SCREEN_W / 2

fun canDrawOverlays() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) Settings.canDrawOverlays(Assist.application) else true
