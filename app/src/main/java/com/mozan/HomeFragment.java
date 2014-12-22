package com.mozan;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.mozan.adapter.GridviewAdapter;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private GridviewAdapter mAdapter;
    private ArrayList<String> listCountry;
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
        mAdapter = new GridviewAdapter(getActivity(),listCountry, listFlag);

        // Set custom adapter to gridview
        gridView = (GridView) rootView.findViewById(R.id.gridView1);
        gridView.setAdapter(mAdapter);

        // Implement On Item click listener
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                Toast.makeText(getActivity(), mAdapter.getItem(position), Toast.LENGTH_SHORT).show();
            }
        });
        return rootView;
    }

    public void prepareList()
    {
        listCountry = new ArrayList<String>();

        listCountry.add("House holder");
        listCountry.add("Realty");
        listCountry.add("Rent");
        listCountry.add("Transport");
        listCountry.add("Car");
        listCountry.add("Service");

        listFlag = new ArrayList<Integer>();
        listFlag.add(R.drawable.house_holder);
        listFlag.add(R.drawable.realty);
        listFlag.add(R.drawable.rent);
        listFlag.add(R.drawable.transport);
        listFlag.add(R.drawable.car);
        listFlag.add(R.drawable.service);
    }
}
