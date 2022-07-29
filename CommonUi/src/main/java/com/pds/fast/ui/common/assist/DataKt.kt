package com.pds.fast.ui.common.assist

import java.text.SimpleDateFormat
import java.util.*

fun Date.isRangeDate(startTime: Date?, endTime: Date?): Boolean {
    if (null == startTime || null == endTime) return false
    if (time == startTime.time || time == endTime.time) return true
    val date = Calendar.getInstance()
    date.time = this
    val begin = Calendar.getInstance()
    begin.time = startTime


    val end = Calendar.getInstance()
    end.time = endTime

    return date.after(begin) && date.before(end)
}

const val HH_MM_SS = "HH:mm:ss"
fun Long.stamp2Date(format: String): Date? {
    val simpleDateFormat = SimpleDateFormat(format)
    val date = Date(this)
    return simpleDateFormat.parse(simpleDateFormat.format(date))
}

fun String.time2Date(format: String): Date? {
    val simpleDateFormat = SimpleDateFormat(format)
    return simpleDateFormat.parse(this)
}