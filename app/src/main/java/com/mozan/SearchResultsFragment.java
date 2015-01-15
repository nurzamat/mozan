package com.mozan;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mozan.adapter.CustomListAdapter;
import com.mozan.model.Post;
import com.mozan.util.ApiHelper;
import com.mozan.util.GlobalVar;
import com.mozan.util.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class SearchResultsFragment extends Fragment {

    // Log tag
    private static final String TAG =  "[search response]";

    private static final String url = ApiHelper.SEARCH_POST_URL + GlobalVar.query;
    private ProgressDialog pDialog;
    private List<Post> postList = new ArrayList<Post>();
    private ListView listView;
    private CustomListAdapter adapter;


    public SearchResultsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_search_results, container, false);

        try
        {
            Activity context = getActivity();
            listView = (ListView) rootView.findViewById(R.id.list);
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

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {

                    int count = response.getInt("count");
                    int start_index = response.getInt("start_index");
                    int end_index = response.getInt("end_index");
                    int num_pages = response.getInt("num_pages");

                    String next = response.getString("next");
                    String previous = response.getString("previous");
                    JSONArray jarray =  response.getJSONArray("results");
                    JSONArray jimages;

                    for (int i = 0; i < jarray.length(); i++) {
                        try {

                            JSONObject obj = jarray.getJSONObject(i).getJSONObject("object");
                            Post post = new Post();

                            post.setContent(obj.getString("content"));

                            post.setCategory("");
                            jimages = obj.getJSONArray("images");
                            if(jimages.length() > 0)
                            post.setThumbnailUrl(ApiHelper.MEDIA_URL + obj.getJSONArray("images").getJSONObject(0).getString("original_image"));
                            post.setUsername(obj.getJSONObject("owner").getString("username"));
                            post.setPrice(obj.getString("price"));

                            // Genre is json array
                            ArrayList<String> genre = new ArrayList<String>();
                            genre.add(post.getUsername());

                            post.setGenre(genre);
                            postList.add(post);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    // notifying list adapter about data changes
                    // so that it renders the list view with updated data
                    adapter.notifyDataSetChanged();

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