package com.agiliztech.musicescape.utils;

import com.agiliztech.musicescape.models.Song;

import java.util.ArrayList;

/**
 * Created by praburaam on 25/08/16.
 */
public class Global {
    public static final String DBNAME = "MusEscDB";
    public static final int DBVERSION = 1;

    public static final String JOURNEY_TBL_NAME = "TblJourney";
    public static final String JOURNEY_SESSION_TBL_NAME = "TblJourneySession";
    public static ArrayList<Song> currentSongList;
    public static boolean isJourney;
    public static String INTROSCREENSSHOWN = "introScreensShown";
}
