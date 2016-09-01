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

public class SelectingMoodActivity extends AppCompatActivity implements View.OnClickListener {
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
                Intent intent = new Intent(SelectingMoodActivity.this, DrawingViewActivity.class);
                startActivity(intent);
                finish();
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
        selectingMood.setText("What is your Target mood");
        Toast.makeText(getApplicationContext(),"You clicked a Text",Toast.LENGTH_LONG).show();
    }
}
