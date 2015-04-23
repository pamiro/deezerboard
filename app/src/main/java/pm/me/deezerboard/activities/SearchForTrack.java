package pm.me.deezerboard.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.avg.dezerboard.DezerApp;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpProtocolParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import pm.me.deezerboard.R;

public class SearchForTrack extends Activity {

    private static final String TAG = SearchForTrack.class.getName();
    SearchView mSearchView;
    ListView   mListView;



    public static class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public ImageDownloader(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap mIcon = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                mIcon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }
            return mIcon;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }


    public static DefaultHttpClient getConfiguredClient() {

        BasicHttpParams httpParameters = new BasicHttpParams();
        httpParameters.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        //httpParameters.setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
        httpParameters.setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 256*1024);
        HttpProtocolParams.setUseExpectContinue(httpParameters, true);

        // TODO add timeouts
        //HttpConnectionParams.setConnectionTimeout(httpParameters, 30 * 1000);
        //HttpConnectionParams.setSoTimeout(httpParameters, 50 * 1000);
        //schemeRegistry = new SchemeRegistry();
        //sslSocketFactory = new EasySSLSocketFactory();
        //schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        //schemeRegistry.register(new Scheme("https", sslSocketFactory, 443));
        //connectionManager = new ThreadSafeClientConnManager(httpParameters, schemeRegistry);
        //return new DefaultHttpClient(connectionManager,httpParameters);

        return new DefaultHttpClient(httpParameters);
    }

    public static JSONObject issueQuery(DefaultHttpClient client, String query) {
        JSONObject object = null;
        HttpGet httpGet = new HttpGet(query);
        try {
            HttpResponse httpResponse = client.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            InputStream is = httpEntity.getContent();
            object = new JSONObject(readStream(is));
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }


    public static String readStream(InputStream in) {
        ByteArrayOutputStream  outputStream = new ByteArrayOutputStream() ;
        byte[] buftmp = new byte[1024*256];
        int b;

        try {
            while((b = in.read(buftmp)) != -1) {
                outputStream.write(buftmp, 0, b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStream.toString();
    }

    ResultsAdapter mAdapter;

    public class ResultsAdapter extends BaseAdapter {

        public JSONArray mItems;

        LayoutInflater mInflater;

        ResultsAdapter() {
            mInflater = (LayoutInflater) DezerApp.instance.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public  synchronized  void load(JSONArray data) {
            mItems = data;
            notifyDataSetInvalidated();
        }

        @Override
        public int getCount() {
            return mItems.length();
        }

        @Override
        public Object getItem(int position) {
            try {
                return mItems.get(position);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).hashCode();
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = mInflater.inflate(R.layout.search_item_list_element, parent, false);

            TextView title = (TextView)view.findViewById(R.id.item_title);
            TextView detailsView = (TextView) view.findViewById(R.id.item_detail);

            ImageView iv = (ImageView) view.findViewById(R.id.image_cover);

            String imageURL = null;
            String details = "";
            JSONObject obj = (JSONObject)getItem(position);
            try {
                Log.d(TAG, "title " + obj.getString("title"));
                title.setText(obj.getString("title"));



                JSONObject tmp = obj.getJSONObject("album");
                if(tmp != null) {
                    details += tmp.getString("title") + " - ";
                    imageURL = tmp.getString("cover");
                }
                tmp = obj.getJSONObject("artist");
                if(tmp != null) {
                    details += tmp.getString("name");
                    if(imageURL == null) {
                        imageURL = tmp.getString("picture");
                    }
                }

                detailsView.setText(details);

                if(imageURL!= null)
                    new ImageDownloader(iv).execute(imageURL);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return view;
        }
    }

    public class TrackSearchTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {

            String query = String.format("https://api.deezer.com/search/track/?q=%s&index=0&limit=10&output=json", params[0].replace(" ", "%20"));

            JSONObject res = issueQuery(getConfiguredClient(),query);
//            if(res!=null) {
//                Log.d(TAG, " " + res.toString());
//            }
            return res;
        }

        @Override
        protected void onPostExecute(JSONObject data) {
            super.onPostExecute(data);

            try {
                mAdapter.load(data.getJSONArray("data"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


    TrackSearchTask mTask = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        mAdapter = new ResultsAdapter();

        mSearchView = (SearchView) findViewById(R.id.searchView);
        mListView = (ListView) findViewById(R.id.searchResults);

        mAdapter.load(new JSONArray());
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = SearchForTrack.this.getIntent();
                intent.putExtra("track", mAdapter.getItem(position).toString());
                SearchForTrack.this.setResult(RESULT_OK, intent);
                finish();

                return false;
            }
        });

        mListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "onQueryTextChange() " + newText);

                synchronized (SearchForTrack.this) {
                    if (mTask != null) {
                        mTask.cancel(true);
                        mTask = null;
                    }

                    mTask = new TrackSearchTask();
                    mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, newText);
                }

                return true;
            }
        });
    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_search_for_track, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
