package com.agiliztech.musicescape.activity;

import android.Manifest;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.agiliztech.musicescape.R;
import com.agiliztech.musicescape.adapter.RecyclerViewAdapter;
import com.agiliztech.musicescape.apiservices.AnalyseApiService;
import com.agiliztech.musicescape.apiservices.ApiService;
import com.agiliztech.musicescape.apiservices.SpotifyApiService;
import com.agiliztech.musicescape.database.DBHandler;
import com.agiliztech.musicescape.models.SongsModel;
import com.agiliztech.musicescape.models.apimodels.BatchIdResponseModel;
import com.agiliztech.musicescape.models.apimodels.DeviceIdModel;
import com.agiliztech.musicescape.models.apimodels.ResponseSongPollModel;
import com.agiliztech.musicescape.models.apimodels.Song;
import com.agiliztech.musicescape.models.apimodels.SongInfo;
import com.agiliztech.musicescape.models.apimodels.SpotifyInfo;
import com.agiliztech.musicescape.models.apimodels.SpotifyModelMain;
import com.agiliztech.musicescape.musicservices.MusicService;
import com.agiliztech.musicescape.rest.ApiClient;
import com.agiliztech.musicescape.rest.ApiInterface;
import com.agiliztech.musicescape.utils.UtilityClass;
import com.google.gson.Gson;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

public class MoodMappingActivity extends BaseMusicActivity implements
        View.OnClickListener, SeekBar.OnSeekBarChangeListener, RecyclerViewAdapter.IClickListener {


    Typeface tf;
    SharedPreferences sp;
    RecyclerView mRecyclerView;
    RecyclerViewAdapter mAdapter;
    SlidingUpPanelLayout slidingUpPanelLayout;
    ImageButton library;
    private Handler handler = new Handler();
    // Button musicButton;
    private boolean isPlaying = false;
    private static boolean isSongPlaying = false;
    Button testButton;
    private ImageView dashboardButton, infoButton;
    private boolean newSongAdded = false;
    private boolean oldSongRemoved = false;

    private final String TAG = "MoodMappingActivity";
    DBHandler dbHandler;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String curSongJson = intent.getStringExtra("currentSong");
            SongsModel songsModel = new Gson().fromJson(curSongJson, SongsModel.class);
            tv_songname.setText(songsModel.getTitle());
            tv_song_detail.setText(songsModel.getArtist());

            SharedPreferences.Editor editor = sp.edit();
            editor.putString("song_name", songsModel.getTitle());
            editor.putString("song_detail", songsModel.getArtist());
            editor.apply();
        }
    };

    private BroadcastReceiver mServiceBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String songJson = intent.getStringExtra("songresponse");
            final ResponseSongPollModel model = new Gson().fromJson(songJson, ResponseSongPollModel.class);
            //Log.e(TAG, " PRINTING " + songJson);
            final DBHandler handler = new DBHandler(MoodMappingActivity.this);
            //stopService(new Intent(ApiService.SERVICE_EVENT));
            new Thread() {
                @Override
                public void run() {
                    for (int i = 0; i < model.getSongs().size(); i++) {
                        handler.updateSongDetails(model.getBatch(), model.getSongs().get(i).getClientId(),
                                model.getSongs().get(i).getEnergy(), model.getSongs().get(i).getValence(),
                                model.getSongs().get(i).getEchonestAnalysisStatus(),
                                model.getSongs().get(i).getId(),
                                model.getSongs().get(i).getSpotifyId());
                    }
                    ArrayList<String> songNames = handler.getSongsWithPendingStatus("pending");
                    Log.e(TAG, " SONG NAMES SENT TO SpotifyApiService.java : " + songNames.toString());
                    //ArrayList<SpotifyInfo> songsIdSentFromServer = handler.getSongsIdSentFromServer();

                    Intent callSpotifyService = new Intent(MoodMappingActivity.this, SpotifyApiService.class);
                    callSpotifyService.putExtra("service_data", "passDataToService");
                    callSpotifyService.putStringArrayListExtra("spotifyList", songNames);
                    //callSpotifyService.putExtra("batchId", batchid);
                    startService(callSpotifyService);
                }
            }.start();


        }
    };

    private BroadcastReceiver mSpotifyServiceBroadCast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("ON RECIEVE CALLED ", " ON RECEIVED ");

            ArrayList<SpotifyInfo> spotifyInfos = dbHandler.getSongsWithServerIdAndSpotifyId();
            SpotifyModelMain spotifyModelMain = new SpotifyModelMain(UtilityClass.getDeviceId(MoodMappingActivity.this), spotifyInfos);
            new ScanAndAnalyseAsync().execute(spotifyModelMain);

            testButton.setText(getResources().getString(R.string.start));
        }
    };

    private BroadcastReceiver mAnalyseServiceBroadCast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String songJson = intent.getStringExtra("songresponse_analysed");
            final ResponseSongPollModel model = new Gson().fromJson(songJson, ResponseSongPollModel.class);
            //Log.e("ON RECEIVE ANALYSED " ," : " + model.getSongs().get(0).getMood());
            ArrayList<SongInfo> info = new ArrayList<>();
            for(int i=0;i<model.getSongs().size();i++){
                info.add(model.getSongs().get(i));
            }
            dbHandler.updateSongsWithEnergyAndValence(info);

        }
    };
    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_mood_mapping);

        settings = getSharedPreferences("MyPreference", 0);
        sp = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        dbHandler = new DBHandler(MoodMappingActivity.this);
        tf = Typeface.createFromAsset(getAssets(),
                "fonts/MontserratRegular.ttf");
        TextView tv = (TextView) findViewById(R.id.moodMapping);
        tv.setTypeface(tf);
        testButton = (Button) findViewById(R.id.button);
        testButton.setText(getResources().getString(R.string.start));
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
        ImageView dashboardButton, infoButton;
        dashboardButton = (ImageView) findViewById(R.id.dashboardButton);
        dashboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (settings.getBoolean("is_first_time", true))
                {
                    Intent intent = new Intent(getApplicationContext(), SlidingImage.class);
                    intent.putExtra("dashboard","Dashboard");
                    startActivity(intent);
                }
                else
                {
                Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                startActivity(intent);}
            }
        });

        infoButton = (ImageView) findViewById(R.id.infoButton);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AppInfoActivity.class);
                startActivity(intent);
            }
        });

        ArrayList<SongsModel> list = dbHandler.getAllSongsFromDB();
        if (list.size() > 0) {
            mAdapter = new RecyclerViewAdapter(list, this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
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

            if(musicSrv!=null) {
                long totalDuration = musicSrv.getDur();
                long currDuration = musicSrv.getPosn();

                int progress = (int) UtilityClass.getProgressPercentage(currDuration, totalDuration);
                play_music_seek_bar.setProgress(progress);
                handler.postDelayed(this, 100);
            }
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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mServiceBroadcast);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mSpotifyServiceBroadCast);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mAnalyseServiceBroadCast);
        super.onPause();
        paused = true;

    }

    @Override
    protected void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(MusicService.SERVICE_EVENT));
        LocalBroadcastManager.getInstance(this).registerReceiver(mServiceBroadcast,
                new IntentFilter(ApiService.SERVICE_EVENT));
        LocalBroadcastManager.getInstance(this).registerReceiver(mSpotifyServiceBroadCast,
                new IntentFilter(SpotifyApiService.SERVICE_EVENT));
        LocalBroadcastManager.getInstance(this).registerReceiver(mAnalyseServiceBroadCast,
                new IntentFilter(AnalyseApiService.SERVICE_EVENT));
        super.onResume();
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
                Log.e("Music service ", " is  Not null");
            }
            ibPlayPause.setVisibility(View.GONE);
            btn_pause.setVisibility(View.VISIBLE);
        } else {
            ibPlayPause.setVisibility(View.VISIBLE);
            btn_pause.setVisibility(View.GONE);
        }
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
    protected void onStop() {
        //controller.hide();
        if (sp != null) {
            if(playbackPaused) {
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

    @Override
    protected void onDestroy() {
        // stopService(playIntent);
        //musicSrv = null;
        super.onDestroy();
      /*  if (sp != null) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("playbackpaused", "" + playbackPaused);
            editor.putString("song_id_sp", musicSrv.getSongId());
            editor.putString("song_position", "" + musicSrv.getPosn());
            editor.putString("song_name_sp", musicSrv.getSongName());
            editor.commit();
        }*/
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
                        //updateProgressBar();
                        btn_pause.setVisibility(View.VISIBLE);
                        ibPlayPause.setVisibility(View.GONE);
                    } else {
                        // songPicked();
                        //musicSrv.go();
                        //updateProgressBar();
                        if(sp.getString("song_name",null) != null){
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
            case R.id.button:
                if (!isPlaying) {
                    //  mPlayer.start();
                    testButton.setText(getResources().getString(R.string.pause));
                    isPlaying = true;

                    ArrayList<SongsModel> originalList = new ArrayList<>(songList);
                    ArrayList<SongsModel> listFromDB = dbHandler.getAllSongsFromDB();

                    if (listFromDB.size() > 0) {
                        if (originalList.containsAll(listFromDB)) {
                            Log.e("SAME ", " SAME");
                            testButton.setText(getResources().getString(R.string.start));
                            isPlaying = false;
                            displayAlertDialog();
                        } else {
                            Log.e("NOT SAME", " NOT SAME");
                            new SyncSongsWithDB().execute(dbHandler);
                            //displayAlertDialog();
                        }
                    } else {
                        new SyncSongsWithDB().execute(dbHandler);


                        //displayAlertDialog();
                    }
                } else {
                    // mPlayer.stop();
                    testButton.setText(getResources().getString(R.string.start));
                    isPlaying = false;
                }
                break;
        }
    }

    public void displayAlertDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
       // String text = "SCAN COMPLETED";
        builder.setTitle("Scan Completed")
                .setPositiveButton("Now", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        testButton.setText(getResources().getString(R.string.pause));

                        new CallScanApiInAsync().execute(dbHandler);
                    }
                })
                .setNegativeButton("Later", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        testButton.setText(getResources().getString(R.string.start));
                    }
                }).setMessage("Message").show();

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
        //updateProgressBar();
    }


    @Override
    public void playSelectedSong(int position, View view) {
        musicSrv.setSong(position);
        musicSrv.playSong();
        isSongPlaying = true;
        //updateProgressBar();
        ibPlayPause.setVisibility(View.GONE);
        btn_pause.setVisibility(View.VISIBLE);
       /* if (playbackPaused) {
            //setController();
            playbackPaused = false;
        }
        if (musicSrv.isPng()) {

        }*/
    }

    @Override
    public void onBackPressed() {
        if (slidingUpPanelLayout != null &&
                (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED
                        || slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
          //  super.onBackPressed();
            finish();

        }
    }

    public  void alertDialog()
    {   LayoutInflater factory = LayoutInflater.from(this);
        final View deleteDialogView = factory.inflate(R.layout.dialog_layout, null);
        final AlertDialog deleteDialog = new AlertDialog.Builder(this).create();
        deleteDialog.setView(deleteDialogView);
        TextView scan_msg = (TextView) deleteDialogView.findViewById(R.id.scan_completed);
        TextView text_dialog = (TextView) deleteDialogView.findViewById(R.id.text_dialog);
        scan_msg.setTypeface(tf);
        text_dialog.setTypeface(tf);
        deleteDialogView.findViewById(R.id.dismiss_dialog).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //your business logic
                deleteDialog.dismiss();
            }
        }); deleteDialog.show();
    }

    class CallScanApiInAsync extends AsyncTask<DBHandler, Void, String> {

        @Override
        protected String doInBackground(DBHandler... params) {
            String batchId = "";
            String deviceId = UtilityClass.getDeviceId(MoodMappingActivity.this);
            ArrayList<Song> listWithScanAndScanError = params[0].getSongsBasedOnWhereParam("scan", "scan_error");
            if(listWithScanAndScanError.size()>0) {
                final DeviceIdModel model = new DeviceIdModel(deviceId, listWithScanAndScanError);
                Log.e(TAG, " SENDING DeviceIdModel Object (SCAN API) : "+new Gson().toJson(model));
                ApiInterface apiInterface = ApiClient.createService(ApiInterface.class, "RandyApp", "N1nj@R@nDy");
                Call<BatchIdResponseModel> calls = apiInterface.sendSongToServerToScan(model);

                try {
                    Response<BatchIdResponseModel> gettingBatchId = calls.execute();
                    if (gettingBatchId.body() != null) {
                        batchId = gettingBatchId.body().getBatchId();
                        Log.e(TAG, " Response From SCAN API (BATCH ID) : "+new Gson().toJson(model));
                    }
                    if (!gettingBatchId.isSuccessful() && gettingBatchId.errorBody() != null) {
                        Log.e("Error Handling", "HANDLE ERRORS 1 ");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return batchId;
            }else{
                return "";
            }

        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);

            if (!aVoid.equals("")) {
                callService(aVoid);
            } else {
                //if()
                testButton.setText(getResources().getString(R.string.start));
                Log.e("Error Handling", "HANDLE ERRORS " + " onPostExecute() 1");
            }
        }
    }

    private void callService(String batchid) {
        Intent intent = new Intent(MoodMappingActivity.this, ApiService.class);
        intent.putExtra("service_data", "passDataToService");
        intent.putExtra("batchId", batchid);
        intent.putExtra("variable", "1");
        startService(intent);
    }


    /**
     * AsyncTask to hit the api for analyse, and get the batchId in response
     */
    class ScanAndAnalyseAsync extends AsyncTask<SpotifyModelMain, Void, String> {


        @Override
        protected String doInBackground(SpotifyModelMain... params) {

            String batchId = "";
            ApiInterface apiInterface = ApiClient.createService(ApiInterface.class, "RandyApp", "N1nj@R@nDy");
            Log.e(TAG," PASSING SpotifyModelMain object to Analyse API : "+ new Gson().toJson(params[0]));
            Call<BatchIdResponseModel> calls = apiInterface.analyseScanSongs(params[0]);

            try {
                Response<BatchIdResponseModel> gettingBatchId = calls.execute();
                if (gettingBatchId.body() != null) {
                    batchId = gettingBatchId.body().getBatchId();
                    Log.e(TAG," RESPONSE FROM ANALYSE API (BATCH ID) : " + batchId);
                } else {
                    //Toast.makeText(MoodMappingActivity.this, "Sorry!! Server is down right now", Toast.LENGTH_SHORT).show();
                    Log.e("Error Handling", "HANDLE ERRORS 2");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return batchId;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                if (!s.equals("")) {
                    Log.e("PRINTING batch id ", " ANALYSE BATCH ID " + s);
                    Intent intent = new Intent(MoodMappingActivity.this, AnalyseApiService.class);
                    intent.putExtra("service_data", "passDataToService");
                    intent.putExtra("batchId", s);
                    intent.putExtra("variable", "2");
                    startService(intent);
                } else {
                    Log.e("Error Handling", "HANDLE ERRORS " + " onPostExecute() 2");
                }
            }

        }
    }

    class SyncSongsWithDB extends AsyncTask<DBHandler, Void, Void> {

        //Button btn = (Button) findViewById(R.id.button);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //btn.setText("START");
        }

        @Override
        protected Void doInBackground(DBHandler... params) {
            ArrayList<SongsModel> songs = new ArrayList<>(songList);
            //songs = songList;
            ArrayList<SongsModel> dbList = params[0].getAllSongsFromDB();

            if (songs.equals(dbList)) {
                Log.e("EQUAL ", " BOTH LISTS ARE EQUAL IN CONTENT");
            } else {
                Log.e("NOT EQUAL ", " BOTH LISTS ARE NOT EQUAL IN CONTENT");
                if (dbList.size() > 0) {
                    //If New Song added
                    if ((songs.size() > dbList.size())) {
                        songs.removeAll(dbList);
                        for (int i = 0; i < songs.size(); i++) {
                            storeSongsINDB(params[0], songs);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    displayAlertDialog();
                                }
                            });

                        }
                    }
                    //If Songs Deleted
                    else if (songs.size() < dbList.size()) {
                        dbList.removeAll(songs);
                        for (int i = 0; i < dbList.size(); i++) {
                            removeSongFromDB(params[0], dbList.get(i));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    displayAlertDialog();
                                }
                            });
                        }
                    } else {
                        Log.e("UP TO DATE", " ");
                    }
                } else {
                    storeSongsINDB(params[0], songs);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            displayAlertDialog();
                        }
                    });
                }
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            testButton.setText(getResources().getString(R.string.start));
            isPlaying = false;
            //displayAlertDialog();

            ArrayList<SongsModel> list = dbHandler.getAllSongsFromDB();
            if (list.size() > 0) {
                mAdapter = new RecyclerViewAdapter(list, MoodMappingActivity.this);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean(getString(R.string.pref_previously_started), Boolean.TRUE);
            edit.apply();
            baseLayout.setVisibility(View.VISIBLE);
            contentFrame.setVisibility(View.VISIBLE);
            play_music_seek_bar.setOnSeekBarChangeListener(MoodMappingActivity.this);
            slidingUpPanelLayout.setScrollableView(mRecyclerView);
        }

        public void storeSongsINDB(DBHandler dbHandler, ArrayList<SongsModel> models) {

            if (models.size() > 0) {
                dbHandler.addDeviceSongsToDB(models);
            }
        }

        public void removeSongFromDB(DBHandler dbHandler, SongsModel model) {
            dbHandler.removeDeviceSongsFromDB(model.getId());
        }
    }
}