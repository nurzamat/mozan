package com.mozan;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.mozan.model.Category;
import com.mozan.util.ApiHelper;
import com.mozan.util.GlobalVar;
import com.mozan.util.JsonArrayRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StartActivity extends Activity {

    private static final String TAG =  "[CATEGORIES response]";
    private ProgressDialog pDialog;
    TextView etText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etText = (TextView) findViewById(R.id.no_internet);

        SharedPreferences sp = this.getSharedPreferences(GlobalVar.MOZAN,0);
        GlobalVar.Phone = sp.getString(GlobalVar.MOZAN_PHONE, "");
        GlobalVar.Token = sp.getString(GlobalVar.MOZAN_TOKEN, "");
        GlobalVar.Uid = sp.getString(GlobalVar.MOZAN_UID, "");

        Log.d("StartActivity", "Phone/token/uid: " + GlobalVar.Phone  + " / " + GlobalVar.Token + " / " + GlobalVar.Uid);

        if(!ApiHelper.isConnected(StartActivity.this)){
            Toast.makeText(StartActivity.this, "No Internet connection!", Toast.LENGTH_LONG).show();
            //return;
            etText.setText(R.string.connection_problem);
        }
        else
        {
            // Use Volley or Async Task
            //VolleyRequest();
            HttpAsyncTask task = new HttpAsyncTask();
            task.execute(ApiHelper.CATEGORIES_URL);
        }
    }

    private void VolleyRequest() {
        pDialog = ProgressDialog.show(StartActivity.this, "", "Загрузка...", true);
        GlobalVar._categories.clear();
        JsonArrayRequest Req = new JsonArrayRequest(ApiHelper.CATEGORIES_URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        // Parsing json array
                        for (int i = 0; i < response.length(); i++) {
                            try
                            {
                                JSONObject obj = response.getJSONObject(i);
                                Category category = new Category();
                                category.setId(obj.getString("id"));
                                category.setName(obj.getString("name"));
                                category.setParent(obj.getString("parent"));

                                GlobalVar._categories.add(category);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        Intent in = new Intent(StartActivity.this, HomeActivity.class);
                        startActivity(in);
                        finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });
        // Adding request to request queue
        AppController appcon = AppController.getInstance();
        appcon.addToRequestQueue(Req);
        pDialog.dismiss();
    }


    private class HttpAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = ProgressDialog.show(StartActivity.this, "", "Loading, please wait...", true);
        }

        @Override
        protected String doInBackground(String... urls) {

            try
            {
                GlobalVar._categories.clear();
                ApiHelper api = new ApiHelper();
                JSONArray response = api.getCategories();
                // Parsing json array
                for (int i = 0; i < response.length(); i++)
                {
                    JSONObject obj = response.getJSONObject(i);
                    Category category = new Category();
                    category.setId(obj.getString("id"));
                    category.setName(obj.getString("name"));
                    category.setParent(obj.getString("parent"));

                    GlobalVar._categories.add(category);
                }
            }
            catch (Exception ex)
            {
                String exText = ex.getMessage();
                Log.d("code activity", "Exeption: " + exText);
                return "Ошибка";
            }

            return "";
        }

        @Override
        protected void onPostExecute(String result)
        {
            pDialog.dismiss();
            if(!result.equals(""))
            {
                Toast.makeText(StartActivity.this, result, Toast.LENGTH_SHORT).show();
                etText.setText(R.string.connection_problem);
            }
            else
            {
                Intent in = new Intent(StartActivity.this, HomeActivity.class);
                startActivity(in);
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
