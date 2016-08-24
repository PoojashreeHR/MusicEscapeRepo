package com.agiliztech.musicescape.activity;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.agiliztech.musicescape.R;
import com.agiliztech.musicescape.view.PieChart;

public class DashboardActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Resources res = getResources();
        final PieChart pie = (PieChart) this.findViewById(R.id.Pie);
//        pie.addItem("Agamemnon", 1, res.getColor(R.color.seafoam));
//        pie.addItem("Bocephus", 1, res.getColor(R.color.chartreuse));
//        pie.addItem("Calliope", 1, res.getColor(R.color.emerald));
//        pie.addItem("Daedalus", 1, res.getColor(R.color.bluegrass));
//        pie.addItem("Euripides", 1, res.getColor(R.color.turquoise));
//        pie.addItem("Ganymede", 1, res.getColor(R.color.slate));
//
//        pie.addItem("Calliope", 1, res.getColor(R.color.emerald));
//        pie.addItem("Daedalus", 1, res.getColor(R.color.bluegrass));
//        pie.addItem("Euripides", 1, res.getColor(R.color.turquoise));
//        pie.addItem("Ganymede", 1, res.getColor(R.color.slate));

        pie.addItem("Agamemnon", 1, res.getColor(R.color.black));
        pie.addItem("Bocephus", 1, res.getColor(R.color.black));
        pie.addItem("Calliope", 1, res.getColor(R.color.black));
        pie.addItem("Daedalus", 1, res.getColor(R.color.black));
        pie.addItem("Euripides", 1, res.getColor(R.color.black));
        pie.addItem("Ganymede", 1, res.getColor(R.color.black));

        pie.addItem("Calliope", 1, res.getColor(R.color.black));
        pie.addItem("Daedalus", 1, res.getColor(R.color.black));
        pie.addItem("Euripides", 1, res.getColor(R.color.black));
        pie.addItem("Ganymede", 1, res.getColor(R.color.black));

    }


}
