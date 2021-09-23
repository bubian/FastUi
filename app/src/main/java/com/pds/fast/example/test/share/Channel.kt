package com.pds.fast.example.test.share

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val channel = Channel<Int>()
    launch {
        // 这里可能是消耗大量 CPU 运算的异步逻辑，我们将仅仅做 5 次整数的平方并发送
        for (x in 1..5) channel.send(x * x)
    }
    // 这里我们打印了 5 次被接收的整数：
    repeat(5) { println(channel.receive()) }
    println("Done!")
}

/*****************关闭与迭代通道********************/

//fun main() = runBlocking {
//    val channel = Channel<Int>()
//    launch {
//        for (x in 1..5) channel.send(x * x)
//        channel.close() // 我们结束发送
//    }
//    // 这里我们使用 `for` 循环来打印所有被接收到的元素（直到通道被关闭）
//    for (y in channel) println(y)
//    println("Done!")
//}

/*****************构建通道生产者********************/
fun CoroutineScope.produceNumbers() = produce<Int> {
    var x = 1
    while (true) send(x++) // 在流中开始从 1 生产无穷多个整数
}

fun CoroutineScope.produceSquares(): ReceiveChannel<Int> = produce {
    for (x in 1..5) send(x * x)
}

fun CoroutineScope.square(numbers: ReceiveChannel<Int>): ReceiveChannel<Int> = produce {
    for (x in numbers) send(x * x)
}

//fun main() = runBlocking {
//    val squares = produceSquares()
//    squares.consumeEach { println(it) }
//    println("Done!")
//}

//fun main() = runBlocking {
//
//    val numbers = produceNumbers() // 从 1 开始生成整数
//    val squares = produceSquares()
//    repeat(5) {
//        println(squares.receive()) // 输出前五个
//    }
//    println("Done!") // 至此已完成
//    coroutineContext.cancelChildren() // 取消子协程
//}
