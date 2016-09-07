package com.agiliztech.musicescape.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.agiliztech.musicescape.R;
import com.agiliztech.musicescape.journey.JourneyDBHelper;
import com.agiliztech.musicescape.journey.JourneyService;
import com.agiliztech.musicescape.journey.JourneySessionDBHelper;
import com.agiliztech.musicescape.journey.JourneyView;
import com.agiliztech.musicescape.journey.Size;
import com.agiliztech.musicescape.models.JourneySession;

public class PlaylistJourneyActivity extends BaseMusicActivity {

    private JourneyView journeyView;
    private EditText title;
    private FrameLayout overlay;
    private ImageView dashboardButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_journey);

        //hideMusicPlayer();
        JourneyService journeyService = JourneyService.getInstance(this);

        journeyView = (JourneyView) findViewById(R.id.journey);
        overlay = (FrameLayout) findViewById(R.id.overlay);

        dashboardButton = (ImageView) findViewById(R.id.imageButton2);
        dashboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlaylistJourneyActivity.this,NewDashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(dpToPx(320), dpToPx(445));
        lparams.gravity = Gravity.CENTER;
        // overlay.setLayoutParams(lparams);



        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(dpToPx(280), dpToPx(400));
        //params.setMargins(adjust(40),0,adjust(40),0);
        //   params.setMargins(adjust(40),adjust(40),adjust(40),adjust(40));
        params.gravity = Gravity.CENTER;

        journeyView.setLayoutParams(params);

        journeyView.setJourneyPoints(journeyService.getCurrentSession().getJourney().getJourneyDotsArray());
        journeyView.setTrackDots(journeyService.getCurrentSession().trackNumbers());
        journeyView.setGaps(new Size(0.92500000000000004f*displayMetrics.widthPixels/560f, 0.96999999999999997f*displayMetrics.heightPixels/560f));

        journeyView.setMode(JourneyView.DrawingMode.DMJOURNEY);
        journeyView.setEnabled(false);

        title = (EditText) findViewById(R.id.title);
        String name = journeyService.getCurrentSession().getJourney().getName();
        final JourneySession session = journeyService.getCurrentSession();
        if(name == null || name.equalsIgnoreCase("NEW PLAYLIST")) {
            title.setText(journeyService.getCurrentSession().getName());
            title.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if ((actionId == EditorInfo.IME_ACTION_DONE) || ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN))) {

                        String name = v.getText().toString();
                        if(name == null || name.length() == 0){
                            Toast.makeText(PlaylistJourneyActivity.this, "Please enter a valid name !!!", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        updateJourneyName(session, name);

                        return false;
                    }
                    return false;
                }
            });

        }
        else{
            title.setText(name);
            title.setEnabled(false);

        }





    }

    private void updateJourneyName(JourneySession session, String name) {
        JourneySessionDBHelper journeySessionDBHelper = new JourneySessionDBHelper(this);
        journeySessionDBHelper.updateName(session,name);
        journeySessionDBHelper.close();
    }

    private int dpToPx(int dp560) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int dp = dp560;//adjust(dp560);
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }
}
