package com.agiliztech.musicescape.journey;

/**
 * Created by praburaam on 24/08/16.
 */
public class DotLine{
    private int dots;
    private float radius;
    private int offset;

    public DotLine( float diameterRatio,int dots, int offset) {
        this.dots = dots;
        this.radius = diameterRatio/2;
        this.offset = offset;
    }

    public DotLine( float diameterRatio,int dots) {
        this.dots = dots;
        this.radius = diameterRatio/2;
        this.offset = 0;
    }

    public int getDots() {
        return dots;
    }

    public float getRadius() {
        return radius;
    }

    public int getOffset() {
        return offset;
    }

}
