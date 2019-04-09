package com.tz.sanga.moviestore.Fragments.Host;

import com.tz.sanga.moviestore.BuildConfig;
import com.tz.sanga.moviestore.Model.API.Connector;
import com.tz.sanga.moviestore.Model.API.Service;
import com.tz.sanga.moviestore.Model.MoviesResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HostPresenter {

    private static final int PAGE_START = 1;
    private int currentPage = PAGE_START;
    HostView hostView;
    private Service movieService;

    public HostPresenter(HostView hostView) {
        this.hostView = hostView;
    }

    void getData(){
        hostView.showLoading();
        // process data here
        movieService= Connector.getConnector().create(Service.class);
        loadMovies();
    }

    private void loadMovies() {

        callPopularMoviesApi().enqueue(new Callback<MoviesResponse>(){

            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                hostView.hideLoading();

                if (response.isSuccessful() && response.body() != null){
                    //got data and send them to adapter
                    hostView.showResults(response.body());

                }
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
                hostView.hideLoading();
                hostView.onErrorLoading(t.getLocalizedMessage());
            }
        });
    }

    private Call<MoviesResponse> callPopularMoviesApi(){
        return movieService.getPopularMovies(
                BuildConfig.THE_MOVIE_DB_API_TOKEN,
                currentPage
        );
    }
}
