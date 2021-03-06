package com.agiliztech.musicescape.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.agiliztech.musicescape.R;
import com.agiliztech.musicescape.activity.AppInfoActivity;
import com.agiliztech.musicescape.activity.SlidingImage;
import com.agiliztech.musicescape.models.AppInfo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Pooja on 24-08-2016.
 */
public class SlidingImage_Adapter extends PagerAdapter {

    private final int targetWidth, targetHeight;
    private ArrayList<Integer> IMAGES;
    private LayoutInflater inflater;
    private Context context;
    ImageView imageView;

    public SlidingImage_Adapter(Context context, ArrayList<Integer> IMAGES) {
        this.context = context;
        this.IMAGES = IMAGES;
        inflater = LayoutInflater.from(context);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        targetWidth = displayMetrics.widthPixels;
        targetHeight = displayMetrics.heightPixels;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return IMAGES.size()+1;
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.sliding_image_layout, view, false);

        assert imageLayout != null;
        imageView = (ImageView) imageLayout
                .findViewById(R.id.image);

        if(position < getCount()-1)
        {
            Picasso.with(context)
                    .load(IMAGES.get(position)).into(imageView);
                  //  .resize(400,400)
                 // .centerInside()

            //imageView.setImageBitmap(decodeResource(context.getResources(), IMAGES.get(position)));
            view.addView(imageLayout, 0);
        }
       // imageView.setImageBitmap(decodeResource(context.getResources(), IMAGES.get(position)));
      //  view.addView(imageLayout, 0);

        return imageLayout;
}
    private static Bitmap decodeResource(Resources res, int id) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        for (options.inSampleSize = 1; options.inSampleSize <= 32; options.inSampleSize++) {
            try {
                bitmap = BitmapFactory.decodeResource(res, id, options);
                Log.d("", "Decoded successfully for sampleSize " + options.inSampleSize);
                break;
            } catch (OutOfMemoryError outOfMemoryError) {
                // If an OutOfMemoryError occurred, we continue with for loop and next inSampleSize value
                Log.e("", "outOfMemoryError while reading file for sampleSize " + options.inSampleSize
                        + " retrying with higher value");
            }
        }
        return bitmap;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }



}
