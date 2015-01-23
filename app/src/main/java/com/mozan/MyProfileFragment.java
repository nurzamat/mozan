package com.mozan;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.mozan.util.ApiHelper;
import com.mozan.util.GlobalVar;
import com.mozan.util.JsonObjectRequest;
import org.json.JSONException;
import org.json.JSONObject;

public class MyProfileFragment extends Fragment {

    private static final String TAG =  "[my profile response]";
    EditText dname, phone;
    NetworkImageView avatar;
    View rootView;
    String url;
    Context context;
    String avatar_original_image;
    String displayed_name;
    String avatar_30;
    String user;
    private ProgressDialog pDialog;
    ProgressBar spin;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

	public MyProfileFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        context = getActivity();
        url = ApiHelper.USER_URL + GlobalVar.Uid + "/profile/";
        rootView = inflater.inflate(R.layout.fragment_my_profile, container, false);
        dname = (EditText) rootView.findViewById(R.id.dname);
        phone = (EditText) rootView.findViewById(R.id.phone);
        phone.setEnabled(false);
        avatar = (NetworkImageView) rootView.findViewById(R.id.avatar);

        spin = (ProgressBar) rootView.findViewById(R.id.spinAvatar);
        spin.setVisibility(View.VISIBLE);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("url", url);
                Log.d(TAG, response.toString());
                try {
                      avatar_original_image = response.getString("avatar_original_image");
                      displayed_name = response.getString("displayed_name");
                      user = response.getString("user");
                      avatar_30 = response.getString("avatar_30");

                    dname.setText(displayed_name);
                    phone.setText(user);
                    avatar.setImageUrl(ApiHelper.MOZAN_URL + avatar_30, imageLoader);
                    if(avatar.getDrawable() != null)
                        spin.setVisibility(View.GONE);

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

        Button btnSave = (Button) rootView.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                putHttpAsyncTask task = new putHttpAsyncTask();
                task.execute(url);
            }
        });

        return rootView;
    }
    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    private class putHttpAsyncTask extends AsyncTask<String, Void, String> {

        ProgressDialog pdialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdialog = ProgressDialog.show(context, "","Загрузка...", true);
            pdialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            try
            {
                ApiHelper api = new ApiHelper();

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("displayed_name", displayed_name);
                //jsonObject.put("avatar_original_image", avatar_original_image);
                //jsonObject.put("avatar_30", avatar_30);
                //jsonObject.put("api_key", ApiHelper.API_KEY);

                JSONObject obj = api.editProfile(url, jsonObject); // will be checked for status ok
            }
            catch (Exception ex)
            {
                Log.d("AddPostFragment", "Exeption: " + ex.getMessage());
                return "Ошибка";
            }

            return "";
        }

        @Override
        protected void onPostExecute(String result)
        {
            pdialog.dismiss();
            if(!result.equals(""))
            {
                Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(context, "Сохранено", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
