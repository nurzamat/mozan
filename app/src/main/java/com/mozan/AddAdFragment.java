package com.mozan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;
import com.mozan.adapter.PlaceSlidesFragmentAdapter;
import com.mozan.lib.CirclePageIndicator;
import com.mozan.util.GlobalVar;

public class AddAdFragment extends Fragment {

    // Declare Variables
    ViewPager mPager;
    PagerAdapter mAdapter;
    View rootView;
    CirclePageIndicator mIndicator;
    int id_resource = 0;
    String paths = "";

    public AddAdFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle obj = getArguments();
        if(obj != null)
        {
           this.id_resource =  obj.getInt("id_resource");
           this.paths = obj.getString("paths");
        }
        rootView = inflater.inflate(R.layout.fragment_add_ad, container, false);

        mAdapter = new PlaceSlidesFragmentAdapter(getActivity(), paths.split("|"));

        mPager = (ViewPager) rootView.findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        mIndicator = (CirclePageIndicator) rootView.findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
        mIndicator.setSnap(true);
        /*
        mIndicator
                .setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        Toast.makeText(AddAdFragment.this.getActivity(),
                                "Changed to page " + position,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPageScrolled(int position,
                                               float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                });

        */
        cameraButton();

        return rootView;
    }

    private void cameraButton() {
        // TODO Auto-generated method stub
        ImageButton btn = (ImageButton) rootView.findViewById(R.id.btnCamera);

        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent in = new Intent(getActivity(), CustomGalleryActivity.class);
                startActivity(in);
            }
        });
    }
}