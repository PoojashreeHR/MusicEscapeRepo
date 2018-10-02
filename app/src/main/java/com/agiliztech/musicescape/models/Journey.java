package com.agiliztech.musicescape.models;

import android.graphics.PointF;

import com.agiliztech.musicescape.journey.Dot;

import java.util.List;

/**
 * Created by amrithamayangorky on 8/12/16.
 */
public class Journey {
    private String generatedBy;
   // NSData * dots;
    // NSData * points;
    private int trackCount;
    private String name;
    private JourneySession sessions;
    private List<PointF> journeyEVArray;
    private List<Integer> journeyDotsArray;

    public String getGeneratedBy() {
        return generatedBy;
    }

    public void setGeneratedBy(String generatedBy) {
        this.generatedBy = generatedBy;
    }

    public int getTrackCount() {
        return trackCount;
    }

    public void setTrackCount(int trackCount) {
        this.trackCount = trackCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JourneySession getSessions() {
        return sessions;
    }

    public void setSessions(JourneySession sessions) {
        this.sessions = sessions;
    }

    public List<PointF> getJourneyEVArray() {
        return journeyEVArray;
    }

    public void setJourneyEVArray(List<PointF> journeyEVArray) {
        this.journeyEVArray = journeyEVArray;
    }

    public List<Integer> getJourneyDotsArray() {
        return journeyDotsArray;
    }

    public void setJourneyDotsArray(List<Integer> journeyDotsArray) {
        this.journeyDotsArray = journeyDotsArray;
    }
}
