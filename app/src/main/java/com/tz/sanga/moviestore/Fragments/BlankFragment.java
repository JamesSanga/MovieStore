package com.tz.sanga.moviestore.Fragments;


import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.tz.sanga.moviestore.Activities.MainActivity;
import com.tz.sanga.moviestore.Adapters.Adapter;
import com.tz.sanga.moviestore.Constants;
import com.tz.sanga.moviestore.Model.FavoriteNote;
import com.tz.sanga.moviestore.Repository.FavoriteRepository;
import com.tz.sanga.moviestore.ViewModel.FavoriteViewModel;
import com.tz.sanga.moviestore.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BlankFragment extends Fragment implements Adapter.dataListener, Adapter.favoriteOnLongClickListener {

    @BindView(R.id.note_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.empty_favorite) TextView emptyTextView;
    private FavoriteViewModel favoriteViewModel;
    private AlertDialog.Builder mDialog;
    private FavoriteNote favoriteNote;
    private Adapter adapter;
    private String path = "/or06FN3Dka5tukK1e9sl16pB3iy.jpg";

    public BlankFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_blank, container, false);
        ButterKnife.bind(this,view);
        setToolBar();
        initialize();
        setAdapter();
        createViewModel();
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            getActivity().onBackPressed();
            return true;
        }
        else if (item.getItemId() == R.id.action_add){
            checkFavoriteMovie();
            return true;
        }else if(item.getItemId() == R.id.action_delete_all){
            favoriteViewModel.deleteAll();
            Toast.makeText(getContext(), "Favorite cleared ", Toast.LENGTH_LONG).show();
            createViewModel();
            return true;
        }else
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.mvvm, menu);
    }

    private void setToolBar() {
        ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.mvvm);
    }

    private void initialize(){
        favoriteViewModel = ViewModelProviders.of(this).get(FavoriteViewModel.class);
        adapter = new Adapter(getContext(), this, this);
        mDialog = new AlertDialog.Builder(getContext());
        favoriteNote = new FavoriteNote("Avengers", "/or06FN3Dka5tukK1e9sl16pB3iy.jpg", "Movie");
    }

    private void setAdapter(){
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(adapter);
    }

    private void createViewModel(){
        favoriteViewModel.getAllFavorites().observe(this, new Observer<List<FavoriteNote>>() {
            @Override
            public void onChanged(@Nullable List<FavoriteNote> favoriteNotes) {
               adapter.setFavoriteList(favoriteNotes);
               if (favoriteNotes.size() >= 1){
                   emptyTextView.setVisibility(View.GONE);
               }else emptyTextView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void checkFavoriteMovie(){
        favoriteViewModel.checkFavorite().observe(this, new Observer<List<FavoriteNote>>() {
            @Override
            public void onChanged(@Nullable List<FavoriteNote> favoriteNotes) {
                if (favoriteNotes.size() >= 1){
                    Toast.makeText(getContext(), "Already in favorite", Toast.LENGTH_LONG).show();
                }else insertFavoriteMovie();
            }
        });
    }

    private void zoom(String path) {
        View mView = getLayoutInflater().inflate(R.layout.add_note, null);
        PhotoView imageView = mView.findViewById(R.id.zoomImageView);
        final ProgressBar progressBar = mView.findViewById(R.id.zoom_image_loading);

        Glide.with(getContext())
                .load(Constants.getImageUrl() + path)
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

    private void insertFavoriteMovie() {
        favoriteViewModel.insert(favoriteNote);
        Toast.makeText(getContext(), "Favorite added", Toast.LENGTH_LONG).show();

    }

    private void warnUser(final FavoriteNote favoriteNote, String title){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setMessage( title +" will be deleted");
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                favoriteViewModel.delete(favoriteNote);
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

    @Override
    public void onClickFavorite(String path) {
        zoom(path);
    }

    @Override
    public void onLongClickFavorite(FavoriteNote favoriteNote, String title) {
        warnUser(favoriteNote, title);
    }
}
