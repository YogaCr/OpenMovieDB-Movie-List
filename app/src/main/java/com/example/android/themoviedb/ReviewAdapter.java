package com.example.android.themoviedb;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Sakata Yoga on 22/02/2018.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.Holder>{
    List<ReviewClass> review;
    Context context;

    public ReviewAdapter(List<ReviewClass> review, Context context) {
        this.review = review;
        this.context = context;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.review_list,null);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        holder.tvName.setText(review.get(position).getAuthor());
        holder.tvReview.setText(review.get(position).getReview());
    }

    @Override
    public int getItemCount() {
        return review.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        public TextView tvName;
        public TextView tvReview;

        public Holder(View itemView) {
            super(itemView);
            tvName=itemView.findViewById(R.id.tvReviewAuthor);
            tvReview=itemView.findViewById(R.id.tvReview);
        }
    }
}
