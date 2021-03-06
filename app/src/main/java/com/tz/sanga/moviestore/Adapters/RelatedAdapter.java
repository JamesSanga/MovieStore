package com.tz.sanga.moviestore.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.tz.sanga.moviestore.Constants;
import com.tz.sanga.moviestore.Model.Movie;
import com.tz.sanga.moviestore.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RelatedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Movie> movieResults;
    private Context context;
    private ReloadListener listener;

    public RelatedAdapter(Context context, ReloadListener listener) {
        this.context = context;
        this.listener = listener;
        movieResults = new ArrayList<>();
    }

    @Override
    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout. related_layout, parent, false);
        return new MovieVH(view);
    }

    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Movie result = movieResults.get(position);
                final MovieVH movieVH = (MovieVH) holder;
                movieVH.movie = getItem(position);
                movieVH.textViewTitle.setText(result.getTitle());
                //load images by glide library
                Glide.with(context).load(Constants.getImageUrl() + result.getPosterPath())
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable>
                                    target, boolean isFirstResource) {
                                movieVH.progressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target
                            <GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                //image id ready, hide progress bar
                                movieVH.progressBar.setVisibility(View.GONE);
                                return false; // return false for glide to handle every thing else
                            }
                        })
                        .diskCacheStrategy(DiskCacheStrategy.ALL) // cache all original and resizable images
                        .centerCrop()
                        .crossFade()
                        .into(movieVH.imageView);
       }

    @Override
    public int getItemCount() {
        return movieResults.size();
    }

    public void add(Movie r){
        movieResults.add(r);
        notifyItemInserted(movieResults.size()-1);
    }

    public void addAll(List<Movie> movieResults){
        for (Movie result : movieResults){
            add(result);
        }
    }

    public Movie getItem(int position){
        return movieResults.get(position);
    }

    protected class MovieVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.movie_poster)ImageView imageView;
        @BindView(R.id.movie_progress)ProgressBar progressBar;
        @BindView(R.id.movie_title)TextView textViewTitle;
        private Movie movie;

        public MovieVH(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            bundle.putString("overview", movie.getOverview());
            bundle.putString("path", movie.getPosterPath());
            bundle.putString("title", movie.getOriginalTile());
            bundle.putInt("moveId", movie.getId());
            bundle.putString("date", movie.getReleaseDate());
            if (movieResults.size() > 0){movieResults.clear();}
            listener.onReload(movie.getId(), movie.getPosterPath(), movie.getOverview(),
                    movie.getOriginalTile(), movie.getReleaseDate());
        }
    }

  public interface ReloadListener {
        void onReload(int moveId, String path, String overview, String title, String date);
  }

}