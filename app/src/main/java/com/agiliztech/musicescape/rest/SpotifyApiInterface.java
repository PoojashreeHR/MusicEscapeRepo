package com.agiliztech.musicescape.rest;

import com.agiliztech.musicescape.models.spotifymodels.SpotifyMain;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by Asif on 23-08-2016.
 */
public interface SpotifyApiInterface {
    @GET("v1/search")
    Call<SpotifyMain> spotifyApiCalling(@QueryMap Map<String,String> query);
}
