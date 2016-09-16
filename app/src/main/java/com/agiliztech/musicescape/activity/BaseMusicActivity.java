package com.agiliztech.musicescape.activity;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.agiliztech.musicescape.R;
import com.agiliztech.musicescape.database.DBHandler;
import com.agiliztech.musicescape.journey.JourneyService;
import com.agiliztech.musicescape.journey.JourneySong;
import com.agiliztech.musicescape.journey.SongMoodCategory;
import com.agiliztech.musicescape.models.Artist;
import com.agiliztech.musicescape.models.Song;
import com.agiliztech.musicescape.models.apimodels.SongRetagInfo;
import com.agiliztech.musicescape.models.apimodels.SongRetagMain;
import com.agiliztech.musicescape.musicservices.MusicService;
import com.agiliztech.musicescape.rest.ApiClient;
import com.agiliztech.musicescape.rest.ApiInterface;
import com.agiliztech.musicescape.utils.Global;
import com.agiliztech.musicescape.utils.SongsManager;
import com.agiliztech.musicescape.utils.UtilityClass;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.google.gson.Gson;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BaseMusicActivity extends AppCompatActivity implements
        MediaController.MediaPlayerControl {

    protected static MusicService musicSrv;
    protected boolean musicBound = false;
    protected ImageButton ibPlayPause;
    SongsManager songsManager = new SongsManager(this);
    protected ArrayList<Song> songList;
    protected Intent playIntent;
    public boolean paused = false, playbackPaused = false;

    protected ImageButton btn_pause;
    protected ImageButton loop_not_selected, loop_selected_for_playlist, loop_selected_for_single_song;
    protected TextView tv_songname;
    protected TextView tv_song_detail;
    protected SeekBar play_music_seek_bar;
    LinearLayout linearLayout;
    protected boolean isPlaying = false;
    public static boolean isSongPlaying = false;
    protected ServiceConnection musicConnection;
    protected LinearLayout dragView;
    private RecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private SharedPreferences sp;


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String curSongJson = intent.getStringExtra("currentSong");
            Song song = new Gson().fromJson(curSongJson, Song.class);
            if (song == null) {
                return;
            }
            tv_songname.setText(song.getSongName());
            tv_song_detail.setText(song.getArtist().getArtistName());

            updateMusicPlayerByMood();
            ArrayList<Song> songs = songList;
            for (int i = 0; i < songs.size(); i++) {
                songs.get(i).setPlaying(false);
                if (songs.get(i).getSongName().equals(song.getSongName())) {
                    mAdapter.updateActiveSongImage(song, i);
                    mRecyclerView.scrollToPosition(i);
                    mAdapter.notifyDataSetChanged();
                    //break;
                }
            }
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("song_name", song.getSongName());
            editor.putString("song_detail", song.getArtist().getArtistName());
            editor.apply();
        }
    };

    public ArrayList<Song> getSongsFromCurPlaylist() {
        if (Global.isJourney) {
            List<JourneySong> jSongs = JourneyService.getInstance(this).getCurrentSession().getSongs();
            ArrayList<Song> currentSongs = new ArrayList<>();
            for (int i = 0; jSongs != null && i < jSongs.size(); i++) {
                if (jSongs.get(i).getSong() != null) {
                    currentSongs.add(jSongs.get(i).getSong());
                }
            }
            // Collections.reverse(currentSongs);
            return currentSongs;
        } else if (Global.isLibPlaylist) {
            return Global.libPlaylistSongs;
        } else {
            DBHandler dbHandler = new DBHandler(this);
            ArrayList<Song> songs =  dbHandler.getAllSongsFromDB();
            dbHandler.close();
            return songs;
        }
    }

    SlidingUpPanelLayout anotherBaseLayout;
    FrameLayout baseLayout, contentFrame;
    FrameLayout anotherContentFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void initViews() {
        sp = getSharedPreferences(Global.PREF_NAME, MODE_PRIVATE);
        btn_pause = (ImageButton) findViewById(R.id.btn_pause);
        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                nMgr.cancelAll();
                //musicSrv.stopForeground(true);
                isSongPlaying = false;
                playbackPaused = true;
                // stopService(playIntent);
                musicSrv.pausePlayer();
                btn_pause.setVisibility(View.GONE);
                ibPlayPause.setVisibility(View.VISIBLE);
            }
        });
        ibPlayPause = (ImageButton) findViewById(R.id.btn_play_pause);
        // ibPlayPause.setOnClickListener(this);
        linearLayout = (LinearLayout) findViewById(R.id.toSwipe);
        ibPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!musicSrv.isPng()) {
                    if (playbackPaused) {
                        musicSrv.go();
                        isSongPlaying = true;
                        play_music_seek_bar.setProgress(0);
                        play_music_seek_bar.setMax(100);
                        //updateProgressBar();
                        btn_pause.setVisibility(View.VISIBLE);
                        ibPlayPause.setVisibility(View.GONE);
                    } else {
                        // songPicked();
                        //musicSrv.go();
                        //updateProgressBar();
                        if (sp.getString("song_name", null) != null) {
                            isSongPlaying = true;
                            btn_pause.setVisibility(View.VISIBLE);
                            ibPlayPause.setVisibility(View.GONE);
                        }
                    }
                } else {
                    isSongPlaying = false;
                }
            }
        });
        linearLayout = (LinearLayout) findViewById(R.id.toSwipe);

        play_music_seek_bar = (SeekBar) findViewById(R.id.play_music_seek_bar);
        play_music_seek_bar.setScaleY(1.5f);
        play_music_seek_bar.setPadding(0, 0, 0, 0);
        if (Build.VERSION.SDK_INT > 16) {
            play_music_seek_bar.getThumb().mutate().setAlpha(0);
        } else {
            play_music_seek_bar.getThumb().mutate().setAlpha(0);
        }
        play_music_seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
                Log.e("onStopTrackingTouch ", " TOTAL DURATION : " + totalDuration);
                int currPosition = UtilityClass.progressToTimer(seekBar.getProgress(), totalDuration);
                Log.e("onStopTrackingTouch ", " CURRENT POSITION : " + currPosition);
                musicSrv.seek(currPosition);
                updateProgressBar();
            }
        });
        tv_songname = (TextView) findViewById(R.id.tv_songname);
        tv_songname.setSelected(true);
        tv_song_detail = (TextView) findViewById(R.id.tv_song_detail);
        loop_not_selected = (ImageButton) findViewById(R.id.loop_not_selected);
        loop_selected_for_playlist = (ImageButton) findViewById(R.id.loop_selected_for_playlist);
        loop_selected_for_single_song = (ImageButton) findViewById(R.id.loop_selected_for_single_song);


        loop_not_selected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loop_not_selected.getVisibility() == View.VISIBLE) {
                    loop_not_selected.setVisibility(View.GONE);
                    loop_selected_for_playlist.setVisibility(View.VISIBLE);
                    loop_selected_for_single_song.setVisibility(View.GONE);
                    musicSrv.setRepeatPlayList(true);
                }

            }
        });
        loop_selected_for_playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loop_selected_for_playlist.getVisibility() == View.VISIBLE) {
                    loop_selected_for_single_song.setVisibility(View.VISIBLE);
                    loop_selected_for_playlist.setVisibility(View.GONE);
                    loop_not_selected.setVisibility(View.GONE);
                    musicSrv.setRepeatSingleSong(true);
                }
            }
        });
        loop_selected_for_single_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loop_selected_for_single_song.getVisibility() == View.VISIBLE) {
                    loop_selected_for_single_song.setVisibility(View.GONE);
                    loop_not_selected.setVisibility(View.VISIBLE);
                    loop_selected_for_playlist.setVisibility(View.GONE);
                    musicSrv.setNoRepeat(true);
                }

            }
        });


        songList = new ArrayList<>();
        //songList = getSongsFromCurPlaylist();
        slider = (SlidingUpPanelLayout) findViewById(R.id.slider_sliding_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_display_song_lists);
        dragView = (LinearLayout) findViewById(R.id.dragView);
        //sort alphabetically by title
        sortSongsAlphabetically();
        linearLayout = (LinearLayout) findViewById(R.id.toSwipe);
        setUpPlaylist();

        linearLayout.setOnTouchListener(new View.OnTouchListener() {
            int downX, upX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    downX = (int) event.getX();
                    Log.i("event.getX()", " downX " + downX);
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    upX = (int) event.getX();
                    Log.i("event.getX()", " upX " + downX);
                    if (upX - downX > 100) {
                        // swipe right
                        // Toast.makeText(getApplicationContext(),"Swiping Right",Toast.LENGTH_LONG).show();
                        musicSrv.playPrev();
                        tv_songname.setText(musicSrv.getSongName());
                        tv_song_detail.setText(musicSrv.getSongDetail());
                    } else if (downX - upX > -100) {
                        //  Toast.makeText(getApplicationContext(),"Swiping Left",Toast.LENGTH_LONG).show();
                        musicSrv.playNext();
                        tv_songname.setText(musicSrv.getSongName());
                        tv_song_detail.setText(musicSrv.getSongDetail());
                        // swipe left
                    }
                    return true;

                }
                return false;
            }

        });

    }

    protected void setUpPlaylist() {
        songList = getSongsFromCurPlaylist();
        if (songList.size() > 0) {
            mAdapter = new RecyclerViewAdapter(songList, this, false, null);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mRecyclerView.setAdapter(mAdapter);
            if (musicSrv != null) {
                musicSrv.setList(songList);
            }

            initSwipeRecyclerView();
            mAdapter.notifyDataSetChanged();
        }

        slider.setScrollableView(mRecyclerView);
    }

    public void songRetag(int position, Song model) {
        // Logic for Retag from Library
        if (UtilityClass.checkInternetConnectivity(this)) {
            Log.e("ABC", "XYZ");
            displaySelectMoodDialogFromBase(model, position);
            //sendToApi(model);
        }
    }

    public void displaySelectMoodDialogFromBase(final Song model, final int position) {
        Log.e("ABC2", "XYZ2");
        final Dialog moodDialog = new Dialog(this);
        moodDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        moodDialog.setContentView(R.layout.selecting_mood_dialog);
        TextView exitedText_dialog, happyText_dialog, chilledText_dialog, peacefullText_dialog, boredText_dialog,
                depressedText_dialog, stressedText_dialog, aggressiveText_dialog;

        exitedText_dialog = (TextView) moodDialog.findViewById(R.id.exitedText_dialog);

        happyText_dialog = (TextView) moodDialog.findViewById(R.id.happyText_dialog);
        chilledText_dialog = (TextView) moodDialog.findViewById(R.id.chilledText_dialog);
        peacefullText_dialog = (TextView) moodDialog.findViewById(R.id.peacefullText_dialog);
        boredText_dialog = (TextView) moodDialog.findViewById(R.id.boredText_dialog);
        depressedText_dialog = (TextView) moodDialog.findViewById(R.id.depressedText_dialog);
        stressedText_dialog = (TextView) moodDialog.findViewById(R.id.stressedText_dialog);
        aggressiveText_dialog = (TextView) moodDialog.findViewById(R.id.aggressiveText_dialog);

        exitedText_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moodDialog.dismiss();
                sendToApiFromBase("Excited", model, position, 0);
            }
        });
        happyText_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moodDialog.dismiss();
                sendToApiFromBase("Happy", model, position, 1);
            }
        });
        chilledText_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moodDialog.dismiss();
                sendToApiFromBase("Chilled", model, position, 2);
            }
        });
        peacefullText_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moodDialog.dismiss();
                sendToApiFromBase("Peaceful", model, position, 3);
            }
        });
        boredText_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moodDialog.dismiss();
                sendToApiFromBase("Bored", model, position, 4);
            }
        });
        depressedText_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moodDialog.dismiss();
                sendToApiFromBase("Depressed", model, position, 5);
            }
        });
        stressedText_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moodDialog.show();
                sendToApiFromBase("Stressed", model, position, 6);
            }
        });
        aggressiveText_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moodDialog.dismiss();
                sendToApiFromBase("Aggressive", model, position, 7);
            }
        });
        moodDialog.setCanceledOnTouchOutside(false);
        moodDialog.show();
    }

    public void sendToApiFromBase(final String mood, final Song model, final int position, final int moodPosition) {
        final ArrayList<SongRetagInfo> info = new ArrayList<>();
        final DBHandler dbHandler = new DBHandler(this);
        final int serverSongId = dbHandler.getServerSongId((int) model.getpID());
        final Song song = model;
        for (int i = 0; i < 1; i++) {
            SongRetagInfo infos = new SongRetagInfo();
            infos.setSong(serverSongId);
            infos.setEnergy(model.getEnergy());
            infos.setValence(model.getValance());
            infos.setMood(mood);
            info.add(infos);
        }

        song.setMood(SongsManager.getMoodFromIndex(moodPosition));
        SongRetagMain main = new SongRetagMain(UtilityClass.getDeviceId(this), info);
        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class, "RandyApp", "N1nj@R@nDy");
        Call<Void> call = apiInterface.retagSongs(main);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    dbHandler.updateSongStatusWithModifiedMood(mood, serverSongId);
                    dbHandler.close();
                    mAdapter.updateSongMoodSelectedByUser(position, song);
                    mAdapter.notifyDataSetChanged();
                    mAdapter.notifyItemChanged(position);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                dbHandler.close();
                Toast.makeText(BaseMusicActivity.this, "Some Issue Occured, Plz try after some time", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void initSwipeRecyclerView() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                //libAdapter = (LibraryRecyclerView) recyclerView.getAdapter();
                Song song = mAdapter.getSongObject(position);
                if (direction == ItemTouchHelper.LEFT) {
                    mAdapter.notifyItemChanged(position);
                    songRetag(position, song);
                } else {
                    swapSong(position, song);
                    mAdapter.notifyItemChanged(position);
                }
            }

            private Paint p = new Paint();

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int swipeFlags;
                if (Global.isJourney) {
                    swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                } else {
                    swipeFlags = ItemTouchHelper.LEFT;
                }
                return makeMovementFlags(ItemTouchHelper.ACTION_STATE_IDLE, swipeFlags);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;
                    if (dX < 0) {
                        p.setColor(Color.parseColor("#000000"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.img_retag_new);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawBitmap(icon, null, icon_dest, p);
                        Log.e("WIDTH ", " WIDTH = " + itemView.getWidth());
                        Log.e("DX ", " DX = " + dX);
                    }

                    // if (Global.isJourney) {
                    else {
                        p.setColor(Color.parseColor("#000000"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_swap2);
                        RectF icon_dest = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom());
                        c.drawBitmap(icon, null, icon_dest, p);
                        Log.e("WIDTH ", " WIDTH = " + itemView.getWidth());
                        Log.e("DX ", " DX = " + dX);
                    }
                    //  }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    public void sortSongsAlphabetically() {
        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getSongName().compareToIgnoreCase(b.getSongName());
            }
        });
    }

    public void sortSongsArtistwise() {
        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getArtist().getArtistName().compareToIgnoreCase(b.getArtist().getArtistName());
            }
        });
    }

    public void sortSongsAlbumwise() {
        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getAlbum().getAlbumTitle().compareToIgnoreCase(b.getAlbum().getAlbumTitle());
            }
        });
    }

    @Override
    protected void onDestroy() {
        musicSrv.killService();
        super.onDestroy();
    }

    @Override
    public void setContentView(int layoutResID) {
        // TODO Auto-generated method stub

        baseLayout = (FrameLayout) getLayoutInflater().inflate(R.layout.activity_base_music, null);
        // Your base layout here
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
        resumeOnce();
    }

    private void resumeOnce() {
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isFirstRun", false).commit();
        if (sp != null) {
            tv_songname.setText(sp.getString("song_name", null));
            tv_song_detail.setText(sp.getString("song_detail", null));
            playbackPaused = Boolean.parseBoolean(sp.getString("playbackpaused", null));

            if (sp.getString("song_id_sp", null) != null) {
                Log.e("SONG ID SP", "" + sp.getString("song_id_sp", null));
            }
            if (sp.getString("song_position", null) != null) {
                Log.e(" SONG POSITION SP ", "" + sp.getString("song_position", null));
                //updateProgressBar();
            }
            if (sp.getString("song_name_sp", null) != null) {
                Log.e(" SONG NAME SP ", "" + sp.getString("song_name_sp", null));
            }
            //updateProgressBar();
            /*SharedPreferences.Editor editor = sp.edit();
            editor.putString("playbackpaused", "" + playbackPaused);
            editor.putString("song_id_sp",musicSrv.getSongId());
            editor.putString("song_position",""+musicSrv.getPosn());
            editor.putString("song_name",musicSrv.getSongName());
            editor.commit();*/
        }

        if (isSongPlaying) {
            //updateProgressBar();
            if (musicSrv == null) {
                Log.e("Music service ", "is null");
            } else {
                tv_songname.setText(musicSrv.getSongName());
                tv_song_detail.setText(musicSrv.getSongDetail());
                updateMusicPlayerByMood();
                setUpPlaylist();
                Log.e("Music service ", " is  Not null");
            }
            ibPlayPause.setVisibility(View.GONE);
            btn_pause.setVisibility(View.VISIBLE);
        } else {
            ibPlayPause.setVisibility(View.VISIBLE);
            btn_pause.setVisibility(View.GONE);
        }
    }

    private void updateMusicPlayerByMood() {
        Song currSong = musicSrv.getCurrentPlayed();
        if (currSong.getMood() != null) {
            btn_pause.setImageResource(SongsManager.getImageResource(currSong.getMood(), false));
            ibPlayPause.setImageResource(SongsManager.getImageResource(currSong.getMood(), true));
            changeSeekbarColor(play_music_seek_bar, SongsManager.colorForMood(currSong.getMood()));
        } else {
            btn_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_current_notfound_pause2x));
            ibPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_current_notfound_play));
            changeSeekbarColor(play_music_seek_bar, Color.WHITE);
        }
    }

    SlidingUpPanelLayout slider;
    FrameLayout frameLayout;


    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean scannedOnce = sharedPreferences.getBoolean(Global.isScannedOnce, false);
        if(!scannedOnce){
            hideMusicPlayer();
        }
        else{
            showMusicPlayer();
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(MusicService.SERVICE_EVENT));
        resumeOnce();
        /*if (paused) {
            updateProgressBar();
            paused = false;
        }*/
        /*if(musicSrv.isPng()) {
            tv_songname.setText(musicSrv.getSongName());
            tv_song_detail.setText(musicSrv.getSongDetail());
        }*/
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
                Global.currentSongList = songList;
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
            //startService(playIntent);
        }
    }

    @Override
    public void onBackPressed() {
        if (slider != null &&
                (slider.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED
                        || slider.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            slider.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            //  super.onBackPressed();
            finish();

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
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onPause();
        paused = true;
    }

    protected void hideMusicPlayer() {
        if (dragView != null) {
            dragView.setVisibility(View.INVISIBLE);
        }
    }

    protected void showMusicPlayer() {
        if (dragView != null) {
            dragView.setVisibility(View.VISIBLE);
        }
    }

    private void onMusicServiceConnected() {
        if (loop_not_selected.getVisibility() == View.VISIBLE) {
            musicSrv.setNoRepeat(true);
        } else if (loop_selected_for_playlist.getVisibility() == View.VISIBLE) {
            musicSrv.setRepeatSingleSong(true);
        } else if (loop_selected_for_single_song.getVisibility() == View.VISIBLE) {
            musicSrv.setRepeatPlayList(true);
        }
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

    /*public ArrayList<Song> getSongsFromCurPlaylist() {
        if (Global.isJourney) {
            List<JourneySong> jSongs = JourneyService.getInstance(this).getCurrentSession().getSongs();
            ArrayList<Song> currentSongs = new ArrayList<>();
            for (int i = 0; jSongs != null && i < jSongs.size(); i++) {
                if (jSongs.get(i).getSong() != null) {
                    currentSongs.add(jSongs.get(i).getSong());
                }
            }
            // Collections.reverse(currentSongs);
            return currentSongs;
        } else if (Global.isLibPlaylist) {
            return Global.libPlaylistSongs;
        } else {
            DBHandler dbHandler = new DBHandler(this);
            return dbHandler.getAllSongsFromDB();
        }
    }*/

    public void swapSong(int position, Song song) {
        List<JourneySong> swapSongInJourney = JourneyService.getInstance(this).getCurrentSession().getSongs();
        if (swapSongInJourney != null) {
            JourneySong jSong = JourneyService.getInstance(this).swapSong(swapSongInJourney.get(position), song.getMood());
            if (jSong != null) {
                Song newSong = jSong.getSong();
                mAdapter.setNewSwapSong(position, newSong);
                mAdapter.notifyDataSetChanged();
            }
        }
    }


    public void playSelectedSong(int position) {

        musicSrv.setSong(position);
        musicSrv.playSong();
        isSongPlaying = true;

        //Song currSong = musicSrv.getCurrentPlayed();
        tv_songname.setText(musicSrv.getSongName());
        tv_song_detail.setText(musicSrv.getSongDetail());
        updateMusicPlayerByMood();

        updateProgressBar();
        ibPlayPause.setVisibility(View.GONE);
        btn_pause.setVisibility(View.VISIBLE);

        //Pass color as the song mood color

    }

    public void changeSeekbarColor(SeekBar s, int colorp) {
        try {
            LayerDrawable layerDrawable = (LayerDrawable) s.getProgressDrawable();
            Drawable background;// = (Drawable) layerDrawable.findDrawableByLayerId(android.R.id.background);
            Bitmap backgroundBmp = BitmapFactory.decodeResource(getResources(), colorp);
            background = new BitmapDrawable(getResources(), backgroundBmp);
            layerDrawable.setDrawableByLayerId(android.R.id.background, background);
        } catch (ClassCastException e) {
            Log.d("changeSeekbarColor", e.getMessage());
            Log.d("changeSeekbarColor", "Using only basic color");
        }

        s.getProgressDrawable().setColorFilter(colorp, PorterDuff.Mode.SRC_IN);
    }

    private Handler handler = new Handler();

    private void updateProgressBar() {
        handler.postDelayed(mUpdateTimeTask, 100);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        @Override
        public void run() {

            if (musicSrv != null) {
                long totalDuration = musicSrv.getDur();
                long currDuration = musicSrv.getPosn();

                int progress = (int) UtilityClass.getProgressPercentage(currDuration, totalDuration);
                play_music_seek_bar.setProgress(progress);
                handler.postDelayed(this, 100);
            }
        }
    };


    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        if (sp != null) {
            if (playbackPaused) {
                if (musicSrv != null) {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("playbackpaused", "" + playbackPaused);
                    editor.putString("song_id_sp", musicSrv.getSongId());
                    editor.putString("song_position", "" + musicSrv.getPosn());
                    editor.putString("song_name_sp", musicSrv.getSongName());
                    editor.apply();
                }
            }
        }
        super.onStop();
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

        private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();


        List<Song> listOfSongs;
        Context context;
        boolean isPlaying;
        Song song;

        public RecyclerViewAdapter(List<Song> listOfSongs, Context context, boolean isPlaying, Song song) {
            this.listOfSongs = listOfSongs;
            this.context = context;
            this.isPlaying = isPlaying;
            this.song = song;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_items, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public int getItemCount() {
            return listOfSongs.size();
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {


            final int pos = position;
            final Song model = listOfSongs.get(position);
            //viewBinderHelper.bind(holder.swipe_layout, String.valueOf(model.getpID()));
            //viewBinderHelper.bind(holder.rv_ll, String.valueOf(model.getpID()));

            holder.rv_song_name.setText(model.getSongName());
            holder.rv_song_detail.setText(handleUnknownArtist(model.getArtist()));
//            holder.rv_ll.setTag(pos);
          /*  holder.rv_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // iClickListener.playSelectedSong(pos,holder.rv_ll);
                    //  iClickListener.playSelectedSong(pos, holder.rv_ll);
                }
            });*/

            /*holder.swipe_layout.setSwipeListener(new SwipeRevealLayout.SimpleSwipeListener() {
                @Override
                public void onClosed(SwipeRevealLayout view) {
                    super.onClosed(view);
                    holder.rv_ll.setLockDrag(false);
                }

                @Override
                public void onOpened(SwipeRevealLayout view) {
                    super.onOpened(view);
                    holder.rv_ll.setLockDrag(true);
                }

                @Override
                public void onSlide(SwipeRevealLayout view, float slideOffset) {
                    //super.onSlide(view, slideOffset);
                    if (holder.swipe_layout.isOpened()) {
                        holder.swipe_layout.close(true);
                    } else if (holder.rv_ll.isOpened()) {
                        holder.rv_ll.close(true);
                    }
                }
            });
            holder.rv_ll.setSwipeListener(new SwipeRevealLayout.SimpleSwipeListener() {
                @Override
                public void onClosed(SwipeRevealLayout view) {
                    super.onClosed(view);
                    holder.swipe_layout.setLockDrag(false);
                }

                @Override
                public void onOpened(SwipeRevealLayout view) {
                    super.onOpened(view);
                    holder.swipe_layout.setLockDrag(true);
                }

                @Override
                public void onSlide(SwipeRevealLayout view, float slideOffset) {
                    //super.onSlide(view, slideOffset);
                    if (holder.swipe_layout.isOpened()) {
                        holder.swipe_layout.close(true);
                    } else if (holder.rv_ll.isOpened()) {
                        holder.rv_ll.close(true);
                    }
                }
            });*/
            if (isPlaying) {

            }
            SongMoodCategory mood = model.getMood();
            if (mood == null) {
                mood = SongMoodCategory.scAllSongs;
            }
            holder.rv_song_name.setTextColor(SongsManager.colorForMood(mood));
            if (pos == 0) {
                if (songList.get(pos).isPlaying()) {
                    holder.image.setImageResource(SongsManager.getMoodImage(mood, 0, true));
                } else {
                    holder.image.setImageResource(SongsManager.getMoodImage(mood, 0, false));
                }
            } else if (pos == listOfSongs.size() - 1) {
                if (songList.get(pos).isPlaying()) {
                    holder.image.setImageResource(SongsManager.getMoodImage(mood, 123456789, true));
                } else {
                    holder.image.setImageResource(SongsManager.getMoodImage(mood, 123456789, false));
                }
            } else {
                if (songList.get(pos).isPlaying()) {
                    holder.image.setImageResource(SongsManager.getMoodImage(mood, 1, true));
                } else {
                    holder.image.setImageResource(SongsManager.getMoodImage(mood, 1, false));
                }
            }
            holder.rv_song_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //           iClickListener.playSelectedSong(pos, holder.rv_ll);
                }
            });

            holder.container_song_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playSelectedSong(pos);
                }
            });

           /* holder.rv_retag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toast.makeText(context, "Retag Clicked", Toast.LENGTH_SHORT).show();
                    if (holder.swipe_layout.isOpened()) {
                        holder.swipe_layout.close(true);
                    } else if (holder.rv_ll.isOpened()) {
                        holder.rv_ll.close(true);
                    }


                }
            });
            final Song newModel = model;
            holder.rv_swap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.swipe_layout.isOpened()) {
                        holder.swipe_layout.close(true);
                    }
                    if (holder.rv_ll.isOpened()) {
                        holder.rv_ll.close(true);
                    }

                    swapSong(pos, newModel);

                }
            });*/

        }

        private String handleUnknownArtist(Artist artist) {
            if (artist == null)
                return "Unknown";
            if (artist.getArtistName() == null) {
                return "Unknown";
            } else {
                return artist.getArtistName();
            }
        }

        public void updateActiveSongImage(Song song, int position) {
            Song songs = song;
            //SongMoodCategory moods = songs.getMood();
            songs.setPlaying(true);
            listOfSongs.set(position, songs);
            notifyDataSetChanged();
        }

        public void setNewSwapSong(int position, Song newSong) {
            //Song songs = newSong;
            listOfSongs.set(position, newSong);
            notifyDataSetChanged();
        }

        public Song getSongObject(int position) {

            return listOfSongs.get(position);
        }

        public void updateSongMoodSelectedByUser(int position, Song song) {
            listOfSongs.set(position, song);
            notifyDataSetChanged();
            notifyItemChanged(position);
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView rv_song_name;
            private TextView rv_song_detail;
            private SwipeRevealLayout rv_ll, swipe_layout;
            private LinearLayout container_song_item;
            private ImageView rv_retag;
            private ImageView rv_swap;
            private ImageView image;

            public MyViewHolder(View itemView) {
                super(itemView);
                // swipe_layout = (SwipeRevealLayout) itemView.findViewById(R.id.swipe_layout);
                rv_song_name = (TextView) itemView.findViewById(R.id.rv_song_name);
                //rv_ll = (SwipeRevealLayout) itemView.findViewById(R.id.click_layout);
                rv_song_detail = (TextView) itemView.findViewById(R.id.rv_song_detail);
                //rv_retag = (ImageView) itemView.findViewById(R.id.rv_retag);
                // rv_swap = (ImageView) itemView.findViewById(R.id.rv_swap);
                container_song_item = (LinearLayout) itemView.findViewById(R.id.container_song_item);
                image = (ImageView) itemView.findViewById(R.id.image);
            }
        }
    }
}