package com.agiliztech.musicescape.Models;

import java.util.Date;
import java.util.List;

/**
 * Created by amrithamayangorky on 8/12/16.
 */
public class JourneySession {
    private int currentMood;
    private int favourite;
    private Date finished;
    private int finishMood;
    private int interruptionTime;
    private int listeningMindfully;
    private int looped;
    private int moodShiftSuccess;
    private String name;
    private int sessionSyncStatus;
    private Date started;
    private int targetMood;
    private int totalDuration;
    private int totalPlaylistDuration;
    private String journeyID;
    private Journey journey;
    private List<Song> songs;

    public int getCurrentMood() {
        return currentMood;
    }

    public void setCurrentMood(int currentMood) {
        this.currentMood = currentMood;
    }

    public int getFavourite() {
        return favourite;
    }

    public void setFavourite(int favourite) {
        this.favourite = favourite;
    }


    public int getFinishMood() {
        return finishMood;
    }

    public void setFinishMood(int finishMood) {
        this.finishMood = finishMood;
    }

    public int getInterruptionTime() {
        return interruptionTime;
    }

    public void setInterruptionTime(int interruptionTime) {
        this.interruptionTime = interruptionTime;
    }

    public int getListeningMindfully() {
        return listeningMindfully;
    }

    public void setListeningMindfully(int listeningMindfully) {
        this.listeningMindfully = listeningMindfully;
    }

    public int getLooped() {
        return looped;
    }

    public void setLooped(int looped) {
        this.looped = looped;
    }

    public int getMoodShiftSuccess() {
        return moodShiftSuccess;
    }

    public void setMoodShiftSuccess(int moodShiftSuccess) {
        this.moodShiftSuccess = moodShiftSuccess;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSessionSyncStatus() {
        return sessionSyncStatus;
    }

    public void setSessionSyncStatus(int sessionSyncStatus) {
        this.sessionSyncStatus = sessionSyncStatus;
    }


    public Date getStarted() {
        return started;
    }

    public void setStarted(Date started) {
        this.started = started;
    }

    public int getTargetMood() {
        return targetMood;
    }

    public void setTargetMood(int targetMood) {
        this.targetMood = targetMood;
    }

    public int getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(int totalDuration) {
        this.totalDuration = totalDuration;
    }

    public int getTotalPlaylistDuration() {
        return totalPlaylistDuration;
    }

    public void setTotalPlaylistDuration(int totalPlaylistDuration) {
        this.totalPlaylistDuration = totalPlaylistDuration;
    }

    public String getJourneyID() {
        return journeyID;
    }

    public void setJourneyID(String journeyID) {
        this.journeyID = journeyID;
    }

    public Journey getJourney() {
        return journey;
    }

    public void setJourney(Journey journey) {
        this.journey = journey;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public Date getFinished() {
        return finished;
    }

    public void setFinished(Date finished) {
        this.finished = finished;
    }
}
