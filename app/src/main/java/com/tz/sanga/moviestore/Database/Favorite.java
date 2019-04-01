package com.tz.sanga.moviestore.Database;

import android.provider.BaseColumns;

public class Favorite {
    public static final class FavoriteEntry implements BaseColumns{
        public static final String DATABASE_NAME = "favorite.db";
        public static final String TABLE_NAME = "favorite";
        public static final String COLUMN_MOVIE_ID = "movieid";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_PATH = "posterpath";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final int DATABASE_VERSION = 1;
        public static final String TAG = "FAVORITE";
    }
}

