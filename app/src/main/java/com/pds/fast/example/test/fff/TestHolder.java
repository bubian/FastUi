package com.pds.fast.example.test.fff;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class TestHolder {
    private final View view;
    private final Handler handler = new Handler(Looper.getMainLooper());

    public TestHolder(Activity context) {
        Log.d("TestHolder", "start");
        view = new View(context);
        View v = context.getWindow().getDecorView();
        ((ViewGroup) v).addView(view);
        view.postDelayed(runnable, 1_000);
    }

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            view.setVisibility(View.VISIBLE);
            Log.d("TestHolder", view.toString());
            view.postDelayed(runnable, 1_000);
        }
    };

    public View getView() {
        return view;
    }
}
