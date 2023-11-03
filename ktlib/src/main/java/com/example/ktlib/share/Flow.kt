package com.example.ktlib.share

import com.example.ktlib.treadName
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.lang.RuntimeException

fun simple(): Sequence<Int> = sequence {
    for (i in 1..3) {
        Thread.sleep(2000)
        yield(i)

    }
}

fun simpleFlow() = flow {
    println("${treadName()}======")
    for (i in 1..10) {
        delay(100) // 假装我们在这里做了一些有用的事情
        emit(i) // 发送下一个值
        if (9 == i) {
            throw RuntimeException("手动抛出异常")
        }
    }
}

fun requestFlow(i: String): Flow<String> = flow {
    emit("$i: First")
    delay(500) // 等待 500 毫秒
    emit("$i: Second")
}

@ExperimentalCoroutinesApi
@FlowPreview
fun main(): Unit = runBlocking {
    val zip = flowOf("one", "two", "three") // 字符串
    simpleFlow()
        // 缓存，当消费速度 < 生产速度时，生产者不用等待消费者，将生产对象放入缓存以供消费者使用
        .buffer()
        // 合并发射项，不对每个值进行处理
        .conflate()
        // 让繁忙的流可取消
        .cancellable()
        // 更改流发射的上下文
        .flowOn(Dispatchers.Default)
        // 限长操作符,只获取前两个
        .take(6)
        // 过滤
        .filter { it > 4 }
        // 转换
        .map { it + 2 }
        // 捕获异常
        .catch { println("发生异常") }
        // 完成流收集
        .onCompletion { println("完成收集") }
        // 流转换操作符
        .transform { request -> emit("Making request $request") }
        // 组合两个流中的相关值
        .zip(zip) { a, b -> "$a -> $b" }
        // 使用“combine”组合单个字符串
        .combine(zip) { a, b -> "$b -> $a" }
        // 流表示异步接收的值序列，所以很容易遇到这样的情况： 每个值都会触发对另一个值序列的请求
        .flatMapMerge { requestFlow(it) }
        .flatMapConcat { requestFlow(it) }
        .flatMapLatest { requestFlow(it) }
        // 确保流发射单个值的操作符
//        .single()
        .onEach { event -> println("Event: $event") }
        // 获取第一个值
//        .first {
//            println("first=$it")
//            true
//        }
        // 使用 reduce 与 fold 将流规约到单个值
//        .fold{
//
//        }
//        .reduce { a, b ->
//            "$a$b"
//        }
//        .toList()
//        .toSet()
//        .forEach {
//            println(it)
//        }
        // 处理最新值
//        .collectLatest {
//            println(it)
//        }
        .launchIn(this)
//        .collect { value ->
//            delay(300)
//            println(value)
//        }

    simple().forEach {
        println(it)
    }

}