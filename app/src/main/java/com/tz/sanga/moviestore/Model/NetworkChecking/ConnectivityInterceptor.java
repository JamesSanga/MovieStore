package com.tz.sanga.moviestore.Model.NetworkChecking;

import android.content.Context;
import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ConnectivityInterceptor implements Interceptor {

    private Context context;

    public ConnectivityInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (!NetworkUtil.isOnline(context)) {
            throw new NoConnectivityException();
        }

        Request.Builder builder = chain.request().newBuilder();
        return chain.proceed(builder.build());
    }

}