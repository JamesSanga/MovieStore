package com.tz.sanga.moviestore.Model.API;

import android.content.Context;

import com.tz.sanga.moviestore.Model.NetworkChecking.ConnectivityInterceptor;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Connector {
    private static final String BASE_URL = "http://api.themoviedb.org/3/";
    private static Retrofit retrofit = null;

    public static Retrofit getConnector(Context context){
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new ConnectivityInterceptor(context))
                .build();

        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    //.client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
