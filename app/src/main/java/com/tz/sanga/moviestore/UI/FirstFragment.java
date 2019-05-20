package com.tz.sanga.moviestore.UI;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView;
import com.tz.sanga.moviestore.Adapters.RelatedAdapter;
import com.tz.sanga.moviestore.Adapters.TrailerAdapter;
import com.tz.sanga.moviestore.Constants;
import com.tz.sanga.moviestore.Database.Local.FavoriteNote;
import com.tz.sanga.moviestore.Model.Movie;
import com.tz.sanga.moviestore.Model.Trailer;
import com.tz.sanga.moviestore.Presenters.First.FirstPresenter;
import com.tz.sanga.moviestore.Presenters.First.FirstView;
import com.tz.sanga.moviestore.R;
import com.tz.sanga.moviestore.ViewModel.FavoriteViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FirstFragment extends Fragment implements FirstView, RelatedAdapter.ReloadListener {
    @BindView(R.id.relatedMovies)
    MultiSnapRecyclerView multiSnapRecyclerView;
    @BindView(R.id.load_similar_movies)
    ProgressBar progressBar;
    @BindView(R.id.similar_movies_title) TextView textView1;
    @BindView(R.id.movie_details)TextView textOverView;
    @BindView(R.id.add_to_favorite) Button addToFavorite;
    @BindView(R.id.poster_image) ImageView imageView;
    @BindView(R.id.view_to_scroll) ScrollView scrollView;
    @BindView(R.id.movie_trailer)ImageView trailerImage;
    @BindView(R.id.movie_title) TextView textMove;
    @BindView(R.id.movie_date)TextView textDate;
    @BindView(R.id.recycler_view_trailer)RecyclerView recyclerView;

    private static final String TAG = "TAG";
    private FavoriteViewModel favoriteViewModel;
    private int moveId;
    private String title, path, overview, date;
    private RelatedAdapter adapter;
    LinearLayoutManager layoutManager;
    private ActionBar actionBar;
    FirstPresenter presenter;
    private FavoriteNote favoriteNote;

    public FirstFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        presenter = new FirstPresenter(this, moveId);
        ButterKnife.bind(this, view);
        presenter.getData();
        saveMovieDetails();
        initialize();
        setView();
        setToolBar();
        trailer();
        return view;
    }

    private void setToolBar() {
        actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.move_details);
    }

    @Override
    public void onResume() {
        super.onResume();
        actionBar.setTitle(R.string.move_details);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home ){
            getActivity().onBackPressed();
            return true;
        }else
                return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!= null){
            setData(getArguments().getInt("moveId"),
                    getArguments().getString("path") ,
                    getArguments().getString("overview"),
                    getArguments().getString("title"),
                    getArguments().getString("date"));

        }else{
            Log.d(TAG, "onCreate: no data available");
        }
    }

    private void setData(int moveId, String path, String overview, String title, String date){
        this.moveId = moveId;
        this.path= path;
        this.overview = overview;
        this.title = title;
        this.date = date;
        Log.d(TAG, "setData: " + path);
    }
    private void initialize(){
        favoriteNote = new FavoriteNote(title, path, overview);
        favoriteViewModel = ViewModelProviders.of(this).get(FavoriteViewModel.class);
        adapter = new RelatedAdapter(getContext(),this);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        multiSnapRecyclerView.setLayoutManager(layoutManager);
        multiSnapRecyclerView.setItemAnimator(new DefaultItemAnimator());
        multiSnapRecyclerView.setAdapter(adapter);
    }

    private void saveMovieDetails() {
        addToFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkFavoriteMovie(view);
            }
        });
    }

    private void setView(){
        textDate.setText(date);
        textMove.setText(title);
        textOverView.setText(overview);
        Glide.with(this)
                .load(Constants.getImageUrl() + path)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target,
                                               boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target,
                                                   boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .centerCrop()
                .into(imageView);
    }

    private void checkFavoriteMovie(final View view){
        if (Constants.repository.checkFavorite(122) == 1)
        {
            Snackbar.make(view, title + " exist to favorite", Snackbar.LENGTH_LONG)
                    .setAction("REMOVE", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {deleteFavorite(v);}
                    }).show();
        }
        else {
            addToSqlDB(view);
        }
//
//        Constants.repository.getPath(path).observe(this, new Observer<List<FavoriteNote>>() {
//            @Override
//            public void onChanged(@Nullable List<FavoriteNote> favoriteNotes) {
//                if (favoriteNotes.size() >= 1){
//                    Snackbar.make(view, title + " exist to favorite", Snackbar.LENGTH_LONG)
//                    .setAction("REMOVE", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {deleteFavorite(v);}
//                    }).show();
//                }else addToSqlDB(view);
//            }
//        });
    }

    public void addToSqlDB(final View view) {
       favoriteViewModel.insert(favoriteNote);
           Snackbar.make(view, title + " added to favorite", Snackbar.LENGTH_LONG)
                   .setAction("REMOVE", new View.OnClickListener() {
               @Override
               public void onClick(View v) {deleteFavorite(v);}
           }).show();
    }


    public void deleteFavorite(View view){
        favoriteViewModel.delete(favoriteNote);
        Snackbar.make(view, "Deleted successful", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showLoading() {progressBar.setVisibility(View.VISIBLE);}

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showResults(List<Movie> moveData) {
        if (moveData.size()<1){
            textView1.setVisibility(View.GONE);
            multiSnapRecyclerView.setVisibility(View.GONE);
        }
        textView1.setText(R.string.related_movies);
        adapter.addAll(moveData);
    }

    @Override
    public void onReload(int moveId, String path, String overview, String title, String date) {
        setData(moveId,path,overview,title, date);
        scrollView.fullScroll(View.FOCUS_BACKWARD);
        presenter.updateMoveId(moveId);
        presenter.getData();
        setView();
        presenter.processTrailer();
    }

    private void trailer(){
        List<Trailer> moveData;
        TrailerAdapter trailerAdapter;
        moveData = new ArrayList<>();
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        trailerAdapter = new TrailerAdapter(getContext(), moveData);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(trailerAdapter);
        presenter.processTrailer();
    }

    @Override
    public void trailerMoviesResults(List<Trailer> trailers) {
        recyclerView.setAdapter(new TrailerAdapter(getContext(), trailers));
        recyclerView.smoothScrollToPosition(0);
    }
    @Override
    public void onErrorLoading(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }
}

