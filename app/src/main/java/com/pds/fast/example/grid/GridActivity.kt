package com.pds.fast.example.grid

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pds.fast.ui.R
import com.pds.fast.ui.common.decoration.FastDividerItemDecoration
import com.pds.fast.ui.common.assist.dp2px

/**
 * 各种Grid实现
 */
class GridActivity : AppCompatActivity() {
    private lateinit var rv: RecyclerView
    private val gridAdapter = GridFunAdapter()
    private val data = arrayListOf(
        GridModel("SpannedGridLayoutManager", SPANNED_GLM),
        GridModel("GridLayout", GRIDLAYOUT)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recyclerview)
        rv = findViewById<RecyclerView>(R.id.rl).apply {
            layoutManager = LinearLayoutManager(this@GridActivity).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
            adapter = gridAdapter
            addItemDecoration(
                FastDividerItemDecoration(
                    this@GridActivity,
                    FastDividerItemDecoration.HORIZONTAL_LIST,
                    15f.dp2px(),
                    Color.TRANSPARENT
                )
            )
        }
        gridAdapter.setList(data)
    }
}