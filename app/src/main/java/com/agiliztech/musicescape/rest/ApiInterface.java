package com.agiliztech.musicescape.rest;

import com.agiliztech.musicescape.models.apimodels.BatchIdResponseModel;
import com.agiliztech.musicescape.models.apimodels.DeviceIdModel;
import com.agiliztech.musicescape.models.apimodels.ResponseSongPollModel;
import com.agiliztech.musicescape.models.apimodels.SongDetail;
import com.agiliztech.musicescape.models.apimodels.SongRetagInfo;
import com.agiliztech.musicescape.models.apimodels.SpotifyInfo;
import com.agiliztech.musicescape.models.apimodels.SpotifyModelMain;
import com.agiliztech.musicescape.models.spotifymodels.SpotifyMain;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Asif on 18-08-2016.
 */
public interface ApiInterface {

    //Scan Batch Creation
    //Response String batchId
    @POST("api/v3/scan")
    Call<BatchIdResponseModel> sendSongToServerToScan(@Body DeviceIdModel model);


    //Scan Batch Poll
    @GET("api/v3/scan/{batchId}")
    Call<ResponseSongPollModel> pollSongFromServer(@Path("batchId") String batchId);
                                                   //@Path("sinceDateTime") String sinceDateTime);

    //Analyse Batch Creation
    //Response String batchId
    @POST("api/v3/analyse")
    Call<BatchIdResponseModel> analyseScanSongs(@Body SpotifyModelMain model);


    //Analyse Batch Poll
    @GET("api/v3/analyse/{batchId}")
    Call<ResponseSongPollModel> analysePollSongs(@Path("batchId") String batchId);
                                                 //@Path("sinceDateTime") String sinceDateTime);

    //SongRetag
    //Response String (HTTP 201)
    @POST("api/v3/songs/retag")
    Call<String> retagSongs(@Body String user,
                            @Body SongRetagInfo retagInfo);

}
