package com.tz.sanga.moviestore.Fragments.Host;

import android.content.SharedPreferences;

import com.tz.sanga.moviestore.Adapters.MoviesAdapter;
import com.tz.sanga.moviestore.BuildConfig;
import com.tz.sanga.moviestore.Model.API.Connector;
import com.tz.sanga.moviestore.Model.API.Service;
import com.tz.sanga.moviestore.Model.Movie;
import com.tz.sanga.moviestore.Model.MoviesResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class HostPresenter {

    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 20;
    private int currentPage = PAGE_START;
    HostView hostView;
    private Service movieService;
    MoviesAdapter adapter;

    public HostPresenter(HostView hostView) {
        this.hostView = hostView;
    }

    public void getData(){
        hostView.showLoading();
        movieService= Connector.getConnector().create(Service.class);
            callPopularMoviesApi().enqueue(new Callback<MoviesResponse>() {
                @Override
                public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                    hostView.hideLoading();
                    if (response.isSuccessful() && response.body() != null) {
                        //got data and send them to adapter
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

    public void getTopRated(){
        hostView.showLoading();
        movieService= Connector.getConnector().create(Service.class);
        callTopRatedMoviesApi().enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                hostView.hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    //got data and send them to adapter
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

    private Call<MoviesResponse> callPopularMoviesApi(){
            return movieService.getPopularMovies(
                    BuildConfig.THE_MOVIE_DB_API_TOKEN,
                    currentPage
            );
    }

    private Call<MoviesResponse> callTopRatedMoviesApi(){
        return movieService.getTopRatedMovies(
                BuildConfig.THE_MOVIE_DB_API_TOKEN,
                currentPage
        );
    }
}
