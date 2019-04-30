package com.tz.sanga.moviestore;

public class Constants {
    public Constants() {}

    private static final String BASE_URL_IMG = "https://image.tmdb.org/t/p/original";

    public static String getImageUrl() {
        return BASE_URL_IMG;
    }
}
