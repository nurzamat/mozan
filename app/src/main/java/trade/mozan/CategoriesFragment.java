package trade.mozan;
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
import trade.mozan.adapter.CategoryListAdapter;
import trade.mozan.model.Category;
import trade.mozan.util.GlobalVar;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by nurzamat on 1/27/15.
 */
public class CategoriesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private ListView listView;
    ArrayList<Category> categories = new ArrayList<Category>();
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

            if(GlobalVar.SelectedCategory != null)
            {
                for(Iterator<Category> i = GlobalVar._categories.iterator(); i.hasNext(); ) {
                    Category item = i.next();
                    if(item.getParent().equals(GlobalVar.SelectedCategory.getId()))
                        categories.add(item);
                }
                if(categories.size() == 0)
                {
                    Intent in = new Intent(getActivity(), HomeActivity.class);
                    in.putExtra("case", 6); //
                    getActivity().startActivity(in);
                }
            }
            else
            {
                for(Iterator<Category> i = GlobalVar._categories.iterator(); i.hasNext(); ) {
                    Category item = i.next();
                    if(item.getParent().equals(null) || item.getParent().equals("null")) // parent categories
                        categories.add(item);
                }
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
