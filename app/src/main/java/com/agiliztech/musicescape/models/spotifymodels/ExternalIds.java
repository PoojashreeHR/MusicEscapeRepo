package com.agiliztech.musicescape.models.spotifymodels;

/**
 * Created by Asif on 23-08-2016.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ExternalIds {

    @SerializedName("isrc")
    @Expose
    private String isrc;

    /**
     *
     * @return
     * The isrc
     */
    public String getIsrc() {
        return isrc;
    }

    /**
     *
     * @param isrc
     * The isrc
     */
    public void setIsrc(String isrc) {
        this.isrc = isrc;
    }

}