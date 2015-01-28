package com.mozan;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.mozan.adapter.PostListAdapter;
import com.mozan.model.Category;
import com.mozan.model.Image;
import com.mozan.model.Post;
import com.mozan.util.ApiHelper;
import com.mozan.util.GlobalVar;
import com.mozan.util.JsonObjectRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ElectronicsPosts extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // Log tag
    private static final String TAG =  "[post response]";

    private static final String url = ApiHelper.ELECTRONICS_URL;
    private ProgressDialog pDialog;
    private List<Post> postList = new ArrayList<Post>();
    private ListView listView;
    private PostListAdapter adapter;
    private TextView emptyText;
    private View rootView;
    AppController appcon;
    private int total;
    private String next = null;
    ProgressBar spin;

    public ElectronicsPosts() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_electronics_posts, container, false);
        try
        {
            Activity context = getActivity();
            listView = (ListView) rootView.findViewById(R.id.list);
            emptyText = (TextView) rootView.findViewById(android.R.id.empty);
            listView.setEmptyView(emptyText);
            adapter = new PostListAdapter(context, postList);
            listView.setAdapter(adapter);

            spin = (ProgressBar) rootView.findViewById(R.id.loading);
            // changing action bar color
            context.getActionBar().setBackgroundDrawable(
                    new ColorDrawable(Color.parseColor("#1b1b1b")));

            ButtonClick();

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

    private void VolleyRequest(String url) {
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
                            post.setHitcount(obj.getJSONObject("hitcount").getString("counter"));
                            post.setHitcountId(obj.getJSONObject("hitcount").getString("id"));
                            post.setPrice(obj.getString("price"));
                            post.setPriceCurrency(obj.getString("price_currency"));
                            owner = obj.getJSONObject("owner");
                            post.setUsername(owner.getString("username"));
                            post.setUserId(owner.getString("id"));
                            post.setDisplayedName(owner.getJSONObject("profile").getString("displayed_name"));
                            post.setAvatarUrl(ApiHelper.MOZAN_URL + owner.getJSONObject("profile").getString("avatar_30"));
                            category_id = obj.getString("category");
                            post.setCategory(category_id);
                            post.setCategoryName(ApiHelper.getCategoryName(category_id));
                            jimages = obj.getJSONArray("images");
                            if(jimages.length() > 0)
                            {
                                post.setThumbnailUrl(ApiHelper.MEDIA_URL + jimages.getJSONObject(0).getString("original_image"));
                                // Images
                                ArrayList<Image> images = new ArrayList<Image>();
                                JSONObject img;
                                Image image;
                                for (int j = 0; j < jimages.length(); j++)
                                {
                                    img = jimages.getJSONObject(j);
                                    image = new Image(img.getString("id"), ApiHelper.MEDIA_URL + img.getString("original_image"));
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
                        emptyText.setText(R.string.no_posts);

                    spin.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                spin.setVisibility(View.GONE);
            }
        });
        // Adding request to request queue
        appcon.addToRequestQueue(jsonObjReq);
    }

    public void ButtonClick()
    {
        try
        {
            ArrayList<Category> categories = new ArrayList<Category>();

            for(Iterator<Category> i = GlobalVar._categories.iterator(); i.hasNext(); ) {
                Category item = i.next();
                if(item.getParent().equals("6")) // 6 - Электроника и техника
                    categories.add(item);
            }

            Button btn1 = (Button) rootView.findViewById(R.id.btn1);
            Button btn2 = (Button) rootView.findViewById(R.id.btn2);
            final Category category1 = categories.get(0);
            final Category category2 = categories.get(1);
            btn1.setText(category1.getName());
            btn2.setText(category2.getName());

            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    postList.clear();
                    next = null;
                    VolleyRequest(ApiHelper.CATEGORY_URL + category1.getId() + "/");
                }
            });

            btn2.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    postList.clear();
                    next = null;
                    VolleyRequest(ApiHelper.CATEGORY_URL + category2.getId() + "/");
                }
            });
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
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
