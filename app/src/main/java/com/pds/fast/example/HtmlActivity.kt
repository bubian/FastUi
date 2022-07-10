package com.pds.fast.example

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.DisplayMetrics
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.pds.fast.ui.R
import com.pds.fast.ui.common.html.HtmlTagHandler
import kotlinx.android.synthetic.main.activity_html.*
import org.sufficientlysecure.htmltextview.ClickableTableSpan
import org.sufficientlysecure.htmltextview.DrawTableLinkSpan
import org.sufficientlysecure.htmltextview.HtmlResImageGetter
import org.sufficientlysecure.htmltextview.OnClickATagListener

class HtmlActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_html)
        htmlTextView()

        htmlSysTv.text =
            HtmlCompat.fromHtml(
                HTML_P,
                HtmlCompat.FROM_HTML_MODE_LEGACY,
                null,
                HtmlTagHandler("myFont")
            )

    }

    private fun systemHtml() {
        htmlTv.text = Html.fromHtml(HTML_P)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun systemHtmlN() {
        htmlTv.text = Html.fromHtml(HTML_P, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    private fun htmlTextView() {
        //text.setRemoveFromHtmlSpace(false); // default is true

        //text.setRemoveFromHtmlSpace(false); // default is true
        htmlTv.setClickableTableSpan(ClickableTableSpanImpl())
        val drawTableLinkSpan = DrawTableLinkSpan()
        drawTableLinkSpan.tableLinkText = "[tap for table]"
        htmlTv.setDrawTableLinkSpan(drawTableLinkSpan)

        // Best to use indentation that matches screen density.

        // Best to use indentation that matches screen density.
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        htmlTv.setListIndentPx(metrics.density * 10)

        // a tag click listener

        // a tag click listener
        htmlTv.setOnClickATagListener(OnClickATagListener { widget: View?, spannedText: String?, href: String? ->
            val toast = Toast.makeText(this@HtmlActivity, null, Toast.LENGTH_SHORT)
            toast.setText(href)
            toast.show()
            false
        })
        htmlTv.blockQuoteBackgroundColor = resources.getColor(R.color.color_14FA3123)
        htmlTv.blockQuoteStripColor = Color.BLUE

        htmlTv.setHtml(getString(R.string.html_p), HtmlResImageGetter(baseContext))
    }


    internal class ClickableTableSpanImpl : ClickableTableSpan() {
        override fun newInstance(): ClickableTableSpan {
            return ClickableTableSpanImpl()
        }

        override fun onClick(widget: View) {

        }
    }
}

/**
 * color：经过测试
 */
const val HTML_P = "<myFont size=\'49\' color=\'#0000ff\'>我是标签p样式</myFont>"