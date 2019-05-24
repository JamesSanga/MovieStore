package com.tz.sanga.moviestore.UI;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.tz.sanga.moviestore.BuildConfig;
import com.tz.sanga.moviestore.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.constraint.Constraints.TAG;

public class YouTube extends Fragment {
    @BindView(R.id.youtube_video) YouTubePlayerView playerView;
    private String name;
    private ActionBar actionBar;

    public YouTube() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_youtube, container, false);
        ButterKnife.bind(this, view);
        setToolBar();
        video();
        return view;
    }

    private void setToolBar() {
        if (getActivity() != null)
        actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        if (actionBar !=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(name);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            if (getActivity() != null)
            getActivity().onBackPressed();
            return true;
        }else
            return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!= null){
              name = getArguments().getString("trailerName");
             // key = getArguments().getString("trailer");

        }else{
            Log.d(TAG, "onCreate: no data available");
        }
    }

    private void video(){
        YouTubePlayer.OnInitializedListener init = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo("dt5g5_1cKVk");
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };
        playerView.initialize(BuildConfig.THE_YOUTUBE_API_TOKEN, init);

    }
}
