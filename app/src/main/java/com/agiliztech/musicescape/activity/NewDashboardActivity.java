package com.agiliztech.musicescape.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
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
import com.agiliztech.musicescape.journey.JourneySessionDBHelper;
import com.agiliztech.musicescape.journey.JourneyView;
import com.agiliztech.musicescape.journey.Size;
import com.agiliztech.musicescape.journey.SongMoodCategory;
import com.agiliztech.musicescape.models.DashboardItem;
import com.agiliztech.musicescape.models.Journey;
import com.agiliztech.musicescape.models.JourneySession;
import com.agiliztech.musicescape.musicservices.MusicService;
import com.agiliztech.musicescape.utils.Global;
import com.agiliztech.musicescape.utils.UtilityClass;

import java.util.List;
import java.util.Timer;

public class NewDashboardActivity extends BaseMusicActivity {

    final Context context = this;
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

        RelativeLayout.LayoutParams relparams = new RelativeLayout.LayoutParams(dpToPx(163), RelativeLayout.LayoutParams.WRAP_CONTENT);
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

        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView_user.setLayoutManager(new GridLayoutManager(this,2));

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(dpToPx(163), RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        recyclerView.setLayoutParams(params);
        recyclerView_user.setLayoutParams(params);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(JourneyService.getInstance(this).needsToGeneratePresets()){
            //create journey and load asynchronously
            new GeneratePresetTask().execute();
        }
        else {
            recyclerView.setAdapter(new DashboardAdapter(getJourneyData()));
        }
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
        return new Size(0.92500000000000004f*displayMetrics.widthPixels/(2*560f), 0.96999999999999997f*displayMetrics.heightPixels/(2*560f));

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
        public void onBindViewHolder(HistoryViewHolder holder, final int position) {
            final int pos = position;
            final DashboardItem item = sessions.get(pos);

            holder.ll_saveHistory.setLayoutParams(getHistoryLayoutParams());
            holder.overlay.setLayoutParams(getLayoutParams());
            holder.overlay.setPadding(dpToPx(9),0, dpToPx(9),0);
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
                holder.overlay.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        //show dialod
                        //yes -> deleteItem(position)
                        final Dialog dialogs = new Dialog(context);
                        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View layout = inflater.inflate(R.layout.dialog_playlist, null);
                        dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialogs.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        Button btn_yes = (Button) layout.findViewById(R.id.btn_yes);
                        btn_yes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deleteItem(item.getSession());
                                //notifyItemRemoved(position);
                                dialogs.dismiss();
                            }
                        });
                        Button btn_no = (Button) layout.findViewById(R.id.btn_no);
                        btn_no.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogs.dismiss();
                            }
                        });

                        dialogs.setContentView(layout);
                        dialogs.show();

                        return false;
                    }
                });
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

    private void deleteItem(JourneySession session) {
        JourneySessionDBHelper journeySessionDBHelper = new JourneySessionDBHelper(this);
        journeySessionDBHelper.deleteSessionFromFavs(session);
        journeySessionDBHelper.close();
        recyclerView_user.setAdapter(new DashboardAdapter(getUserJourneyData()));
    }

    private FrameLayout.LayoutParams getFrameLayoutParams() {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(dpToPx(63),dpToPx(90));
        layoutParams.setMargins(0, dpToPx(30),0,dpToPx(30));
        return layoutParams;
    }

    private LinearLayout.LayoutParams getLayoutParams() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dpToPx(81),dpToPx(127));
        return layoutParams;
    }

    private LinearLayout.LayoutParams getHistoryLayoutParams() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dpToPx(82),dpToPx(170));
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
       new LoadingPresetTask(this, journey).execute();
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

    @Override
    public void onBackPressed() {
        if(musicSrv!=null){
            if(!musicSrv.isPng()){
                musicSrv.stopSelf();
                musicSrv.killService();
                if (musicSrv != null) {
                    stopService(new Intent(this, MusicService.class));
                    if (musicSrv != null)
                        musicSrv.pausePlayer();
                }
            }
        }
        super.onBackPressed();

    }

    @Override
    protected void onDestroy() {
       /* musicSrv.stopSelf();
        musicSrv.killService();
        if (musicSrv != null) {
            stopService(new Intent(this, MusicService.class));
            if (musicSrv != null)
                musicSrv.pausePlayer();
        }*/
        super.onDestroy();
    }

    private class GeneratePresetTask extends AsyncTask<Void, Void, List<DashboardItem>> {
        private ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(NewDashboardActivity.this, "", "Generating Presets...");
        }

        @Override
        protected void onPostExecute(List<DashboardItem> aVoid) {
            if(progressDialog != null) {
                progressDialog.dismiss();
            }
            recyclerView.setAdapter(new DashboardAdapter(aVoid));
        }

        @Override
        protected List<DashboardItem> doInBackground(Void... params) {
            return getJourneyData();
        }
    }

    private class LoadingPresetTask extends AsyncTask<Void, Journey, JourneySession> {
        private Context thisContext;
        private Journey thisJourney;
        private ProgressDialog progressDialog;

        public LoadingPresetTask(Context context, Journey journey) {
            thisContext = context;
            thisJourney = journey;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(NewDashboardActivity.this, "", "Loading Preset...");
        }

        @Override
        protected void onPostExecute(JourneySession session) {
            JourneyService journeyService = JourneyService.getInstance(thisContext);
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

            if(progressDialog != null) {
                progressDialog.dismiss();
            }
            startActivity(new Intent(thisContext, PlaylistJourneyActivity.class));
            //startPlaylistviewWithJourneySession(session);

        }

        @Override
        protected JourneySession doInBackground(Void... params) {
            JourneyService journeyService = JourneyService.getInstance(thisContext);
            return journeyService.createJourneySessionFromJourney(thisJourney,null,
                    SongMoodCategory.scAllSongs, SongMoodCategory.scAllSongs, false);
        }
    }


    @Override
    protected void onPause() {
        SharedPreferences sp = getSharedPreferences(Global.PREF_NAME, MODE_PRIVATE);
        if (playbackPaused) {
            if (musicSrv != null) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("playbackpaused", "" + playbackPaused);
                //JourneySession curSession = JourneyService.getInstance(this).getCurrentSession();
                if(!Global.isJourney){
                    editor.putString(Global.LAST_PL_TYPE, "");
                    editor.putString(Global.LAST_JOURNEY_ID, "");
                    editor.putInt(Global.LAST_SONG_POS, musicSrv.getSongPosn());
                }
                else{
                    editor.putInt(Global.LAST_SONG_POS, musicSrv.getSongPosn());
                    String gen = JourneyService.getInstance(this).getCurrentSession().getJourney().getGeneratedBy();
                    if(gen.equalsIgnoreCase("Preset")) {
                        editor.putString(Global.LAST_JOURNEY_ID, JourneyService.getInstance(this).getCurrentSession().getJourney().getName());
                    }
                    else{
                        editor.putString(Global.LAST_JOURNEY_ID, JourneyService.getInstance(this).getCurrentSession().getJourneyID());
                    }
                    editor.putString(Global.LAST_PL_TYPE, gen);
                }
//                    editor.putString("song_id_sp", musicSrv.getSongId());
//                    editor.putString("song_position", "" + musicSrv.getPosn());
//                    editor.putString("song_name_sp", musicSrv.getSongName());
                editor.apply();
            }
        }
        super.onPause();
    }
}
