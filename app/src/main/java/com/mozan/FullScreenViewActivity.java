package com.mozan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import com.mozan.adapter.FullScreenImageAdapter;
import com.mozan.lib.CirclePageIndicator;

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

        adapter = new FullScreenImageAdapter(FullScreenViewActivity.this);

        viewPager.setAdapter(adapter);
    }
}

