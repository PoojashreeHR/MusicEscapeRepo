package com.agiliztech.musicescape.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.agiliztech.musicescape.R;
import com.agiliztech.musicescape.journey.JourneyView;
import com.agiliztech.musicescape.journey.Size;

public class DrawingViewActivity extends AppCompatActivity {

    private JourneyView journey;
    private FrameLayout overlay;


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

        journey = (JourneyView)findViewById(R.id.journey);
        overlay = (FrameLayout) findViewById(R.id.overlay);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(dpToPx(320), dpToPx(445));
        lparams.gravity = Gravity.CENTER;
       // overlay.setLayoutParams(lparams);



        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(dpToPx(280), dpToPx(400));
        //params.setMargins(adjust(40),0,adjust(40),0);
     //   params.setMargins(adjust(40),adjust(40),adjust(40),adjust(40));
        params.gravity = Gravity.CENTER;

        journey.setLayoutParams(params);

        journey.setGaps(new Size(0.92500000000000004f*displayMetrics.widthPixels/560f, 0.96999999999999997f*displayMetrics.heightPixels/560f));
        journey.setMode(JourneyView.DrawingMode.DMDRAWING);
    }
}
