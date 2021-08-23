package com.pds.fast.example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pds.fast.ui.R
import com.pds.fast.ui.common.FastFlowLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fastFlowLayout = findViewById<FastFlowLayout>(R.id.fast_flow_layout)
        fastFlowLayout.apply {
            setStartText("开始")
            setEndText("结束")
            setMiddleMargin(60)
            setData("哈哈哈,功能简介,京津冀,你啊和体会,野猴,哦就给你,jajaj,加官晋爵,刚阿胶糕", ",")
        }
    }
}