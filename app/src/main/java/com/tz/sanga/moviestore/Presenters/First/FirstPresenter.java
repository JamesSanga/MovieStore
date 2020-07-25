package com.tz.sanga.moviestore.Presenters.First;


import android.content.Context;
import android.support.annotation.NonNull;

import com.tz.sanga.moviestore.BuildConfig;
import com.tz.sanga.moviestore.Model.API.Connector;
import com.tz.sanga.moviestore.Model.API.Service;
import com.tz.sanga.moviestore.Model.Movie;
import com.tz.sanga.moviestore.Model.MoviesResponse;
import com.tz.sanga.moviestore.Model.Trailer;
import com.tz.sanga.moviestore.Model.TrailerResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirstPresenter {
    private static final int PAGE_START = 1;
    private int currentPage = PAGE_START;
    private FirstView firstView;
    private Service service;
    private int moveId;
    private Context context;
    private List<Trailer> trailerList;


    public FirstPresenter(Context context, FirstView firstView, int moveId) {
        this.context = context;
        this.firstView = firstView;
        this.moveId = moveId;
    }

    public void updateMoveId(int moveId){
        this.moveId = moveId;
    }

    public void getData(){
        firstView.showLoading();
        service = Connector.getConnector(context).create(Service.class);
       callSimilarMoviesApi().enqueue(new Callback<MoviesResponse>() {
           @Override
           public void onResponse(@NonNull Call<MoviesResponse> call, @NonNull Response<MoviesResponse> response) {
               List<Movie>results = fetchResults(response);
               firstView.showResults(results);
           }

           @Override
           public void onFailure(@NonNull Call<MoviesResponse> call, @NonNull Throwable t) {
               firstView.onErrorLoading(t.getLocalizedMessage());
           }
       });
    }

    private List<Movie> fetchResults(Response<MoviesResponse> response){
        MoviesResponse SimilarMovies = response.body();
        return SimilarMovies.getResults();
    }

    private Call<MoviesResponse> callSimilarMoviesApi(){
        return service.getSimilarMovies(
                moveId, BuildConfig.THE_MOVIE_DB_API_TOKEN,
                currentPage

        );
    }

    public void processTrailer(){
        Service service = Connector.getConnector(context).create(Service.class);
        Call<TrailerResponse> call = service.getTrailer(moveId, BuildConfig.THE_MOVIE_DB_API_TOKEN);
        call.enqueue(new Callback<TrailerResponse>() {
            @Override
            public void onResponse(@NonNull Call<TrailerResponse> call, @NonNull Response<TrailerResponse> response) {
                if (response.body() != null)
                trailerList = response.body().getResults();
                firstView.trailerMoviesResults(trailerList);
            }

            @Override
            public void onFailure(@NonNull Call<TrailerResponse> call, @NonNull Throwable t) {
                firstView.onErrorLoading(t.getLocalizedMessage());
            }
        });
    }
}
