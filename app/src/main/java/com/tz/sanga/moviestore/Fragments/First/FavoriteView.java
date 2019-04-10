package com.tz.sanga.moviestore.Fragments.First;

import com.tz.sanga.moviestore.Model.Movie;
import com.tz.sanga.moviestore.Model.MovieObjects;

import java.util.List;

public interface FavoriteView {
    void showLoading();
    void hideLoading();
    void showResultsFavorite(List<MovieObjects> moveData);
    void onErrorLoading(String message);
}
