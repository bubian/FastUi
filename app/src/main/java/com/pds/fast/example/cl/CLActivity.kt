package com.pds.fast.example.cl

import android.os.Bundle
import android.transition.TransitionManager
import android.util.Log
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.Placeholder
import com.pds.fast.ui.R
import com.pds.fast.ui.common.assist.isSameDay
import com.pds.fast.ui.common.page.BaseAppCompatActivity


class CLActivity : BaseAppCompatActivity() {

    private val mConstraintSet1 = ConstraintSet()
    private val mConstraintSet2 = ConstraintSet()
    private lateinit var mConstraintLayout: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e("CLActivity", "start=${System.currentTimeMillis()}")
        super.onCreate(savedInstanceState)
        Log.e("CLActivity", "end  =${System.currentTimeMillis()}")
        mConstraintSet2.load(this, R.layout.cl_state)
        setContentView(R.layout.activity_cl)
        mConstraintLayout = findViewById(R.id.cl_cs)
        // 从布局文件中获取 约束集 对象
        mConstraintSet1.clone(mConstraintLayout)
        mConstraintLayout.postDelayed({
            // 调用 TransitionManager.beginDelayedTransition ( ) 方法 , 生成过渡帧 , 执行时会自动进行关键帧动画执行
            TransitionManager.beginDelayedTransition(mConstraintLayout);
            mConstraintSet2.applyTo(mConstraintLayout)

        }, 1000)

        mConstraintLayout.postDelayed({
            findViewById<Placeholder>(R.id.ph).setContentId(R.id.p2)
        }, 1000)
    }
}