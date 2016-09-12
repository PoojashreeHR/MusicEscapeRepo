package com.agiliztech.musicescape.apiservices;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.agiliztech.musicescape.models.apimodels.ResponseSongPollModel;
import com.agiliztech.musicescape.rest.ApiClient;
import com.agiliztech.musicescape.rest.ApiInterface;
import com.agiliztech.musicescape.utils.Global;
import com.agiliztech.musicescape.utils.UtilityClass;
import com.google.gson.Gson;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Asif on 26-08-2016.
 */
public class AnalyseApiService extends Service {
    public static final String SERVICE_EVENT = "com.agiliztech.musicescape.musicservices.MusicService" + "_event_response_analyse";
    private CountDownLatch latch;
    private ResponseSongPollModel responseSongPollModel;
    private String TAG = "AnalyseApiService.java";

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
        //final String variable = intent.getStringExtra("variable");
        Log.e(TAG, " SENDING BATCH ID TO ANALYSE API : " + batchIds);
        String batchId = batchIds;
        new Thread() {
            @Override
            public void run() {

                while (true) {
                    if (UtilityClass.checkInternetConnectivity(AnalyseApiService.this) && !Global.HALT_API) {
                        // if (variable.contains("1")) {
                        latch = new CountDownLatch(1);
                        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class, "RandyApp", "N1nj@R@nDy");
                        Call<ResponseSongPollModel> call1 = apiInterface.analysePollSongs(batchIds);
                        //latch = new CountDownLatch(1);
                        call1.enqueue(new Callback<ResponseSongPollModel>() {
                            @Override
                            public void onResponse(Call<ResponseSongPollModel> call, Response<ResponseSongPollModel> response) {

                                if (response.isSuccessful()) {
                                    Log.e("RESPONSE SUCCESS ", new Gson().toJson(response.body()));
                                    responseSongPollModel = response.body();


                                    //latch.countDown();
                                    // responseSongPollModel.setSongs(new ArrayList<>(Arrays.asList(responseSongPollModel.getSongs().get(0))));

                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseSongPollModel> call, Throwable t) {
                                Log.e("RESPONSE ERROR ", "" + t.getMessage());
                                //latch.countDown();
                            }
                        });
                        try {
                            latch.await(10, TimeUnit.SECONDS);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (responseSongPollModel != null) {
                            if (responseSongPollModel.getStatus().equalsIgnoreCase("completed")) {
                                Intent intent = new Intent(SERVICE_EVENT);
                                Log.e("songresponse_analysed", new Gson().toJson(responseSongPollModel));
                                intent.putExtra("songresponse_analysed", new Gson().toJson(responseSongPollModel));
                                LocalBroadcastManager.getInstance(AnalyseApiService.this).sendBroadcast(intent);
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
                    else if(Global.HALT_API){
                        try {
                            synchronized(this){
                                this.wait();
                                Toast.makeText(AnalyseApiService.this, "You Paused the Action", Toast.LENGTH_SHORT).show();
                                Global.HALT_API = false;
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    else{
                        Toast.makeText(AnalyseApiService.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SERVICE_EVENT);
                        Log.e("songresponse_analysed", new Gson().toJson(responseSongPollModel));
                        intent.putExtra("songresponse_analysed", "");
                        LocalBroadcastManager.getInstance(AnalyseApiService.this).sendBroadcast(intent);
                        stopSelf();
                        break;
                    }
                }
            }
        }.start();
        return Service.START_STICKY;

    }
}

