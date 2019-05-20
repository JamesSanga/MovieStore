package com.tz.sanga.moviestore;

import com.tz.sanga.moviestore.Database.Local.FavoriteDatabase;

public class Constants {
    public Constants() {}

    private static final String BASE_URL_IMG = "https://image.tmdb.org/t/p/original";

    public static String getImageUrl() {
        return BASE_URL_IMG;
    }

    public static String path = "/or06FN3Dka5tukK1e9sl16pB3iy.jpg";
    public static FavoriteDatabase favoriteRoomDatabase;
}
