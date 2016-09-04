package com.agiliztech.musicescape.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.agiliztech.musicescape.R;
import com.agiliztech.musicescape.utils.Global;

import java.util.Timer;
import java.util.TimerTask;

public class MainSplashScreen extends Activity {
    private static final int MY_PERMISSIONS_REQUEST = 1234;
    TextView mTextView;

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    proceedWithAppLaunch();

                } else {

                    // pe   rmission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

        }
    }

    private void proceedWithAppLaunch() {

        Typeface tf = Typeface.createFromAsset(getAssets(),
                "fonts/MontserratRegular.ttf");
        TextView content = (TextView) findViewById(R.id.textView13);
        content.setTypeface(tf);
        TextView contents = (TextView) findViewById(R.id.textView12);
        contents.setTypeface(tf);
        SpannableStringBuilder builder = new SpannableStringBuilder();

        mTextView = (TextView) findViewById(R.id.textView11);
        String red = "music\t";
        SpannableString redSpannable= new SpannableString(red);
        redSpannable.setSpan(new ForegroundColorSpan(Color.parseColor("#9D9D9D")), 0, red.length(), 0);
        builder.append(redSpannable);

        String white = "e";
        SpannableString whiteSpannable= new SpannableString(white);
        whiteSpannable.setSpan(new ForegroundColorSpan(Color.WHITE), 0, white.length(), 0);
        builder.append(whiteSpannable);

        String blue = "Scape";
        SpannableString blueSpannable = new SpannableString(blue);
        blueSpannable.setSpan(new ForegroundColorSpan(Color.parseColor("#9D9D9D")), 0, blue.length(), 0);
        builder.append(blueSpannable);

        mTextView.setText(builder, TextView.BufferType.SPANNABLE);

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final boolean introScreensShown = sharedPreferences.getBoolean(Global.INTROSCREENSSHOWN, false);
        final boolean scannedOnce = sharedPreferences.getBoolean(Global.isScannedOnce, false);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if(!introScreensShown){
                    SharedPreferences.Editor edit = sharedPreferences.edit();
                    edit.putBoolean(Global.INTROSCREENSSHOWN, true);
                    edit.commit();
                    startActivity(new Intent(MainSplashScreen.this, SplashScreen.class));
                    finish();
                }
                else {
                    if(scannedOnce) {
                        startActivity(new Intent(MainSplashScreen.this, NewDashboardActivity.class));
                    }
                    else{
                        startActivity(new Intent(MainSplashScreen.this, MoodMappingActivity.class));
                    }
                    finish();
                }
            }
        },3000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_splash_screen);

        String[] allPermissions = new String[2];
        int i = 0;

        int storagePermissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(storagePermissionCheck != PackageManager.PERMISSION_GRANTED){
            allPermissions[i] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
            i++;
        }

        int phonePermissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE);
        if(phonePermissionCheck != PackageManager.PERMISSION_GRANTED){
            allPermissions[i] = Manifest.permission.READ_PHONE_STATE;
            i++;
        }


        String[] allPerms = new String[i];
        for(int k=0;k<i;k++){
            allPerms[k] = allPermissions[k];
        }

        if(i>0){
            if(Build.VERSION.SDK_INT >= 23) {
                requestPermissions(
                        allPerms,
                        MY_PERMISSIONS_REQUEST);
            }
            else{
                proceedWithAppLaunch();
            }
        }
        else{
            proceedWithAppLaunch();
        }


    }
}
