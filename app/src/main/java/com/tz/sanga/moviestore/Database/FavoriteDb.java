package com.tz.sanga.moviestore.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.provider.BaseColumns._ID;

public class FavoriteDb extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "favorite.db";
    public static final String TABLE_NAME = "favorite";
    public static final String COLUMN_MOVIEID = "movieid";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_POSTER_PATH = "posterpath";
    public static final String COLUMN_OVERVIEW = "overview";
    public static final int DATABASE_VERSION = 1;
    public static final String LOGTAG = "FAVORITE";

    SQLiteOpenHelper dbHandler;
    SQLiteDatabase db;


    public FavoriteDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void open() {
        Log.i(LOGTAG, "open: Database Opened");
        db = dbHandler.getWritableDatabase();
    }

    public void close() {
        Log.i(LOGTAG, "close: Database closed");
        dbHandler.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String CREATE_TABLE = " CREATE TABLE " + TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_MOVIEID + " INTEGER, " +
                COLUMN_TITLE + " TEXT NOT NULL, " +
                COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                COLUMN_POSTER_PATH + " TEXT NOT NULL" + ");";
        sqLiteDatabase.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);

    }

    public boolean addFavorite(String originalTitle, String averageVote, String overView, String thumbnail) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_MOVIEID, averageVote);
        values.put(COLUMN_TITLE, originalTitle);
        values.put(COLUMN_OVERVIEW, overView);
        values.put(COLUMN_POSTER_PATH, thumbnail);

        long result = database.insert(TABLE_NAME, null, values);
        database.close();

        if (result == -1){
            return false;
        }else return true;

    }

//    public List<Movie>getAllFavorite(){
//
//        String [] columns = {
//                _ID,
//                Favorite.FavoriteEntry.COLUMN_MOVIEID,
//                Favorite.FavoriteEntry.COLUMN_TITLE,
//                Favorite.FavoriteEntry.COLUMN_POSTER_PATH,
//                Favorite.FavoriteEntry.COLUMN_OVERVIEW
//        };
//
//        String sortOder = _ID + " ASC";
//        List<Movie> favoriteList = new ArrayList<>();
//        SQLiteDatabase database = this.getReadableDatabase();
//
//        Cursor cursor = database.query(Favorite.FavoriteEntry.TABLE_NAME,
//                columns,
//                null,
//                null,
//                null,
//                null,
//                sortOder
//                );
//
//        if (cursor.moveToFirst()){
//            do {
//                Movie movie = new Movie();
//                movie.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Favorite.FavoriteEntry.COLUMN_MOVIEID))));
//                movie.setOriginalTile(cursor.getString(cursor.getColumnIndex(Favorite.FavoriteEntry.COLUMN_TITLE)));
//                movie.setPosterPath(cursor.getString(cursor.getColumnIndex(Favorite.FavoriteEntry.COLUMN_POSTER_PATH)));
//                movie.setOverview(cursor.getString(cursor.getColumnIndex(Favorite.FavoriteEntry.COLUMN_OVERVIEW)));
//
//                favoriteList.add(movie);
//            }while (cursor.moveToNext());
//        }
//
//        cursor.close();
//        database.close();
//
//        return favoriteList;
//    }
    public Cursor getMovies(){
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor data = database.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return data;
    }

}