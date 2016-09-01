package com.agiliztech.musicescape.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.agiliztech.musicescape.R;
import com.agiliztech.musicescape.journey.JourneyService;
import com.agiliztech.musicescape.journey.JourneyView;

public class PlaylistJourneyActivity extends BaseMusicActivity {

    private JourneyView journeyView;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_journey);

        JourneyService journeyService = JourneyService.getInstance(this);

        journeyView = (JourneyView) findViewById(R.id.journey);
        journeyView.setJourneyPoints(journeyService.getCurrentSession().getJourney().getJourneyDotsArray());
        journeyView.setTrackDots(journeyService.getCurrentSession().trackNumbers());
        journeyView.setMode(JourneyView.DrawingMode.DMJOURNEY);

        title = (TextView) findViewById(R.id.title);
        title.setText(journeyService.getCurrentSession().getJourney().getName());
    }
}
