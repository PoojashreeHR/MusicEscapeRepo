package com.agiliztech.musicescape.activity;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.agiliztech.musicescape.R;
import com.agiliztech.musicescape.apiservices.AnalyseApiService;
import com.agiliztech.musicescape.apiservices.ApiService;
import com.agiliztech.musicescape.apiservices.SpotifyApiService;
import com.agiliztech.musicescape.database.DBHandler;
import com.agiliztech.musicescape.models.Song;
import com.agiliztech.musicescape.models.apimodels.BatchIdResponseModel;
import com.agiliztech.musicescape.models.apimodels.DeviceIdModel;
import com.agiliztech.musicescape.models.apimodels.ResponseSongPollModel;
import com.agiliztech.musicescape.models.apimodels.SongInfo;
import com.agiliztech.musicescape.models.apimodels.SongRequest;
import com.agiliztech.musicescape.models.apimodels.SpotifyInfo;
import com.agiliztech.musicescape.models.apimodels.SpotifyModelMain;
import com.agiliztech.musicescape.rest.ApiClient;
import com.agiliztech.musicescape.rest.ApiInterface;
import com.agiliztech.musicescape.utils.SongsManager;
import com.agiliztech.musicescape.utils.UtilityClass;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

public class MoodMappingActivity extends BaseMusicActivity implements
        View.OnClickListener {


    Typeface tf;
    SharedPreferences sp;
    //RecyclerView mRecyclerView;
    //RecyclerViewAdapter mAdapter;
    //SlidingUpPanelLayout slidingUpPanelLayout;
    ImageButton library;

    // Button musicButton;
    private boolean isPlaying = false;
    private static boolean isSongPlaying = false;
    Button testButton;
    TextView tv_aggressive;
    TextView tv_excited;
    TextView tv_happy;
    TextView tv_chilled;
    TextView tv_peaceful;
    TextView tv_bored;
    TextView tv_depressed;
    TextView tv_stressed;
    TextView mood_scanning;
    private ImageView dashboardButton, infoButton;
    private boolean newSongAdded = false;
    private boolean oldSongRemoved = false;

    private final String TAG = "MoodMappingActivity";
    DBHandler dbHandler;


    private BroadcastReceiver mServiceBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String songJson = intent.getStringExtra("songresponse");
            if (songJson.equals("")) {
                mood_scanning.setVisibility(View.GONE);
                testButton.setText("START");
            } else {
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
        }
    };

    private BroadcastReceiver mSpotifyServiceBroadCast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("ON RECIEVE CALLED ", " ON RECEIVED ");

            if (!UtilityClass.checkInternetConnectivity(MoodMappingActivity.this)) {
                testButton.setText("START");
                mood_scanning.setVisibility(View.GONE);
            } else {
                ArrayList<SpotifyInfo> spotifyInfos = dbHandler.getSongsWithServerIdAndSpotifyId();
                SpotifyModelMain spotifyModelMain = new SpotifyModelMain(UtilityClass.getDeviceId(MoodMappingActivity.this), spotifyInfos);
                new ScanAndAnalyseAsync().execute(spotifyModelMain);
                Log.e("ScanAndAnalyse", " : " + new Gson().toJson(spotifyModelMain));

            }
        }
    };

    private BroadcastReceiver mAnalyseServiceBroadCast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String songJson = intent.getStringExtra("songresponse_analysed");
            if(!songJson.equals("")){
                final ResponseSongPollModel model = new Gson().fromJson(songJson, ResponseSongPollModel.class);
                //Log.e("ON RECEIVE ANALYSED " ," : " + model.getSongs().get(0).getMood());
                ArrayList<SongInfo> info = new ArrayList<>();
                for (int i = 0; i < model.getSongs().size(); i++) {
                    info.add(model.getSongs().get(i));
                }
                dbHandler.updateSongsWithEnergyAndValence(info);
                tv_aggressive.setText(dbHandler.getMoodCount("aggressive") + "");
                tv_excited.setText(dbHandler.getMoodCount("excited") + "");
                tv_happy.setText(dbHandler.getMoodCount("happy") + "");
                tv_chilled.setText(dbHandler.getMoodCount("chilled") + "");
                tv_peaceful.setText(dbHandler.getMoodCount("peaceful") + "");
                tv_bored.setText(dbHandler.getMoodCount("bored") + "");
                tv_depressed.setText(dbHandler.getMoodCount("depressed") + "");
                tv_stressed.setText(dbHandler.getMoodCount("stressed") + "");
                testButton.setText(getResources().getString(R.string.start));
                mood_scanning.setText("Scanning");
                mood_scanning.setVisibility(View.GONE);
            }else{
                testButton.setText("START");
                mood_scanning.setVisibility(View.GONE);
            }

        }
    };
    SharedPreferences settings;
    private ArrayList<Song> totalSongs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_mood_mapping);
        totalSongs = new SongsManager(this).getSongList();

        settings = getSharedPreferences("MyPreference", 0);
        sp = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        dbHandler = new DBHandler(MoodMappingActivity.this);
        tf = Typeface.createFromAsset(getAssets(),
                "fonts/MontserratRegular.ttf");
        TextView tv = (TextView) findViewById(R.id.moodMapping);
        tv.setTypeface(tf);
        testButton = (Button) findViewById(R.id.button);
        testButton.setText(getResources().getString(R.string.start));
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPlaying) {
                    //  mPlayer.start();
                    testButton.setText(getResources().getString(R.string.pause));
                    mood_scanning.setVisibility(View.VISIBLE);
                    isPlaying = true;
                    ArrayList<com.agiliztech.musicescape.models.Song> originalList = totalSongs;
                    ArrayList<com.agiliztech.musicescape.models.Song> listFromDB = dbHandler.getAllSongsFromDB();
                    if (listFromDB.size() > 0) {
                        if (originalList.containsAll(listFromDB) && listFromDB.containsAll(originalList)) {
                            Log.e("SAME ", " SAME");
                            testButton.setText(getResources().getString(R.string.start));
                            isPlaying = false;
                            displayAlertDialog();
                        } else {
                            Log.e("NOT SAME", " NOT SAME");
                            new SyncSongsWithDB(MoodMappingActivity.this).execute(dbHandler);
                            //displayAlertDialog();
                        }
                    } else {
                        new SyncSongsWithDB(MoodMappingActivity.this).execute(dbHandler);
                        //displayAlertDialog();
                    }
                } else {
                    // mPlayer.stop();
                    testButton.setText(getResources().getString(R.string.start));
                    isPlaying = false;
                }
            }
        });

        tv_aggressive = (TextView) findViewById(R.id.tv_aggressive);
        tv_excited = (TextView) findViewById(R.id.tv_excited);
        tv_happy = (TextView) findViewById(R.id.tv_happy);
        tv_chilled = (TextView) findViewById(R.id.tv_chilled);
        tv_peaceful = (TextView) findViewById(R.id.tv_peaceful);
        tv_bored = (TextView) findViewById(R.id.tv_bored);
        tv_depressed = (TextView) findViewById(R.id.tv_depressed);
        tv_stressed = (TextView) findViewById(R.id.tv_stressed);
        mood_scanning = (TextView) findViewById(R.id.mood_scanning);
        tv_aggressive.setText(dbHandler.getMoodCount("aggressive") + "");
        tv_excited.setText(dbHandler.getMoodCount("excited") + "");
        tv_happy.setText(dbHandler.getMoodCount("happy") + "");
        tv_chilled.setText(dbHandler.getMoodCount("chilled") + "");
        tv_peaceful.setText(dbHandler.getMoodCount("peaceful") + "");
        tv_bored.setText(dbHandler.getMoodCount("bored") + "");
        tv_depressed.setText(dbHandler.getMoodCount("depressed") + "");
        tv_stressed.setText(dbHandler.getMoodCount("stressed") + "");

//        slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.slider_sliding_layout);
//        mRecyclerView = (RecyclerView) findViewById(R.id.rv_display_song_lists);


        library = (ImageButton) findViewById(R.id.libraryButton);
        library.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (settings.getBoolean("first_time_library", true)) {
                    Intent intent = new Intent(getApplicationContext(), SlidingImage.class);
                    intent.putExtra("library", "Library");
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), LibraryActivity.class);
                    startActivity(intent);
                }
            }
        });

        dashboardButton = (ImageView) findViewById(R.id.dashboardButton);
        dashboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (settings.getBoolean("is_first_time", true)) {
                    Intent intent = new Intent(getApplicationContext(), SlidingImage.class);
                    intent.putExtra("dashboard", "Dashboard");
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                    startActivity(intent);
                }
            }
        });

        infoButton = (ImageView) findViewById(R.id.infoButton);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AppInfoActivity.class);
                startActivity(intent);
                // checkInternetConnection();
            }
        });
//        ArrayList<com.agiliztech.musicescape.models.Song> list = dbHandler.getAllSongsFromDB();
//        if (list.size() > 0) {
//            mAdapter = new RecyclerViewAdapter(list, this, MoodMappingActivity.this);
//            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
//            mRecyclerView.setLayoutManager(mLayoutManager);
//            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
//            mRecyclerView.setAdapter(mAdapter);
//            mAdapter.notifyDataSetChanged();
//
//        }
        //play_music_seek_bar.setOnSeekBarChangeListener(this);
        //slidingUpPanelLayout.setScrollableView(mRecyclerView);
        //setup controller
        setController();

    }

//    private void updateProgressBar() {
//        handler.postDelayed(mUpdateTimeTask, 100);
//    }

//    private Runnable mUpdateTimeTask = new Runnable() {
//        @Override
//        public void run() {
//
//            if (musicSrv != null) {
//                long totalDuration = musicSrv.getDur();
//                long currDuration = musicSrv.getPosn();
//
//                int progress = (int) UtilityClass.getProgressPercentage(currDuration, totalDuration);
//                play_music_seek_bar.setProgress(progress);
//                handler.postDelayed(this, 100);
//            }
//        }
//    };

    //    private Runnable mUpdateTimeTask = new Runnable() {
//        @Override
//        public void run() {
//
//            if (musicSrv != null) {
//                long totalDuration = musicSrv.getDur();
//                long currDuration = musicSrv.getPosn();
//
//                int progress = (int) UtilityClass.getProgressPercentage(currDuration, totalDuration);
//                play_music_seek_bar.setProgress(progress);
//                handler.postDelayed(this, 100);
//            }
//        }
//    };
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

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mServiceBroadcast);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mSpotifyServiceBroadCast);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mAnalyseServiceBroadCast);
        super.onPause();


    }

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(mServiceBroadcast,
                new IntentFilter(ApiService.SERVICE_EVENT));
        LocalBroadcastManager.getInstance(this).registerReceiver(mSpotifyServiceBroadCast,
                new IntentFilter(SpotifyApiService.SERVICE_EVENT));
        LocalBroadcastManager.getInstance(this).registerReceiver(mAnalyseServiceBroadCast,
                new IntentFilter(AnalyseApiService.SERVICE_EVENT));


    }

    @Override
    protected void onStop() {
        //controller.hide();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mServiceBroadcast);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mSpotifyServiceBroadCast);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mAnalyseServiceBroadCast);
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

            case R.id.button:
                /*if (!isPlaying) {
                    //  mPlayer.start();
                    testButton.setText(getResources().getString(R.string.pause));
                    isPlaying = true;

                    ArrayList<com.agiliztech.musicescape.models.Song> originalList = totalSongs;
                    ArrayList<com.agiliztech.musicescape.models.Song> listFromDB = dbHandler.getAllSongsFromDB();
                    if (listFromDB.size() > 0) {
                        if (originalList.containsAll(listFromDB) && listFromDB.containsAll(originalList)) {
                            Log.e("SAME ", " SAME");
                            testButton.setText(getResources().getString(R.string.start));
                            isPlaying = false;
                            displayAlertDialog();
                        } else {
                            Log.e("NOT SAME", " NOT SAME");
                            new SyncSongsWithDB(this).execute(dbHandler);
                            //displayAlertDialog();
                        }
                    } else {
                        new SyncSongsWithDB(this).execute(dbHandler);
                        //displayAlertDialog();
                    }
                } else {
                    // mPlayer.stop();
                    testButton.setText(getResources().getString(R.string.start));
                    isPlaying = false;
                }*/
                break;
        }
    }

    /*public boolean checkInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo wifi = cm
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        android.net.NetworkInfo datac = cm
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return ((wifi != null & datac != null)
                && (wifi.isConnected() | datac.isConnected()));
        *//*{
            //connection is avlilable
            Toast.makeText(getApplicationContext(), "Network is available", Toast.LENGTH_LONG).show();
        } else {
            //no connection
            networkAlertDialog();
        }*//*
    }*/

    public void displayAlertDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // String text = "SCAN COMPLETED";
        //mood_scanning.setVisibility(View.GONE);
        builder.setTitle("Scan Completed")
                .setPositiveButton("Now", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        testButton.setText(getResources().getString(R.string.pause));
                        mood_scanning.setText(" Processing ");
                        mood_scanning.setVisibility(View.VISIBLE);
                        if (UtilityClass.checkInternetConnectivity(MoodMappingActivity.this)) {
                            new CallScanApiInAsync().execute(dbHandler);
                        } else {
                            Toast.makeText(MoodMappingActivity.this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();
                            testButton.setText(getResources().getString(R.string.start));
                        }
                    }
                })
                .setNegativeButton("Later", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        testButton.setText(getResources().getString(R.string.start));
                        mood_scanning.setVisibility(View.GONE);
                    }
                }).setMessage("Message").show();

    }


//    @Override
//    public void onBackPressed() {
//        if (slidingUpPanelLayout != null &&
//                (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED
//                        || slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
//            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
//        } else {
//            //  super.onBackPressed();
//            finish();
//
//        }
//    }

    public void alertDialog() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View alertDialogView = factory.inflate(R.layout.dialog_layout, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setView(alertDialogView);
        TextView scan_msg = (TextView) alertDialogView.findViewById(R.id.scan_completed);
        TextView text_dialog = (TextView) alertDialogView.findViewById(R.id.text_dialog);
        scan_msg.setTypeface(tf);
        text_dialog.setTypeface(tf);
        alertDialogView.findViewById(R.id.dismiss_dialog).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //your business logic
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public void networkAlertDialog() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View networkDialogView = factory.inflate(R.layout.network_dialog_layout, null);
        final AlertDialog networkDialog = new AlertDialog.Builder(this).create();
        networkDialog.setView(networkDialogView);
        TextView scan_msg = (TextView) networkDialogView.findViewById(R.id.nt_dialog_title);
        TextView text_dialog = (TextView) networkDialogView.findViewById(R.id.nt_text_dialog);
        scan_msg.setTypeface(tf);
        text_dialog.setTypeface(tf);
        networkDialogView.findViewById(R.id.nt_dismiss_dialog).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //your business logic
                networkDialog.dismiss();
            }
        });
        networkDialog.show();
    }

    class CallScanApiInAsync extends AsyncTask<DBHandler, Void, String> {

        @Override
        protected String doInBackground(DBHandler... params) {
            String batchId = "";
            String deviceId = UtilityClass.getDeviceId(MoodMappingActivity.this);
            ArrayList<SongRequest> listWithScanAndScanError = params[0].getSongsBasedOnWhereParam("scan", "scan_error");
            if (listWithScanAndScanError.size() > 0) {
                final DeviceIdModel model = new DeviceIdModel(deviceId, listWithScanAndScanError);
                Log.e(TAG, " SENDING DeviceIdModel Object (SCAN API) : " + new Gson().toJson(model));
                ApiInterface apiInterface = ApiClient.createService(ApiInterface.class, "RandyApp", "N1nj@R@nDy");
                Call<BatchIdResponseModel> calls = apiInterface.sendSongToServerToScan(model);

                try {
                    Response<BatchIdResponseModel> gettingBatchId = calls.execute();
                    if (gettingBatchId.body() != null) {
                        batchId = gettingBatchId.body().getBatchId();
                        Log.e(TAG, " Response From SCAN API (BATCH ID) : " + new Gson().toJson(model));
                    }
                    if (!gettingBatchId.isSuccessful() && gettingBatchId.errorBody() != null) {
                        Log.e("Error Handling", "HANDLE ERRORS 1 ");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return batchId;
            } else {
                return "";
            }

        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);

            if (!aVoid.equals("")) {
                if (UtilityClass.checkInternetConnectivity(MoodMappingActivity.this)) {
                    callService(aVoid);
                } else {
                    Toast.makeText(MoodMappingActivity.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (dbHandler.getSongsWithPendingStatus("pending").size() > 0) {
                    if (UtilityClass.checkInternetConnectivity(MoodMappingActivity.this)) {
                        ArrayList<String> songNames = dbHandler.getSongsWithPendingStatus("pending");
                        Log.e(TAG, " SONG NAMES SENT TO SpotifyApiService.java : " + songNames.toString());
                        //ArrayList<SpotifyInfo> songsIdSentFromServer = handler.getSongsIdSentFromServer();

                        Intent callSpotifyService = new Intent(MoodMappingActivity.this, SpotifyApiService.class);
                        callSpotifyService.putExtra("service_data", "passDataToService");
                        callSpotifyService.putStringArrayListExtra("spotifyList", songNames);
                        //callSpotifyService.putExtra("batchId", batchid);
                        startService(callSpotifyService);
                    } else {
                        Toast.makeText(MoodMappingActivity.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
                        testButton.setText(getResources().getString(R.string.start));
                    }
                } else {

                    //DISPLAY MESSAGE THAT ALL SONGS ARE SYNCED
                    testButton.setText(getResources().getString(R.string.start));
                    Log.e("Error Handling", "HANDLE ERRORS " + " onPostExecute() 1");
                }
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
            if(UtilityClass.checkInternetConnectivity(MoodMappingActivity.this)) {

                ApiInterface apiInterface = ApiClient.createService(ApiInterface.class, "RandyApp", "N1nj@R@nDy");
                Log.e(TAG, " PASSING SpotifyModelMain object to Analyse API : " + new Gson().toJson(params[0]));
                Call<BatchIdResponseModel> calls = apiInterface.analyseScanSongs(params[0]);

                try {
                    Response<BatchIdResponseModel> gettingBatchId = calls.execute();
                    if (gettingBatchId.body() != null) {
                        batchId = gettingBatchId.body().getBatchId();
                        Log.e(TAG, " RESPONSE FROM ANALYSE API (BATCH ID) : " + batchId);
                    } else {
                        //Toast.makeText(MoodMappingActivity.this, "Sorry!! Server is down right now", Toast.LENGTH_SHORT).show();
                        Log.e("Error Handling", "HANDLE ERRORS 2");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return batchId;
            }else{
                return batchId;
            }
            //return batchId;
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
                    testButton.setText("START");
                    mood_scanning.setVisibility(View.GONE);
                    Log.e("Error Handling", "HANDLE ERRORS " + " onPostExecute() 2");
                }
            }

        }
    }

    class SyncSongsWithDB extends AsyncTask<DBHandler, Void, Void> {

        //Button btn = (Button) findViewById(R.id.button);
        Context ssContext;

        public SyncSongsWithDB(Context ctx) {
            ssContext = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mood_scanning.setVisibility(View.VISIBLE);
            //btn.setText("START");
        }

        @Override
        protected Void doInBackground(DBHandler... params) {
            ArrayList<com.agiliztech.musicescape.models.Song> songs = new SongsManager(ssContext).getSongList();
            //songs = songList;
            ArrayList<com.agiliztech.musicescape.models.Song> dbList = songList;

            if (songs.equals(dbList)) {
                Log.e("EQUAL ", " BOTH LISTS ARE EQUAL IN CONTENT");
            } else {
                Log.e("NOT EQUAL ", " BOTH LISTS ARE NOT EQUAL IN CONTENT");
                if (dbList.size() > 0) {
                    //If New SongRequest added
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
            mood_scanning.setVisibility(View.GONE);
            isPlaying = false;
            //displayAlertDialog();

            setUpPlaylist();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean(getString(R.string.pref_previously_started), Boolean.TRUE);
            edit.apply();
            baseLayout.setVisibility(View.VISIBLE);
            contentFrame.setVisibility(View.VISIBLE);


        }

        public void storeSongsINDB(DBHandler dbHandler, ArrayList<com.agiliztech.musicescape.models.Song> models) {

            if (models.size() > 0) {
                dbHandler.addDeviceSongsToDB(models);
            }
        }

        public void removeSongFromDB(DBHandler dbHandler, com.agiliztech.musicescape.models.Song model) {
            dbHandler.removeDeviceSongsFromDB(model.getpID());
        }
    }
}