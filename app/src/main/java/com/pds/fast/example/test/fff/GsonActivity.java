package com.pds.fast.example.test.fff;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.LogPrinter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class GsonActivity extends AppCompatActivity {

    Handler one = new Handler(Looper.getMainLooper());
    Handler two = new Handler(Looper.getMainLooper());
    HandlerThread handlerThread = new HandlerThread("handlerThread");

    private TestHolder holder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<TaoWorkTagModel> tagModels = new ArrayList<>();

        TaoWorkTagModel model = new TaoWorkTagModel("1", "2");
        TaoWorkTagModel modelOne = new TaoWorkTagModel("3", "4");
        tagModels.add(model);
        tagModels.add(modelOne);

        Log.d("GsonActivity", new Gson().toJson(tagModels));

        one.post(oneRun);

        two.post(twoRun);
        two.post(twoRun);
        two.post(twoRun);

        two.post(twoRun);
        two.post(twoRun);
        two.post(twoRun);
        two.post(twoRun);

        // two.removeCallbacks(twoRun);
        // two.removeCallbacksAndMessages(twoRun);
        Log.e("Test", "dump start");



    }

    private final Runnable oneRun = () -> {
        Log.e("Test1", "oneRun");

    };

    private final Runnable twoRun = () -> {
        Log.e("Test2", "twoRun");
    };

    private void testHandlerThread() {
        handlerThread.start();


        Handler three = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Log.d("Test11111", null == msg.obj ? "111" : msg.obj.toString());
            }
        };

        three.sendEmptyMessage(10000);

        three.sendMessageDelayed(message1(), 2000);

        three.sendMessageDelayed(message(), 2000);
        three.sendMessageDelayed(message(), 2000);
        three.sendMessageDelayed(message(), 2000);

        three.removeCallbacksAndMessages(holderHandler);

        three.dump(new LogPrinter(Log.ERROR, "Test111"), "handler");
    }

    private TestHandlerObj holderHandler = new TestHandlerObj();

    private Message message() {

        // obj使用file会接收不到消息
        // File file = new File("");
        Message message1 = Message.obtain();
        message1.what = 301;
        message1.obj = holderHandler;
        return message1;
    }

    private Message message1() {

        // obj使用file会接收不到消息
        // File file = new File("");
        Message message1 = Message.obtain();
        message1.what = 301;
        message1.obj = new TestHandlerObj();
        return message1;
    }

    public static class TaoWorkTagModel {

        public TaoWorkTagModel(String type, String word) {
            this.type = type;
            this.word = word;
        }

        public String type;
        public String word;
    }
}
