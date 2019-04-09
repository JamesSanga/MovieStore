package com.tz.sanga.moviestore.Fragments.Host;

import com.tz.sanga.moviestore.Model.MoviesResponse;

import java.util.List;

public interface HostView {
    void showLoading();
    void hideLoading();
    void showResults(MoviesResponse moveData);
    void onErrorLoading(String message);
}
