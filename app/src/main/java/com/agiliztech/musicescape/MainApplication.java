package com.agiliztech.musicescape;

import android.app.Application;

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
        FontUtils.setDefaultFont(this, "DEFAULT", "fonts/montserratBold.ttf");
        FontUtils.setDefaultFont(this, "SERIF", "fonts/montserratRegular.ttf");
        FontUtils.setDefaultFont(this, "SANS_SERIF", "fonts/proximaNovaBlack.ttf");
        FontUtils.setDefaultFont(this, "MONOSPACE", "fonts/proximaNovaCond.ttf");
    }
}
