package com.pds.fast.example

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.pds.fast.assist.Assist

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
    }
}