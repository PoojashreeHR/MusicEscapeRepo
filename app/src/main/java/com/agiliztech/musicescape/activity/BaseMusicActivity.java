package com.agiliztech.musicescape.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;

import com.agiliztech.musicescape.R;
import com.agiliztech.musicescape.models.SongsModel;
import com.agiliztech.musicescape.musicservices.MusicService;
import com.agiliztech.musicescape.utils.SongsManager;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class BaseMusicActivity extends AppCompatActivity implements MediaController.MediaPlayerControl,
        View.OnClickListener {

    protected static MusicService musicSrv;
    protected boolean musicBound = false;
    protected ImageButton ibPlayPause;
    SongsManager songsManager = new SongsManager(this);
    protected ArrayList<SongsModel> songList;
    protected Intent playIntent;
    public boolean paused = false, playbackPaused = false;

    protected ImageButton btn_pause;
    protected ImageButton loop_not_selected, loop_selected;
    protected TextView tv_songname;
    protected TextView tv_song_detail;
    protected SeekBar play_music_seek_bar;

    protected boolean isPlaying = false;
    public static boolean isSongPlaying = false;
    protected ServiceConnection musicConnection;

    public ArrayList<SongsModel> getSongsFromCurPlaylist()
    {
        return songsManager.getSongList();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void initViews() {
        btn_pause = (ImageButton) findViewById(R.id.btn_pause);
        btn_pause.setOnClickListener(this);
        ibPlayPause = (ImageButton) findViewById(R.id.btn_play_pause);
        ibPlayPause.setOnClickListener(this);
        play_music_seek_bar = (SeekBar) findViewById(R.id.play_music_seek_bar);
        tv_songname = (TextView) findViewById(R.id.tv_songname);
        tv_songname.setSelected(true);
        tv_song_detail = (TextView) findViewById(R.id.tv_song_detail);
        loop_not_selected = (ImageButton) findViewById(R.id.loop_not_selected);
        loop_selected = (ImageButton) findViewById(R.id.loop_selected);
        loop_selected.setOnClickListener(this);
        loop_not_selected.setOnClickListener(this);
        songList = new ArrayList<>();
        songList = getSongsFromCurPlaylist();
        //sort alphabetically by title
        sortSongsAlphabetically();

    }

    public void sortSongsAlphabetically() {
        Collections.sort(songList, new Comparator<SongsModel>() {
            public int compare(SongsModel a, SongsModel b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });
    }

    public void sortSongsArtistwise() {
        Collections.sort(songList, new Comparator<SongsModel>() {
            public int compare(SongsModel a, SongsModel b) {
                return a.getArtist().compareTo(b.getArtist());
            }
        });
    }

    @Override
    public void setContentView(int layoutResID) {
        // TODO Auto-generated method stub
        FrameLayout baseLayout, contentFrame;
        SlidingUpPanelLayout anotherBaseLayout;
        FrameLayout anotherContentFrame;

        baseLayout = (FrameLayout) getLayoutInflater().inflate(R.layout.activity_base_music, null); // Your base layout here
        contentFrame = (FrameLayout) baseLayout.findViewById(R.id.container);
        getLayoutInflater().inflate(layoutResID, contentFrame, true);

        anotherBaseLayout = (SlidingUpPanelLayout) getLayoutInflater().inflate(R.layout.slider_layout_to_play_song, null); // Your base layout here
        anotherContentFrame = (FrameLayout) anotherBaseLayout.findViewById(R.id.content_slider);
        getLayoutInflater().inflate(layoutResID, anotherContentFrame, true);
        super.setContentView(baseLayout);
        super.setContentView(anotherBaseLayout);

        initMPElements();
    }

    private void initMPElements() {
        initViews();
    }

    @Override
    protected void onStart() {
        super.onStart();
        musicConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
                //get service
                musicSrv = binder.getService();
                //musicSrv = MainApplication.getInstance();
                //pass list
                musicSrv.setList(songList);
                musicBound = true;
                onMusicServiceConnected();

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                //musicBound = false;
            }
        };
        if (playIntent == null) {
            playIntent = new Intent(getApplicationContext(), MusicService.class);
            getApplicationContext().bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }


    @Override
    public void start() {
        // musicSrv.go();
    }

    @Override
    public void pause() {

    }

    @Override
    public int getDuration() {
        if (musicSrv != null && musicBound && musicSrv.isPng())
            return musicSrv.getDur();
        else return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (musicSrv != null && musicBound && musicSrv.isPng())
            return musicSrv.getPosn();
        else return 0;
    }

    @Override
    public void seekTo(int pos) {
        musicSrv.seek(pos);
    }

    @Override
    public boolean isPlaying() {
        if (musicSrv != null && musicBound)
            return musicSrv.isPng();
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        paused = true;
    }


    private void onMusicServiceConnected() {
        /*if (isSongPlaying) {
            updateProgressBar();
            if (musicSrv == null) {
                Log.e("Music service ", "is null");
            } else {
                tv_songname.setText(musicSrv.getSongName());
                tv_song_detail.setText(musicSrv.getSongDetail());
                Log.e("Music service ", " is  Not null");
            }
            ibPlayPause.setVisibility(View.GONE);
            btn_pause.setVisibility(View.VISIBLE);
        } else {
            ibPlayPause.setVisibility(View.VISIBLE);
            btn_pause.setVisibility(View.GONE);
        }
        if (paused) {
            updateProgressBar();
            paused = false;
        }*/
    }
}
