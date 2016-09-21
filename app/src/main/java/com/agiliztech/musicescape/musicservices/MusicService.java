package com.agiliztech.musicescape.musicservices;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.agiliztech.musicescape.R;
import com.agiliztech.musicescape.activity.MoodMappingActivity;
import com.agiliztech.musicescape.activity.NewDashboardActivity;
import com.agiliztech.musicescape.journey.JourneyService;
import com.agiliztech.musicescape.journey.JourneySong;
import com.agiliztech.musicescape.models.Artist;
import com.agiliztech.musicescape.models.JourneySession;
import com.agiliztech.musicescape.models.Song;
import com.agiliztech.musicescape.utils.Global;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    public static final String SERVICE_EVENT = "com.agiliztech.musicescape.musicservices.MusicService" + "_event";
    //media player
    private MediaPlayer player;
    //song list
    private ArrayList<Song> songs;
    //current position
    private int songPosn;
    //binder
    private final IBinder musicBind = new MusicBinder();
    //title of current song
    private String songTitle = "";
    private String songDetail = "";
    private String songId = "";
    //notification id
    private static final int NOTIFY_ID = 1;
    //shuffle flag and random
    private boolean shuffle = false;
    private boolean noRepeatSong = false;
    private boolean repeatSingleSong = false;
    private boolean repeatPlayList = false;
    private Random rand;

    AudioManager am;
    TelephonyManager mgr;
    PhoneStateListener phoneStateListener;
    public static boolean isNextButtonClicked = false;
    int duration;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {

        //create the service
        super.onCreate();
        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                if (state == TelephonyManager.CALL_STATE_RINGING) {
                    //Incoming call: Pause music

                    pausePlayer();
                } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                    //Not in call: Play music
                    go();
                } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    //A call is dialing, active or on hold
                    pausePlayer();
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };

        if (mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
        //initialize position
        songPosn = 0;
        //random
        rand = new Random();
        //create player
        player = new MediaPlayer();
        //initialize
        initMusicPlayer();
    }

    public void initMediaPlayer() {
        player = new MediaPlayer();
    }

    public void initMusicPlayer() {
        //set player properties
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //set listeners
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    //pass song list
    public void setList(ArrayList<Song> theSongs) {
        songs = theSongs;
        Global.currentSongList = songs;
    }

    public void playCurrentSession() {
        JourneySession session = JourneyService.getInstance(this).getCurrentSession();
        List<JourneySong> journeySongList = session.getSongs();

        ArrayList<Song> songList = new ArrayList<>();
        if (journeySongList == null) {
            return;
        }
        for (int i = 0; i < journeySongList.size(); i++) {
            songList.add(journeySongList.get(i).getSong());
        }


        //Collections.reverse(songList);
        setList(songList);
    }

    public void setSongPosn(int songPosn) {
        this.songPosn = songPosn;
    }

    //binder
    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    //activity will bind to service
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    //release resources when unbind
    @Override
    public boolean onUnbind(Intent intent) {
        /*player.stop();
        player.release();*/
        return false;
    }

    Song playSong;

    public Song getCurrentPlayed() {
        return playSong;
    }

    AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                // Pause playback
                player.pause();
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                // Resume playback
                player.start();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                //am.unregisterMediaButtonEventReceiver(RemoteControlReceiver);
                am.abandonAudioFocus(afChangeListener);
                player.pause();
                // Stop playback
            }
        }
    };

    //play a song
    public void playSong() {
        //play
        int result = am.requestAudioFocus(afChangeListener,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            player.reset();
            //get song
            playSong = songs.get(songPosn);
            //get title
            songTitle = playSong.getSongName();
            songDetail = handleNullArtist(playSong.getArtist());
            //get id
            long currSong = playSong.getpID();
            //set uri
            Uri trackUri = ContentUris.withAppendedId(
                    android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    currSong);
            //set the data source
            try {

                player.setDataSource(getApplicationContext(), trackUri);
                Intent intent = new Intent(SERVICE_EVENT);
                intent.putExtra("currentSong", new Gson().toJson(playSong));
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            } catch (Exception e) {
                Log.e("MUSIC SERVICE", "Error setting data source", e);
                tryInternalStorage(currSong);
            }
            try {
                player.prepareAsync();
            } catch (Exception e) {

            }
        }

    }

    private String handleNullArtist(Artist artist) {
        if (artist == null)
            return "Unknown";
        if (artist.getArtistName() == null) {
            return "Unknown";
        } else {
            return artist.getArtistName();
        }
    }

    private void tryInternalStorage(long songid) {
        Uri trackUri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.INTERNAL_CONTENT_URI,
                songid);
        try {
            player.setDataSource(getApplicationContext(), trackUri);
            player.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //set the song
    public void setSong(int songIndex) {
        songPosn = songIndex;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //check if playback has reached the end of a track

        if (isNextButtonClicked) {
            if (player.getCurrentPosition() > 0) {
                mp.reset();
                playNext();
                Intent intent = new Intent(SERVICE_EVENT);
                intent.putExtra("currentSong", new Gson().toJson(playSong));
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            }
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.v("MUSIC PLAYER", "Playback Error");
        mp.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //start playback
        duration = player.getDuration();
        mp.start();
        showNotification();
        //notification

    }

    public void showNotification(){
        Intent notIntent = new Intent(this, NewDashboardActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(NewDashboardActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(notIntent);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText(songTitle);
        Notification not = builder.build();
        startForeground(NOTIFY_ID, not);
    }

    public String getSongName() {
        return songTitle;
    }

    public String getSongDetail() {
        return songDetail;
    }

    public String getSongId() {
        return songId;
    }

    //playback methods
    public int getPosn() {
        if (player == null)
            return 0;
        return player.getCurrentPosition();
    }

    public int getDur() {
        if (player == null)
            return 0;
        return duration;
    }

    public boolean isPng() {
        return player.isPlaying();
    }

    public void pausePlayer() {
        if (player != null)
            player.pause();
    }

    public void stopPlayer() {
        if (player != null)
            player.stop();
    }

    public void seek(int posn) {
        if (player != null && player.isPlaying())
            player.seekTo(posn);
    }

    public void go() {
        player.start();
    }

    //skip to previous track
    public void playPrev() {
        songPosn--;
        if (songPosn < 0) songPosn = songs.size() - 1;
        playSong();
    }

    //skip to next
    public void playNext() {


        if (isNextButtonClicked) {
            if (shuffle) {
                int newSong = songPosn;
                while (newSong == songPosn) {
                    newSong = rand.nextInt(songs.size());
                }
                songPosn = newSong;
                playSong();
            } else if (noRepeatSong) {
                songPosn++;
                if (songPosn >= songs.size()) {
                    //songPosn = 0;
                    player.stop();
                } else {
                    playSong();
                }
            } else if (repeatSingleSong) {
                //songPosn = songPosn;
                playSong();
            } else if (repeatPlayList) {
                songPosn++;
                if (songPosn >= songs.size()) songPosn = 0;
                playSong();
            } else {
           /* songPosn++;
            if (songPosn >= songs.size()) songPosn = 0;*/
            }
        }
    }

    public void killService() {
        if (mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
        stopForeground(true);
        stopSelf();

    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }

    //toggle shuffle
    public void setShuffle() {
        if (shuffle) shuffle = false;
        else shuffle = true;
    }

    public void setNoRepeat(boolean setNoRepeat) {
        noRepeatSong = setNoRepeat;
        repeatPlayList = false;
        repeatSingleSong = false;
    }

    public void setRepeatPlayList(boolean setRepeatPlaylist) {
        repeatPlayList = setRepeatPlaylist;
        noRepeatSong = false;
        repeatSingleSong = false;
    }

    public void setRepeatSingleSong(boolean setRepeat) {
        repeatSingleSong = setRepeat;
        noRepeatSong = false;
        repeatPlayList = false;
    }

    public int getSongPosn(){
        return songPosn;
    }


}
