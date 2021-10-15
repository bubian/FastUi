package com.pds.fast.ui.common.html

import android.graphics.Color
import android.text.Editable
import android.text.Spanned
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import org.xml.sax.XMLReader
import java.lang.reflect.Field

class MyFontTag {
    // 标签开始索引
    private var startIndex = 0
    // 标签结束索引
    private var endIndex = 0
    // 存放标签所有属性键值对
    private val attributes: HashMap<String, String> = HashMap()

    fun handleTag(
        opening: Boolean,
        tag: String?,
        output: Editable,
        xmlReader: XMLReader
    ) {
        // 解析所有属性值
        parseAttributes(xmlReader)
        if (opening) {
            startHandleTag(tag, output, xmlReader)
        } else {
            endEndHandleTag(tag, output, xmlReader)
        }
    }

    private fun startHandleTag(tag: String?, output: Editable, xmlReader: XMLReader?) {
        startIndex = output.length
    }

    private fun endEndHandleTag(tag: String?, output: Editable, xmlReader: XMLReader?) {
        endIndex = output.length
        val color = attributes["color"]
        if (!TextUtils.isEmpty(color)) {
            output.setSpan( ForegroundColorSpan(Color.parseColor(color)), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        var size: String? = attributes["size"]
        if (null == size) {
            return
        }
        size = size.split("px".toRegex()).toTypedArray()[0]
        // 设置字体大小
        if (!TextUtils.isEmpty(size)) {
            output.setSpan(
                AbsoluteSizeSpan(size.toInt()), startIndex, endIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    private fun parseAttributes(xmlReader: XMLReader) {
        try {
            val elementField: Field = xmlReader::class.java.getDeclaredField("theNewElement")
            elementField.isAccessible = true
            val element: Any = elementField.get(xmlReader)
            val attrsField: Field = element.javaClass.getDeclaredField("theAtts")
            attrsField.isAccessible = true
            val attrs: Any = attrsField.get(element)
            val dataField: Field = attrs.javaClass.getDeclaredField("data")
            dataField.isAccessible = true
            val data = dataField.get(attrs) as Array<String>
            val lengthField: Field = attrs.javaClass.getDeclaredField("length")
            lengthField.isAccessible = true
            val len = lengthField.get(attrs) as Int
            for (i in 0 until len) {
                attributes[data[i * 5 + 1]] = data[i * 5 + 4]
            }
        } catch (e: Exception) {
        }
    }
}