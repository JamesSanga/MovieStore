package com.tz.sanga.moviestore.Activities;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    MoviesAdapter adapter;
    LinearLayoutManager layoutManager;
    @BindView(R.id.Movie_list)RecyclerView recyclerView;
    @BindView(R.id.Load_movies)ProgressBar progressBar;

    private AppCompatActivity activity = MainActivity.this;

    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;

    private int TOTAL_PAGES = 20;
    private int currentPage = PAGE_START;

    private Service movieService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

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
        loadMovies();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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
            public void onFailure(Call<MoviesResponse> call, Throwable t) {

            }
        });

    }


    private void showChangeMoviesOptions() {
        final String [] moviesOptions ={"Popular movies", "Top rated movies"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        mBuilder.setTitle("Choose movies type to display");
        mBuilder.setSingleChoiceItems(moviesOptions, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (i==0){
//                    Set popular movies
                    setMovies("P");
                }else if (i==1) {
                    //set Top rated movies
                    setMovies("T");
                }
                dialogInterface.dismiss();
            }
        });

        AlertDialog mDialog = mBuilder.create();
//        show dialog
        mDialog.show();
    }

    private void setMovies(String movies) {
        Locale locale = new Locale(movies);
        locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());

//        save data to shared preferences
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("My_movie", movies );
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
        else {
            loaFirstPage();
        }
    }
}
