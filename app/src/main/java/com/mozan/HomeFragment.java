package com.mozan;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;

import com.mozan.adapter.GridviewAdapter;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private GridviewAdapter mAdapter;
    private ArrayList<String> listCategory;
    private ArrayList<Integer> listCategoryImage;
    Fragment fragment = null;
    private View rootView;

    private GridView gridView;

	public HomeFragment()
    {

    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        prepareList();

        // prepared arraylist and passed it to the Adapter class
        mAdapter = new GridviewAdapter(getActivity(), listCategory, listCategoryImage);

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
                        fragment = new HouseHolderPosts();
                        break;
                    }
                    case 1:
                    {
                        fragment = new RealtyPosts();
                        break;
                    }
                    case 2:
                    {
                        fragment = new RentPosts();
                        break;
                    }
                    case 3:
                    {
                        fragment = new TransportPosts();
                        break;
                    }
                    case 4:
                    {
                        fragment = new CarPosts();
                        break;
                    }
                    case 5:
                    {
                        fragment = new ServicePosts();
                        break;
                    }

                    default:
                        break;
                }

                createFragment(fragment);
            }
        });

        configureImageButton();

        return rootView;
    }

    private void createFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

            HomeActivity.isHomeFragment = false;

        } else {
            // error in creating fragment
            Log.e("HomeActivity", "Error in creating fragment");
        }
    }

    public void prepareList()
    {
        listCategory = new ArrayList<String>();

        listCategory.add("House holder");
        listCategory.add("Realty");
        listCategory.add("Rent");
        listCategory.add("Transport");
        listCategory.add("Car");
        listCategory.add("Service");

        listCategoryImage = new ArrayList<Integer>();
        listCategoryImage.add(R.drawable.house_holder);
        listCategoryImage.add(R.drawable.realty);
        listCategoryImage.add(R.drawable.rent);
        listCategoryImage.add(R.drawable.transport);
        listCategoryImage.add(R.drawable.car);
        listCategoryImage.add(R.drawable.service);
    }

    private void configureImageButton() {
        // TODO Auto-generated method stub
        ImageButton btn = (ImageButton) rootView.findViewById(R.id.btnAdd);

        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("id",  R.drawable.camera);
                bundle.putString("paths", "");
                fragment = new AddAdFragment();
                fragment.setArguments(bundle);
             createFragment(fragment);

            }
        });
    }

}
