package com.agiliztech.musicescape.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.agiliztech.musicescape.R;
import com.agiliztech.musicescape.fasrscrollinginterface.FastScrollRecyclerViewInterface;
import com.agiliztech.musicescape.journey.SongMoodCategory;
import com.agiliztech.musicescape.models.Song;
import com.agiliztech.musicescape.utils.SongsManager;
import com.chauthai.swipereveallayout.SwipeRevealLayout;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Pooja on 18-08-2016.
 */
public class LibraryRecyclerView extends RecyclerView.Adapter<LibraryRecyclerView.MyViewHolder> implements FastScrollRecyclerViewInterface {
    List<Song> songList;
    private HashMap<String, Integer> mMapIndex;
    String mood;
    Context context;
    ILibraryClickListener iLibraryClickListener;

    public interface ILibraryClickListener {
        void songRetagInLibrary(int position);
    }

    @Override
    public HashMap<String, Integer> getMapIndex() {
        return this.mMapIndex;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout songlistLayout;
        public TextView mTextView;
        public TextView title, artist;
        public ImageView rv_swap_library;
        public SwipeRevealLayout swipe_layout_library;
        private ImageView mood_image;

        public MyViewHolder(View view, TextView v) {
            super(view);
            mTextView = v;
            title = (TextView) view.findViewById(R.id.song_title);
            artist = (TextView) view.findViewById(R.id.song_artist);
            songlistLayout = (LinearLayout) view.findViewById(R.id.songListLayout);
            rv_swap_library = (ImageView) view.findViewById(R.id.rv_swap_library);
            swipe_layout_library = (SwipeRevealLayout) view.findViewById(R.id.swipe_layout_library);
            mood_image = (ImageView) view.findViewById(R.id.mood_image);

        }
    }

    public LibraryRecyclerView(List<Song> songList, HashMap<String, Integer> mapIndex,
                               String mood, ILibraryClickListener iLibraryClickListener, Context context) {
        this.songList = songList;
        mMapIndex = mapIndex;
        this.mood = mood;
        this.context = context;
        this.iLibraryClickListener = iLibraryClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_list_row, parent, false);
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.side_index_item, parent, false);
        return new MyViewHolder(itemView, (TextView) v);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final int pos = position;

        Song model = songList.get(position);
        //holder.mTextView.setText(model.getArtist());
        holder.title.setText(model.getSongName());
        if (mood.contains("depressed")) {
            holder.title.setTextColor(SongsManager.colorForMood(SongMoodCategory.scDepressed));
            holder.mood_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_depressed_library_inactive));
        } else if (mood.contains("happy")) {
            holder.title.setTextColor(SongsManager.colorForMood(SongMoodCategory.scHappy));
            holder.mood_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_happy_library_inactive));
        } else if (mood.contains("chilled")) {
            holder.title.setTextColor(SongsManager.colorForMood(SongMoodCategory.scChilled));
            holder.mood_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_chilled_library_inactive));
        } else if (mood.contains("peaceful")) {
            holder.title.setTextColor(SongsManager.colorForMood(SongMoodCategory.scPeaceful));
        } else if (mood.contains("bored")) {
            holder.title.setTextColor(SongsManager.colorForMood(SongMoodCategory.scBored));
            holder.mood_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bored_library_inactive));
        } else if (mood.contains("stressed")) {
            holder.title.setTextColor(SongsManager.colorForMood(SongMoodCategory.scStressed));
            holder.mood_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_stressed_library_inactive));
        } else if (mood.contains("aggressive")) {
            holder.title.setTextColor(SongsManager.colorForMood(SongMoodCategory.scAggressive));
            holder.mood_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_aggressive_library_inactive));
        } else if (mood.contains("excited")) {
            holder.title.setTextColor(SongsManager.colorForMood(SongMoodCategory.scExcited));
            holder.mood_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_excited_library_inactive));
        } else if (mood.contains("nomood")) {
            holder.title.setTextColor(SongsManager.colorForMood(SongMoodCategory.scMoodNotFound));
        } else if (mood.contains("allmood") || TextUtils.isEmpty(mood)) {
            SongMoodCategory mood = model.getMood();
            Log.e("MOOD RECYCLER VIEW", " MOOD : " + mood);
            if (mood == null) {
                mood = SongMoodCategory.scAllSongs;
                holder.mood_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_notfound_library_inactive));
            } else if (mood == SongMoodCategory.scDepressed) {
                holder.mood_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_depressed_library_inactive));
            } else if (mood == SongMoodCategory.scExcited) {
                holder.mood_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_depressed_library_inactive));
            } else if (mood == SongMoodCategory.scHappy) {
                holder.mood_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_depressed_library_inactive));
            } else if (mood == SongMoodCategory.scChilled) {
                holder.mood_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_depressed_library_inactive));
            } else if (mood == SongMoodCategory.scPeaceful) {
                holder.mood_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_depressed_library_inactive));
            } else if (mood == SongMoodCategory.scBored) {
                holder.mood_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_depressed_library_inactive));
            } else if (mood == SongMoodCategory.scStressed) {
                holder.mood_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_depressed_library_inactive));
            } else if (mood == SongMoodCategory.scAggressive) {
                holder.mood_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_depressed_library_inactive));
            }
            holder.title.setTextColor(SongsManager.colorForMood(mood));
        }
        holder.artist.setText(model.getArtist().getArtistName());
        holder.songlistLayout.setTag(pos);

        holder.rv_swap_library.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iLibraryClickListener.songRetagInLibrary(position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

}

