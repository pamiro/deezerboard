package pm.me.deezerboard.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.deezer.sdk.network.request.event.DeezerError;
import com.deezer.sdk.player.exception.TooManyPlayersExceptions;

import pm.me.deezerboard.R;
import pm.me.deezerboard.engine.TrackFragmentPlayer;

public class Player extends Activity {

    Button mFirst, mSecond;

    TrackFragmentPlayer.PlayerTask mStaticPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        mFirst = (Button) findViewById(R.id.button_first);
        mSecond = (Button) findViewById(R.id.button_second);

        try {
            mStaticPlayer = new TrackFragmentPlayer.PlayerTask(false);
        } catch (DeezerError deezerError) {
            deezerError.printStackTrace();
        } catch (TooManyPlayersExceptions tooManyPlayersExceptions) {
            tooManyPlayersExceptions.printStackTrace();
        }
        final TrackFragmentPlayer.TaskSpecification task = new TrackFragmentPlayer.TaskSpecification(98363604, 0, 5000);

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
