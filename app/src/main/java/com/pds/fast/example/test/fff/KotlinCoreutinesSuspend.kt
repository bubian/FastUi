package com.pds.fast.example.test.fff

import com.pds.fast.example.treadName
import kotlinx.coroutines.*

suspend fun doSuspendOne(): Int {
    delay(2000) // 假设我们在这里做了一些有用的事
    println("${treadName()}======doSuspendOne")
    return 13
}

suspend fun doSuspendTwo(): Int {
    delay(2000) // 假设我们在这里也做了一些有用的事
    println("${treadName()}======doSuspendTwo")
    return 29
}

//fun main() = runBlocking {
//    println("${treadName()}======start")
//    launch {
//        println("${treadName()}======delay 1s  start")
//        delay(3000)
//        println("${treadName()}======delay 1s end")
//    }
//
//    println("${treadName()}======delay 3s start")
//    delay(3000)
//    println("${treadName()}======delay 3s end")
//    // 延迟，保活进程
//    //Thread.sleep(500000)
//}

fun main() {
    runBlocking {
        doSuspendOne()
        println("${treadName()}======111")
        doSuspendTwo()
    }
}

//fun main() = runBlocking{
//    val time = measureTimeMillis {
//        val one = async { doSomethingUsefulOne() }
//        val two = async { doSomethingUsefulTwo() }
//        println("The answer is ${one.await() + two.await()}")
//    }
//    println("Completed in $time ms")
//}

//fun main() = runBlocking{
//    val time = measureTimeMillis {
//        val one = async(start = CoroutineStart.LAZY) { doSomethingUsefulOne() }
//        val two = async(start = CoroutineStart.LAZY) { doSomethingUsefulTwo() }
//        // 执行一些计算
//        one.start() // 启动第一个
//        two.start() // 启动第二个
//        println("The answer is ${one.await() + two.await()}")
//    }
//    println("Completed in $time ms")
//}