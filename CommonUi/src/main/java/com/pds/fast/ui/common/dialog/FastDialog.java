package com.pds.fast.ui.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;

import androidx.annotation.ColorRes;
import androidx.annotation.StringRes;

import com.pds.fast.ui.common.R;

public class FastDialog extends Dialog {

    public Context context;

    public FastDialog(Context context) {
        super(context, R.style.StyleDialog);
        this.context = context;
        init();

    }

    public FastDialog(Context context, int style) {
        super(context, style);
        this.context = context;
        init();
    }

    private void init() {
        Window window = getWindow();
        if (window != null) {
            // 解决dialog闪屏问题
            window.setWindowAnimations(R.style.NullAnimationDialog);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(Color.TRANSPARENT);
            }
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }

    protected String getString(@StringRes int id) {
        return context.getResources().getString(id);
    }

    protected int getColor(@ColorRes int id) {
        return context.getResources().getColor(id);
    }

}
