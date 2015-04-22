package pm.me.deezerboard.engine;

import android.util.Log;

import com.avg.dezerboard.DezerApp;
import com.deezer.sdk.model.Track;
import com.deezer.sdk.network.request.event.DeezerError;
import com.deezer.sdk.player.TrackPlayer;
import com.deezer.sdk.player.event.OnPlayerProgressListener;
import com.deezer.sdk.player.event.OnPlayerStateChangeListener;
import com.deezer.sdk.player.event.PlayerState;
import com.deezer.sdk.player.event.PlayerWrapperListener;
import com.deezer.sdk.player.exception.TooManyPlayersExceptions;
import com.deezer.sdk.player.networkcheck.WifiAndMobileNetworkStateChecker;


/**
 * Created by Pavel Mironchyk on 22/04/15.
 */
public class TrackFragmentPlayer {


    public static class TaskSpecification {
        public long TrackId;
        public long startPosition;
        public long endPosition;

        public TaskSpecification(long id, long start, long end) {
            TrackId = id;
            startPosition = start;
            endPosition = end;
        }
    }

    public static class PlayerTask implements
            OnPlayerStateChangeListener,
            OnPlayerProgressListener,
            PlayerWrapperListener
    {

        private static final String TAG = PlayerTask.class.getName();

        TrackPlayer mPlayer;
        TaskSpecification activeSpec;
        boolean onTimeSeek;
        boolean mAutoRelease;


        public PlayerTask(boolean auto) throws DeezerError, TooManyPlayersExceptions {
            mPlayer = new TrackPlayer(DezerApp.instance, DezerApp.deezerConnect, new WifiAndMobileNetworkStateChecker());

            mPlayer.addOnPlayerStateChangeListener(this);
            mPlayer.addPlayerListener(this);
            mPlayer.addOnPlayerProgressListener(this);

            mPlayer.setStereoVolume(0.0f, 0.0f);

            mAutoRelease = auto;
        }

        public void play(TaskSpecification spec)
        {
            activeSpec = spec;
            if(activeSpec.startPosition != 0)
                onTimeSeek = true;
            else
                onTimeSeek = false;

            mPlayer.playTrack(activeSpec.TrackId);
            mPlayer.setStereoVolume(0.0f,0.0f);
            mPlayer.seek(activeSpec.startPosition);
        }

        @Override
        public void onPlayerStateChange(PlayerState playerState, long l) {
            Log.d(TAG, "onPlayerStateChange(" + playerState.toString() + ", " + l + ")");
            mPlayer.setStereoVolume(0.0f,0.0f);

//            if(playerState.equals(PlayerState.PLAYING) && onTimeSeek) {
//                onTimeSeek = false;
//                mPlayer.seek(activeSpec.startPosition);
//            }
        }

        @Override
        public void onPlayerProgress(long position) {
            Log.d(TAG, "onPlayerProgress() : " + position);

            if(onTimeSeek) {
                onTimeSeek = false;
                mPlayer.seek(activeSpec.startPosition);
            }

            if(position >= activeSpec.startPosition) {
                mPlayer.setStereoVolume(1.0f,1.0f);
            }

            if(position >= activeSpec.endPosition) {
                mPlayer.stop();
                if(mAutoRelease)
                  mPlayer.release();
            }
        }

        public void release() {
            mPlayer.release();
        }

        @Override
        public void onAllTracksEnded() {
            Log.d(TAG, "onAllTracksEnded()");
        }

        @Override
        public void onPlayTrack(Track track) {
            Log.d(TAG, "onPlayTrack()");
        }

        @Override
        public void onTrackEnded(Track track) {
            Log.d(TAG, "onTrackEnded()");
        }

        @Override
        public void onRequestException(Exception e, Object o) {
            Log.d(TAG, "onRequestException()");
            mPlayer.release();
        }
    };
}
