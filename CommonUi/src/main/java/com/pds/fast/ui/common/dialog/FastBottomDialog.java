package com.pds.fast.ui.common.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pds.fast.ui.common.R;

public class FastBottomDialog extends BaseFragmentDialog{
    protected boolean isCancelOnTouchOutside() {
        return false;
    }

    protected void setSoftInputMode(int mode) {
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setSoftInputMode(mode);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.BottomDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCanceledOnTouchOutside(isCancelOnTouchOutside());
            Window window = dialog.getWindow();
            if (window != null) {
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                if (isSetInputAdjustReSize()) {
                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                }
            }
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected boolean isSetInputAdjustReSize() {
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && getActivity() != null && dialog.getWindow() != null) {
            Window window = dialog.getWindow();
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            if (isFullHeight()) {
                window.setLayout(dm.widthPixels, ViewGroup.LayoutParams.MATCH_PARENT);
            } else {
                window.setLayout(dm.widthPixels, getContentHeight());
            }
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.gravity = gravityPosition();
            window.setAttributes(attributes);
        }
    }

    protected boolean isFullHeight() {
        return false;
    }

    protected int getContentHeight() {
        return ViewGroup.LayoutParams.WRAP_CONTENT;
    }


    protected int gravityPosition() {
        return Gravity.BOTTOM;
    }

    public interface DialogBack {
        void onBack(@NonNull Bundle bundle);
    }

    protected DialogBack mBack;

    public void setBack(DialogBack mBack) {
        this.mBack = mBack;
    }
}
