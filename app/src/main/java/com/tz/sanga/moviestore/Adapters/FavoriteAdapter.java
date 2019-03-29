package com.tz.sanga.moviestore.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tz.sanga.moviestore.Database.MovieObjects;
import com.tz.sanga.moviestore.R;

import java.util.ArrayList;
import java.util.List;

public class FavoriteAdapter extends ArrayAdapter {

    List list= new ArrayList();
    public FavoriteAdapter(Context context, int resource){
        super(context,resource);
    }
    static class LayoutHandler{
        TextView textView;
        ImageView imageView;
        String path;
    }
    public void add(Object object){
        super.add(object);
        list.add(object);

    }
    public int getCount(){
        return list.size();
    }
    public Object getTtem(int position){
        return list.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        LayoutHandler layoutHandler;
        if(row == null)
        {
            LayoutInflater layoutInflater =(LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row=layoutInflater.inflate(R.layout.display_view,parent,false);
            layoutHandler=new LayoutHandler();
            layoutHandler.textView=row.findViewById(R.id.view_text);
            layoutHandler.imageView = row.findViewById(R.id.img_view);
            row.setTag(layoutHandler);
        }

        else{
            layoutHandler=(LayoutHandler)row.getTag();
        }
        MovieObjects movieObjects=(MovieObjects) this.getTtem(position);
        layoutHandler.textView.setText(movieObjects.getTitle());
        return row;
    }
}


