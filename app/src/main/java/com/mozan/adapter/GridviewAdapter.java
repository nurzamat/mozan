package com.mozan.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mozan.HomeFragment;
import com.mozan.R;

public class GridviewAdapter extends BaseAdapter
{
	private ArrayList<String> listCategory;

	private ArrayList<Integer> listCategoryImage;
	private Activity activity;
	
	public GridviewAdapter(Activity activity,ArrayList<String> listCategory, ArrayList<Integer> listCategoryImage) {
		super();
		this.listCategory = listCategory;
		this.listCategoryImage = listCategoryImage;
		this.activity = activity;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listCategory.size();
	}

	@Override
	public String getItem(int position) {
		// TODO Auto-generated method stub
		return listCategory.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static class ViewHolder
	{
		public ImageView imgViewFlag;
		public TextView txtViewTitle;
        public LinearLayout layout;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder view;

        try {
            LayoutInflater inflator = activity.getLayoutInflater();

            if(convertView==null)
            {
                view = new ViewHolder();
                convertView = inflator.inflate(R.layout.gridview_row, null);

                view.txtViewTitle = (TextView) convertView.findViewById(R.id.textView1);
                view.imgViewFlag = (ImageView) convertView.findViewById(R.id.imageView1);
                view.layout = (LinearLayout) convertView.findViewById(R.id.layout_id);
                //view.imgViewFlag.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 180)); //for image
                // view.layout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, HomeFragment.height));
                convertView.setMinimumHeight(HomeFragment.height/3);
                convertView.setTag(view);
            }
            else
            {
                view = (ViewHolder) convertView.getTag();
            }

            if(position < 6)
            {
                view.txtViewTitle.setText(listCategory.get(position));
                view.imgViewFlag.setImageResource(listCategoryImage.get(position));
            }
        }
        catch (Exception ex)
        {
            Log.d("GridviewAdapter", ex.getMessage());

        }
		return convertView;
	}

}
