package com.mozan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import com.mozan.adapter.FullScreenImageAdapter;

import java.util.ArrayList;

/**
 * Created by nurzamat on 1/18/15.
 */
public class FullScreenViewActivity extends Activity {

    private FullScreenImageAdapter adapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_view);

        viewPager = (ViewPager) findViewById(R.id.pager);

        Intent i = getIntent();
        int position = i.getIntExtra("position", 0);
        String id = i.getStringExtra("id");
        ArrayList<String> image_urls = i.getStringArrayListExtra("image_urls");

        adapter = new FullScreenImageAdapter(FullScreenViewActivity.this, image_urls);

        viewPager.setAdapter(adapter);
        // displaying selected image first
        viewPager.setCurrentItem(position);
    }
}

