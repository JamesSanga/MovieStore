package com.tz.sanga.moviestore.Presenters.Host;

import android.content.Context;
import android.support.annotation.NonNull;

import com.tz.sanga.moviestore.BuildConfig;
import com.tz.sanga.moviestore.Model.API.Connector;
import com.tz.sanga.moviestore.Model.API.Service;
import com.tz.sanga.moviestore.Model.Movie;
import com.tz.sanga.moviestore.Model.MoviesResponse;
import com.tz.sanga.moviestore.Model.NetworkChecking.HttpRequestErrors;

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
    private HostView hostView;
    private Service movieService;
    private int movePreference;
    private Context context;

    public HostPresenter(Context context, HostView hostView, int movePreference) {
        this.context = context;
        this.hostView = hostView;
        this.movePreference = movePreference;
    }

    public void getData(){
        hostView.showLoading();
        movieService= Connector.getConnector(context).create(Service.class);
            callMoviesApi().enqueue(new Callback<MoviesResponse>() {
                @Override
                public void onResponse(@NonNull Call<MoviesResponse> call, @NonNull Response<MoviesResponse> response) {
                    hostView.hideLoading();
                    if (response.isSuccessful() && response.body() != null) {
                        List<Movie> results = fetchResults(response);
                        hostView.showResults(results);

                        if (currentPage <= TOTAL_PAGES){
                            hostView.onLoadingFirstPage(isLoading = true);
                        }else
                            hostView.onLoadingFirstPage(isLastPage = true);
                    }
                    if (response.code() == 401){
                        hostView.onErrorLoading(HttpRequestErrors.connection_required);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<MoviesResponse> call, @NonNull Throwable t) {
                    hostView.hideLoading();
                    hostView.onErrorLoading(t.getLocalizedMessage());
                    hostView.onErrorLoading(t.getMessage());
//                    if (t instanceof NoConnectivityException)
//                    {
//                        hostView.onErrorLoading(t.getMessage());
//                    }
                }
            });
    }


//    public void loadNext(){
//        movieService= Connector.getConnector(context).create(Service.class);
//        Call<MoviesResponse> call = movieService.getPopularMovies(BuildConfig.THE_MOVIE_DB_API_TOKEN, currentPage);
//        call.enqueue(new Callback<MoviesResponse>() {
//            @Override
//            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
//                hostView.hideLoading(isLoading = false);
//                List<Movie> results = response.body().getResults();
//                hostView.showResults(results);
//            }
//
//            @Override
//            public void onFailure(Call<MoviesResponse> call, Throwable t) {
////                if (t instanceof NoConnectivityException){
////                }
//            }
//        });
//    }

    private List<Movie> fetchResults(Response<MoviesResponse> response){
        MoviesResponse movies = response.body();
        if (movies != null)
        return movies.getResults();
        return null;
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
