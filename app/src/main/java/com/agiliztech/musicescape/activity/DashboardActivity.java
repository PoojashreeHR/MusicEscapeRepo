package com.agiliztech.musicescape.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.agiliztech.musicescape.R;


import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private RecyclerView recycler_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
//
//        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
//        recycler_view.setLayoutManager(new LinearLayoutManager(this));
//        recycler_view.setAdapter(new CircularAdapter());



    }


}
