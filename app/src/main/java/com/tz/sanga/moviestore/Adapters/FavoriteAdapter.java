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
import com.tz.sanga.moviestore.Constants;
import com.tz.sanga.moviestore.Database.Local.Favorite;
import com.tz.sanga.moviestore.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoriteAdapter extends RecyclerView.Adapter <RecyclerView.ViewHolder>{
    private static final String TAG = "TAG";
    private Context context;
    private List<Favorite> movieObjects= new ArrayList<>();
    private dataListener listener;
    private favoriteOnLongClickListener favoriteListener;
    private MyHolder myHolder;

    public FavoriteAdapter(Context context, dataListener listener, favoriteOnLongClickListener favoriteListener) {
        this.context = context;
        this.listener = listener;
        this.favoriteListener = favoriteListener;
    }

    public FavoriteAdapter(MyHolder myHolder) {
        this.myHolder = myHolder;
    }

    @Override
    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.display_view, viewGroup, false);
        myHolder = new MyHolder(view);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        final MyHolder myHolder = (MyHolder)viewHolder;
        myHolder.favorite = movieObjects.get(i);
        myHolder.textView.setText(myHolder.favorite.getTitle());

        Glide.with(context)
                .load(Constants.getImageUrl() +myHolder.favorite.getPath())
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


    public void setFavoriteList (List<Favorite>movieObjects) {
        this.movieObjects = movieObjects;
        notifyDataSetChanged();
    }

    public interface dataListener {
        void onClickFavorite(String path);
    }

    public interface favoriteOnLongClickListener{
        void onLongClickFavorite(Favorite favorite, String title);
    }

    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        @BindView(R.id.view_text) TextView textView;
        @BindView(R.id.img_view) ImageView imageView;
        @BindView(R.id.loading_favorite_poster) ProgressBar progressBar;
        private Favorite favorite;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClickFavorite(favorite.getPath());
            Log.d(TAG, "onClick: " + favorite.getId());
        }
        @Override
        public boolean onLongClick(View v) {
            int position = getAdapterPosition();
            favoriteListener.onLongClickFavorite(movieObjects.get(position), favorite.getTitle());
            return false;
        }
    }
}