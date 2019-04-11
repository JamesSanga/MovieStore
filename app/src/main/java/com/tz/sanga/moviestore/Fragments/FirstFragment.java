package com.tz.sanga.moviestore.Fragments;


import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView;
import com.tz.sanga.moviestore.Fragments.First.FavoritePresenter;
import com.tz.sanga.moviestore.Fragments.First.FavoriteView;
import com.tz.sanga.moviestore.Fragments.First.FirstPresenter;
import com.tz.sanga.moviestore.Fragments.First.FirstView;
import com.tz.sanga.moviestore.Model.API.Connector;
import com.tz.sanga.moviestore.Model.API.Service;
import com.tz.sanga.moviestore.Activities.MainActivity;
import com.tz.sanga.moviestore.Adapters.FavoriteAdapter;
import com.tz.sanga.moviestore.Adapters.RelatedAdapter;
import com.tz.sanga.moviestore.BuildConfig;
import com.tz.sanga.moviestore.Model.Favorite;
import com.tz.sanga.moviestore.Model.FavoriteDb;
import com.tz.sanga.moviestore.Model.MovieObjects;
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


public class FirstFragment extends Fragment implements FirstView, FavoriteView, RelatedAdapter.ReloadListener {

    private static final String BASE_URL_IMG = "https://image.tmdb.org/t/p/original";
    @BindView(R.id.relatedMovies) MultiSnapRecyclerView multiSnapRecyclerView;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.load_similar_movies) ProgressBar progressBar;
    @BindView(R.id.similar_movies_title) TextView textView1;
    @BindView(R.id.similar_movies_layout) RelativeLayout relativeLayout;
    @BindView(R.id.fab) FloatingActionButton floatingActionButton;
    @BindView(R.id.text_view)TextView textView;
    @BindView(R.id.favorite_movies)TextView textView2;
    @BindView(R.id.poster_image) ImageView imageView;
    @BindView(R.id.refreshDb) SwipeRefreshLayout swipeRefreshLayout;

    private static final String TAG = "TAG";
    private int moveId;
    private String title, path, overview;

    private static final int PAGE_START = 1;
    private int currentPage = PAGE_START;

    private ArrayList<MovieObjects> movieList = new ArrayList<>();

    private FavoriteDb favoriteDb;
    RelatedAdapter adapter;
    LinearLayoutManager layoutManager;
    FavoriteAdapter favoriteAdapter;
    private ActionBar actionBar;
    FirstPresenter presenter;
    FavoritePresenter favoritePresenter;



    public FirstFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        presenter = new FirstPresenter(this, moveId);
        favoritePresenter = new FavoritePresenter(this);
        ButterKnife.bind(this, view);
        loadSimilarMovies();
        saveMovieDetails();
        loadSqliteData();
        initialize();
        setToolBar();
        refreshData();
        return view;
    }

    private void refreshData() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadSqliteData();
            }
        });
    }

    private void setToolBar() {
        actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(title);

    }

    @Override
    public void onResume() {
        super.onResume();
        actionBar.setTitle(title);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!= null){
            setData(getArguments().getInt("moveId"),
                    getArguments().getString("path") ,
                    getArguments().getString("overview"),
                    getArguments().getString("title"));
        }else{
            Log.d(TAG, "onCreate: no data available");
        }
    }

    private void setData(int moveId, String path, String overview, String title){
        this.moveId = moveId;
        this.path= path;
        this.overview = overview;
        this.title = title;
    }
    private void initialize(){
        setView();
        //adapter = new MoviesAdapter(this);
        adapter = new RelatedAdapter(getContext(),this);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        multiSnapRecyclerView.setLayoutManager(layoutManager);

        multiSnapRecyclerView.setItemAnimator(new DefaultItemAnimator());
        multiSnapRecyclerView.setAdapter(adapter);


    }
    private void saveMovieDetails() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToSqlDB(view);
            }
        });
    }

    private void setView(){
        textView.setText(overview);
        Glide.with(this)
                .load(BASE_URL_IMG + path)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .centerCrop()
                .into(imageView);
    }

    public void addToSqlDB(final View view) {
        favoriteDb = new FavoriteDb(getContext());
        if (!favoriteDb.checkMovie(path)){
            boolean insertData = favoriteDb.addFavorite(moveId, title, overview, path);
            if (insertData){
                Snackbar.make(view, title + " added to favorite", Snackbar.LENGTH_LONG).setAction("REMOVE", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteFavorite(v);
                    }
                }).show();
            }else {
                Snackbar.make(view, title + " not added to favorite", Snackbar.LENGTH_LONG).show();
            }
        }else {
            Snackbar.make(view, title + " exist to favorite", Snackbar.LENGTH_LONG).setAction("REMOVE", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteFavorite(v);
                }
            }).show();
        }
    }

    public void deleteFavorite(View view){
        boolean delete = favoriteDb.deleteMovie(path);
        if (delete){Snackbar.make(view, "Deleted successful", Snackbar.LENGTH_LONG).show();}
        if (!delete){ Snackbar.make(view, "Not successful", Snackbar.LENGTH_LONG).show();}
    }

    public void loadSqliteData(){
       // favoritePresenter.loadSqlLiteData();
        favoriteDb = new FavoriteDb(getContext());
        Cursor data = favoriteDb.getMovies("select * from " + Favorite.FavoriteEntry.TABLE_NAME);

        if (data.getCount() < 1){
            textView2.setVisibility(View.GONE);
        }else {

            textView2.setVisibility(View.VISIBLE);
            textView2.setText("Favorite movies");
            if (data.moveToNext()) {
                do {
                    MovieObjects movieObjects = new MovieObjects();
                    movieObjects.setTitle(data.getString(2));
                    movieObjects.setPath(data.getString(4));
                    movieList.add(movieObjects);

                }while (data.moveToNext());
            }
        }

        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        favoriteAdapter = new FavoriteAdapter(getContext(), movieList);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(favoriteAdapter);
    }

    private void loadSimilarMovies() {
        presenter.getData();
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showResultsFavorite(List<MovieObjects> moveData) {
    }

    @Override
    public void showResults(List<Movie> moveData) {
        if (moveData.size()<1){
            textView1.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.GONE);
        }
        textView1.setText("Similar/Related movies");
        adapter.addAll(moveData);
    }

    @Override
    public void onErrorLoading(String message) {

    }

    @Override
    public void onReload(int moveId, String path, String overview, String title) {
        setData(moveId,path,overview,title);
        presenter.updateMoveId(moveId);
        actionBar.setTitle(title);
        loadSimilarMovies();
        setView();
    }
}

