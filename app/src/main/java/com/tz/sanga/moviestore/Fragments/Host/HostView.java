package com.tz.sanga.moviestore.Fragments.Host;

import com.tz.sanga.moviestore.Model.Movie;
import com.tz.sanga.moviestore.Model.MoviesResponse;

import java.util.List;

public interface HostView {
    void showLoading();
    void hideLoading();
    void showResults(List<Movie> moveData);
    void onErrorLoading(String message);
}
