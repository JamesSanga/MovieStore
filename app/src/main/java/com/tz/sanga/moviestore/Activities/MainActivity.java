package com.tz.sanga.moviestore.Activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.tz.sanga.moviestore.API.Connector;
import com.tz.sanga.moviestore.API.Service;
import com.tz.sanga.moviestore.Adapters.MoviesAdapter;
import com.tz.sanga.moviestore.BuildConfig;
import com.tz.sanga.moviestore.Model.Movie;
import com.tz.sanga.moviestore.Model.MoviesResponse;
import com.tz.sanga.moviestore.R;
import com.tz.sanga.moviestore.Utils.MoviesScrollListener;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    MoviesAdapter adapter;
    LinearLayoutManager layoutManager;
    RecyclerView recyclerView;
    ProgressBar progressBar;

    private AppCompatActivity activity = MainActivity.this;

    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;

    private int TOTAL_PAGES = 8;
    private int currentPage = PAGE_START;

    private Service movieService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.Movie_list);
        progressBar = findViewById(R.id.Load_movies);

        //adapter = new MoviesAdapter(this);
        adapter = new MoviesAdapter(this);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new MoviesScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                //network delay for calling api
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadNextPage();
                    }
                }, 1000);
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }
        });

        //init service and load data
        movieService = Connector.getConnector().create(Service.class);
        loaFirstPage();
    }

    private void loaFirstPage(){
        Log.d(TAG, "loaFirstPage: ");

        callPopularMoviesApi().enqueue(new Callback<MoviesResponse>(){

            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {

                //got data and send them to adapter
                List<Movie> results = fetchResults(response);
                progressBar.setVisibility(View.GONE);
                adapter.addAll(results);

                if (currentPage <= TOTAL_PAGES) adapter.addLoadingFooter();
                else isLastPage = true;
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {

            }
        });
    }

    private List<Movie> fetchResults(Response<MoviesResponse> response){
        MoviesResponse topRatedMovies = response.body();
        return topRatedMovies.getResults();
    }

    private void loadNextPage(){
        Log.d(TAG, "loadNextPage: " + currentPage);
        callPopularMoviesApi().enqueue(new Callback<MoviesResponse>(){

            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                adapter.removeLoadingFooter();
                isLoading = false;

                List<Movie> results = fetchResults(response);
                adapter.addAll(results);

                if (currentPage != TOTAL_PAGES)adapter.addLoadingFooter();
                else isLastPage = true;
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
                t.printStackTrace();
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
