package com.avg.dezerboard.events;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Switch;

import com.avg.dezerboard.DezerApp;

import pm.me.deezerboard.R;

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
        else if (action.equalsIgnoreCase(Events.SHOW_DISMISSED_THREATS)){
        }
        else {
            if(activity !=null){

            }
        }
    }
}
