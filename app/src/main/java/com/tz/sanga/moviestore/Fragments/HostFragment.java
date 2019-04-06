package com.tz.sanga.moviestore.Fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.tz.sanga.moviestore.API.Connector;
import com.tz.sanga.moviestore.API.Service;
import com.tz.sanga.moviestore.Activities.MainActivity;
import com.tz.sanga.moviestore.Adapters.MoviesAdapter;
import com.tz.sanga.moviestore.BuildConfig;
import com.tz.sanga.moviestore.DaggerInjection.MovieStore;
import com.tz.sanga.moviestore.Model.Movie;
import com.tz.sanga.moviestore.Model.MoviesResponse;
import com.tz.sanga.moviestore.R;
import com.tz.sanga.moviestore.Utils.MoviesScrollListener;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HostFragment extends Fragment {

    @Inject
    public SharedPreferences preferences;
    @Inject public Context context;

    @BindView(R.id.Movie_list) RecyclerView recyclerView;
    @BindView(R.id.Load_movies) ProgressBar progressBar;

    private static final String TAG = "TAG";
    MoviesAdapter adapter;
    LinearLayoutManager layoutManager;
    final String [] moviesOptions ={"Popular movies", "Top rated movies"};
    AlertDialog.Builder mBuilder;

    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 20;
    private int currentPage = PAGE_START;
    private Service movieService;

    public HostFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_host, container, false);
        ButterKnife.bind(this, view);

        ((MovieStore)getActivity().getApplication()).getMyApplicationComponents().inject(this);

        adapter = new MoviesAdapter(getContext());
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
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
        loadMovies();

        return view;
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            showChangeMoviesOptions();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
    private Call<MoviesResponse> callTopRatedMoviesApi(){
        return movieService.getTopRatedMovies(
                BuildConfig.THE_MOVIE_DB_API_TOKEN,
                currentPage
        );
    }

    private void loaFirstPage1() {
        callTopRatedMoviesApi(). enqueue(new Callback<MoviesResponse>(){
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
            public void onFailure(Call<MoviesResponse> call, Throwable t) {}
        });
    }

    private void showChangeMoviesOptions() {
        int number = preferences.getInt("No", Integer.parseInt("0"));
        mBuilder = new AlertDialog.Builder(getContext());
        mBuilder.setTitle("Sort movies by");
        mBuilder.setSingleChoiceItems(moviesOptions, number, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (i==0){
//                    Set popular movies
                    setMovies("P", String.valueOf(i));
                }else if (i==1) {
                    //set Top rated movies
                    setMovies("T", String.valueOf(i));
                }
                dialogInterface.dismiss();
            }
        });

        AlertDialog mDialog = mBuilder.create();
//        show dialog
        mDialog.show();
    }

    private void setMovies(String movies, String i) {
        Locale locale = new Locale(movies, i);
        locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
       getActivity().getBaseContext().getResources().updateConfiguration(configuration, getActivity()
               .getBaseContext().getResources().getDisplayMetrics());

//        save data to shared preferences
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("My_movie", movies);
        editor.putInt("No", Integer.parseInt(i));
        editor.apply();
    }

    //    load movies shared in preferences
    public void loadMovies(){
        String Movie = preferences.getString("My_movie", "");
        if (Movie.equals("P")){
            loaFirstPage();
        }
        else if (Movie.equals("T")){
            loaFirstPage1();
        }
    }

}
