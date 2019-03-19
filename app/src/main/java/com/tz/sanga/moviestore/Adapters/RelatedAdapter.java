package com.tz.sanga.moviestore.Adapters;

import android.content.Context;
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
import com.tz.sanga.moviestore.Model.Movie;
import com.tz.sanga.moviestore.R;

import java.util.ArrayList;
import java.util.List;

public class RelatedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private static final String BASE_URL_IMG = "https://image.tmdb.org/t/p/original";

    private List<Movie> movieResults;
    private Context context;

    private boolean isLoadingAdded = false;

    public RelatedAdapter(Context context) {
        this.context = context;
        movieResults = new ArrayList<>();

    }

    public List<Movie> getMovieResults() {
        return movieResults;
    }

    public void setMovies(List<Movie> movieResults){this.movieResults = movieResults; }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType){
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater){
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout. related_layout, parent, false);
        viewHolder = new RelatedAdapter.MovieVH(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Movie result = movieResults.get(position);

        switch (getItemViewType(position)){
            case ITEM:
                // final MovieVH movieVH = (MovieVH) holder;
                final RelatedAdapter.MovieVH movieVH = (RelatedAdapter.MovieVH) holder;
                movieVH.textViewTitle.setText(result.getTitle());
                movieVH.textViewDate.setText(result.getReleaseDate());
                movieVH.textViewVoteAverage.setText(String.valueOf(result.getVoteAverage()));

                //load images by glide library

                Glide.with(context).load(BASE_URL_IMG + result.getPosterPath())
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                movieVH.progressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                //image id ready, hide progress bar
                                movieVH.progressBar.setVisibility(View.GONE);
                                return false; // return false for glide to handle every thing else
                            }
                        })
                        .diskCacheStrategy(DiskCacheStrategy.ALL) // cache all original and resizable images
                        .centerCrop()
                        .crossFade()
                        .into(movieVH.imageView);
                break;

            case LOADING:
                //do nothing
                break;
        }


    }

    @Override
    public int getItemCount() {
        return movieResults == null ? 0 : movieResults.size();
    }

    @Override
    public int getItemViewType(int position){
        return (position == movieResults.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
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

    protected class MovieVH extends RecyclerView.ViewHolder{
        private ImageView imageView;
        private ProgressBar progressBar;
        private TextView textViewTitle;
        private TextView textViewDate;
        private TextView textViewVoteAverage;


        public MovieVH(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.movie_poster);
            progressBar = itemView.findViewById(R.id.movie_progress);
            textViewTitle = itemView.findViewById(R.id.movie_title);
            textViewDate = itemView.findViewById(R.id.release_date);
            textViewVoteAverage = itemView.findViewById(R.id.vote_average);
        }

    }

}