package com.agiliztech.musicescape.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.agiliztech.musicescape.R;

public class DashboardActivity extends AppCompatActivity {

    ImageView menu_activeSettings,menu_settings;

    private ImageView settingsButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        menu_activeSettings = (ImageView) findViewById(R.id.menu_activeSettings);
        settingsButton = (ImageView) findViewById(R.id.menu_settings);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu_activeSettings.setVisibility(View.VISIBLE);
                Intent intent = new Intent(DashboardActivity.this,SettingsActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


}
