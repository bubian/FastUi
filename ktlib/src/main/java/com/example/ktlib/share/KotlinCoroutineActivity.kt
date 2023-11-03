package com.example.ktlib.share

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*

class KotlinCoroutineActivity : AppCompatActivity() {

    private val mainScope = CoroutineScope(Dispatchers.Default)
    private fun treadName(): String = Thread.currentThread().name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runBlocking {
            doSuspendOne()
        }
        lifecycleScope.launch {

        }
    }

    suspend fun doSuspendOne(): Int {
        delay(1000) // 假设我们在这里做了一些有用的事
        Log.d("KotlinCoroutineActivity", "${treadName()}======doSuspendOne")
        return 13
    }
}