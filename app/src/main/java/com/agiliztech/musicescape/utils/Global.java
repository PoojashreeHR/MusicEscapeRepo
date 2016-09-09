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
    public static ArrayList<Song> currentSongList;
    public static boolean isJourney;
    public static String INTROSCREENSSHOWN = "introScreensShown";
    public static boolean isLibPlaylist;
    public static ArrayList<Song> libPlaylistSongs;
    public static String isScannedOnce = "isScannedOnce";
    public static boolean HALT_API = false;
}
