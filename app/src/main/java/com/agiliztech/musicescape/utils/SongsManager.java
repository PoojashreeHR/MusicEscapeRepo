package com.agiliztech.musicescape.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.agiliztech.musicescape.journey.SongMoodCategory;
import com.agiliztech.musicescape.models.SongsModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pooja on 11-08-2016.
 */
public class SongsManager {
    private static Integer kExcitedColor;
    private static Integer kHappyColor;
    private static Integer kChillColor;
    private static Integer kPeacefulColor;
    private static Integer kBoredColor;
    private static Integer kDepressedColor;
    private static Integer kStressedColor;
    private static Integer kAggressiveColor;
    private static Integer kUntaggedMoods;
    private static Integer kAllMoods;


    public static void initializeColors(){
        kExcitedColor = Color.argb(1,255,160,0);
        kHappyColor = Color.argb(1,255,233,86);
        kChillColor = Color.argb(1,139,241,74);
        kPeacefulColor = Color.argb(1,137,229,246);
        kBoredColor = Color.argb(1,36,119,184);
        kDepressedColor = Color.argb(1,145,74,218);
        kStressedColor = Color.argb(1,222,60,227);
        kAggressiveColor = Color.argb(1,237,70,47);
        kUntaggedMoods = Color.argb(1,80,80,80);
        kAllMoods = Color.argb(1,255,255,255);

    }

    Context context;
    private ArrayList<SongsModel> songList = new ArrayList<SongsModel>();
    SongsManager songsManager;

    public SongsManager(Context context){
        this.context = context;
    }


    public ArrayList<SongsModel> getSongList()
    {
        //query external audio

        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String orderBy = android.provider.MediaStore.Audio.Media.TITLE;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, orderBy);

        //iterate over results if valid
        if(musicCursor!=null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int isMusicColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC);
            int dataCol = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
          /*  int albumColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM);*/

            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                int isMusic = musicCursor.getInt(isMusicColumn);
                String filePath = musicCursor.getString(dataCol);

                if(isMusic > 0 ) {
                    songList.add(new SongsModel(thisId, thisTitle, thisArtist,filePath));
                    Log.e("Song Details", ":" + thisTitle + ":" + thisArtist);
                }
            }
            while (musicCursor.moveToNext());

        }
        musicCursor.close();
        return songList;
    }

    public static int colorForMood(SongMoodCategory mood) {
        initializeColors();
        Map<SongMoodCategory, Integer> allColors = new HashMap<SongMoodCategory, Integer>();
        allColors.put(SongMoodCategory.scExcited, kExcitedColor);
        allColors.put(SongMoodCategory.scAggressive, kAggressiveColor);
        allColors.put(SongMoodCategory.scAllSongs, kAllMoods);
        allColors.put(SongMoodCategory.scBored, kBoredColor);
        allColors.put(SongMoodCategory.scChilled, kChillColor);
        allColors.put(SongMoodCategory.scDepressed, kDepressedColor);
        allColors.put(SongMoodCategory.scHappy, kHappyColor);
        allColors.put(SongMoodCategory.scMoodNotFound, kUntaggedMoods);
        allColors.put(SongMoodCategory.scPeaceful, kPeacefulColor);
        allColors.put(SongMoodCategory.scStressed, kStressedColor);
        return allColors.get(mood);
    }
}
