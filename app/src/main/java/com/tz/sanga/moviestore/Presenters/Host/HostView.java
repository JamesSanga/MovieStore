package com.tz.sanga.moviestore.Presenters.Host;

import com.tz.sanga.moviestore.Model.Movie;

import java.util.List;

public interface HostView {
    void showLoading();
    void hideLoading();
    void showResults(List<Movie> moveData);
    void onErrorLoading(String message);
    void onLoadingFirstPage(boolean firstPage);
    void hideLoading(boolean b);
}
