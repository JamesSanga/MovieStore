package com.tz.sanga.moviestore.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tz.sanga.moviestore.R;

public class CustomAdapter extends BaseAdapter {
    private static final String BASE_URL_IMG = "https://image.tmdb.org/t/p/original";
    Context context;
    public static LayoutInflater inflater = null;

    public CustomAdapter(Context context) {
        this.context = context;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final holder holder = new holder();
        View rowview;
        rowview = inflater.inflate(R.layout.display_view, null);
        holder.imageView = rowview.findViewById(R.id.img_view);
        holder.textView = rowview.findViewById(R.id.view_text);
        return rowview;
    }

    public class holder{
        TextView textView;
        ImageView imageView;

    }
}
