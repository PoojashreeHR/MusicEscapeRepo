package com.agiliztech.musicescape.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.agiliztech.musicescape.R;

import com.agiliztech.musicescape.database.DBHandler;
import com.agiliztech.musicescape.fasrscrollinginterface.FastScrollRecyclerViewInterface;
import com.agiliztech.musicescape.fasrscrollinginterface.FastScrollRecyclerViewItemDecoration;
import com.agiliztech.musicescape.journey.SongMoodCategory;
import com.agiliztech.musicescape.models.Song;
import com.agiliztech.musicescape.models.SongsModel;
import com.agiliztech.musicescape.utils.Global;

import com.agiliztech.musicescape.utils.SongsManager;
import com.chauthai.swipereveallayout.SwipeRevealLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

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



    public class LibraryRecyclerView extends RecyclerView.Adapter<LibraryRecyclerView.MyViewHolder> implements FastScrollRecyclerViewInterface {
        List<Song> songList;
        private HashMap<String, Integer> mMapIndex;
        String mood;
        Context context;
       //List<SwipedState> list ;

        @Override
        public HashMap<String, Integer> getMapIndex() {
            return this.mMapIndex;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public LinearLayout songlistLayout;
            public TextView mTextView;
            public TextView title, artist;
            public ImageView rv_swap_library;
            public SwipeRevealLayout swipe_layout_library;
            private ImageView mood_image;
            private ViewPager viewPager;
            public MyViewHolder(View view) {
                super(view);
                //mTextView = v;
                title = (TextView) view.findViewById(R.id.song_title);
                artist = (TextView) view.findViewById(R.id.song_artist);
                songlistLayout = (LinearLayout) view.findViewById(R.id.songListLayout);
                //rv_swap_library = (ImageView) view.findViewById(R.id.rv_swap_library);
                //swipe_layout_library = (SwipeRevealLayout) view.findViewById(R.id.swipe_layout_library);
                mood_image = (ImageView) view.findViewById(R.id.mood_image);
               // viewPager = (ViewPager) view.findViewById(R.id.viewPager);

            }
        }

        public LibraryRecyclerView(List<Song> songList, HashMap<String, Integer> mapIndex,
                                   String mood,  Context context) {
            this.songList = songList;
            mMapIndex = mapIndex;
            this.mood = mood;
            this.context = context;
//            list = new ArrayList<>();
//            for (int i = 0; i < songList.size(); i++) {
//                list.add(i, SwipedState.SHOWING_PRIMARY_CONTENT);
//            }


        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

           /* ViewPager v = (ViewPager) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.song_list_row, parent, false);
            RecyclerViewPagerAdapter adapter = new RecyclerViewPagerAdapter();

            ((ViewPager) v.findViewById(R.id.viewPager)).setAdapter(adapter);

            //Perhaps the first most crucial part. The ViewPager loses its width information when it is put
            //inside a RecyclerView. It needs to be explicitly resized, in this case to the width of the
            //screen. The height must be provided as a fixed value.
            DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
            v.getLayoutParams().width = displayMetrics.widthPixels;
            v.requestLayout();

            MyViewHolder vh = new MyViewHolder(v);
            return vh;*/


            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_list_row, parent, false);
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.side_index_item, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            final int pos = position;

            Song model = songList.get(position);
            //holder.mTextView.setText(model.getArtist());
            holder.title.setText(model.getSongName());
            if (mood.contains("depressed")) {
                holder.title.setTextColor(SongsManager.colorForMood(SongMoodCategory.scDepressed));
                holder.mood_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_depressed_library_inactive));
            } else if (mood.contains("happy")) {
                holder.title.setTextColor(SongsManager.colorForMood(SongMoodCategory.scHappy));
                holder.mood_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_happy_library_inactive));
            } else if (mood.contains("chilled")) {
                holder.title.setTextColor(SongsManager.colorForMood(SongMoodCategory.scChilled));
                holder.mood_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_chilled_library_inactive));
            } else if (mood.contains("peaceful")) {
                holder.title.setTextColor(SongsManager.colorForMood(SongMoodCategory.scPeaceful));
            } else if (mood.contains("bored")) {
                holder.title.setTextColor(SongsManager.colorForMood(SongMoodCategory.scBored));
                holder.mood_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bored_library_inactive));
            } else if (mood.contains("stressed")) {
                holder.title.setTextColor(SongsManager.colorForMood(SongMoodCategory.scStressed));
                holder.mood_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_stressed_library_inactive));
            } else if (mood.contains("aggressive")) {
                holder.title.setTextColor(SongsManager.colorForMood(SongMoodCategory.scAggressive));
                holder.mood_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_aggressive_library_inactive));
            } else if (mood.contains("excited")) {
                holder.title.setTextColor(SongsManager.colorForMood(SongMoodCategory.scExcited));
                holder.mood_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_excited_library_inactive));
            } else if (mood.contains("nomood")) {
                holder.title.setTextColor(SongsManager.colorForMood(SongMoodCategory.scMoodNotFound));
            } else if (mood.contains("allmood") || TextUtils.isEmpty(mood)) {
                SongMoodCategory mood = model.getMood();
                Log.e("MOOD RECYCLER VIEW", " MOOD : " + mood);
                if (mood == null) {
                    mood = SongMoodCategory.scAllSongs;
                    holder.mood_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_notfound_library_inactive));
                } else if (mood == SongMoodCategory.scDepressed) {
                    holder.mood_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_depressed_library_inactive));
                } else if (mood == SongMoodCategory.scExcited) {
                    holder.mood_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_excited_library_inactive));
                } else if (mood == SongMoodCategory.scHappy) {
                    holder.mood_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_happy_library_inactive));
                } else if (mood == SongMoodCategory.scChilled) {
                    holder.mood_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_chilled_library_inactive));
                } else if (mood == SongMoodCategory.scPeaceful) {
                    holder.mood_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_peaceful_library_inactive));
                } else if (mood == SongMoodCategory.scBored) {
                    holder.mood_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bored_library_inactive));
                } else if (mood == SongMoodCategory.scStressed) {
                    holder.mood_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_stressed_library_inactive));
                } else if (mood == SongMoodCategory.scAggressive) {
                    holder.mood_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_aggressive_library_inactive));
                }
                holder.title.setTextColor(SongsManager.colorForMood(mood));
            }
            holder.artist.setText(model.getArtist().getArtistName());
            holder.songlistLayout.setTag(pos);
           /* holder.viewPager.setCurrentItem(list.get(position).ordinal());
            holder.viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                int previousPagePosition = 0;

                @Override
                public void onPageScrolled(int pagePosition, float positionOffset, int positionOffsetPixels) {
                    if (pagePosition == previousPagePosition)
                        return;

                    switch (pagePosition) {
                        case 0:
                            list.set(pos, SwipedState.SHOWING_PRIMARY_CONTENT);
                            break;
                        case 1:
                            list.set(pos, SwipedState.SHOWING_SECONDARY_CONTENT);
                            break;

                    }
                    previousPagePosition = pagePosition;

                    //Log.i("MyAdapter", "PagePosition " + position + " set to " + mItemSwipedStates.get(position).ordinal());
                }

                @Override
                public void onPageSelected(int pagePosition) {
                    //This method keep incorrectly firing as the RecyclerView scrolls.
                    //Use the one above instead
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
*/
         /*   holder.rv_swap_library.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    songRetagInLibrary(position);
                }
            });
*/
            //((ViewPager) holder.viewPager).setCurrentItem(list.get(position));
            holder.songlistLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createTempPlaylsitFromSong(pos);
                }
            });


        }

        @Override
        public int getItemCount() {
            return songList.size();
        }

    }

    private void createTempPlaylsitFromSong(int pos) {
        if(musicSrv != null){
            if (musicSrv.isPng()) {
                musicSrv.pausePlayer();
            }
            musicSrv.setList(dbSongList);
            Global.isJourney = false;
            playSelectedSong(pos);
            Global.isLibPlaylist = true;
            Global.libPlaylistSongs = dbSongList;
           setUpPlaylist();
        }
    }
    private enum SwipedState {
        SHOWING_PRIMARY_CONTENT,
        SHOWING_SECONDARY_CONTENT
    }

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
//        if(dbSongList.size() <= 0){
//            hideMusicPlayer();
//        }
//        else{
//            hideMusicPlayer();
//        }
        dashboardButton = (ImageView) findViewById(R.id.imageButton2);
        dashboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LibraryActivity.this, NewDashboardActivity.class);
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

        SongAdapter(dbSongList, "");

        final ImageButton songScan = (ImageButton) findViewById(R.id.library1);
        songScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //songScan.setBackgroundResource(R.drawable.menu_buttons_song_scan_active);
                songScan.setFocusableInTouchMode(false);
                songScan.setFocusable(false);
                Intent intent = new Intent(LibraryActivity.this, MoodMappingActivity.class);
                startActivity(intent);
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
        dbSongList = listOfSongs;
        HashMap<String, Integer> mapIndex = calculateIndexesForName(listOfSongs);
        libAdapter = new LibraryRecyclerView(listOfSongs, mapIndex, mood,LibraryActivity.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        FastScrollRecyclerViewItemDecoration decoration = new FastScrollRecyclerViewItemDecoration(this);
        recyclerView.addItemDecoration(decoration);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(libAdapter);
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof LibraryRecyclerView.MyViewHolder) return 1f;
                return super.getSwipeThreshold(viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                Toast.makeText(LibraryActivity.this, "Swiped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(ItemTouchHelper.ACTION_STATE_SWIPE,swipeFlags);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    // Get RecyclerView item from the ViewHolder
                    View itemView = viewHolder.itemView;

                    Paint p = new Paint();
                    if (dX > 0) {
            /* Set your color for positive displacement */

                        // Draw Rect with varying right side, equal to displacement dX
                        c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX,
                                (float) itemView.getBottom(), p);
                    } else {
            /* Set your color for negative displacement */

                        // Draw Rect with varying left side, equal to the item's right side plus negative displacement dX
                        c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                                (float) itemView.getRight(), (float) itemView.getBottom(), p);
                    }

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
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


    public void songRetagInLibrary(int position) {
        // Logic for Retag from Library
    }
}
