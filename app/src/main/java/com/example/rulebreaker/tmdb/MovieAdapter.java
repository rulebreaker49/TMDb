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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.support.v4.content.ContextCompat.startActivity;

/**
 * Created by rulebreaker on 2/6/17.
 */

class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder>{

    public final String[] movieTitle,backdropPath,releaseDate,adult,overview;
    public final float[] voteAverage;
    public final int[] voting,id;
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

        if(adult[position].equals("false"))
            cert="UA";
        else
            cert="A";
        holder.movieTitle.setText(movieTitle[position]);
        holder.movieRelease.setText("In Cinemas on "+releaseDate[position]);
        Picasso.with(holder.movieImage.getContext()).load(movieUrl).fit().into(holder.movieImage);
        holder.certificate.setText(cert);
        holder.popularity.setText(" "+rating+"%");
        holder.voting.setText(String.valueOf(voting[position])+" Votes");
        holder.movieOverview.setText(overview[position]);
        holder.movieId.setText(String.valueOf(id[position]));

    }

    @Override
    public int getItemCount() {
        return movieTitle.length;
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
        @BindView(R.id.movieOverview)
        TextView movieOverview;
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
