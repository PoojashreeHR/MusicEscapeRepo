package com.agiliztech.musicescape.journey;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;

import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

import android.view.MotionEvent;
import android.view.View;

import com.agiliztech.musicescape.R;
import com.agiliztech.musicescape.utils.SongsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by praburaam on 24/08/16.
 */



public class JourneyView extends View {

    private static final String CACHE_STR = "cache";
    private int xm,ym, em=1, vm =1;
    private Paint circlePaint;
    private int nullColor = 2;


    public enum DrawingMode{
        DMDRAWING,
        DMJOURNEY,
        DMMENU
    }

    //internal data structure, pertaining to the view
    List<Dot> points;
    PointF lastTouch;
    List<PointF> actualTouches;
    boolean userInteractionEnabled;

    private DrawingMode mode;

    private List<Integer> journeyPoints;
    private List<Integer> trackDots;

    private float dotGap;
    private float lineGap;

    private Size gaps;
    private int currentSongIndex;

    static Map<String, List<Dot>> cachedPoints;

    public JourneyView(Context context) {
        super(context);
        init();
    }

    public JourneyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public JourneyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        xm = getMeasuredWidth();
        ym = getMeasuredHeight();

        circlePaint = new Paint();
        circlePaint.setStyle(Paint.Style.FILL);

        journeyPoints = new ArrayList<Integer>();
        trackDots = new ArrayList<Integer>();

        if(cachedPoints == null) {
            cachedPoints = new HashMap<String, List<Dot>>();
        }

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        touchesBegin(event);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        touchesMoved(event);
                        break;
                    case MotionEvent.ACTION_UP:
                        touchesEnd(event);
                        break;
                    default:
                        return false;
                }
                invalidate();
                return true;
            }
        });

        mode = DrawingMode.DMDRAWING;
        setupDots();
    }

    /*Handle touch events*/
    private void touchesEnd(MotionEvent event) {
        lastTouch = new PointF(0,0);
    }

    private void touchesMoved(MotionEvent event) {
        if(!userInteractionEnabled){
            return;
        }

        PointF point = new PointF(event.getX(), event.getY());
        addTouch(point);
        lastTouch = point;
    }

    private void addTouch(PointF point) {
        actualTouches.add(point);
        List<Integer> dotIndexes = dotIndexesBetweenPoints(point, lastTouch);
        addUniqueDotsToJourneyPoints(dotIndexes);

    }

    private void addUniqueDotsToJourneyPoints(List<Integer> dotIndexes) {
        for(Integer d :dotIndexes){
            if(!journeyPoints.contains(d)){
                journeyPoints.add(d);

                Dot dot = points.get(d);
                Rect dirtyRect = new Rect();
                dot.getBounds().roundOut(dirtyRect);
                invalidate(dirtyRect);
            }
        }
    }

    private List<Integer> dotIndexesBetweenPoints(PointF p1, PointF p2) {
        List<PointF> pointsToAdd = new ArrayList<PointF>();
        List<Integer> journeyDotIndexes = new ArrayList<Integer>();

        float d = Math.max(Math.abs(p1.x - p2.x), Math.abs(p1.y - p2.y));
        for(float i = 0; i <= d; i++)
        {
            PointF li = interpolate(p1,p2,i/d);
            pointsToAdd.add(li);
        }

        for(PointF point : pointsToAdd){
            Integer index = dotIndexForPoint(point);
            if(index != null){
                journeyDotIndexes.add(index);
            }
        }

        return journeyDotIndexes;
    }

    private Integer dotIndexForPoint(PointF point) {

        for(int i=0; i<points.size(); i++){
            Dot d = points.get(i);

            RectF rectF = d.getBounds();

            if(rectF.contains(point.x, point.y)){
                return i;
            }
        }

        return null;
    }

    private PointF interpolate(PointF p1, PointF p2, float k) {
        float x = (p1.x * k) + (p2.x * (1 - k));
        float y = (p1.y * k) + (p2.y * (1 - k));
        return new PointF(x,y);
    }

    private void touchesBegin(MotionEvent event) {
        lastTouch = new PointF(event.getX(), event.getY());
        actualTouches = new ArrayList<PointF>();
        actualTouches.add(lastTouch);
        clearJourney();
    }



    @Override
    protected void onDraw(Canvas canvas) {
        drawPointsInRect(canvas);
    }

    private void drawPointsInRect(Canvas canvas) {
        int lastJourneyDotIndex = this.journeyPoints.size() - 1;
        boolean moreThanOneDot = this.journeyPoints.size() > 1;

        int idx = 0;

        for (Dot dot : points) {
            Integer dotIndex = this.journeyPoints.indexOf(idx);
            boolean journeyDot = moreThanOneDot && (dotIndex != -1 && lastJourneyDotIndex != -1);
            boolean currentDot = (dotIndex == currentSongIndex);
            boolean firstJourneyDot = journeyDot && (dotIndex == 0);
            boolean lastJourneyDot = journeyDot && (dotIndex == lastJourneyDotIndex);

            boolean skipThis = false;
            if (mode != null && mode == DrawingMode.DMJOURNEY) {
                if (journeyDot) {

                    boolean trackDot = (trackDots.indexOf(dotIndex) != -1);
                    if (!trackDot) {
                        circlePaint.setColor(Color.rgb(170, 170, 170));
                        canvas.drawCircle(dot.getXYPoint().x, dot.getXYPoint().y, (float) (dot.getRadius() * 0.5), circlePaint);
                        //return;
                        skipThis = true;
                    }
                }
            }

            if (!skipThis) {
                int color = colorForDot(dot, journeyDot, currentDot);
                boolean skipSecond = false;
                if (color == nullColor) {
                    skipSecond = true;
                }
                if (!skipSecond) {
                    circlePaint.setColor(color);
                    canvas.drawCircle(dot.getXYPoint().x, dot.getXYPoint().y, dot.getRadius(), circlePaint);


                    if ((mode == DrawingMode.DMMENU || mode == DrawingMode.DMJOURNEY) && firstJourneyDot) {
                        drawFirstDotAtPoint(canvas, dot.getXYPoint(), dot.getRadius());
                    } else if ((mode == DrawingMode.DMMENU || mode == DrawingMode.DMJOURNEY) && lastJourneyDot) {
                        drawLastDotAtPoint(canvas, dot.getXYPoint(), dot.getRadius(), color);
                    }
                }

            }

            idx++;
        }
    }

    private void drawLastDotAtPoint(Canvas canvas, PointF xyPoint, float radius, int color) {
        drawFirstDotAtPoint(canvas, xyPoint, radius);
        circlePaint.setColor(color);
        canvas.drawCircle(xyPoint.x, xyPoint.y, radius*0.3f, circlePaint);
    }

    private void drawFirstDotAtPoint(Canvas canvas, PointF xyPoint, float radius) {
        circlePaint.setColor(Color.rgb(38,38,38));
        canvas.drawCircle(xyPoint.x, xyPoint.y, radius*0.65f, circlePaint);
    }



    private int colorForDot(Dot dot, boolean journeyDot, boolean currentSong) {
        int color = nullColor;
        if(mode == DrawingMode.DMDRAWING && isActualTouch(dot)){
            color = Color.rgb(255,255,255);
        }
        else if(mode == DrawingMode.DMJOURNEY && currentSong){
            color = Color.rgb(255,255,255);
        }
        else if(journeyDot && dot.getMood() == SongMoodCategory.scMoodNotFound && mode != DrawingMode.DMDRAWING) {
            // Change the color of the dots in the center for non drawing modes
            color = Color.rgb(170,170,170);
        } else if(mode == DrawingMode.DMDRAWING && journeyDot) {
            color = Color.rgb(255,255,255);
        } else if(mode == DrawingMode.DMJOURNEY && !journeyDot) {
            return nullColor;
        } else if(mode == DrawingMode.DMMENU && !journeyDot) {
            return nullColor; // No need to draw this point!
        } else if(dot.getMood() == SongMoodCategory.scMoodNotFound) {
            color = Color.rgb(38,38,38);
        }
        return (color == nullColor) ? SongsManager.colorForMood(dot.getMood()) : color;
    }

    private boolean isActualTouch(Dot dot) {
        boolean isTouch = false;

        if(actualTouches == null){
            return false;
        }

        for(PointF point : actualTouches){
            if(dot.getBounds().contains(point.x, point.y)){
                isTouch = true;
                break;
            }
        }
        return isTouch;
    }

    void addRow(SongMoodCategory category, float startX, float startY, int xDirection, int yDirection){
        List<DotLine> row = rowSector();

        float y = startY;
        for(int i=0; i< row.size(); i++){
            DotLine line = row.get(i);

            y += line.getRadius() * yDirection;
            float x = startX + (line.getRadius() * xDirection);

            for(int od = 0; od < line.getOffset(); od++)
            {
                PointF p = new PointF(x, y);
                addPoint(p,line.getRadius(),SongMoodCategory.scMoodNotFound);
                x +=  ((line.getRadius() * 2 + getDotGap()) * xDirection);
            }

            for(int d = 0; d < line.getDots(); d++)
            {

                PointF p = new PointF(x, y);
                addPoint(p,line.getRadius(),category);
                x +=  ((line.getRadius() * 2 + getDotGap()) * xDirection);
            }

            y += ((line.getRadius() + getLineGap()) * yDirection);
        }

        List<Dot> customPoints = rowCustomDots();
        for(int i=0; i<customPoints.size(); i++){
            Dot dot = customPoints.get(i);

            float v = dot.getEVPoint().x;
            float e = dot.getEVPoint().y;
            int r = (int) dot.getRadius();

            PointF point = new PointF((xDirection==1 ? 1-v : v),(yDirection == -1 ? 1-e : e));
            addPoint(point,getRelativeDiameter(r),category);
        }
    }

    void addCol(SongMoodCategory category, float startX, float startY, int xDirection, int yDirection){
        List<DotLine> row = colSector();
        float x = startX;

        for(int i=0; i<row.size(); i++){
            DotLine line = row.get(i);

            x += (line.getRadius() * xDirection);
            float y = startY + (line.getRadius() * yDirection);

            for(int od = 0; od < line.getOffset(); od++)
            {
                PointF p = new PointF(x, y);
                addPoint(p,line.getRadius(),SongMoodCategory.scMoodNotFound);
                y +=  ((line.getRadius() * 2 + getDotGap()) * yDirection);
            }

            for(int d = 0; d < line.getDots(); d++)
            {

                PointF p = new PointF(x, y);
                addPoint(p,line.getRadius(),category);
                y +=  ((line.getRadius() * 2 + getDotGap()) * yDirection);
            }

            x += ((line.getRadius() + getLineGap()) * xDirection);
        }

        List<Dot> customPoints = colCustomDots();
        for(int i=0; i<customPoints.size(); i++){
            Dot dot = customPoints.get(i);

            float v = dot.getEVPoint().x;
            float e = dot.getEVPoint().y;
            int r = (int) dot.getRadius();

            PointF point = new PointF((xDirection == -1 ? 1-v : v),(yDirection == 1 ? 1-e : e));
            addPoint(point,getRelativeDiameterHWise(r),category);
        }
    }

    private void addPoint(PointF p, float radius, SongMoodCategory category) {
        Dot dot = new Dot(radius);
        dot.setXYPoint(p);
        dot.setEVPoint(getEYFromXY(p));
        dot.setMood(category);
        dot.setBounds(new RectF(p.x-radius, p.y-radius, p.x-radius+radius*2, p.y-radius+radius*2 ));

        points.add(dot);
    }

    private PointF getEYFromXY(PointF point) {
        float v = (point.x / xm) * vm;
        float e = 1 - ((point.y / ym) * em);
        return new PointF(v,e);
    }

    private List<DotLine> rowSector() {
        List<DotLine> sector = new ArrayList<DotLine>();
        sector.add(new DotLine(getRelativeDiameter(38),7));
        sector.add(new DotLine(getRelativeDiameter(38),6));

        sector.add(new DotLine(getRelativeDiameter(32),6));
        sector.add(new DotLine(getRelativeDiameter(28),6));
        sector.add(new DotLine(getRelativeDiameter(24),6));
        sector.add(new DotLine(getRelativeDiameter(24),6));

        sector.add(new DotLine(getRelativeDiameter(21),6));
        sector.add(new DotLine(getRelativeDiameter(17),6));
        sector.add(new DotLine(getRelativeDiameter(17),6));
        sector.add(new DotLine(getRelativeDiameter(15),6));

        sector.add(new DotLine(getRelativeDiameter(15),5));
        sector.add(new DotLine(getRelativeDiameter(9),6));
        sector.add(new DotLine(getRelativeDiameter(8),7));
        sector.add(new DotLine(getRelativeDiameter(8),6));

        sector.add(new DotLine(getRelativeDiameter(8),5));
        sector.add(new DotLine(getRelativeDiameter(8),4));
        sector.add(new DotLine(getRelativeDiameter(8),4));
        sector.add(new DotLine(getRelativeDiameter(8),3));

        sector.add(new DotLine(getRelativeDiameter(8),2));
        sector.add(new DotLine(getRelativeDiameter(8),2));
        sector.add(new DotLine(getRelativeDiameter(8),1));

        return sector;
    }

    private List<DotLine> colSector(){
        List<DotLine> sector = new ArrayList<DotLine>();
        sector.add(new DotLine(getRelativeDiameterHWise(38),9));
        sector.add(new DotLine(getRelativeDiameterHWise(32),9));
        sector.add(new DotLine(getRelativeDiameterHWise(24),11));
        sector.add(new DotLine(getRelativeDiameterHWise(23),10));

        sector.add(new DotLine(getRelativeDiameterHWise(22),8));
        sector.add(new DotLine(getRelativeDiameterHWise(17),9));
        sector.add(new DotLine(getRelativeDiameterHWise(15),8));
        sector.add(new DotLine(getRelativeDiameterHWise(13),8));

        sector.add(new DotLine(getRelativeDiameterHWise(10),9));
        sector.add(new DotLine(getRelativeDiameterHWise(8),9));
        sector.add(new DotLine(getRelativeDiameterHWise(8),8));
        sector.add(new DotLine(getRelativeDiameterHWise(8),7));

        sector.add(new DotLine(getRelativeDiameterHWise(8),5));
        sector.add(new DotLine(getRelativeDiameterHWise(8),4));
        sector.add(new DotLine(getRelativeDiameterHWise(8),2));
        sector.add(new DotLine(getRelativeDiameterHWise(8),1));
        return sector;
    }

    private List<Dot> colCustomDots(){
        List<Dot> dots = new ArrayList<Dot>();

        dots.add(getDotFromEV(0.09f,0.89f,14));
        dots.add(getDotFromEV(0.19f,0.817f,10));
        dots.add(getDotFromEV(0.23f,0.748f,9));
        dots.add(getDotFromEV(0.31f,0.674f,8));

        return dots;
    }

    private List<Dot> rowCustomDots(){
        List<Dot> dots = new ArrayList<Dot>();

        dots.add(getDotFromEV(0.126f,0.89f,14));
        dots.add(getDotFromEV(0.247f,0.748f,9));
        dots.add(getDotFromEV(0.283f,0.718f,9));
        dots.add(getDotFromEV(0.34f,0.653f,8));

        return dots;
    }

    private Dot getDotFromEV(float valance, float energy, int radius) {
        Dot dot = new Dot(radius);
        dot.setEVPoint(new PointF(valance,energy));
        dot.setXYPoint(getXYFromEV(dot.getEVPoint()));
        return dot;
    }

    private PointF getXYFromEV(PointF evPoint) {
        float e = evPoint.y;
        float v = evPoint.x;

        float x = (xm * v) / vm;
        float y = ym * ((-e + em) / em);

        return new PointF(x,y);
    }

    private float getRelativeDiameter(int diaFor560) {
        float diafor560 = diaFor560 * xm;
        return diafor560/560.f;
    }

    private float getRelativeDiameterHWise(int diaFor560) {
        float diafor560 = diaFor560 * xm;
        return diafor560/560.f;
    }

    void setupDots(){
        if(cachedPoints != null && cachedPoints.size() > 0 && cachedPoints.containsKey(getCacheStr())){
            points = cachedPoints.get(getCacheStr());
            return;
        }

        points = new ArrayList<Dot>(924);
        float centerX = ((float)xm)/2;
        float centerY = ((float)ym)/2;

        float halfGap = getLineGap()/2;

        addRow(SongMoodCategory.scAggressive,centerX-halfGap, getLineGap(),-1,1);
        addRow(SongMoodCategory.scExcited,centerX+halfGap, getLineGap(),1,1);

        addCol(SongMoodCategory.scHappy, xm - getLineGap(), centerY-halfGap, -1,-1);
        addCol(SongMoodCategory.scChilled , xm - getLineGap() , centerY+halfGap, -1, 1);

        addRow(SongMoodCategory.scBored,centerX-halfGap, ym-getLineGap(),-1,-1);
        addRow(SongMoodCategory.scPeaceful,centerX+halfGap, ym-getLineGap(),1,-1);

        addCol(SongMoodCategory.scStressed , getLineGap() ,centerY-halfGap ,1 ,-1);
        addCol(SongMoodCategory.scDepressed , getLineGap() ,centerY+halfGap ,1 ,1);

        cachedPoints.put(getCacheStr(),points);

    }

    private String getCacheStr() {
        return String.format("%d %.2f %.2f",xm,dotGap,lineGap);
    }

    //getters and setters
    public List<Integer> getJourneyPoints() {
        return journeyPoints;
    }

    public void setJourneyPoints(List<Integer> journeyPoints) {
        this.journeyPoints = journeyPoints;
        invalidate();
    }

    public List<Integer> getTrackDots() {
        return trackDots;
    }

    public void setTrackDots(List<Integer> trackDots) {
        this.trackDots = trackDots;
    }

    public float getDotGap() {
        return dotGap;
    }

    public void setDotGap(float dotGap) {
        this.dotGap = dotGap;
        setupDots();
        invalidate();
    }

    public float getLineGap() {
        return lineGap;
    }

    public void setLineGap(float lineGap) {
        this.lineGap = lineGap;
        setupDots();
        invalidate();
    }

    public Size getGaps() {
        return gaps;
    }

    public void setGaps(Size gaps) {
        this.gaps = gaps;
        this.lineGap = gaps.getWidth();
        this.dotGap = gaps.getHeight();
        setupDots();
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(parentWidth, parentHeight);
        if(parentWidth != xm || parentHeight != ym) {
            xm = parentWidth ;
            ym = parentHeight ;
            setupDots();
            invalidate();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public int getCurrentSongIndex() {
        return currentSongIndex;
    }

    public void setCurrentSongIndex(int currentSongIndex) {
        this.currentSongIndex = currentSongIndex;
        // Redraw the current song index's dot
        invalidate();
    }

    public DrawingMode getMode() {
        return mode;
    }

    public void setMode(DrawingMode mode) {
        this.mode = mode;
        userInteractionEnabled = (this.mode == DrawingMode.DMDRAWING);
        invalidate();
    }

    public void clearJourney(){
        this.journeyPoints.clear();
        invalidate();
    }

    public List<PointF> journeyAsValenceAndEnergyPoints(){
        List<PointF> vePoints = new ArrayList<PointF>();
        for(Integer i : journeyPoints){
            Dot d = points.get(i);
            vePoints.add(d.getEVPoint());
        }
        return vePoints;
    }

}
