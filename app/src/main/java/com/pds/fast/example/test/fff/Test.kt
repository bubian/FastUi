package com.pds.fast.example.test.fff

import com.pds.fast.example.treadName
import kotlinx.coroutines.*

//fun main(): Unit = runBlocking {
//    val cc = CoroutineScope(Dispatchers.Default)
//    launch {
//        println("${treadName()}======1")
//    }
//    GlobalScope.launch {
//        println("${treadName()}======3")
//    }
//    launch {
//        println("${treadName()}======2")
//    }
//    println("${treadName()}======4")
//    Thread.sleep(2000)
//}

fun main() = runBlocking (Dispatchers.Unconfined){
    val result = withContext(Dispatchers.Default) {
        delay(3000)
        println("${treadName()}======1")
        30
    }
    println("${treadName()}======$result")
}
