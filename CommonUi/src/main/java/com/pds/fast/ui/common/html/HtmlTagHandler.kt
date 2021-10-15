package com.pds.fast.ui.common.html

import android.text.Editable
import android.text.Html
import org.xml.sax.XMLReader

class HtmlTagHandler(private val tagName: String) : Html.TagHandler {
    override fun handleTag(
        opening: Boolean,
        tag: String,
        output: Editable,
        xmlReader: XMLReader
    ) {

        if (tag.equals(tagName, true)) {
            MyFontTag().handleTag(opening,tag,output,xmlReader)
        }
    }
}