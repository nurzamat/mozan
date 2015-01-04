package com.mozan.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.mozan.R;

/*
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
*/

public class PlaceSlidesFragmentAdapter extends PagerAdapter {
    // Declare Variables
    Context context;
    int[] flag;
    LayoutInflater inflater;
    Bitmap bitmap;

    public PlaceSlidesFragmentAdapter(Context context, int[] flag, Bitmap bitmap) {
        this.context = context;
        this.flag = flag;
        this.bitmap = bitmap;
    }

    @Override
    public int getCount() {
        return flag.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        ImageView imgflag;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.viewpager_item, container,
                false);

        // Locate the ImageView in viewpager_item.xml
        imgflag = (ImageView) itemView.findViewById(R.id.flag);
        // Capture position and set to the ImageView

        imgflag.setImageResource(flag[position]);

        if(bitmap != null)
        {
            imgflag.setImageBitmap(bitmap);
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