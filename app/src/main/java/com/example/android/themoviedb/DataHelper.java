package com.example.android.themoviedb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Sakata Yoga on 25/02/2018.
 */

public class DataHelper extends SQLiteOpenHelper {
    private static final String databasename="favorite.db";
    private static final int databaseversion=1;

    public DataHelper(Context context)
    {
        super(context,databasename,null,databaseversion);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql="Create table favorite(id integer primary key)";
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
