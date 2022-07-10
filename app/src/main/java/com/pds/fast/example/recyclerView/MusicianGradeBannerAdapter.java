package com.pds.fast.example.recyclerView;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pds.fast.ui.R;

public class MusicianGradeBannerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static String[] colors = {"#ff0000", "#00ff00", "#00ffcc", "#ddff00", "#001f00", "#34ff00", "#04ff00", "#009f00", "#012f00", "#00ff10", "#011100"};

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rl_banner, parent, false);
        return new RlHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        holder.itemView.setBackgroundColor(Color.parseColor(colors[position]));
    }

    @Override
    public int getItemCount() {
        return colors.length;
    }

    private class RlHolder extends RecyclerView.ViewHolder {

        public RlHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
