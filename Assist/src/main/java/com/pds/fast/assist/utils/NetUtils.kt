package com.pds.fast.assist.utils

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager

fun isWifiConnected(app: Application): Boolean {
    val cm = app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return run {
        val ni = cm.activeNetworkInfo
        ni != null && ni.type == 1
    }
}