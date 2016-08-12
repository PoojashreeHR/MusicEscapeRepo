package com.agiliztech.musicescape.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.MediaController;

import com.agiliztech.musicescape.R;
import com.agiliztech.musicescape.models.SongsModel;
import com.agiliztech.musicescape.musicservices.MusicService;
import com.agiliztech.musicescape.utils.SongsManager;

import java.util.ArrayList;

public class BaseMusicActivity extends AppCompatActivity implements MediaController.MediaPlayerControl {

    private FrameLayout baseLayout, contentFrame;
    protected MusicService musicSrv;
    private boolean musicBound = false;
    protected ImageButton ibPlayPause;
    SongsManager songsManager = new SongsManager(this);
    protected ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            //get service
            musicSrv = binder.getService();
            //pass list
            musicSrv.setList(getSongsFromCurPlaylist());
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };
    private Intent playIntent;

    private ArrayList<SongsModel> getSongsFromCurPlaylist() {
        return songsManager.getSongList();
    }

    private boolean playbackPaused;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void setContentView(int layoutResID) {
        // TODO Auto-generated method stub
        baseLayout= (FrameLayout) getLayoutInflater().inflate(R.layout.activity_base_music, null); // Your base layout here
        contentFrame= (FrameLayout) baseLayout.findViewById(R.id.container);
        getLayoutInflater().inflate(layoutResID, contentFrame, true);
        super.setContentView(baseLayout);

        initMPElements();
    }

    private void initMPElements() {
        ibPlayPause = (ImageButton) baseLayout.findViewById(R.id.btn_play_pause);
        ibPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!musicSrv.isPng()) {
                    songPicked();
                    musicSrv.go();
                } else {
                    playbackPaused = true;
                    musicSrv.pausePlayer();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    private void songPicked() {
        musicSrv.setSong(0);
        musicSrv.playSong();
        if (playbackPaused) {
            //setController();
            playbackPaused = false;
        }
    }

    @Override
    public void start() {
        musicSrv.go();
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
        return false;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }
}
