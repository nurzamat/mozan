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
import com.android.volley.toolbox.JsonArrayRequest;
import com.mozan.adapter.CustomListAdapter;
import com.mozan.model.Post;
import com.mozan.util.ApiHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PostsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // Log tag
    private static final String TAG =  "[posts response]";

    // Movies json url
    // private static final String url = "http://api.androidhive.info/json/movies.json";
    private static final String url = "https://mozan.market/api/post/list/";
    private static final String imagesUrl = ApiHelper.IMAGES_URL;
    private ProgressDialog pDialog;
    private List<Post> postList = new ArrayList<Post>();
    Map<String , String> images = new HashMap<String, String>();
    private ListView listView;
    private CustomListAdapter adapter;


    public PostsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_posts, container, false);

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
                                Post movie = new Post();
                                movie.setContent("title");
                                movie.setCategory("title");
                                movie.setThumbnailUrl("https://mozan.market/media/post_images/Beauty-of-nature-random-4884759-1280-800.jpg");
                                movie.setUsername("username");
                                movie.setPrice("12 som");

                                // Genre is json array
                                ArrayList<String> genre = new ArrayList<String>();
                                genre.add("title");
                                movie.setGenre(genre);
                                // adding movie to movies array
                                postList.add(movie);

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
