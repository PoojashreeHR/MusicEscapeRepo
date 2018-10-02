package com.agiliztech.musicescape.models;

/**
 * Created by amrithamayangorky on 8/12/16.
 */
public class JourneySongs {

    private int playedCount;
    private int  skipped;
    private int  swapped;
    private int  trackNo;
    private JourneySession session;
    private Song song;

    public int getPlayedCount() {
        return playedCount;
    }

    public void setPlayedCount(int playedCount) {
        this.playedCount = playedCount;
    }

    public int getSkipped() {
        return skipped;
    }

    public void setSkipped(int skipped) {
        this.skipped = skipped;
    }

    public int getSwapped() {
        return swapped;
    }

    public void setSwapped(int swapped) {
        this.swapped = swapped;
    }

    public int getTrackNo() {
        return trackNo;
    }

    public void setTrackNo(int trackNo) {
        this.trackNo = trackNo;
    }

    public JourneySession getSession() {
        return session;
    }

    public void setSession(JourneySession session) {
        this.session = session;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }
}
