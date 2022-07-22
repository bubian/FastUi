package com.pds.fast.example

import android.content.Intent
import android.os.Build
import android.os.Bundle
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CODE && resultCode == RESULT_OK) {
            PetCatFloatingManager.showPwtView(this)
        }
    }
}