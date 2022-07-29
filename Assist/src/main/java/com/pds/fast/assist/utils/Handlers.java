package com.pds.fast.assist.utils;

import android.os.Handler;
import android.os.Looper;

public class Handlers {
    public static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

    public static void postAutoSwitchThread(Runnable runnable) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            Handlers.MAIN_HANDLER.post(runnable);
        } else {
            runnable.run();
        }
    }
}

