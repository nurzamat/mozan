package com.mozan.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.mozan.R;
import com.mozan.util.CustomNetworkImageView;
import com.mozan.util.GlobalVar;

public class PlaceSlidesFragmentAdapter extends PagerAdapter {
    // Declare Variables
    Context context;
    LayoutInflater inflater;
    int size;

    public PlaceSlidesFragmentAdapter(Context context) {
        this.context = context;
        this.size = GlobalVar._bitmaps.size();
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

        View itemView;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(GlobalVar.Mode)
        {
            // Add mode
            itemView = inflater.inflate(R.layout.viewpager_item, container, false);
            // Locate the ImageView in viewpager_item.xml
            ImageView imgflag = (ImageView) itemView.findViewById(R.id.flag);
            imgflag.setScaleType(ImageView.ScaleType.CENTER_CROP);
            // Capture position and set to the ImageView
            try
            {
                if(size > 0)
                    imgflag.setImageBitmap(GlobalVar._bitmaps.get(position));
                else {
                    imgflag.setImageResource(R.drawable.default_img);
                }
            }
            catch (IndexOutOfBoundsException ex)
            {
                Log.d("PlaceSlidesFragmentAdapter exeption:", ex.getMessage());
            }
        }
        else
        {   // Edit mode
            itemView = inflater.inflate(R.layout.viewpager_item_edit, container, false);
            // Locate the ImageView in viewpager_item.xml
            CustomNetworkImageView imgflag = (CustomNetworkImageView) itemView.findViewById(R.id.flag);
            imgflag.setScaleType(ImageView.ScaleType.CENTER_CROP);
            // Capture position and set to the ImageView
            try
            {
                if(size > 0)
                {
                    imgflag.setLocalImageBitmap(GlobalVar._bitmaps.get(position));
                    Toast.makeText(context, "size = "+size, Toast.LENGTH_LONG).show();
                }
                else
                {
                    //Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_img);

                    Toast.makeText(context, "size = "+size, Toast.LENGTH_LONG).show();

                    Drawable drawable = context.getResources().getDrawable(R.drawable.default_img);
                    Bitmap bmp = ((BitmapDrawable)drawable).getBitmap();

                    imgflag.setLocalImageBitmap(bmp);
                }
            }
            catch (IndexOutOfBoundsException ex)
            {
                Log.d("PlaceSlidesFragmentAdapter exeption:", ex.getMessage());
            }
        }
        // Add viewpager_item.xml to ViewPager
        ((ViewPager) container).addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        ((ViewPager) container).removeView((RelativeLayout) object);

    }
}