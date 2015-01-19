package com.mozan;

import android.app.Activity;
import android.app.ProgressDialog;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddPostFragment extends Fragment {

    // Declare Variables
    // Log tag
    private static final String TAG =  "[add/edit post response]";
    ViewPager mPager;
    PagerAdapter mAdapter;
    View rootView;
    CirclePageIndicator mIndicator;
    String content;
    int position;
    String category;
    String price;
    String price_currency;
    String result;
    Activity context;
    boolean mode = true; // add mode = 1, edit mode = 0;
    String url;
    String id = "";
    private ArrayList<String> image_urls = null;
    private ProgressDialog dialog;
    EditText etContent;
    EditText etPrice;

    public AddPostFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle obj = getArguments();
        if(obj != null)
        {
           //this.mode =  obj.getBoolean("mode");
           this.mode =  GlobalVar.Mode;
           //for set text
           if(!mode)
           {
               this.id = GlobalVar._Post.getId();
               this.url = ApiHelper.POST_URL + id + "/";
               this.category = GlobalVar._Post.getCategory();
               this.content = GlobalVar._Post.getContent();
               this.image_urls = GlobalVar._Post.getImageUrls();
               this.price_currency = GlobalVar._Post.getPriceCurrency();
               try
               {
                   String part = GlobalVar._Post.getPrice().split(" ")[0];
                   if(part.split(".").length > 0)
                       this.price = part.split(".")[0];
               }
               catch (Exception ex)
               {
                   ex.printStackTrace();
               }
           }
        }
        rootView = inflater.inflate(R.layout.fragment_add_post, container, false);
        etContent = (EditText) rootView.findViewById(R.id.content);
        etPrice = (EditText) rootView.findViewById(R.id.price);

        context = getActivity();
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
        /*
        mIndicator
                .setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        Toast.makeText(AddPostFragment.this.getActivity(),
                                "Changed to page " + position,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPageScrolled(int position,
                                               float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                });

        */

        //spinner job

        Spinner spinner = (Spinner) rootView.findViewById(R.id.spinner);
        Spinner spinner_category = (Spinner) rootView.findViewById(R.id.spinner_category);
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
        spinner_category.setAdapter(adapter_category);

        if(!mode)
        {
            etContent.setText(content);
            etPrice.setText(price);
            spinner.setSelection(adapter.getPosition(price_currency));
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
        spinner_category.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int pos, long id) {

                        position = pos;
                        category = parent.getItemAtPosition(pos).toString();
                        // On selecting a spinner item
                       // String item = parent.getItemAtPosition(pos).toString();
                        // Showing selected spinner item
                       // Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub
                    }
                }
        );

        cameraButton();
        categoryButton();
        postButton();
        return rootView;
    }

    private void cameraButton() {
        // TODO Auto-generated method stub
        ImageButton btn = (ImageButton) rootView.findViewById(R.id.btnCamera);

        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent in = new Intent(context, MultiPhotoSelectActivity.class);
                startActivity(in);
            }
        });
    }
    private void categoryButton() {
        // TODO Auto-generated method stub

    }

    private void postButton() {
        // TODO Auto-generated method stub
        Button btn = (Button) rootView.findViewById(R.id.btnPost);

        if (mode)
        {
            // add mode
            btn.setText(R.string.add);
            btn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if(!validate())
                    {
                        Toast.makeText(context, "Заполните все поля", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        HttpAsyncTask task = new HttpAsyncTask();
                        task.execute(ApiHelper.SEND_POST_URL);
                    }
                }
            });
        }
        else
        {
            //edit mode
            btn.setText(R.string.save);
            btn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (!validate()) {
                        Toast.makeText(context, "Заполните все поля", Toast.LENGTH_LONG).show();
                    } else {
                        dialog = ProgressDialog.show(context, "",
                                "Загрузка...", true);
                        PutRequest dr = new PutRequest(url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        // response
                                        Log.d(TAG, response);
                                       // Toast.makeText(context, "Сохранено", Toast.LENGTH_LONG).show();
                                        Intent in = new Intent(context, HomeActivity.class);
                                        in.putExtra("case", 1);
                                        startActivity(in);
                                        GlobalVar.Mode = true;
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                                        // hide the progress dialog
                                    }
                                }
                        ){
                            @Override
                            protected Map<String, String> getParams()
                            {
                                Map<String, String>  params = new HashMap<String, String>();

                                ApiHelper api = new ApiHelper();
                                params.put("category", api.getCategoryId(position));
                                params.put("content", content);
                                params.put("price", price);
                                params.put("price_currency", price_currency);
                                params.put("api_key", api.API_KEY);

                                return params;
                            }
                        };

                        dialog.dismiss();
                        AppController appcon = AppController.getInstance();
                        appcon.addToRequestQueue(dr);
                    }
                }
            });
        }
    }

    private boolean validate(){

        content = etContent.getText().toString().trim();
        price = etPrice.getText().toString().trim();

        return (!content.equals("") && !price.equals("") && !category.equals("") && !price_currency.equals(""));
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = ProgressDialog.show(context, "",
                    "Загрузка...", true);
        }

        @Override
        protected String doInBackground(String... urls) {

            try
            {
                result = "";
                ApiHelper api = new ApiHelper();

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("category", api.getCategoryId(position));
                jsonObject.put("content", content);
                jsonObject.put("price", price);
                jsonObject.put("price_currency", price_currency);
                jsonObject.put("api_key", api.API_KEY);

                JSONObject obj = api.sendPost(jsonObject);
                if(obj.has("id"))
                {
                    String id = obj.getString("id");
                    int length = GlobalVar.image_paths.size();
                    if(length > 0)
                    {
                        JSONObject jobj;
                        for (int i = 0; i <length; i++) {
                            jobj = api.sendImage(id, GlobalVar.image_paths.get(i));
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
        }
    }
}