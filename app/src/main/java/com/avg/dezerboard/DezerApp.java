package com.avg.dezerboard;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.deezer.sdk.network.connect.DeezerConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import pm.me.deezerboard.Constants;


/**
 * Created by Pavel Mironchyk on 23/12/14.
 */
public class DezerApp extends Application {

    private static final String TAG = DezerApp.class.getName();
    private static Context context;
    private static LocalBroadcastManager localBrdcstMgr;

    public static final String PARAMS = "params";
    public static DeezerConnect deezerConnect = null;

    public static DezerApp instance = null;
    public static Typeface font;

    public static JSONObject lastSelectedTrack = null;
    public static int positionToSelect = -1;


    public static String title = "TheNextWeb2015";

    public static JSONObject[] trackInCells = new JSONObject[] {
        null, null, null, null, null, null
    };

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        localBrdcstMgr = LocalBroadcastManager.getInstance(this);
        deezerConnect = new DeezerConnect(this, Constants.APP_ID);
        instance = this;

        font = Typeface.createFromAsset(getAssets(), "pixelsix14.ttf");

        load("default");

    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        save("default");
    }

    public static void save(String boarname) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("title", title);
            JSONArray arr = new JSONArray();
            for(JSONObject t : trackInCells) {
                arr.put(t);
            }

            obj.put("tracks", arr);
            FileOutputStream outputStream;

            try {
                outputStream = DezerApp.instance.openFileOutput(boarname, Context.MODE_PRIVATE);
                outputStream.write(obj.toString().getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            ///
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void load(String boardname) {

        FileInputStream inputStream;

        try {
            inputStream = DezerApp.instance.openFileInput(boardname);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            String str = new String(buffer, "UTF-8");

            JSONObject obj = new JSONObject(str);

            if(obj.getString("title") != null) {
                title = obj.getString("title");
            }
            JSONArray arr = obj.getJSONArray("tracks");
            for(int i = 0; i < arr.length(); i ++) {

                trackInCells[i] = arr.getJSONObject(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Context getContext(){
        return context;
    }

    public static LocalBroadcastManager getLocalBrdcstMgr() {
        return localBrdcstMgr;
    }

    public static void sendLocalEvent(String event){
        DezerApp.getLocalBrdcstMgr().sendBroadcast(new Intent(event));
    }

    public static void sendLocalEvent(String event, Bundle params){
        final Intent intent = new Intent(event);
        intent.putExtra(PARAMS, params);
        DezerApp.getLocalBrdcstMgr().sendBroadcast(intent);
    }


    /**
     * Check if the VPN service is already running in the background.
     *
     * @param serviceClass
     * @param ctx
     * @return
     */
    public static boolean isServiceRunning(Class<?> serviceClass, Context ctx) {
        ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }



}
