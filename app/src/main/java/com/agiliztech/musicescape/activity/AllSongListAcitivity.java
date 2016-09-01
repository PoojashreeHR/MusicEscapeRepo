package com.agiliztech.musicescape.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.agiliztech.musicescape.R;
import com.agiliztech.musicescape.adapter.RecyclerViewAdapter;
import com.agiliztech.musicescape.models.Song;
import com.agiliztech.musicescape.models.SongsModel;

import java.util.List;

public class AllSongListAcitivity extends MoodMappingActivity implements RecyclerViewAdapter.IClickListener {

    RecyclerView mRecyclerView;
    RecyclerViewAdapter mAdapter;
    List<Song> listOfSongs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_song_list_acitivity);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        /*Intent intent = getIntent();
        listOfSongs = intent.getExtras().getParcelableArrayList("songList");*/

        //mAdapter = new RecyclerViewAdapter(listOfSongs, this,AllSongListAcitivity.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*mAdapter = new RecyclerViewAdapter(listOfSongs,this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);*/
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
        getSharedPreferences("MyPreference", MODE_PRIVATE).edit()
                .putBoolean("playlist_first_run", false).commit();
    }

    @Override
    public void playSelectedSong(int position, View v) {
       /* musicSrv.setSong(Integer.parseInt(v.getTag().toString()));
        musicSrv.playSong();
        isSongPlaying = true;
        *//*tv_songname.setText(listOfSongs.get(position).getTitle());
        tv_song_detail.setText(listOfSongs.get(position).getArtist());*//*

        if (playbackPaused) {
            //setController();
            playbackPaused = false;
        }*/
    }
}
