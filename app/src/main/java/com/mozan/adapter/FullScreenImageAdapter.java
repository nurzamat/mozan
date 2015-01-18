package com.mozan.adapter;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.mozan.AppController;
import com.mozan.R;

/**
 * Created by nurzamat on 1/18/15.
 */
public class FullScreenImageAdapter extends PagerAdapter {

    private Activity _activity;
    private ArrayList<String> _imagePaths;
    private LayoutInflater inflater;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    // constructor
    public FullScreenImageAdapter(Activity activity,
                                  ArrayList<String> imagePaths) {
        this._activity = activity;
        this._imagePaths = imagePaths;
    }

    @Override
    public int getCount() {
        return this._imagePaths.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        NetworkImageView imgDisplay;
        ImageButton btnClose;
        TextView count;

        inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.layout_fullscreen_image, container,
                false);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        imgDisplay = (NetworkImageView) viewLayout.findViewById(R.id.imgDisplay);
        count = (TextView) viewLayout.findViewById(R.id.text_indicator);
        btnClose = (ImageButton) viewLayout.findViewById(R.id.btnClose);

        imgDisplay.setDefaultImageResId(R.drawable.default_img);
        imgDisplay.setImageUrl(_imagePaths.get(position),imageLoader);

        //indicator text
        int start = position + 1;
        count.setText(start + " из " + _imagePaths.size());
        // close button click event
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _activity.finish();
            }
        });

        ((ViewPager) container).addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }
}
