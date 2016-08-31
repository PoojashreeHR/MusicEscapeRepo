package com.agiliztech.musicescape.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.agiliztech.musicescape.R;
import com.agiliztech.musicescape.adapter.LibraryRecyclerView;
import com.agiliztech.musicescape.database.DBHandler;
import com.agiliztech.musicescape.fasrscrollinginterface.FastScrollRecyclerViewItemDecoration;
import com.agiliztech.musicescape.models.Song;
import com.agiliztech.musicescape.models.SongsModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class LibraryActivity extends BaseMusicActivity implements View.OnClickListener {
    Spinner sp;
    BaseMusicActivity baseMusicActivity;
    private View mViewGroup;
    private View songViewGroup;
    private ImageButton mButton, songButton;
    TextView moodList, songs;
    Typeface tf;
    LinearLayout linearLayout;
    RecyclerView recyclerView;
    LibraryRecyclerView libAdapter;
    private ImageView dashboardButton;
    DBHandler dbHandler;
    ArrayList<Song> dbSongList;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        recyclerView = (RecyclerView) findViewById(R.id.library_recycler_view);
        baseMusicActivity = new BaseMusicActivity();

        linearLayout = (LinearLayout) findViewById(R.id.viewContainer);
        tf = Typeface.createFromAsset(getAssets(), "fonts/MontserratRegular.ttf");
        TextView library = (TextView) findViewById(R.id.library);
        moodList = (TextView) findViewById(R.id.textView9);
        songs = (TextView) findViewById(R.id.songs);
        library.setTypeface(tf);
        moodList.setTypeface(tf);
        songs.setTypeface(tf);

        dbHandler = new DBHandler(this);
        dbSongList = dbHandler.getAllSongsFromDB();
        dashboardButton = (ImageView) findViewById(R.id.imageButton2);
        dashboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LibraryActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });

        songViewGroup = findViewById(R.id.songSort);
        songViewGroup.setOnClickListener(this);
        mViewGroup = findViewById(R.id.viewContainer);
        mButton = (ImageButton) findViewById(R.id.arrow);
        songButton = (ImageButton) findViewById(R.id.arrow1);
        mViewGroup.setOnClickListener(this);

        SongAdapter(dbSongList, "allmood");

        final ImageButton songScan = (ImageButton) findViewById(R.id.library1);
        songScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //songScan.setBackgroundResource(R.drawable.menu_buttons_song_scan_active);
                Toast.makeText(getApplicationContext(), "Button clicked!",
                        Toast.LENGTH_SHORT).show();
                songScan.setFocusableInTouchMode(false);
                songScan.setFocusable(false);
                finish();
            }
        });

        moodList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mViewGroup.getVisibility() == View.VISIBLE) {
                    mButton.animate().rotation(360).start();
                    mViewGroup.setVisibility(View.GONE);

                } else {
                    mViewGroup.setVisibility(View.VISIBLE);
                    mButton.animate().rotation(180).start();
                }
            }
        });
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mViewGroup.getVisibility() == View.VISIBLE) {
                    mButton.animate().rotation(360).start();
                    mViewGroup.setVisibility(View.GONE);

                } else {
                    mViewGroup.setVisibility(View.VISIBLE);
                    mButton.animate().rotation(180).start();
                }
            }
        });

        songs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (songViewGroup.getVisibility() == View.VISIBLE) {
                    songButton.animate().rotation(360).start();
                    songViewGroup.setVisibility(View.GONE);

                } else {
                    songViewGroup.setVisibility(View.VISIBLE);
                    songButton.animate().rotation(180).start();
                }
            }
        });
        songButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (songViewGroup.getVisibility() == View.VISIBLE) {
                    songButton.animate().rotation(360).start();
                    songViewGroup.setVisibility(View.GONE);

                } else {
                    songViewGroup.setVisibility(View.VISIBLE);
                    songButton.animate().rotation(180).start();
                }
            }
        });
    }

    public void SongAdapter(ArrayList<Song> listOfSongs, String mood) {

      /*  for(int i=0; i<21; i++) {
            songList.add(Character.toString((char)(66 + i).
        }*/

        // if (listOfSongs.size() > 0) {
        HashMap<String, Integer> mapIndex = calculateIndexesForName(listOfSongs);
        libAdapter = new LibraryRecyclerView(listOfSongs, mapIndex, mood);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        FastScrollRecyclerViewItemDecoration decoration = new FastScrollRecyclerViewItemDecoration(this);
        recyclerView.addItemDecoration(decoration);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(libAdapter);
        // }


    }

    public void artistWise() {
        sortSongsArtistwiseLocal();
        SongAdapter(dbSongList, "");

    }

    public void sortSongsArtistwiseLocal() {
        Collections.sort(dbSongList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getArtist().getArtistName().compareToIgnoreCase(b.getArtist().getArtistName());
            }
        });
    }


    public void songWiseDisplay() {
        sortSongsAlphabeticallyLocal();
        SongAdapter(dbSongList, "");
    }

    public void sortSongsAlphabeticallyLocal() {
        Collections.sort(dbSongList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getSongName().compareToIgnoreCase(b.getSongName());
            }
        });
    }

    public void albumwise() {
        sortSongsAlbumwiseLocal();
        SongAdapter(dbSongList, "");
    }

    public void sortSongsAlbumwiseLocal() {
        Collections.sort(dbSongList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getAlbum().getAlbumTitle().compareToIgnoreCase(b.getAlbum().getAlbumTitle());
            }
        });
    }

    private HashMap<String, Integer> calculateIndexesForName(ArrayList<Song> songList) {
        HashMap<String, Integer> mapIndex = new LinkedHashMap<String, Integer>();
        for (int i = 0; i < songList.size(); i++) {
            String name = String.valueOf(songList.get(i).getSongName());
            String index = name.substring(0, 1);
            index = index.toUpperCase();

            if (!mapIndex.containsKey(index)) {
                mapIndex.put(index, i);
            }
        }
        return mapIndex;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.aggressive:
                //your code here
                TextView aggressive = (TextView) findViewById(R.id.aggressive);
                moodList.setText(aggressive.getText().toString());
                moodList.setTypeface(tf);
                moodList.setTextColor(aggressive.getTextColors());
                mButton.animate().rotation(360).start();
                mViewGroup.setVisibility(View.GONE);
                //Toast.makeText(getApplicationContext(), "Aggressive is clicked", Toast.LENGTH_LONG).show();
                ArrayList<Song> modelAggressive = dbHandler.getSongsListBasedOnMoods("aggressive");
                SongAdapter(modelAggressive, "aggressive");
                break;
            case R.id.bored:
                //your code here
                TextView bore = (TextView) findViewById(R.id.bored);
                moodList.setText(bore.getText().toString());
                moodList.setTypeface(tf);
                moodList.setTextColor(bore.getTextColors());
                mButton.animate().rotation(360).start();
                mViewGroup.setVisibility(View.GONE);
                //Toast.makeText(getApplicationContext(), "Bored is clicked", Toast.LENGTH_LONG).show();
                ArrayList<Song> modelBored = dbHandler.getSongsListBasedOnMoods("bored");
                SongAdapter(modelBored, "bored");
                break;
            case R.id.chilled:
                //your code here
                TextView chill = (TextView) findViewById(R.id.chilled);
                moodList.setText(chill.getText().toString());
                moodList.setTextColor(chill.getTextColors());
                moodList.setTypeface(tf);
                mButton.animate().rotation(360).start();
                mViewGroup.setVisibility(View.GONE);
                //Toast.makeText(getApplicationContext(), "Chilled is clicked", Toast.LENGTH_LONG).show();
                ArrayList<Song> modelChilled = dbHandler.getSongsListBasedOnMoods("chilled");
                SongAdapter(modelChilled, "chilled");
                break;
            case R.id.depressed:
                //your code here
                TextView depress = (TextView) findViewById(R.id.depressed);
                moodList.setText(depress.getText().toString());
                moodList.setTextColor(depress.getTextColors());
                moodList.setTypeface(tf);
                mButton.animate().rotation(360).start();
                mViewGroup.setVisibility(View.GONE);
                //Toast.makeText(getApplicationContext(), "Chilled is clicked", Toast.LENGTH_LONG).show();
                ArrayList<Song> modelDepressed = dbHandler.getSongsListBasedOnMoods("depressed");
                SongAdapter(modelDepressed, "depressed");
                break;
            case R.id.exited:
                //your code here
                TextView exited = (TextView) findViewById(R.id.exited);
                moodList.setText(exited.getText().toString());
                moodList.setTextColor(exited.getTextColors());
                moodList.setTypeface(tf);
                mButton.animate().rotation(360).start();
                mViewGroup.setVisibility(View.GONE);
                //Toast.makeText(getApplicationContext(), "Chilled is clicked", Toast.LENGTH_LONG).show();
                ArrayList<Song> modelExcited = dbHandler.getSongsListBasedOnMoods("excited");
                SongAdapter(modelExcited, "excited");
                break;
            case R.id.happy:
                //your code here
                TextView happy = (TextView) findViewById(R.id.happy);
                moodList.setText(happy.getText().toString());
                moodList.setTextColor(happy.getTextColors());
                moodList.setTypeface(tf);
                mButton.animate().rotation(360).start();
                mViewGroup.setVisibility(View.GONE);
                //Toast.makeText(getApplicationContext(), "Chilled is clicked", Toast.LENGTH_LONG).show();
                ArrayList<Song> modelHappy = dbHandler.getSongsListBasedOnMoods("happy");
                SongAdapter(modelHappy, "happy");
                break;
            case R.id.peacefull:
                //your code here
                TextView peace = (TextView) findViewById(R.id.peacefull);
                moodList.setText(peace.getText().toString());
                moodList.setTextColor(peace.getTextColors());
                moodList.setTypeface(tf);
                mButton.animate().rotation(360).start();
                mViewGroup.setVisibility(View.GONE);
                //Toast.makeText(getApplicationContext(), "Chilled is clicked", Toast.LENGTH_LONG).show();
                ArrayList<Song> modelPeaceful = dbHandler.getSongsListBasedOnMoods("peaceful");
                SongAdapter(modelPeaceful, "peaceful");
                break;
            case R.id.stressed:
                //your code here
                TextView stress = (TextView) findViewById(R.id.stressed);
                moodList.setText(stress.getText().toString());
                moodList.setTextColor(stress.getTextColors());
                moodList.setTypeface(tf);
                mButton.animate().rotation(360).start();
                mViewGroup.setVisibility(View.GONE);
                ArrayList<Song> modelStressed = dbHandler.getSongsListBasedOnMoods("stressed");
                SongAdapter(modelStressed, "stressed");
                //Toast.makeText(getApplicationContext(), "Chilled is clicked", Toast.LENGTH_LONG).show();
                break;
            case R.id.noMood:
                //your code here
                TextView noMood = (TextView) findViewById(R.id.noMood);
                moodList.setText(noMood.getText().toString());
                moodList.setTextColor(noMood.getTextColors());
                moodList.setTypeface(tf);
                mButton.animate().rotation(360).start();
                mViewGroup.setVisibility(View.GONE);
                ArrayList<Song> modelNoMood = dbHandler.getSongsListBasedOnMoods("");
                SongAdapter(modelNoMood, "nomood");
                //Toast.makeText(getApplicationContext(), "Chilled is clicked", Toast.LENGTH_LONG).show();
                break;
            case R.id.allMood:
                //your code here
                TextView allMood = (TextView) findViewById(R.id.allMood);
                moodList.setText(allMood.getText().toString());
                moodList.setTextColor(allMood.getTextColors());
                moodList.setTypeface(tf);

                mButton.animate().rotation(360).start();
                mViewGroup.setVisibility(View.GONE);
                //Toast.makeText(getApplicationContext(), "Chilled is clicked", Toast.LENGTH_LONG).show();
                ArrayList<Song> modelAllMoods = dbHandler.getAllSongsFromDB();
                SongAdapter(modelAllMoods, "allmood");
                break;
            case R.id.sortSong:
                //your code here
                TextView songSort = (TextView) findViewById(R.id.sortSong);
                songs.setText(songSort.getText().toString());
                songs.setTextColor(songSort.getTextColors());
                songs.setTypeface(tf);
                songWiseDisplay();
                songButton.animate().rotation(360).start();
                songViewGroup.setVisibility(View.GONE);
                //Toast.makeText(getApplicationContext(), "Chilled is clicked", Toast.LENGTH_LONG).show();
                break;
            case R.id.sortArtist:
                //your code here
                TextView sortArtist = (TextView) findViewById(R.id.sortArtist);
                songs.setText(sortArtist.getText().toString());
                songs.setTextColor(sortArtist.getTextColors());
                songs.setTypeface(tf);
                //sortSongsArtistwise();
                artistWise();
                songButton.animate().rotation(360).start();
                songViewGroup.setVisibility(View.GONE);
                //Toast.makeText(getApplicationContext(), "Chilled is clicked", Toast.LENGTH_LONG).show();
                break;

            case R.id.sortAlbum:
                //your code here
                TextView sortAlbun = (TextView) findViewById(R.id.sortAlbum);
                songs.setText(sortAlbun.getText().toString());
                songs.setTextColor(sortAlbun.getTextColors());
                songs.setTypeface(tf);
                //sortSongsArtistwise();
                albumwise();
                songButton.animate().rotation(360).start();
                songViewGroup.setVisibility(View.GONE);
                //Toast.makeText(getApplicationContext(), "Chilled is clicked", Toast.LENGTH_LONG).show();
                break;
        }

    }
    public void onResume()
    {    super.onResume();
        //   settings.edit().putBoolean("is_first_time", false).commit();
        getSharedPreferences("MyPreference", MODE_PRIVATE).edit()
                .putBoolean("first_time_library", false).commit();
    }

}
