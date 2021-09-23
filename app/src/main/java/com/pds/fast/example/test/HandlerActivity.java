package com.pds.fast.example.test;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.pds.fast.ui.R;

public class HandlerActivity extends Activity {

    private View view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handler);
        view = findViewById(R.id.handler);
        view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                Log.e("HandlerActivity", "onViewAttachedToWindow");
                view.postDelayed(testRunnable,15000);
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                Log.e("HandlerActivity", "onViewDetachedFromWindow");
            }
        });
    }

    // activity销毁，依然会执行，这样内存泄漏
    private final Runnable testRunnable = new Runnable() {
        @Override
        public void run() {
            Log.e("HandlerActivity", "wo is HandlerActivity run");
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("HandlerActivity", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("HandlerActivity", "onDestroy");
    }
}
