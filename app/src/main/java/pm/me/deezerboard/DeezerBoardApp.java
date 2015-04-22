package pm.me.deezerboard;

import com.deezer.sdk.network.connect.DeezerConnect;

/**
 * Created by pavelmironchyk on 22/04/15.
 */
public class DeezerBoardApp extends android.app.Application {

    public static DeezerConnect deezerConnect = null;
    public static DeezerBoardApp instance = null;

    @Override
    public void onCreate() {
        super.onCreate();

        deezerConnect = new DeezerConnect(this, Constants.APP_ID);
        instance = this;
    }
}
