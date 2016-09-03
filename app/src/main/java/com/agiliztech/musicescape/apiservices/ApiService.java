package com.agiliztech.musicescape.apiservices;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.agiliztech.musicescape.models.apimodels.ResponseSongPollModel;
import com.agiliztech.musicescape.rest.ApiClient;
import com.agiliztech.musicescape.rest.ApiInterface;
import com.agiliztech.musicescape.utils.UtilityClass;
import com.google.gson.Gson;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Asif on 22-08-2016.
 */
public class ApiService extends Service {
    public static final String SERVICE_EVENT = "com.agiliztech.musicescape.musicservices.MusicService" + "_event_response";
    private CountDownLatch latch;
    private ResponseSongPollModel responseSongPollModel;
    private String TAG = "ApiService.java";
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String batchIds = intent.getStringExtra("batchId");
        final String variable = intent.getStringExtra("variable");
        Log.e("batchId", batchIds);
        final String batchId = batchIds;

        new Thread() {
            @Override
            public void run() {

                while (true) {
                    if (UtilityClass.checkInternetConnectivity(ApiService.this)) {
                        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class, "RandyApp", "N1nj@R@nDy");
                        Call<ResponseSongPollModel> call1 = apiInterface.pollSongFromServer(batchIds);
                        Log.e(TAG, " SENDING BATCH ID TO SCAN API (POLL ) : " + batchIds);
                        latch = new CountDownLatch(1);
                        call1.enqueue(new Callback<ResponseSongPollModel>() {
                            @Override
                            public void onResponse(Call<ResponseSongPollModel> call, Response<ResponseSongPollModel> response) {

                                if (response.isSuccessful()) {
                                    Log.e("RESPONSE SUCCESS ", new Gson().toJson(response.body()));
                                    responseSongPollModel = response.body();
                                    latch.countDown();
                                    // responseSongPollModel.setSongs(new ArrayList<>(Arrays.asList(responseSongPollModel.getSongs().get(0))));

                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseSongPollModel> call, Throwable t) {
                                Log.e("RESPONSE ERROR ", "" + t.getMessage());
                                latch.countDown();
                            }
                        });

                        try {
                            latch.await(10, TimeUnit.SECONDS);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if (responseSongPollModel != null) {
                            if (responseSongPollModel.getStatus().equalsIgnoreCase("Completed")) {
                                Intent intent = new Intent(SERVICE_EVENT);
                                Log.e(TAG, " RESPONSE FROM AFTER SENDING BATCH ID(SCAN API) : " + new Gson().toJson(responseSongPollModel));
                                intent.putExtra("songresponse", new Gson().toJson(responseSongPollModel));
                                LocalBroadcastManager.getInstance(ApiService.this).sendBroadcast(intent);
                                stopSelf();
                                break;
                            } else {
                                try {
                                    Thread.sleep(4000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    else{
                        Intent intent = new Intent(SERVICE_EVENT);
                        Log.e(TAG, " RESPONSE FROM AFTER SENDING BATCH ID(SCAN API) : " + new Gson().toJson(responseSongPollModel));
                        intent.putExtra("songresponse", "");
                        LocalBroadcastManager.getInstance(ApiService.this).sendBroadcast(intent);
                        stopSelf();
                        break;
                    }
                }

            }
        }.start();


        return Service.START_STICKY;

    }
}
