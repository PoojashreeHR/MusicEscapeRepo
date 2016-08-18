package com.agiliztech.musicescape.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;

import com.agiliztech.musicescape.R;
import com.agiliztech.musicescape.adapter.RecyclerViewAdapter;
import com.agiliztech.musicescape.models.SongsModel;
import com.agiliztech.musicescape.musicservices.MusicService;
import com.agiliztech.musicescape.utils.UtilityClass;
import com.google.gson.Gson;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class MoodMappingActivity extends BaseMusicActivity implements
        View.OnClickListener, SeekBar.OnSeekBarChangeListener, RecyclerViewAdapter.IClickListener {

    SharedPreferences sp;
    RecyclerView mRecyclerView;
    RecyclerViewAdapter mAdapter;
    SlidingUpPanelLayout slidingUpPanelLayout;
    ImageButton library;
    private Handler handler = new Handler();
    // Button musicButton;
    private boolean isPlaying = false;
    private static boolean isSongPlaying = false;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String curSongJson = intent.getStringExtra("currentSong");
            SongsModel songsModel = new Gson().fromJson(curSongJson, SongsModel.class);
            tv_songname.setText(songsModel.getTitle());
            tv_song_detail.setText(songsModel.getArtist());

            SharedPreferences.Editor editor = sp.edit();
            editor.putString("song_name",songsModel.getTitle());
            editor.putString("song_detail",songsModel.getArtist());
            editor.commit();
        }
    };
    Button testButton;
    private ImageView dashboardButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_mood_mapping);

        sp = getSharedPreferences("MyPrefs",MODE_PRIVATE);

        Typeface tf = Typeface.createFromAsset(getAssets(),
                "fonts/MontserratRegular.ttf");
        TextView tv = (TextView) findViewById(R.id.moodMapping);
        tv.setTypeface(tf);
        testButton = (Button) findViewById(R.id.button);
        testButton.setText("START");
        testButton.setOnClickListener(this);

        slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.slider_sliding_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_display_song_lists);


        library = (ImageButton) findViewById(R.id.libraryButton);
        library.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                library.setFocusableInTouchMode(false);
                library.setFocusable(false);
                Intent intent = new Intent(getApplicationContext(), LibraryActivity.class);
                startActivity(intent);
            }
        });

        dashboardButton = (ImageView) findViewById(R.id.dashboardButton);
        dashboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),DashboardActivity.class);
                startActivity(intent);
            }
        });

        mAdapter = new RecyclerViewAdapter(songList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        play_music_seek_bar.setOnSeekBarChangeListener(this);
        slidingUpPanelLayout.setScrollableView(mRecyclerView);
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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onPause();
        paused = true;

    }

    @Override
    protected void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(MusicService.SERVICE_EVENT));
        super.onResume();

        if(sp!=null){
            tv_songname.setText(sp.getString("song_name",null));
            tv_song_detail.setText(sp.getString("song_detail",null));
        }

        if (isSongPlaying) {
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
        }
        /*if(musicSrv.isPng()) {
            tv_songname.setText(musicSrv.getSongName());
            tv_song_detail.setText(musicSrv.getSongDetail());
        }*/

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
            //getApplicationContext().unbindService(musicConnection);
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
                        // songPicked();
                        //musicSrv.go();
                        updateProgressBar();
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
                stopService(playIntent);
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
            case R.id.button:
                if (!isPlaying) {
                    //  mPlayer.start();
                    testButton.setText("Pause");
                    isPlaying = true;
                } else {
                    // mPlayer.stop();
                    testButton.setText("Start");
                    isPlaying = false;
                }
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
    public void playSelectedSong(int position, View view) {
        musicSrv.setSong(Integer.parseInt(view.getTag().toString()));
        musicSrv.playSong();
        isSongPlaying = true;
        updateProgressBar();
        ibPlayPause.setVisibility(View.GONE);
        btn_pause.setVisibility(View.VISIBLE);
        if (playbackPaused) {
            //setController();
            playbackPaused = false;
        }
        if (musicSrv.isPng()) {

        }
    }

    @Override
    public void onBackPressed() {
        if (slidingUpPanelLayout != null &&
                (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED
                        || slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }
}