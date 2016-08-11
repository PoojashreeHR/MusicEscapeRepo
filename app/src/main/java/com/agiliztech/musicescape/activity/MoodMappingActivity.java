package com.agiliztech.musicescape.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.agiliztech.musicescape.R;

public class MoodMappingActivity extends AppCompatActivity {

   // Button musicButton;
    private boolean isPlaying = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_mapping);

        final Button testButton = (Button) findViewById(R.id.button);
        testButton.setText("Start");
        testButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!isPlaying) {
                    //  mPlayer.start();
                    testButton.setText("Pause");
                    isPlaying = true;
                } else {
                    // mPlayer.stop();
                    testButton.setText("Start");
                    isPlaying = false;
                }
            }
        });
    }
}