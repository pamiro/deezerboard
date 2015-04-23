package com.avg.dezerboard.events;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.avg.dezerboard.DezerApp;
import com.avg.dezerboard.ui.activity.TrackActivity;

import pm.me.deezerboard.VisualizerActivity;
import pm.me.deezerboard.activities.Player;
import pm.me.deezerboard.activities.SearchForTrack;

/**
 * Created by tanweer.ali on 1/27/2015.
 */
public class EventsReceiver extends BroadcastReceiver {

    private static EventsReceiver instance = new EventsReceiver();
    private EventsFragment fragment;
    private Activity activity;


    public static EventsReceiver getInstance() {
        return instance;
    }

    public void setFragment(EventsFragment fragment) {
        this.fragment = fragment;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d("Event", "event received:"+ action);

        if(action.equalsIgnoreCase(Events.REFRESH_SCREEN)){
            Bundle params = intent.getBundleExtra(DezerApp.PARAMS);
            this.fragment.eventRefreshScreen( 0 );
        }
        else if (action.equalsIgnoreCase(Events.EVENT_SEARCH_TRACKS)){
            intent = new Intent(DezerApp.instance, SearchForTrack.class);
            activity.startActivityForResult(intent,0);
        }
        else if (action.equalsIgnoreCase(Events.SHOW_BLOCKED_APPS)){
            if(activity!= null) {
                activity.startActivity(new Intent(activity, Player.class));
            }
        }
        else if (action.equalsIgnoreCase(Events.EVENT_SHOW_TRACKBAR)){
            if(activity!= null) {
                activity.startActivity(new Intent(activity, TrackActivity.class));
            }
        }
        else if (action.equalsIgnoreCase(Events.EVENT_SHOW_VISUALIZER)){
            if(activity!= null) {
                activity.startActivity(new Intent(activity, VisualizerActivity.class));
            }
        }
        else {
            if(activity !=null){

            }
        }
    }
}
