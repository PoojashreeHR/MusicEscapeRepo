package com.agiliztech.musicescape.models.apimodels;

/**
 * Created by Asif on 19-08-2016.
 */
import com.google.gson.annotations.SerializedName;

public class SongRequest {

    @SerializedName("artistName")
    private String artistName;
    @SerializedName("songName")
    private String songName;
    @SerializedName("clientId")
    private Integer clientId;
    @SerializedName("albumName")
    private String albumName;

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

}