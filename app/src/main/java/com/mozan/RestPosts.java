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
import android.widget.ListView;
import android.widget.TextView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.mozan.adapter.CustomListAdapter;
import com.mozan.model.Post;
import com.mozan.util.ApiHelper;
import com.mozan.util.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class RestPosts extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // Log tag
    private static final String TAG =  "[post response]";

    private static final String url = ApiHelper.REST_URL;
    private ProgressDialog pDialog;
    private List<Post> postList = new ArrayList<Post>();
    private ListView listView;
    private CustomListAdapter adapter;
    private TextView emptyText;

    public RestPosts() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_rent_posts, container, false);

        try
        {
            Activity context = getActivity();
            listView = (ListView) rootView.findViewById(R.id.list);
            emptyText = (TextView)rootView.findViewById(android.R.id.empty);
            listView.setEmptyView(emptyText);
            adapter = new CustomListAdapter(context, postList);
            listView.setAdapter(adapter);

            pDialog = new ProgressDialog(context);
            // Showing progress dialog before making http request
            pDialog.setMessage("Loading...");
            pDialog.show();

            // changing action bar color
            context.getActionBar().setBackgroundDrawable(
                    new ColorDrawable(Color.parseColor("#1b1b1b")));
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {

                    int count = response.getInt("count");
                    String next = response.getString("next");
                    String previous = response.getString("previous");
                    JSONArray jarray =  response.getJSONArray("results");
                    JSONArray jimages;

                    for (int i = 0; i < jarray.length(); i++) {
                        try {

                            JSONObject obj = jarray.getJSONObject(i);
                            Post post = new Post();
                            post.setId(obj.getString("id"));
                            post.setContent(obj.getString("content"));
                            post.setCategory(obj.getString("category"));
                            post.setPrice(obj.getString("price"));
                            post.setPriceCurrency(obj.getString("price_currency"));
                            post.setUsername(obj.getJSONObject("owner").getString("username"));
                            jimages = obj.getJSONArray("images");
                            if(jimages.length() > 0)
                            {
                                post.setThumbnailUrl(ApiHelper.MEDIA_URL + jimages.getJSONObject(0).getString("original_image"));
                                // Image Urls
                                ArrayList<String> urls = new ArrayList<String>();

                                for (int j = 0; j < jimages.length(); j++)
                                {
                                    urls.add(ApiHelper.MEDIA_URL + jimages.getJSONObject(j).getString("original_image"));
                                }
                                post.setImageUrls(urls);
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

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                hidePDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                // hide the progress dialog
                hidePDialog();
            }
        });
        // Adding request to request queue
        AppController appcon = AppController.getInstance();
        appcon.addToRequestQueue(jsonObjReq);

        // Inflate the layout for this fragment
        return rootView;
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }
}
