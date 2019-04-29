package com.tz.sanga.moviestore.YoutubePlayer;

import com.tz.sanga.moviestore.BuildConfig;

public class YoutubeConfig {
    public YoutubeConfig(){

    }
    private static final String API_KEY = BuildConfig.THE_YOUTUBE_API_TOKEN;

    public static String getApiKey() {
        return API_KEY;
    }
}
