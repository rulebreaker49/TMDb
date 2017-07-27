package com.example.rulebreaker.tmdb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StartPage extends Activity {


    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    int page=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean checkInternet=this.haveNetworkConnection();
        if(checkInternet==true) {

            setContentView(R.layout.activity_start_page);
            ButterKnife.bind(this);

            new ProgressTask().execute();
        }
        else {
            setContentView(R.layout.no_internet_connectivity);
            Button button=(Button)findViewById(R.id.retry);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(haveNetworkConnection()==true){
                    Intent intent=new Intent(StartPage.this,StartPage.class);
                    finish();
                    startActivity(intent);
                    }
                    else
                        Toast.makeText(StartPage.this,"No Internet Connectivity",Toast.LENGTH_SHORT).show();
                }
            });
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



    public class ProgressTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute(){
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {

            return null;
        }

        @Override
        protected void onPostExecute(Void params) {
            new CountDownTimer(3000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }
                @Override
                public void onFinish() {
                    if (haveNetworkConnection() == true) {
                        Intent intent = new Intent(StartPage.this, Dashboard.class);
                        progressBar.setVisibility(View.GONE);
                        startActivity(intent);
                    }
                    else
                        Toast.makeText(StartPage.this,"No Internet Connectivity",Toast.LENGTH_SHORT).show();
                }}.start();

        }
    }
}

