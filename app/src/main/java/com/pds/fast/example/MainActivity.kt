package com.pds.fast.example

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.TextView
import com.pds.fast.ui.R
import com.pds.fast.ui.common.FastFlowLayout
import com.pds.fast.ui.common.page.BaseActivity

class MainActivity : BaseActivity() {
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
                tagView.setTextColor(context.resources.getColor(R.color.color_a6a6a6))
                tagView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
                tagView.text = text
                tagView
            }
            setData(
                "哈哈哈,功能简介,京津冀,你体会,就,哦就给你,jajaj,加官晋爵,刚阿胶糕,哈,HHHHHH,功能简介,京津冀,你体会,就,哦就给你,jajaj,晋爵,刚阿胶糕,减肥哈哈,HHHHHH",
                ","
            )
        }
    }

    private fun dip2px(dpValue: Double): Int {
        val density: Float = resources.displayMetrics.density
        return (dpValue * density + 0.5).toInt()
    }
}