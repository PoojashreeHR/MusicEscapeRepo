package com.agiliztech.musicescape.journey;

import com.agiliztech.musicescape.models.JourneySession;
import com.agiliztech.musicescape.models.Song;

/**
 * Created by praburaam on 29/08/16.
 */
public class JourneySong {
    private int playedCount;
    private int skipped;
    private boolean swapped;
    private int trackNo;
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

    public boolean isSwapped() {
        return swapped;
    }

    public void setSwapped(boolean swapped) {
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
