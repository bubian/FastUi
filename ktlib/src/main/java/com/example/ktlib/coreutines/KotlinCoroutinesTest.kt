package com.example.ktlib.coreutines

import kotlinx.coroutines.*
import kotlin.concurrent.thread

fun treadName(): String = Thread.currentThread().name

fun launchGlobal() {
    // 全局携程
    val job = GlobalScope.launch(Dispatchers.Default) { // CoroutineScope（英文翻译：携程范围，即我们的携程体）
        delay(1000L)
        println("${treadName()}======才开始学习coroutines")
    }
}

fun launchGlobalOne() {
    GlobalScope.launch { // CoroutineScope（英文翻译：携程范围，即我们的携程体）
        Thread.sleep(1000)
        println("${treadName()}======使用姿势不对哟～")
    }
}

//fun launchGlobalError() {
//    thread (start = true){
//        delay(1000L)
//        println("${treadName()}======才开始学习coroutines")
//    }
//}

fun launchTread() {
    val thread = thread(start = true) {
        Thread.sleep(1000)
        println("${treadName()}======thread启动")
    }
}

fun blockTest() = runBlocking<Boolean> {
    // 非阻塞
    delay(5000L)
    true
}

suspend fun joinMe() = GlobalScope.launch {
    delay(5000)
    println("${treadName()}======我加入，但是我要睡一会～")
}.join()


fun main(): Unit = runBlocking {
    launch {
        println("${treadName()}======launch～")
    }
    println("${treadName()}======最后的倔犟～")
}
//fun main() = runBlocking {
//    val job = launch {
//        try {
//            repeat(1000) { i ->
//                println("job: I'm sleeping $i ...")
//                delay(500L)
//            }
//        } finally {
//            println("job: I'm running finally")
//        }
//    }
//    delay(1300L) // 延迟一段时间
//    println("main: I'm tired of waiting!")
//    job.cancelAndJoin() // 取消该作业并且等待它结束
//    println("main: Now I can quit.")
//}

//
//fun main(): Unit = runBlocking {
//    GlobalScope.launch {
//        delay(2000L)
//        println("${treadName()}======全局携程")
//    }
//    // 如果没有下面的代码，上面代码不会执行
//    launch {
//        delay(1000L)
//        println("${treadName()}======局部携程")
//    }
//}


//suspend fun main() {
//    launchGlobal()
//    println("--------我启动了一个全局携程")
//    blockTest()
//    println("--------我换一种姿势启动线上")
//    launchTread()
//    println("--------我要和你们同流合污～")
//    joinMe()
//    Thread.sleep(20000L)
//    println("${treadName()}======我是最后的倔犟～")
//}

// 阻塞当前线程来等待，常规函数,在所有已启动子协程执行完毕之前不会结束
//fun main(): Unit = runBlocking {
//    GlobalScope.launch {
//        delay(1000L)
//        println("${treadName()}======全局携程")
//    }
//    // 如果没有下面的代码，上面代码不会执行
//    launch {
//        delay(1000L)
//        println("${treadName()}======局部携程")
//    }
//}

// 这是你的第一个挂起函数
//suspend fun doWorld() {
//    delay(1000L)
//    println("World!")
//}

//fun CoroutineScope.doWorld(){
//    launch {
//
//    }
//}

//suspend fun main() {
//    // 声明携程作用域,挂起函数,会释放底层线程用于其他用途,创建一个协程作用域并且在所有已启动子协程执行完毕之前不会结束
//    coroutineScope {
//        // 在改携程作用域启动携程
//        launch {
//            delay(3000L)
//            println("${treadName()}======才开始学习coroutines")
//        }
//    }
//
//    println("${treadName()}======最后的倔犟～")
//}

//fun main() = runBlocking { // this: CoroutineScope
//    launch {
//        println("${treadName()}======Task from runBlocking")
//    }
//    println("${treadName()}======scope is over")
//}

//fun main() = runBlocking {
//    repeat(100_000) { // 启动大量的协程
//        delay(1000L)
//        launch {
//            println("${treadName()}======才开始学习coroutines")
//        }
//    }
//}

//fun main() = runBlocking {
//    repeat(100_000) { // 启动大量的协程
//        launch {
//            delay(1000L)
//            println("${treadName()}======才开始学习coroutines")
//        }
//    }
//}

//suspend fun main() {
//    GlobalScope.launch {
//        repeat(1000) { i ->
//            println("I'm sleeping $i ...")
//            delay(500L)
//        }
//    }
//    delay(1300L) // 在延迟后退出
//}

//fun main(): Unit = runBlocking{
//    // 有时我们需要在协程上下文中定义多个元素。我们可以使用 + 操作符来实现。 比如说，我们可以显式指定一个调度器来启动协程并且同时显式指定一个命名：
//    launch(Dispatchers.Default + CoroutineName("test")) {
//        println("I'm working in thread ${Thread.currentThread().name}")
//    }
//}

