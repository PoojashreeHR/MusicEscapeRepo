package com.agiliztech.musicescape.journey;

import android.graphics.PointF;
import android.graphics.RectF;

/**
 * Created by praburaam on 24/08/16.
 */
public class Dot {
    private float radius;
    private SongMoodCategory mood;
    private PointF XYPoint;
    private PointF EVPoint;
    private RectF bounds;

    public Dot(float radius) {
        this.radius = radius;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public SongMoodCategory getMood() {
        return mood;
    }

    public void setMood(SongMoodCategory mood) {
        this.mood = mood;
    }

    public PointF getXYPoint() {
        return XYPoint;
    }

    public void setXYPoint(PointF XYPoint) {
        this.XYPoint = XYPoint;
    }

    public PointF getEVPoint() {
        return EVPoint;
    }

    public void setEVPoint(PointF EVPoint) {
        this.EVPoint = EVPoint;
    }

    public RectF getBounds() {
        return bounds;
    }

    public void setBounds(RectF bounds) {
        this.bounds = bounds;
    }
}
