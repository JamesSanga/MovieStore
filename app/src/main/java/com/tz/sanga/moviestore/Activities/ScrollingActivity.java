package com.tz.sanga.moviestore.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView;
import com.tz.sanga.moviestore.API.Connector;
import com.tz.sanga.moviestore.API.Service;
import com.tz.sanga.moviestore.Adapters.RelatedAdapter;
import com.tz.sanga.moviestore.BuildConfig;
import com.tz.sanga.moviestore.Database.FavoriteDb;
import com.tz.sanga.moviestore.Model.Movie;
import com.tz.sanga.moviestore.Model.MoviesResponse;
import com.tz.sanga.moviestore.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScrollingActivity extends AppCompatActivity {
    private static final String BASE_URL_IMG = "https://image.tmdb.org/t/p/original";
    TextView textViewTitle, textViewOverView;
    MultiSnapRecyclerView multiSnapRecyclerView;
    ImageView imageView;
    private FavoriteDb favoriteDb;
    private Movie favorite;
    String originalTitle;
    RelatedAdapter adapter;
    LinearLayoutManager layoutManager;
    private final AppCompatActivity activity = ScrollingActivity.this;
    private Service movieService;

    private static final int PAGE_START = 1;
    private int currentPage = PAGE_START;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        originalTitle = getIntent().getExtras().getString("original_title");
        getSupportActionBar().setTitle(originalTitle);

        textViewTitle = findViewById(R.id.move_title_name);
        textViewOverView = findViewById(R.id.details);
        imageView = findViewById(R.id.poster_image);
        multiSnapRecyclerView = findViewById(R.id.relatedMovies);

        originalTitle = getIntent().getExtras().getString("original_title");
        String averageVote = getIntent().getExtras().getString("average_vote");
        String overView = getIntent().getExtras().getString("overview");
        String thumbnail = getIntent().getExtras().getString("poster_path");

        //load image to image view
        Glide.with(this).load(BASE_URL_IMG + thumbnail).placeholder(R.drawable.loading).into(imageView);
        //adapter = new MoviesAdapter(this);
        adapter = new RelatedAdapter(this);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        multiSnapRecyclerView.setLayoutManager(layoutManager);

        multiSnapRecyclerView.setItemAnimator(new DefaultItemAnimator());
        multiSnapRecyclerView.setAdapter(adapter);

        textViewTitle.setText(originalTitle);
        textViewOverView.setText(overView);

        saveMovieDetails();


        //init service and load data
        movieService = Connector.getConnector().create(Service.class);
        loaFirstPage();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveMovieDetails() {
        final FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = getSharedPreferences("com.tz.sanga.moviestore.Activities.ScrollingActivity",MODE_PRIVATE).edit();
                editor.putBoolean("Movie details saved successful", true);
                editor.commit();

                Snackbar.make(floatingActionButton,"Details saved",Snackbar.LENGTH_LONG).show();

                addToSqlDB();
            }
        });

    }

    public void addToSqlDB() {

        favoriteDb = new FavoriteDb(activity);
        favorite = new Movie();
        int movie_id = getIntent().getExtras().getInt("id");
        String rate = getIntent().getExtras().getString("vote_average");
        String poster = getIntent().getExtras().getString("poster_path");

        favorite.setId(movie_id);
        favorite.setOriginalTile(textViewTitle.getText().toString().trim());
        favorite.setOverview(textViewOverView.getText().toString().trim());
        favorite.setPosterPath(poster);

        favoriteDb.addFavorite(favorite);
    }

    private void loaFirstPage(){

        callTopRatedMoviesApi().enqueue(new Callback<MoviesResponse>(){

            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {

                //got data and send them to adapter
                List<Movie> results = fetchResults(response);
                adapter.addAll(results);

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

    private Call<MoviesResponse> callTopRatedMoviesApi(){
        return movieService.getTopRatedMovies(
                BuildConfig.THE_MOVIE_DB_API_TOKEN,
                currentPage

        );
    }

}
