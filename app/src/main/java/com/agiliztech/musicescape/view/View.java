package com.agiliztech.musicescape.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceView;

/**
 * Created by Pooja on 12-08-2016.
 */
public class View extends SurfaceView
{
        Paint paint = new Paint();

        public View(Context context) {
            super(context);
        }

        @Override
        public void onDraw(Canvas canvas) {
            paint.setColor(Color.GREEN);
            // set your own position and radius
            canvas.drawCircle(100,200,100,paint);
        }
    }
