package com.pds.fast.example.recyclerView

import android.os.Bundle
import android.view.ViewGroup.MarginLayoutParams
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.pds.fast.ui.R
import com.pds.fast.ui.common.assist.dp2px
import com.pds.fast.ui.common.assist.getScreenWidth
import com.pds.fast.ui.common.listener.RecyclerViewPositionListener
import com.pds.fast.ui.common.page.BaseAppCompatActivity

class SampleRecyclerViewActivity : BaseAppCompatActivity() {
    private lateinit var banner: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample_recycler_view)
        banner = findViewById(R.id.banner)
        val paddingLeft: Int = getScreenWidth() * (375 - 340) / 375 / 2
        banner.setPadding(paddingLeft, 0, paddingLeft, 0)
        banner.post {
            (banner.layoutParams as MarginLayoutParams).topMargin = 16f.dp2px()
            banner.requestLayout()
        }
        PagerSnapHelper().attachToRecyclerView(banner)
        banner.adapter = MusicianGradeBannerAdapter()
        banner.addOnScrollListener(RecyclerViewPositionListener(banner) { })

    }
}