package com.cerence.cara.base.ext

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date

// 2024-01-01T17:56:00-08:00
// 2024-01-02T05:25:00Z
@SuppressLint("SimpleDateFormat")
fun String.toDate(): Date? = try {
    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").parse(this)
} catch (e: Exception) {
    e.printStackTrace()
    null
}

@SuppressLint("SimpleDateFormat")
fun Long?.dateToString(): String? = if (null == this) null else try {
    SimpleDateFormat("yyyy-MM-dd").format(this)
} catch (e: Exception) {
    e.printStackTrace()
    null
}
