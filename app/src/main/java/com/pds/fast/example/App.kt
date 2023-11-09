package com.pds.fast.example

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.pds.fast.assist.Assist
import com.pds.fast.assist.ext.str
import com.pds.fast.ui.R

class App : MultiDexApplication() {
    companion object {
        lateinit var app: Application
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        app = this
        Assist.init(this)
        Log.e("113333", "333=${R.string.wake_up_tip111.str(null)}")
    }
}