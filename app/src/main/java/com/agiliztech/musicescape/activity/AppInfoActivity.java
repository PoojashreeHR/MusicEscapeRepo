package com.agiliztech.musicescape.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.agiliztech.musicescape.R;
import com.agiliztech.musicescape.adapter.AppInfoAdapter;
import com.agiliztech.musicescape.models.AppInfo;
import com.agiliztech.musicescape.utils.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class AppInfoActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    private List<AppInfo> itemList = new ArrayList<>();
    ImageView backButton;
    RelativeLayout relative;
    private AppInfoAdapter mAdapter;
    Typeface tf;
    private int mSelectedPosition;
    private View mSelectedView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);

        recyclerView = (RecyclerView) findViewById(R.id.appInfoRecyclerView);
         tf = Typeface.createFromAsset(getAssets(),
                "fonts/MontserratRegular.ttf");
        TextView appInfo = (TextView) findViewById(R.id.appInfo);
        appInfo.setTypeface(tf);
        relative = (RelativeLayout) findViewById(R.id.relativeLayout2);
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
       /* RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        recyclerView.addItemDecoration(itemDecoration);*/
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
               // position = getLayoutPosition(); // gets item position
                AppInfo item = itemList.get(position);
                switch(position){
                    case 0:
                        view.setBackgroundColor(Color.WHITE);
                        String url = "http://www.youngandwellcrc.org.au";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                        break;
                    case 1:
                        Intent privacy = new Intent(getApplicationContext(),PrivacyActivity.class);
                        privacy.putExtra("appInfo","AppInfo");
                        startActivity(privacy);
                        break;
                    case 2:
                        Intent tutorial = new Intent(getApplicationContext(),SlidingImage.class);
                        tutorial.putExtra("fullSlide","FullImgSliding");
                        startActivity(tutorial);
                        break;
                    case 3:

                        Intent faq = new Intent(getApplicationContext(),FaqActivity.class);
                        faq.putExtra("URL", "file:///android_asset/faq.html");
                        faq.putExtra("name","FAQ");
                        startActivity(faq);
                        break;
                    case 4:
                        Intent gethelp = new Intent(getApplicationContext(),FaqActivity.class);
                        gethelp.putExtra("URL", "file:///android_asset/gethelp.html");
                        gethelp.putExtra("name","GET HELP");
                        startActivity(gethelp);
                        break;
                    case 5:
                        String[] TO = {"demo@gmail.com"};
                       // String[] Subject = {"eScape_Feedback"};
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SENDTO);
                        sendIntent.setData(Uri.parse("mailto:"));
                        sendIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "eScape Feedback");
                        startActivity(sendIntent);
                    default:
                        break;
                }
               // Toast.makeText(getApplicationContext(), listItem.getTitle() + " is selected!", Toast.LENGTH_SHORT).show();
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
