package trade.mozan.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import trade.mozan.HomeActivity;
import trade.mozan.R;
import trade.mozan.model.Category;
import trade.mozan.util.ApiHelper;
import trade.mozan.util.GlobalVar;

import java.util.List;

/**
 * Created by nurzamat on 1/27/15.
 */
public class CategoryListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Category> categoryItems;

    public CategoryListAdapter(Activity activity, List<Category> categoryItems) {
        this.activity = activity;
        this.categoryItems = categoryItems;
    }

    @Override
    public int getCount() {
        return categoryItems.size();
    }

    @Override
    public Object getItem(int location) {
        return categoryItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row_category, null);

        ImageView image = (ImageView) convertView.findViewById(R.id.category_image);
        TextView category_name = (TextView) convertView.findViewById(R.id.category_name);

        // getting category data for the row
        Category cat = categoryItems.get(position);
        // thumbnail image
        image.setImageResource(ApiHelper.getCategoryImage(cat.getId()));
        // name
        category_name.setText(cat.getName());

        convertView.setOnClickListener(new ClickListener(cat));

        return convertView;
    }

    class ClickListener implements View.OnClickListener {

        Category category;

        public ClickListener(Category _category)
        {
            this.category = _category;
        }
        @Override
        public void onClick(View v)
        {
            GlobalVar.SelectedCategory = category;

            Intent in = new Intent(activity, HomeActivity.class);
            in.putExtra("case", 8); //categories
            activity.startActivity(in);
        }
    }
}
