package com.mozan.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.mozan.R;
import com.mozan.util.GlobalVar;

import java.util.ArrayList;

public class PlaceSlidesFragmentAdapter extends PagerAdapter {
    // Declare Variables
    Context context;
    LayoutInflater inflater;
    int size;
    ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();

    public PlaceSlidesFragmentAdapter(Context context) {
        bitmaps.clear();
        bitmaps.addAll(GlobalVar._postBitmaps);
        bitmaps.addAll(GlobalVar._bitmaps);
        this.context = context;
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
                else
                {
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
            ImageView imgflag = (ImageView) itemView.findViewById(R.id.flag);
            imgflag.setScaleType(ImageView.ScaleType.CENTER_CROP);
            // Capture position and set to the ImageView

            //imgflag.setDefaultImageResId(R.drawable.default_img);

            imgflag.setImageBitmap(bitmaps.get(position));
            imgflag.setImageBitmap(bitmaps.get(position));
            /*
            itemView = inflater.inflate(R.layout.viewpager_item_edit, container, false);
            // Locate the ImageView in viewpager_item.xml
            CustomNetworkImageView imgflag = (CustomNetworkImageView) itemView.findViewById(R.id.flag);
            imgflag.setScaleType(ImageView.ScaleType.CENTER_CROP);
            // Capture position and set to the ImageView

            if (imageLoader == null)
                imageLoader = AppController.getInstance().getImageLoader();

            //imgflag.setDefaultImageResId(R.drawable.default_img);
            if(position < url_size)
            {
                //imgflag.setImageUrl(urls.get(position), imageLoader);
                imageLoader.get(urls.get(position), new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        bitmap = response.getBitmap();
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("PlaceSlidesFragmentAdapter", "Error: " + error.getMessage());
                    }
                });
                Log.d("<:", ""+position);
            }
            else
            {
                //imgflag.setLocalImageBitmap(GlobalVar._bitmaps.get(position));
                //imgflag.setLocalImageBitmap(GlobalVar._bitmaps.get(position - url_size));
                bitmap = GlobalVar._bitmaps.get(position - url_size);
                Log.d("=:", ""+position);
            }

            imgflag.setLocalImageBitmap(bitmap);

            */
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