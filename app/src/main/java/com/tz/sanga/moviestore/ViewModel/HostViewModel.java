package com.tz.sanga.moviestore.ViewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import com.tz.sanga.moviestore.BuildConfig;
import com.tz.sanga.moviestore.Model.API.Connector;
import com.tz.sanga.moviestore.Model.API.Service;
import com.tz.sanga.moviestore.Model.Movie;
import com.tz.sanga.moviestore.Model.MoviesResponse;
import com.tz.sanga.moviestore.Presenters.Host.HostView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HostViewModel extends ViewModel {
    private static final int PAGE_START = 1;
    private int movePreference = 5;
    private MutableLiveData<List<Movie>>results;
    private Service movieService;
    private int currentPage = PAGE_START;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 20;
    private HostView hostView;

    public HostViewModel(HostView hostView) {
        this.hostView = hostView;
    }

    //we will call this method to get the data
    public LiveData<List<Movie>> getMovies(Context context) {
        //if the list is null
        if (results == null) {
            results = new MutableLiveData<List<Movie>>();
            //we will load it asynchronously from server in this method
            loadMovies(context);
        }

        //finally we will return the list
        return results;
    }


    //This method is using Retrofit to get the JSON data from URL
    private void loadMovies(Context context) {
        movieService = Connector.getConnector(context).create(Service.class);
        callMoviesApi().enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                hostView.hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                   List <Movie> results = fetchResults(response);

                    if (currentPage <= TOTAL_PAGES){
                        hostView.onLoadingFirstPage(isLoading = true);
                    }else
                        hostView.onLoadingFirstPage(isLastPage = true);
                }
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {

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
