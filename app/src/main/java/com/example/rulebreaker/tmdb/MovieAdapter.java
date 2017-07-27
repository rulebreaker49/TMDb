package com.example.rulebreaker.tmdb;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.support.v4.content.ContextCompat.startActivity;

/**
 * Created by rulebreaker on 2/6/17.
 */

class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder>{

    public String[] movieTitle,backdropPath,releaseDate,adult,overview;
    public float[] voteAverage;
    public int[] voting,id;
    private final LayoutInflater mLayoutInflater;


    public MovieAdapter(Context context, String[] movieTitle, String[] backdropPath, String[] releaseDate, float[] voteAverage, String[] adult,int[] voting,String[] overview,int[] id)
    {
        this.movieTitle = movieTitle;
        this.backdropPath = backdropPath;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.adult = adult;
        this.voting=voting;
        this.overview=overview;
        this.id=id;
        mLayoutInflater=LayoutInflater.from(context);
    }
    @Override
    public MovieAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {

        View view = mLayoutInflater.inflate(R.layout.movie_tile_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapter.ViewHolder holder, int position) {
        String movieUrl="https://image.tmdb.org/t/p/w780"+backdropPath[position];
        String cert;
        String rating= String.valueOf(((int)(10.0*voteAverage[position])));
        String votes=String.valueOf(voting[position]);

        if(adult[position].equals("false"))
            cert="UA";
        else
            cert="A";
        if(movieTitle[position]=="")
            movieTitle[position]="N/A";
        if(releaseDate[position]=="")
            releaseDate[position]="N/A";
        if(rating=="0")
            holder.popularity.setText("N/A");
        else
            holder.popularity.setText(" "+rating+"%");

        if(votes=="0")
            holder.voting.setText("N/A");
        else
            holder.voting.setText(votes+" Votes");

        String release=releaseDate[position];
        String date = null,month=null,year=null;
        if(release!=""&&release!="N/A")
        {
            year= String.valueOf(release.charAt(0))+String.valueOf(release.charAt(1))+String.valueOf(release.charAt(2))+String.valueOf(release.charAt(3));
            month=String.valueOf(release.charAt(5))+String.valueOf(release.charAt(6));
            date=String.valueOf(release.charAt(8))+String.valueOf(release.charAt(9));
        }

        holder.movieTitle.setText(movieTitle[position]);
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
            holder.movieRelease.setText("In Cinemas on "+month+" "+date+", "+year);
        else
            holder.movieRelease.setText("In Cinemas on N/A");
        Picasso.with(holder.movieImage.getContext()).load(movieUrl).fit().into(holder.movieImage);
        holder.certificate.setText(cert);
       // holder.movieOverview.setText(overview[position]);
        holder.movieId.setText(String.valueOf(id[position]));

    }

    @Override
    public int getItemCount() {
        return movieTitle.length;
    }

    public void setFilter(ArrayList<String> mmovieTitle, ArrayList<String> mbackdropPath, ArrayList<String> mreleaseDate, ArrayList<Float> mvoteAverage, ArrayList<String> madult, ArrayList<Integer> mvoting, ArrayList<String> moverview, ArrayList<Integer> mid){

        int len=mmovieTitle.size();
        movieTitle=new String[len];
        backdropPath=new String[len];
        releaseDate=new String[len];
        voteAverage=new float[len];
        adult=new String[len];
        voting=new int[len];
        overview=new String[len];
        id=new int[len];

        for(int i=0;i<len;i++)
        {
            movieTitle[i]=mmovieTitle.get(i);
            backdropPath[i]=mbackdropPath.get(i);
            releaseDate[i]=mreleaseDate.get(i);
            voteAverage[i]=mvoteAverage.get(i);
            adult[i]=madult.get(i);
            voting[i]=mvoting.get(i);
            overview[i]=moverview.get(i);
            id[i]=mid.get(i);

        }
        notifyDataSetChanged();



    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.movieTitle)
        TextView movieTitle;
        @BindView(R.id.movieImage)
        ImageView movieImage;
       @BindView(R.id.certificate)
        TextView certificate;
        @BindView(R.id.popularity)
        TextView popularity;
        @BindView(R.id.voting)
        TextView voting;
        @BindView(R.id.movieRelease)
        TextView movieRelease;

        @BindView(R.id.movieId)
        TextView movieId;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    boolean haveConnectedWifi = false;
                    boolean haveConnectedMobile = false;

                    ConnectivityManager cm = (ConnectivityManager) v.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo[] netInfo = cm.getAllNetworkInfo();
                    for (NetworkInfo ni : netInfo) {
                        if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                            if (ni.isConnected())
                                haveConnectedWifi = true;
                        if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                            if (ni.isConnected())
                                haveConnectedMobile = true;
                    }

                    if(haveConnectedWifi || haveConnectedMobile) {
                        String id = movieId.getText().toString();
                        Bundle bundle = new Bundle();
                        bundle.putString("Data", id);
                        Intent intent = new Intent(v.getContext(), MovieDetails.class);
                        intent.putExtras(bundle);
                        v.getContext().startActivity(intent);
                    }
                    else
                        Toast.makeText(v.getContext(), "No Internet Connectivity", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}
