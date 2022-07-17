package com.pds.fast.example.test

import android.net.Uri


fun main(): Unit {
    val key = "position"
    val url = "https://saas.hixiaoman.com/activity/index?appKey=ky-az-hdgj_bxgemy&placeId=3706"
    val uri = Uri.parse(url)
    val params = uri.queryParameterNames
    val newUri = uri.buildUpon().clearQuery()
    if (params.contains(key)) {
        for (param in params) {
            newUri.appendQueryParameter(param, if (param == key) "sdsa" else uri.getQueryParameter(param))
        }
    } else {
        newUri.appendQueryParameter(key, "ssss")
    }
}
