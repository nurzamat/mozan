package com.mozan.adapter;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mozan.PlaceSlideFragment;
import com.mozan.R;
import com.mozan.lib.IconPagerAdapter;

/**
 * Created by nurzamat on 1/3/15.
 */
public class PlaceSlidesFragmentAdapter extends FragmentPagerAdapter implements
        IconPagerAdapter {

    private int[] Images = new int[] { R.drawable.rent, R.drawable.house_holder,
            R.drawable.realty, R.drawable.car

    };

    protected static final int[] ICONS = new int[] { R.drawable.add_adv,
            R.drawable.add_adv, R.drawable.add_adv, R.drawable.add_adv };

    private int mCount = Images.length;

    public PlaceSlidesFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        Bundle bundle= new Bundle();
        bundle.putInt("i", Images[position]);
        //set Fragmentclass Arguments
        PlaceSlideFragment fragobj = new PlaceSlideFragment();
        fragobj.setArguments(bundle);
        return fragobj;
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public int getIconResId(int index) {
        return ICONS[index % ICONS.length];
    }

    public void setCount(int count) {
        if (count > 0 && count <= 10) {
            mCount = count;
            notifyDataSetChanged();
        }
    }
}
