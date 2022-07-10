package com.pds.fast.ui.common.listener;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class RecyclerViewPositionListener extends RecyclerView.OnScrollListener {
    private final RecyclerView recyclerView;
    private final Callback callback;
    private int currentPosition = -1;

    public RecyclerViewPositionListener(RecyclerView recyclerView, Callback callback) {
        this.recyclerView = recyclerView;
        this.callback = callback;
    }

    @Override
    public void onScrollStateChanged(@NonNull @NotNull RecyclerView recyclerView, int newState) {
        checkPosition();
    }

    @Override
    public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
        checkPosition();
    }

    private void checkPosition() {
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            int position = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
            if (position >= 0 && currentPosition != position) {
                callback.onPositionChanged(position);
                currentPosition = position;
            }
        }
    }

    public interface Callback {
        void onPositionChanged(int position);
    }
}
