package com.agiliztech.musicescape.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.agiliztech.musicescape.R;
import com.agiliztech.musicescape.journey.SongMoodCategory;
import com.agiliztech.musicescape.utils.SongsManager;

public class SelectingMoodActivity extends AppCompatActivity implements View.OnClickListener {

    boolean firstMoodSelected, secondMoodSelected;
    SongMoodCategory firstMood, secondMood;

    TextView selectingMood;
    String result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selecting_mood);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.selection_layout);
        overrideFonts(getApplicationContext(),linearLayout);

        selectingMood = (TextView) findViewById(R.id.moodSelection);
        ImageButton nextMood = (ImageButton) findViewById(R.id.nextMood);
        nextMood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void overrideFonts(final Context context, final View v) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    overrideFonts(context, child);
                }
            } else if (v instanceof TextView ) {
                ((TextView) v).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/MontserratRegular.ttf"));
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onClick(View v) {

        TextView tv = (TextView) v;
        if(!firstMoodSelected){
            selectingMood.setText("What is your Target mood");
            firstMoodSelected =  true;
            firstMood = SongsManager.getMoodForText(tv.getText().toString());
        }
        else if(!secondMoodSelected){
            secondMoodSelected = true;
            secondMood = SongsManager.getMoodForText(tv.getText().toString());


            Intent intent = new Intent(SelectingMoodActivity.this, DrawingViewActivity.class);
            intent.putExtra("current",SongsManager.getIntValue(firstMood));
            intent.putExtra("target",SongsManager.getIntValue(secondMood));
            startActivity(intent);
            finish();
        }
        //Toast.makeText(getApplicationContext(),"You clicked a Text",Toast.LENGTH_LONG).show();
    }
}
