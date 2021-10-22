package com.pds.fast.assist.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File

fun installApp(activity: Activity, file: File) {
    try {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val contentUri = FileProvider.getUriForFile(activity, "com.stones.download.fileprovider", file)
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive")
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
        }
        activity.application.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun getVersionCode(context: Context): Long {
    var versionCode = 0L
    try {
        versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            context.packageManager.getPackageInfo(context.packageName, 0).longVersionCode
        } else {
            context.packageManager.getPackageInfo(context.packageName, 0).versionCode.toLong()
        }
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace();
    }
    return versionCode
}

fun getVersionName(context: Context): String? {
    try {
        return context.packageManager.getPackageInfo(context.packageName, 0).versionName
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace();
    }
    return ""
}

fun getUninstallAPKVersionName(ctx: Context, archiveFilePath: String): String? {
    val pm = ctx.packageManager
    val pakInfo = pm.getPackageArchiveInfo(archiveFilePath, PackageManager.GET_ACTIVITIES)
    return pakInfo?.versionName
}

fun getUninstallAPKVersionCode(ctx: Context, archiveFilePath: String): Long? {
    val pm = ctx.packageManager
    val pakInfo = pm.getPackageArchiveInfo(archiveFilePath, PackageManager.GET_ACTIVITIES)
    return (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) pakInfo?.longVersionCode
    else pakInfo?.versionCode?.toLong())
}

/**
 * 0代表相等，1代表version1大于version2，-1代表version1小于version2
 */
fun compareVersion(version1: String?, version2: String?): Int {
    if (version1 == version2) {
        return 0
    }
    version1 ?: return -1
    version2 ?: return 1

    if (version1.isNullOrEmpty()) return -1
    if (version2.isNullOrEmpty()) return 1

    val version1Array = version1.split("\\.".toRegex()).toTypedArray()
    val version2Array = version2.split("\\.".toRegex()).toTypedArray()

    val len1 = version1Array.size
    val len2 = version2Array.size

    if (len1 >= len2) {
        var index = 0
        while (index < len2) {
            val v1 =  version1Array[index].toIntOrNull() ?: 0
            val v2 =  version2Array[index].toIntOrNull() ?: 0
            when {
                (v1 - v2) == 0 -> { index++ }
                (v1 - v2) > 0 -> { return 1 }
                else -> { return -1 }
            }
        }

        if (len1 == index) {
            return 0
        }

        if (index == len2) {
            for (v in index until len1) {
                val vv1 = version1Array[v].toIntOrNull() ?: 0
                if (vv1 > 0) { return 1 }
            }
            return 0
        }
    } else if (len1 < len2) {
        var index = 0
        while (index < len1) {
            val v1 =  version1Array[index].toIntOrNull() ?: 0
            val v2 =  version2Array[index].toIntOrNull() ?: 0
            when {
                (v1 - v2) == 0 -> { index++ }
                (v1 - v2) > 0 -> { return 1 }
                else -> { return -1 }
            }
        }

        for (v in index until len2) {
            val vv2 = version2Array[v].toIntOrNull() ?: 0
            if (vv2 > 0) {
                return -1
            }
        }
        return 0
    }
    return 0
}