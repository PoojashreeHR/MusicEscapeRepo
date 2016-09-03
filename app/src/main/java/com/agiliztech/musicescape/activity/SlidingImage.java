package com.agiliztech.musicescape.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.agiliztech.musicescape.R;
import com.agiliztech.musicescape.adapter.SlidingImage_Adapter;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class SlidingImage extends AppCompatActivity {
    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 8;
    private static final Integer[] IMAGES = {
            R.drawable.tutorial_img_1,
            R.drawable.tutorial_img,
            R.drawable.tutorial_img_3,
            R.drawable.tutorial_img_4,
            R.drawable.tutorial_img_5,
            R.drawable.tutorial_img_6,
            R.drawable.tutorial_img_7,
            R.drawable.tutorial_img_8};
    private ArrayList<Integer> ImagesArray = new ArrayList<Integer>();
    String appInfo, splashScreen, dashboard, library, history, draw, playlist;
    String isClicked = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sliding_image);
        mPager = (ViewPager) findViewById(R.id.pager);

        appInfo = getIntent().getStringExtra("fullSlide");
        splashScreen = getIntent().getStringExtra("moodMapping");
        dashboard = getIntent().getStringExtra("dashboard");
        library = getIntent().getStringExtra("library");
        history = getIntent().getStringExtra("history");
        draw = getIntent().getStringExtra("draw");
        playlist = getIntent().getStringExtra("playlist");

        if(appInfo == null && splashScreen == null
                &&dashboard == null && library == null
                && history == null && draw == null
                && playlist == null
                ) {
            for (int i = 0; i < IMAGES.length; i++)
                ImagesArray.add(IMAGES[i]);
            mPager.setAdapter(new SlidingImage_Adapter(SlidingImage.this, ImagesArray));
            NUM_PAGES = IMAGES.length;
        }
        else{
            init();
        }
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // TODO Auto-generated method stub
                currentPage = position;
                if (appInfo == null) {
                    if (position == mPager.getAdapter().getCount() - 1) {
                        Intent reg = new
                                Intent(SlidingImage.this, MoodMappingActivity.class);
                        startActivity(reg);
                        finish();
                        //start next Activity
                    }
                }
                else {

                        if (position == mPager.getAdapter().getCount() - 1) {
                            Intent reg = new
                                    Intent(SlidingImage.this, AppInfoActivity.class);
                            //startActivity(reg);
                            finish();
                            //start next Activity
                        }

                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }
        });
    }

    private void init() {

        if (appInfo != null) {
            for (int i = 0; i < IMAGES.length; i++)
                ImagesArray.add(IMAGES[i]);
            mPager.setAdapter(new SlidingImage_Adapter(SlidingImage.this, ImagesArray));
            NUM_PAGES = IMAGES.length;
        } else if (splashScreen!= null) {
            ImagesArray.add(R.drawable.tutorial_img_1);
            mPager.setAdapter(new SlidingImage_Adapter(SlidingImage.this, ImagesArray));
            new Timer().schedule(new TimerTask() {
                public void run() {
                        startActivity(new Intent(SlidingImage.this, MoodMappingActivity.class));
                        finish();
                    }
            }, 3000);
        } else if (library!= null) {
            ImagesArray.add(R.drawable.tutorial_img);
            ImagesArray.add(R.drawable.tutorial_img_3);
            mPager.setAdapter(new SlidingImage_Adapter(SlidingImage.this, ImagesArray));
            new Timer().schedule(new TimerTask() {
                public void run() {
                    startActivity(new Intent(SlidingImage.this, LibraryActivity.class));
                    finish();
                }
            }, 3000);
        } else if (dashboard!= null) {
            ImagesArray.add(R.drawable.tutorial_img_4);
            mPager.setAdapter(new SlidingImage_Adapter(SlidingImage.this, ImagesArray));
            new Timer().schedule(new TimerTask() {
                public void run() {
                    startActivity(new Intent(SlidingImage.this, DashboardActivity.class));
                    finish();
                }
            }, 3000);
        } else if (history!= null) {
            ImagesArray.add(R.drawable.tutorial_img_5);
            mPager.setAdapter(new SlidingImage_Adapter(SlidingImage.this, ImagesArray));
            new Timer().schedule(new TimerTask() {
                public void run() {
                    startActivity(new Intent(SlidingImage.this, HistoryActivity.class));
                    finish();
                }
            }, 3000);
        } else if(draw!= null){
//            startActivity(new Intent(SlidingImage.this, SelectingMoodActivity.class));
//            finish();
            ImagesArray.add(R.drawable.tutorial_img_6);
            ImagesArray.add(R.drawable.tutorial_img_7);
            mPager.setAdapter(new SlidingImage_Adapter(SlidingImage.this, ImagesArray));
            new Timer().schedule(new TimerTask() {
                public void run() {
                    startActivity(new Intent(SlidingImage.this, SelectingMoodActivity.class));
                    finish();
                }
            }, 3000);
        }

    }



}
