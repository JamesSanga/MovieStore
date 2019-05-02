package com.tz.sanga.moviestore.Fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.tz.sanga.moviestore.Activities.MainActivity;
import com.tz.sanga.moviestore.Adapters.FavoriteAdapter;
import com.tz.sanga.moviestore.Model.Favorite;
import com.tz.sanga.moviestore.Model.FavoriteDb;
import com.tz.sanga.moviestore.Model.MovieObjects;
import com.tz.sanga.moviestore.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FavoriteFragment extends Fragment implements FavoriteAdapter.dataListener, FavoriteAdapter.favoriteOnLongClickListener{
    private static final String BASE_URL_IMG = "https://image.tmdb.org/t/p/original";

    @BindView(R.id.refresh_favorite_movies) SwipeRefreshLayout refreshLayout;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.empty_view)TextView textView;

    private ArrayList<MovieObjects> movieList = new ArrayList<>();
    GridLayoutManager layoutManager;
    FavoriteAdapter favoriteAdapter;
    private FavoriteDb favoriteDb;

    AlertDialog.Builder mDialog;

    public FavoriteFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        ButterKnife.bind(this, view);
        setToolBar();
        initialize();
        loadSqliteData();
        refreshFavorite();
        return view;
    }

    private void initialize() {
        mDialog = new AlertDialog.Builder(getContext());
        favoriteAdapter = new FavoriteAdapter(getContext(), this,this, movieList);
        layoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(favoriteAdapter);
    }

    private void setToolBar() {
        ActionBar actionBar;
        actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Favorite Movies");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.delete_favorite, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case R.id.action_delete:
                alertMessage();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void alertMessage() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setMessage("Are you sure you want to clear Favorite?");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clearFavorite();
            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialogBuilder.setCancelable(true);
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void refreshFavorite(){
        refreshLayout.setColorScheme(R.color.colorAccent, R.color.lightGray, R.color.black);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                movieList.clear();
                loadSqliteData();
            }
        });
    }

    private void clearFavorite(){
        FavoriteDb favoriteDb = new FavoriteDb(getContext());
        SQLiteDatabase db = favoriteDb.getWritableDatabase();
        int deleted = db.delete(Favorite.FavoriteEntry.TABLE_NAME, "1", null);
        if (deleted > 0){
            Toast.makeText(getContext(), "Favorite Cleared", Toast.LENGTH_LONG).show();
            movieList.clear();
            favoriteAdapter.notifyDataSetChanged();
        }else{Toast.makeText(getContext(), "You don't have to clear empty list", Toast.LENGTH_LONG).show();}
    }

    public void loadSqliteData(){
        FavoriteDb favoriteDb = new FavoriteDb(getContext());
        Cursor data = favoriteDb.getMovies("select * from " + Favorite.FavoriteEntry.TABLE_NAME);
        if (data.getCount() < 1){
            textView.setVisibility(View.VISIBLE);
            favoriteAdapter.notifyDataSetChanged();
        }else {
                textView.setVisibility(View.GONE);
            if (data.moveToNext()) {
                do {
                    MovieObjects movieObjects = new MovieObjects();
                    movieObjects.setTitle(data.getString(2));
                    movieObjects.setPath(data.getString(4));
                    movieList.add(movieObjects);

                }while (data.moveToNext());
                favoriteAdapter.notifyDataSetChanged();
            }

        }
        refreshLayout.setRefreshing(false);
    }
    private void zoomFavorite(String overView, String path){
        mDialog = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.pop_up, null);
        PhotoView imageView = mView.findViewById(R.id.imageView);
        final ProgressBar progressBar = mView.findViewById(R.id.full_image_loading);
        Glide.with(getContext())
                .load(BASE_URL_IMG+path)
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
        mDialog.setView(mView);
        mDialog.create();
        mDialog.show();
    }

    @Override
    public void onClickFavorite(String overView, String path) {
        zoomFavorite(overView, path);
    }

    @Override
    public void onLongClickFavorite(String path, String title) {
        warnUser(path, title);
    }

    private void warnUser(final String path, final String title){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setMessage(title + " will be deleted");
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteMovie(path, title);
            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialogBuilder.setCancelable(true);
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    private void deleteMovie(String path, String title){
        favoriteDb = new FavoriteDb(getContext());
        boolean delete = favoriteDb.deleteMovie(path);
        if (delete){
            movieList.clear();
            loadSqliteData();
            Toast.makeText(getContext(),title + " Deleted successful", Snackbar.LENGTH_LONG).show();}
        if (!delete){ Toast.makeText(getContext(), "Not successful", Snackbar.LENGTH_LONG).show();}
    }
}
