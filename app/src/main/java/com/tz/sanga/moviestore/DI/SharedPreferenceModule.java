package com.tz.sanga.moviestore.DI;

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

    @Provides @Singleton Context providesApplicationContext(){return application;}
    @Provides @Singleton SharedPreferences providesSharedPreferences(Context application)
    {return application.getSharedPreferences("Setting", Context.MODE_PRIVATE);}


}
