package com.tz.sanga.moviestore.Fragments.First;

import com.tz.sanga.moviestore.BuildConfig;
import com.tz.sanga.moviestore.Model.API.Connector;
import com.tz.sanga.moviestore.Model.API.Service;
import com.tz.sanga.moviestore.Model.Movie;
import com.tz.sanga.moviestore.Model.MoviesResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirstPresenter {
    private static final int PAGE_START = 1;
    private int currentPage = PAGE_START;
    FirstView firstView;
    private Service service;

    public FirstPresenter(FirstView firstView) {
        this.firstView = firstView;
    }

    public void getData(){
        firstView.showLoading();

        service = Connector.getConnector().create(Service.class);
//        callSimilarMoviesApi().enqueue(new Callback<MoviesResponse>() {
//            @Override
//            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
//                List<Movie> results = fetchResults(response);
//            }
//
//            @Override
//            public void onFailure(Call<MoviesResponse> call, Throwable t) {
//
//            }
//        });


    }

    private List<Movie> fetchResults(Response<MoviesResponse> response){
        MoviesResponse SimilarMovies = response.body();
        return SimilarMovies.getResults();
    }

//    private Call<MoviesResponse> callSimilarMoviesApi(){
//        return service.getSimilarMovies(
//                moveId, BuildConfig.THE_MOVIE_DB_API_TOKEN,
//                currentPage
//
//        );
//    }
}
