package com.agiliztech.musicescape.activity;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;

import com.agiliztech.musicescape.R;
import com.agiliztech.musicescape.adapter.RecyclerViewAdapter;
import com.agiliztech.musicescape.database.DBHandler;
import com.agiliztech.musicescape.journey.JourneyService;
import com.agiliztech.musicescape.journey.JourneySong;
import com.agiliztech.musicescape.models.Artist;
import com.agiliztech.musicescape.models.Song;
import com.agiliztech.musicescape.models.SongsModel;
import com.agiliztech.musicescape.musicservices.MusicService;
import com.agiliztech.musicescape.utils.Global;
import com.agiliztech.musicescape.utils.SongsManager;
import com.agiliztech.musicescape.utils.UtilityClass;
import com.agiliztech.musicescape.view.CustomDrawableForSeekBar;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class BaseMusicActivity extends AppCompatActivity implements
        MediaController.MediaPlayerControl,
        View.OnClickListener,  SeekBar.OnSeekBarChangeListener
{

    protected static MusicService musicSrv;
    protected boolean musicBound = false;
    protected ImageButton ibPlayPause;
    SongsManager songsManager = new SongsManager(this);
    protected ArrayList<Song> songList;
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
    protected LinearLayout dragView;
    private RecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private SharedPreferences sp;

    public ArrayList<Song> getSongsFromCurPlaylist()
    {
        if(Global.isJourney){
            List<JourneySong> jSongs = JourneyService.getInstance(this).getCurrentSession().getSongs();
            ArrayList<Song> currentSongs = new ArrayList<>();
            for(int i=0; i< jSongs.size(); i++){
                if(jSongs.get(i).getSong() != null) {
                    currentSongs.add(jSongs.get(i).getSong());
                }
            }
           // Collections.reverse(currentSongs);
            return currentSongs;
        }
        else {
            DBHandler dbHandler = new DBHandler(this);
            return dbHandler.getAllSongsFromDB();
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
        //songList = getSongsFromCurPlaylist();
        slider = (SlidingUpPanelLayout) findViewById(R.id.slider_sliding_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_display_song_lists);
        dragView = (LinearLayout) findViewById(R.id.dragView);
        //sort alphabetically by title
        sortSongsAlphabetically();

        setUpPlaylist();

    }

    protected void setUpPlaylist() {
        songList = getSongsFromCurPlaylist();
        if (songList.size() > 0) {
            mAdapter = new RecyclerViewAdapter(songList, this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mRecyclerView.setAdapter(mAdapter);
            if(musicSrv != null ){
                musicSrv.setList(songList);
            }
            mAdapter.notifyDataSetChanged();

        }

        slider.setScrollableView(mRecyclerView);
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
            btn_pause.setImageResource(SongsManager.getImageResource(currSong.getMood(),false));
            ibPlayPause.setImageResource(SongsManager.getImageResource(currSong.getMood(),true));
            changeSeekbarColor(play_music_seek_bar,SongsManager.colorForMood(currSong.getMood()));
        }
    }

    SlidingUpPanelLayout slider;
    FrameLayout frameLayout;



    @Override
    protected void onResume() {
        super.onResume();
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_play_pause:
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
                break;
            case R.id.btn_pause:
                NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                nMgr.cancelAll();
                //musicSrv.stopForeground(true);
                isSongPlaying = false;
                playbackPaused = true;
                // stopService(playIntent);
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
    protected void onPause() {
        super.onPause();
        paused = true;
    }

    protected void hideMusicPlayer(){
        if(dragView != null) {
            dragView.setVisibility(View.INVISIBLE);
        }
    }

    protected void showMusicPlayer(){
        if(dragView != null) {
            dragView.setVisibility(View.VISIBLE);
        }
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


    public void playSelectedSong(int position) {

        musicSrv.setSong(position);
        musicSrv.playSong();
        isSongPlaying = true;

        //Song currSong = musicSrv.getCurrentPlayed();
        tv_songname.setText(musicSrv.getSongName());
        tv_song_detail.setText(musicSrv.getSongDetail());
        updateMusicPlayerByMood();

        //updateProgressBar();
        ibPlayPause.setVisibility(View.GONE);
        btn_pause.setVisibility(View.VISIBLE);

        //Pass color as the song mood color

    }

    public void changeSeekbarColor(SeekBar s, int colorp) {
        CustomDrawableForSeekBar drawableForSeekBar =
                new CustomDrawableForSeekBar(0, 0, 0, 25, colorp, 0);


        if (Build.VERSION.SDK_INT > 16) {
            s.setBackground(drawableForSeekBar);
        }
        if (Build.VERSION.SDK_INT < 16) {
            s.setBackgroundDrawable(drawableForSeekBar);
        }

        /*Drawable drawable = getResources().getDrawable(R.drawable.progress_drawable_demo);
        s.setProgressDrawable(drawable);
*/

       /* GradientDrawable drawable = (GradientDrawable)s.getBackground();
        drawable.setStroke(20, getResources().getColor(R.color.happy)); // set stroke width and stroke color
*/
    }
    private Handler handler = new Handler();
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
    }

    @Override
    protected void onStop() {

        if (sp != null) {
            if (playbackPaused) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("playbackpaused", "" + playbackPaused);
                editor.putString("song_id_sp", musicSrv.getSongId());
                editor.putString("song_position", "" + musicSrv.getPosn());
                editor.putString("song_name_sp", musicSrv.getSongName());
                editor.apply();
            }
        }
        super.onStop();
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

        private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();





        List<Song> listOfSongs;
        Context context;

        public RecyclerViewAdapter(List<Song> listOfSongs,  Context context) {
            this.listOfSongs = listOfSongs;

            this.context = context;
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
            Song model = listOfSongs.get(position);
            viewBinderHelper.bind(holder.swipe_layout, String.valueOf(model.getpID()));
            viewBinderHelper.bind(holder.rv_ll, String.valueOf(model.getpID()));

            holder.rv_song_name.setText(model.getSongName());
            holder.rv_song_detail.setText(handleUnknownArtist(model.getArtist()));
            holder.rv_ll.setTag(pos);
            holder.rv_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // iClickListener.playSelectedSong(pos,holder.rv_ll);
                    //  iClickListener.playSelectedSong(pos, holder.rv_ll);
                }
            });
            holder.swipe_layout.setSwipeListener(new SwipeRevealLayout.SimpleSwipeListener() {
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
            });

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

            holder.rv_retag.setOnClickListener(new View.OnClickListener() {
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

            holder.rv_swap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.swipe_layout.isOpened()) {
                        holder.swipe_layout.close(true);
                    } else if (holder.rv_ll.isOpened()) {
                        holder.rv_ll.close(true);
                    }
                }
            });

        }

        private String handleUnknownArtist(Artist artist) {
            if(artist == null)
                return "Unknown";
            if(artist.getArtistName() == null){
                return "Unknown";
            }
            else{
                return artist.getArtistName();
            }
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView rv_song_name;
            private TextView rv_song_detail;
            private SwipeRevealLayout rv_ll, swipe_layout;
            private LinearLayout container_song_item;
            private ImageView rv_retag;
            private ImageView rv_swap;

            public MyViewHolder(View itemView) {
                super(itemView);
                swipe_layout = (SwipeRevealLayout) itemView.findViewById(R.id.swipe_layout);
                rv_song_name = (TextView) itemView.findViewById(R.id.rv_song_name);
                rv_ll = (SwipeRevealLayout) itemView.findViewById(R.id.click_layout);
                rv_song_detail = (TextView) itemView.findViewById(R.id.rv_song_detail);
                rv_retag = (ImageView) itemView.findViewById(R.id.rv_retag);
                rv_swap = (ImageView) itemView.findViewById(R.id.rv_swap);
                container_song_item = (LinearLayout) itemView.findViewById(R.id.container_song_item);
            }
        }
    }
}
