package trade.mozan;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import trade.mozan.adapter.MyPostListAdapter;
import trade.mozan.model.Image;
import trade.mozan.model.Post;
import trade.mozan.util.ApiHelper;
import trade.mozan.util.GlobalVar;
import trade.mozan.util.JsonObjectRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyPostsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // Log tag
    private static final String TAG =  "[my post response]";

    private String url;
    private ProgressDialog pDialog;
    private List<Post> postList = new ArrayList<Post>();
    private ListView listView;
    public static MyPostListAdapter adapter;
    private TextView emptyText;
    private View rootView;
    AppController appcon;
    private int total;
    private String next = null;
    ProgressBar spin;
    Fragment fragment = null;

    public MyPostsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_my_posts, container, false);
        url = ApiHelper.USER_URL + GlobalVar.Uid + "/posts/";
        try
        {
            Activity context = getActivity();
            listView = (ListView) rootView.findViewById(R.id.list);
            emptyText = (TextView)rootView.findViewById(android.R.id.empty);
            listView.setEmptyView(emptyText);
            adapter = new MyPostListAdapter(context, this, postList);
            listView.setAdapter(adapter);
            spin = (ProgressBar) rootView.findViewById(R.id.loading);
            // changing action bar color
            context.getActionBar().setBackgroundDrawable(
                    new ColorDrawable(Color.parseColor("#1b1b1b")));

            configureAddButton();

            appcon = AppController.getInstance();

            VolleyRequest(url);
            listView.setOnScrollListener(new EndlessScrollListener(1));
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }
        // Inflate the layout for this fragment
        return rootView;
    }

    private void VolleyRequest(String url)
    {
        spin.setVisibility(View.VISIBLE);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {

                    total = response.getInt("count");
                    next = response.getString("next");
                    String previous = response.getString("previous");
                    JSONArray jarray =  response.getJSONArray("results");
                    JSONArray jimages;
                    String category_id;
                    JSONObject owner;
                    for (int i = 0; i < jarray.length(); i++) {
                        try {

                            JSONObject obj = jarray.getJSONObject(i);
                            Post post = new Post();
                            post.setId(obj.getString("id"));
                            post.setContent(obj.getString("content"));
                            post.setHitcount(obj.getJSONObject("hitcount_pk").getString("counter"));
                            post.setHitcountId(obj.getJSONObject("hitcount_pk").getString("id"));
                            post.setPrice(obj.getString("price"));
                            post.setPriceCurrency(obj.getString("price_currency"));
                            owner = obj.getJSONObject("owner");
                            post.setUsername(owner.getString("username"));
                            post.setUserId(owner.getString("id"));
                            post.setDisplayedName(owner.getJSONObject("profile").getString("displayed_name"));
                            post.setAvatarUrl(owner.getJSONObject("profile").getString("avatar_30"));
                            post.setQuickbloxId(owner.getJSONObject("profile").getString("quick_blox_id"));
                            category_id = obj.getString("category");
                            post.setCategory(category_id);
                            post.setCategoryName(ApiHelper.getCategoryName(category_id));
                            jimages = obj.getJSONArray("images");
                            if(jimages.length() > 0)
                            {
                                post.setThumbnailUrl(jimages.getJSONObject(0).getString("original_image"));
                                // Images
                                ArrayList<Image> images = new ArrayList<Image>();
                                JSONObject img;
                                Image image;
                                for (int j = 0; j < jimages.length(); j++)
                                {
                                    img = jimages.getJSONObject(j);
                                    image = new Image(img.getString("id"), img.getString("original_image"));
                                    images.add(image);
                                }
                                post.setImages(images);
                            }
                            postList.add(post);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    // notifying list adapter about data changes
                    // so that it renders the list view with updated data
                    adapter.notifyDataSetChanged();
                    if(!(postList.size() > 0))
                        emptyText.setText(R.string.no_my_posts);

                    spin.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                // hide the progress dialog
                spin.setVisibility(View.GONE);
            }
        });
        // Adding request to request queue
        appcon = AppController.getInstance();
        appcon.addToRequestQueue(jsonObjReq);
    }

    private void configureAddButton() {
        // TODO Auto-generated method stub
        final ImageButton btn = (ImageButton) rootView.findViewById(R.id.btnAdd);

        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                GlobalVar.Mode = true;
                GlobalVar._Post = null;
                GlobalVar.SelectedCategory = null;
                GlobalVar._bitmaps.clear();
                GlobalVar.image_paths.clear();
                GlobalVar.mSparseBooleanArray.clear();

                if (!GlobalVar.Phone.equals("") && !GlobalVar.Token.equals("")) {
                    fragment = new AddPostFragment();
                    if (fragment != null) {
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.frame_container, fragment).commit();

                        GlobalVar.isHomeFragment = false;

                    } else {
                        // error in creating fragment
                        Log.e("HomeActivity", "Error in creating fragment");
                    }
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

    public class EndlessScrollListener implements AbsListView.OnScrollListener {

        private int visibleThreshold = 5;
        private int currentPage = 0;
        private int previousTotal = 0;
        private boolean loading = true;

        public EndlessScrollListener() {
        }
        public EndlessScrollListener(int visibleThreshold) {
            this.visibleThreshold = visibleThreshold;
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                    currentPage++;
                }
            }
            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                if(next != null && !next.equals("null"))
                    VolleyRequest(next);
                loading = true;
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }
    }
}
