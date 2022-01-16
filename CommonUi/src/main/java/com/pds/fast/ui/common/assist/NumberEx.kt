package com.pds.fast.ui.common.assist

import android.content.res.Resources
import android.util.TypedValue

fun Float.dp2px() = dp22px().toInt()
fun Float.dp22px() = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this,
    Resources.getSystem().displayMetrics
)