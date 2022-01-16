package com.pds.fast.example.grid

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayout
import com.pds.fast.example.common.BaseHolder
import com.pds.fast.ui.R

class GridHolderFactory {
    fun create(parent: ViewGroup, viewType: Int): BaseHolder<GridModel> {
        val context = parent.context
        return when (viewType) {
            SPANNED_GLM -> StaggeredGridHolder(LayoutInflater.from(context).inflate(R.layout.recyclerview, null) as RecyclerView)
            GRIDLAYOUT -> GridLayoutHolder(GridLayout(parent.context))
            FLEX_BOX_LAYOUT -> FlexboxLayoutHolder(FlexboxLayout(parent.context))
            else -> BaseHolder(View(context))
        }
    }
}