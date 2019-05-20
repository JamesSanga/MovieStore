package com.tz.sanga.moviestore.DI;

import com.tz.sanga.moviestore.UI.BaseActivity;
import com.tz.sanga.moviestore.UI.HostFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton @Component(modules = {SharedPreferenceModule.class})

public interface MovieComponents {

    void inject(BaseActivity baseActivity);
    void inject(MovieStore application);
    void inject(HostFragment hostFragment);
}
