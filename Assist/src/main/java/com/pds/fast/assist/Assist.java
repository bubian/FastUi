package com.pds.fast.assist;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.text.TextUtils;

import androidx.annotation.ColorRes;

public class Assist {
    public static Application application;

    public static void init(Application app){
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
}
