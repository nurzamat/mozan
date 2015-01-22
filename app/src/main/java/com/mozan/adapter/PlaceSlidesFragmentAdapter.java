package com.mozan.adapter;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.mozan.HomeActivity;
import com.mozan.R;
import com.mozan.util.GlobalVar;

import java.util.ArrayList;

public class PlaceSlidesFragmentAdapter extends PagerAdapter {
    // Declare Variables
    Context context;
    LayoutInflater inflater;
    ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
    int size;
    public ViewPager collection;

    public PlaceSlidesFragmentAdapter(Context context, ArrayList<Bitmap> _bitmaps) {

        //bitmaps.clear();
        //bitmaps.addAll(GlobalVar._postBitmaps);
        //bitmaps.addAll(GlobalVar._bitmaps);
        this.context = context;
        this.bitmaps = _bitmaps;
        this.size = bitmaps.size();
    }

    @Override
    public int getCount()
    {
        if(size > 0)
        return size;
        else return 1;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        this.collection = (ViewPager)container;
        View itemView;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        itemView = inflater.inflate(R.layout.viewpager_item, container, false);
        // Locate the ImageView in viewpager_item.xml
        ImageView imgflag = (ImageView) itemView.findViewById(R.id.flag);
        imgflag.setScaleType(ImageView.ScaleType.CENTER_CROP);
        // Capture position and set to the ImageView
        try
        {
            if(size > 0)
            {
                imgflag.setImageBitmap(bitmaps.get(position));
            }
            else
            {
                imgflag.setImageResource(R.drawable.default_img);
            }
        }
        catch (IndexOutOfBoundsException ex)
        {
            Log.d("PlaceSlidesFragmentAdapter exeption:", ex.getMessage());
        }

        // Add viewpager_item.xml to ViewPager
        collection.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
       // ((ViewPager) container).removeView((RelativeLayout) object);
        collection.removeViewAt(position);
    }

    @Override
    public int getItemPosition(Object object){
        return PagerAdapter.POSITION_NONE;
    }
}