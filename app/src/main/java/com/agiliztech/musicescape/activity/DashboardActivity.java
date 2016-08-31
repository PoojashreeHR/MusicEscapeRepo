package com.agiliztech.musicescape.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.agiliztech.musicescape.R;
import com.agiliztech.musicescape.view.PieChart;

import java.util.Timer;
import java.util.TimerTask;

public class DashboardActivity extends AppCompatActivity {

    ImageView menu_activeSettings,menu_activelibrary,menu_library, menu_settings,menu_activedraw;
    private ImageView menu_draw;
    String dashboard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        dashboard =  getIntent().getStringExtra("dashboard");

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        menu_activedraw = (ImageView) findViewById(R.id.menu_activedraw);
        menu_draw = (ImageView) findViewById(R.id.menu_draw);
        menu_draw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu_activedraw.setVisibility(View.VISIBLE);
                Intent intent = new Intent(DashboardActivity.this,DrawingViewActivity.class);
                startActivity(intent);
                finish();
            }
        });

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



        Resources res = getResources();
        final PieChart pie = (PieChart) this.findViewById(R.id.Pie);
//        pie.addItem("Agamemnon", 1, res.getColor(R.color.seafoam));
//        pie.addItem("Bocephus", 1, res.getColor(R.color.chartreuse));
//        pie.addItem("Calliope", 1, res.getColor(R.color.emerald));
//        pie.addItem("Daedalus", 1, res.getColor(R.color.bluegrass));
//        pie.addItem("Euripides", 1, res.getColor(R.color.turquoise));
//        pie.addItem("Ganymede", 1, res.getColor(R.color.slate));
//
//        pie.addItem("Calliope", 1, res.getColor(R.color.emerald));
//        pie.addItem("Daedalus", 1, res.getColor(R.color.bluegrass));
//        pie.addItem("Euripides", 1, res.getColor(R.color.turquoise));
//        pie.addItem("Ganymede", 1, res.getColor(R.color.slate));

        pie.addItem("Agamemnon", 1, res.getColor(R.color.black));
        pie.addItem("Bocephus", 1, res.getColor(R.color.black));
        pie.addItem("Calliope", 1, res.getColor(R.color.black));
        pie.addItem("Daedalus", 1, res.getColor(R.color.black));
        pie.addItem("Euripides", 1, res.getColor(R.color.black));
        pie.addItem("Ganymede", 1, res.getColor(R.color.black));

        pie.addItem("Calliope", 1, res.getColor(R.color.black));
        pie.addItem("Daedalus", 1, res.getColor(R.color.black));
        pie.addItem("Euripides", 1, res.getColor(R.color.black));
        pie.addItem("Ganymede", 1, res.getColor(R.color.black));


    }
    public void  run()
    {
        new Timer().schedule(new TimerTask(){
            public void run() {
                startActivity(new Intent(DashboardActivity.this, DashboardActivity.class));
                finish();
            }
        }, 4000);
    }
    @Override
    public void onResume()
    {    super.onResume();
     //   settings.edit().putBoolean("is_first_time", false).commit();
        getSharedPreferences("MyPreference", MODE_PRIVATE).edit()
                .putBoolean("is_first_time", false).commit();
    }
}
