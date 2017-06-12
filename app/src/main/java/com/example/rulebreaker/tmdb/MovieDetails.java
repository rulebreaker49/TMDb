package com.example.rulebreaker.tmdb;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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

public class MovieDetails extends AppCompatActivity {

    String data;
    HttpURLConnection connection=null;
    String adult;
    String budget;
    String homepage;
    String original_title;
    String overview;
    String poster_path;
    String release_date;
    String revenue;
    String runtime;
    String tagline;
    String vote_average;
    String[] production_companies;
    String[] genres;

    @BindView(R.id.movieDetail)
    TextView movieDetail;
    @BindView(R.id.movieLayout)
    ImageView movieLayout;
    @BindView(R.id.moviePoster)
    ImageView moviePoster;
    @BindView(R.id.movie_tagline)
    TextView mtagline;
    @BindView(R.id.popularity)
    TextView popularity;
    @BindView(R.id.website)
    TextView mwebsite;
    @BindView(R.id.releaseDate)
    TextView releasedate;
    @BindView(R.id.budget)
    TextView mbudget;
    @BindView(R.id.revenue)
    TextView mrevenue;
    @BindView(R.id.runtime)
    TextView mruntime;
    @BindView(R.id.genre)
    TextView mgenre;
    @BindView(R.id.production)
    TextView production;
    @BindView(R.id.details)
    TextView detail;
    @BindView(R.id.certificate)
    TextView Certi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        data=getIntent().getExtras().getString("Data");
        ButterKnife.bind(this);

        Toolbar mtoolbar=(Toolbar)findViewById(R.id.BackButtonToolbar);
        mtoolbar.setTitle("Movie Details");
        mtoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        new LoadMovie().execute();



    }

    public class LoadMovie extends AsyncTask<Void, Void, String> {


        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL("https://api.themoviedb.org/3/movie/"+data+"?api_key=55957fcf3ba81b137f8fc01ac5a31fb5");
                connection=(HttpURLConnection)url.openConnection();
                connection.connect();

                InputStream stream=connection.getInputStream();

                return IOUtils.toString(stream,"UTF-8");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(connection!=null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject parentObject=new JSONObject(s);
                adult=parentObject.getString("adult");
                budget=parentObject.getString("budget");
                homepage=parentObject.getString("homepage");
                original_title=parentObject.getString("original_title");
                overview=parentObject.getString("overview");
                poster_path=parentObject.getString("poster_path");
                release_date=parentObject.getString("release_date");
                revenue=parentObject.getString("revenue");
                runtime=parentObject.getString("runtime");
                tagline=parentObject.getString("tagline");
                vote_average=parentObject.getString("vote_average");

                JSONArray array1=parentObject.getJSONArray("production_companies");
                JSONArray array2=parentObject.getJSONArray("genres");

                production_companies=new String[array1.length()];
                for(int i=0;i<array1.length();i++) {
                    JSONObject finalObject = array1.getJSONObject(i);
                    production_companies[i]=finalObject.getString("name");
                }

                genres=new String[array2.length()];
                for(int i=0;i<array2.length();i++){
                    JSONObject finalObject = array2.getJSONObject(i);
                    genres[i]=finalObject.getString("name");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Picasso.with(MovieDetails.this).load("https://image.tmdb.org/t/p/w780"+poster_path).fit().into(movieLayout);
            Picasso.with(MovieDetails.this).load("https://image.tmdb.org/t/p/w780"+poster_path).fit().into(moviePoster);
            movieDetail.setText(original_title);
            if(tagline.equals("")||tagline.equals(null))
                mtagline.setText("Wait and Watch");
            else
                mtagline.setText(tagline);
            popularity.setText(vote_average+"/10");
            mwebsite.setText(homepage);
            releasedate.setText("Release Date: "+release_date);
            if(adult.equals("false"))
                Certi.setText("UA");
            else
                Certi.setText("A");
            mbudget.setText("Budget: Rs "+budget+"/-");
            mrevenue.setText("Revenue: Rs "+revenue+"/-");
            mruntime.setText("Runtime: "+runtime+" minutes\n");

            StringBuffer genre=new StringBuffer();
            for(int i=0;i<genres.length;i++)
            {
                genre.append(genres[i]);
                if(i!=genres.length-1)
                genre.append(", ");
            }
            mgenre.setText(genre.toString());

            StringBuffer productn=new StringBuffer();
            for(int i=0;i<production_companies.length;i++)
            {
                productn.append(production_companies[i]);
                if(i!=production_companies.length-1)
                    productn.append(", \n");
            }
            production.setText("Production Companies: \n"+productn.toString()+"\n");
            detail.setText(overview);


        }
    }
}
