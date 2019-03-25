package com.tz.sanga.moviestore.Dagger;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class SharedPreferenceModule {
    private final MovieStore application;

    public SharedPreferenceModule(MovieStore application) {
        this.application = application;
    }

    @Provides @Singleton
    Context providesApplicationContext(){return application;}
    @Provides @Singleton
    SharedPreferences providesSharedPreferences(Context application)
    {return application.getSharedPreferences("Settings", Context.MODE_PRIVATE);}

}
