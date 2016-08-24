package com.agiliztech.musicescape.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.agiliztech.musicescape.R;
import com.agiliztech.musicescape.activity.AppInfoActivity;
import com.agiliztech.musicescape.activity.SlidingImage;
import com.agiliztech.musicescape.models.AppInfo;

import java.util.ArrayList;

/**
 * Created by Pooja on 24-08-2016.
 */
public class SlidingImage_Adapter extends PagerAdapter {

    private ArrayList<Integer> IMAGES;
    private LayoutInflater inflater;
    private Context context;


    public SlidingImage_Adapter(Context context, ArrayList<Integer> IMAGES) {
        this.context = context;
        this.IMAGES = IMAGES;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return IMAGES.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.sliding_image_layout, view, false);


        assert imageLayout != null;
        final ImageView imageView = (ImageView) imageLayout
                .findViewById(R.id.image);
        imageView.setImageResource(IMAGES.get(position));
        view.addView(imageLayout, 0);

        /*if(position == IMAGES.size())
        {
            Intent intent = new Intent(context,AppInfoActivity.class);
            context.startActivity(intent);
        }*/
        return imageLayout;
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
