package com.tz.sanga.moviestore.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tz.sanga.moviestore.Model.Trailer;
import com.tz.sanga.moviestore.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {

    private Context context;
    private List<Trailer> moveData;

    public TrailerAdapter(Context context, List<Trailer> moveData) {
        this.context = context;
        this.moveData = moveData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.trailer_view, viewGroup, false);
       return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TrailerAdapter.ViewHolder viewHolder, int i) {
        viewHolder.textView.setText(moveData.get(i).getName());
    }

    @Override
    public int getItemCount() {
        return moveData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name_trailer) TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        String videoId = moveData.get(position).getKey();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:"+videoId));
                        intent.putExtra("VIDEO_ID", videoId);
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
}
