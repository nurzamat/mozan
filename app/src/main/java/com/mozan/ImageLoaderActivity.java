package com.mozan;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.mozan.R;
import com.mozan.util.ApiHelper;
import com.mozan.util.GlobalVar;

import java.util.ArrayList;

public class ImageLoaderActivity extends Activity {

    private static final String TAG =  "[ImageLoaderActivity]";
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    Bitmap bm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(imageLoader ==  null)
            imageLoader = AppController.getInstance().getImageLoader();

        ArrayList<String> urls = ApiHelper.getImageUrls(GlobalVar._Post.getImages());

        for (int i = 0; i < urls.size(); i++)
        {
            imageLoader.get(urls.get(i), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    bm = response.getBitmap();
                    if(bm != null)
                    {
                        GlobalVar._postBitmaps.add(bm);
                        Log.d(TAG, "bitmap: " + "ok");
                        Log.d(TAG, "_postBitmaps size: " + GlobalVar._postBitmaps.size());
                    }
                    else Log.d(TAG, "bitmap: " + "null");
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                }
            });
        }

    }
}
