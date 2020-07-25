package com.tz.sanga.moviestore.UI;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.tz.sanga.moviestore.DI.MovieStore;

import javax.inject.Inject;

public class BaseActivity extends AppCompatActivity {
    @Inject public SharedPreferences preferences;
    @Inject public Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((MovieStore)getApplication()).getMyApplicationComponents().inject(this);

    }
}
