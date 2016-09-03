package com.agiliztech.musicescape.activity;

import android.content.Intent;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.agiliztech.musicescape.R;
import com.agiliztech.musicescape.journey.JourneyService;
import com.agiliztech.musicescape.journey.JourneySessionDBHelper;
import com.agiliztech.musicescape.journey.JourneyView;
import com.agiliztech.musicescape.journey.Size;
import com.agiliztech.musicescape.models.JourneySession;
import com.agiliztech.musicescape.utils.SongsManager;
import com.agiliztech.musicescape.utils.Global;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HistoryActivity extends BaseMusicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ImageView dashboardButton = (ImageView) findViewById(R.id.imageButton2);
        dashboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistoryActivity.this,DashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.history_rv);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.setAdapter(new HistoryAdapter(getHistoryData()));
    }

    private List<JourneySession> getHistoryData() {
        JourneySessionDBHelper journeySessionDBHelper = new JourneySessionDBHelper(this);
        List<JourneySession> lastEight = journeySessionDBHelper.getTopEightSessions();
        return lastEight;
    }

    @Override
    public void onResume()
    {    super.onResume();
        //   settings.edit().putBoolean("is_first_time", false).commit();
        getSharedPreferences("DashboardPreference", MODE_PRIVATE).edit()
                .putBoolean("history", false).commit();
    }

    private class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>

    {
        private  List<JourneySession> sessions;

        public HistoryAdapter(List<JourneySession> journeySessionList) {
            this.sessions = journeySessionList;
        }


        @Override
        public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);
            return new HistoryViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(HistoryViewHolder holder, int position) {
            final int pos = position;
            final JourneySession session = sessions.get(pos);
            if (pos % 2 == 0) {
                holder.right_view.setVisibility(View.VISIBLE);
                // pos++;
            }
            else{
                holder.right_view.setVisibility(View.GONE);
                // pos--;
            }

            holder.journeyView.setGaps(getGapsSize());
            holder.journeyView.setMode(JourneyView.DrawingMode.DMMENU);
            holder.journeyView.setJourneyPoints(session.getJourney().getJourneyDotsArray());
            holder.journeyView.setEnabled(false);

            holder.overlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //startPlaylistviewWithJourney(session);
                    saveSession(session);
                }
            });

            holder.tv_currentmood.setTextColor(SongsManager.colorForMood(session.getCurrentMood()));
            holder.tv_targetmood.setTextColor(SongsManager.colorForMood(session.getTargetMood()));

            holder.tv_currentmood.setText(SongsManager.textForMood(SongsManager.getIntValue(session.getCurrentMood())));
            holder.tv_targetmood.setText(SongsManager.textForMood(SongsManager.getIntValue(session.getTargetMood())));


            Date myDate= session.getStarted();
            String formatedDate = "";
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM");
                formatedDate = dateFormat.format(myDate);
                holder.tv_playlistCreated.setText(formatedDate);
            } catch (Exception exception) {
                exception.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return sessions.size();
        }





         class HistoryViewHolder extends  RecyclerView.ViewHolder {

             FrameLayout overlay;
             JourneyView journeyView;
              TextView tv_currentmood,tv_targetmood,tv_playlistCreated;
             View right_view;

             public HistoryViewHolder(View itemView) {
                 super(itemView);
                 journeyView = (JourneyView) itemView.findViewById(R.id.journey);
                 overlay = (FrameLayout) itemView.findViewById(R.id.overlay);
                 tv_currentmood = (TextView) itemView.findViewById(R.id.tv_currentmood);
                 tv_targetmood = (TextView) itemView.findViewById(R.id.tv_targetmood);
                 tv_playlistCreated = (TextView) itemView.findViewById(R.id.tv_playlistCreated);
                 right_view = (View) itemView.findViewById(R.id.right_view);

             }
         }
    }

    private void saveSession(JourneySession session) {
        JourneySessionDBHelper journeySessionDBHelper = new JourneySessionDBHelper(this);
        session.setFavourite(1);
        journeySessionDBHelper.favJourneySession(session);
    }

    private void startPlaylistviewWithJourney(JourneySession session) {
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
        finish();
    }

    private Size getGapsSize() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return new Size(0.92500000000000004f*displayMetrics.widthPixels/560f, 0.96999999999999997f*displayMetrics.heightPixels/560f);

    }
}
