package com.mozan;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.mozan.adapter.GridviewAdapter;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private GridviewAdapter mAdapter;
    private ArrayList<String> listCategory;
    private ArrayList<Integer> listFlag;

    private GridView gridView;

	public HomeFragment()
    {

    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        prepareList();

        // prepared arraylist and passed it to the Adapter class
        mAdapter = new GridviewAdapter(getActivity(), listCategory, listFlag);

        // Set custom adapter to gridview
        gridView = (GridView) rootView.findViewById(R.id.gridView1);
        gridView.setAdapter(mAdapter);

        // Implement On Item click listener
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {

                Fragment fragment = null;
                switch (position) {
                    case 0:
                    {
                        fragment = new CarPosts();
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
                        fragment = new HouseHolderPosts();
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

                if (fragment != null) {
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.frame_container, fragment).commit();

                } else {
                    // error in creating fragment
                    Log.e("HomeActivity", "Error in creating fragment");
                }
                //Toast.makeText(getActivity(), mAdapter.getItem(position), Toast.LENGTH_SHORT).show();
            }
        });
        return rootView;
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

        listFlag = new ArrayList<Integer>();
        listFlag.add(R.drawable.house_holder);
        listFlag.add(R.drawable.realty);
        listFlag.add(R.drawable.rent);
        listFlag.add(R.drawable.transport);
        listFlag.add(R.drawable.car);
        listFlag.add(R.drawable.service);
    }
}
