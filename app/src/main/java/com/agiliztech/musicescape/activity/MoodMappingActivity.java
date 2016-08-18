package com.agiliztech.musicescape.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;

import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.MediaController;

import android.widget.RelativeLayout;

import android.widget.SeekBar;

import android.widget.TextView;

import com.agiliztech.musicescape.R;
import com.agiliztech.musicescape.models.SongsModel;
import com.agiliztech.musicescape.musiccontroller.MusicController;
import com.agiliztech.musicescape.musicservices.MusicService;
import com.agiliztech.musicescape.utils.SongsManager;
import com.agiliztech.musicescape.utils.UtilityClass;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MoodMappingActivity extends AppCompatActivity implements MediaController.MediaPlayerControl,
        View.OnClickListener, SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener {

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
    ImageButton btn_pause;
    ImageButton loop_not_selected, loop_selected;
    TextView tv_songname;
    TextView tv_song_detail;
    SeekBar play_music_seek_bar;
    ImageButton library;

    SongsManager manager = new SongsManager(MoodMappingActivity.this);

    View view;

    // Button musicButton;
    private Handler handler = new Handler();
    // Button musicButton;
    private boolean isPlaying = false;
    private static boolean isSongPlaying = false;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String curSongJson = intent.getStringExtra("currentSong");
            SongsModel songsModel = new Gson().fromJson(curSongJson,SongsModel.class);
            tv_songname.setText(songsModel.getTitle());
            tv_song_detail.setText(songsModel.getArtist());

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_mapping);


        Typeface tf = Typeface.createFromAsset(getAssets(),
                "fonts/MontserratRegular.ttf");
        TextView tv = (TextView) findViewById(R.id.moodMapping);
        tv.setTypeface(tf);

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

        library = (ImageButton) findViewById(R.id.libraryButton);
        library.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                library.setFocusableInTouchMode(false);
                library.setFocusable(false);
                Intent intent = new Intent(getApplicationContext(),LibraryActivity.class);
                startActivity(intent);
            }
        });

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

        play_music_seek_bar.setOnSeekBarChangeListener(this);

        //setup controller
        setController();
    }


    private void updateProgressBar() {
        handler.postDelayed(mUpdateTimeTask, 100);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        @Override
        public void run() {
            long totalDuration = musicSrv.getDur();
            long currDuration = musicSrv.getPosn();

            int progress = (int) UtilityClass.getProgressPercentage(currDuration, totalDuration);
            play_music_seek_bar.setProgress(progress);
            handler.postDelayed(this, 100);
        }
    };
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
            //musicBound = false;
        }
    };

    public void songPicked() {
        musicSrv.setSong(0);
        musicSrv.playSong();

        tv_songname.setText(songList.get(0).getTitle());
        tv_song_detail.setText(songList.get(0).getArtist());

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
            playIntent = new Intent(getApplicationContext(), MusicService.class);
            getApplicationContext().bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
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
        //musicSrv.go();
    }

    @Override
    public void pause() {

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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mMessageReceiver);
        super.onPause();
        paused = true;
    }

    @Override
    protected void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter(MusicService.SERVICE_EVENT));
        super.onResume();

        if (isSongPlaying) {
            updateProgressBar();
            tv_songname.setText(songList.get(0).getTitle());
            tv_song_detail.setText(songList.get(0).getArtist());
            ibPlayPause.setVisibility(View.GONE);
            btn_pause.setVisibility(View.VISIBLE);
        } else {
            ibPlayPause.setVisibility(View.VISIBLE);
            btn_pause.setVisibility(View.GONE);
        }
        if (paused) {
            tv_songname.setText(songList.get(0).getTitle());
            tv_song_detail.setText(songList.get(0).getArtist());
            updateProgressBar();
            //setController();
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
        // stopService(playIntent);
        //musicSrv = null;
        super.onDestroy();
        if (musicConnection != null) {
            getApplicationContext().unbindService(musicConnection);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_play_pause:
                if (!musicSrv.isPng()) {
                    if (playbackPaused) {
                        musicSrv.go();
                        isSongPlaying = true;
                        play_music_seek_bar.setProgress(0);
                        play_music_seek_bar.setMax(100);
                        updateProgressBar();
                        btn_pause.setVisibility(View.VISIBLE);
                        ibPlayPause.setVisibility(View.GONE);
                    } else {
                        songPicked();
                        musicSrv.go();
                        isSongPlaying = true;
                        btn_pause.setVisibility(View.VISIBLE);
                        ibPlayPause.setVisibility(View.GONE);
                    }
                } else {
                    isSongPlaying = false;
                }
                break;
            case R.id.btn_pause:
                isSongPlaying = false;
                playbackPaused = true;
                musicSrv.pausePlayer();
                btn_pause.setVisibility(View.GONE);
                ibPlayPause.setVisibility(View.VISIBLE);
                break;
            case R.id.loop_selected:
                loop_not_selected.setVisibility(View.VISIBLE);
                loop_selected.setVisibility(View.GONE);
                break;
            case R.id.loop_not_selected:
                loop_not_selected.setVisibility(View.GONE);
                loop_selected.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        handler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        handler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = musicSrv.getDur();
        int currPosition = UtilityClass.progressToTimer(seekBar.getProgress(), totalDuration);
        musicSrv.seek(currPosition);
        updateProgressBar();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }


}