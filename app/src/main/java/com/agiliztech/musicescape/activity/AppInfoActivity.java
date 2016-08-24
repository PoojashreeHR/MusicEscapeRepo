package com.agiliztech.musicescape.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.agiliztech.musicescape.R;
import com.agiliztech.musicescape.adapter.AppInfoAdapter;
import com.agiliztech.musicescape.models.AppInfo;

import java.util.ArrayList;
import java.util.List;

public class AppInfoActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    private List<AppInfo> itemList = new ArrayList<>();
    ImageView backButton;
    private AppInfoAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);

        recyclerView = (RecyclerView) findViewById(R.id.appInfoRecyclerView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Typeface tf = Typeface.createFromAsset(getAssets(),
                "fonts/MontserratRegular.ttf");
        TextView appInfo = (TextView) findViewById(R.id.appInfo);
        appInfo.setTypeface(tf);
        backButton = (ImageView) findViewById(R.id.backbutton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MoodMappingActivity.class);
                startActivity(intent);

            }
        });
        mAdapter = new AppInfoAdapter(itemList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View view, int position) {

                //  AppInfo movie = movieList.get(position);
                switch(position){
                    case 0:
                        recyclerView.setFocusableInTouchMode(false);
                        recyclerView.setFocusable(false);
                        startActivity(new Intent(AppInfoActivity.this, MoodMappingActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(AppInfoActivity.this, DashboardActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(AppInfoActivity.this, DashboardActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(AppInfoActivity.this, FaqActivity.class));
                        break;
                    case 4:
                        startActivity(new Intent(AppInfoActivity.this, DashboardActivity.class));
                        break;

                    default:
                        break;
                }
               // Toast.makeText(getApplicationContext(), movie.getTitle() + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        AppInfoItem();
    }

    private void AppInfoItem() {
        AppInfo item = new AppInfo("Young and Well CRC Website");
        itemList.add(item);
        item = new AppInfo("View Privacy Statement");
        itemList.add(item);
        item = new AppInfo("Tutorial");
        itemList.add(item);
        item = new AppInfo("FAQs");
        itemList.add(item);
        item = new AppInfo("Get Help");
        itemList.add(item);
        item = new AppInfo("Contact Us");
        itemList.add(item);

    }



    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private AppInfoActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final AppInfoActivity.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }


        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
                rv.setBackgroundColor(Color.WHITE);
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
}
