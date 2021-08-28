package com.pds.fast.ui.common.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.StyleRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class BaseFragmentDialog extends Fragment implements DialogInterface.OnCancelListener, DialogInterface.OnDismissListener{
    @IntDef({STYLE_NORMAL, STYLE_NO_TITLE, STYLE_NO_FRAME, STYLE_NO_INPUT})
    @Retention(RetentionPolicy.SOURCE)
    private @interface DialogStyle {
    }

    public static final int STYLE_NORMAL = 0;
    public static final int STYLE_NO_TITLE = 1;
    public static final int STYLE_NO_FRAME = 2;
    public static final int STYLE_NO_INPUT = 3;
    private static final String SAVED_DIALOG_STATE_TAG = "android:savedDialogState";
    private static final String SAVED_STYLE = "android:style";
    private static final String SAVED_THEME = "android:theme";
    private static final String SAVED_CANCELABLE = "android:cancelable";
    private static final String SAVED_SHOWS_DIALOG = "android:showsDialog";
    private static final String SAVED_BACK_STACK_ID = "android:backStackId";
    int mStyle = STYLE_NORMAL;
    int mTheme = 0;
    boolean mCancelable = true;
    boolean mShowsDialog = true;
    int mBackStackId = -1;
    Dialog mDialog;
    boolean mViewDestroyed;
    boolean mDismissed;
    boolean mShownByMe;
    private int windowType = -1;

    public BaseFragmentDialog() {
    }

    public void setStyle(@DialogStyle int style, @StyleRes int theme) {
        mStyle = style;
        if (mStyle == STYLE_NO_FRAME || mStyle == STYLE_NO_INPUT) {
            mTheme = android.R.style.Theme_Panel;
        }
        if (theme != 0) {
            mTheme = theme;
        }
    }

    public void show(Context context) {
        if (context instanceof FragmentActivity) {
            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            show(fragmentTransaction, context.getClass().getSimpleName());
        }
    }

    public void show(FragmentManager manager, String tag) {
        this.mDismissed = false;
        this.mShownByMe = true;
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitNowAllowingStateLoss();
    }

    public int show(FragmentTransaction transaction, String tag) {
        this.mDismissed = false;
        this.mShownByMe = true;
        transaction.add(this, tag);
        this.mViewDestroyed = false;
        this.mBackStackId = transaction.commitAllowingStateLoss();
        return this.mBackStackId;
    }

    public void showNow(FragmentManager manager, String tag) {
        this.mDismissed = false;
        this.mShownByMe = true;
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitNow();
    }

    public void dismiss() {
        //modified to true
        this.dismissInternal(true);
    }

    public void dismissAllowingStateLoss() {
        this.dismissInternal(true);
    }

    void dismissInternal(boolean allowStateLoss) {
        if (!this.mDismissed) {
            this.mDismissed = true;
            this.mShownByMe = false;
            if (this.mDialog != null) {
                this.mDialog.dismiss();
            }

            this.mViewDestroyed = true;
            FragmentManager fragmentManager = getFragmentManager();
            if (fragmentManager == null) {
                return;
            }
            if (this.mBackStackId >= 0) {
                fragmentManager.popBackStack(this.mBackStackId, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                this.mBackStackId = -1;
            } else {
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.remove(this);
                if (allowStateLoss) {
                    ft.commitNowAllowingStateLoss();
                } else {
                    ft.commitNow();
                }
            }

        }
    }

    public Dialog getDialog() {
        return this.mDialog;
    }

    @StyleRes
    public int getTheme() {
        return this.mTheme;
    }

    public void setCancelable(boolean cancelable) {
        this.mCancelable = cancelable;
        if (this.mDialog != null) {
            this.mDialog.setCancelable(cancelable);
        }

    }

    public boolean isCancelable() {
        return this.mCancelable;
    }

    public void setShowsDialog(boolean showsDialog) {
        this.mShowsDialog = showsDialog;
    }

    public boolean getShowsDialog() {
        return this.mShowsDialog;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        if (!this.mShownByMe) {
            this.mDismissed = false;
        }
    }

    public void onDetach() {
        super.onDetach();
        if (!this.mShownByMe && !this.mDismissed) {
            this.mDismissed = true;
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mShowsDialog = true;
        if (savedInstanceState != null) {
            mStyle = savedInstanceState.getInt(SAVED_STYLE, STYLE_NORMAL);
            mTheme = savedInstanceState.getInt(SAVED_THEME, 0);
            mCancelable = savedInstanceState.getBoolean(SAVED_CANCELABLE, true);
            mShowsDialog = savedInstanceState.getBoolean(SAVED_SHOWS_DIALOG, mShowsDialog);
            mBackStackId = savedInstanceState.getInt(SAVED_BACK_STACK_ID, -1);
        }

    }

    @NonNull
    public LayoutInflater onGetLayoutInflater(@Nullable Bundle savedInstanceState) {
        if (!this.mShowsDialog) {
            return super.onGetLayoutInflater(savedInstanceState);
        } else {
            this.mDialog = this.onCreateDialog(savedInstanceState);
            this.setupDialog(this.mDialog, this.mStyle);
            return (LayoutInflater) this.mDialog.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void setupDialog(Dialog dialog, int style) {
        switch (style) {
            case STYLE_NO_INPUT:
                dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            case STYLE_NO_FRAME:
            case STYLE_NO_TITLE:
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            default:
        }
    }

    @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new Dialog(this.getActivity(), this.getTheme());
    }

    @Override
    public void onCancel(DialogInterface dialog) {
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (!this.mViewDestroyed) {
            this.dismissInternal(true);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (this.mShowsDialog) {
            View view = this.getView();
            if (view != null) {
                if (view.getParent() != null) {
                    throw new IllegalStateException("DialogFragment can not be attached to a container view");
                }

                this.mDialog.setContentView(view);
            }

            Activity activity = this.getActivity();
            if (activity != null) {
                this.mDialog.setOwnerActivity(activity);
            }

            this.mDialog.setCancelable(this.mCancelable);
            this.mDialog.setOnCancelListener(this);
            this.mDialog.setOnDismissListener(this);
            if (savedInstanceState != null) {
                Bundle dialogState = savedInstanceState.getBundle(SAVED_DIALOG_STATE_TAG);
                if (dialogState != null) {
                    this.mDialog.onRestoreInstanceState(dialogState);
                }
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (this.mDialog != null) {
            this.mViewDestroyed = false;
            if (windowType != -1 && this.mDialog.getWindow() != null) {
                this.mDialog.getWindow().setType(windowType);
            }
            this.mDialog.show();
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mDialog != null) {
            Bundle dialogState = mDialog.onSaveInstanceState();
            if (dialogState != null) {
                outState.putBundle(SAVED_DIALOG_STATE_TAG, dialogState);
            }
        }
        if (mStyle != STYLE_NORMAL) {
            outState.putInt(SAVED_STYLE, mStyle);
        }
        if (mTheme != 0) {
            outState.putInt(SAVED_THEME, mTheme);
        }
        if (!mCancelable) {
            outState.putBoolean(SAVED_CANCELABLE, mCancelable);
        }
        if (!mShowsDialog) {
            outState.putBoolean(SAVED_SHOWS_DIALOG, mShowsDialog);
        }
        if (mBackStackId != -1) {
            outState.putInt(SAVED_BACK_STACK_ID, mBackStackId);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (this.mDialog != null) {
            this.mDialog.hide();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (this.mDialog != null) {
            this.mViewDestroyed = true;
            this.mDialog.dismiss();
            this.mDialog = null;
        }

    }

    public int getWindowType() {
        return windowType;
    }

    public void setWindowType(int windowType) {
        this.windowType = windowType;
    }

}
