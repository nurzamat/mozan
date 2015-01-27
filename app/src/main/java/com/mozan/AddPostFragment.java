package com.mozan;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.mozan.adapter.PlaceSlidesFragmentAdapter;
import com.mozan.lib.CirclePageIndicator;
import com.mozan.util.ApiHelper;
import com.mozan.util.GlobalVar;
import com.mozan.util.PutRequest;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class AddPostFragment extends Fragment {

    // Declare Variables
    // Log tag
    private static final String TAG =  "[add/edit post response]";
    ViewPager mPager;
    PagerAdapter mAdapter;
    View rootView;
    CirclePageIndicator mIndicator;
    String content;
    String category = "";
    String category_name = "";
    String price;
    String price_currency;
    String result;
    Activity context;
    boolean mode = true; // add mode = 1, edit mode = 0;
    String url;
    String id = "";
    EditText etContent;
    EditText etPrice;
    public static Context ctx;

    public AddPostFragment()
    {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        context = getActivity();
        ctx = context;
        this.mode =  GlobalVar.Mode;
        //for set text
        if(!mode) //edit
        {
            this.id = GlobalVar._Post.getId();
            this.url = ApiHelper.POST_URL + id + "/";
            this.category = GlobalVar._Post.getCategory();
            this.category_name = GlobalVar._Post.getCategoryName();
            this.content = GlobalVar._Post.getContent();
            this.price_currency = GlobalVar._Post.getPriceCurrency();

            String raw_price = GlobalVar._Post.getPrice();
            if (raw_price.contains("."))
            {
                String[] parts = raw_price.split(Pattern.quote("."));
                this.price = parts[0].replaceAll("\\D+","") + "." + parts[1].replaceAll("\\D+","");
            }
            else
            {
                this.price = "";
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //context = getActivity();
        rootView = inflater.inflate(R.layout.fragment_add_post, container, false);
        etContent = (EditText) rootView.findViewById(R.id.content);
        etPrice = (EditText) rootView.findViewById(R.id.price);
        //spinner job
        Spinner spinner = (Spinner) rootView.findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.price_currencies, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapter_category = ArrayAdapter.createFromResource(context,
                R.array.categories_ru, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter_category.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        Button postBtn = (Button) rootView.findViewById(R.id.btnPost);
        Button categoryBtn = (Button) rootView.findViewById(R.id.btnCategory);
        if(GlobalVar.SelectedCategory != null)
        {
            categoryBtn.setText(GlobalVar.SelectedCategory.getName());
            this.category = GlobalVar.SelectedCategory.getId();
        }
        else if(!category_name.equals(""))
        {
            categoryBtn.setText(category_name);
        }

        if(!mode) //edit
        {
            etContent.setText(content);
            etPrice.setText(price);
            spinner.setSelection(adapter.getPosition(price_currency));
            postBtn.setText(R.string.save);
        }
        else
        {
            postBtn.setText(R.string.add);
        }

        spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int pos, long id) {
                        // On selecting a spinner item
                        price_currency = parent.getItemAtPosition(pos).toString();

                        // Showing selected spinner item
                        //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub
                    }
                }
        );

        int color = getResources().getColor(R.color.blue_dark);
        mAdapter = new PlaceSlidesFragmentAdapter(context);

        mPager = (ViewPager) rootView.findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        mIndicator = (CirclePageIndicator) rootView.findViewById(R.id.indicator);
        mIndicator.setFillColor(color);
        mIndicator.setStrokeColor(color);
        mIndicator.setRadius(5);
        mIndicator.setViewPager(mPager);
        mIndicator.setSnap(true);

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postButton();
            }
        });

        categoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalVar.SelectedCategory = null;
                Intent in = new Intent(context, HomeActivity.class);
                in.putExtra("case", 8); //categories
                startActivity(in);
            }
        });

        return rootView;
    }

    private void postButton() {
        if(!validate())
        {
            Toast.makeText(context, "Заполните все поля", Toast.LENGTH_LONG).show();
        }
        else
        {
            if (mode)
            {
                HttpAsyncTask task = new HttpAsyncTask();
                task.execute(ApiHelper.SEND_POST_URL);
            }
            else
            {   //use volley or async
                //VolleyPut();

                //async task
                ProgressDialog pdialog = ProgressDialog.show(context, "","Загрузка...", true);
                pdialog.show();
                putHttpAsyncTask task = new putHttpAsyncTask();
                task.execute(url);
                pdialog.dismiss();
            }
        }
    }

    private void VolleyPut() {
        PutRequest pr = new PutRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d(TAG, response);
                        if(GlobalVar.image_paths.size() > 0)
                        {
                            Intent in = new Intent(context, EditImageActivity.class);
                            startActivity(in);
                        }
                        else
                        {
                            Toast.makeText(context, "Сохранено", Toast.LENGTH_SHORT).show();
                            Intent in = new Intent(context, HomeActivity.class);
                            in.putExtra("case", 1);
                            startActivity(in);

                            //clear images
                            GlobalVar._bitmaps.clear();
                            GlobalVar.image_paths.clear();
                            GlobalVar._Post = null;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        // hide the progress dialog
                        Toast.makeText(context, "Ошибка", Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();

                params.put("category", category);
                params.put("content", content);
                params.put("price", price);
                params.put("price_currency", price_currency);
                params.put("api_key", ApiHelper.API_KEY);

                return params;
            }
        };
        AppController appcon = AppController.getInstance();
        appcon.addToRequestQueue(pr);
    }

    private boolean validate(){

        content = etContent.getText().toString().trim();
        price = etPrice.getText().toString().trim();

        return (!content.equals("") && !price.equals("") && !category.equals("") && !price_currency.equals(""));
    }

    // add mode
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {

        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(context, "","Загрузка...", true);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {

            try
            {
                result = "";
                ApiHelper api = new ApiHelper();

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("category", category);
                jsonObject.put("content", content);
                jsonObject.put("price", price);
                jsonObject.put("price_currency", price_currency);
                jsonObject.put("api_key", ApiHelper.API_KEY);

                JSONObject obj = api.sendPost(jsonObject);
                if(obj.has("id"))
                {
                    String url = ApiHelper.POST_URL + obj.getString("id") + "/images/";
                    int length = GlobalVar.image_paths.size();
                    if(length > 0)
                    {
                        JSONObject jobj;
                        for (int i = 0; i <length; i++) {
                            jobj = api.sendImage(url, GlobalVar.image_paths.get(i), true);
                            if(jobj.has("id"))
                                continue;
                        }
                    }
                    result = "Добавлено";
                }
                else result = "Ошибка";

                Log.d("AddPostFragment", "Token: " + GlobalVar.Token);
            }
            catch (Exception ex)
            {
                String exText = ex.getMessage();
                Log.d("AddPostFragment", "Exeption: " + exText);
                return "Ошибка";
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result)
        {
            dialog.dismiss();
            Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
            Intent in = new Intent(context, HomeActivity.class);
            in.putExtra("case", 1);
            startActivity(in);
            //clear images
            GlobalVar._bitmaps.clear();
            GlobalVar.image_paths.clear();
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
                jsonObject.put("category", category);
                jsonObject.put("content", content);
                jsonObject.put("price", price);
                jsonObject.put("price_currency", price_currency);
                jsonObject.put("api_key", ApiHelper.API_KEY);

                JSONObject obj = api.editPost(url, jsonObject); // will be checked for status ok
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
                if(GlobalVar.image_paths.size() > 0)
                {
                    Intent in = new Intent(context, EditImageActivity.class);
                    startActivity(in);
                }
                else
                {
                    Toast.makeText(context, "Сохранено", Toast.LENGTH_SHORT).show();
                    Intent in = new Intent(context, HomeActivity.class);
                    in.putExtra("case", 1);
                    startActivity(in);

                    //clear images
                    GlobalVar._bitmaps.clear();
                    GlobalVar.image_paths.clear();
                    GlobalVar._Post = null;
                }
            }
        }
    }
}