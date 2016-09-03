package com.agiliztech.musicescape.journey;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PointF;

import com.agiliztech.musicescape.database.DBHandler;
import com.agiliztech.musicescape.models.Journey;
import com.agiliztech.musicescape.models.JourneySession;
import com.agiliztech.musicescape.models.Song;
import com.agiliztech.musicescape.models.WeightObject;
import com.agiliztech.musicescape.utils.Global;
import com.agiliztech.musicescape.utils.SongsManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Created by praburaam on 25/08/16.
 */
public class JourneyService {
    private static Context mContext;
    private JourneySession currentSession;
    private List<Integer> trackDots;

    static JourneyService sharedInstance;
    private float kMaxRange = 0.2f;

    public static JourneyService getInstance(Context context) {
        mContext = context;
        if (sharedInstance == null) {
            sharedInstance = new JourneyService();
            sharedInstance.currentSession = new JourneySession();
        }
        return sharedInstance;
    }

    public List<Journey> getAllFavouriteAndPresetsJourneys() {
        List<Journey> journeys = null;

        JourneyDBHelper journeyDBHelper = new JourneyDBHelper(mContext);
        List<Journey> presetsList = journeyDBHelper.getJourneysBy(JourneyDBHelper.COL_GEN_BY, "Presets");
        journeyDBHelper.close();

        if (presetsList != null && presetsList.size() > 0) {
            journeys = presetsList;
        }

        JourneySessionDBHelper journeySessionDBHelper = new JourneySessionDBHelper(mContext);
        List<JourneySession> favsList = journeySessionDBHelper.getFavouriteJourneySessions();
        journeySessionDBHelper.close();

        if (favsList.size() > 0) {

            if (journeys == null) {
                journeys = new ArrayList<Journey>();
            }

            for (JourneySession session : favsList) {
                journeys.add(session.getJourney());
            }
        }

        return journeys;
    }

    public void unFavouriteJourney(JourneySession session) {
        session.setFavourite(0);
    }

    public void generatePresetsIfRequired() {
        if (needsToGeneratePresets()) {
            generatePresets();
        }
    }

    private void generatePresets() {
        JourneyDBHelper journeyDBHelper = new JourneyDBHelper(mContext);
        List<Journey> allJourneys = journeyDBHelper.getAllJourneys();

        if (allJourneys.size() > 0) {
            createCheerUpJourney();
            createWorkOutJourney();
            createPartyJourney();
            createChillJourney();
            createWakeUpJourney();
            createCoolOffJourney();
            createFocusJourney();
        } else {
            for (Journey j : allJourneys) {
                List<Song> songArrayWithNull = returnEVArrayFromJourney(j.getJourneyEVArray());
                List<Song> filterNilArray = filterNullSongs(songArrayWithNull);
                j.setTrackCount(filterNilArray.size());
                journeyDBHelper.addJourney(j);
            }
        }

        journeyDBHelper.close();
    }

    public List<Song> filterNullSongs(List<Song> songArrayWithNull) {
        songArrayWithNull.removeAll(Collections.singleton(null));
        return songArrayWithNull;
    }

    private List<Song> returnEVArrayFromJourney(List<PointF> journeyEVArray) {
        List<Song> playListArray = new ArrayList<Song>();
        for (PointF EV : journeyEVArray) {
            Song songItem = findSongBasedOnEV(EV, playListArray);
            if (songItem != null) {
                playListArray.add(songItem);
            } else {
                playListArray.add(null);
            }
        }
        return playListArray;
    }

    private Song findSongBasedOnEV(PointF ev, List<Song> playListArray) {
        float e = ev.y;
        float v = ev.x;

        SongMoodCategory mood = SongsService.getInstance(mContext).checkMoodForEnergyAndValence(e, v);
        List<Song> shortListedArray = null;

        List<Song> filteredSongArray = filterSongByMoodNotinPlaylist(mood, playListArray);

        for (float searchRange = 0.01f; searchRange <= kMaxRange; searchRange = searchRange + 0.01f) {
            shortListedArray = filteredArrayUsingPredicate(filteredSongArray, e, v, searchRange);

            if (shortListedArray != null) {
                if (shortListedArray.size() == 1) {
                    return shortListedArray.get(0);
                } else if (shortListedArray.size() >= 2) {
                    Random r = new Random();
                    int randomRange = shortListedArray.size();
                    return shortListedArray.get(r.nextInt(randomRange));
                }
            }
        }

        return null;
    }

    private List<Song> filteredArrayUsingPredicate(List<Song> filteredSongArray, float e, float v, float searchRange) {
        List<Song> filteredSongs = new ArrayList<>();
        for (Song song : filteredSongArray) {
            if ((song.getEnergy() >= e - searchRange && song.getEnergy() <= e + searchRange) &&
                    (song.getValance() >= v - searchRange && song.getValance() <= v + searchRange)) {
                filteredSongs.add(song);
            }
        }
        return filteredSongs;
    }

    private List<Song> filterSongByMoodNotinPlaylist(SongMoodCategory mood, List<Song> playList) {
        DBHandler dbHandler = new DBHandler(mContext);
        List<Song> songs = dbHandler.getSongItemInSongbasedOnMood(mood, playList);
        dbHandler.close();
        return songs;
    }

    private void createFocusJourney() {
        Journey j = new Journey();
        j.setGeneratedBy("Preset");
        j.setName("Focus");

        List<PointF> journeyEVArray = new ArrayList<>();
        List<Integer> journeyDotsArray = new ArrayList<>();

        journeyDotsArray.add(715);
        journeyDotsArray.add(725);
        journeyDotsArray.add(733);
        journeyDotsArray.add(743);
        journeyDotsArray.add(742);
        journeyDotsArray.add(751);
        journeyDotsArray.add(760);
        journeyDotsArray.add(759);
        journeyDotsArray.add(769);
        journeyDotsArray.add(777);
        journeyDotsArray.add(784);
        journeyDotsArray.add(791);
        journeyDotsArray.add(790);
        journeyDotsArray.add(795);
        journeyDotsArray.add(794);
        journeyDotsArray.add(797);
        journeyDotsArray.add(798);
        journeyDotsArray.add(919);
        journeyDotsArray.add(677);
        journeyDotsArray.add(675);
        journeyDotsArray.add(674);
        journeyDotsArray.add(671);
        journeyDotsArray.add(672);
        journeyDotsArray.add(668);
        journeyDotsArray.add(664);
        journeyDotsArray.add(660);
        journeyDotsArray.add(654);
        journeyDotsArray.add(647);
        journeyDotsArray.add(648);
        journeyDotsArray.add(642);
        journeyDotsArray.add(635);
        journeyDotsArray.add(636);
        journeyDotsArray.add(630);
        journeyDotsArray.add(624);
        journeyDotsArray.add(618);
        journeyDotsArray.add(619);
        journeyDotsArray.add(612);
        journeyDotsArray.add(613);
        journeyDotsArray.add(607);
        journeyDotsArray.add(608);
        journeyDotsArray.add(602);
        journeyDotsArray.add(379);
        journeyDotsArray.add(459);
        journeyDotsArray.add(368);
        journeyDotsArray.add(369);
        journeyDotsArray.add(357);
        journeyDotsArray.add(358);
        journeyDotsArray.add(348);

        journeyEVArray.add(new PointF(0.20160715f, 0.64023125f));
        journeyEVArray.add(new PointF(0.24508929f, 0.63460624f));
        journeyEVArray.add(new PointF(0.2832143f, 0.60648125f));
        journeyEVArray.add(new PointF(0.31508932f, 0.61640626f));
        journeyEVArray.add(new PointF(0.31508932f, 0.59523124f));
        journeyEVArray.add(new PointF(0.34339288f, 0.60265625f));
        journeyEVArray.add(new PointF(0.36723217f, 0.59695625f));
        journeyEVArray.add(new PointF(0.36723217f, 0.58203125f));
        journeyEVArray.add(new PointF(0.3866072f, 0.58070624f));
        journeyEVArray.add(new PointF(0.4041965f, 0.56828129f));
        journeyEVArray.add(new PointF(0.4217858f, 0.55585623f));
        journeyEVArray.add(new PointF(0.43795538f, 0.55587494f));
        journeyEVArray.add(new PointF(0.43795538f, 0.54356241f));
        journeyEVArray.add(new PointF(0.45570537f, 0.54356241f));
        journeyEVArray.add(new PointF(0.45570537f, 0.53124988f));
        journeyEVArray.add(new PointF(0.47345537f, 0.51893735f));
        journeyEVArray.add(new PointF(0.49120536f, 0.50662488f));
        journeyEVArray.add(new PointF(0.49120536f, 0.49337476f));
        journeyEVArray.add(new PointF(0.50879461f, 0.48106223f));
        journeyEVArray.add(new PointF(0.50879461f, 0.46874976f));
        journeyEVArray.add(new PointF(0.52654463f, 0.45643723f));
        journeyEVArray.add(new PointF(0.52654463f, 0.4441247f));
        journeyEVArray.add(new PointF(0.54429466f, 0.4441247f));
        journeyEVArray.add(new PointF(0.54429466f, 0.43181223f));
        journeyEVArray.add(new PointF(0.54429466f, 0.4194997f));
        journeyEVArray.add(new PointF(0.56204462f, 0.40718722f));
        journeyEVArray.add(new PointF(0.56204462f, 0.39487469f));
        journeyEVArray.add(new PointF(0.56204462f, 0.38256216f));
        journeyEVArray.add(new PointF(0.57979465f, 0.38256216f));
        journeyEVArray.add(new PointF(0.58783036f, 0.36962467f));
        journeyEVArray.add(new PointF(0.57554466f, 0.35231215f));
        journeyEVArray.add(new PointF(0.60579461f, 0.35231215f));
        journeyEVArray.add(new PointF(0.60579461f, 0.33124971f));
        journeyEVArray.add(new PointF(0.61829466f, 0.30893725f));
        journeyEVArray.add(new PointF(0.61829466f, 0.28537476f));
        journeyEVArray.add(new PointF(0.65211606f, 0.28537476f));
        journeyEVArray.add(new PointF(0.64329463f, 0.25931227f));
        journeyEVArray.add(new PointF(0.68425894f, 0.25931227f));
        journeyEVArray.add(new PointF(0.7083661f, 0.2288748f));
        journeyEVArray.add(new PointF(0.75468749f, 0.2288748f));
        journeyEVArray.add(new PointF(0.75468749f, 0.19656235f));
        journeyEVArray.add(new PointF(0.79839295f, 0.20389372f));
        journeyEVArray.add(new PointF(0.81f, 0.18300003f));
        journeyEVArray.add(new PointF(0.84366077f, 0.19201875f));
        journeyEVArray.add(new PointF(0.84366077f, 0.15959376f));
        journeyEVArray.add(new PointF(0.89696437f, 0.18186873f));
        journeyEVArray.add(new PointF(0.89696437f, 0.13944376f));


        j.setJourneyDotsArray(journeyDotsArray);
        j.setJourneyEVArray(journeyEVArray);

        List<Song> examplePlaylist = returnEVArrayFromJourney(journeyEVArray);
        List<Song> filterNilArray = filterNullSongs(examplePlaylist);
        j.setTrackCount(filterNilArray.size());

        JourneyDBHelper journeyDBHelper = new JourneyDBHelper(mContext);
        journeyDBHelper.addJourney(j);
    }

    private void createCoolOffJourney() {
        Journey j = new Journey();
        j.setGeneratedBy("Preset");
        j.setName("Cool Off");

        List<PointF> journeyEVArray = new ArrayList<>();
        List<Integer> journeyDotsArray = new ArrayList<>();

        journeyDotsArray.add(5);
        journeyDotsArray.add(12);
        journeyDotsArray.add(11);
        journeyDotsArray.add(18);
        journeyDotsArray.add(17);
        journeyDotsArray.add(23);
        journeyDotsArray.add(29);
        journeyDotsArray.add(34);
        journeyDotsArray.add(40);
        journeyDotsArray.add(39);
        journeyDotsArray.add(46);
        journeyDotsArray.add(45);
        journeyDotsArray.add(51);
        journeyDotsArray.add(50);
        journeyDotsArray.add(56);
        journeyDotsArray.add(62);
        journeyDotsArray.add(61);
        journeyDotsArray.add(66);
        journeyDotsArray.add(72);
        journeyDotsArray.add(182);
        journeyDotsArray.add(189);
        journeyDotsArray.add(196);
        journeyDotsArray.add(202);
        journeyDotsArray.add(207);
        journeyDotsArray.add(322);
        journeyDotsArray.add(321);
        journeyDotsArray.add(313);
        journeyDotsArray.add(312);
        journeyDotsArray.add(302);
        journeyDotsArray.add(292);
        journeyDotsArray.add(413);
        journeyDotsArray.add(405);
        journeyDotsArray.add(406);
        journeyDotsArray.add(398);
        journeyDotsArray.add(399);
        journeyDotsArray.add(390);
        journeyDotsArray.add(391);
        journeyDotsArray.add(382);
        journeyDotsArray.add(383);
        journeyDotsArray.add(373);
        journeyDotsArray.add(374);
        journeyDotsArray.add(363);
        journeyDotsArray.add(364);
        journeyDotsArray.add(354);
        journeyDotsArray.add(355);
        journeyDotsArray.add(346);
        journeyDotsArray.add(347);

        journeyEVArray.add(new PointF(0.10781251f, 0.97393751f));
        journeyEVArray.add(new PointF(0.10781251f, 0.92412502f));
        journeyEVArray.add(new PointF(0.17913394f, 0.92412502f));
        journeyEVArray.add(new PointF(0.16674107f, 0.87806249f));
        journeyEVArray.add(new PointF(0.22734822f, 0.87806249f));
        journeyEVArray.add(new PointF(0.25949109f, 0.83825004f));
        journeyEVArray.add(new PointF(0.29163393f, 0.80343747f));
        journeyEVArray.add(new PointF(0.33795536f, 0.77112496f));
        journeyEVArray.add(new PointF(0.35670537f, 0.74068749f));
        journeyEVArray.add(new PointF(0.39766964f, 0.74068749f));
        journeyEVArray.add(new PointF(0.38170537f, 0.714625f));
        journeyEVArray.add(new PointF(0.41552681f, 0.714625f));
        journeyEVArray.add(new PointF(0.41552681f, 0.69106245f));
        journeyEVArray.add(new PointF(0.44934824f, 0.69106245f));
        journeyEVArray.add(new PointF(0.45470539f, 0.66874993f));
        journeyEVArray.add(new PointF(0.45470539f, 0.64768744f));
        journeyEVArray.add(new PointF(0.48495537f, 0.64768744f));
        journeyEVArray.add(new PointF(0.49031252f, 0.63037491f));
        journeyEVArray.add(new PointF(0.49120536f, 0.61743748f));
        journeyEVArray.add(new PointF(0.50879461f, 0.61743748f));
        journeyEVArray.add(new PointF(0.50879461f, 0.60512495f));
        journeyEVArray.add(new PointF(0.52654463f, 0.59281242f));
        journeyEVArray.add(new PointF(0.54429466f, 0.58049989f));
        journeyEVArray.add(new PointF(0.56204462f, 0.56818742f));
        journeyEVArray.add(new PointF(0.57821429f, 0.55585623f));
        journeyEVArray.add(new PointF(0.57821429f, 0.54343128f));
        journeyEVArray.add(new PointF(0.59580356f, 0.54343128f));
        journeyEVArray.add(new PointF(0.59580356f, 0.53100622f));
        journeyEVArray.add(new PointF(0.61339283f, 0.51858127f));
        journeyEVArray.add(new PointF(0.63276786f, 0.50740623f));
        journeyEVArray.add(new PointF(0.63276786f, 0.49259377f));
        journeyEVArray.add(new PointF(0.65660715f, 0.49071878f));
        journeyEVArray.add(new PointF(0.65660715f, 0.47204375f));
        journeyEVArray.add(new PointF(0.68491077f, 0.46829379f));
        journeyEVArray.add(new PointF(0.68491077f, 0.44711876f));
        journeyEVArray.add(new PointF(0.71678573f, 0.44086874f));
        journeyEVArray.add(new PointF(0.71678573f, 0.41719377f));
        journeyEVArray.add(new PointF(0.75491077f, 0.42524374f));
        journeyEVArray.add(new PointF(0.75491077f, 0.39531875f));
        journeyEVArray.add(new PointF(0.79839295f, 0.39094377f));
        journeyEVArray.add(new PointF(0.79839295f, 0.35976875f));
        journeyEVArray.add(new PointF(0.84366077f, 0.35414374f));
        journeyEVArray.add(new PointF(0.84366077f, 0.32171875f));
        journeyEVArray.add(new PointF(0.89696437f, 0.30914378f));
        journeyEVArray.add(new PointF(0.89696437f, 0.26671875f));
        journeyEVArray.add(new PointF(0.9627679f, 0.22546875f));

        j.setJourneyDotsArray(journeyDotsArray);
        j.setJourneyEVArray(journeyEVArray);

        List<Song> examplePlaylist = returnEVArrayFromJourney(journeyEVArray);
        List<Song> filterNilArray = filterNullSongs(examplePlaylist);
        j.setTrackCount(filterNilArray.size());

        JourneyDBHelper journeyDBHelper = new JourneyDBHelper(mContext);
        journeyDBHelper.addJourney(j);
    }

    private void createWakeUpJourney() {
        Journey j = new Journey();
        j.setGeneratedBy("Preset");
        j.setName("Wake up");

        List<PointF> journeyEVArray = new ArrayList<>();
        List<Integer> journeyDotsArray = new ArrayList<>();


        journeyDotsArray.add(577);
        journeyDotsArray.add(576);
        journeyDotsArray.add(583);
        journeyDotsArray.add(582);
        journeyDotsArray.add(589);
        journeyDotsArray.add(588);
        journeyDotsArray.add(594);
        journeyDotsArray.add(600);
        journeyDotsArray.add(605);
        journeyDotsArray.add(611);
        journeyDotsArray.add(618);
        journeyDotsArray.add(617);
        journeyDotsArray.add(623);
        journeyDotsArray.add(629);
        journeyDotsArray.add(635);
        journeyDotsArray.add(641);
        journeyDotsArray.add(647);
        journeyDotsArray.add(653);
        journeyDotsArray.add(659);
        journeyDotsArray.add(664);
        journeyDotsArray.add(668);
        journeyDotsArray.add(671);
        journeyDotsArray.add(674);
        journeyDotsArray.add(676);
        journeyDotsArray.add(456);
        journeyDotsArray.add(455);
        journeyDotsArray.add(334);
        journeyDotsArray.add(335);
        journeyDotsArray.add(214);
        journeyDotsArray.add(212);
        journeyDotsArray.add(209);
        journeyDotsArray.add(205);
        journeyDotsArray.add(201);
        journeyDotsArray.add(196);
        journeyDotsArray.add(190);
        journeyDotsArray.add(183);
        journeyDotsArray.add(177);
        journeyDotsArray.add(172);
        journeyDotsArray.add(166);
        journeyDotsArray.add(160);
        journeyDotsArray.add(161);
        journeyDotsArray.add(155);
        journeyDotsArray.add(149);
        journeyDotsArray.add(143);
        journeyDotsArray.add(137);
        journeyDotsArray.add(138);
        journeyDotsArray.add(132);
        journeyDotsArray.add(126);
        journeyDotsArray.add(127);
        journeyDotsArray.add(120);
        journeyDotsArray.add(121);
        journeyDotsArray.add(114);
        journeyDotsArray.add(115);


        journeyEVArray.add(new PointF(0.89218748f, 0.026062489f));
        journeyEVArray.add(new PointF(0.82086605f, 0.026062489f));
        journeyEVArray.add(new PointF(0.82086605f, 0.075874925f));
        journeyEVArray.add(new PointF(0.74954462f, 0.075874925f));
        journeyEVArray.add(new PointF(0.77265179f, 0.12193739f));
        journeyEVArray.add(new PointF(0.71204466f, 0.12193739f));
        journeyEVArray.add(new PointF(0.68704462f, 0.1617499f));
        journeyEVArray.add(new PointF(0.66204464f, 0.19656235f));
        journeyEVArray.add(new PointF(0.61572319f, 0.2288748f));
        journeyEVArray.add(new PointF(0.60233033f, 0.25931227f));
        journeyEVArray.add(new PointF(0.61829466f, 0.28537476f));
        journeyEVArray.add(new PointF(0.58447319f, 0.28537476f));
        journeyEVArray.add(new PointF(0.58447319f, 0.30893725f));
        journeyEVArray.add(new PointF(0.57554466f, 0.33124971f));
        journeyEVArray.add(new PointF(0.57554466f, 0.35231215f));
        journeyEVArray.add(new PointF(0.56829464f, 0.36962467f));
        journeyEVArray.add(new PointF(0.56204462f, 0.38256216f));
        journeyEVArray.add(new PointF(0.54429466f, 0.39487469f));
        journeyEVArray.add(new PointF(0.54429466f, 0.40718722f));
        journeyEVArray.add(new PointF(0.54429466f, 0.4194997f));
        journeyEVArray.add(new PointF(0.54429466f, 0.43181223f));
        journeyEVArray.add(new PointF(0.52654463f, 0.4441247f));
        journeyEVArray.add(new PointF(0.52654463f, 0.45643723f));
        journeyEVArray.add(new PointF(0.52654463f, 0.46874976f));
        journeyEVArray.add(new PointF(0.52654463f, 0.48106223f));
        journeyEVArray.add(new PointF(0.52654463f, 0.49337476f));
        journeyEVArray.add(new PointF(0.52654463f, 0.50662488f));
        journeyEVArray.add(new PointF(0.52654463f, 0.51893735f));
        journeyEVArray.add(new PointF(0.52654463f, 0.53124988f));
        journeyEVArray.add(new PointF(0.52654463f, 0.54356241f));
        journeyEVArray.add(new PointF(0.52654463f, 0.55587494f));
        journeyEVArray.add(new PointF(0.52654463f, 0.56818742f));
        journeyEVArray.add(new PointF(0.52654463f, 0.58049989f));
        journeyEVArray.add(new PointF(0.52654463f, 0.59281242f));
        journeyEVArray.add(new PointF(0.52654463f, 0.60512495f));
        journeyEVArray.add(new PointF(0.52654463f, 0.61743748f));
        journeyEVArray.add(new PointF(0.5292232f, 0.63037491f));
        journeyEVArray.add(new PointF(0.54529464f, 0.64768744f));
        journeyEVArray.add(new PointF(0.54529464f, 0.66874993f));
        journeyEVArray.add(new PointF(0.55065179f, 0.69106245f));
        journeyEVArray.add(new PointF(0.58447319f, 0.69106245f));
        journeyEVArray.add(new PointF(0.58447319f, 0.714625f));
        journeyEVArray.add(new PointF(0.60233033f, 0.74068749f));
        journeyEVArray.add(new PointF(0.61572319f, 0.77112496f));
        journeyEVArray.add(new PointF(0.61572319f, 0.80343747f));
        journeyEVArray.add(new PointF(0.66204464f, 0.80343747f));
        journeyEVArray.add(new PointF(0.68704462f, 0.83825004f));
        journeyEVArray.add(new PointF(0.71204466f, 0.87806249f));
        journeyEVArray.add(new PointF(0.77265179f, 0.87806249f));
        journeyEVArray.add(new PointF(0.74954462f, 0.92412502f));
        journeyEVArray.add(new PointF(0.82086605f, 0.92412502f));
        journeyEVArray.add(new PointF(0.82086605f, 0.97393751f));
        journeyEVArray.add(new PointF(0.89218748f, 0.97393751f));

        j.setJourneyDotsArray(journeyDotsArray);
        j.setJourneyEVArray(journeyEVArray);

        List<Song> examplePlaylist = returnEVArrayFromJourney(journeyEVArray);
        List<Song> filterNilArray = filterNullSongs(examplePlaylist);
        j.setTrackCount(filterNilArray.size());

        JourneyDBHelper journeyDBHelper = new JourneyDBHelper(mContext);
        journeyDBHelper.addJourney(j);
    }

    private void createChillJourney() {
        Journey j = new Journey();
        j.setGeneratedBy("Preset");
        j.setName("Chill");

        List<PointF> journeyEVArray = new ArrayList<>();
        List<Integer> journeyDotsArray = new ArrayList<>();


        journeyDotsArray.add(652);
        journeyDotsArray.add(645);
        journeyDotsArray.add(639);
        journeyDotsArray.add(633);
        journeyDotsArray.add(627);
        journeyDotsArray.add(621);
        journeyDotsArray.add(615);
        journeyDotsArray.add(609);
        journeyDotsArray.add(603);
        journeyDotsArray.add(597);
        journeyDotsArray.add(591);
        journeyDotsArray.add(585);
        journeyDotsArray.add(586);
        journeyDotsArray.add(580);
        journeyDotsArray.add(581);
        journeyDotsArray.add(574);
        journeyDotsArray.add(575);
        journeyDotsArray.add(576);
        journeyDotsArray.add(577);
        journeyDotsArray.add(584);
        journeyDotsArray.add(349);
        journeyDotsArray.add(348);
        journeyDotsArray.add(347);
        journeyDotsArray.add(346);
        journeyDotsArray.add(345);
        journeyDotsArray.add(355);
        journeyDotsArray.add(354);
        journeyDotsArray.add(364);
        journeyDotsArray.add(363);
        journeyDotsArray.add(374);
        journeyDotsArray.add(373);
        journeyDotsArray.add(372);
        journeyDotsArray.add(371);
        journeyDotsArray.add(370);
        journeyDotsArray.add(359);

        journeyEVArray.add(new PointF(0.52654463f, 0.39487469f));
        journeyEVArray.add(new PointF(0.52654463f, 0.38256216f));
        journeyEVArray.add(new PointF(0.5292232f, 0.36962467f));
        journeyEVArray.add(new PointF(0.51504463f, 0.35231215f));
        journeyEVArray.add(new PointF(0.51504463f, 0.33124971f));
        journeyEVArray.add(new PointF(0.51683033f, 0.30893725f));
        journeyEVArray.add(new PointF(0.51683033f, 0.28537476f));
        journeyEVArray.add(new PointF(0.52040178f, 0.25931227f));
        journeyEVArray.add(new PointF(0.52308035f, 0.2288748f));
        journeyEVArray.add(new PointF(0.52308035f, 0.19656235f));
        journeyEVArray.add(new PointF(0.52665174f, 0.1617499f));
        journeyEVArray.add(new PointF(0.53022319f, 0.12193739f));
        journeyEVArray.add(new PointF(0.59083033f, 0.12193739f));
        journeyEVArray.add(new PointF(0.60690176f, 0.075874925f));
        journeyEVArray.add(new PointF(0.67822319f, 0.075874925f));
        journeyEVArray.add(new PointF(0.67822319f, 0.026062489f));
        journeyEVArray.add(new PointF(0.74954462f, 0.026062489f));
        journeyEVArray.add(new PointF(0.82086605f, 0.026062489f));
        journeyEVArray.add(new PointF(0.89218748f, 0.026062489f));
        journeyEVArray.add(new PointF(0.89218748f, 0.075874925f));
        journeyEVArray.add(new PointF(0.9627679f, 0.075693727f));
        journeyEVArray.add(new PointF(0.9627679f, 0.12561876f));
        journeyEVArray.add(new PointF(0.9627679f, 0.17554373f));
        journeyEVArray.add(new PointF(0.9627679f, 0.22546875f));
        journeyEVArray.add(new PointF(0.9627679f, 0.27539372f));
        journeyEVArray.add(new PointF(0.89696437f, 0.26671875f));
        journeyEVArray.add(new PointF(0.89696437f, 0.30914378f));
        journeyEVArray.add(new PointF(0.84366077f, 0.32171875f));
        journeyEVArray.add(new PointF(0.84366077f, 0.35414374f));
        journeyEVArray.add(new PointF(0.79839295f, 0.35976875f));
        journeyEVArray.add(new PointF(0.79839295f, 0.39094377f));
        journeyEVArray.add(new PointF(0.79839295f, 0.42211878f));
        journeyEVArray.add(new PointF(0.79839295f, 0.45329374f));
        journeyEVArray.add(new PointF(0.79839295f, 0.48446876f));
        journeyEVArray.add(new PointF(0.84366077f, 0.48384374f));

        j.setJourneyDotsArray(journeyDotsArray);
        j.setJourneyEVArray(journeyEVArray);

        List<Song> examplePlaylist = returnEVArrayFromJourney(journeyEVArray);
        List<Song> filterNilArray = filterNullSongs(examplePlaylist);
        j.setTrackCount(filterNilArray.size());

        JourneyDBHelper journeyDBHelper = new JourneyDBHelper(mContext);
        journeyDBHelper.addJourney(j);
    }

    private void createPartyJourney() {
        Journey j = new Journey();
        j.setGeneratedBy("Preset");
        j.setName("Party");

        List<PointF> journeyEVArray = new ArrayList<>();
        List<Integer> journeyDotsArray = new ArrayList<>();

        journeyDotsArray.add(220);
        journeyDotsArray.add(221);
        journeyDotsArray.add(222);
        journeyDotsArray.add(223);
        journeyDotsArray.add(224);
        journeyDotsArray.add(225);
        journeyDotsArray.add(226);
        journeyDotsArray.add(227);
        journeyDotsArray.add(228);
        journeyDotsArray.add(122);
        journeyDotsArray.add(115);
        journeyDotsArray.add(114);
        journeyDotsArray.add(113);
        journeyDotsArray.add(112);
        journeyDotsArray.add(111);
        journeyDotsArray.add(110);

        journeyEVArray.add(new PointF(0.9627679f, 0.52490628f));
        journeyEVArray.add(new PointF(0.9627679f, 0.57483125f));
        journeyEVArray.add(new PointF(0.9627679f, 0.62475622f));
        journeyEVArray.add(new PointF(0.9627679f, 0.67468125f));
        journeyEVArray.add(new PointF(0.9627679f, 0.72460628f));
        journeyEVArray.add(new PointF(0.9627679f, 0.77453125f));
        journeyEVArray.add(new PointF(0.9627679f, 0.82445627f));
        journeyEVArray.add(new PointF(0.9627679f, 0.87438124f));
        journeyEVArray.add(new PointF(0.9627679f, 0.92430627f));
        journeyEVArray.add(new PointF(0.89218748f, 0.92412502f));
        journeyEVArray.add(new PointF(0.89218748f, 0.97393751f));
        journeyEVArray.add(new PointF(0.82086605f, 0.97393751f));
        journeyEVArray.add(new PointF(0.74954462f, 0.97393751f));
        journeyEVArray.add(new PointF(0.67822319f, 0.97393751f));
        journeyEVArray.add(new PointF(0.60690176f, 0.97393751f));
        journeyEVArray.add(new PointF(0.53558034f, 0.97393751f));

        j.setJourneyDotsArray(journeyDotsArray);
        j.setJourneyEVArray(journeyEVArray);

        List<Song> examplePlaylist = returnEVArrayFromJourney(journeyEVArray);
        List<Song> filterNilArray = filterNullSongs(examplePlaylist);
        j.setTrackCount(filterNilArray.size());

        JourneyDBHelper journeyDBHelper = new JourneyDBHelper(mContext);
        journeyDBHelper.addJourney(j);
    }

    private void createWorkOutJourney() {
        Journey j = new Journey();
        j.setGeneratedBy("Preset");
        j.setName("Work out");

        List<PointF> journeyEVArray = new ArrayList<>();
        List<Integer> journeyDotsArray = new ArrayList<>();

        journeyDotsArray.add(343);
        journeyDotsArray.add(353);
        journeyDotsArray.add(363);
        journeyDotsArray.add(362);
        journeyDotsArray.add(373);
        journeyDotsArray.add(383);
        journeyDotsArray.add(392);
        journeyDotsArray.add(401);
        journeyDotsArray.add(409);
        journeyDotsArray.add(418);
        journeyDotsArray.add(417);
        journeyDotsArray.add(427);
        journeyDotsArray.add(426);
        journeyDotsArray.add(435);
        journeyDotsArray.add(434);
        journeyDotsArray.add(442);
        journeyDotsArray.add(441);
        journeyDotsArray.add(448);
        journeyDotsArray.add(447);
        journeyDotsArray.add(452);
        journeyDotsArray.add(451);
        journeyDotsArray.add(334);
        journeyDotsArray.add(335);
        journeyDotsArray.add(214);
        journeyDotsArray.add(212);
        journeyDotsArray.add(208);
        journeyDotsArray.add(204);
        journeyDotsArray.add(200);
        journeyDotsArray.add(195);
        journeyDotsArray.add(189);
        journeyDotsArray.add(182);
        journeyDotsArray.add(176);
        journeyDotsArray.add(171);
        journeyDotsArray.add(165);
        journeyDotsArray.add(159);
        journeyDotsArray.add(153);
        journeyDotsArray.add(147);
        journeyDotsArray.add(141);
        journeyDotsArray.add(142);
        journeyDotsArray.add(136);
        journeyDotsArray.add(130);
        journeyDotsArray.add(124);
        journeyDotsArray.add(125);
        journeyDotsArray.add(118);
        journeyDotsArray.add(119);
        journeyDotsArray.add(120);
        journeyDotsArray.add(113);
        journeyDotsArray.add(114);
        journeyDotsArray.add(115);
        journeyDotsArray.add(122);
        journeyDotsArray.add(227);
        journeyDotsArray.add(226);
        journeyDotsArray.add(236);
        journeyDotsArray.add(235);
        journeyDotsArray.add(234);
        journeyDotsArray.add(245);
        journeyDotsArray.add(244);
        journeyDotsArray.add(255);
        journeyDotsArray.add(254);
        journeyDotsArray.add(264);
        journeyDotsArray.add(263);
        journeyDotsArray.add(262);
        journeyDotsArray.add(252);
        journeyDotsArray.add(251);

        journeyEVArray.add(new PointF(0.9627679f, 0.37524378f));
        journeyEVArray.add(new PointF(0.89696437f, 0.35156876f));
        journeyEVArray.add(new PointF(0.84366077f, 0.35414374f));
        journeyEVArray.add(new PointF(0.84366077f, 0.38656878f));
        journeyEVArray.add(new PointF(0.79839295f, 0.39094377f));
        journeyEVArray.add(new PointF(0.75491077f, 0.39531875f));
        journeyEVArray.add(new PointF(0.71678573f, 0.39351875f));
        journeyEVArray.add(new PointF(0.68491077f, 0.40476876f));
        journeyEVArray.add(new PointF(0.65660715f, 0.41601872f));
        journeyEVArray.add(new PointF(0.63276786f, 0.41796875f));
        journeyEVArray.add(new PointF(0.63276786f, 0.43289375f));
        journeyEVArray.add(new PointF(0.61339283f, 0.43171877f));
        journeyEVArray.add(new PointF(0.61339283f, 0.44414377f));
        journeyEVArray.add(new PointF(0.59580356f, 0.44414377f));
        journeyEVArray.add(new PointF(0.59580356f, 0.45656878f));
        journeyEVArray.add(new PointF(0.57821429f, 0.45656878f));
        journeyEVArray.add(new PointF(0.57821429f, 0.46899378f));
        journeyEVArray.add(new PointF(0.56204462f, 0.46874976f));
        journeyEVArray.add(new PointF(0.56204462f, 0.48106223f));
        journeyEVArray.add(new PointF(0.54429466f, 0.48106223f));
        journeyEVArray.add(new PointF(0.54429466f, 0.49337476f));
        journeyEVArray.add(new PointF(0.52654463f, 0.50662488f));
        journeyEVArray.add(new PointF(0.52654463f, 0.51893735f));
        journeyEVArray.add(new PointF(0.52654463f, 0.53124988f));
        journeyEVArray.add(new PointF(0.52654463f, 0.54356241f));
        journeyEVArray.add(new PointF(0.50879461f, 0.55587494f));
        journeyEVArray.add(new PointF(0.50879461f, 0.56818742f));
        journeyEVArray.add(new PointF(0.50879461f, 0.58049989f));
        journeyEVArray.add(new PointF(0.50879461f, 0.59281242f));
        journeyEVArray.add(new PointF(0.50879461f, 0.60512495f));
        journeyEVArray.add(new PointF(0.50879461f, 0.61743748f));
        journeyEVArray.add(new PointF(0.50968748f, 0.63037491f));
        journeyEVArray.add(new PointF(0.51504463f, 0.64768744f));
        journeyEVArray.add(new PointF(0.51504463f, 0.66874993f));
        journeyEVArray.add(new PointF(0.51683033f, 0.69106245f));
        journeyEVArray.add(new PointF(0.51683033f, 0.714625f));
        journeyEVArray.add(new PointF(0.52040178f, 0.74068749f));
        journeyEVArray.add(new PointF(0.52308035f, 0.77112496f));
        journeyEVArray.add(new PointF(0.56940174f, 0.77112496f));
        journeyEVArray.add(new PointF(0.56940174f, 0.80343747f));
        journeyEVArray.add(new PointF(0.58011603f, 0.83825004f));
        journeyEVArray.add(new PointF(0.59083033f, 0.87806249f));
        journeyEVArray.add(new PointF(0.65143746f, 0.87806249f));
        journeyEVArray.add(new PointF(0.60690176f, 0.92412502f));
        journeyEVArray.add(new PointF(0.67822319f, 0.92412502f));
        journeyEVArray.add(new PointF(0.74954462f, 0.92412502f));
        journeyEVArray.add(new PointF(0.74954462f, 0.97393751f));
        journeyEVArray.add(new PointF(0.82086605f, 0.97393751f));
        journeyEVArray.add(new PointF(0.89218748f, 0.97393751f));
        journeyEVArray.add(new PointF(0.89218748f, 0.92412502f));
        journeyEVArray.add(new PointF(0.9627679f, 0.87438124f));
        journeyEVArray.add(new PointF(0.9627679f, 0.82445627f));
        journeyEVArray.add(new PointF(0.89696437f, 0.81813127f));
        journeyEVArray.add(new PointF(0.89696437f, 0.77570623f));
        journeyEVArray.add(new PointF(0.89696437f, 0.73328125f));
        journeyEVArray.add(new PointF(0.84366077f, 0.74313128f));
        journeyEVArray.add(new PointF(0.84366077f, 0.71070623f));
        journeyEVArray.add(new PointF(0.79839295f, 0.70258129f));
        journeyEVArray.add(new PointF(0.79839295f, 0.67140627f));
        journeyEVArray.add(new PointF(0.75491077f, 0.66453123f));
        journeyEVArray.add(new PointF(0.75491077f, 0.63460624f));
        journeyEVArray.add(new PointF(0.75491077f, 0.60468125f));
        journeyEVArray.add(new PointF(0.79839295f, 0.60905623f));
        journeyEVArray.add(new PointF(0.79839295f, 0.57788122f));

        j.setJourneyDotsArray(journeyDotsArray);
        j.setJourneyEVArray(journeyEVArray);

        List<Song> examplePlaylist = returnEVArrayFromJourney(journeyEVArray);
        List<Song> filterNilArray = filterNullSongs(examplePlaylist);
        j.setTrackCount(filterNilArray.size());

        JourneyDBHelper journeyDBHelper = new JourneyDBHelper(mContext);
        journeyDBHelper.addJourney(j);

    }

    private void createCheerUpJourney() {
        Journey j = new Journey();
        j.setGeneratedBy("Preset");
        j.setName("Cheer Up");

        List<PointF> journeyEVArray = new ArrayList<>();
        List<Integer> journeyDotsArray = new ArrayList<>();

        journeyDotsArray.add(811);
        journeyDotsArray.add(810);
        journeyDotsArray.add(809);
        journeyDotsArray.add(808);
        journeyDotsArray.add(807);
        journeyDotsArray.add(806);
        journeyDotsArray.add(805);
        journeyDotsArray.add(814);
        journeyDotsArray.add(823);
        journeyDotsArray.add(822);
        journeyDotsArray.add(833);
        journeyDotsArray.add(843);
        journeyDotsArray.add(851);
        journeyDotsArray.add(850);
        journeyDotsArray.add(859);
        journeyDotsArray.add(867);
        journeyDotsArray.add(875);
        journeyDotsArray.add(763);
        journeyDotsArray.add(772);
        journeyDotsArray.add(780);
        journeyDotsArray.add(788);
        journeyDotsArray.add(793);
        journeyDotsArray.add(797);
        journeyDotsArray.add(103);
        journeyDotsArray.add(213);
        journeyDotsArray.add(214);
        journeyDotsArray.add(333);
        journeyDotsArray.add(328);
        journeyDotsArray.add(321);
        journeyDotsArray.add(314);
        journeyDotsArray.add(305);
        journeyDotsArray.add(295);
        journeyDotsArray.add(287);
        journeyDotsArray.add(278);
        journeyDotsArray.add(279);
        journeyDotsArray.add(269);
        journeyDotsArray.add(261);
        journeyDotsArray.add(251);
        journeyDotsArray.add(252);
        journeyDotsArray.add(241);
        journeyDotsArray.add(231);
        journeyDotsArray.add(232);
        journeyDotsArray.add(223);
        journeyDotsArray.add(224);
        journeyDotsArray.add(225);
        journeyDotsArray.add(226);
        journeyDotsArray.add(227);
        journeyDotsArray.add(228);


        journeyEVArray.add(new PointF(0.037232142f, 0.12561876f));
        journeyEVArray.add(new PointF(0.037232142f, 0.17554373f));
        journeyEVArray.add(new PointF(0.037232142f, 0.22546875f));
        journeyEVArray.add(new PointF(0.037232142f, 0.27539372f));
        journeyEVArray.add(new PointF(0.037232142f, 0.32531875f));
        journeyEVArray.add(new PointF(0.037232142f, 0.37524378f));
        journeyEVArray.add(new PointF(0.10303572f, 0.39399374f));
        journeyEVArray.add(new PointF(0.15633929f, 0.41899377f));
        journeyEVArray.add(new PointF(0.15633929f, 0.45141876f));
        journeyEVArray.add(new PointF(0.20160715f, 0.45329374f));
        journeyEVArray.add(new PointF(0.24508929f, 0.45516878f));
        journeyEVArray.add(new PointF(0.2832143f, 0.46454376f));
        journeyEVArray.add(new PointF(0.2832143f, 0.48821878f));
        journeyEVArray.add(new PointF(0.31508932f, 0.48946875f));
        journeyEVArray.add(new PointF(0.34339288f, 0.49071878f));
        journeyEVArray.add(new PointF(0.36723217f, 0.49259377f));
        journeyEVArray.add(new PointF(0.3866072f, 0.50615621f));
        journeyEVArray.add(new PointF(0.4041965f, 0.50615621f));
        journeyEVArray.add(new PointF(0.4217858f, 0.50615621f));
        journeyEVArray.add(new PointF(0.43795538f, 0.51893735f));
        journeyEVArray.add(new PointF(0.45570537f, 0.51893735f));
        journeyEVArray.add(new PointF(0.47345537f, 0.51893735f));
        journeyEVArray.add(new PointF(0.49120536f, 0.53124988f));
        journeyEVArray.add(new PointF(0.50879461f, 0.53124988f));
        journeyEVArray.add(new PointF(0.52654463f, 0.53124988f));
        journeyEVArray.add(new PointF(0.54429466f, 0.54356241f));
        journeyEVArray.add(new PointF(0.56204462f, 0.54356241f));
        journeyEVArray.add(new PointF(0.57821429f, 0.54343128f));
        journeyEVArray.add(new PointF(0.59580356f, 0.55585623f));
        journeyEVArray.add(new PointF(0.61339283f, 0.55585623f));
        journeyEVArray.add(new PointF(0.63276786f, 0.55218124f));
        journeyEVArray.add(new PointF(0.65660715f, 0.56530625f));
        journeyEVArray.add(new PointF(0.68491077f, 0.55288124f));
        journeyEVArray.add(new PointF(0.68491077f, 0.57405627f));
        journeyEVArray.add(new PointF(0.71678573f, 0.55913126f));
        journeyEVArray.add(new PointF(0.75491077f, 0.57475626f));
        journeyEVArray.add(new PointF(0.79839295f, 0.57788122f));
        journeyEVArray.add(new PointF(0.79839295f, 0.60905623f));
        journeyEVArray.add(new PointF(0.84366077f, 0.61343122f));
        journeyEVArray.add(new PointF(0.89696437f, 0.60600626f));
        journeyEVArray.add(new PointF(0.89696437f, 0.64843124f));
        journeyEVArray.add(new PointF(0.9627679f, 0.67468125f));
        journeyEVArray.add(new PointF(0.9627679f, 0.72460628f));
        journeyEVArray.add(new PointF(0.9627679f, 0.77453125f));
        journeyEVArray.add(new PointF(0.9627679f, 0.82445627f));
        journeyEVArray.add(new PointF(0.9627679f, 0.87438124f));
        journeyEVArray.add(new PointF(0.9627679f, 0.92430627f));

        j.setJourneyDotsArray(journeyDotsArray);
        j.setJourneyEVArray(journeyEVArray);

        List<Song> examplePlaylist = returnEVArrayFromJourney(journeyEVArray);
        List<Song> filterNilArray = filterNullSongs(examplePlaylist);
        j.setTrackCount(filterNilArray.size());

        JourneyDBHelper journeyDBHelper = new JourneyDBHelper(mContext);
        journeyDBHelper.addJourney(j);

    }

    private boolean needsToGeneratePresets() {
        JourneyDBHelper journeyDBHelper = new JourneyDBHelper(mContext);
        int count = journeyDBHelper.getJourneyCount();
        journeyDBHelper.close();
        return (count == 0);
    }

    public List<Song> createPlaylistFromJourney(List<PointF> journeyArray){
        List<Song> playListArray = returnEVArrayFromJourney(journeyArray);

        String key = "limit_journey";
        SharedPreferences sharedpreferences = mContext.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        int limit = sharedpreferences.getInt(key, 0);

        if(limit > 0){
            int songLimit = limit * 15 * 60; //secs
            int totalDuration = getTotalSongsDuration(playListArray);
            if(totalDuration >= songLimit){
                return checkSongDurationLimits(songLimit+10, playListArray);
            }
        }

        return playListArray;
    }

    private List<Song> checkSongDurationLimits(int W, List<Song> playListArray) {
        List<Song> knapsackArray = null;
        knapsackArray = new ArrayList<>(playListArray.size());
        for(Song song:playListArray){
            knapsackArray.add(song);
        }

        knapsackArray = filterNullSongs(knapsackArray);
        List<WeightObject> weightArray = sortSongsWithWeight(knapsackArray);

        knapsackArray = knapsackWithWeightArrayAndLimits(weightArray,W);

        int idx = 0;
        for(Song song : playListArray){
            if(knapsackArray.contains(song)){
                playListArray.set(idx, null);
            }
            idx++;
        }

        return playListArray;
    }

    private List<Song> knapsackWithWeightArrayAndLimits(List<WeightObject> weightArray, int WLimits) {
        List<Song> keepSongs = new ArrayList<>(WLimits);
        int N = weightArray.size() - 1;

        List<List<Integer>> optionA = new ArrayList<>(N+1);
        List<List<Boolean>> solA = new ArrayList<>(N+1);

        for(int i=0; i< (N+1); i++ ){
            List<Integer> optASecond = new ArrayList<Integer>(WLimits+1);
            List<Boolean> solASecond = new ArrayList<Boolean>(WLimits+1);

            for(int j=0; j<WLimits +1; j++){
                optASecond.add(0);
                solASecond.add(false);
            }

            optionA.add(optASecond);
            solA.add(solASecond);
        }

        int totalDuration = 0;
        for(int n = 1; n <= N; n++){
            for(int w =1; w<= WLimits; w++){
                WeightObject wObj = weightArray.get(n-1);

                int opt1 = optionA.get(n-1).get(w);
                int opt2 = Integer.MIN_VALUE;

                int knWeight = wObj.getWeight();

                if(knWeight <= w){
                    opt2 = wObj.getValue() + optionA.get(n-1).get(w-knWeight);
                }

                optionA.get(n).set(w,Math.max(opt1, opt2));
                solA.get(n).set(w, opt2 > opt1);
            }
        }

        for(int n = N, w = WLimits; n > 0; n--){
            if(solA.get(n).get(w)){
                w = w - (weightArray.get(n-1).getWeight());
                WeightObject obj = weightArray.get(n-1);
                totalDuration = totalDuration + obj.getWeight();
                keepSongs.add(obj.getSongObj());
            }
        }

        return keepSongs;
    }

    private List<WeightObject> sortSongsWithWeight(List<Song> songs) {
        List<WeightObject> weightObjects = new ArrayList<>();
        int mood = -1;
        int value = 100;
        int idx = 0;

        for(Song song: songs){
            WeightObject weightObject = new WeightObject();

            if(SongsManager.getIntValue(song.getMood()) != mood){
                mood = SongsManager.getIntValue(song.getMood());
                value = 100;
            }

            weightObject.setSongIndex(idx);
            weightObject.setSongObj(song);
            weightObject.setMood(mood);
            weightObject.setValue(value);
            weightObject.setWeight(song.getPlaybackDuration());

            value = value - 10;
            weightObjects.add(weightObject);
            idx++;
        }

        return weightObjects;
    }

    public int getTotalSongsDuration(List<Song> playListArray) {
        int total = 0;
        for(Song song : playListArray){
            if(song != null) {
                total += song.getPlaybackDuration();
            }
        }
        return total;
    }

    private Song findClosestSongFrom(List<Song> slArray, float E, float V){
        Song closestSongItem = null;
        float kClosesDist = 1;

        for(Song songItem : slArray){
            PointF songEV = new PointF( songItem.getEnergy(), songItem.getValance());
            PointF pointEV = new PointF(E,V);

            float distance = distanceBetweenEVPoints(pointEV, songEV);

            if(kClosesDist > distance){
                kClosesDist = distance;
                closestSongItem =songItem;
            }

        }
        return closestSongItem;
    }

    private float distanceBetweenEVPoints(PointF point1, PointF point2) {
        return (float) Math.sqrt(Math.pow(point2.x - point1.x, 2) + Math.pow(point2.y - point1.y, 2));
    }

    public JourneySession createJourneySessionFromSession(JourneySession js){
        JourneySessionDBHelper journeySessionDBHelper = new JourneySessionDBHelper(mContext);

        JourneySession session = new JourneySession();

        trackDots = new ArrayList<>();

        for(JourneySong obj : js.getSongs()){
            if(obj.isSwapped()){
                JourneySong songObj = new JourneySong();
                songObj.setSong(obj.getSong());
                songObj.setTrackNo(obj.getTrackNo());
                trackDots.add(obj.getTrackNo());

                songObj.setSwapped(false);
                songObj.setSkipped(0);
                session.addSongObj(songObj);
            }
        }

        session.setName(js.getName());
        session.setFavourite(0);
        session.setSessionSyncStatus(0);
        session.setJourneyID(UUID.randomUUID().toString());
        session.setStarted(new Date());

        journeySessionDBHelper.addJourneySession(session);
        journeySessionDBHelper.close();
        return session;
    }

    public JourneySession createJourneySessionFromJourney(Journey j, List<Song> songsArray, SongMoodCategory current, SongMoodCategory target){
        List<Song>  songs = null;
        JourneySession session = null;
        JourneySessionDBHelper journeySessionDBHelper = new JourneySessionDBHelper(mContext);

        if(songsArray == null){
            songs = createPlaylistFromJourney(j.getJourneyEVArray());
            session = createJourneySessionFromSongs(songs);
        }
        else{
            session = createJourneySessionFromSongs(songsArray);
        }

        session.setName((j.getName() == null || j.getName().length() > 0 )? j.getName() : "NEW PLAYLIST");
        session.setJourney(j);
        session.setFavourite(0);
        session.setSessionSyncStatus(0);
        session.setStarted(new Date());
        session.setJourneyID(UUID.randomUUID().toString());
        session.setCurrentMood(current);
        session.setTargetMood(target);

        journeySessionDBHelper.addJourneySession(session);
        journeySessionDBHelper.close();

        return session;
    }

    public JourneySession createJourneySessionFromLibrary(List<Song> libArray){
        JourneySession session = createJourneySessionFromSongs(libArray);
        session.setName("library");
        return session;
    }

    private JourneySession createJourneySessionFromSongs(List<Song> songs) {
        JourneySession session = new JourneySession();
        trackDots = new ArrayList<>();

        int idx = 0;
        for(Song song : songs){
            if(song != null){
                JourneySong songObj = new JourneySong();
                songObj.setSong(song);
                songObj.setTrackNo(idx);
                trackDots.add(idx);
                songObj.setSwapped(false);
                songObj.setSkipped(0);
                session.addSongObj(songObj);
            }
            idx++;
        }

        return session;
    }

    public JourneySong swapSong(JourneySong jSongObj, SongMoodCategory songMood){
        List<Song> jSongArray = Global.currentSongList;

        List<Song> songArray = new ArrayList<>();
        for(Song song : jSongArray){
            songArray.add(song);
        }

        PointF songObjEV = new PointF(jSongObj.getSong().getValance(), jSongObj.getSong().getEnergy());
        Song newSong = findSongBasedOnEV(songObjEV, songArray);

        if(newSong != null){
            jSongObj.setSwapped(true);
            JourneySong newJSong = new JourneySong();

            newJSong.setSong(newSong);
            newJSong.setTrackNo(jSongObj.getTrackNo());
            newJSong.setSwapped(false);
            newJSong.setSkipped(0);

            return newJSong;
        }

        return null;
    }

    public List<JourneySong> filterSwapSongFromJourneySession(JourneySession jSession){
        List<JourneySong> jSessionArray = jSession.getSongs();
        List<JourneySong> filteredArray = new ArrayList<>();

        for(JourneySong song : jSessionArray){
            if(!song.isSwapped()){
                filteredArray.add(song);
            }
        }
        return filteredArray;
    }

    public List<JourneySong> filterNullJourneySongFromArray(List<JourneySong> songWithNullArray){
        List<JourneySong> filteredNullArray = new ArrayList<>();

        for(JourneySong song : songWithNullArray){
            if(song.getSong() != null){
                filteredNullArray.add(song);
            }
        }
        return filteredNullArray;
    }

    public JourneySession retagJourneySongwithNewMood(JourneySong jSongObj, SongMoodCategory newMood){
        JourneySession jSession = this.currentSession;

        if(Global.isJourney){
            PointF newMoodEV = SongsService.getInstance(mContext).changejSongMoodToNewMood(jSongObj, newMood);

            Song song = jSongObj.getSong();
            song.setMood(newMood);
            song.setEnergy(newMoodEV.y);
            song.setValance(newMoodEV.x);
            song.setUserChangedMood(true);
            song.setUserChangedEnergy(newMoodEV.y);
            song.setUserChangedValance(newMoodEV.x);
            song.setUserRetaggedMood(SongsManager.textForMood(SongsManager.getIntValue(newMood)));

            jSongObj.setSong(song);
            jSession.removeObject(jSongObj);
        }

        this.currentSession = jSession;
        return jSession;
    }

    public void updateCurrent(SongMoodCategory mood){
        this.currentSession.setCurrentMood(mood);
    }

    public void updateTarget(SongMoodCategory mood){
        this.currentSession.setFinishMood(mood);
    }

    public void clearUpJourneySession(){
        JourneySessionDBHelper journeySessionDBHelper = new JourneySessionDBHelper(mContext);
        List<JourneySession> theTopEightJourneySessionArray = journeySessionDBHelper.getTopEightSessions();
        if(theTopEightJourneySessionArray.size() <= 8){
            journeySessionDBHelper.close();
            return;
        }

        Date lastDate = theTopEightJourneySessionArray.get(7).getStarted();
        journeySessionDBHelper.clearEntriesOlderThan(lastDate);
        journeySessionDBHelper.close();
    }

    public JourneySession getCurrentSession(){
        return currentSession;
    }

    public void setCurrentSession(JourneySession currentSession) {
        this.currentSession = currentSession;
    }
}
