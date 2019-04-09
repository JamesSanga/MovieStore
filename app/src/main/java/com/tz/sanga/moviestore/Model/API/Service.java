package com.tz.sanga.moviestore.Model.API;

import com.tz.sanga.moviestore.Model.MoviesResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Service {
    @GET("movie/popular")
    Call<MoviesResponse>getPopularMovies(@Query("api_key") String apiKey, @Query("page") int pageIndex);

    @GET("movie/top_rated")
    Call<MoviesResponse>getTopRatedMovies(@Query("api_key") String apiKey, @Query("page") int pageIndex);

    @GET("movie/{movie_id}/similar")
    Call<MoviesResponse>getSimilarMovies(@Path("movie_id") int similar, @Query("api_key") String apiKey,
                                         @Query("page") int pageIndex);
}
