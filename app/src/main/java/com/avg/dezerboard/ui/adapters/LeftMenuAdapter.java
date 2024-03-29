package com.avg.dezerboard.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avg.dezerboard.DezerApp;
import com.avg.dezerboard.events.Events;

import pm.me.deezerboard.R;

/**
 * Created by tanweer.ali on 2/5/2015.
 */
public class LeftMenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final Context context;
    String[] options;

    public LeftMenuAdapter(Context ctx){
        context = ctx;
        Resources res = ctx.getResources();
        options = res.getStringArray(R.array.options);
    }

    //------------------------------------------------------------------
    // define holders
    //------------------------------------------------------------------
    // ViewHolder #1
    public static class OptionViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView title;
        public OptionViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.title);
        }
    }

    // ViewHolder #1
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView header;
        public HeaderViewHolder(View v) {
            super(v);
            header = (ImageView) v.findViewById(R.id.header);
        }
    }

    //------------------------------------------------------------------

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh = null;
        switch (viewType) {
            case 0:
                // create the Main Card
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_item_option_header, parent, false);

                // set the view's size, margins, paddings and layout parameters
                vh = new HeaderViewHolder(v);
                break;


            case 1:
                // create the AppRisk Card
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_item_option, parent, false);

                // set the view's size, margins, paddings and layout parameters
                vh = new OptionViewHolder(v);
                break;
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        switch(position){

            ///
            case 1: {
                OptionViewHolder optViewHolder = (OptionViewHolder) holder;
                optViewHolder.title.setText(options[position-1]);
                optViewHolder.title.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DezerApp.getLocalBrdcstMgr().sendBroadcast(new Intent(Events.EVENT_SHOW_VISUALIZER));
                    }
                });
            }
            break;

            case 2: {
                OptionViewHolder optViewHolder = (OptionViewHolder) holder;
                optViewHolder.title.setText(options[position-1]);
                optViewHolder.title.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DezerApp.getLocalBrdcstMgr().sendBroadcast(new Intent(Events.EVENT_SHOW_TRACKBAR));
                    }
                });
            }
            break;

        };

    }

    @Override
    public int getItemCount() {
        return (options.length)+1;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return 0;
        }
        else {
            return 1;
        }
    }
}
