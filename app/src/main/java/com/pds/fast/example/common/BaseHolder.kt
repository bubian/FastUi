package com.pds.fast.example.common

import android.view.View
import androidx.recyclerview.widget.RecyclerView

open class BaseHolder<T>(view: View) : RecyclerView.ViewHolder(view) {
    open fun bindData(data: T, position: Int) {}
}