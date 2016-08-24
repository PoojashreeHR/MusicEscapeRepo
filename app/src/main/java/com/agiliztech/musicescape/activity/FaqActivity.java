package com.agiliztech.musicescape.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.agiliztech.musicescape.R;

public class FaqActivity extends AppCompatActivity {

    WebView mWebView;
    ImageView backButton;
    Typeface tf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView textView = (TextView) findViewById(R.id.listInfo);
        tf = Typeface.createFromAsset(getAssets(),
                "fonts/MontserratRegular.ttf");
        String name =  getIntent().getStringExtra("name");
        textView.setText(name);
        textView.setTypeface(tf);
        mWebView = (WebView) findViewById(R.id.webView);
        String url = getIntent().getStringExtra("URL");
        mWebView.loadUrl(url);

        backButton = (ImageView) findViewById(R.id.backbutton1);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AppInfoActivity.class);
                startActivity(intent);

            }
        });
    }
}