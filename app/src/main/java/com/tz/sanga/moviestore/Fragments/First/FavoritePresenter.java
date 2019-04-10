package com.tz.sanga.moviestore.Fragments.First;

import android.content.Context;

import com.tz.sanga.moviestore.Model.FavoriteDb;
import com.tz.sanga.moviestore.Model.MovieObjects;

import java.util.ArrayList;

public class FavoritePresenter {
    Context context;

    private FavoriteDb favoriteDb;
    FavoriteView favoriteView;

    public FavoritePresenter(FavoriteView favoriteView) {
        this.favoriteView = favoriteView;
    }

    public void loadSqlLiteData(){
        favoriteView.showLoading();
        favoriteDb = new FavoriteDb(context);
        //favoriteDb.getAllMovies();
        favoriteView.showResultsFavorite(favoriteDb.getAllMovies());
    }
}
