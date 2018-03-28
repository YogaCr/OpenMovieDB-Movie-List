package com.example.android.themoviedb;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sakata Yoga on 21/02/2018.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHodler>{
    List<TrailerClass> trailerList = new ArrayList<>();
    Context context;

    public TrailerAdapter(List<TrailerClass> trailerList, Context context) {
        this.trailerList = trailerList;
        this.context = context;
    }

    @Override
    public ViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.trailer_list,null);
        return new ViewHodler(v);
    }

    @Override
    public void onBindViewHolder(ViewHodler holder, final int position) {
        holder.trailerName.setText(trailerList.get(position).getTrailerName());
        holder.trailerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(trailerList.get(position).getTrailerUrl()));
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return trailerList.size();
    }

    public class ViewHodler extends RecyclerView.ViewHolder {
        TextView trailerName;
        LinearLayout trailerLayout;
        public ViewHodler(View itemView) {
            super(itemView);
            trailerName=itemView.findViewById(R.id.trailer_name);
            trailerLayout=itemView.findViewById(R.id.trailer);
        }
    }
}
