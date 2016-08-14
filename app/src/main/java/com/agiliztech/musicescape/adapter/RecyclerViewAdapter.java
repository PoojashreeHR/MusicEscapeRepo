package com.agiliztech.musicescape.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.agiliztech.musicescape.R;
import com.agiliztech.musicescape.models.SongsModel;

import java.util.List;

/**
 * Created by Asif on 14-08-2016.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    public interface IClickListener{
        public void playSelectedSong(int position);
    }


    IClickListener iClickListener;
    List<SongsModel> listOfSongs;

    public RecyclerViewAdapter(List<SongsModel> listOfSongs,IClickListener iClickListener) {
        this.listOfSongs = listOfSongs;
        this.iClickListener = iClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_items,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return listOfSongs.size();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final int pos = position;
        SongsModel model = listOfSongs.get(position);
        holder.rv_song_name.setText(model.getTitle());

        holder.rv_song_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iClickListener.playSelectedSong(pos);
            }
        });
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView rv_song_name;
        public MyViewHolder(View itemView) {
            super(itemView);
            rv_song_name = (TextView) itemView.findViewById(R.id.rv_song_name);

        }
    }
}
