package com.pds.fast.example

import android.animation.*
import android.annotation.SuppressLint
import android.graphics.PointF
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.pds.fast.ui.R
import com.pds.fast.ui.common.view.SportsView

class ViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_view)
        an(findViewById(R.id.sports))
    }

    private fun tran() {
        findViewById<View>(R.id.property_an).animate()
            .translationX(500f)
            .withLayer() // 动画开始前setLayerType(LAYER_TYPE_HARDWARE, null)，动画结束后setLayerType(LAYER_TYPE_NONE, null)
    }

    fun change(view: View) {
        tran()
    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun an(view: View) {
        // 创建 ObjectAnimator 对象
        // propertyName 属性名称，定义在View中的"progress"字段
        val animator: ObjectAnimator = ObjectAnimator.ofFloat(view, SportsView.PROGRESS, 0f, 65f)
        animator.start()
    }

    // 在 Android 5.0 （API 21） 加入了新的方法 ofArgb()
    @SuppressLint("ObjectAnimatorBinding")
    private fun colorAn(view: View) {
        val animator = ObjectAnimator.ofInt(view, SportsView.COLOR, -0x10000, -0xff0100)
        animator.setEvaluator(ArgbEvaluator())
        animator.start()
    }

    private fun objectAn(view: View) {
        val animator = ObjectAnimator.ofObject(
            view, "position",
            PointFEvaluator(), PointF(0f, 0f), PointF(1f, 1f)
        )
        animator.start()
    }

    // 另外在 API 21 中，已经自带了 PointFEvaluator
    private class PointFEvaluator : TypeEvaluator<PointF?> {
        var newPoint: PointF = PointF()

        override fun evaluate(fraction: Float, startValue: PointF?, endValue: PointF?): PointF? {
            return newPoint
        }
    }

    // 而对于 ObjectAnimator，是不能这么用的。不过你可以使用 PropertyValuesHolder 来同时在一个动画中改变多个属性。
    private fun hhh(view: View) {
        val holder1 = PropertyValuesHolder.ofFloat("scaleX", 1f)
        val holder2 = PropertyValuesHolder.ofFloat("scaleY", 1f)
        val holder3 = PropertyValuesHolder.ofFloat("alpha", 1f)
        val animator = ObjectAnimator.ofPropertyValuesHolder(view, holder1, holder2, holder3)
        animator.start()
    }

    private fun kkkk(view: View) {
        val animator1 = ObjectAnimator.ofFloat(0f, 65f)
        animator1.interpolator = LinearInterpolator()
        val animator2 = ObjectAnimator.ofInt(0, 30)
        animator2.interpolator = DecelerateInterpolator()

        val animatorSet = AnimatorSet()
        // 两个动画依次执行
        animatorSet.playSequentially(animator1, animator2)
        // 使用 playSequentially()，就可以让两个动画依次播放，而不用为它们设置监听器来手动为他们监管协作。
        // animatorSet.playTogether(animator1, animator2)

        // 使用 AnimatorSet.play(animatorA).with/before/after(animatorB)
        // 的方式来精确配置各个 Animator 之间的关系
        // animatorSet.play(animator1).with(animator2)
        // animatorSet.play(animator1).before(animator2)
        // animatorSet.play(animator1).after(animator2)
        animatorSet.start()
    }

    // 除了合并多个属性和调配多个动画，你还可以在 PropertyValuesHolder 的基础上更进一步，通过设置 Keyframe （关键帧），把同一个动画属性拆分成多个阶段。例如，你可以让一个进度增加到 100% 后再「反弹」回来。
    private fun ggg(view: View) {
        // 在 0% 处开始
        // 在 0% 处开始
        val keyframe1 = Keyframe.ofFloat(0f, 0f)
        // 时间经过 50% 的时候，动画完成度 100%
        // 时间经过 50% 的时候，动画完成度 100%
        val keyframe2 = Keyframe.ofFloat(0.5f, 100f)
        // 时间见过 100% 的时候，动画完成度倒退到 80%，即反弹 20%
        // 时间见过 100% 的时候，动画完成度倒退到 80%，即反弹 20%
        val keyframe3 = Keyframe.ofFloat(1f, 80f)
        val holder =
            PropertyValuesHolder.ofKeyframe("progress", keyframe1, keyframe2, keyframe3)

        val animator = ObjectAnimator.ofPropertyValuesHolder(view, holder)
        animator.start()
    }

}
