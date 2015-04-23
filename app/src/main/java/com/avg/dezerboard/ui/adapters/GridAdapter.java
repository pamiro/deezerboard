package com.avg.dezerboard.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.avg.dezerboard.DezerApp;
import com.avg.dezerboard.events.Events;
import com.deezer.sdk.network.request.event.DeezerError;
import com.deezer.sdk.player.exception.TooManyPlayersExceptions;

import org.json.JSONException;
import org.json.JSONObject;

import pm.me.deezerboard.R;
import pm.me.deezerboard.activities.SearchForTrack;
import pm.me.deezerboard.engine.TrackFragmentPlayer;

/**
 * Created by tanweer.ali on 2/5/2015.
 */
public class GridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = GridAdapter.class.getName();
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

        public CellViewHolder(View v, final int position) {
            super(v);

            Log.d(TAG, "CellViewHolder()" + position);

            cover = (ImageView) v.findViewById(R.id.cover);
            button = (ImageView) v.findViewById(R.id.button);

            update(position);
        }

        private void update(final int position) {
            JSONObject obj = DezerApp.trackInCells[position];

            if(DezerApp.trackInCells[position] == null) {

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DezerApp.positionToSelect = position;
                        DezerApp.getLocalBrdcstMgr().sendBroadcast(new Intent(Events.EVENT_SEARCH_TRACKS));
                    }
                });

                if (position % 2 == 1) {
                    cover.setImageResource(R.drawable.tile_b);
                }
            }
            else {

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // PLAY
                        try {
                            new TrackFragmentPlayer.PlayerTask(true).play(new TrackFragmentPlayer.TaskSpecification(DezerApp.trackInCells[position].getLong("id"), 0, 5000));
                        } catch (DeezerError deezerError) {
                            deezerError.printStackTrace();
                        } catch (TooManyPlayersExceptions tooManyPlayersExceptions) {
                            tooManyPlayersExceptions.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

                button.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        DezerApp.positionToSelect = position;
                        DezerApp.getLocalBrdcstMgr().sendBroadcast(new Intent(Events.EVENT_SEARCH_TRACKS));
                        return true;
                    }
                });

                String imageURL = null;
                try {
                    JSONObject tmp = null;

                    tmp = obj.getJSONObject("album");

                    if(tmp != null) {
                        imageURL = tmp.getString("cover");
                    }
                    tmp = obj.getJSONObject("artist");
                    if(tmp != null) {
                        if(imageURL == null) {
                            imageURL = tmp.getString("picture");
                         }
                    }

                    if(imageURL!= null)
                        new SearchForTrack.ImageDownloader(cover).execute(imageURL);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //------------------------------------------------------------------

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Log.d(TAG, "onCreateViewHolder " + viewType);

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
        Log.d(TAG, "onBindViewHolder " + position);
        CellViewHolder vh = (CellViewHolder) holder;
        vh.update(position);
    }

    @Override
    public int getItemCount() {
        return (6);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
