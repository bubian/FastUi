package com.pds.fast.example

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.ImageView
import android.widget.TextView
import com.pds.compose.KComponentActivity
import com.pds.fast.assist.glide.Glider
import com.pds.fast.ui.R
import com.pds.fast.ui.common.FastFlowLayout
import com.pds.fast.ui.common.OverlapAvatarView
import com.pds.fast.ui.common.assist.dp2px
import com.pds.fast.ui.common.page.BaseAppCompatActivity
import com.pds.fast.ui.common.project.TaoWorkBottomDialog
import com.pds.fast.ui.common.ruler.SceneItemModel
import com.pds.fast.ui.common.ruler.SceneScrollPicker
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseAppCompatActivity() {
    private lateinit var overlapAvatarView: OverlapAvatarView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val fastFlowLayout = findViewById<FastFlowLayout>(R.id.fast_flow_layout)
        fastFlowLayout.apply {
            setStartText("开始")
            setEndText("结束")
            setHorizontalCap(60)
            setVerticalCap(60)
            setMaxRow(1)

            setFastFlow { _, data, tipType ->
                val text = data as String
                val context = context
                val tagView = TextView(context)
                val params = MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dip2px(17.0))
                tagView.layoutParams = params
                if (0 == tipType) {
                    tagView.setBackgroundColor(Color.BLUE)
                    tagView.setPadding(dip2px(4.0), 0, dip2px(4.0), 0)
                } else {
                    tagView.setBackgroundColor(Color.BLACK)
                }
                tagView.gravity = Gravity.CENTER_VERTICAL
                tagView.setTextColor(context.resources.getColor(R.color.color_A6A6A6))
                tagView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
                tagView.text = text
                tagView
            }
            setData(
                "哈哈哈,功能简介,京津冀,你体会,就,哦就给你,jajaj,加官晋爵,刚阿胶糕,哈,HHHHHH,功能简介,京津冀,你体会,就,哦就给你,jajaj,晋爵,刚阿胶糕,减肥哈哈,HHHHHH",
                ","
            )
        }
        tao_work_bottom_dialog.setOnClickListener {
            doTaoWorkDialog();
        }

        ring_progress.setProgress(0.55f)
        html.setOnClickListener { startActivity(Intent(this, HtmlActivity::class.java)) }

        doOverlapAvatarView()

        initRulerView()

        findViewById<View>(R.id.bezierView).setOnClickListener {
            startActivity(Intent(this, KComponentActivity::class.java))
        }
    }

    private fun doOverlapAvatarView() {
        Glider.enable()
        val avatarList: List<String> = arrayListOf(
            "https://b-ssl.duitang.com/uploads/item/201811/04/20181104223950_vygmz.thumb.700_0.jpeg"
        )

        val avatarList11: List<String> = arrayListOf(
            "https://b-ssl.duitang.com/uploads/item/201811/04/20181104223950_vygmz.thumb.700_0.jpeg",
            "https://b-ssl.duitang.com/uploads/item/201811/04/20181104223952_zfhli.thumb.700_0.jpeg",
            "https://b-ssl.duitang.com/uploads/item/201811/01/20181101093301_u2NKu.thumb.700_0.jpeg",
            "https://b-ssl.duitang.com/uploads/item/201807/08/20180708095827_SYPL3.thumb.700_0.jpeg",
            "https://b-ssl.duitang.com/uploads/item/201811/01/20181101093301_u2NKu.thumb.700_0.jpeg"
        )
        overlapAvatarView = findViewById(R.id.overlapAvatarView)
        overlapAvatarView.setOverlapAvatar(object : OverlapAvatarView.IOverlapAvatar {
            override fun buildItemView(inflater: LayoutInflater?): View {
                return ImageView(this@MainActivity).apply {
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    layoutParams = ViewGroup.LayoutParams(38f.dp2px(), 38f.dp2px())
                }
            }

            override fun bindData(view: View, data: Any) {
                if (view is ImageView && data is String) {
                    Glider.loadAvatarNoBorder(view, data)
                }
            }

            override fun addData(view: View, data: Any) {
                if (view is ImageView && data is String) {
                    Glider.loadAvatarNoBorder(view, data)
                    overlapAvatarView.addAvatarView(view, 1)
                }
            }
        })
        overlapAvatarView.setMaxChildCount(3)
        overlapAvatarView.setData(avatarList)

        var index = 0
        findViewById<View>(R.id.add_avatar).setOnClickListener {
            index++
            if (index > 4) index = 0
            overlapAvatarView.addData(avatarList11[index])
        }
    }

    private fun doTaoWorkDialog() {
        val list = mutableListOf<TaoWorkBottomDialog.TaoWorkTagModel>()
        list.add(TaoWorkBottomDialog.TaoWorkTagModel("曾工", "test"))
        list.add(TaoWorkBottomDialog.TaoWorkTagModel("歌哈哈哈", "test"))
        list.add(TaoWorkBottomDialog.TaoWorkTagModel("嘎哈哈", "test"))
        list.add(TaoWorkBottomDialog.TaoWorkTagModel("鸭鹅", "test"))
        list.add(TaoWorkBottomDialog.TaoWorkTagModel("u人", "test"))
        list.add(TaoWorkBottomDialog.TaoWorkTagModel("u时光", "test"))
        list.add(TaoWorkBottomDialog.TaoWorkTagModel("瓯江", "test"))
        list.add(TaoWorkBottomDialog.TaoWorkTagModel("爬进", "test"))
        list.add(TaoWorkBottomDialog.TaoWorkTagModel("就哈就纠结啊就", "test"))
        TaoWorkBottomDialog().setLikeTags(list).show(this)
    }

    private fun dip2px(dpValue: Double) = with(dpValue) {
        val density: Float = resources.displayMetrics.density
        (dpValue * density + 0.5).toInt()
    }

    private fun initRulerView() {
        val rulerView = findViewById<SceneScrollPicker>(R.id.view_ruler)
        val colors = arrayOf("#ff0000", "#00ff00", "#00ffcc", "#ddff00", "#001f00", "#34ff00", "#04ff00", "#009f00", "#012f00", "#00ff10", "#011100")
        rulerView.setOnSelectedListener { _, _ -> }
        rulerView.data = arrayListOf()
        for (index in 1..10) {
            rulerView.addItem(SceneItemModel(index.toString(), "标题$index", colors[index]))
        }
    }
}