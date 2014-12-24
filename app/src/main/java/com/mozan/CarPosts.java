package com.mozan;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.mozan.adapter.CustomListAdapter;
import com.mozan.model.Post;
import com.mozan.util.ApiHelper;
import com.mozan.util.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class CarPosts extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // Log tag
    private static final String TAG =  "[posts response]";

    // Movies json url
    private static final String url = ApiHelper.CARS_URL;
    private ProgressDialog pDialog;
    private List<Post> postList = new ArrayList<Post>();
    private ListView listView;
    private CustomListAdapter adapter;


    public CarPosts() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_car_posts, container, false);

        try
        {
            listView = (ListView) rootView.findViewById(R.id.list);
            adapter = new CustomListAdapter(getActivity(), postList);
            listView.setAdapter(adapter);

            pDialog = new ProgressDialog(getActivity());
            // Showing progress dialog before making http request
            pDialog.setMessage("Loading...");
            pDialog.show();

            // changing action bar color
            getActivity().getActionBar().setBackgroundDrawable(
                    new ColorDrawable(Color.parseColor("#1b1b1b")));
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }

        JsonArrayRequest postReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        hidePDialog();

                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject obj = response.getJSONObject(i);
                                Post post = new Post();
                                post.setContent(obj.getString("content"));
                                post.setCategory(obj.getJSONObject("category").getString("name"));
                                post.setThumbnailUrl(ApiHelper.MEDIA_URL + obj.getJSONArray("images").getJSONObject(0).getString("image"));
                                post.setUsername(obj.getJSONObject("owner").getString("username"));
                                post.setPrice("12 som");

                                // Genre is json array
                                ArrayList<String> genre = new ArrayList<String>();
                                genre.add(post.getUsername());

                                post.setGenre(genre);
                                // adding movie to movies array
                                postList.add(post);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidePDialog();
            }
        });
        // Adding request to request queue
        AppController appcon = AppController.getInstance();
        appcon.addToRequestQueue(postReq);

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
