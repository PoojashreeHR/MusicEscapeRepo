package com.agiliztech.musicescape.apiservices;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.agiliztech.musicescape.database.DBHandler;
import com.agiliztech.musicescape.models.apimodels.SpotifyInfo;
import com.agiliztech.musicescape.models.spotifymodels.SpotifyMain;
import com.agiliztech.musicescape.rest.SpotifyApiClient;
import com.agiliztech.musicescape.rest.SpotifyApiInterface;
import com.agiliztech.musicescape.utils.Global;
import com.agiliztech.musicescape.utils.UtilityClass;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Headers;
import retrofit2.Call;

/**
 * Created by Asif on 23-08-2016.
 */
public class SpotifyApiService extends Service {
    public static final String SERVICE_EVENT = "com.agiliztech.musicescape.musicservices.MusicService" + "_sportify_event_response";
    DBHandler handler;
    private String TAG = "SpotifyApiService.java";
    private int errorCount = 0;
    private boolean sentToAnalyseOnce;
    private AtomicBoolean paused = new AtomicBoolean();
    SharedPreferences sharedPreferences;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        handler.close();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, final int flags, int startId) {
        if (intent == null) {
            return 0;
        }
        final ArrayList<String> songNamesList = intent.getStringArrayListExtra("spotifyList");
        final SpotifyApiInterface apiInterface = SpotifyApiClient.createService(SpotifyApiInterface.class);
        handler = new DBHandler(getBaseContext());
        sharedPreferences = getSharedPreferences("db_prefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("size_passed",""+songNamesList.size());
        editor.putString("size_from_db",""+handler.getExceptAnalysedCount());
        editor.apply();
        new Thread() {
            @Override
            public void run() {
                int sentRows = 0;
                int sizeOfLoop = 0;

               /* Intent pendingIntent = new Intent(SERVICE_EVENT);
                LocalBroadcastManager.getInstance(SpotifyApiService.this).sendBroadcast(pendingIntent);*/
                // while (true) {
                for (int i = 0; i < songNamesList.size(); i++) {
                    if (UtilityClass.checkInternetConnectivity(SpotifyApiService.this) && !Global.HALT_API) {
                        String name = songNamesList.get(i);
                        //if (name.contains("-")) {
                        sizeOfLoop = i;
                        String originalName = getOriginalSongName(name);
                        Map<String, String> data = new HashMap<>();
                        data.put("query", originalName);
                        data.put("offset", "0");
                        data.put("limit", "1");
                        data.put("type", "track");

                        Intent setProcessingIntent = new Intent(SET_PROCESSING_EVEENT);
                        setProcessingIntent.putExtra("processing_count", "" + i);
                        LocalBroadcastManager.getInstance(SpotifyApiService.this).sendBroadcast(setProcessingIntent);

                        Call<SpotifyMain> call = apiInterface.spotifyApiCalling(data);
                        try {
                            Log.e(TAG, " SENDING QUERY TO SPOTIFY API : " + data.toString());
                            retrofit2.Response<SpotifyMain> response = call.execute();
                            Headers headers = response.headers();
                            //String strHeader = headers.toString();
                            Log.e(TAG, " PRINTING HEADERS  " + headers.get("Retry-After") + "\n" +
                                    "PRINTING RESPONSE CODE " + response.code());
                            if (response.code() == 200) {
                                SpotifyMain main = response.body();
                                if (main != null) {
                                    if (main.getTracks().getItems().size() > 0) {
                                        Log.e(TAG, " SPOTIFY ID " + main.getTracks().getItems().get(0).getId());
                                        String spotifyId = main.getTracks().getItems().get(0).getId();
                                        Log.e(TAG, " IF SPOTIFY ID FOUND THEN STORE IN DB  : " + spotifyId);
                                        handler.updateSongWithSpotifyID(spotifyId, name);
                                        int identifiedCount = handler.getRowCount();

                                        if (identifiedCount < 100 && sizeOfLoop == songNamesList.size() - 1) {
                                            Log.e(TAG, " PRINTING if (identifiedCount < 100 && sizeOfLoop == songNamesList.size()) : " + i);
                                            sendToAnalyseAPI();
                                            stopSelf();
                                            break;
                                        } else if (identifiedCount - sentRows >= 100 || (identifiedCount - sentRows < 100 && sizeOfLoop == songNamesList.size() - 1)) {
                                            //globalRowCounter = globalRowCounter + 1;
                                            if (sizeOfLoop != songNamesList.size() - 1) {
                                                sendToAnalyseAPI();
                                                Log.e(TAG, " PRINTING identifiedCount - sentRows >= 100 || (identifiedCount -sentRows <100 && sizeOfLoop==songNamesList.size())");
                                                Log.e(TAG, " PRINTING sizeOfLoop != songNamesList.size()" + i);
                                            } else if (sizeOfLoop == songNamesList.size() - 1) {
                                                Log.e(TAG, " PRINTING sizeOfLoop == songNamesList.size()" + i);
                                                sendToAnalyseAPI();
                                                //break;
                                            }
                                        }
                                    } else {
                                        if (sizeOfLoop == songNamesList.size() - 1) {
                                            handler.updateSongStatusForSpotifyError(name);
                                            sendToAnalyseAPI();
                                            break;
                                        }
                                        handler.updateSongStatusForSpotifyError(name);
                                        errorCount++;
                                        //Log.e("NOT MATCHED ", " NOT MATCHING :  " + name);
                                        Log.e(TAG, " IF SPOTIFY ID NOT FOUND THEN STORE IN DB  : ORIGINAL NAME : " + name + "\n NEW NAME : " + originalName);
                                    }
                                }
                            } else if (response.code() == 429) {
                                if (response.headers().get("Retry-After") != null) {
                                    Log.e(TAG, " SPOTIFY ERROR" + response.code());
                                    Log.e(TAG, " , WAIT FOR SOME TIME  : " + response.headers().get("Retry-After"));
                                    long valueToWait = Long.parseLong(response.headers().get("Retry-After"));
                                    try {
                                        long secondToMilis = (valueToWait + 3) % 60;
                                        Thread.sleep(secondToMilis);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (Global.HALT_API && !Global.CONTINUE_API) {
                        try {
                            synchronized (this) {
                                //Toast.makeText(SpotifyApiService.this, "You Paused the Action", Toast.LENGTH_SHORT).show();
                                Global.HALT_API = false;
                                Global.CONTINUE_API = true;
                                this.wait();

                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                           /* Toast.makeText(SpotifyApiService.this, "You Paused the Action", Toast.LENGTH_SHORT).show();
                            Global.HALT_API = false;*/
                    } else if (!Global.HALT_API && Global.CONTINUE_API) {
                        synchronized (this) {
                            this.start();
                            this.notifyAll();
                        }
                    } else {
//                        Toast.makeText(SpotifyApiService.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
                        Intent sendingIntent = new Intent(SERVICE_EVENT);
                        LocalBroadcastManager.getInstance(SpotifyApiService.this).sendBroadcast(sendingIntent);
                        break;
                    }

                }
                // }
                if (errorCount == songNamesList.size()) {
                    //rare case where all spotify calls are empty
                    broadcastEvent();
                }
                if (!sentToAnalyseOnce) {
                    broadcastEvent();
                }
                stopSelf();
            }
            // }
        }.start();
        return START_STICKY;
    }

    private void broadcastEvent() {
        Intent sendingIntent = new Intent(SERVICE_EVENT);
        LocalBroadcastManager.getInstance(SpotifyApiService.this).sendBroadcast(sendingIntent);
    }

    public void sendToAnalyseAPI() {
        sentToAnalyseOnce = true;
        //ArrayList<SpotifyInfo> spotifyInfos = handler.getSongsWithServerIdAndSpotifyId();

        // Send TO ANALYSE
        Intent sendingIntent = new Intent(SERVICE_EVENT);
        LocalBroadcastManager.getInstance(SpotifyApiService.this).sendBroadcast(sendingIntent);

    }


    private String getOriginalSongName(String name) {
        String[] constantArray = new String[]{"male", "female", "remix"};
        String newName = name;
        String[] arrayName = new String[]{name};
        boolean nameChanged = false;
        if (Character.isDigit(newName.charAt(0))) {
            newName = newName.replaceAll("\\d", "");
            nameChanged = true;
        }
        if (newName.contains("_")) {
            arrayName = newName.split("_");
            nameChanged = true;
        }
        if (newName.contains("-")) {
            arrayName = newName.split("-");
            nameChanged = true;
        }
        if(arrayName[0].length()>1){
            // do nothing
        }else{
            arrayName[0] = arrayName[1];
        }
        if (arrayName[0].contains("(")) {
            arrayName = arrayName[0].split("\\(");
            nameChanged = true;
        }
        if (arrayName[0].contains("[")) {
            arrayName = arrayName[0].split("\\[");
            nameChanged = true;
        }
        if (nameChanged) {
            return arrayName[0];
        } else {
            return newName;
        }
        // return arrayName[0];
    }

    public static final String SET_PROCESSING_EVEENT = "com.agiliztech.musicescape.musicservices.MusicService" + "_sportify_event_response_processing";


}
