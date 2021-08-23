package com.pds.fast.assist;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;

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
}
