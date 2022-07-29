package com.pds.fast.example

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Log
import androidx.annotation.RequiresApi

import com.pds.fast.ui.common.page.BaseAppCompatActivity
import com.pds.fast.ui.common.pet.PetCatFloatingManager


class PetActivity : BaseAppCompatActivity() {

    companion object {
        const val CODE = 1000
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PetCatFloatingManager.doCatPetLogic(this, CODE)

        tc()
    }

    fun tc() {
        val paint = TextPaint()
        paint.isAntiAlias = true;
        paint.textSize = 10 * resources.displayMetrics.density;
        val alignment = Layout.Alignment.ALIGN_NORMAL;
        val spaceMultiplier = 1f
        val spaceAddition = 0f
        val textWidth = 100000
        val temp = "Happy new year\nHappy new"
        val myStaticLayout = StaticLayout(temp, paint, textWidth, alignment, spaceMultiplier, spaceAddition, true)
        Log.e("111111", " myStaticLayout=${myStaticLayout.height}")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CODE && resultCode == RESULT_OK) {
            PetCatFloatingManager.showPwtView(this)
        }
    }
}