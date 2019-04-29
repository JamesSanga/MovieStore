package com.tz.sanga.moviestore.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.tz.sanga.moviestore.Model.MovieObjects;
import com.tz.sanga.moviestore.R;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FavoriteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final String BASE_URL_IMG = "https://image.tmdb.org/t/p/original";
    private Context context;
    private static final String TAG = "TAG";
    List<MovieObjects>movieObjects= Collections.emptyList();
    private dataListener listener;
    private favoriteOnLongClickListener favoriteListener;

    public FavoriteAdapter(Context context, dataListener listener, favoriteOnLongClickListener favoriteListener, List<MovieObjects> movieObjects) {
        this.context = context;
        this.listener = listener;
        this.favoriteListener = favoriteListener;
        this.movieObjects = movieObjects;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.display_view, viewGroup, false);
        MyHolder myHolder = new MyHolder(view);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        final MyHolder myHolder = (MyHolder)viewHolder;
        myHolder.object = movieObjects.get(i);
        myHolder.textView.setText(movieObjects.get(i).getTitle());

        Glide.with(context)
                .load(BASE_URL_IMG+movieObjects.get(i).getPath())
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        myHolder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        myHolder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .centerCrop()
                .into(myHolder.imageView);

    }

    @Override
    public int getItemCount() {
        return movieObjects.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.view_text)TextView textView;
        @BindView(R.id.img_view)ImageView imageView;
        @BindView(R.id.loading_favorite_poster)ProgressBar progressBar;
        private MovieObjects object;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: " + object.getPath());
//                    if (listener != null)
                        listener.onClickFavorite(object.getOverview(), object.getPath());
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    favoriteListener.onLongClickFavorite(object.getPath(), object.getTitle());
                    return false;
                }
            });
        }
    }

    public interface dataListener {
        void onClickFavorite(String overView, String path);
    }
   public interface favoriteOnLongClickListener{
        void onLongClickFavorite(String path, String title);
   }
}


