package com.agiliztech.musicescape.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.agiliztech.musicescape.R;

import java.util.ArrayList;
import java.util.List;

public class LibraryActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Spinner sp;
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);


        Typeface tf = Typeface.createFromAsset(getAssets(),
                "fonts/MontserratRegular.ttf");
        TextView tv = (TextView) findViewById(R.id.library);
        tv.setTypeface(tf);


        final ImageButton songScan = (ImageButton) findViewById(R.id.imageButton);
        songScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //songScan.setBackgroundResource(R.drawable.menu_buttons_song_scan_active);
                Toast.makeText(getApplicationContext(), "Button clicked!",
                        Toast.LENGTH_SHORT).show();
                songScan.setFocusableInTouchMode(false);
                songScan.setFocusable(false);
                Intent intent = new Intent(getApplicationContext(), MoodMappingActivity.class);
                startActivity(intent);
            }
        });

        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Aggressive");
        categories.add("Bored" );
        categories.add("Chilled");
        categories.add("Depressed");
        categories.add("Exited");
        categories.add("Happy");
        categories.add("Peacefull");
        categories.add("Stressed");


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }
    public void onNothingSelected(AdapterView<?> arg0) {

    }

}
