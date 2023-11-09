package com.pds.fast.assist.ext

import android.content.res.Resources
import android.util.TypedValue
import com.pds.fast.assist.Assist

val Float.dp
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics)

fun Int.str(vararg args: Any?) = Assist.application.resources.getString(this, *args)