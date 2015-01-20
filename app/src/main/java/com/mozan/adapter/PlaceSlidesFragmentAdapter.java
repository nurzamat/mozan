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

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.mozan.AppController;
import com.mozan.R;
import com.mozan.model.Image;
import com.mozan.util.CustomNetworkImageView;
import com.mozan.util.GlobalVar;

import java.util.ArrayList;

public class PlaceSlidesFragmentAdapter extends PagerAdapter {
    // Declare Variables
    Context context;
    LayoutInflater inflater;
    int size;
    int url_size;
    ArrayList<Image> images = null;
    ArrayList<String> urls = null;
    ImageLoader imageLoader = null;

    public PlaceSlidesFragmentAdapter(Context context) {
        this.context = context;
        this.size = GlobalVar._bitmaps.size();
        if(GlobalVar._Post != null)
        {
            this.images = GlobalVar._Post.getImages();
            this.urls = new ArrayList<String>();
            for (int i = 0; i < images.size(); i++)
            {
                urls.add(images.get(i).getUrl());
            }
            this.url_size = urls.size();
            this.size = size + url_size;
            this.imageLoader = AppController.getInstance().getImageLoader();
        }

        Log.d("PlaceSlidesFragmentAdapter size:", " "+size);
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
                    //imgflag.setDefaultImageResId(R.drawable.default_img);
                    if(position < url_size) {
                        imgflag.setImageUrl(urls.get(position), imageLoader);
                    }
                    else
                    {
                        imgflag.mShowLocal = true;
                        imgflag.setLocalImageBitmap(GlobalVar._bitmaps.get(position - url_size));
                    }
                }
                else
                {
                    //default image
                    //Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_img);
                    //Drawable drawable = context.getResources().getDrawable(R.drawable.default_img);
                    //Bitmap bmp = ((BitmapDrawable)drawable).getBitmap();
                    //imgflag.setLocalImageBitmap(bmp);
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