package com.agiliztech.musicescape.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.agiliztech.musicescape.journey.SongMoodCategory;
import com.agiliztech.musicescape.models.Album;
import com.agiliztech.musicescape.models.Artist;
import com.agiliztech.musicescape.models.SongsModel;
import com.agiliztech.musicescape.models.Song;

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
        kExcitedColor = Color.rgb(255,160,0);
        kHappyColor = Color.rgb(255,233,86);
        kChillColor = Color.rgb(139,241,74);
        kPeacefulColor = Color.rgb(137,229,246);
        kBoredColor = Color.rgb(36,119,184);
        kDepressedColor = Color.rgb(145,74,218);
        kStressedColor = Color.rgb(222,60,227);
        kAggressiveColor = Color.rgb(237,70,47);
        kUntaggedMoods = Color.rgb(80,80,80);
        kAllMoods = Color.rgb(255,255,255);

    }

    Context context;
    private ArrayList<Song> songList = new ArrayList<>();
    SongsManager songsManager;

    public SongsManager(Context context){
        this.context = context;
    }


    public ArrayList<Song> getSongList()
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
            int artistIDColumn = musicCursor.getColumnIndex(
                    MediaStore.Audio.Media.ARTIST_ID
            );
            int isMusicColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC);
            int dataCol = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int albumColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM);
            int albumIDColumn = musicCursor.getColumnIndex(
                    MediaStore.Audio.Media.ALBUM_ID
            );

            int durationColumn = musicCursor.getColumnIndex(
                    MediaStore.Audio.Media.DURATION
            );

            //add songs to list
            int i=0;
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                int isMusic = musicCursor.getInt(isMusicColumn);
                String filePath = musicCursor.getString(dataCol);
                String albumName = musicCursor.getString(albumColumn);

                String albumId = musicCursor.getString(albumIDColumn);
                String artistId = musicCursor.getString(artistIDColumn);
                String duration = musicCursor.getString(durationColumn);

                Album album = new Album(albumId, albumName);
                Artist artist = new Artist(artistId, thisArtist);

                Song s = new Song(thisId, thisTitle, artist, album);
                s.setPlaybackDuration(Integer.parseInt(duration));

                if(isMusic > 0 ) {
                    songList.add(s);
                    Log.e("Song Name", ": " + thisTitle + "\n Artist: " + thisArtist
                    +"\n Album Name :" + albumName + "\n ID = " + thisId);
                }
                i++;
            }
            while (musicCursor.moveToNext());
            Log.e("PRINTING I " , "" + i );

        }
        if(musicCursor != null && !musicCursor.isClosed()) {
            musicCursor.close();
        }
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

    public static String textForMood(int moodIndex) {
        String[] allTexts = new String[] {"Excited","Happy"
        ,"Chilled","Peaceful","Bored", "Depressed"
                ,"Stressed", "Aggressive", "Not Found", "All Moods"
        };
        return allTexts[moodIndex];
    }

    public static SongMoodCategory getMoodForText(String mood) {
        String[] allTexts = new String[] {"Excited","Happy"
                ,"Chilled","Peaceful","Bored", "Depressed"
                ,"Stressed", "Aggressive", "Not Found", "All Moods"
        };

        int moodIndex = 8;

        for(int i=0; i<allTexts.length; i++){
            if(allTexts[i].equalsIgnoreCase(mood)){
                moodIndex = i;
                break;
            }
        }

        switch (moodIndex){
            case 0:
                return SongMoodCategory.scExcited;
            case 1:
                return SongMoodCategory.scHappy;
            case 2:
                return SongMoodCategory.scChilled;
            case 3:
                return SongMoodCategory.scPeaceful;
            case 4:
                return SongMoodCategory.scBored;
            case 5 :
                return SongMoodCategory.scDepressed;
            case 6:
                return SongMoodCategory.scStressed;
            case 7:
                return SongMoodCategory.scAggressive;
            case 8:
                return SongMoodCategory.scMoodNotFound;
        }

        return SongMoodCategory.scMoodNotFound;
    }

    public static int getIntValue(SongMoodCategory moodCategory) {
        switch (moodCategory){
            case scExcited:
                return 0;
            case scHappy:
                return 1;
            case scChilled:
                return 2;
            case scPeaceful:
                return 3;
            case scBored:
                return 4;
            case scDepressed:
                return 5;
            case scStressed:
                return 6;
            case scAggressive:
                return 7;
        }
        return 8;
    }
}
