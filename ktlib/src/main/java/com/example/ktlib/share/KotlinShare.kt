package com.example.ktlib.share

import com.example.ktlib.treadName
import kotlinx.coroutines.*

/***************************创建线程*************************/
//fun main() {
//    thread(start = true,isDaemon = false){
//        println("${treadName()}=====创建一个线程")
//    }
//}

/***************************全局携程*************************/
//fun main() {
//    // CoroutineScope（英文翻译：携程范围，即我们的携程体）
//    GlobalScope.launch{
//        println("${treadName()}======1")
//        delay(1000)
//        println("${treadName()}======2")
//    }
//    println("${treadName()}======3")
//    Thread.sleep(3000)
//}

//fun main(): Unit = runBlocking{
//    GlobalScope.launch (CoroutineName("指定携程名字")){
//        delay(1000)
//        println("${treadName()}======全局携程～")
//    }
//}

//fun main() {
//    GlobalScope.launch(start = CoroutineStart.UNDISPATCHED){
//        println("${treadName()}======我开始执行了～")
//        delay(1000)
//        println("${treadName()}======全局携程～")
//    }
//    println("${treadName()}======我要睡觉～")
//    Thread.sleep(3000)
//}

//fun main() {
//    GlobalScope.launch (Dispatchers.Main){ //不能使用Dispatchers.Main
//        delay(1000)
//        println("${treadName()}======全局携程～")
//    }
//    Thread.sleep(3000)
//}
/***************************创建子携程*************************/
//fun main(): Unit = runBlocking {
//    launch {
//        delay(1000)
//        println("${treadName()}======局部携程～")
//    }
//}

//fun main(): Unit = runBlocking {
//    launch {
//        println("${treadName()}======开始delay～")
//        delay(3000)
//        println("${treadName()}======局部携程～")
//    }
//    println("${treadName()}======最后的倔犟～")
//}

//fun main() {
//
//    runBlocking {
//        println("${treadName()}======start")
//        launch {
//            println("${treadName()}======delay 1s  start")
//            delay(4000)
//            println("${treadName()}======delay 1s end")
//        }
//
//        println("${treadName()}======delay 3s start")
//        delay(2000)
//        println("${treadName()}======delay 3s end")
//    }
//
//    // 延迟，保活进程
//    Thread.sleep(500000)
//}
/***************************子携程和全局携程*************************/
//fun main(): Unit = runBlocking {
//    GlobalScope.launch {
//        delay(500)
//        println("${treadName()}======全局携程")
//    }
//    // 如果没有下面的代码，上面代码不会执行
//    launch {
//        delay(1000L)
//        println("${treadName()}======局部携程")
//    }
//}

//fun main(): Unit = runBlocking {
//    GlobalScope.launch (Dispatchers.IO){ //指定携程上下文
//        delay(1000L)
//        println("${treadName()}======局部携程")
//    }
//    println("${treadName()}======最后的倔犟～")
//    Thread.sleep(2000)
//}

//fun main(): Unit = runBlocking {
//    launch (Dispatchers.IO){ //指定携程上下文
//        delay(1000L)
//        println("${treadName()}======局部携程")
//    }
//    println("${treadName()}======最后的倔犟～")
//}

//fun main(): Unit = runBlocking {
//    repeat(100) {
//        delay(3000)
//        println("${treadName()}======1")
//    }
//    println("${treadName()}======2")
//}

 // 看上去日志是同时打印的
//fun main(): Unit = runBlocking {
//    launch {
//        delay(3000)
//        println("${treadName()}======1")
//
//    }
//    delay(3000)
//    println("${treadName()}======2")
//}

/***************************coroutineScope作用域*************************/
//suspend fun main() {
//    // 声明携程作用域,挂起函数,会释放底层线程用于其他用途,创建一个协程作用域并且在所有已启动子协程执行完毕之前不会结束
//    coroutineScope {
//        // 在该携程作用域启动携程
//        launch {
//            delay(3000L)
//            println("${treadName()}======才开始学习coroutines")
//        }
//    }
//    println("${treadName()}======最后的倔犟～")
//}

fun main() = runBlocking {
    launch {
        delay(2000L)
        println("${treadName()}======Task from runBlocking")
    }
    coroutineScope { // 创建一个协程作用域
        launch {
            delay(8000L)
            println("${treadName()}======Task from nested launch")
        }

        delay(100L)
        println("${treadName()}======Task from coroutine scope") // 这一行会在内嵌 launch 之前输出
    }
    println("${treadName()}======scope is over")
}

//fun main() {
//    val cs = CoroutineScope(Dispatchers.Default)
//    cs.launch {  }
//}

/***************************withContext在指定携程上下文启动携程*************************/
//fun main() = runBlocking {
//    val result = withContext(Dispatchers.Default) {
//        delay(3000)
//        println("${treadName()}======1")
//        30
//    }
//    println("${treadName()}======$result")
//}

/****************************携程超时************************/

//fun main() = runBlocking{
//    withTimeout(1300L) {
//        repeat(1000) { i ->
//            println("I'm sleeping $i ...")
//            delay(500L)
//        }
//    }
//}

//fun main(): Unit = runBlocking{
//    withTimeoutOrNull(1300L) {
//        repeat(1000) { i ->
//            println("I'm sleeping $i ...")
//            delay(500L)
//        }
//    }
//}

/****************************组合挂起函数看KotlinCoreutinesSuspend************************/


/****************************携程join************************/
//fun main() = runBlocking{
//    val job = GlobalScope.launch { // 启动一个新协程并保持对这个作业的引用
//        delay(1000L)
//        println("World!")
//    }
//    println("Hello,")
//    job.join() // 等待直到协程执行结束
//}
/****************************携程取消************************/
//fun main() = runBlocking {
//    val startTime = System.currentTimeMillis()
//    val job = launch(Dispatchers.Default) {
//        var nextPrintTime = startTime
//        var i = 0
//        while (i < 5) { // 一个执行计算的循环，只是为了占用 CPU
//            // 每秒打印消息两次
//            if (System.currentTimeMillis() >= nextPrintTime) {
//                println("job: I'm sleeping ${i++} ...")
//                nextPrintTime += 500L
//            }
//        }
//    }
//    delay(1300L) // 等待一段时间
//    println("main: I'm tired of waiting!")
//    job.cancelAndJoin() // 取消一个作业并且等待它结束
//    println("main: Now I can quit.")
//}

/****************************在 finally 中释放资源************************/

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

/****************************运行不能取消的代码块************************/
//fun main() = runBlocking{
//    val job = launch {
//        try {
//            repeat(1000) { i ->
//                println("job: I'm sleeping $i ...")
//                delay(500L)
//            }
//        } finally {
//            withContext(NonCancellable) {
//                println("job: I'm running finally")
//                delay(1000L)
//                println("job: And I've just delayed for 1 sec because I'm non-cancellable")
//            }
//        }
//    }
//    delay(1300L) // 延迟一段时间
//    println("main: I'm tired of waiting!")
//    job.cancelAndJoin() // 取消该作业并等待它结束
//    println("main: Now I can quit.")
//}

/******************************************************************/
// 日志基本上是同时打印出来了的，是不是感觉是异步，启动100个携程体并加入队列，delay是异步执行
//fun main(): Unit = runBlocking {
//    // repeat(100) {
//        launch(start = CoroutineStart.UNDISPATCHED) {
//            delay(100000)
//            println("${treadName()}======1")
//       // }
//    }
//    println("${treadName()}======2")
//}

