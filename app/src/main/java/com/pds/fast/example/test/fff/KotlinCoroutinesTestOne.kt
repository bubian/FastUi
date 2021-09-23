@file:JvmName(name = "kct")

package com.pds.fast.example

import kotlinx.coroutines.*
import java.io.File


fun tryExp() = runBlocking{
    val job = launch {
        try {
            repeat(1000) { i ->
                println("job: I'm sleeping $i ...")
                delay(500L)
            }
        } finally {
            println("job: I'm running finally")
        }
    }
    delay(1300L) // 延迟一段时间
    println("main: I'm tired of waiting!")
    job.cancelAndJoin() // 取消该作业并且等待它结束
    println("main: Now I can quit.")
}

fun tryOne() = runBlocking{
    val job = launch {
        try {
            repeat(1000) { i ->
                println("job: I'm sleeping $i ...")
                delay(500L)
            }
        } finally {
            withContext(NonCancellable) {
                println("job: I'm running finally")
                delay(1000L)
                println("job: And I've just delayed for 1 sec because I'm non-cancellable")
            }
        }

        // readLines间接调用了use函数，所以不用我们关闭file流
        File("").readLines().forEach{print(it)}
    }
    delay(1300L) // 延迟一段时间
    println("main: I'm tired of waiting!")
    job.cancelAndJoin() // 取消该作业并等待它结束
    println("main: Now I can quit.")
}

//fun main() = runBlocking {
//    val job = launch {
//        repeat(1000) { i ->
//            println("job: I'm sleeping $i ...")
//            delay(500L)
//        }
//    }
//
//    // 放在最后和这里效果不同
//    delay(2300L) // 延迟一段时间
//    println("main: I'm tired of waiting!")
//    // 下面两句调换位置，效果不同
//    // job.cancelAndJoin() // 等同下面两句
//    job.cancel() // 取消该作业
//    job.join() // 等待作业执行结束
//    println("main: Now I can quit.")
//
//    // 资源不可以正常关闭，会抛出异常
//    withTimeout(1300L) {
//        repeat(1000) { i ->
//            println("I'm sleeping $i ...")
//            delay(500L)
//        }
//    }
//
//    // 资源可以正常关闭，不会抛出异常，结果返回null
//    val result = withTimeoutOrNull(1300L) {
//        repeat(1000) { i ->
//            println("I'm sleeping $i ...")
//            delay(500L)
//        }
//        "Done" // 在它运行得到结果之前取消它
//    }
//    println("Result is $result")
//
//}