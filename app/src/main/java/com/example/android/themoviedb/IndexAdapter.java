package com.example.android.themoviedb;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.List;

/**
 * Created by Sakata Yoga on 20/02/2018.
 */

public class IndexAdapter extends RecyclerView.Adapter<IndexAdapter.ViewHolder>{
    List<MovieClass> movieList;
    Context context;

    public IndexAdapter(List<MovieClass> movieList, Context context) {
        this.movieList = movieList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_index,null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        Picasso.with(context).load(movieList.get(position).getPosterUrl()).fit().into(holder.poster);
        holder.poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,DetailActivity.class);
                intent.putExtra("idFilm",movieList.get(position).getIdFilm());
                intent.putExtra("posterUrl",movieList.get(position).getPosterUrl());
                context.startActivity(intent);


            }
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView poster;
        public ViewHolder(View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.poster_img);
        }
    }
}
