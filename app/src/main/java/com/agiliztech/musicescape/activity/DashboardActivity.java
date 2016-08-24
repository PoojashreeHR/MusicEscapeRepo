package com.agiliztech.musicescape.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.agiliztech.musicescape.R;

public class DashboardActivity extends AppCompatActivity {

    ImageView menu_activeSettings,menu_activelibrary,menu_library, menu_settings;
    private ImageView menu_draw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        menu_activeSettings = (ImageView) findViewById(R.id.menu_activeSettings);
        menu_settings = (ImageView) findViewById(R.id.menu_settings);
        menu_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu_activeSettings.setVisibility(View.VISIBLE);
                Intent intent = new Intent(DashboardActivity.this,SettingsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        menu_activelibrary = (ImageView) findViewById(R.id.menu_activelibrary);
        menu_library = (ImageView) findViewById(R.id.menu_library);
        menu_library.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu_activelibrary.setVisibility(View.VISIBLE);
                Intent intent = new Intent(DashboardActivity.this,LibraryActivity.class);
                startActivity(intent);
                finish();
            }
        });

        menu_draw = (ImageView) findViewById(R.id.menu_draw);
        menu_draw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this,DrawingViewActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }


}
