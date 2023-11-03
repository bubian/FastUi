package com.example.ktlib.share

inline fun sum(a: Int, b: Int): Int {
    return a + b
}

inline fun sum(a: Int, b: Int, crossinline bad: (result: Int) -> Unit): Int {
    val r = a + b
    bad.invoke(r)
    return r
}
//
//fun main() {
//    repeat(100){
//        sum(1, 2) {
//            println(it)
//        }
//    }
//    "".run {
//        1
//    }
//
//    println(with("123"){
//        this + "122"
//    })
//}

fun main() {
    println(with("123") {
        this + 122
    })
}