package com.example.rulebreaker.tmdb;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.Toast;

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
import butterknife.OnClick;

public class MovieDetails extends AppCompatActivity {

    String data;    //id
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
    @OnClick(R.id.trailer)
    public void LoadTrailer(){
        Bundle bundle=new Bundle();
        bundle.putString("Id",data);
        bundle.putInt("Pos",0);
        Intent intent = new Intent(MovieDetails.this, PlayTrailer.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private boolean haveNetworkConnection() {
        {
            boolean haveConnectedWifi = false;
            boolean haveConnectedMobile = false;

            ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
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

    public class LoadMovie extends AsyncTask<Void, Void, String> {


        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL("https://api.themoviedb.org/3/movie/"+data+"?api_key=3872304bae3e7e53adbdba5dca474162");
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
            if(haveNetworkConnection()){
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
            Picasso.with(MovieDetails.this).load("https://image.tmdb.org/t/p/w780"+poster_path).fit().into(moviePoster);}
            else
                Toast.makeText(MovieDetails.this,"Check Internet connnectivity!",Toast.LENGTH_LONG).show();

            movieDetail.setText(original_title);

            if(tagline.equals("")||tagline.equals(null))
                mtagline.setText("Wait and Watch");
            else
                mtagline.setText(tagline);

            if(vote_average.equals("")||vote_average.equals(null))
                popularity.setText("N/A");
            else
                popularity.setText(vote_average+"/10");
            if(homepage=="")
                homepage="N/A";
            mwebsite.setText(homepage);

            if(release_date=="")
                release_date="N/A";

            String release=release_date;
            String date = null,month=null,year=null;
            if(release!=""&&release!="N/A")
            {
                year= String.valueOf(release.charAt(0))+String.valueOf(release.charAt(1))+String.valueOf(release.charAt(2))+String.valueOf(release.charAt(3));
                month=String.valueOf(release.charAt(5))+String.valueOf(release.charAt(6));
                date=String.valueOf(release.charAt(8))+String.valueOf(release.charAt(9));
            }
            if(month.equals("01"))
                month="January";
            if(month.equals("02"))
                month="February";
            if(month.equals("03"))
                month="March";
            if(month.equals("04"))
                month="April";
            if(month.equals("05"))
                month="May";
            if(month.equals("06"))
                month="June";
            if(month.equals("07"))
                month="July";
            if(month.equals("08"))
                month="August";
            if(month.equals("09"))
                month="September";
            if(month.equals("10"))
                month="October";
            if(month.equals("11"))
                month="November";
            if(month.equals("12"))
                month="December";

            if(year!=null)
                releasedate.setText("Release Date: "+month+" "+date+", "+year);
            else
                releasedate.setText("Release Date: N/A");


            if(adult.equals("false"))
                Certi.setText("UA");
            else
                Certi.setText("A");

            if(budget=="0")
                mbudget.setText("Budget: N/A");
            else
            {
                String revrese=new StringBuffer(budget).reverse().toString();

                String data="";
                for(int i=0;i<revrese.length();i++)
                {
                    if((i==2||(i%2==0&&i!=0))&&i!=revrese.length()-1)
                        data=data+revrese.charAt(i)+",";
                    else
                        data=data+revrese.charAt(i);
                }
                String result=new StringBuffer(data).reverse().toString();
                mbudget.setText("Budget: Rs "+result+"/-");
            }

            if(revenue=="0")
                mrevenue.setText("Revenue: N/A");
            else
            {
                String revrese=new StringBuffer(revenue).reverse().toString();

                String data="";
                for(int i=0;i<revrese.length();i++)
                {
                    if(i%2==0&&i!=0&&i!=revrese.length()-1)
                        data=data+revrese.charAt(i)+",";
                    else
                        data=data+revrese.charAt(i);
                }
                String result=new StringBuffer(data).reverse().toString();
                mrevenue.setText("Revenue: Rs "+result+"/-");
            }

            if(runtime==""||runtime=="0")
                mruntime.setText("Runtime: N/A\n");
            else
            {
                int time=Integer.parseInt(runtime);
                int hours=time/60;
                int minutes= time%60;
                if(hours>0)
                    mruntime.setText("Runtime: "+Integer.toString(hours)+" hours "+Integer.toString(minutes)+" minutes\n");
                else
                    mruntime.setText("Runtime: "+Integer.toString(minutes)+" minutes\n");
            }

            StringBuffer genre=new StringBuffer();
            for(int i=0;i<genres.length;i++)
            {
                genre.append(genres[i]);
                if(i!=genres.length-1)
                genre.append(", ");
            }
            String genres=genre.toString();
            if(genres=="")
                genres="N/A";
            mgenre.setText(genres);

            StringBuffer productn=new StringBuffer();
            for(int i=0;i<production_companies.length;i++)
            {
                productn.append(production_companies[i]);
                if(i!=production_companies.length-1)
                    productn.append(", \n");
            }
            String productions=productn.toString();
            if(productions=="")
                productions="N/A";
            production.setText("Production Companies: \n"+productions+"\n");
            detail.setText(overview);

        }
    }
}
