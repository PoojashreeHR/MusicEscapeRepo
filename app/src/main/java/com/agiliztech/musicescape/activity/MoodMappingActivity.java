package com.agiliztech.musicescape.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.agiliztech.musicescape.models.SongsModel;
import com.agiliztech.musicescape.R;
import com.agiliztech.musicescape.musiccontroller.MusicController;
import com.agiliztech.musicescape.musicservices.MusicService;
import com.agiliztech.musicescape.utils.SongsManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MoodMappingActivity extends AppCompatActivity implements MediaController.MediaPlayerControl,
        View.OnClickListener {

    ArrayList<SongsModel> songList;
    private MusicService musicSrv;
    private Intent playIntent;
    //binding
    private boolean musicBound = false;

    //controller
    private MusicController controller;

    //activity and playback pause flags
    private boolean paused = false, playbackPaused = false;

    ImageButton ibPlayPause;

    SongsManager manager = new SongsManager(MoodMappingActivity.this);

    View view;

   // Button musicButton;
    private boolean isPlaying = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_mapping);

        final Button testButton = (Button) findViewById(R.id.button);
        testButton.setText("Start");
        testButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!isPlaying) {
                    //  mPlayer.start();
                    testButton.setText("Pause");
                    isPlaying = true;
                } else {
                    // mPlayer.stop();
                    testButton.setText("Start");
                    isPlaying = false;
                }
            }
        });

      /*  class SampleView extends View
        {
            public SampleView(Context context)
            {
                super(context);
                // TODO Auto-generated constructor stub
            }
            @Override
            protected void onDraw(Canvas canvas)
            {
                Paint mPaint = new Paint();
                mPaint.setColor(Color.RED);
                mPaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(30, 30, 10, mPaint);

            }
        }*/
            ibPlayPause = (ImageButton) findViewById(R.id.btn_play_pause);
        ibPlayPause.setOnClickListener(this);
        songList = new ArrayList<>();
        songList = manager.getSongList();
        //getSongList();

        //sort alphabetically by title
        Collections.sort(songList, new Comparator<SongsModel>() {
            public int compare(SongsModel a, SongsModel b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });

        //setup controller
        setController();
    }

    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            //get service
            musicSrv = binder.getService();
            //pass list
            musicSrv.setList(songList);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    public void songPicked() {
        musicSrv.setSong(0);
        musicSrv.playSong();
        if (playbackPaused) {
            setController();
            playbackPaused = false;
        }
        //controller.show(0);
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

    @Override
    public void pause() {
        /*playbackPaused = true;
        musicSrv.pausePlayer();*/
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
    public void start() {
        musicSrv.go();
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


    private void setController() {

    }

    private void playNext() {
        musicSrv.playNext();
        if (playbackPaused) {
            setController();
            playbackPaused = false;
        }
        //controller.show(0);
    }

    private void playPrev() {
        musicSrv.playPrev();
        if (playbackPaused) {
            setController();
            playbackPaused = false;
        }
        //controller.show(0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        paused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (paused) {
            setController();
            paused = false;
        }
    }

    @Override
    protected void onStop() {
        //controller.hide();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        stopService(playIntent);
        musicSrv = null;
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_play_pause:
                if (!musicSrv.isPng()) {
                    songPicked();
                    musicSrv.go();
                } else {
                    playbackPaused = true;
                    musicSrv.pausePlayer();
                }
                break;
        }
    }
}