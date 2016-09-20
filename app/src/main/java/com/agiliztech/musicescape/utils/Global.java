package com.agiliztech.musicescape.utils;

import com.agiliztech.musicescape.models.Song;

import java.util.ArrayList;

/**
 * Created by praburaam on 25/08/16.
 */
public class Global {
    public static final String DBNAME = "MusEscDB";
    public static final int DBVERSION = 2;

    public static final String JOURNEY_TBL_NAME = "TblJourney";
    public static final String JOURNEY_SESSION_TBL_NAME = "TblJourneySession";
    public static final String PREF_NAME = "MyPrefs";

    //Last journey song info
    public static final String LAST_PL_TYPE = "ms_last_pl_type";
    public static final String LAST_JOURNEY_ID = "ms_last_journey_id";
    public static final String LAST_SONG_POS = "ms_last_song_pos";

    public static ArrayList<Song> currentSongList;
    public static boolean CONTINUE_API = true;
    public static boolean isJourney;
    public static String INTROSCREENSSHOWN = "introScreensShown";
    public static boolean isLibPlaylist;
    public static ArrayList<Song> libPlaylistSongs;
    public static String isScannedOnce = "isScannedOnce";
    public static boolean HALT_API = false;


}
