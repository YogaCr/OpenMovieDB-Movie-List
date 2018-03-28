package com.example.android.themoviedb;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
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

public class FavoriteActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    Cursor cursor;
    DataHelper dataHelper;
    static List<Integer> id = new ArrayList<>();
    static List<MovieClass> movieList = new ArrayList<>();
    RecyclerView recyclerView;
    IndexAdapter indexAdapter;
    String APIKey;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        recyclerView = findViewById(R.id.rv_data_favorite);
        SetupSharedPreference();
        dataHelper = new DataHelper(this);
        RefreshList();
        ProcessGet();
    }
    private void ProcessGet(){
        for (int x = 0; x < id.size(); x++) {
            url = "https://api.themoviedb.org/3/movie/" + id.get(x) + "?api_key="+APIKey;
            VolleyProcess();
        }
    }
    private void RefreshList() {
        SQLiteDatabase db = dataHelper.getReadableDatabase();
        cursor = db.rawQuery("Select * from favorite", null);
        cursor.moveToFirst();
        for (int x = 0; x < cursor.getCount(); x++) {
            cursor.moveToPosition(x);
            id.add(cursor.getInt(0));
        }
        cursor.close();
    }

    private void VolleyProcess() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                GetJSON(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void GetJSON(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            MovieClass movieClass = new MovieClass();
            movieClass.setPosterUrl("http://image.tmdb.org/t/p/original/" + jsonObject.getString("poster_path"));
            movieClass.setIdFilm(String.valueOf(jsonObject.getInt("id")));
            movieList.add(movieClass);
            indexAdapter = new IndexAdapter(movieList, this);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
            recyclerView.setLayoutManager(gridLayoutManager);
            recyclerView.setAdapter(indexAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void SetupSharedPreference() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        APIKey = sharedPreferences.getString("keyAPI", "");
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh_favorite,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.menu_refresh){
            movieList.clear();
            indexAdapter.notifyDataSetChanged();
            id.clear();
            RefreshList();
            ProcessGet();

            indexAdapter.notifyDataSetChanged();
        }
        return super.onOptionsItemSelected(item);
    }

}
