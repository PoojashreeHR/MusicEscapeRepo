package com.agiliztech.musicescape.activity;

import android.content.Intent;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.agiliztech.musicescape.R;
import com.agiliztech.musicescape.journey.JourneySessionDBHelper;
import com.agiliztech.musicescape.journey.JourneyView;
import com.agiliztech.musicescape.models.History;
import com.agiliztech.musicescape.models.JourneySession;
import com.agiliztech.musicescape.utils.SongsManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {



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
             int pos = position;
            JourneySession session = sessions.get(pos);
            //for (int i = 0; i < sessions.size() ; i++) {
                if (pos % 2 == 0) {
                    holder.right_view.setVisibility(View.VISIBLE);
                   // pos++;
                }
                else{
                        holder.right_view.setVisibility(View.GONE);
                   // pos--;
                }
            //}
            holder.journeyView.setMode(JourneyView.DrawingMode.DMMENU);
            holder.journeyView.setJourneyPoints(session.getJourney().getJourneyDotsArray());
            holder.journeyView.setEnabled(false);


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




           // holder.tv_playlistCreated.setText();
        }

        @Override
        public int getItemCount() {
            return sessions.size();
        }





         class HistoryViewHolder extends  RecyclerView.ViewHolder {

             JourneyView journeyView;
              TextView tv_currentmood,tv_targetmood,tv_playlistCreated;
             View right_view;

             public HistoryViewHolder(View itemView) {
                 super(itemView);
                 journeyView = (JourneyView) itemView.findViewById(R.id.journey);
                 tv_currentmood = (TextView) itemView.findViewById(R.id.tv_currentmood);
                 tv_targetmood = (TextView) itemView.findViewById(R.id.tv_targetmood);
                 tv_playlistCreated = (TextView) itemView.findViewById(R.id.tv_playlistCreated);
                 right_view = (View) itemView.findViewById(R.id.right_view);

             }
         }
    }
}
