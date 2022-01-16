package com.pds.fast.example.grid

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pds.fast.example.common.BaseHolder

class GridFunAdapter : RecyclerView.Adapter<BaseHolder<GridModel>>() {

    private val list = arrayListOf<GridModel>()
    private val factory = GridHolderFactory()

    @SuppressLint("NotifyDataSetChanged")
    fun setList(data: List<GridModel>) {
        list.clear()
        list.addAll(data)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int = list[position].type


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = factory.create(parent, viewType)

    override fun onBindViewHolder(holder: BaseHolder<GridModel>, position: Int) {
        holder.bindData(list[position], position)
    }

    override fun getItemCount(): Int = list.size
}