package com.tz.sanga.moviestore.Fragments.First;

import com.tz.sanga.moviestore.Model.Movie;
import com.tz.sanga.moviestore.Model.MoviesResponse;
import com.tz.sanga.moviestore.Model.Trailer;

import java.util.List;

public interface FirstView {
    void showLoading();
    void hideLoading();
    void showResults(List<Movie> moveData);
    void onErrorLoading(String message);
    void trailerMoviesResults(List<Trailer>trailers);
}
