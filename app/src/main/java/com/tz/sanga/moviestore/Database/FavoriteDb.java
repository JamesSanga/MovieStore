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

import javax.inject.Inject;

import static android.provider.BaseColumns._ID;

public class FavoriteDb extends SQLiteOpenHelper {
    private SQLiteOpenHelper dbHandler;
    private SQLiteDatabase db;

    @Inject
    public FavoriteDb(Context context) {
        super(context, Favorite.FavoriteEntry.DATABASE_NAME, null, Favorite.FavoriteEntry.DATABASE_VERSION);
    }

    @Inject
    public void open() {
        Log.i(Favorite.FavoriteEntry.TAG, "open: Database Opened");
        db = dbHandler.getWritableDatabase();
    }
    @Inject
    public void close() {
        Log.i(Favorite.FavoriteEntry.TAG, "close: Database closed");
        dbHandler.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String CREATE_TABLE = " CREATE TABLE " + Favorite.FavoriteEntry.TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Favorite.FavoriteEntry.COLUMN_MOVIE_ID + " INTEGER, " +
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

    public boolean addFavorite(int id, String title, String overview, String path ) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Favorite.FavoriteEntry.COLUMN_MOVIE_ID, id);
        values.put(Favorite.FavoriteEntry.COLUMN_TITLE, title);
        values.put(Favorite.FavoriteEntry.COLUMN_OVERVIEW, overview);
        values.put(Favorite.FavoriteEntry.COLUMN_POSTER_PATH, path);

        long result = database.insert(Favorite.FavoriteEntry.TABLE_NAME, null, values);
        database.close();
        if (result==-1){
            return false;
        }return true;
    }
    public Cursor getMovies(){
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor data = database.rawQuery("SELECT _ID  FROM " + Favorite.FavoriteEntry.TABLE_NAME, null);
        return data;
    }


   public boolean deleteMovie(int id){
        SQLiteDatabase database = this.getWritableDatabase();
       long result = database.delete(Favorite.FavoriteEntry.TABLE_NAME, Favorite.FavoriteEntry._ID + "=" + id, null);

       if (result == -1){
           return false;
       }return true;
    }

    public boolean checkMovie(String posterPath){
        String [] columns = {
                Favorite.FavoriteEntry._ID
        };
        SQLiteDatabase database = this.getReadableDatabase();
        String selection = Favorite.FavoriteEntry.COLUMN_POSTER_PATH+ " = ?";
        String[] selectinArgs = new String[]{posterPath};

        Cursor cursor = database.query(Favorite.FavoriteEntry.TABLE_NAME,
                columns,
                selection,
                selectinArgs,
                null,
                null,
                null
                );
        int cursorCount = cursor.getCount();
        cursor.close();
        database.close();

        if (cursorCount > 0){
            return true;
        }return false;
    }

    public List<MovieObjects>getAllMovies(){
        String [] columns = {
                Favorite.FavoriteEntry._ID,
                Favorite.FavoriteEntry.COLUMN_MOVIE_ID,
                Favorite.FavoriteEntry.COLUMN_TITLE,
                Favorite.FavoriteEntry.COLUMN_POSTER_PATH,
                Favorite.FavoriteEntry.COLUMN_OVERVIEW
        };

        String sortOrder = Favorite.FavoriteEntry._ID +" DESC";
        List<MovieObjects>favoriteList = new ArrayList<>();

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query(Favorite.FavoriteEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                sortOrder);

        if (cursor.moveToNext()){
            do {
                MovieObjects movie = new MovieObjects();
                movie.setTitle(cursor.getString(cursor.getColumnIndex(Favorite.FavoriteEntry.COLUMN_TITLE)));
                movie.setPath(cursor.getString(cursor.getColumnIndex(Favorite.FavoriteEntry.COLUMN_POSTER_PATH)));
                movie.setOverview(cursor.getString(cursor.getColumnIndex(Favorite.FavoriteEntry.COLUMN_OVERVIEW)));

                favoriteList.add(movie);
            }while (cursor.moveToNext());
            cursor.close();
            database.close();
        }

        return favoriteList;

    }

}