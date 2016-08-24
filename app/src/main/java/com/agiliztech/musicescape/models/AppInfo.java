package com.agiliztech.musicescape.models;

/**
 * Created by Pooja on 23-08-2016.
 */
public class AppInfo {

    private String title;
    private int imageUrl;

    public AppInfo(String title){

        this.title = title;
        //this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(int imageUrl) {
        this.imageUrl = imageUrl;
    }


    // getters & setters
}
