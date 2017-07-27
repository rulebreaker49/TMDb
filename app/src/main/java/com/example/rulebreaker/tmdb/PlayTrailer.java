package com.example.rulebreaker.tmdb;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerView;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlayTrailer extends YouTubeBaseActivity {
    public String Id;
    public String key1, title, type;
    ProgressDialog progressDialog;
    int pos = 0;

    public String[] trailerId;
    public String[] trailerName;
    public String[] videoType;

    public TrailerAdapter adapter;
    public YouTubePlayer playerYb;
    public Boolean fullscreen;

    @BindView(R.id.currenttrailerTitle)
    TextView trailerTitle;
    @BindView(R.id.currenttrailerType)
    TextView trailerType;
    @BindView(R.id.trailerRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.youtubeVideo)
    YouTubePlayerView player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_trailer);
        ButterKnife.bind(this);

        Id = getIntent().getExtras().getString("Id");
        pos = getIntent().getExtras().getInt("Pos");
        if(haveNetworkConnection())
        new trailerLinkTask().execute();
        else
            Toast.makeText(this,"No Internet connection!",Toast.LENGTH_LONG).show();

    }

    @Override
    public void onBackPressed() {
        if (fullscreen){
            playerYb.setFullscreen(false);
        }
        else{
            if(playerYb.isPlaying()) {
                playerYb.pause();
                playerYb.release();
            }
            super.onBackPressed();
        }
    }

    private boolean haveNetworkConnection() {
        {
            boolean haveConnectedWifi = false;
            boolean haveConnectedMobile = false;

            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] netInfo = cm.getAllNetworkInfo();
            for (NetworkInfo ni : netInfo) {
                if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                    if (ni.isConnected())
                        haveConnectedWifi = true;
                if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                    if (ni.isConnected())
                        haveConnectedMobile = true;
            }
            return haveConnectedWifi || haveConnectedMobile;
        }
    }

    public class trailerLinkTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(PlayTrailer.this, "Please Wait!", "Loading", true);
        }

        @Override
        protected String doInBackground(Void... params) {
            HttpURLConnection connection = null;
            try {

                URL url = new URL("http://api.themoviedb.org/3/movie/" + Id + "/videos?api_key=3872304bae3e7e53adbdba5dca474162");
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                return IOUtils.toString(stream, "UTF-8");

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String s) {

            String data = s;
            int len = 0;
            try {

                JSONObject parentObject = new JSONObject(data);
                JSONArray parentArray = parentObject.getJSONArray("results");
                len = parentArray.length();

                trailerId = new String[len];
                trailerName = new String[len];
                videoType = new String[len];

                for (int i = 0; i < len; i++) {
                    JSONObject object = parentArray.getJSONObject(i);
                    trailerId[i] = object.getString("key");
                    trailerName[i] = object.getString("name");
                    videoType[i] = object.getString("type");
                }
                key1 = trailerId[pos];
                title = trailerName[pos];
                type = "(" + videoType[pos] + ")";

            } catch (JSONException e) {
                e.printStackTrace();
            }
            String[] ctrailerId=new String[len-1];
            String[] ctrailerName=new String[len-1];
            String[] cvideoType=new String[len-1];
            int j=0;
            for(int i=0;i<len;i++)
            {
                if(i!=pos)
                {
                    ctrailerId[j]=trailerId[i];
                    ctrailerName[j]=trailerName[i];
                    cvideoType[j]=videoType[i];
                    j++;
                }

            }

            trailerTitle.setText(title);
            trailerType.setText(type);
            LinearLayoutManager layoutManager = new LinearLayoutManager(PlayTrailer.this, LinearLayoutManager.HORIZONTAL, false);
            mRecyclerView.setLayoutManager(layoutManager);
            adapter = new TrailerAdapter(PlayTrailer.this, ctrailerId, ctrailerName, cvideoType, Id,trailerId);
            mRecyclerView.setAdapter(adapter);
            progressDialog.dismiss();
            player.initialize("AIzaSyCsd_9XzrXk93LdArgw-83w1q6QK8yb54g", new YouTubePlayer.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                    youTubePlayer.loadVideo(key1);
                    playerYb=youTubePlayer;
                    youTubePlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                        @Override
                        public void onFullscreen(boolean b) {
                            fullscreen=b;
                        }
                    });
                }

                @Override
                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                    Toast.makeText(PlayTrailer.this,"Check Internet!",Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}



