package com.pds.fast.ui.common.view

import android.content.Context
import android.graphics.*
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.RequiresApi

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-26 09:12
 * Email：pengdaosong@medlinker.com
 * Description:
 */
class CustomTextView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr) {

    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintTag = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintLine = Paint(Paint.ANTI_ALIAS_FLAG)

    private val pathEffect =  DashPathEffect(floatArrayOf(10f,5f), 0f)

    private val path = Path()
    private val rect = Rect()

    private val textP1 = TextPaint()
    private val textP2 = TextPaint()

    private val TX = "Pǖ、ǘ、ǚ、¥jDo"

    private var text1 = "Lorem Ipsum is simply dummy text of the printing and typesetting industry."
    private val staticLayout1 = StaticLayout(text1, textP1, 300, Layout.Alignment.ALIGN_NORMAL, 1f, 20f, true)

    private var text2 = "a\nbc\ndefghi\njklm\nnopqrst\nuvwx\nyz"
    private var staticLayout2 = StaticLayout(text2, textP2, 300,Layout.Alignment.ALIGN_NORMAL, 1f, 20f, true)

    val TEXT = "Hello ǘds AaJj"

    init {
        paintText.color = Color.WHITE
        paint.color = Color.RED
        paintTag.color = Color.RED
        paintLine.color = Color.RED
        paintLine.pathEffect = pathEffect
        paintLine.style = Paint.Style.STROKE
        paintText.textSize = 120f
        paintTag.textSize = 40f
        setLayerType(LAYER_TYPE_SOFTWARE, null)

        path.moveTo(20f,650f)
        path.rLineTo(100f,100f)
        path.rLineTo(100f,-100f)
        path.rLineTo(100f,100f)

        textP1.color = Color.RED
        textP1.textSize = 40f

        textP2.color = Color.RED
        textP2.textSize = 40f
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),1000)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onDraw(canvas: Canvas) {
        // 方法的参数很简单： text 是文字内容，x 和 y 是文字的坐标。但需要注意：这个坐标并不是文字的左上角，而是一个与左下角比较接近的位置
        // 参数 "x" 并不是字母 "H" 左边的位置，而是比它的左边再往左一点点，它是字母 "H" 的左边的空隙。绝大多数的字符，它们的宽度都是要略微大于实际显示的宽度的。字符的左右两边会留出一部分空隙，用于文字之间的间隔，以及文字和边框的间隔
        // 参数 "y" 指定的基线的位置
        canvas.drawText(TEXT,100f,200f,paintText)
        paintTag.color = Color.WHITE
        // 绘制drawText参数x,y位置
        paint.strokeWidth = 12f
        paint.strokeCap = Paint.Cap.ROUND
        canvas.drawPoint(92f,200f,paint)
        canvas.drawText("(x, y)",52f,240f,paintTag)

        // 绘制文字基线（baseline）位置 需要关闭硬件加速，不然用这个方法绘制不出虚线
        // drawText() 方法参数中的 y 值，就是指定的基线的位置
        canvas.drawLine(50f,200f,1000f,200f,paintLine)
        canvas.drawText("baseline",920f,200f,paintTag)

        // 它提供了几个文字排印方面的数值：ascent, descent, top, bottom, leading，除了leading，其余都是相对"baseline"的相对位置。
        // ascent 和 descent 这两个值还可以通过 Paint.ascent() 和 Paint.descent() 来快捷获取。
        val fontMetrics = paintText.fontMetrics
        // top 的值是和baseline 的相对位移，它的值为负（因为它在 baseline 的上方）
        val top = fontMetrics.top + 200f // 加上baseline的值，就是文字真实的坐标位置
        val bottom = fontMetrics.bottom  + 200f
        val ascent = fontMetrics.ascent  + 200f
        val descent = fontMetrics.descent + 200f
        // 指的是行的额外间距，即对于上下相邻的两行，上行的 bottom 线和下行的 top 线的距离
        val leading = fontMetrics.leading

        canvas.drawLine(50f,top,1000f,top,paintLine)
        canvas.drawText("real top",920f,top,paintTag)

        canvas.drawLine(50f,bottom,1000f,bottom,paintLine)
        canvas.drawText("real bottom",820f,bottom + 30,paintTag)

        canvas.drawLine(50f,ascent,1000f,ascent,paintLine)
        canvas.drawText("real ascent",820f,ascent + 30,paintTag)

        canvas.drawLine(50f,descent,1000f,descent,paintLine)
        canvas.drawText("real descent",520f,descent,paintTag)

        canvas.drawText(TX,100f,400f,paintText)
        // hOffset 和 vOffset。它们是文字相对于 Path 的水平偏移量和竖直偏移量
        canvas.drawTextOnPath("printing and typesetting",path,0f,0f,paintTag)

        paint.pathEffect = pathEffect
        paint.strokeWidth = 3f

        paintText.getTextBounds(TX,0,TX.length,rect)
        rect.left += 100
        rect.top += 400
        rect.right += 100
        rect.bottom += 400
        paint.style = Paint.Style.STROKE
        canvas.drawRect(rect, paint)

        // getTextBounds: 它测量的是文字的显示范围（关键词：显示）。形象点来说，你这段文字外放置一个可变的矩形，然后把矩形尽可能地缩小，一直小到这个矩形恰好紧紧包裹住文字，那么这个矩形的范围，就是这段文字的 bounds。
        // measureText(): 它测量的是文字绘制时所占用的宽度（关键词：占用）。前面已经讲过，一个文字在界面中，往往需要占用比他的实际显示宽度更多一点的宽度，以此来让文字和文字之间保留一些间距，不会显得过于拥挤。
        // 上面的这幅图，我并没有设置 setLetterSpacing() ，这里的 letter spacing 是默认值 0，但你可以看到，图中每两个字母之间都是有空隙的。
        // 另外，下方那条用于表示文字宽度的横线，在左边超出了第一个字母 H 一段距离的，在右边也超出了最后一个字母 r（虽然右边这里用肉眼不太容易分辨），
        // 而就是两边的这两个「超出」，导致了 measureText() 比 getTextBounds() 测量出的宽度要大一些。
        val textWidth = paintText.measureText(TX)
        paintLine.color = Color.WHITE
        canvas.drawLine(100f,400f,100f + textWidth,400f,paintLine)

        // StaticLayout
        canvas.save()
        canvas.translate(350f, 650f)
        // width 是文字区域的宽度，文字到达这个宽度后就会自动换行；
        // align 是文字的对齐方向；
        // spacingmult 是行间距的倍数，通常情况下填 1 就好；
        // spacingadd 是行间距的额外增加值，通常情况下填 0 就好；
        // includepad 是指是否在文字上下添加额外的空间，来避免某些过高的字符的绘制出现越界
        staticLayout1.draw(canvas)
        canvas.translate(0f, 100f)
        staticLayout2.draw(canvas)
        canvas.restore()

        // 用 CSS 的 font-feature-settings 的方式来设置文字。
        paintTag.fontFeatureSettings = "smcp" // 设置 "small caps"
        canvas.drawText("Hello pds", 650f, 850f, paintTag)

        // setHinting 设置是否启用字体的 hinting （字体微调
        // setElegantTextHeight 通常都有两个版本的字体：一个原始版本，一个压缩了高度的版本,使用 setElegantTextHeight() 就可以切换到原始版本
        // setSubpixelText 是否开启次像素级的抗锯齿

        // paint.getFontSpacing 获取推荐的行距,即推荐的两行文字的 baseline 的距离.这个值是系统根据文字的字体和字号自动计算的。
        // 它的作用是当你要手动绘制多行文字（而不是使用 StaticLayout）的时候，可以在换行的时候给 y 坐标加上这个值来下移文字

        // FontMetrics 和 getFontSpacing()：
        //
        //从定义可以看出，上图中两行文字的 font spacing (即相邻两行的 baseline 的距离) 可以通过 bottom - top + leading (top 的值为负，前面刚说过，记得吧？）来计算得出。
        //
        //但你真的运行一下会发现， bottom - top + leading 的结果是要大于 getFontSpacing() 的返回值的。
        //
        //两个方法计算得出的 font spacing 竟然不一样？
        //
        // 这并不是 bug，而是因为 getFontSpacing() 的结果并不是通过 FontMetrics 的标准值计算出来的，而是另外计算出来的一个值，它能够做到在两行文字不显得拥挤的前提下缩短行距，以此来得到更好的显示效果。
        // 所以如果你要对文字手动换行绘制，多数时候应该选取 getFontSpacing() 来得到行距，不但使用更简单，显示效果也会更好。


        // breakText 这个方法也是用来测量文字宽度的。但和 measureText() 的区别是， breakText() 是在给出宽度上限的前提下测量文字的宽度。如果文字的宽度超出了上限，那么在临近超限的位置截断文字。

        // getRunAdvance 对于一段文字，计算出某个字符处光标的 x 坐标

        // getOffsetForAdvance 给出一个位置的像素值，计算出文字中最接近这个位置的字符偏移量（即第几个字符最接近这个坐标）

        // hasGlyph 检查指定的字符串中是否是一个单独的字形 (glyph）。最简单的情况是，string 只有一个字母（比如 a）
    }

}