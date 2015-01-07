package com.mozan;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mozan.util.ApiHelper;
import com.mozan.util.GlobalVar;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeActivity extends Activity {

    EditText etPhone;
    Button btnPost;
    String phone;
    String result;
    String status;
    public static Activity fa;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
        fa = this;
        setContentView(R.layout.code);
        etPhone = (EditText) findViewById(R.id.phone);
        btnPost = (Button) findViewById(R.id.btnCode);

        // check if you are connected or not
        if(!isConnected()){
            Toast.makeText(getBaseContext(), "No Internet connection!", Toast.LENGTH_LONG).show();
        }
    }

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private boolean validate()
    {
        phone = etPhone.getText().toString().trim();
        return !phone.equals("");
    }

    public void clickButton(View view){

        if(!isConnected()){
            Toast.makeText(getBaseContext(), "No Internet connection!", Toast.LENGTH_LONG).show();
            return;
        }
        else if(!validate())
        {
            Toast.makeText(this, "Enter some data!", Toast.LENGTH_LONG).show();
        }
        else if(!isPhoneNumberValid(phone))
        {
            Toast.makeText(this, "Введите пожалуйста Ваш номер телефона в международном формате.", Toast.LENGTH_LONG).show();
        }
        else
        {
            HttpAsyncTask task = new HttpAsyncTask();
            task.execute(ApiHelper.CODE_URL);
        }
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = ProgressDialog.show(CodeActivity.this, "",
                    "Loading, please wait...", true);
        }

        @Override
        protected String doInBackground(String... urls) {

           try
           {
               ApiHelper api = new ApiHelper();
               JSONObject obj = api.getCode(phone);
               //result = obj.getString("response");
               status = obj.getString("status");
               result = api.responseText(status);
               Log.d("code activity", "result: " + result);
               Log.d("code activity", "status: " + status);
               if(status.equals("ACTIVATION_CODE_SENT"))
               {
                   //defining global variables
                   GlobalVar.Phone = phone;
                  // GlobalVar.Code = result;
               }
           }
           catch (Exception ex)
           {
               String exText = ex.getMessage();
               Log.d("code activity", "Exeption: " + exText);
               return exText;
           }

            return result;
        }

        @Override
        protected void onPostExecute(String result)
        {
            dialog.dismiss();
            Toast.makeText(CodeActivity.this, result, Toast.LENGTH_SHORT).show();

            if(!GlobalVar.Phone.equals(""))
            {
                Intent in = new Intent(CodeActivity.this, RegisterActivity.class);
                startActivity(in);
            }
        }
    }

    public  boolean isPhoneNumberValid(String phoneNumber){
        boolean isValid = true;

        if(phone.length() < 9 || phone.length() > 15)
        {
            isValid = false;
        }
        return isValid;
    }
}