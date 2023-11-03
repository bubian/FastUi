package com.example.ktlib.coreutines

import kotlinx.coroutines.*
import kotlinx.coroutines.GlobalScope.coroutineContext

suspend fun doSomethingUsefulOne(): Int {
    delay(1000L) // 假设我们在这里做了一些有用的事
    println("${treadName()}====doSomethingUsefulOne")
    return 13
}

suspend fun doSomethingUsefulTwo(): Int {
    delay(1000L) // 假设我们在这里也做了一些有用的事
    println("${treadName()}====doSomethingUsefulTwo")
    return 29
}

fun somethingUsefulOneAsync() = GlobalScope.async {
    doSomethingUsefulOne()
}

// somethingUsefulTwoAsync 函数的返回值类型是 Deferred<Int>
fun somethingUsefulTwoAsync() = GlobalScope.async {
    doSomethingUsefulTwo()
}

val v2 = GlobalScope.async(CoroutineName("v2coroutine")) {
    delay(1000)
    6
}

//fun main() = runBlocking{
//    // 默认顺序调用
//    val time = measureTimeMillis {
//        val one = doSomethingUsefulOne()
//        println("The answer is $one")
//        val two = doSomethingUsefulTwo()
//        println("The answer is $two")
//    }
//
//    //async 并发
//    val time1 = measureTimeMillis {
//        val one = async { doSomethingUsefulOne() }
//        val two = async { doSomethingUsefulTwo() }
//        println("The answer is ${one.await() + two.await()}")
//    }
//
//    // 参数设置为 CoroutineStart.LAZY 而变为惰性的。 在这个模式下，只有结果通过 await 获取的时候协程才会启动，或者在 Job 的 start 函数调用的时候
//    val time2 = measureTimeMillis {
//        val one = async(start = CoroutineStart.LAZY) { doSomethingUsefulOne() }
//        val two = async(start = CoroutineStart.LAZY) { doSomethingUsefulTwo() }
//        // 执行一些计算
//        one.start() // 启动第一个
//        two.start() // 启动第二个
//        println("The answer is ${one.await() + two.await()}")
//    }
//
//    println("Completed in $time ms")
//}

// 取消始终通过协程的层次结构来进行传递：
//fun main() = runBlocking<Unit> {
//    try {
//        failedConcurrentSum()
//    } catch(e: ArithmeticException) {
//        println("Computation failed with ArithmeticException")
//    }
//}

suspend fun failedConcurrentSum(): Int = coroutineScope {
    val one = async<Int> {
        try {
            delay(Long.MAX_VALUE) // 模拟一个长时间的运算
            42
        } finally {
            println("First child was cancelled")
        }
    }
    val two = async<Int> {
        println("Second child throws an exception")
        throw ArithmeticException()
    }
    one.await() + two.await()
}


// 在不同线程间跳转
// 使用 -Dkotlinx.coroutines.debug
@ObsoleteCoroutinesApi
fun main() {
//    newSingleThreadContext("Ctx1").use { ctx1 ->
//        newSingleThreadContext("Ctx2").use { ctx2 ->
//            runBlocking(ctx1) {
//                println("Started in ctx1")
//                withContext(ctx2) {
//                    println("Working in ctx2")
//                }
//                println("Back to ctx1")
//            }
//        }
//    }
    launchGlobalKK()
    // 协程的 Job 是上下文的一部分，并且可以使用 coroutineContext [Job] 表达式在上下文中检索它
    println("My job is ${coroutineContext[Job]}")
    Thread.sleep(3000L)
}

fun launchGlobalKK() {
    // 全局携程
    val job = GlobalScope.launch { // CoroutineScope（英文翻译：携程范围，即我们的携程体）
        delay(1000L)
        println("${treadName()}======才开始学习coroutines")
        // println("My job is ${GlobalScope.coroutineContext[Job]}") 打印是null
        println("My job is ${coroutineContext[Job]}")
    }
}

val threadLocal = ThreadLocal<String?>() // declare thread-local variable
fun fff() = runBlocking{
    threadLocal.set("main")
    println("Pre-main, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
    val job = launch(Dispatchers.Default + threadLocal.asContextElement(value = "launch")) {
        println("Launch start, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
        yield()
        println("After yield, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
    }
    job.join()
    println("Post-main, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
}



