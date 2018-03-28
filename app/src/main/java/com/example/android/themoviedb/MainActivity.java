package com.example.android.themoviedb;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    List<MovieClass> movieList = new ArrayList<>();
    RecyclerView recyclerView;
    IndexAdapter indexAdapter;
    int page = 1, maxpage;
    String api_key;
    String base_url = "https://api.themoviedb.org/3/movie/top_rated?api_key=";
    String page_url = "&page=" + String.valueOf(page);
    String url;
    TextView tvPage,tvError;
    Button next, prev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.rv_data);
        next = findViewById(R.id.nextpage);
        prev = findViewById(R.id.previouspage);
        tvPage = findViewById(R.id.page);
        tvError=findViewById(R.id.tv_error);
        prev.setVisibility(View.INVISIBLE);
        next.setVisibility(View.INVISIBLE);
        tvPage.setVisibility(View.INVISIBLE);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page++;
                if (page == 1) {
                    prev.setVisibility(View.INVISIBLE);
                } else if (page < maxpage && page != 1) {
                    prev.setVisibility(View.VISIBLE);
                    next.setVisibility(View.VISIBLE);
                } else {
                    prev.setVisibility(View.VISIBLE);
                    next.setVisibility(View.INVISIBLE);
                }
                movieList.clear();
                indexAdapter.notifyDataSetChanged();
                tvPage.setText(String.valueOf(page));
                page_url = "&page=" + String.valueOf(page);
                url = base_url + api_key + page_url;
                VolleyProcess();
            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page--;
                if (page == 1) {
                    prev.setVisibility(View.INVISIBLE);
                } else if (page < maxpage && page != 1) {
                    prev.setVisibility(View.VISIBLE);
                    next.setVisibility(View.VISIBLE);
                } else {
                    prev.setVisibility(View.VISIBLE);
                    next.setVisibility(View.INVISIBLE);
                }
                movieList.clear();
                indexAdapter.notifyDataSetChanged();
                tvPage.setText(String.valueOf(page));
                page_url = "&page=" + String.valueOf(page);
                url = base_url + api_key + page_url;
                VolleyProcess();
            }
        });
        setupPreference();
        VolleyProcess();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.layout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.setting_menu) {
            Intent i = new Intent(this, SettingActivity.class);
            startActivity(i);
            return true;
        } else if (id == R.id.favorite_menu) {
            Intent i = new Intent(this,FavoriteActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void VolleyProcess() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                GetJSON(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                tvError.setVisibility(View.VISIBLE);
                //next.setVisibility(View.INVISIBLE);
                //prev.setVisibility(View.INVISIBLE);
                //tvPage.setVisibility(View.INVISIBLE);
            }
        });
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }

    public void GetJSON(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            tvError.setVisibility(View.INVISIBLE);
            if (page == 1) {
                next.setVisibility(View.VISIBLE);
                prev.setVisibility(View.INVISIBLE);
                tvPage.setVisibility(View.VISIBLE);
            }
            maxpage = jsonObject.getInt("total_pages");
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            for (int x = 0; x < jsonArray.length(); x++) {
                JSONObject j = jsonArray.getJSONObject(x);
                MovieClass movieClass = new MovieClass();
                movieClass.setPosterUrl("http://image.tmdb.org/t/p/original/" + j.getString("poster_path"));
                movieClass.setIdFilm(String.valueOf(j.getInt("id")));
                movieList.add(movieClass);
                indexAdapter = new IndexAdapter(movieList, this);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
                recyclerView.setLayoutManager(gridLayoutManager);
                recyclerView.setAdapter(indexAdapter);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            tvError.setVisibility(View.VISIBLE);
        }
    }

    private void setupPreference() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String val = sharedPreferences.getString("keySort", "Top Rated");
        setSort(val);
        api_key = sharedPreferences.getString("keyAPI", "");
        url = base_url + api_key + page_url;
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    private void setSort(String val) {
        if (val.equals("Top Rated")) {
            page = 1;
            base_url = "https://api.themoviedb.org/3/movie/top_rated?api_key=";
            tvPage.setText(String.valueOf(page));
            page_url = "&page=" + String.valueOf(page);
            url = base_url + api_key + page_url;
            getSupportActionBar().setTitle("Top Rated");
        } else if (val.equals("Most Popular")) {
            page = 1;
            base_url = "https://api.themoviedb.org/3/movie/popular?api_key=";
            tvPage.setText(String.valueOf(page));
            page_url = "&page=" + String.valueOf(page);
            url = base_url + api_key + page_url;
            getSupportActionBar().setTitle("Most Popular");
        }
        movieList.clear();
        if (indexAdapter != null) {
            indexAdapter.notifyDataSetChanged();
        }
        VolleyProcess();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals("keySort")) {
            String val = sharedPreferences.getString(s, getString(R.string.sort_topRated));
            setSort(val);
        } else if (s.equals("keyAPI")) {
            api_key = sharedPreferences.getString(s, "");
            url = base_url + api_key + page_url;
            movieList.clear();
            if (indexAdapter != null) {
                indexAdapter.notifyDataSetChanged();
            }
            VolleyProcess();
        }
    }
}
