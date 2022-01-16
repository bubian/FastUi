package com.pds.fast.example.banner

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.pds.fast.ui.R
import com.pds.fast.assist.glide.Glider
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.StringCallback
import android.widget.Toast
import androidx.viewpager.widget.ViewPager
import com.google.gson.Gson
import com.pds.fast.example.App
import com.pds.fast.ui.common.assist.dp2px
import com.pds.fast.ui.common.banner.FastBannerUtils
import okhttp3.Call
import java.lang.Exception
import java.util.ArrayList

class BannerActivity : AppCompatActivity() {
    private lateinit var banner: OverLapBanner

    private val bannerMaxWidth: Int = FastBannerUtils.getScreenWidth(App.app) - 30f.dp2px()
    private val bannerWidth = (bannerMaxWidth / 3f).toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_banner)
        Glider.enable()
        banner = findViewById(R.id.banner)
        initBanner()
        initData()
    }


    private fun initData() {
        //加载网络图片资源
        val url = "https://api.tuchong.com/2/wall-paper/app"
        OkHttpUtils.get().url(url).build().execute(object : StringCallback() {
            override fun onError(call: Call, e: Exception, id: Int) {
                Toast.makeText(this@BannerActivity, "onError", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(response: String, id: Int) {
                val advertiseEntity = Gson().fromJson(response, TuchongEntity::class.java)
                val others = advertiseEntity.feedList
                val data: MutableList<BannerModel> = ArrayList()
                others.forEach {
                    if ("post" == it.type) {
                        if (data.size == 6) return@forEach
                        data.add(BannerModel(it.entry.url, "100", it.entry.images[0].user_id, it.entry.images[0].img_id))
                    }
                }
                banner.setBannerData(data)
            }
        })
    }

    private fun initBanner() {
        banner.layoutParams = banner.layoutParams.apply {
            height = bannerWidth
            width = bannerMaxWidth
        }
        banner.setHandLoop(true)
            .setIsClipChildrenMode(true)
            .setViewPagerMargin((bannerWidth * 2.5f).toInt())
//                .setViewPagerMargin((bannerWidth / 2f).toInt())
//                .setClipChildrenRightMargin(bannerWidth)
//                .setClipChildrenLeftMargin(bannerWidth)

        banner.setOnItemClickListener { _, model: Any?, _, _ ->
        }
        banner.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(i: Int, v: Float, i1: Int) {
                Log.d("BasePageTransformer", " position=test222=" + banner.viewPager.childCount)
            }

            override fun onPageSelected(position: Int) {
                Log.d("BasePageTransformer", " position=test333=$position")

                banner.pageTransformer.setPageIndex(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    val childCount = banner.viewPager.childCount

//                    for (i in 0..childCount) {
//                        val child: View = getChildAt(i)
//                        val lp = child.layoutParams as ViewPager.LayoutParams
//                        if (lp.isDecor) continue
//                        val transformPos: Float = (child.left - scrollX) as Float / getClientWidth()
//                        mPageTransformer.transformPage(child, transformPos)
//                    }
                }
            }
        })

        banner.setAdapter(object : OverLapBanner.BannerAdapter {
            override fun loadBanner(banner: OverLapBanner?, model: Any?, view: View?, position: Int) {
                if (view is SquareBannerView && model is BannerModel) {
                    view.tag = position
                    view.bindData(model, position)
                    view.setBannerScale(1f)
                    view.setBannerTranslationZ(-Float.MAX_VALUE)
                }
            }

            override fun getBannerItemView(context: Context) = SquareBannerView(context).apply {
                alpha = 0f
                setPadding(bannerWidth, 0, bannerWidth, 0)
            }

            override fun switchToPoint(model: Any?) {

            }
        })
    }
}