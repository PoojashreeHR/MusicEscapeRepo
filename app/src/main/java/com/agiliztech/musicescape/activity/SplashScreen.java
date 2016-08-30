package com.agiliztech.musicescape.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.agiliztech.musicescape.PrivacyActivity;
import com.agiliztech.musicescape.R;
import com.agiliztech.musicescape.activity.MoodMappingActivity;

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
        proceedWithAppLaunch();
    }

      /*  getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isFirstRun", false).commit();
*/

    public void proceedWithAppLaunch() {
        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);

        if (isFirstRun) {
            //show start activity
            // isStoragePermissionGranted();
            startActivity(new Intent(SplashScreen.this, SplashScreen.class));
            Toast.makeText(SplashScreen.this, "First Run", Toast.LENGTH_LONG)
                    .show();
        } else {
            startActivity(new Intent(SplashScreen.this, MoodMappingActivity.class));
            finish();
            Toast.makeText(SplashScreen.this, "Second Run", Toast.LENGTH_LONG)
                    .show();
        }
    }
}
