package com.pds.fast.example.grid

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.pds.fast.example.common.BaseHolder
import com.pds.fast.ui.common.assist.dp22px
import com.pds.fast.ui.common.assist.dp2px
import com.pds.fast.ui.common.decoration.SpaceItemDecorator
import com.pds.fast.ui.common.layoutmanager.SpanSize
import com.pds.fast.ui.common.layoutmanager.SpannedGridLayoutManager

class StaggeredGridHolder(private val recyclerView: RecyclerView) : BaseHolder<GridModel>(recyclerView) {

    private val itemAdapter = GridItemAdapter()
    private var spannedGridLayoutManager: SpannedGridLayoutManager =
        SpannedGridLayoutManager(3, 36f.dp2px()).apply {
            itemOrderIsStable = true
            spanSizeLookup = SpannedGridLayoutManager.SpanSizeLookup { position, itemWidth ->
                SpanSize(getWidthSpan(position, itemWidth), if (itemAdapter.clickedItems[position] || 0 == position) 2 else 1)
            }
        }

    init {
        recyclerView.apply {
            setBackgroundColor(Color.GRAY)
            addItemDecoration(SpaceItemDecorator(left = 10, top = 10, right = 10, bottom = 10))
            adapter = itemAdapter
            layoutManager = spannedGridLayoutManager
        }
    }

    override fun bindData(data: GridModel, position: Int) {
        itemAdapter.notifyDataSetChanged()
    }

    private fun getWidthSpan(position: Int, itemWidth: Int): Int {
        val tt = getTextW(itemAdapter.getDataP(position))
        val cap = if (tt > itemWidth) 2 else 1
        return 1.coerceAtLeast(cap)
    }

    private val paint = Paint()
    private fun getTextW(text: String): Float {
        paint.textSize = 16f.dp22px() //设置字体大小
        return paint.measureText(text)
    }

    class GridItemAdapter : RecyclerView.Adapter<GridItemViewHolder>() {

        private val urlList = arrayOf(
            "中海油",
            "哈哈哈",
            "黄俊捷啦啦啦ggafffaafafafafa faaf agag",
            "令箭荷花",
            "哦叫啊叫",
            "就按你奶奶",
            "OK来看看",
            "抛开看看",
            "叫啊哈哈哈"
        )
        val clickedItems: MutableList<Boolean>

        init {
            clickedItems = MutableList(itemCount) { false }
            setHasStableIds(true)
        }


        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        private val colors = arrayOf(Color.RED, Color.GREEN, Color.BLUE, Color.CYAN, Color.DKGRAY, Color.MAGENTA, Color.YELLOW)

        override fun onBindViewHolder(holder: GridItemViewHolder, position: Int) {
            (holder.itemView as? GridItemView)?.setTitle(urlList[position])

            holder.itemView.setBackgroundColor(
                colors[position % colors.size]
            )

            holder.itemView.setOnClickListener {
                clickedItems[position] = !clickedItems[position]
                notifyItemChanged(position)
            }
        }

        override fun getItemViewType(position: Int): Int {
            return position
        }

        override fun getItemCount(): Int {
            return urlList.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridItemViewHolder {
            val gridItemView = GridItemView(parent.context).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 36f.dp2px())
            }
            return GridItemViewHolder(gridItemView)
        }

        fun getDataP(position: Int): String = urlList[position]
    }

    class GridItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class GridItemView(context: Context) : FrameLayout(context) {

        var title: TextView = TextView(context)

        init {
            title.textSize = 16f
            title.maxLines = 1
            title.gravity = Gravity.CENTER
            title.ellipsize = TextUtils.TruncateAt.END
            title.gravity = Gravity.CENTER
            addView(title, LayoutParams(LayoutParams.WRAP_CONTENT, 36f.dp2px()).apply {
                gravity = Gravity.CENTER
            })
        }

        fun setTitle(text: String?) {
            title.text = text
            requestLayout()
        }
    }
}