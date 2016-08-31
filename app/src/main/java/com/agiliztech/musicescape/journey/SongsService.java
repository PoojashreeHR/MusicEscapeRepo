package com.agiliztech.musicescape.journey;

import android.content.Context;
import android.graphics.PointF;

import com.agiliztech.musicescape.models.Song;
import com.agiliztech.musicescape.utils.SongsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by praburaam on 29/08/16.
 */
public class SongsService {
    static SongsService sharedInstance;
    private static Context mContext;

    float centerRad =  0.01f;

    public static SongsService getInstance(Context context){
        mContext = context;
        if(sharedInstance == null){
            sharedInstance = new SongsService();
        }
        return sharedInstance;
    }

    public SongMoodCategory checkMoodForEnergyAndValence(float e, float v){
        float y = e - 0.5f;
        float x = v - 0.5f;

        float angle = (float) (Math.atan2(y,x)*180/Math.PI);
        float r = (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));

        if (r <= centerRad){
            return SongMoodCategory.scHappy;
        }
        else{
            if (angle>=0 && angle<45)
            {
                return SongMoodCategory.scHappy;
            }
            else if (angle>=45 && angle<90)
            {
                return SongMoodCategory.scExcited;
            }
            else if (angle>=90 && angle<135)
            {
                return SongMoodCategory.scAggressive;
            }
            else if (angle>= 135 && angle<180)
            {
                return SongMoodCategory.scStressed;
            }
            else   if (angle>= -180 && angle<-135)
            {
                return SongMoodCategory.scDepressed;
            }
            else   if (angle>= -135 && angle< -90)
            {
                return SongMoodCategory.scBored;
            }
            else  if (angle>= -90 && angle<-45)
            {
                return SongMoodCategory.scPeaceful;
            }
            else   if (angle>= -45 && angle<0)
            {
                return SongMoodCategory.scChilled;
            }
        }
        return null;
    }

    public Map<SongMoodCategory,  PointF[]> returnMoodArrayWithEV(float y, float x){
        PointF xy = new PointF(x, y);
        PointF yx = new PointF(y, x);
        PointF yOneMinusX = new PointF(y, 1 - x);
        PointF xOneMinusY = new PointF(x, 1 - y);
        PointF oneMinusXOneMinusY = new PointF(1 - x, 1 - y);
        PointF oneMinusYOneMinusX = new PointF(1 - y, 1 - x);
        PointF oneMinusYX = new PointF(1 - y, x);
        PointF oneMinusXY = new PointF(1 - x, y);

        Map<SongMoodCategory,  PointF[]> songMoodMap = new HashMap<SongMoodCategory,  PointF[]>();

        PointF[] pointsArray = new PointF[]{xy,yx,yOneMinusX,xOneMinusY,oneMinusXOneMinusY,oneMinusYOneMinusX,oneMinusYX,oneMinusXY};
        songMoodMap.put(SongMoodCategory.scExcited, pointsArray);

        pointsArray = new PointF[]{yx,xy,xOneMinusY,yOneMinusX,oneMinusYOneMinusX,oneMinusXOneMinusY,oneMinusXY,oneMinusYX};
        songMoodMap.put(SongMoodCategory.scHappy, pointsArray);


        pointsArray = new PointF[]{oneMinusYX,xOneMinusY,xy,oneMinusYOneMinusX,yOneMinusX,oneMinusXY,oneMinusXOneMinusY,yx};
        songMoodMap.put(SongMoodCategory.scChilled, pointsArray);

        pointsArray = new PointF[]{xOneMinusY,oneMinusYX,oneMinusYOneMinusX,xy,oneMinusXY,yOneMinusX,yx,oneMinusXOneMinusY};
        songMoodMap.put(SongMoodCategory.scPeaceful, pointsArray);

        pointsArray = new PointF[]{oneMinusXOneMinusY,oneMinusYOneMinusX,oneMinusYX,oneMinusXY,xy,yx,yOneMinusX,oneMinusYX};
        songMoodMap.put(SongMoodCategory.scBored, pointsArray);

        pointsArray = new PointF[]{oneMinusYOneMinusX,oneMinusXOneMinusY,oneMinusXY,oneMinusYX,yx,xy,xOneMinusY,yOneMinusX};
        songMoodMap.put(SongMoodCategory.scDepressed, pointsArray);

        pointsArray = new PointF[]{yOneMinusX,oneMinusXY,oneMinusXOneMinusY,yx,oneMinusYX,xOneMinusY,xy,oneMinusYOneMinusX};
        songMoodMap.put(SongMoodCategory.scStressed, pointsArray);

        pointsArray = new PointF[]{oneMinusXY,yOneMinusX,yx,oneMinusXOneMinusY,xOneMinusY,oneMinusYX,oneMinusYOneMinusX,xy};
        songMoodMap.put(SongMoodCategory.scAggressive, pointsArray);

        return songMoodMap;
    }


    public PointF changejSongMoodToNewMood(JourneySong song,    SongMoodCategory newMood){
            return changeSongMoodToNewMood(song.getSong(), newMood);
    }

    private PointF changeSongMoodToNewMood(Song song, SongMoodCategory newMood) {
        if(song.getMood() == SongMoodCategory.scMoodNotFound)
        {
            song.setMood(SongMoodCategory.scHappy);
            song.setValance(0.75f);
            song.setEnergy(0.65f);
            return changeSongMoodToNewMood(song,newMood);
        }

        Map<SongMoodCategory,  PointF[]> songMoodArray = returnMoodArrayWithEV(song.getEnergy(), song.getValance());
        SongMoodCategory oldMood = song.getMood();

        PointF[] moodArray = songMoodArray.get(oldMood);


        return moodArray[SongsManager.getIntValue(newMood)];
    }



    public void retagSongWithNewMood(Song song, SongMoodCategory newMood){
        PointF newMoodEV = changeSongMoodToNewMood(song, newMood);

        song.setMood(newMood);
        song.setEnergy(newMoodEV.y);
        song.setValance(newMoodEV.x);
        song.setUserChangedMood(true);
        song.setUserChangedEnergy(newMoodEV.y);
        song.setUserChangedValance(newMoodEV.x);
        song.setUserRetaggedMood(SongsManager.textForMood(SongsManager.getIntValue(newMood)));

        //update the db with the song detail

        //

    }

}
