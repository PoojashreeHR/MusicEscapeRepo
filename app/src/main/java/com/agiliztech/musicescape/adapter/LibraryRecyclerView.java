package com.agiliztech.musicescape.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.agiliztech.musicescape.R;
import com.agiliztech.musicescape.fasrscrollinginterface.FastScrollRecyclerViewInterface;
import com.agiliztech.musicescape.journey.SongMoodCategory;
import com.agiliztech.musicescape.models.SongsModel;
import com.agiliztech.musicescape.utils.SongsManager;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Pooja on 18-08-2016.
 */
public class LibraryRecyclerView extends RecyclerView.Adapter<LibraryRecyclerView.MyViewHolder> implements FastScrollRecyclerViewInterface {
    List<SongsModel> songList;
    private HashMap<String, Integer> mMapIndex;
    String mood;

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
    public LibraryRecyclerView(List<SongsModel> songList, HashMap<String, Integer> mapIndex,String mood) {
        this.songList = songList;
        mMapIndex = mapIndex;
        this.mood = mood;
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
        if(mood.contains("depressed")) {
            holder.title.setTextColor(SongsManager.colorForMood(SongMoodCategory.scDepressed));
        }else if(mood.contains("happy")) {
            holder.title.setTextColor(SongsManager.colorForMood(SongMoodCategory.scHappy));
        }else if(mood.contains("chilled")) {
            holder.title.setTextColor(SongsManager.colorForMood(SongMoodCategory.scChilled));
        }else if(mood.contains("peaceful")) {
            holder.title.setTextColor(SongsManager.colorForMood(SongMoodCategory.scPeaceful));
        }else if(mood.contains("bored")) {
            holder.title.setTextColor(SongsManager.colorForMood(SongMoodCategory.scBored));
        }else if(mood.contains("stressed")) {
            holder.title.setTextColor(SongsManager.colorForMood(SongMoodCategory.scStressed));
        }else if(mood.contains("aggressive")) {
            holder.title.setTextColor(SongsManager.colorForMood(SongMoodCategory.scAggressive));
        }else if(mood.contains("excited")) {
            holder.title.setTextColor(SongsManager.colorForMood(SongMoodCategory.scExcited));
        }else if(mood.contains("nomood")) {
            holder.title.setTextColor(SongsManager.colorForMood(SongMoodCategory.scMoodNotFound));
        }else if(mood.contains("allmood")) {
            if(model.getSongMood().contains("depressed")) {
                holder.title.setTextColor(SongsManager.colorForMood(SongMoodCategory.scDepressed));
            }else if((model.getSongMood().contains("happy"))) {
                holder.title.setTextColor(SongsManager.colorForMood(SongMoodCategory.scHappy));
            }else if((model.getSongMood().contains("chilled"))) {
                holder.title.setTextColor(SongsManager.colorForMood(SongMoodCategory.scChilled));
            }else if((model.getSongMood().contains("peaceful"))) {
                holder.title.setTextColor(SongsManager.colorForMood(SongMoodCategory.scPeaceful));
            }else if((model.getSongMood().contains("bored"))) {
                holder.title.setTextColor(SongsManager.colorForMood(SongMoodCategory.scBored));
            }else if((model.getSongMood().contains("stressed"))) {
                holder.title.setTextColor(SongsManager.colorForMood(SongMoodCategory.scStressed));
            }else if((model.getSongMood().contains("aggressive"))) {
                holder.title.setTextColor(SongsManager.colorForMood(SongMoodCategory.scAggressive));
            }else if((model.getSongMood().contains("excited"))) {
                holder.title.setTextColor(SongsManager.colorForMood(SongMoodCategory.scExcited));
            }
        }
        holder.artist.setText(model.getArtist());
        holder.songlistLayout.setTag(pos);

    }
    @Override
    public int getItemCount() {
        return songList.size();
    }

}

