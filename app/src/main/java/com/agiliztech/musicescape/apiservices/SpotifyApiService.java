package com.agiliztech.musicescape.apiservices;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.agiliztech.musicescape.database.DBHandler;
import com.agiliztech.musicescape.models.spotifymodels.SpotifyMain;
import com.agiliztech.musicescape.rest.SpotifyApiClient;
import com.agiliztech.musicescape.rest.SpotifyApiInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

/**
 * Created by Asif on 23-08-2016.
 */
public class SpotifyApiService extends Service {
    public static final String SERVICE_EVENT = "com.agiliztech.musicescape.musicservices.MusicService" + "_sportify_event_response";

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
                for (int i = 0; i < songNamesList.size(); i++) {
                    String name = songNamesList.get(i);
                    if (name.contains("-")) {

                        String originalName = getOriginalSongName(name);
                        Map<String, String> data = new HashMap<>();
                        data.put("query", originalName);
                        data.put("offset", "0");
                        data.put("limit", "1");
                        data.put("type", "track");

                        SpotifyApiInterface apiInterface = SpotifyApiClient.createService(SpotifyApiInterface.class);
                        Call<SpotifyMain> call = apiInterface.spotifyApiCalling(data);
                        try {
                            SpotifyMain main = call.execute().body();
                            if (main != null) {
                                if (main.getTracks().getItems().size() > 0) {
                                    Log.e(" PRINTING ", " SPOTIFY ID " + main.getTracks().getItems().get(0).getId());
                                    String spotifyId = main.getTracks().getItems().get(0).getId();

                                    handler.updateSongWithSpotifyID(spotifyId, name);
                                } else {
                                    handler.updateSongStatusForSpotifyError(name);
                                    Log.e("NOT MATCHED ", " NOT MATCHING :  " + name);
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                }
                Intent sendingIntent = new Intent(SERVICE_EVENT);
                LocalBroadcastManager.getInstance(SpotifyApiService.this).sendBroadcast(sendingIntent);
                stopSelf();
            }
        }.start();


        return START_STICKY;
    }

    private String getOriginalSongName(String name) {
        String[] constantArray = new String[]{ "male", "female", "remix" };
        String newName = name;
        String[] arrayName = new String[0];
        if (Character.isDigit(newName.charAt(0))) {
            newName = newName.replaceAll("\\d", "");
        }
        if (newName.contains("-")) {
            arrayName = newName.split("-");
        }
        if (arrayName[0].contains("(")) {
            arrayName = arrayName[0].split("\\(");
        }
        if (arrayName[0].contains("[")) {
            arrayName = arrayName[0].split("\\[");
        }
        return arrayName[0];
    }


}