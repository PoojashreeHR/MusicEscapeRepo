package com.agiliztech.musicescape.apiservices;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.agiliztech.musicescape.database.DBHandler;
import com.agiliztech.musicescape.models.apimodels.BatchIdResponseModel;
import com.agiliztech.musicescape.models.apimodels.SpotifyInfo;
import com.agiliztech.musicescape.models.apimodels.SpotifyModelMain;
import com.agiliztech.musicescape.models.spotifymodels.SpotifyMain;
import com.agiliztech.musicescape.rest.ApiClient;
import com.agiliztech.musicescape.rest.ApiInterface;
import com.agiliztech.musicescape.rest.SpotifyApiClient;
import com.agiliztech.musicescape.rest.SpotifyApiInterface;
import com.agiliztech.musicescape.utils.UtilityClass;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Asif on 23-08-2016.
 */
public class SpotifyApiService extends Service {
    public static final String SERVICE_EVENT = "com.agiliztech.musicescape.musicservices.MusicService" + "_sportify_event_response";

    private String TAG = "SpotifyApiService.java";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final ArrayList<String> songNamesList = intent.getStringArrayListExtra("spotifyList");

        new Thread() {
            @Override
            public void run() {
                DBHandler handler = new DBHandler(getBaseContext());
                int globalRowCounter = 0;
                boolean exhausted = false;
                int globalCount = 0;
                int sentRows = 0;
                int sizeOfLoop = 0;
                for (int i = 0; i < songNamesList.size(); i++) {
                    String name = songNamesList.get(i);
                    //if (name.contains("-")) {
                    sizeOfLoop = i;
                    String originalName = getOriginalSongName(name);
                    Map<String, String> data = new HashMap<>();
                    data.put("query", originalName);
                    data.put("offset", "0");
                    data.put("limit", "1");
                    data.put("type", "track");

                    SpotifyApiInterface apiInterface = SpotifyApiClient.createService(SpotifyApiInterface.class);
                    Call<SpotifyMain> call = apiInterface.spotifyApiCalling(data);
                    try {
                        Log.e(TAG, " SENDING QUERY TO SPOTIFY API : " + data.toString());
                        SpotifyMain main = call.execute().body();
                        if (main != null) {
                            if (main.getTracks().getItems().size() > 0) {
                                Log.e(" PRINTING ", " SPOTIFY ID " + main.getTracks().getItems().get(0).getId());
                                String spotifyId = main.getTracks().getItems().get(0).getId();
                                Log.e(TAG, " IF SPOTIFY ID FOUND THEN STORE IN DB  : " + spotifyId);
                                handler.updateSongWithSpotifyID(spotifyId, name);
                                int identifiedCount = handler.getRowCount();


                                //globalRowCounter = i + 1;
                                ArrayList<SpotifyInfo> spotifyInfos = handler.getSongsWithServerIdAndSpotifyId();
                                String deviceId = UtilityClass.getDeviceId(SpotifyApiService.this);
                                SpotifyModelMain spotifyModelMain = new SpotifyModelMain(deviceId, spotifyInfos);

                                if (identifiedCount < 100 && sizeOfLoop == songNamesList.size()) {
                                    Log.e(TAG," PRINTING if (identifiedCount < 100 && sizeOfLoop == songNamesList.size()) : " + i);
                                    sendToAnalyseAPI();
                                }
                                else if (identifiedCount - sentRows >= 100 || (identifiedCount -sentRows <100 && sizeOfLoop==songNamesList.size())) {
                                    //globalRowCounter = globalRowCounter + 1;
                                    if (sizeOfLoop != songNamesList.size()) {
                                        Log.e(TAG," PRINTING identifiedCount - sentRows >= 100 || (identifiedCount -sentRows <100 && sizeOfLoop==songNamesList.size())");
                                        Log.e(TAG," PRINTING sizeOfLoop != songNamesList.size()" +i);
                                    } else if (sizeOfLoop == songNamesList.size()) {
                                        Log.e(TAG," PRINTING sizeOfLoop == songNamesList.size()" +i);
                                        /*String batchId = "";
                                        ApiInterface analyseApiInterface = ApiClient.createService(ApiInterface.class, "RandyApp", "N1nj@R@nDy");
                                        Call<BatchIdResponseModel> calls = analyseApiInterface.analyseScanSongs(spotifyModelMain);
                                        try {
                                            Response<BatchIdResponseModel> gettingBatchId = calls.execute();
                                            sentRows = sentRows + 100;
                                            if (gettingBatchId.body() != null) {
                                                batchId = gettingBatchId.body().getBatchId();
                                                for (int j = 0; j < spotifyInfos.size(); j++) {
                                                    spotifyInfos.remove(j);
                                                }
                                                globalCount = 0;
                                                Log.e(TAG, " RESPONSE FROM ANALYSE API (BATCH ID) : " + batchId);
                                            } else {
                                                //Toast.makeText(MoodMappingActivity.this, "Sorry!! Server is down right now", Toast.LENGTH_SHORT).show();
                                                Log.e("Error Handling", "HANDLE ERRORS 2");
                                            }

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }*/
                                    }

                                }
                            } else {
                                handler.updateSongStatusForSpotifyError(name);
                                //Log.e("NOT MATCHED ", " NOT MATCHING :  " + name);
                                Log.e(TAG, " IF SPOTIFY ID NOT FOUND THEN STORE IN DB  : ORIGINAL NAME : " + name + "\n NEW NAME : " + originalName);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                // }
                Intent sendingIntent = new Intent(SERVICE_EVENT);
                //LocalBroadcastManager.getInstance(SpotifyApiService.this).sendBroadcast(sendingIntent);
                //stopSelf();
            }
        }.start();


        return START_STICKY;
    }

    public void sendToAnalyseAPI(){

        /*ArrayList<SpotifyInfo> spotifyInfos = handler.getSongsWithServerIdAndSpotifyId();
        String deviceId = UtilityClass.getDeviceId(SpotifyApiService.this);
        SpotifyModelMain spotifyModelMain = new SpotifyModelMain(deviceId, spotifyInfos);
        String batchId = "";
        ApiInterface analyseApiInterface = ApiClient.createService(ApiInterface.class, "RandyApp", "N1nj@R@nDy");
        Call<BatchIdResponseModel> calls = analyseApiInterface.analyseScanSongs(spotifyModelMain);
        try {
            Response<BatchIdResponseModel> gettingBatchId = calls.execute();
            //sentRows = sentRows + 100;
            if (gettingBatchId.body() != null) {
                batchId = gettingBatchId.body().getBatchId();
                for (int j = 0; j < spotifyInfos.size(); j++) {
                    spotifyInfos.remove(j);
                }
                Log.e(TAG, " RESPONSE FROM ANALYSE API (BATCH ID) : " + batchId);
            } else {
                //Toast.makeText(MoodMappingActivity.this, "Sorry!! Server is down right now", Toast.LENGTH_SHORT).show();
                Log.e("Error Handling", "HANDLE ERRORS 2");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }*/
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
        if (newName.contains("-")) {
            arrayName = newName.split("-");
            nameChanged = true;
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


}
