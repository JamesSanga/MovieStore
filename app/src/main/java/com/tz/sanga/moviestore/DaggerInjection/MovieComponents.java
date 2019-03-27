package com.tz.sanga.moviestore.DaggerInjection;

import com.tz.sanga.moviestore.Activities.BaseActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton @Component(modules = {SharedPreferenceModule.class})

public interface MovieComponents {

    void inject(BaseActivity baseActivity);
    void inject(MovieStore application);
}
