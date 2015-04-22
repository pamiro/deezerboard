package pm.me.deezerboard.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.avg.dezerboard.DezerApp;
import com.deezer.sdk.network.request.event.DeezerError;
import com.deezer.sdk.player.exception.TooManyPlayersExceptions;

import pm.me.deezerboard.R;
import pm.me.deezerboard.engine.TrackFragmentPlayer;

public class Player extends Activity {

    Button mFirst, mSecond, mSearch;

    TrackFragmentPlayer.PlayerTask mStaticPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        mFirst = (Button) findViewById(R.id.button_first);
        mSecond = (Button) findViewById(R.id.button_second);
        mSearch = (Button) findViewById(R.id.button_search);

        try {
            mStaticPlayer = new TrackFragmentPlayer.PlayerTask(false);
        } catch (DeezerError deezerError) {
            deezerError.printStackTrace();
        } catch (TooManyPlayersExceptions tooManyPlayersExceptions) {
            tooManyPlayersExceptions.printStackTrace();
        }
        final TrackFragmentPlayer.TaskSpecification task = new TrackFragmentPlayer.TaskSpecification(1109731, 0, 5000);

        mFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mStaticPlayer.play(task);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        mSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new TrackFragmentPlayer.PlayerTask(true).play(new TrackFragmentPlayer.TaskSpecification(98363604, 15000, 20000));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DezerApp.instance, SearchForTrack.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
