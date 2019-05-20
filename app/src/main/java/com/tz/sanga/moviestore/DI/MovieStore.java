package com.tz.sanga.moviestore.DI;

import android.app.Application;

public class MovieStore extends Application {

   private MovieComponents myApplicationComponents;

    @Override
    public void onCreate() {
        super.onCreate();
        myApplicationComponents = DaggerMovieComponents.builder()
                .sharedPreferenceModule(new SharedPreferenceModule(this)).build();
        myApplicationComponents.inject(this);
    }

    public MovieComponents getMyApplicationComponents() {
        return myApplicationComponents;
    }
}
