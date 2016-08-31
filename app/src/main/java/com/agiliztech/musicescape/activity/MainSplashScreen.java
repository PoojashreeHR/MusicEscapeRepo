package com.agiliztech.musicescape.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.agiliztech.musicescape.R;

public class MainSplashScreen extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 500;
    TextView mTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_splash_screen);
        SpannableStringBuilder builder = new SpannableStringBuilder();

        mTextView = (TextView) findViewById(R.id.textView11);
        String red = "music\t";
        SpannableString redSpannable= new SpannableString(red);
        redSpannable.setSpan(new ForegroundColorSpan(Color.GRAY), 0, red.length(), 0);
        builder.append(redSpannable);

        String white = "e";
        SpannableString whiteSpannable= new SpannableString(white);
        whiteSpannable.setSpan(new ForegroundColorSpan(Color.WHITE), 0, white.length(), 0);
        builder.append(whiteSpannable);

        String blue = "Scape";
        SpannableString blueSpannable = new SpannableString(blue);
        blueSpannable.setSpan(new ForegroundColorSpan(Color.GRAY), 0, blue.length(), 0);
        builder.append(blueSpannable);

        mTextView.setText(builder, TextView.BufferType.SPANNABLE);
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(MainSplashScreen.this, MoodMappingActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
