package com.agiliztech.musicescape.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.agiliztech.musicescape.R;
import com.agiliztech.musicescape.journey.JourneyService;
import com.agiliztech.musicescape.journey.JourneyView;
import com.agiliztech.musicescape.journey.Size;
import com.agiliztech.musicescape.journey.SongMoodCategory;
import com.agiliztech.musicescape.models.DashboardItem;
import com.agiliztech.musicescape.models.Journey;
import com.agiliztech.musicescape.models.JourneySession;
import com.agiliztech.musicescape.utils.Global;
import com.agiliztech.musicescape.utils.SongsManager;
import com.agiliztech.musicescape.utils.UtilityClass;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NewDashboardActivity extends BaseMusicActivity {


    SharedPreferences dashboardPreference;
    ImageView menu_activeSettings,menu_activelibrary,menu_library, menu_settings,menu_activedraw,menu_history,menu_activehistory;
    private ImageView menu_draw;
    TabItem btn_default,btn_user;
    String dashboard;
    private RecyclerView recyclerView,recyclerView_user;
    private Timer highlightTimer;
    private TabLayout dash_items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_dashboard);

        ImageView infoButton;
        dashboardPreference = getSharedPreferences("DashboardPreference", 0);

        dash_items = (TabLayout) findViewById(R.id.dash_items);

        RelativeLayout.LayoutParams relparams = new RelativeLayout.LayoutParams(dpToPx(162), RelativeLayout.LayoutParams.WRAP_CONTENT);
        dash_items.setLayoutParams(relparams);


        infoButton = (ImageView) findViewById(R.id.dash_imageView);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AppInfoActivity.class);
                intent.putExtra("dashboard","DashBoard");
                startActivity(intent);
                // checkInternetConnection();
            }
        });

        dash_items.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0){
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView_user.setVisibility(View.GONE);
                }
                else{
                    recyclerView_user.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });




        highlightTimer = new Timer();
        menu_activedraw = (ImageView) findViewById(R.id.menu_activedraw);
        menu_draw = (ImageView) findViewById(R.id.menu_draw);
        menu_draw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu_activedraw.setVisibility(View.VISIBLE);
                if (dashboardPreference.getBoolean("draw", true))
                {
                    Intent intent = new Intent(getApplicationContext(), SlidingImage.class);
                    intent.putExtra("draw","Draw");
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(NewDashboardActivity.this, SelectingMoodActivity.class);
                    startActivity(intent);
                   // finish();
                }

            }
        });

        menu_activehistory = (ImageView) findViewById(R.id.menu_activehistory);
        menu_history = (ImageView) findViewById(R.id.menu_history);
        menu_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu_activehistory.setVisibility(View.VISIBLE);
                if (dashboardPreference.getBoolean("history", true))
                {
                    Intent intent = new Intent(getApplicationContext(), SlidingImage.class);
                    intent.putExtra("history","History");
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(NewDashboardActivity.this,HistoryActivity.class);
                    startActivity(intent);
                    //finish();
                }

            }
        });

        menu_activeSettings = (ImageView) findViewById(R.id.menu_activeSettings);
        menu_settings = (ImageView) findViewById(R.id.menu_settings);
        menu_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu_activeSettings.setVisibility(View.VISIBLE);
                Intent intent = new Intent(NewDashboardActivity.this,SettingsActivity.class);
                startActivity(intent);

                //finish();
            }
        });

        menu_activelibrary = (ImageView) findViewById(R.id.menu_activelibrary);
        menu_library = (ImageView) findViewById(R.id.menu_library);
        menu_library.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu_activelibrary.setVisibility(View.VISIBLE);
                Intent intent = new Intent(NewDashboardActivity.this,LibraryActivity.class);
                startActivity(intent);

                //finish();
            }
        });

         recyclerView = (RecyclerView) findViewById(R.id.history_rv);
        recyclerView_user = (RecyclerView) findViewById(R.id.user_rv);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView_user.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    protected void onResume() {
        super.onResume();
        recyclerView.setAdapter(new DashboardAdapter(getJourneyData()));
        recyclerView_user.setAdapter(new DashboardAdapter(getUserJourneyData()));
        resetSelected();
    }

    private void resetSelected() {
        menu_activedraw.setVisibility(View.GONE);
        menu_activehistory.setVisibility(View.GONE);
        menu_activelibrary.setVisibility(View.GONE);
        menu_activeSettings.setVisibility(View.GONE);
        menu_draw.setVisibility(View.VISIBLE);
        menu_history.setVisibility(View.VISIBLE);
        menu_library.setVisibility(View.VISIBLE);
        menu_settings.setVisibility(View.VISIBLE);
    }

    private List<DashboardItem> getJourneyData() {
        JourneyService journeyService = JourneyService.getInstance(this);
        return journeyService.getAllPresets();
    }

    private List<DashboardItem> getUserJourneyData() {
        JourneyService journeyService = JourneyService.getInstance(this);
        return journeyService.getAllFavouritesJourneys();
    }

    private Size getGapsSize() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return new Size(0.92500000000000004f*displayMetrics.widthPixels/560f, 0.96999999999999997f*displayMetrics.heightPixels/560f);

    }

    private class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.HistoryViewHolder>

    {
        private List<DashboardItem> sessions;

        public DashboardAdapter(List<DashboardItem> journeySessionList) {
            this.sessions = journeySessionList;
        }


        @Override
        public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_item, parent, false);
            return new HistoryViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(HistoryViewHolder holder, int position) {
            final int pos = position;
            final DashboardItem item = sessions.get(pos);

            holder.ll_saveHistory.setLayoutParams(getLayoutParams());
            holder.overlay.setLayoutParams(getLayoutParams());
            holder.overlay.setPadding(dpToPx(18),0, dpToPx(18),0);
            holder.journeyView.setLayoutParams(getFrameLayoutParams());

            holder.journeyView.setGaps(getGapsSize());
            holder.journeyView.setMode(JourneyView.DrawingMode.DMMENU);
            if(item.isPreset()){
                holder.journeyView.setJourneyPoints(item.getJourney().getJourneyDotsArray());
                holder.tv_title.setText(handleNull(item.getJourney().getName()));
                holder.overlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        startPlaylistviewWithJourney(item.getJourney());
                    }
                });
                holder.tv_tracks.setText(item.getJourney().getTrackCount()+" Tracks");
            }
            else{
                holder.journeyView.setJourneyPoints(item.getSession().getJourney().getJourneyDotsArray());
                holder.tv_title.setText(handleNull(item.getSession().getName()));
                holder.overlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        startPlaylistviewWithJourneySession(item.getSession());
                    }
                });
                holder.tv_tracks.setText(item.getSession().getJourney().getTrackCount()+" Tracks");
            }

            holder.journeyView.setEnabled(false);



        }

        private String handleNull(String name) {
            return name == null ? "NEW PLAYLIST" : name;
        }

        @Override
        public int getItemCount() {
            return sessions.size();
        }





        class HistoryViewHolder extends  RecyclerView.ViewHolder {


            LinearLayout ll_saveHistory;
            FrameLayout overlay;
            JourneyView journeyView;
            TextView tv_title, tv_tracks;

            public HistoryViewHolder(View itemView) {
                super(itemView);
                journeyView = (JourneyView) itemView.findViewById(R.id.journey);
                tv_title = (TextView) itemView.findViewById(R.id.tv_title);
                overlay = (FrameLayout) itemView.findViewById(R.id.overlay);
                tv_tracks = (TextView) itemView.findViewById(R.id.tv_tracks);
                ll_saveHistory = (LinearLayout) itemView.findViewById(R.id.ll_saveHistory);
            }
        }
    }

    private FrameLayout.LayoutParams getFrameLayoutParams() {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(dpToPx(126),dpToPx(180));
        layoutParams.setMargins(0, dpToPx(30),0,dpToPx(30));
        return layoutParams;
    }

    private LinearLayout.LayoutParams getLayoutParams() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dpToPx(162),dpToPx(255));
        return layoutParams;
    }

    public int dpToPx(int dp560) {

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int dp = adjustWidth(dp560);
       int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public int adjustWidth(int dp560){
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int dp = dp560 * displayMetrics.widthPixels / 560;
        return (int) UtilityClass.convertPixelsToDpWidth(dp,this);
         //return  dp;
    }

    private void startPlaylistviewWithJourney(Journey journey) {
        JourneyService journeyService = JourneyService.getInstance(this);
        JourneySession session  = journeyService.createJourneySessionFromJourney(journey,null,
                SongMoodCategory.scAllSongs, SongMoodCategory.scAllSongs, false);
        startPlaylistviewWithJourneySession(session);
    }

    private void startPlaylistviewWithJourneySession(JourneySession session) {
        JourneyService journeyService = JourneyService.getInstance(this);
        journeyService.setCurrentSession(session);
        if(musicSrv != null) {
            if (musicSrv.isPng()) {
                musicSrv.pausePlayer();
            }
            musicSrv.playCurrentSession();
            Global.isJourney = true;
            playSelectedSong(0);
            setUpPlaylist();
            // journey.setMode(JourneyView.DrawingMode.DMJOURNEY);
        }
        startActivity(new Intent(this, PlaylistJourneyActivity.class));
        //finish();
    }
}
