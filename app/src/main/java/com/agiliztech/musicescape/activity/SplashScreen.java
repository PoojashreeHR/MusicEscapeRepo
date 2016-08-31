package com.agiliztech.musicescape.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.agiliztech.musicescape.R;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Typeface tf = Typeface.createFromAsset(getAssets(),
                "fonts/MontserratRegular.ttf");
        TextView content = (TextView) findViewById(R.id.textView10);
        content.setTypeface(tf);
        Button startedButton = (Button) findViewById(R.id.getStarted);
        startedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //isStoragePermissionGranted();
                Intent intent = new Intent(getApplicationContext(), SlidingImage.class);
                intent.putExtra("moodMapping","MoodMapping");
                startActivity(intent);
                finish();
            }
        });

        RelativeLayout splashScreen = (RelativeLayout) findViewById(R.id.splashScreen);
        splashScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PrivacyActivity.class);
                intent.putExtra("splash", "SlashScreen");
                startActivity(intent);
            }
        });

    }
}
