package com.tz.sanga.moviestore.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.tz.sanga.moviestore.Database.MovieObjects;
import com.tz.sanga.moviestore.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String BASE_URL_IMG = "https://image.tmdb.org/t/p/original";
    private Context context;
    List<MovieObjects>movieObjects= Collections.emptyList();

    public FavoriteAdapter(Context context, List<MovieObjects> movieObjects) {
        this.context = context;
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
        MyHolder myHolder = (MyHolder)viewHolder;
        myHolder.textView.setText(movieObjects.get(i).getTitle());

        Glide.with(context)
                .load(BASE_URL_IMG+movieObjects.get(i).getPath())
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
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
        private TextView textView;
        private ImageView imageView;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.view_text);
            imageView = itemView.findViewById(R.id.img_view);
        }
    }
}


