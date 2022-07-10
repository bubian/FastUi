package com.pds.fast.assist.utils

import android.graphics.Color

fun String?.safeParseColor(defaultColor: Int? = Color.TRANSPARENT) = try {
    Color.parseColor(this)
} catch (e: Exception) {
    defaultColor ?: Color.TRANSPARENT
}