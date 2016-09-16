package com.agiliztech.musicescape.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.agiliztech.musicescape.journey.SongMoodCategory;
import com.agiliztech.musicescape.models.Song;
import com.agiliztech.musicescape.models.SongsModel;

import com.agiliztech.musicescape.models.apimodels.SongInfo;
import com.agiliztech.musicescape.models.apimodels.SongRequest;
import com.agiliztech.musicescape.models.apimodels.SpotifyInfo;
import com.agiliztech.musicescape.utils.SongsManager;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Asif on 19-08-2016.
 */
public class DBHandler extends SQLiteOpenHelper {


    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Songs Database Name
    private static final String DATABASE_NAME = "songsManager";

    // Songs table name
    private static final String TABLE_SONGS = "tbl_songs";

    // Songs Table Columns names
    private static final String KEY_ID = "id";  //primary key
    private static final String KEY_CLIENT_ID = "song_id"; // int (device SongRequest id)
    private static final String KEY_SONG_TITLE = "song_title";  // SongRequest Name
    private static final String KEY_ARTIST_NAME = "artist_name";    // Artist Name
    private static final String KEY_ALBUM_NAME = "album_name";      // Album Name
    private static final String KEY_STATUS = "status";  //"scan"(first time song added),"scan_error"(if Error occurs)
    private static final String KEY_SONG_MOOD = "song_mood";        // song mood
    private static final String KEY_META_DATA = "meta_data";        // empty field now
    private static final String KEY_ENERGY = "song_energy";         // song energy
    private static final String KEY_VALENCE = "song_valence";       // song valence
    private static final String KEY_BATCH_ID = "batch_id";          // batch id returns from server
    private static final String KEY_SERVER_SONG_ID = "server_song_id";  // server sends their id for songs
    private static final String KEY_SPOTIFY_ID = "spotify_id";          // after hitting spotify api, will get spotify id
    private static final String KEY_API_STATUS = "analysing_status";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SONG_TABLE = "CREATE TABLE " + TABLE_SONGS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_CLIENT_ID + " INTEGER,"
                + KEY_SONG_TITLE + " TEXT,"
                + KEY_ARTIST_NAME + " TEXT,"
                + KEY_ALBUM_NAME + " TEXT,"
                + KEY_STATUS + " TEXT,"
                + KEY_SONG_MOOD + " TEXT,"
                + KEY_META_DATA + " TEXT,"
                + KEY_ENERGY + " TEXT,"
                + KEY_VALENCE + " TEXT,"
                + KEY_BATCH_ID + " TEXT,"
                + KEY_SERVER_SONG_ID + " INTEGER,"
                + KEY_SPOTIFY_ID + " TEXT,"
                + KEY_API_STATUS + " TEXT"
                + ")";
        db.execSQL(CREATE_SONG_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONGS);
        onCreate(db);
    }

    // Adding Device songs to db
    public void addDeviceSongsToDB(List<com.agiliztech.musicescape.models.Song> songsModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        for (int i = 0; i < songsModel.size(); i++) {
            values.put(KEY_CLIENT_ID, songsModel.get(i).getpID());
            values.put(KEY_SONG_TITLE, songsModel.get(i).getSongName());
            values.put(KEY_ARTIST_NAME, songsModel.get(i).getArtist().getArtistName());
            values.put(KEY_ALBUM_NAME, songsModel.get(i).getAlbum().getAlbumTitle());
            values.put(KEY_STATUS, "scan");
            values.put(KEY_SONG_MOOD, "");
            values.put(KEY_META_DATA, new Gson().toJson(songsModel.get(i)));
            values.put(KEY_ENERGY, songsModel.get(i).getEnergy());
            values.put(KEY_VALENCE, songsModel.get(i).getValance());
            values.put(KEY_BATCH_ID, "");
            values.put(KEY_SERVER_SONG_ID, "");
            values.put(KEY_SPOTIFY_ID, "");
            values.put(KEY_API_STATUS, "analysing");
            db.insert(TABLE_SONGS, null, values);
            Log.e("Inserted Songs ", " " + i);
        }
        //db.close();
    }

    //if(no energy and valence) execute if condition, else execute else (which has energy and valence and update in db)
    public void updateSongDetails(String batch, String clientId, double energy,
                                  double valence, String echonestAnalysisStatus, int serverSongId,
                                  String spotifyId,String mood) {
        SQLiteDatabase db = this.getWritableDatabase();
        int id = Integer.parseInt(clientId);
        int batchId = Integer.parseInt(batch);
        String echo = echonestAnalysisStatus;


        ContentValues values = new ContentValues();
        values.put(KEY_BATCH_ID, batch);
        values.put(KEY_STATUS, echo);
        values.put(KEY_SERVER_SONG_ID, serverSongId);
        values.put(KEY_SONG_MOOD,mood);
        if (spotifyId != null) {
            values.put(KEY_SPOTIFY_ID, spotifyId);
        }
        Song oldSong = getSongObject(id);
        if(oldSong != null){
            oldSong.setMood(SongsManager.getMoodForText(mood));
            values.put(KEY_META_DATA,new Gson().toJson(oldSong));
        }
        int x = db.update(TABLE_SONGS, values, KEY_CLIENT_ID + "=" + id, null);
        Log.e("UPDATED ", "UPDATED ROW " + x);
        //db.close();
        /*} else {
          *//*  db.rawQuery("UPDATE " + TABLE_SONGS + " SET " + KEY_BATCH_ID + "=\'" + batch +

        ContentValues values = new ContentValues();
        values.put(KEY_BATCH_ID, batch);
        values.put(KEY_STATUS, echo);
        values.put(KEY_SERVER_SONG_ID, serverSongId);
        if (spotifyId != null) {
            values.put(KEY_SPOTIFY_ID, spotifyId);
        }
        SongRequest oldSong = getSongObject(id);
        if(oldSong != null){
            values.put(KEY_META_DATA, new Gson().toJson(oldSong));
        }
        int x = db.update(TABLE_SONGS, values, KEY_CLIENT_ID + "=" + id, null);
        Log.e("UPDATED ", "UPDATED ROW " + x);
        //db.close();
        /*} else {
          *//*  db.rawQuery("UPDATE " + TABLE_SONGS + " SET " + KEY_BATCH_ID + "=\'" + batch +
                    "\'," + KEY_VALENCE + "=\'" + valence + "\'," + KEY_ENERGY + "=\'" + energy + "\' where " + KEY_CLIENT_ID + "=" + id + ";", null);
            //db.close();*//*
        }*/
    }


    // getting the songs list based on status="pending"
    public ArrayList<String> getSongsWithPendingStatus(String pending) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> songName = new ArrayList<>();
        String query = "select * from " + TABLE_SONGS + " where " +
                KEY_STATUS + " = \'" + pending + "\' OR " + KEY_STATUS + "=\'identify_error\' OR " +
                KEY_STATUS + "=\'identify_failed\'";// OR " + KEY_STATUS + "=\'identified\'" ;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(2);
                String id = cursor.getString(1);
                songName.add(name);

            } while (cursor.moveToNext());
        }
        return songName;
    }


    //getting the list of songs with status="scan" and status="scan_error"
    public ArrayList<SongRequest> getSongsBasedOnWhereParam(String whereParam1, String whereParam2) {
        ArrayList<SongRequest> getSongList = new ArrayList<>();
        String scan = whereParam1;
        String scan_error = whereParam2;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor findEntry = db.rawQuery("select * from " + TABLE_SONGS + " where " +
                KEY_STATUS + " = \'" + scan + "\'" + " or " + KEY_STATUS + "=\'" + scan_error + "\'", null);
        if (findEntry.moveToFirst()) {
            do {

                SongRequest model = new SongRequest();
                model.setSongName(findEntry.getString(2));
                model.setArtistName(findEntry.getString(3));
                model.setClientId(Integer.valueOf(findEntry.getString(1)));
                model.setAlbumName(findEntry.getString(4));
                getSongList.add(model);
            } while (findEntry.moveToNext());
        }
        return getSongList;

    }


    //remove songs which is removed from device
    public void removeDeviceSongsFromDB(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rwo = db.delete(TABLE_SONGS, KEY_CLIENT_ID + " = " + id, null);
        Log.e("DELETED ", " " + rwo);
        //db.close();
    }


    // Getting All Songs FROM DB
    public ArrayList<Song> getAllSongsFromDB() {
        ArrayList<Song> getSongList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SONGS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                String jsonStr = cursor.getString(cursor.getColumnIndex(KEY_META_DATA));
                Song model = new Gson().fromJson(jsonStr, Song.class);
                Log.e("GET ALL SONGS ", " FROM DB " + model.getSongName());
                // Adding contact to list
                getSongList.add(model);
            } while (cursor.moveToNext());
        }
        // return songs list
        return getSongList;
    }

    //update status="{spotify_id}"
    public void updateSongWithSpotifyID(String spotifyId, String songName) {
        if (songName.contains("\'")) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(KEY_SPOTIFY_ID, spotifyId);
            cv.put(KEY_STATUS, "identified");
            int x = db.update(TABLE_SONGS, cv, KEY_SONG_TITLE + "=\"" + songName + "\"", null);
            Log.e("UPDATED ", "SPOTIFY ID " + x);
            //db.close();
        } else if (songName.contains("\"")) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(KEY_SPOTIFY_ID, spotifyId);
            cv.put(KEY_STATUS, "identified");
            int x = db.update(TABLE_SONGS, cv, KEY_SONG_TITLE + "=\'" + songName + "\'", null);
            Log.e("UPDATED ", "SPOTIFY ID " + x);
            //db.close();
        } else {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(KEY_SPOTIFY_ID, spotifyId);
            cv.put(KEY_STATUS, "identified");
            int x = db.update(TABLE_SONGS, cv, KEY_SONG_TITLE + "=\'" + songName + "\'", null);
            Log.e("UPDATED ", "SPOTIFY ID " + x);
            //db.close();
        }

    }


    //update status="identify_error" if some error occured while getting the spotify_id
    public void updateSongStatusForSpotifyError(String songName) {
        if (songName.contains("\'")) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(KEY_STATUS, "identify_error");
            int x = db.update(TABLE_SONGS, cv, KEY_SONG_TITLE + "=\"" + songName + "\"", null);
            Log.e("UPDATED ", "SPOTIFY STATUS ERROR " + x + " : " + songName);
          //  //db.close();
        } else if (songName.contains("\"")) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(KEY_STATUS, "identify_error");
            int x = db.update(TABLE_SONGS, cv, KEY_SONG_TITLE + "=\'" + songName + "\'", null);
            Log.e("UPDATED ", "SPOTIFY STATUS ERROR " + x + " : " + songName);
           // //db.close();
        } else {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(KEY_STATUS, "identify_error");
            int x = db.update(TABLE_SONGS, cv, KEY_SONG_TITLE + "=\'" + songName + "\'", null);
            Log.e("UPDATED ", "SPOTIFY STATUS ERROR " + x + " : " + songName);
           // //db.close();
        }

    }


    public void updateSongWithAnalysingStatus(ArrayList<SpotifyInfo> spotifyInfos) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (int i = 0; i < spotifyInfos.size(); i++) {
            String query = "select * from " + TABLE_SONGS + " where " +
                    KEY_SERVER_SONG_ID + "=\'" + spotifyInfos.get(i).getId() + "\'";
            ContentValues cv = new ContentValues();




            cv.put(KEY_API_STATUS, "again_analysing");
            db.update(TABLE_SONGS, cv, KEY_SERVER_SONG_ID + " =\'" + spotifyInfos.get(i).getId() + "\'", null);
        }
        //db.close();

    }

    // get the songs id which is sent by the server
    public ArrayList<SpotifyInfo> getSongsWithServerIdAndSpotifyId() {
        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<SpotifyInfo> spotifyInfos = new ArrayList<>();
        String query = "select * from " + TABLE_SONGS + " where " +
                KEY_SPOTIFY_ID + " != \'" + "" + "\' AND " + KEY_API_STATUS + "=\'analysing\'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                SpotifyInfo spotifyInfo = new SpotifyInfo();
                spotifyInfo.setId(Integer.parseInt(cursor.getString(11)));
                spotifyInfo.setSpotifyId(cursor.getString(12));
                spotifyInfos.add(spotifyInfo);
            } while (cursor.moveToNext());
        }
        return spotifyInfos;
    }

    public void updateSongsWithEnergyAndValence(ArrayList<SongInfo> model) {
        SQLiteDatabase db = this.getWritableDatabase();
        //ArrayList<SongInfo> info = model;

        for (int i = 0; i < model.size(); i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_ENERGY, model.get(i).getEnergy());
            contentValues.put(KEY_VALENCE, model.get(i).getValence());
            contentValues.put(KEY_SONG_MOOD, model.get(i).getMood());
            contentValues.put(KEY_STATUS, model.get(i).getEchonestAnalysisStatus());
            Song oldSong = getSongObject(model.get(i).getId());
            if(oldSong != null){
                oldSong.setEnergy((float) model.get(i).getEnergy());
                oldSong.setValance((float) model.get(i).getValence());
                oldSong.setMood(SongsManager.getMoodForText(model.get(i).getMood()));
                contentValues.put(KEY_META_DATA, new Gson().toJson(oldSong));
            }
            contentValues.put(KEY_API_STATUS, "analysed");
            int x = db.update(TABLE_SONGS, contentValues, KEY_SERVER_SONG_ID + "=\'" + model.get(i).getId() + "\'", null);
            //Log.e("UPDATED ", "SPOTIFY STATUS ERROR " + x + " : " + KEY_SERVER_SONG_ID);

        }
        //db.close();
    }

    public int getRowCount() {
        SQLiteDatabase db = this.getWritableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, TABLE_SONGS, KEY_STATUS + "=\'identified\'", null);
        Log.e("COUNT PRINTING ", " COUNT(*) : " + count);
        return (int) count;
    }

    public int getMoodCount(String mood) {
        SQLiteDatabase db = this.getWritableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, TABLE_SONGS, KEY_SONG_MOOD + "=\'" + mood + "\'", null);
        Log.e(" Count : ", mood + " count : " + (int)count);
        return (int) count;
    }


    public ArrayList<Song> getSongsListBasedOnMoods(String mood) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Song> models = new ArrayList<>();
        String query = "select * from " + TABLE_SONGS + " where " +
                KEY_SONG_MOOD + "= \'" + mood + "\'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                String jsonStr = cursor.getString(cursor.getColumnIndex(KEY_META_DATA));
                Song model = new Gson().fromJson(jsonStr, Song.class);
                if(model.getMood() == null){
                    model.setMood(SongsManager.getMoodForText(mood));
                }
                models.add(model);
            } while (cursor.moveToNext());
        }
        return models;
    }

    public ArrayList<SpotifyInfo> getSongsWithServerIdAndSpotifyIdWithLimit() {
        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<SpotifyInfo> spotifyInfos = new ArrayList<>();
        String query = "select * from " + TABLE_SONGS + " where " +
                KEY_SPOTIFY_ID + " != \'" + "" + "\' AND " + KEY_API_STATUS + "=\'\"\"\'";
          Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                SpotifyInfo spotifyInfo = new SpotifyInfo();
                spotifyInfo.setId(Integer.parseInt(cursor.getString(11)));
                spotifyInfo.setSpotifyId(cursor.getString(12));
                spotifyInfos.add(spotifyInfo);
            } while (cursor.moveToNext());
        }
        return spotifyInfos;
    }



    public Song getSongObject(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from " + TABLE_SONGS + " where " +
                KEY_SERVER_SONG_ID + "= \'" + id + "\'";
        Cursor cursor = db.rawQuery(query, null);
        Song model = null;
        if (cursor.moveToFirst()) {
            do {
                String jsonStr = cursor.getString(cursor.getColumnIndex(KEY_META_DATA));
                 model = new Gson().fromJson(jsonStr, Song.class);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return model;
    }


    public Song getSongObjectFromServerid(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from " + TABLE_SONGS + " where " +
                KEY_SERVER_SONG_ID + "= \'" + id + "\'";
        Cursor cursor = db.rawQuery(query, null);
        Song model = null;
        if (cursor.moveToFirst()) {
            do {
                String jsonStr = cursor.getString(cursor.getColumnIndex(KEY_META_DATA));
                model = new Gson().fromJson(jsonStr, Song.class);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return model;
    }

    public void updateSongObj(Song song){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_META_DATA, new Gson().toJson(song));
        int x = db.update(TABLE_SONGS, contentValues, KEY_SERVER_SONG_ID + "=\'" + song.getpID() + "\'", null);
        //db.close();

    }



    public List<Song> getSongItemInSongbasedOnMood(SongMoodCategory mood, List<Song> playList) {
        String moodName = SongsManager.textForMood(SongsManager.getIntValue(mood));
        if(moodName != null){
            moodName = moodName.toLowerCase();
            moodName = moodName.trim();
            List<Song> moodSongs = getSongsListBasedOnMoods(moodName);

            boolean removed = moodSongs.removeAll(playList);
            return moodSongs;
        }

        return null;
    }

    public int getAnalysedCount() {
        SQLiteDatabase db = this.getWritableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, TABLE_SONGS, KEY_STATUS + "=\'analysed\'", null);
        Log.e("COUNT PRINTING ", " COUNT(*) : " + count);
        //db.close();
        return (int) count;
    }
    public int getExceptAnalysedCount() {
        SQLiteDatabase db = this.getWritableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, TABLE_SONGS, KEY_STATUS + "=\'pending\' OR "
                + KEY_STATUS + " =\'identify_error\' OR "
                + KEY_STATUS + " =\'identify_failed\'", null);
        Log.e("COUNT PRINTING ", " COUNT(*) : " + count);
        //db.close();
        return (int) count;
    }

    public int getServerSongId(int i) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT "+KEY_SERVER_SONG_ID+" FROM " +
                TABLE_SONGS + " WHERE " + KEY_CLIENT_ID + "=" + i,null);
        int x = 0;
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                x=  Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_SERVER_SONG_ID)));
            } while (cursor.moveToNext());
        }
         return x;
    }

    public void updateSongStatusWithModifiedMood(String mood,int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        Song oldSong = getSongObject(id);
        if(oldSong != null){
            oldSong.setMood(SongsManager.getMoodForText(mood));
            cv.put(KEY_META_DATA,new Gson().toJson(oldSong));
        }
        cv.put(KEY_SONG_MOOD,mood.toLowerCase());
        cv.put(KEY_STATUS,"analysed");
        int x = db.update(TABLE_SONGS,cv,KEY_SERVER_SONG_ID + "="+ id,null);
        Log.e("SongMood", "SONG MOOD UPDATED " + x);
        //db.close();
    }


}
