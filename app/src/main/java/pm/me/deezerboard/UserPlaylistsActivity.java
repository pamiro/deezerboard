package pm.me.deezerboard;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.deezer.sdk.model.Playlist;
import com.deezer.sdk.model.Track;
import com.deezer.sdk.network.connect.SessionStore;
import com.deezer.sdk.network.request.AsyncDeezerTask;
import com.deezer.sdk.network.request.DeezerRequest;
import com.deezer.sdk.network.request.DeezerRequestFactory;
import com.deezer.sdk.network.request.event.DeezerError;
import com.deezer.sdk.network.request.event.JsonRequestListener;
import com.deezer.sdk.network.request.event.OAuthException;
import com.deezer.sdk.player.TrackPlayer;
import com.deezer.sdk.player.event.OnPlayerProgressListener;
import com.deezer.sdk.player.event.OnPlayerStateChangeListener;
import com.deezer.sdk.player.event.PlayerState;
import com.deezer.sdk.player.event.PlayerWrapperListener;
import com.deezer.sdk.player.exception.TooManyPlayersExceptions;
import com.deezer.sdk.player.networkcheck.WifiAndMobileNetworkStateChecker;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class UserPlaylistsActivity extends PlayerActivity
        implements
        PlayerWrapperListener,
        OnPlayerProgressListener,
        OnPlayerStateChangeListener {

    private static final String TAG =  UserPlaylistsActivity.class.getName();

    /** The list of playlists of displayed by this activity. */
    private List<Playlist> mPlaylistList = new ArrayList<Playlist>();
    
    /** the Playlists list adapter */
    private ArrayAdapter<Playlist> mPlaylistAdapter;
    private TrackPlayer mTrackPlayer;
    
    private enum Option {
        none,
        fade_in_out,
    }
    
    private Option mOption;
    
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // restore existing deezer Connection
        new SessionStore().restore(mDeezerConnect, this);
        
        // Setup the UI
        setContentView(R.layout.activity_tracklists);
        setupPlaylistsList();
        setPlayerVisible(true);
        
        //build the player
        createPlayer();
        
        // fetch radio list 
        getUserPlaylists();
        
        mOption = Option.none;
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        super.onCreateOptionsMenu(menu);
        
        getMenuInflater().inflate(R.menu.playlist, menu);
        return true;
    }
    
    @Override
    @TargetApi(11)
    public boolean onOptionsItemSelected(final MenuItem item) {
        boolean res = true;
        
        switch (item.getItemId()) {
            case R.id.action_none:
                mOption = Option.none;
                break;
            case R.id.action_fade_in_out:
                mOption = Option.fade_in_out;
                break;
            default:
                res = super.onOptionsItemSelected(item);
                break;
        }
        return res;
    }
    
    /**
     * Setup the List UI
     */
    private void setupPlaylistsList() {
        mPlaylistAdapter = new ArrayAdapter<Playlist>(this,
                R.layout.item_title_cover, mPlaylistList) {
            
            @Override
            public View getView(final int position, final View convertView, final ViewGroup parent) {
                Playlist playlist = getItem(position);
                
                View view = convertView;
                if (view == null) {
                    LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = vi.inflate(R.layout.item_title_cover, null);
                }
                
                
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                textView.setText(playlist.getTitle());
                
                ImageView imageView = (ImageView) view.findViewById(android.R.id.icon);
                Picasso.with(UserPlaylistsActivity.this).load(playlist.getPictureUrl())
                        .into(imageView);
                
                return view;
            }
        };
        ListView listview = (ListView) findViewById(android.R.id.list);
        listview.setAdapter(mPlaylistAdapter);
        listview.setOnItemClickListener(new OnItemClickListener() {
            
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view,
                    final int position, final long id) {
                Playlist playlist = mPlaylistList.get(position);
                //mTrackPlayer.playPlaylist(playlist.getId());
                setPlayerVisible(true);
            }
        });
    }
    /**
     * Creates the PlaylistPlayer
     */
    private void createPlayer() {
        try {
//            mPlaylistPlayer = new PlaylistPlayer(getApplication(), mDeezerConnect,
//                    new WifiAndMobileNetworkStateChecker());
            mTrackPlayer = new TrackPlayer(getApplication(), mDeezerConnect,
                    new WifiAndMobileNetworkStateChecker());
            mTrackPlayer.addPlayerListener(this);
            mTrackPlayer.addOnPlayerStateChangeListener(this);
            mTrackPlayer.addOnPlayerProgressListener(this);

            mTrackPlayer.playTrack(98363604);
            //mTrackPlayer.setStereoVolume(0,0);
           // mTrackPlayer.pause();
            Log.d(TAG, " position " + mTrackPlayer.getTrackDuration());
       //     mTrackPlayer.seek(120000);
            setAttachedPlayer(mTrackPlayer);
        }
        catch (OAuthException e) {
            handleError(e);
        }
        catch (TooManyPlayersExceptions e) {
            handleError(e);
        }
        catch (DeezerError e) {
            handleError(e);
        }
    }
    
    /**
     * Search for all radios splitted by genre
     */
    private void getUserPlaylists() {
        DeezerRequest request = DeezerRequestFactory.requestCurrentUserPlaylists();
        AsyncDeezerTask task = new AsyncDeezerTask(mDeezerConnect,
                new JsonRequestListener() {
                    
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onResult(final Object result, final Object requestId) {
                        
                        mPlaylistList.clear();
                        
                        try {
                            mPlaylistList.addAll((List<Playlist>) result);
                        }
                        catch (ClassCastException e) {
                            handleError(e);
                        }
                        
                        if (mPlaylistList.isEmpty()) {
                            Toast.makeText(UserPlaylistsActivity.this, getResources()
                                    .getString(R.string.no_results), Toast.LENGTH_LONG).show();
                        }
                        
                        mPlaylistAdapter.notifyDataSetChanged();
                    }
                    
                    @Override
                    public void onUnparsedResult(final String response, final Object requestId) {
                        handleError(new DeezerError("Unparsed reponse"));
                    }
                    
                    
                    @Override
                    public void onException(final Exception exception,
                            final Object requestId) {
                        handleError(exception);
                    }
                    
                    
                });
        task.execute(request);
    }
    
    @Override
    protected void onSkipToNextTrack() {
        mTrackPlayer.skipToNextTrack();
    }
    
    @Override
    protected void onSkipToPreviousTrack() {
        mTrackPlayer.skipToPreviousTrack();
    }
    
    
    //////////////////////////////////////////////////////////////////////////////////////
    // Player listener
    //////////////////////////////////////////////////////////////////////////////////////
    
    @Override
    public void onPlayTrack(final Track track) {
        displayTrack(track);
//        if (mOption == Option.fade_in_out) {
//            applyFadeInOut(0);
//        }
    }
    
    @Override
    public void onTrackEnded(final Track track) {
    }
    
    @Override
    public void onAllTracksEnded() {
    }
    
    @Override
    public void onRequestException(final Exception e, final Object requestId) {
        handleError(e);
    }


    static boolean once2 = true;
    @Override
    public void onPlayerProgress(final long timePosition) {
        Log.d(TAG, "position " + timePosition);
        if(/*timePosition > 10000 &&*/ once2) {
            //mTrackPlayer.pause();
            once2 = false;
          //  mTrackPlayer.seek(60000);
            //mTrackPlayer.play();
            Log.d(TAG, "position after seek : " + mTrackPlayer.getPosition());
           // mTrackPlayer.setStereoVolume(100, 100);


        }
//        switch (mOption) {
//            case fade_in_out:
//                applyFadeInOut(timePosition);
//            case none:
//            default:
//                break;
//        }
    }
    
    private static final long FADE_DURATION = 5000;
    
    private static final long PROGRESS_LONG = 1000L;
    private static final long PROGRESS_SMALL = 50L;
    
    private long mProgressInterval = 1000;
    
    private void applyFadeInOut(final long timePosition) {
        long trackDuration = mTrackPlayer.getTrackDuration();
        
        float factor = 1.0f / FADE_DURATION;
        
        if (trackDuration > (FADE_DURATION * 2)) {
            float volume;
            if (timePosition < FADE_DURATION) {
                // Fade in 
                volume = timePosition * factor;
            } else if (timePosition > (trackDuration - FADE_DURATION)) {
                volume = (trackDuration - timePosition) * factor;
            } else {
                volume = 1.0f;
            }
            
            if ((mProgressInterval == PROGRESS_LONG) && ((timePosition < FADE_DURATION)
                    || (timePosition >= (trackDuration - FADE_DURATION - PROGRESS_LONG)))) {
                mProgressInterval = PROGRESS_SMALL;
                mTrackPlayer.setPlayerProgressInterval(mProgressInterval);
            } else if ((mProgressInterval == PROGRESS_SMALL)
                    && (timePosition > (FADE_DURATION + PROGRESS_SMALL))) {
                mProgressInterval = PROGRESS_LONG;
                mTrackPlayer.setPlayerProgressInterval(mProgressInterval);
            }
            
            volume *= volume;
            Log.i("Volume", "Set volume to " + volume);
      //ß      mTrackPlayer.setStereoVolume(volume, volume);
        }
        
    }

    static boolean once = true;

    @Override
    public void onPlayerStateChange(PlayerState var1, long var2) {
        Log.d(TAG, "here : " + var1.name());
        if(var1 == PlayerState.PLAYING && once)
        {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    mTrackPlayer.seek(60000);

                    //showPlayerState(state);
                    //showPlayerProgress(timePosition);
                }
            });
            once = false;
        //    new DelayedTask().execute();
        }
    }


    public class DelayedTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            mTrackPlayer.seek(10000);
            Log.d(TAG, "position after seek : " + mTrackPlayer.getPosition());
            return null;
        }
    }
}
