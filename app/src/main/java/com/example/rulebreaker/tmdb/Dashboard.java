package com.example.rulebreaker.tmdb;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Dashboard extends AppCompatActivity {

    private AlertDialog mAlertDialog;
    public String data;
    public String[] movieTitle;
    public String[] backdropPath;
    public String[] releaseDate;
    public float[] voteAverage;
    public String[] adult;
    public int[] voting,id;
    public String[] overview;
    MovieAdapter adapter;

    @BindView(R.id.movieRecyclerView)
    RecyclerView movieRecyclerView;
    @BindView(R.id.swipe)
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        data=getIntent().getExtras().getString("Data");
        ButterKnife.bind(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.refresh_progress_1));

        try {
            JSONObject parentObject=new JSONObject(data);
            JSONArray parentArray=parentObject.getJSONArray("results");
            int size=parentArray.length();

            movieTitle=new String[size];
            backdropPath=new String[size];
            releaseDate=new String[size];
            voteAverage=new float[size];
            adult=new String[size];
            voting=new int[size];
            overview=new String[size];
            id=new int[size];


            for(int i=0;i<size;i++)
            {
                JSONObject finalObjet=parentArray.getJSONObject(i);
                movieTitle[i]=finalObjet.getString("original_title");
                backdropPath[i]=finalObjet.getString("backdrop_path");
                releaseDate[i]=finalObjet.getString("release_date");
                voteAverage[i]=(float) finalObjet.getDouble("vote_average");
                adult[i]=finalObjet.getString("adult");
                voting[i]=finalObjet.getInt("vote_count");
                overview[i]=finalObjet.getString("overview");
                id[i]=finalObjet.getInt("id");

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        LinearLayoutManager layoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        movieRecyclerView.setLayoutManager(layoutManager);

        adapter= new MovieAdapter(this, movieTitle,backdropPath,releaseDate,voteAverage,adult,voting,overview,id);
        movieRecyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(haveNetworkConnection()==false)
                {
                    Toast.makeText(Dashboard.this,"No Internet Connectivity",Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
                else
                {
                    new ProgressTask().execute();
                }

            }
        }
    );
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

    public class ProgressTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected String doInBackground(Void... params) {

            HttpURLConnection connection = null;

            try {
                URL url = new URL("https://api.themoviedb.org/3/movie/now_playing?api_key=55957fcf3ba81b137f8fc01ac5a31fb5&language=en-US&page=undefined");
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
        protected void onPostExecute(String Data) {

            data=Data;
            swipeRefreshLayout.setRefreshing(false);
            try {
                JSONObject parentObject=new JSONObject(data);
                JSONArray parentArray=parentObject.getJSONArray("results");
                int size=parentArray.length();

                for(int i=0;i<size;i++)
                {
                    JSONObject finalObjet=parentArray.getJSONObject(i);
                    movieTitle[i]=finalObjet.getString("original_title");
                    backdropPath[i]=finalObjet.getString("backdrop_path");
                    releaseDate[i]=finalObjet.getString("release_date");
                    voteAverage[i]=(float) finalObjet.getDouble("vote_average");
                    adult[i]=finalObjet.getString("adult");
                    voting[i]=finalObjet.getInt("vote_count");
                    overview[i]=finalObjet.getString("overview");
                    id[i]=finalObjet.getInt("id");

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            LinearLayoutManager layoutManager=new LinearLayoutManager(Dashboard.this,LinearLayoutManager.VERTICAL,false);
            movieRecyclerView.setLayoutManager(layoutManager);

            MovieAdapter adapter= new MovieAdapter(Dashboard.this, movieTitle,backdropPath,releaseDate,voteAverage,adult,voting,overview,id);
            movieRecyclerView.setAdapter(adapter);

        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("Please Confirm!");
        builder.setMessage("Are you sure you want to exit?");

        builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });
        builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
            }
        });

        mAlertDialog = builder.create();
        mAlertDialog.show();
        Button nbutton = mAlertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(Color.RED);
        Button pbutton = mAlertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(Color.RED);
    }
}
