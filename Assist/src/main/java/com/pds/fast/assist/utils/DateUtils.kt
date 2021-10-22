package com.pds.fast.assist.utils

import java.text.SimpleDateFormat
import java.util.*

//两个时间戳是否是同一天 时间戳是long型的
fun isSameData(currentTime: Long, lastTime: Long): Boolean {
    try {
        val nowCal = Calendar.getInstance()
        val dataCal = Calendar.getInstance()
        val df1 = SimpleDateFormat("yyyy-MM-dd  HH:mm:ss")
        val df2 = SimpleDateFormat("yyyy-MM-dd  HH:mm:ss")
        val nowLong: Long = currentTime
        val dataLong: Long = lastTime
        val data1: String = df1.format(nowLong)
        val data2: String = df2.format(dataLong)
        val now: Date = df1.parse(data1)
        val date: Date = df2.parse(data2)
        nowCal.time = now
        dataCal.time = date
        return isSameDay(nowCal, dataCal)
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
    return false
}

fun isSameDay(cal1: Calendar?, cal2: Calendar?): Boolean {
    return if (cal1 != null && cal2 != null) {
        cal1.get(Calendar.ERA) === cal2.get(Calendar.ERA) && cal1.get(Calendar.YEAR) === cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) === cal2.get(Calendar.DAY_OF_YEAR)
    } else {
        false
    }
}