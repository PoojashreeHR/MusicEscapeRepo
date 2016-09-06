package com.agiliztech.musicescape.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.agiliztech.musicescape.R;

public class SettingsActivity extends AppCompatActivity {


    SwitchCompat switchCompat;
    TextView first_limit, third_limit, fourth_limit, second_limit;
    RelativeLayout rl_limit;
    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedpreferences;
    String key = "limit_journey";
    private ImageView dashboardButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        dashboardButton = (ImageView) findViewById(R.id.imageButton2);
        dashboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this,NewDashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });
        rl_limit = (RelativeLayout) findViewById(R.id.rl_limit);
        switchCompat = (SwitchCompat) findViewById(R.id.switchButton);
        first_limit = (TextView) findViewById(R.id.first_limit);
        second_limit = (TextView) findViewById(R.id.second_limit);
        third_limit = (TextView) findViewById(R.id.third_limit);
        fourth_limit = (TextView) findViewById(R.id.fourth_limit);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        int limit = sharedpreferences.getInt(key, 0);
        if (limit == 0) {
            switchCompat.setChecked(false);
            setAllLimits(false);
        }
        else{
            switchCompat.setChecked(true);
            setAllLimits(true);
        }

        if (limit == 1){
            first_limit.setTextColor(Color.parseColor("#ffffff"));

        }
        else if (limit == 2){
            second_limit.setTextColor(Color.parseColor("#ffffff"));

        }
        else if (limit == 3){
            third_limit.setTextColor(Color.parseColor("#ffffff"));

        }
        else if (limit == 4){
            fourth_limit.setTextColor(Color.parseColor("#ffffff"));

        }


        first_limit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideAllLimits();
                first_limit.setTextColor(Color.parseColor("#ffffff"));
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putInt(key, 1);
                editor.commit();
            }
        });

        second_limit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideAllLimits();
                second_limit.setTextColor(Color.parseColor("#ffffff"));
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putInt(key, 2);
                editor.commit();
            }
        });

        third_limit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideAllLimits();
                third_limit.setTextColor(Color.parseColor("#ffffff"));
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putInt(key, 3);
                editor.commit();
            }
        });

        fourth_limit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideAllLimits();
                fourth_limit.setTextColor(Color.parseColor("#ffffff"));
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putInt(key, 4);
                editor.commit();
            }
        });


        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    hideAllLimits();
                    setDefaultFirstOne();


                } else {
                    hideAllLimits();
                    setAllLimits(false);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putInt(key, 0);
                    editor.commit();

                }
            }
        });


    }

    private void setDefaultFirstOne() {
        first_limit.setTextColor(Color.parseColor("#ffffff"));
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt(key, 1);
        editor.commit();
        setAllLimits(true);

    }

    private void setAllLimits(boolean enabled) {
        first_limit.setEnabled(enabled);
        second_limit.setEnabled(enabled);
        third_limit.setEnabled(enabled);
        fourth_limit.setEnabled(enabled);
    }

    private void hideAllLimits() {
        first_limit.setTextColor(Color.parseColor("#252525"));
        second_limit.setTextColor(Color.parseColor("#252525"));
        third_limit.setTextColor(Color.parseColor("#252525"));
        fourth_limit.setTextColor(Color.parseColor("#252525"));
    }
}
