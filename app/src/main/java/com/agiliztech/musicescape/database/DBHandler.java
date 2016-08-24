package com.agiliztech.musicescape.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.agiliztech.musicescape.models.SongsModel;
import com.agiliztech.musicescape.models.apimodels.Song;
import com.agiliztech.musicescape.models.apimodels.SpotifyInfo;

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
    private static final String KEY_CLIENT_ID = "song_id"; // int (device Song id)
    private static final String KEY_SONG_TITLE = "song_title";  // Song Name
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
                + KEY_SPOTIFY_ID + " TEXT"
                + ")";
        db.execSQL(CREATE_SONG_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONGS);
        onCreate(db);
    }

    // Adding Device songs to db
    public void addDeviceSongsToDB(List<SongsModel> songsModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        for (int i = 0; i < songsModel.size(); i++) {
            values.put(KEY_CLIENT_ID, songsModel.get(i).getId());
            values.put(KEY_SONG_TITLE, songsModel.get(i).getTitle());
            values.put(KEY_ARTIST_NAME, songsModel.get(i).getArtist());
            values.put(KEY_ALBUM_NAME, songsModel.get(i).getAlbumName());
            values.put(KEY_STATUS, "scan");
            values.put(KEY_SONG_MOOD, "");
            values.put(KEY_META_DATA, "");
            values.put(KEY_ENERGY, "");
            values.put(KEY_VALENCE, "");
            values.put(KEY_BATCH_ID, "");
            values.put(KEY_SERVER_SONG_ID, "");
            values.put(KEY_SPOTIFY_ID, "");
            db.insert(TABLE_SONGS, null, values);
            Log.e("Inserted Songs ", " " + i);
        }
        db.close();
    }

    //if(no energy and valence) execute if condition, else execute else (which has energy and valence and update in db)
    public void updateSongDetails(String batch, String clientId, double energy,
                                  double valence, String echonestAnalysisStatus, int serverSongId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int id = Integer.parseInt(clientId);
        int batchId = Integer.parseInt(batch);
        String echo = echonestAnalysisStatus;

        if (energy == 0.0 && valence == 0.0) {
            ContentValues values = new ContentValues();
            values.put(KEY_BATCH_ID, batch);
            values.put(KEY_STATUS, echo);
            values.put(KEY_SERVER_SONG_ID, serverSongId);
            int x = db.update(TABLE_SONGS, values, KEY_CLIENT_ID + "=" + id, null);
            Log.e("UPDATED ", "UPDATED ROW " + x);
            db.close();
        } else {
          /*  db.rawQuery("UPDATE " + TABLE_SONGS + " SET " + KEY_BATCH_ID + "=\'" + batch +
                    "\'," + KEY_VALENCE + "=\'" + valence + "\'," + KEY_ENERGY + "=\'" + energy + "\' where " + KEY_CLIENT_ID + "=" + id + ";", null);
            db.close();*/
        }
    }


    // getting the songs list based on status="pending"
    public ArrayList<String> getSongsWithPendingStatus(String pending) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<String> songName = new ArrayList<>();
        String query = "select * from " + TABLE_SONGS + " where " +
                KEY_STATUS + " = \'" + pending + "\'";
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
    public ArrayList<Song> getSongsBasedOnWhereParam(String whereParam1, String whereParam2) {
        ArrayList<Song> getSongList = new ArrayList<>();
        String scan = whereParam1;
        String scan_error = whereParam2;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor findEntry = db.rawQuery("select * from " + TABLE_SONGS + " where " +
                KEY_STATUS + " = \'" + scan + "\'" + " or " + KEY_STATUS + "=\'" + scan_error + "\'", null);
        if (findEntry.moveToFirst()) {
            do {
                Song model = new Song();
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
        db.close();
    }


    // Getting All Songs FROM DB
    public ArrayList<SongsModel> getAllSongsFromDB() {
        ArrayList<SongsModel> getSongList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SONGS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                SongsModel model = new SongsModel();
                model.setId(Integer.parseInt(cursor.getString(1)));
                model.setTitle(cursor.getString(2));
                model.setArtist(cursor.getString(3));
                model.setAlbumName(cursor.getString(4));
                // Adding contact to list
                getSongList.add(model);
            } while (cursor.moveToNext());
        }
        // return songs list
        return getSongList;
    }

    //update status="{spotify_id}"
    public void updateSongWithSpotifyID(String spotifyId, String songName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_SPOTIFY_ID, spotifyId);
        cv.put(KEY_STATUS, "identified");
        int x = db.update(TABLE_SONGS, cv, KEY_SONG_TITLE + "=\'" + songName + "\'", null);
        Log.e("UPDATED ", "SPOTIFY ID " + x);
        db.close();
    }


    //update status="identify_error" if some error occured while getting the spotify_id
    public void updateSongStatusForSpotifyError(String songName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_STATUS, "identify_error");
        int x = db.update(TABLE_SONGS, cv, KEY_SONG_TITLE + "=\'" + songName + "\'", null);
        Log.e("UPDATED ", "SPOTIFY STATUS ERROR " + x + " : " + songName);
        db.close();
    }


    // get the songs id which is sent by the server
    public ArrayList<SpotifyInfo> getSongsIdSentFromServer() {
        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<SpotifyInfo> spotifyInfos = new ArrayList<>();
        String query = "select * from " + TABLE_SONGS + " where " +
                KEY_STATUS + " = \'" + "pending" + "\'";
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
}