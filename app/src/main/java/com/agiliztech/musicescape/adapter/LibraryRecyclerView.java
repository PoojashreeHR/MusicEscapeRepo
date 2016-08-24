package com.agiliztech.musicescape.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.agiliztech.musicescape.R;
import com.agiliztech.musicescape.fasrscrollinginterface.FastScrollRecyclerViewInterface;
import com.agiliztech.musicescape.models.SongsModel;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Pooja on 18-08-2016.
 */
public class LibraryRecyclerView extends RecyclerView.Adapter<LibraryRecyclerView.MyViewHolder> implements FastScrollRecyclerViewInterface {
    List<SongsModel> songList;
    private HashMap<String, Integer> mMapIndex;

    @Override
    public HashMap<String, Integer> getMapIndex() {
        return this.mMapIndex;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout songlistLayout;
        public TextView mTextView;
        public TextView title, artist;
        public MyViewHolder(View view,TextView v)
        {
            super(view);
            mTextView = v;
            title = (TextView) view.findViewById(R.id.song_title);
            artist = (TextView) view.findViewById(R.id.song_artist);
            songlistLayout = (LinearLayout) view.findViewById(R.id.songListLayout);

        }
    }
    public LibraryRecyclerView(List<SongsModel> songList, HashMap<String, Integer> mapIndex) {
        this.songList = songList;
        mMapIndex = mapIndex;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_list_row, parent, false);
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.side_index_item, parent, false);
        return new MyViewHolder(itemView, (TextView) v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final int pos = position;

        SongsModel model = songList.get(position);
        //holder.mTextView.setText(model.getArtist());
        holder.title.setText(model.getTitle());
        holder.artist.setText(model.getArtist());
        holder.songlistLayout.setTag(pos);
    }
    @Override
    public int getItemCount() {
        return songList.size();
    }

}

