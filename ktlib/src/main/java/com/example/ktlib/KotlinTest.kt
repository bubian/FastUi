package com.example.ktlib

fun main(array: Array<String>?) {
    listCategory()
}

/**
 * 数组分类
 */
private fun listCategory() {
    val numbers = listOf("one", "two", "three", "four", "five", "six")
    numbers.groupingBy { it.first() }.apply {
        println(this)
    }
    numbers.fold(StringBuilder()) { str: StringBuilder, i: String -> str.append(i).append(" ") }.apply {
        println("result=$this")
    }

    listOf(1, 1, 1, 1).reduce { a: Int, b: Int -> a + b }.apply {
        println("resultInt=$this")
    }

    val aggregateTo = listOf(3, 4, 5, 6, 7, 8, 9)
    val aggregated = aggregateTo.groupingBy { it % 3 }.aggregateTo(mutableMapOf()) { key, accumulator: StringBuilder?, element, first ->
        if (first) // first element
            StringBuilder().append(key).append(":").append(element)
        else accumulator!!.append("-").append(element)
    }

    println(aggregated.values) // [0:3-6-9, 1:4-7, 2:5-8]
    aggregated.clear()

    listOf(1, 1, 1, 1)
}