package com.agiliztech.musicescape.view;

import android.graphics.drawable.GradientDrawable;

/**
 * Created by Asif on 31-08-2016.
 */
public class CustomDrawableForSeekBar extends GradientDrawable {

    public CustomDrawableForSeekBar(int pStartColor, int pCenterColor,
                                    int pEndColor, int pStrokeWidth, int pStrokeColor,
                                    float cornerRadius) {
        super(Orientation.BOTTOM_TOP, new int[]{pStartColor, pCenterColor, pEndColor});
        setStroke(pStrokeWidth, pStrokeColor);
        setShape(GradientDrawable.LINE);
        setCornerRadius(cornerRadius);
    }

}
