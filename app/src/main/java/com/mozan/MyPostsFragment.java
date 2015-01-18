package com.mozan;

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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.mozan.adapter.CustomListPostAdapter;
import com.mozan.model.Post;
import com.mozan.util.ApiHelper;
import com.mozan.util.GlobalVar;
import com.mozan.util.JsonObjectRequest;
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
    public static CustomListPostAdapter adapter;
    private TextView emptyText;
    private View rootView;
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
            adapter = new CustomListPostAdapter(context, postList);
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
                        emptyText.setText(R.string.no_my_posts);

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

        configureAddButton();
        // Inflate the layout for this fragment
        return rootView;
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    private void configureAddButton() {
        // TODO Auto-generated method stub
        final ImageButton btn = (ImageButton) rootView.findViewById(R.id.btnAdd);

        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!GlobalVar.Phone.equals("") && !GlobalVar.Token.equals("")) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", R.drawable.camera);
                    bundle.putString("paths", "");
                    fragment = new AddPostFragment();
                    fragment.setArguments(bundle);
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
                    Intent in = new Intent(getActivity(), CodeActivity.class);
                    startActivity(in);
                }
            }
        });

    }
}
