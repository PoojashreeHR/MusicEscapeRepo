package com.agiliztech.musicescape;

import android.app.Application;
import android.content.SharedPreferences;

import com.agiliztech.musicescape.utils.FontUtils;

/**
 * Created by praburaam on 11/08/16.
 */
public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        setupFonts();
    }

    private void setupFonts() {
        FontUtils.setDefaultFont(this, "DEFAULT", "fonts/MontserratBold.ttf");
        FontUtils.setDefaultFont(this, "SERIF", "fonts/MontserratRegular.ttf");
        FontUtils.setDefaultFont(this, "SANS_SERIF", "fonts/ProximaNovaBlack.ttf");
        FontUtils.setDefaultFont(this, "MONOSPACE", "fonts/ProximaNovaCond.ttf");
    }
}
