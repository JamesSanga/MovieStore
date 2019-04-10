package com.tz.sanga.moviestore.Fragments.First;

import com.tz.sanga.moviestore.Model.MoviesResponse;

public interface FirstView {
    void showLoading();
    void hideLoading();
    void showResults(MoviesResponse moveData);
    void onErrorLoading(String message);
}
