package com.tz.sanga.moviestore.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tz.sanga.moviestore.Database.FavoriteDb;
import com.tz.sanga.moviestore.Model.Movie;
import com.tz.sanga.moviestore.R;

public class ScrollingActivity extends AppCompatActivity {
    private static final String BASE_URL_IMG = "https://image.tmdb.org/t/p/original";
    TextView textViewTitle, textViewOverView;
    ImageView imageView;
    private FavoriteDb favoriteDb;
    private Movie favorite;
    String originalTitle;
    private final AppCompatActivity activity = ScrollingActivity.this;

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

        originalTitle = getIntent().getExtras().getString("original_title");
        String averageVote = getIntent().getExtras().getString("average_vote");
        String overView = getIntent().getExtras().getString("overview");
        String thumbnail = getIntent().getExtras().getString("poster_path");

        //load image to image view
        Glide.with(this).load(BASE_URL_IMG + thumbnail).placeholder(R.drawable.loading).into(imageView);


        textViewTitle.setText(originalTitle);
        textViewOverView.setText(overView);

        saveMovieDetails();

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

}
