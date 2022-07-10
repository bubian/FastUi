package com.pds.fast.ui.common.recyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class HeaderGrid extends RecyclerView {
    public HeaderGrid(@NonNull Context context) {
        this(context, null);
    }

    public HeaderGrid(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        boolean canScrollHorizontally = canScrollHorizontally(-1) || canScrollHorizontally(1);
        boolean canScrollVertically = canScrollVertically(-1) || canScrollVertically(1);
        if (canScrollHorizontally || canScrollVertically) {
            ViewParent parent = getParent();
            if (parent != null) {
                parent.requestDisallowInterceptTouchEvent(true);
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
