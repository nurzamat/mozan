package com.mozan;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
    String result;

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

        mAdapter = new PlaceSlidesFragmentAdapter(getActivity(), paths.split("|"));

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
                Intent in = new Intent(getActivity(), CustomGalleryActivity.class);
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
                    Toast.makeText(getActivity(), "Empty content text!", Toast.LENGTH_LONG).show();
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
        return !content.equals("");
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = ProgressDialog.show(getActivity(), "",
                    "Posting...", true);
        }

        @Override
        protected String doInBackground(String... urls) {

            try
            {
                String category = "1";
                result = "";
                ApiHelper api = new ApiHelper();
                JSONObject obj = api.sendPost(category, content);

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
            Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();

        }
    }
}