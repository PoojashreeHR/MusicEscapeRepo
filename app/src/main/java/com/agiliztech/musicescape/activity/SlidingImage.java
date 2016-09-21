package com.agiliztech.musicescape.activity;

import android.content.Intent;
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
            R.drawable.coach_screen_img_01,
            R.drawable.coach_screen_img_02,
            R.drawable.coach_screen_img_03,
            R.drawable.coach_screen_img_04,
            R.drawable.coach_screen_img_05,
            R.drawable.coach_screen_img_06,
            R.drawable.coach_screen_img_07,
            R.drawable.coach_screen_img_08};
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
            mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    if (position == mPager.getAdapter().getCount() - 1) {
                        Intent reg = new
                                Intent(SlidingImage.this, MoodMappingActivity.class);
                        startActivity(reg);
                        finish();
                        //start next Activity
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
        else{
            init();
        }

    }

    private void init() {
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
        if (appInfo != null) {
            for (int i = 0; i < IMAGES.length; i++)
                ImagesArray.add(IMAGES[i]);
            mPager.setAdapter(new SlidingImage_Adapter(SlidingImage.this, ImagesArray));
            NUM_PAGES = IMAGES.length;
        } else if (splashScreen!= null) {
            ImagesArray.add(R.drawable.coach_screen_img_01);
            mPager.setAdapter(new SlidingImage_Adapter(SlidingImage.this, ImagesArray));
            new Timer().schedule(new TimerTask() {
                public void run() {
                        startActivity(new Intent(SlidingImage.this, MoodMappingActivity.class));
                        finish();
                    }
            }, 3000);
        } else if (library!= null) {
            ImagesArray.add(R.drawable.coach_screen_img_02);
            //ImagesArray.add(R.drawable.coa);
            mPager.setAdapter(new SlidingImage_Adapter(SlidingImage.this, ImagesArray));
            new Timer().schedule(new TimerTask() {
                public void run() {
                    startActivity(new Intent(SlidingImage.this, LibraryActivity.class));
                    finish();
                }
            }, 3000);
        } else if (dashboard!= null) {
            ImagesArray.add(R.drawable.coach_screen_img_05);
            mPager.setAdapter(new SlidingImage_Adapter(SlidingImage.this, ImagesArray));
            new Timer().schedule(new TimerTask() {
                public void run() {
                    startActivity(new Intent(SlidingImage.this, NewDashboardActivity.class));
                    finish();
                }
            }, 3000);
        } else if (history!= null) {
            ImagesArray.add(R.drawable.coach_screen_img_06);
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
            ImagesArray.add(R.drawable.coach_screen_img_07);
            ImagesArray.add(R.drawable.coach_screen_img_08);
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
