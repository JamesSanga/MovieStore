package com.tz.sanga.moviestore;

import com.tz.sanga.moviestore.Repositories.FavoriteRepository;

public class Constants {
    public Constants() {}

    private static final String BASE_URL_IMG = "https://image.tmdb.org/t/p/original";
    public static String getImageUrl() {
        return BASE_URL_IMG;
    }
    public static FavoriteRepository favoriteRepository;
}
