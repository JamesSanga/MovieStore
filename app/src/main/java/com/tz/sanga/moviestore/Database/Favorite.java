package com.tz.sanga.moviestore.Database;

import android.provider.BaseColumns;

public class Favorite {
    public static final class FavoriteEntry implements BaseColumns{
        public static final String TABLE_NAME = "favorite";
        public static final String COLUMN_MOVIEID = "movieid";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_PATH = "posterpath";
        public static final String COLUMN_OVERVIEW = "overview";
    }
}
