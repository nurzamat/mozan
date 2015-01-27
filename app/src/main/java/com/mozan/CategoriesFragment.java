package com.mozan;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.mozan.adapter.CategoryListAdapter;
import com.mozan.model.Category;
import com.mozan.util.GlobalVar;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by nurzamat on 1/27/15.
 */
public class CategoriesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private ListView listView;
    private CategoryListAdapter adapter;
    private View rootView;

    public CategoriesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.list_category, container, false);
        try
        {
            Activity context = getActivity();
            listView = (ListView) rootView.findViewById(R.id.list);

            ArrayList<Category> categories = new ArrayList<Category>();

            for(Iterator<Category> i = GlobalVar._categories.iterator(); i.hasNext(); ) {
                Category item = i.next();
                if(item.getParent().equals(null) || item.getParent().equals("null")) // parent categories
                    categories.add(item);
            }

            adapter = new CategoryListAdapter(context, categories);
            listView.setAdapter(adapter);
            // changing action bar color
            context.getActionBar().setBackgroundDrawable(
                    new ColorDrawable(Color.parseColor("#1b1b1b")));
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }
        // Inflate the layout for this fragment
        return rootView;
    }

}
