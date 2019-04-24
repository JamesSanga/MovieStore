package com.tz.sanga.moviestore;


import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class YoutubePlayer extends YouTubeBaseActivity {
    @BindView(R.id.youtube_video)
    YouTubePlayerView playerView;
    @BindView(R.id.trailer_name)
    TextView textView;
    @BindView(R.id.exitTrailer)
    ImageButton cancel;
    String VideoKey, trailerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_player);
        ButterKnife.bind(this);


        //getting extras
        VideoKey = getIntent().getExtras().getString("key");
        trailerName = getIntent().getExtras().getString("name");
        exitTrailerPage();
        video();
    }

    private void exitTrailerPage() {
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void video(){
        YouTubePlayer.OnInitializedListener init = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo(VideoKey);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };
        playerView.initialize(YoutubeConfig.getApiKey(), init);

        textView.setText(trailerName);

    }
}
