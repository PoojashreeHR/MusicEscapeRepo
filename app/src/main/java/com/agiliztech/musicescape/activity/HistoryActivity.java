package com.agiliztech.musicescape.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.agiliztech.musicescape.R;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ImageView dashboardButton = (ImageView) findViewById(R.id.imageButton2);
        dashboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistoryActivity.this,DashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onResume()
    {    super.onResume();
        //   settings.edit().putBoolean("is_first_time", false).commit();
        getSharedPreferences("DashboardPreference", MODE_PRIVATE).edit()
                .putBoolean("history", false).commit();
    }
}
