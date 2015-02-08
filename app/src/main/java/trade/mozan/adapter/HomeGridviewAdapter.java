package trade.mozan.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import trade.mozan.HomeFragment;
import trade.mozan.R;

public class HomeGridviewAdapter extends BaseAdapter
{
	private ArrayList<String> listCategory;

	private ArrayList<Integer> listCategoryImage;
	private Activity activity;
	
	public HomeGridviewAdapter(Activity activity, ArrayList<String> listCategory, ArrayList<Integer> listCategoryImage) {
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

        view.txtViewTitle.setText(listCategory.get(position));
        view.imgViewFlag.setImageResource(listCategoryImage.get(position));

		return convertView;
	}

}
