package com.example.android.themoviedb;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    List<TrailerClass> trailerList = new ArrayList<>();
    List<ReviewClass> reviewList = new ArrayList<>();
    RecyclerView rvTrailer, rvReview;
    TrailerAdapter trailerAdapter;
    ReviewAdapter reviewAdapter;
    Intent intent;
    ImageView poster;
    TextView tvTitle, tvYear, tvDuration, tvRatingReview, tvDescription, tvReviewAvailable, tvTrailerAvailable;
    String APIKey;
    String url;
    Button btnFavorite;
    DataHelper dataHelper;
    boolean trailer = false, review = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        dataHelper = new DataHelper(this);
        rvTrailer = findViewById(R.id.rv_trailer);
        rvReview = findViewById(R.id.rv_review);
        tvReviewAvailable = findViewById(R.id.tv_revnotavailable);
        tvTrailerAvailable = findViewById(R.id.tv_trailernotavailable);
        intent = getIntent();
        tvTitle = findViewById(R.id.tv_title);
        tvYear = findViewById(R.id.yearRelease);
        tvDuration = findViewById(R.id.duration);
        tvRatingReview = findViewById(R.id.ratingreview);
        tvDescription = findViewById(R.id.tv_desc);
        btnFavorite = findViewById(R.id.btn_favorite);
        SetupSharedPreference();
        SQLiteDatabase db = dataHelper.getReadableDatabase();
        String query = "Select * From favorite where id=" + intent.getStringExtra("idFilm");
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() > 0) {
            btnFavorite.setText("Remove From Favorite");
        } else {
            btnFavorite.setText("Add To Favorite");
        }
        cursor.close();
        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase db = dataHelper.getReadableDatabase();
                if (btnFavorite.getText().toString().equals("Add To Favorite")) {
                    db.execSQL("Insert into favorite(id) values(" + intent.getStringExtra("idFilm") + ")");
                    btnFavorite.setText("Remove From Favorite");
                } else if (btnFavorite.getText().toString().equals("Remove From Favorite")) {
                    db.delete("favorite","id=?",new String[]{intent.getStringExtra("idFilm")});
                    btnFavorite.setText("Add To Favorite");
                }
                Toast.makeText(DetailActivity.this, "Berhasil", Toast.LENGTH_SHORT).show();
            }
        });
        poster = findViewById(R.id.posterDetail);
        url = "https://api.themoviedb.org/3/movie/" + intent.getStringExtra("idFilm") + "?api_key="+APIKey+"&language=en-US";
        volleyProcess();
    }

    public void volleyProcess() {
        StringRequest sr = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                getJSONData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        RequestQueue rq = Volley.newRequestQueue(this);
        rq.add(sr);
    }

    public void getJSONData(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (!trailer && !review) {
                Picasso.with(getApplicationContext()).load(intent.getStringExtra("posterUrl")).fit().into(poster);
                tvTitle.setText(" " + jsonObject.getString("title"));
                tvYear.setText(jsonObject.getString("release_date").substring(0, 4));
                tvDuration.setText(String.valueOf(jsonObject.getInt("runtime")) + "min");
                tvRatingReview.setText(String.valueOf(jsonObject.getDouble("vote_average")) + "/10");
                tvDescription.setText(jsonObject.getString("overview"));
                trailer = true;
                url = "https://api.themoviedb.org/3/movie/" + intent.getStringExtra("idFilm") + "/videos?api_key="+APIKey+"&language=en-US";
                volleyProcess();
            } else if (trailer && !review) {
                JSONArray jsonArray = jsonObject.getJSONArray("results");
                if (jsonArray.length() <= 0) {
                    tvTrailerAvailable.setVisibility(View.VISIBLE);
                }
                for (int x = 0; x < jsonArray.length(); x++) {
                    tvTrailerAvailable.setVisibility(View.INVISIBLE);
                    JSONObject j = jsonArray.getJSONObject(x);
                    TrailerClass trailerClass = new TrailerClass();
                    trailerClass.setTrailerName(j.getString("name"));
                    trailerClass.setTrailerUrl("https://www.youtube.com/watch?v=" + j.getString("key"));

                    trailerList.add(trailerClass);
                    trailerAdapter = new TrailerAdapter(trailerList, this);
                    rvTrailer.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                    rvTrailer.setHasFixedSize(false);
                    rvTrailer.setAdapter(trailerAdapter);
                    //Toast.makeText(this,"tes",Toast.LENGTH_SHORT).show();
                }
                review = true;
                url = "https://api.themoviedb.org/3/movie/" + intent.getStringExtra("idFilm") + "/reviews?api_key="+APIKey+"&language=en-US";
                volleyProcess();
            } else {
                JSONArray jsonArray = jsonObject.getJSONArray("results");
                if (jsonObject.getInt("total_results") == 0) {
                    tvReviewAvailable.setVisibility(View.VISIBLE);
                } else {
                    for (int x = 0; x < jsonArray.length(); x++) {
                        tvReviewAvailable.setVisibility(View.INVISIBLE);
                        JSONObject j = jsonArray.getJSONObject(x);
                        ReviewClass reviewClass = new ReviewClass();
                        reviewClass.setAuthor(j.getString("author"));
                        reviewClass.setReview(j.getString("content"));
                        reviewList.add(reviewClass);
                        reviewAdapter = new ReviewAdapter(reviewList, this);
                        rvReview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                        rvReview.setHasFixedSize(false);
                        rvReview.setAdapter(reviewAdapter);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void SetupSharedPreference(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        APIKey = sharedPreferences.getString("keyAPI","");
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

    }
}
