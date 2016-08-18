package com.agiliztech.musicescape.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.agiliztech.musicescape.R;

public class LibraryActivity extends AppCompatActivity implements View.OnClickListener {
    Spinner sp;
    private View mViewGroup;
    private View songViewGroup;
    private ImageButton mButton,songButton;
    TextView moodList,songs;
    Typeface tf;
    LinearLayout linearLayout;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);


        linearLayout = (LinearLayout)findViewById(R.id.viewContainer);
        tf = Typeface.createFromAsset(getAssets(), "fonts/MontserratRegular.ttf");
        TextView library = (TextView) findViewById(R.id.library);
        moodList = (TextView) findViewById(R.id.textView9);
        songs = (TextView) findViewById(R.id.songs);
        library.setTypeface(tf);
        moodList.setTypeface(tf);
        songs.setTypeface(tf);

        songViewGroup = findViewById(R.id.songSort);
        mViewGroup = findViewById(R.id.viewContainer);
        mButton = (ImageButton) findViewById(R.id.arrow);
        songButton = (ImageButton) findViewById(R.id.arrow1);
        mViewGroup.setOnClickListener(this);

        final ImageButton songScan = (ImageButton) findViewById(R.id.library1);
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

      // final ImageButton mbutton = (ImageButton) findViewById(R.id.arrow);
       // assert mbutton != null;
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mViewGroup.getVisibility() == View.VISIBLE) {
                    mButton.animate().rotation(360).start();
                    mViewGroup.setVisibility(View.GONE);

                } else {
                    mViewGroup.setVisibility(View.VISIBLE);
                    mButton.animate().rotation(180).start();
                }
            }
        });

      //  final ImageButton sortButton = (ImageButton) findViewById(R.id.arrow1);
        songButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (songViewGroup.getVisibility() == View.VISIBLE) {
                    songButton.animate().rotation(360).start();
                    songViewGroup.setVisibility(View.GONE);

                } else {
                    songViewGroup.setVisibility(View.VISIBLE);
                    songButton.animate().rotation(540).start();
                }
            }
        });
    }
      @Override
            public void onClick(View v) {
          int id = v.getId();
          switch (id) {
              case R.id.aggressive:
                  //your code here
                  TextView aggressive = (TextView) findViewById(R.id.aggressive);
                  moodList.setText(aggressive.getText().toString());
                  moodList.setTypeface(tf);
                  moodList.setTextColor(aggressive.getTextColors());
                  mButton.animate().rotation(360).start();
                  mViewGroup.setVisibility(View.GONE);
                  //Toast.makeText(getApplicationContext(), "Aggressive is clicked", Toast.LENGTH_LONG).show();
                  break;
              case R.id.bored:
                  //your code here
                  TextView bore = (TextView) findViewById(R.id.bored);
                  moodList.setText(bore.getText().toString());
                  moodList.setTypeface(tf);
                  moodList.setTextColor(bore.getTextColors());
                  mButton.animate().rotation(360).start();
                  mViewGroup.setVisibility(View.GONE);
                  //Toast.makeText(getApplicationContext(), "Bored is clicked", Toast.LENGTH_LONG).show();
                  break;
              case R.id.chilled:
                  //your code here
                  TextView chill = (TextView) findViewById(R.id.chilled);
                  moodList.setText(chill.getText().toString());
                  moodList.setTextColor(chill.getTextColors());
                  moodList.setTypeface(tf);
                  mButton.animate().rotation(360).start();
                  mViewGroup.setVisibility(View.GONE);
                  //Toast.makeText(getApplicationContext(), "Chilled is clicked", Toast.LENGTH_LONG).show();
                  break;
              case R.id.depressed:
                  //your code here
                  TextView depress = (TextView) findViewById(R.id.depressed);
                  moodList.setText(depress.getText().toString());
                  moodList.setTextColor(depress.getTextColors());
                  moodList.setTypeface(tf);
                  mButton.animate().rotation(360).start();
                  mViewGroup.setVisibility(View.GONE);
                  //Toast.makeText(getApplicationContext(), "Chilled is clicked", Toast.LENGTH_LONG).show();
                  break;
              case R.id.exited:
                  //your code here
                  TextView exited = (TextView) findViewById(R.id.exited);
                  moodList.setText(exited.getText().toString());
                  moodList.setTextColor(exited.getTextColors());
                  moodList.setTypeface(tf);
                  mButton.animate().rotation(360).start();
                  mViewGroup.setVisibility(View.GONE);
                  //Toast.makeText(getApplicationContext(), "Chilled is clicked", Toast.LENGTH_LONG).show();
                  break;
              case R.id.happy:
                  //your code here
                  TextView happy = (TextView) findViewById(R.id.happy);
                  moodList.setText(happy.getText().toString());
                  moodList.setTextColor(happy.getTextColors());
                  moodList.setTypeface(tf);
                  mButton.animate().rotation(360).start();
                  mViewGroup.setVisibility(View.GONE);
                  //Toast.makeText(getApplicationContext(), "Chilled is clicked", Toast.LENGTH_LONG).show();
                  break;
              case R.id.peacefull:
                  //your code here
                  TextView peace = (TextView) findViewById(R.id.peacefull);
                  moodList.setText(peace.getText().toString());
                  moodList.setTextColor(peace.getTextColors());
                  moodList.setTypeface(tf);
                  mButton.animate().rotation(360).start();
                  mViewGroup.setVisibility(View.GONE);
                  //Toast.makeText(getApplicationContext(), "Chilled is clicked", Toast.LENGTH_LONG).show();
                  break;
              case R.id.stressed:
                  //your code here
                  TextView stress = (TextView) findViewById(R.id.stressed);
                  moodList.setText(stress.getText().toString());
                  moodList.setTextColor(stress.getTextColors());
                  moodList.setTypeface(tf);
                  mButton.animate().rotation(360).start();
                  mViewGroup.setVisibility(View.GONE);
                  //Toast.makeText(getApplicationContext(), "Chilled is clicked", Toast.LENGTH_LONG).show();
                  break;
              case R.id.noMood:
                  //your code here
                  TextView noMood = (TextView) findViewById(R.id.noMood);
                  moodList.setText(noMood.getText().toString());
                  moodList.setTextColor(noMood.getTextColors());
                  moodList.setTypeface(tf);
                  mButton.animate().rotation(360).start();
                  mViewGroup.setVisibility(View.GONE);
                  //Toast.makeText(getApplicationContext(), "Chilled is clicked", Toast.LENGTH_LONG).show();
                  break;
              case R.id.allMood:
                  //your code here
                  TextView allMood = (TextView) findViewById(R.id.allMood);
                  moodList.setText(allMood.getText().toString());
                  moodList.setTextColor(allMood.getTextColors());
                  moodList.setTypeface(tf);
                  mButton.animate().rotation(360).start();
                  mViewGroup.setVisibility(View.GONE);
                  //Toast.makeText(getApplicationContext(), "Chilled is clicked", Toast.LENGTH_LONG).show();
                  break;
          }
      }
}
