package com.pds.fast.assist;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.text.TextUtils;

import androidx.annotation.ColorRes;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Assist {
    public static Application application;

    public static void init(Application app) {
        application = app;
    }

    public static int dip2px(Context context, double dpValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5);
    }

    public static int getColor(Context context, @ColorRes int colorId) {
        return context.getResources().getColor(colorId);
    }

    public static boolean assertContextIllegal(Context context) {
        if (context == null) {
            return true;
        }
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            return activity.isDestroyed() || activity.isFinishing();
        } else if (context instanceof ContextWrapper) {
            return assertContextIllegal(((ContextWrapper) context).getBaseContext());
        }
        return false;
    }

    public static int getWidth(Context var0) {
        return var0.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getHeight(Context var0) {
        return var0.getResources().getDisplayMetrics().heightPixels;
    }

    public static boolean isRunningForeground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = cn.getPackageName();
        return !TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(context.getPackageName());
    }

    public static boolean isSameDay(long timestamp) {
        Date date = new Date();
        date.setTime(timestamp);
        return isSameDay(date);
    }

    public static boolean isSameDay(Date date) {
        return isEquals(date, "yyyy-MM-dd");
    }

    private static boolean isEquals(Date date, String format) {
        //当前时间
        Date now = new Date();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sf = new SimpleDateFormat(format);
        //获取今天的日期
        String nowDay = sf.format(now);
        //对比的时间
        String day = sf.format(date);
        return day.equals(nowDay);
    }
}
