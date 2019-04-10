package com.tz.sanga.moviestore.Fragments.Host;

import com.tz.sanga.moviestore.BuildConfig;
import com.tz.sanga.moviestore.Model.API.Connector;
import com.tz.sanga.moviestore.Model.API.Service;
import com.tz.sanga.moviestore.Model.Movie;
import com.tz.sanga.moviestore.Model.MoviesResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HostPresenter {
    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 20;
    private int currentPage = PAGE_START;
    HostView hostView;
    private Service movieService;
    int movePreference;

    public HostPresenter(HostView hostView, int movePreference) {
        this.hostView = hostView;
        this.movePreference = movePreference;
    }

    public void getData(){
        hostView.showLoading();
        movieService= Connector.getConnector().create(Service.class);
            callMoviesApi().enqueue(new Callback<MoviesResponse>() {
                @Override
                public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                    hostView.hideLoading();
                    if (response.isSuccessful() && response.body() != null) {
                        List<Movie> results = fetchResults(response);
                        hostView.showResults(results);
                    }
                }

                @Override
                public void onFailure(Call<MoviesResponse> call, Throwable t) {
                    hostView.hideLoading();
                    hostView.onErrorLoading(t.getLocalizedMessage());
                }
            });
    }

    private List<Movie> fetchResults(Response<MoviesResponse> response){
        MoviesResponse movies = response.body();
        return movies.getResults();
    }

    private Call<MoviesResponse> callMoviesApi() {
        if (movePreference == 0) {
            return movieService.getPopularMovies(
                    BuildConfig.THE_MOVIE_DB_API_TOKEN,
                    currentPage
            );
        }
        if (movePreference == 1) {
            return movieService.getTopRatedMovies(
                    BuildConfig.THE_MOVIE_DB_API_TOKEN,
                    currentPage
            );
        }else return null;
    }
}
