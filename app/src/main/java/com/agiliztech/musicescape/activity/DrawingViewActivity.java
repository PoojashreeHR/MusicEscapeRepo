package com.agiliztech.musicescape.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.agiliztech.musicescape.R;
import com.agiliztech.musicescape.journey.JourneyService;
import com.agiliztech.musicescape.journey.JourneyView;
import com.agiliztech.musicescape.journey.Size;
import com.agiliztech.musicescape.journey.SongMoodCategory;
import com.agiliztech.musicescape.models.Journey;
import com.agiliztech.musicescape.models.JourneySession;
import com.agiliztech.musicescape.models.Song;
import com.agiliztech.musicescape.utils.Global;
import com.agiliztech.musicescape.utils.SongsManager;

import java.util.List;

public class DrawingViewActivity extends BaseMusicActivity {

    private JourneyView journey;
    private FrameLayout overlay;
    private ImageView dashboardButton;
    private ImageButton playBtn;
    private SongMoodCategory currentMood = SongMoodCategory.scMoodNotFound, targetMood = SongMoodCategory.scMoodNotFound;



    class JourneyCreator extends AsyncTask<Void,Void,Void>{
        private  ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(DrawingViewActivity.this, "", "Creating Journey...");
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(progressDialog != null) {
                progressDialog.dismiss();
            }
            drawViewToPlaylist();
        }

        @Override
        protected Void doInBackground(Void... params) {
            generatePlaylist();
            return null;
        }
    }

    public int dpToPx(int dp560) {

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int dp = dp560;//adjust(dp560);
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public int adjust(int dp560){
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int dp = dp560 * displayMetrics.widthPixels / 560;
        return dp;
       // return  dp560;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing_view);

        dashboardButton = (ImageView) findViewById(R.id.imageButton2);
        dashboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DrawingViewActivity.this,DashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Intent i = getIntent();
        int curIndex = i.getIntExtra("current",8);
        int targetIndex = i.getIntExtra("target",8);

         currentMood = SongsManager.getMoodFromIndex(curIndex);
        targetMood = SongsManager.getMoodFromIndex(targetIndex);

        hideMusicPlayer();

        journey = (JourneyView)findViewById(R.id.journey);
        overlay = (FrameLayout) findViewById(R.id.overlay);
        playBtn = (ImageButton) findViewById(R.id.play);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(dpToPx(320), dpToPx(445));
        lparams.gravity = Gravity.CENTER;
       // overlay.setLayoutParams(lparams);



        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(dpToPx(280), dpToPx(400));
        //params.setMargins(adjust(40),0,adjust(40),0);
     //   params.setMargins(adjust(40),adjust(40),adjust(40),adjust(40));
        params.gravity = Gravity.CENTER;

        journey.setLayoutParams(params);
        //journey.setMode(JourneyView.DrawingMode.DMDRAWING);
        journey.setGaps(new Size(0.92500000000000004f*displayMetrics.widthPixels/560f, 0.96999999999999997f*displayMetrics.heightPixels/560f));
        journey.setMode(JourneyView.DrawingMode.DMDRAWING);




        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlayBtnClicked();
            }
        });
    }

    private void onPlayBtnClicked() {

        List<PointF> vePoints = journey.journeyAsValenceAndEnergyPoints();
        if(vePoints == null || vePoints.size() == 0){
            Toast.makeText(DrawingViewActivity.this, "Please draw a journey !!!", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(vePoints.size() < 2){
            Toast.makeText(DrawingViewActivity.this, getString(R.string.not_enough_draw_points), Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            int totalDuration = JourneyService.getInstance(this).getCurrentSession().totalDuration();
            int totalSongDuration = JourneyService.getInstance(this).getCurrentSession().totalSongsDuration();

            if(Global.isJourney && totalDuration >= totalSongDuration/2){
                showMJView();
            }
            else{
                new JourneyCreator().execute();
            }
        }

    }

    private void generatePlaylist() {
        List<PointF> vePoints = journey.journeyAsValenceAndEnergyPoints();
        List<Song> journeySongs = JourneyService.getInstance(this).createPlaylistFromJourney(vePoints);

        List<Song> filterNullArray = JourneyService.getInstance(this).filterNullSongs(journeySongs);

        if(filterNullArray.size() < 2){
            Toast.makeText(DrawingViewActivity.this, getString(R.string.not_enough_draw_points), Toast.LENGTH_SHORT).show();
            return;
        }

        Journey newJourney = new Journey();
        newJourney.setGeneratedBy("User");
        newJourney.setJourneyDotsArray(journey.getJourneyPoints());
        newJourney.setJourneyEVArray(vePoints);
        newJourney.setTrackCount(filterNullArray.size());

        JourneySession session = JourneyService.getInstance(this).createJourneySessionFromJourney(newJourney, filterNullArray, currentMood, targetMood);
        JourneyService.getInstance(this).setCurrentSession(session);

       // drawViewToPlaylist();
    }

    private void drawViewToPlaylist() {
        if(musicSrv != null) {
            if (musicSrv.isPng()) {
                musicSrv.pausePlayer();
            }
            musicSrv.playCurrentSession();
            Global.isJourney = true;
            Global.isLibPlaylist = false;
            Global.libPlaylistSongs = null;
            playSelectedSong(0);
            setUpPlaylist();
           // journey.setMode(JourneyView.DrawingMode.DMJOURNEY);
        }
        Intent nextIntent = new Intent(this, PlaylistJourneyActivity.class);
        nextIntent.putExtra("fromMenu",false);
        nextIntent.putExtra("currentMood", SongsManager.getIntValue(currentMood));
        nextIntent.putExtra("targetMood", SongsManager.getIntValue(targetMood));


        startActivity(nextIntent);
        finish();
    }

    private void showMJView() {
        //journey.setMode(JourneyView.DrawingMode.DMJOURNEY);
        //show current and target mood here
    }

    @Override
    public void onResume()
    {    super.onResume();
        //   settings.edit().putBoolean("is_first_time", false).commit();
        getSharedPreferences("DashboardPreference", MODE_PRIVATE).edit()
                .putBoolean("draw", false).commit();
    }
}
