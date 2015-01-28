package com.mozan;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;

import com.mozan.adapter.FullScreenImageAdapter;
import com.mozan.lib.CirclePageIndicator;
import com.mozan.model.Post;
import com.mozan.util.ApiHelper;
import com.mozan.util.GlobalVar;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by nurzamat on 1/18/15.
 */
public class FullScreenViewActivity extends Activity {

    private FullScreenImageAdapter adapter;
    private ViewPager viewPager;
    private Post p = GlobalVar._Post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_view);

        viewPager = (ViewPager) findViewById(R.id.pager);

        adapter = new FullScreenImageAdapter(FullScreenViewActivity.this);

        viewPager.setAdapter(adapter);
        if(!p.getUsername().equals(GlobalVar.Phone))
        {
            HttpAsyncTask task = new HttpAsyncTask();
            task.execute(ApiHelper.HITCOUNT_URL);
        }
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {

            try
            {
                ApiHelper api = new ApiHelper();

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("hitcount_pk", p.getHitcountId());
                jsonObject.put("api_key", ApiHelper.API_KEY);

                JSONObject obj = api.sendHitcount(jsonObject);
                if(obj.getString("status").equals("success"))
                {
                    return "success";
                }
            }
            catch (Exception ex)
            {
               ex.printStackTrace();
                return "Ошибка";
            }

            return "";
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
        }
    }
}

