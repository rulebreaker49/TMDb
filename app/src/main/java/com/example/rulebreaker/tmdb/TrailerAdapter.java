package com.example.rulebreaker.tmdb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by rulebreaker on 19/6/17.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {

    public String[] mtrailerId;
    public String[] mtrailerName;
    public String[] mvideoType;
    public String id;
    public String[] moriginalId;
    private final LayoutInflater mlayoutinflator;


    public TrailerAdapter(Context context, String[] trailerId, String[] trailerName, String[] videoType,String Id,String[] originalId) {

        id=Id;
        int len=trailerId.length;
        mtrailerId=new String[len];
        mtrailerName=new String[len];
        mvideoType=new String[len];
        moriginalId=new String[originalId.length];
        for(int i=0;i<len;i++)
        {
            mtrailerId[i]=trailerId[i];
            mtrailerName[i]=trailerName[i];
            mvideoType[i]=videoType[i];
        }
        moriginalId=originalId;
        mlayoutinflator=LayoutInflater.from(context);
    }

    @Override
    public TrailerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mlayoutinflator.inflate(R.layout.trailer_recycler_view,parent,false);
        return new TrailerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TrailerAdapter.ViewHolder holder, final int position) {

        holder.trailerTitle.setText(mtrailerName[position]);
        holder.trailerType.setText("("+mvideoType[position]+")");
        Picasso.with(holder.trailerThumbnail.getContext()).load("https://img.youtube.com/vi/"+mtrailerId[position]+"/0.jpg").fit().into(holder.trailerThumbnail);
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(holder.itemView.getContext(),PlayTrailer.class);
                Bundle bundle=new Bundle();
                int i=0;
                for(i=0;i<moriginalId.length;i++)
                {
                    if(moriginalId[i].equals(mtrailerId[position]))
                        break;
                }
                bundle.putInt("Pos",i);
                bundle.putString("Id",id);
                intent.putExtras(bundle);
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mtrailerId.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.trailerTitle)
        TextView trailerTitle;
        @BindView(R.id.trailerType)
        TextView trailerType;

        @BindView(R.id.trailerThumbnail)
        ImageView trailerThumbnail;
        public ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

}
