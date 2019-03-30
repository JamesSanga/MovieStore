package com.tz.sanga.moviestore.Activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView;
import com.tz.sanga.moviestore.API.Connector;
import com.tz.sanga.moviestore.API.Service;
import com.tz.sanga.moviestore.Adapters.FavoriteAdapter;
import com.tz.sanga.moviestore.Adapters.RelatedAdapter;
import com.tz.sanga.moviestore.BuildConfig;
import com.tz.sanga.moviestore.Database.Favorite;
import com.tz.sanga.moviestore.Database.FavoriteDb;
import com.tz.sanga.moviestore.Database.MovieObjects;
import com.tz.sanga.moviestore.Model.Movie;
import com.tz.sanga.moviestore.Model.MoviesResponse;
import com.tz.sanga.moviestore.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ScrollingActivity extends AppCompatActivity {
    private static final String BASE_URL_IMG = "https://image.tmdb.org/t/p/original";
    @BindView(R.id.move_title_name) TextView textViewTitle;
    @BindView(R.id.details) TextView textViewOverView;
    @BindView(R.id.relatedMovies) MultiSnapRecyclerView multiSnapRecyclerView;
    @BindView(R.id.poster_image) ImageView imageView;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.load_similar_movies) ProgressBar progressBar;
    @BindView(R.id.load_big_view) ProgressBar progressBar1;
    @BindView(R.id.similar_movies_title) TextView textView;
    @BindView(R.id.similar_movies_layout) RelativeLayout relativeLayout;

    private FavoriteDb favoriteDb;
    String originalTitle, averageVote, overView, thumbnail, similar;
    RelatedAdapter adapter;
    LinearLayoutManager layoutManager;
    private final AppCompatActivity activity = ScrollingActivity.this;
    private Service movieService;
    FavoriteAdapter favoriteAdapter;

    private static final int PAGE_START = 1;
    private int currentPage = PAGE_START;
    private static final String TAG = "TAG";

    private ArrayList<MovieObjects> movieList = new ArrayList<>();
    private ArrayList<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        originalTitle = getIntent().getExtras().getString("original_title");
        getSupportActionBar().setTitle(originalTitle);
        ButterKnife.bind(this);

        originalTitle = getIntent().getExtras().getString("original_title");
        averageVote = getIntent().getExtras().getString("average_vote");
        overView = getIntent().getExtras().getString("overview");
        thumbnail = getIntent().getExtras().getString("poster_path");
        similar = getIntent().getExtras().getString("id");

        Log.d(TAG, "onCreate: "+similar);

        //load image to image view
        Glide.with(this)
                .load(BASE_URL_IMG + thumbnail)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        progressBar1.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBar1.setVisibility(View.GONE);
                        return false;
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.ALL) // cache all original and resizable images
                .centerCrop()
                .crossFade()
                .into(imageView);
        //adapter = new MoviesAdapter(this);
        adapter = new RelatedAdapter(this);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        multiSnapRecyclerView.setLayoutManager(layoutManager);

        multiSnapRecyclerView.setItemAnimator(new DefaultItemAnimator());
        multiSnapRecyclerView.setAdapter(adapter);

        textViewTitle.setText(originalTitle);
        textViewOverView.setText(overView);

        saveMovieDetails();
        loadSqliteData();
        //init service and load data
        movieService = Connector.getConnector().create(Service.class);
        loadSimilarMovies();
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
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToSqlDB(view);
            }
        });
    }

    public void addToSqlDB(final View view) {
        favoriteDb = new FavoriteDb(this);
        if (!favoriteDb.checkMovie(thumbnail)){
            boolean insertData = favoriteDb.addFavorite(Integer.parseInt(similar), originalTitle, overView, thumbnail);
            if (insertData == true){
              Snackbar.make(view, originalTitle + " added to favorite", Snackbar.LENGTH_LONG).setAction("REMOVE", new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                     deleteFavorite(v);
                  }
              }).show();
            }else {
                Snackbar.make(view, originalTitle + " not added to favorite", Snackbar.LENGTH_LONG).show();
            }
        }else {
            Snackbar.make(view, originalTitle + " exist to favorite", Snackbar.LENGTH_LONG).setAction("REMOVE", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteFavorite(v);
                }
            }).show();
        }
    }

    public void deleteFavorite(View view){
       // boolean delete = favoriteDb.deleteMovie(Integer.parseInt(similar));
        boolean delete = favoriteDb.deleteMovie(2);
        if (delete == true){
            Snackbar.make(view, "Deleted successful", Snackbar.LENGTH_LONG).show();
        } Snackbar.make(view, "Not successful", Snackbar.LENGTH_LONG).show();
    }

    public void loadSqliteData(){
        favoriteDb = new FavoriteDb(getApplicationContext());
        Cursor data = favoriteDb.getMovies("select * from favorite");

        if (data == null){
            TextView textView = findViewById(R.id.favorite_movies);
            textView.setVisibility(View.GONE);
        }else {
            TextView textView = findViewById(R.id.favorite_movies);
            textView.setVisibility(View.VISIBLE);
            if (data.moveToNext()) {
                do {
                    MovieObjects movieObjects = new MovieObjects();
                    movieObjects.setTitle(data.getString(2));
                    movieObjects.setPath(data.getString(4));
                    movieList.add(movieObjects);

                }while (data.moveToNext());
            }
        }

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        favoriteAdapter = new FavoriteAdapter(getApplicationContext(), movieList);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(favoriteAdapter);
     }

    private void loadSimilarMovies(){
        callSimilarMoviesApi().enqueue(new Callback<MoviesResponse>(){
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                //got data and send them to adapter
                progressBar.setVisibility(View.INVISIBLE);
                List<Movie> results = fetchResults(response);
                if (results.size()<1){
                    textView.setVisibility(View.GONE);
                    relativeLayout.setVisibility(View.GONE);
                }
               adapter.addAll(results);
            }
            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {

            }
        });
    }

    private List<Movie> fetchResults(Response<MoviesResponse> response){
        MoviesResponse SimilarMovies = response.body();
        return SimilarMovies.getResults();
    }

    private Call<MoviesResponse> callSimilarMoviesApi(){
        return movieService.getSimilarMovies(
                similar, BuildConfig.THE_MOVIE_DB_API_TOKEN,
                currentPage

        );
    }
}
