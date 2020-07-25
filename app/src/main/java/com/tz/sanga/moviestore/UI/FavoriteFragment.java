package com.tz.sanga.moviestore.UI;


import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
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
import com.tz.sanga.moviestore.Adapters.FavoriteAdapter;
import com.tz.sanga.moviestore.Constants;
import com.tz.sanga.moviestore.Database.Local.Favorite;
import com.tz.sanga.moviestore.R;
import com.tz.sanga.moviestore.ViewModel.FavoriteViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoriteFragment extends Fragment implements FavoriteAdapter.dataListener,
        FavoriteAdapter.favoriteOnLongClickListener {

    @BindView(R.id.note_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.empty_favorite) TextView emptyTextView;
    private FavoriteViewModel favoriteViewModel;
    private AlertDialog.Builder mDialog;
    private FavoriteAdapter adapter;
    private  ActionBar actionBar;

    public FavoriteFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        ButterKnife.bind(this,view);
        setToolBar();
        initialize();
        setAdapter();
        createViewModel();
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            if (getActivity() != null)
            getActivity().onBackPressed();
            return true;
        }else if(id == R.id.action_delete_all){
            warnUser();
            return true;
        }else
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.mvvm, menu);
    }

    private void setToolBar() {
        if (getActivity() != null)
        actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        if (actionBar !=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.favorite_movies);
        }
    }

    private void initialize(){
        favoriteViewModel = ViewModelProviders.of(this).get(FavoriteViewModel.class);
        adapter = new FavoriteAdapter(getContext(), this, this);
        mDialog = new AlertDialog.Builder(getContext());
    }

    private void setAdapter(){
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(adapter);
    }

    private void createViewModel(){
        favoriteViewModel.getAllFavorites().observe(this, new Observer<List<Favorite>>()
        {
            @Override
            public void onChanged(@Nullable List<Favorite> favorites) {
               adapter.setFavoriteList(favorites);
               if (favorites != null)
               if (favorites.size() > 0){
                   emptyTextView.setVisibility(View.GONE);
               }else emptyTextView.setVisibility(View.VISIBLE);
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
                    public boolean onException(Exception e, String model, Target<GlideDrawable>
                            target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target
                            <GlideDrawable> target, boolean isFromMemoryCache,
                                                   boolean isFirstResource) {
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

    private void warnUser(final Favorite favorite, String title){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setMessage( title +" will be deleted");
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                favoriteViewModel.delete(favorite);
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

    private void warnUser(){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setMessage("All favorite movies will be cleared");
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                favoriteViewModel.deleteAll();
                Toast.makeText(getContext(),"Favorite movies cleared ", Toast.LENGTH_LONG)
                        .show();
                createViewModel();
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
    public void onLongClickFavorite(Favorite favorite, String title) {
        warnUser(favorite, title);
    }
}
