package com.tz.sanga.moviestore.Fragments;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import com.tz.sanga.moviestore.Activities.MainActivity;
import com.tz.sanga.moviestore.Adapters.FavoriteAdapter;
import com.tz.sanga.moviestore.Model.Favorite;
import com.tz.sanga.moviestore.Model.FavoriteDb;
import com.tz.sanga.moviestore.Model.MovieObjects;
import com.tz.sanga.moviestore.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FavoriteFragment extends Fragment {

    @BindView(R.id.refresh_favorite_movies) SwipeRefreshLayout refreshLayout;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.empty_view)TextView textView;

    private ArrayList<MovieObjects> movieList = new ArrayList<>();
    GridLayoutManager layoutManager;
    FavoriteAdapter favoriteAdapter;

    public FavoriteFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        ButterKnife.bind(this, view);
        setToolBar();
        loadSqliteData();
        refreshFavorite();
        return view;
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
                clearFavorite();
            default:
                return super.onOptionsItemSelected(item);
        }
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
        }loadSqliteData();
    }

    public void loadSqliteData(){
        FavoriteDb favoriteDb;
        favoriteDb = new FavoriteDb(getContext());
        Cursor data = favoriteDb.getMovies("select * from " + Favorite.FavoriteEntry.TABLE_NAME);

        if (data.getCount() < 1){
            textView.setVisibility(View.VISIBLE);
            movieList.clear();
            favoriteAdapter = new FavoriteAdapter(getContext(), movieList);
        }else {
                textView.setVisibility(View.GONE);
            if (data.moveToNext()) {
                do {
                    MovieObjects movieObjects = new MovieObjects();
                    movieObjects.setTitle(data.getString(2));
                    movieObjects.setPath(data.getString(4));
                    movieList.add(movieObjects);

                }while (data.moveToNext());
            }
        }
        layoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(layoutManager);
        favoriteAdapter = new FavoriteAdapter(getContext(), movieList);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(favoriteAdapter);
        refreshLayout.setRefreshing(false);
    }
}
