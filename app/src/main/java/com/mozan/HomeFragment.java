package com.mozan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;

import com.mozan.adapter.HomeGridviewAdapter;
import com.mozan.util.ApiHelper;
import com.mozan.util.GlobalVar;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomeGridviewAdapter mAdapter;
    private ArrayList<String> listCategory;
    private ArrayList<Integer> listCategoryImage;
    Fragment fragment = null;
    private View rootView;
    private GridView gridView;
    public static int height;
    public static final int const_height = 165;

	public HomeFragment()
    {

    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        prepareList();
       /*
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        this.height = height/4;
       */
        Activity context = getActivity();
        DisplayMetrics metrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        //int width = metrics.widthPixels;
        height = metrics.heightPixels;
        height = height - const_height;
/*
        TypedValue tv = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);
        int actionBarHeight = getResources().getDimensionPixelSize(tv.resourceId);

        height = height - actionBarHeight;
*/
        // prepared arraylist and passed it to the Adapter class
        mAdapter = new HomeGridviewAdapter(context, listCategory, listCategoryImage);

        // Set custom adapter to gridview
        gridView = (GridView) rootView.findViewById(R.id.gridView1);
        gridView.setAdapter(mAdapter);

        // Implement On Item click listener
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {

                switch (position) {
                    case 0:
                    {
                        fragment = new ElectronicsPosts();
                        break;
                    }
                    case 1:
                    {
                        fragment = new BuildingsPosts();
                        break;
                    }
                    case 2:
                    {
                        fragment = new RealtyPosts();
                        break;
                    }
                    case 3:
                    {
                        fragment = new ServicePartsPosts();
                        break;
                    }
                    case 4:
                    {
                        fragment = new AvtoPosts();
                        break;
                    }
                    case 5:
                    {
                        fragment = new RestPosts();
                        break;
                    }

                    default:
                        break;
                }

                createFragment(fragment);
            }
        });

        configureAddButton();

        return rootView;
    }

    private void createFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

            GlobalVar.isHomeFragment = false;

        } else {
            // error in creating fragment
            Log.e("HomeActivity", "Error in creating fragment");
        }
    }

    public void prepareList()
    {
        listCategory = new ArrayList<String>();

        listCategory.add(ApiHelper.getCategoryName(ApiHelper.getCategoryId(0)).toUpperCase());
        listCategory.add(ApiHelper.getCategoryName(ApiHelper.getCategoryId(1)).toUpperCase());
        listCategory.add(ApiHelper.getCategoryName(ApiHelper.getCategoryId(2)).toUpperCase());
        listCategory.add(ApiHelper.getCategoryName(ApiHelper.getCategoryId(3)).toUpperCase());
        listCategory.add(ApiHelper.getCategoryName(ApiHelper.getCategoryId(4)).toUpperCase());
        listCategory.add(ApiHelper.getCategoryName(ApiHelper.getCategoryId(5)).toUpperCase());

        listCategoryImage = new ArrayList<Integer>();
        listCategoryImage.add(R.drawable.house_holder);
        listCategoryImage.add(R.drawable.rent);
        listCategoryImage.add(R.drawable.realty);
        listCategoryImage.add(R.drawable.transport);
        listCategoryImage.add(R.drawable.car);
        listCategoryImage.add(R.drawable.service);
    }

    private void configureAddButton() {
        // TODO Auto-generated method stub
        final ImageButton btn = (ImageButton) rootView.findViewById(R.id.btnAdd);

        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                GlobalVar.Mode = true;
                GlobalVar._Post = null;
                GlobalVar._bitmaps.clear();
                GlobalVar.image_paths.clear();
                GlobalVar.mSparseBooleanArray.clear();

                if (!GlobalVar.Phone.equals("") && !GlobalVar.Token.equals("")) {
                    fragment = new AddPostFragment();
                    createFragment(fragment);
                } else {

                    GlobalVar.adv_position = true;
                    Intent in;
                    if(GlobalVar.isCodeSent)
                      in = new Intent(getActivity(), RegisterActivity.class);
                    else in = new Intent(getActivity(), CodeActivity.class);
                    startActivity(in);
                }
            }
        });

    }

}
