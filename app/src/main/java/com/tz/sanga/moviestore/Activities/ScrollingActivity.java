package com.tz.sanga.moviestore.Activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ScrollingActivity extends AppCompatActivity {
    private static final String BASE_URL_IMG = "https://image.tmdb.org/t/p/original";
    @BindView(R.id.move_title_name)TextView textViewTitle;
    @BindView(R.id.details)TextView textViewOverView;
    @BindView(R.id.relatedMovies)MultiSnapRecyclerView multiSnapRecyclerView;
    @BindView(R.id.poster_image)ImageView imageView;
    @BindView(R.id.listData)ListView listView;
    private FavoriteDb favoriteDb;
    //String MovieId;
    String originalTitle, averageVote, overView, thumbnail;
    String similar;
    RelatedAdapter adapter;
    LinearLayoutManager layoutManager;
    private final AppCompatActivity activity = ScrollingActivity.this;
    private Service movieService;

    private static final int PAGE_START = 1;
    private int currentPage = PAGE_START;
    private static final String TAG = "TAG";

    ArrayList<String> list = new ArrayList<>();

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
       // similar = "458723";

        Log.d(TAG, "onCreate: "+similar);

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
                addToSqlDB();
            }
        });
    }

    public void addToSqlDB() {
        favoriteDb = new FavoriteDb(this);
        boolean insertData = favoriteDb.addFavorite(originalTitle, averageVote, overView, thumbnail);

        if (insertData == true){
            Toast.makeText(ScrollingActivity.this, "Successful", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(ScrollingActivity.this, "Not successful", Toast.LENGTH_LONG).show();
        }
    }

    public void loadSqliteData(){
        favoriteDb = new FavoriteDb(this);
        Cursor data = favoriteDb.getMovies();

        if (data.getCount() == 0){
            TextView textView = findViewById(R.id.favorite_movies);
            textView.setVisibility(View.GONE);
        }else {
            TextView textView = findViewById(R.id.favorite_movies);
            textView.setVisibility(View.VISIBLE);

            while (data.moveToNext()){
                list.add(data.getString(2));
                ListAdapter listAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, list);
                listView.setAdapter(listAdapter);

            }
        }
    }

    private void loadSimilarMovies(){
        callSimilarMoviesApi().enqueue(new Callback<MoviesResponse>(){
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
