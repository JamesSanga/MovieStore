package com.tz.sanga.moviestore.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.tz.sanga.moviestore.Model.Movie;

import java.util.ArrayList;
import java.util.List;

public class FavoriteDb extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "favorite.db";
    public static final int DATABASE_VERSION = 1;
    public static final String LOGTAG = "FAVORITE";

    SQLiteOpenHelper dbHandler;
    SQLiteDatabase db;


    public FavoriteDb(Context context) {
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }

    public void open(){
        Log.i(LOGTAG, "open: Database Opened");
        db = dbHandler.getWritableDatabase();
    }

    public void close(){
        Log.i(LOGTAG, "close: Database closed");
        dbHandler.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String CREATE_TABLE = " CREATE TABLE " + Favorite.FavoriteEntry.TABLE_NAME + " (" +
                Favorite.FavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Favorite.FavoriteEntry.COLUMN_MOVIEID + " INTEGER, " +
                Favorite.FavoriteEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                Favorite.FavoriteEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                Favorite.FavoriteEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL" + ");";
        sqLiteDatabase.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Favorite.FavoriteEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }

    public void addFavorite(Movie movie){
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Favorite.FavoriteEntry.COLUMN_MOVIEID, movie.getId());
        values.put(Favorite.FavoriteEntry.COLUMN_TITLE, movie.getTitle());
        values.put(Favorite.FavoriteEntry.COLUMN_OVERVIEW, movie.getOverview());
        values.put(Favorite.FavoriteEntry.COLUMN_POSTER_PATH, movie.getPosterPath());

        database.insert(Favorite.FavoriteEntry.TABLE_NAME, null, values);
        database.close();
    }

    public List<Movie>getAllFavorite(){

        String [] columns = {
                Favorite.FavoriteEntry._ID,
                Favorite.FavoriteEntry.COLUMN_MOVIEID,
                Favorite.FavoriteEntry.COLUMN_TITLE,
                Favorite.FavoriteEntry.COLUMN_POSTER_PATH,
                Favorite.FavoriteEntry.COLUMN_OVERVIEW
        };

        String sortOder = Favorite.FavoriteEntry._ID + " ASC";
        List<Movie> favoriteList = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.query(Favorite.FavoriteEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                sortOder
                );

        if (cursor.moveToFirst()){
            do {
                Movie movie = new Movie();
                movie.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Favorite.FavoriteEntry.COLUMN_MOVIEID))));
                movie.setOriginalTile(cursor.getString(cursor.getColumnIndex(Favorite.FavoriteEntry.COLUMN_TITLE)));
                movie.setPosterPath(cursor.getString(cursor.getColumnIndex(Favorite.FavoriteEntry.COLUMN_POSTER_PATH)));
                movie.setOverview(cursor.getString(cursor.getColumnIndex(Favorite.FavoriteEntry.COLUMN_OVERVIEW)));

                favoriteList.add(movie);
            }while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return favoriteList;
    }
}
