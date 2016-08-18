package com.agiliztech.musicescape.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.agiliztech.musicescape.models.SongsModel;

import java.util.ArrayList;

/**
 * Created by Pooja on 11-08-2016.
 */
public class SongsManager {
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
}
