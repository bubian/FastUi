package com.pds.fast.ui.common.assist

import android.content.res.Resources
import android.util.TypedValue

fun Float.dp2px() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        Resources.getSystem().displayMetrics
).toInt()