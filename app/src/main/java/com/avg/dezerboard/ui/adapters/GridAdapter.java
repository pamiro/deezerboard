package com.avg.dezerboard.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.avg.dezerboard.DezerApp;
import com.avg.dezerboard.events.Events;

import pm.me.deezerboard.R;

/**
 * Created by tanweer.ali on 2/5/2015.
 */
public class GridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;

    private static int cellHeight = 0;
    private static int cellWidth;

    public GridAdapter(Context ctx) {
        context = ctx;
    }

    public void setCellHeight(int width, int height){
        this.cellHeight = height;
        this.cellWidth = width;
    }

    //------------------------------------------------------------------
    // define holders
    //------------------------------------------------------------------
    // ViewHolder #1
    public static class CellViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView cover;
        public ImageView button;

        public CellViewHolder(View v, int position) {
            super(v);
            cover = (ImageView) v.findViewById(R.id.cover);
            button = (ImageView) v.findViewById(R.id.button);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DezerApp.getLocalBrdcstMgr().sendBroadcast(new Intent(Events.EVENT_SEARCH_TRACKS));
                }
            });

            if(position%2 == 1){
                cover.setImageResource(R.drawable.tile_b);
            }

//            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(cellWidth, cellHeight);
//            v.setLayoutParams(params);
        }
    }
    //------------------------------------------------------------------

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh = null;
        // create the Main Card
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_item_cell, parent, false);

        // set the view's size, margins, paddings and layout parameters
        vh = new CellViewHolder(v, viewType);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        switch (position) {

        };

    }

    @Override
    public int getItemCount() {
        return (6);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position == 3 || position==4) {
            return 0;
        } else {
            return 1;
        }
    }
}
