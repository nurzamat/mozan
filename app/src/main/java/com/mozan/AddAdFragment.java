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

import com.mozan.adapter.PlaceSlidesFragmentAdapter;
import com.mozan.lib.CirclePageIndicator;
import com.mozan.util.ApiHelper;
import com.mozan.util.GlobalVar;

import org.json.JSONObject;

public class AddAdFragment extends Fragment {

    // Declare Variables
    ViewPager mPager;
    PagerAdapter mAdapter;
    View rootView;
    CirclePageIndicator mIndicator;
    int id_resource = 0;
    String paths = "";
    String content;
    int position;
    String category;
    String price;
    String price_currency;
    String result;
    Activity context;

    public AddAdFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle obj = getArguments();
        if(obj != null)
        {
           this.id_resource =  obj.getInt("id_resource");
           this.paths = obj.getString("paths");
        }
        rootView = inflater.inflate(R.layout.fragment_add_ad, container, false);

        context = getActivity();
        mAdapter = new PlaceSlidesFragmentAdapter(context, paths.split("|"));

        mPager = (ViewPager) rootView.findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        mIndicator = (CirclePageIndicator) rootView.findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
        mIndicator.setSnap(true);
        /*
        mIndicator
                .setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        Toast.makeText(AddAdFragment.this.getActivity(),
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
                //Intent in = new Intent(context, CustomGalleryActivity.class);
                Intent in = new Intent(context, GalleryActivity.class);
               // Intent in = new Intent(context, GridViewActivity.class); //Ravi solution
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

    private boolean validate(){
        EditText etContent = (EditText) rootView.findViewById(R.id.content);
        content = etContent.getText().toString().trim();

        EditText etPrice = (EditText) rootView.findViewById(R.id.price);
        price = etPrice.getText().toString().trim();

        return (!content.equals("") && !price.equals("") && !category.equals("") && !price_currency.equals(""));
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = ProgressDialog.show(context, "",
                    "Posting...", true);
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
                    result = "Добавлено";

                }
                else result = "Ошибка";

                Log.d("AddAdFragment", "Token: " + GlobalVar.Token);
            }
            catch (Exception ex)
            {
                String exText = ex.getMessage();
                Log.d("AddAdFragment", "Exeption: " + exText);
                return exText;
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result)
        {
            dialog.dismiss();
            Toast.makeText(context, result, Toast.LENGTH_SHORT).show();

        }
    }
}