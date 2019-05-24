package com.tz.sanga.moviestore.Presenters.First;

import com.tz.sanga.moviestore.Model.Movie;
import com.tz.sanga.moviestore.Model.Trailer;

import java.util.List;

public interface FirstView {
    void showLoading();
    void showResults(List<Movie> moveData);
    void onErrorLoading(String message);
    void trailerMoviesResults(List<Trailer>trailers);
}
