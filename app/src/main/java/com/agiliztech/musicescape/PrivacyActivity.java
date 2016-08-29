package com.agiliztech.musicescape;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.agiliztech.musicescape.activity.AppInfoActivity;
import com.agiliztech.musicescape.activity.SplashScreen;

public class PrivacyActivity extends AppCompatActivity {
    ImageView backButton;
    String appInfo;
    String isClicked = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        TextView textView = (TextView) findViewById(R.id.privacyText);
        appInfo =  getIntent().getStringExtra("appInfo");
        final String  splashScreen =  getIntent().getStringExtra("splash");

        backButton = (ImageView) findViewById(R.id.backbutton2);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(appInfo != isClicked)
                {
                    startActivity(new Intent(getApplicationContext(), AppInfoActivity.class));
                }
                else
                {
                    startActivity(new Intent(getApplicationContext(), SplashScreen.class));
                }
            }
        });
    }
}
