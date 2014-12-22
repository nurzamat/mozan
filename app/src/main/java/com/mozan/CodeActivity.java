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

public class CodeActivity extends Activity {

    EditText etPhone;
    Button btnPost;
    String phone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
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
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
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
        if(!validate())
        {
            Toast.makeText(this, "Enter some data!", Toast.LENGTH_LONG).show();
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

            String result;
           try
           {
               ApiHelper api = new ApiHelper();
               phone = phone.replace("+", "");
               result = api.getCode(phone).getString("response");
               Log.d("code activity", "result: " + result);
               if(result.equals("Code sent."))
               {
                   //defining global variables
                   GlobalVar.Phone = phone;
                  // GlobalVar.Code = result;
               } else result = "";
           }
           catch (Exception ex)
           {
               Log.d("code activity", "Exeption: " + ex.getMessage());
               return "";
           }

            return result;
        }

        @Override
        protected void onPostExecute(String result)
        {
            dialog.dismiss();
            if(result.equals(""))
            {
               Toast.makeText(CodeActivity.this, "Some error happened", Toast.LENGTH_SHORT).show();
            }else
            {
                Intent in = new Intent(CodeActivity.this, RegisterActivity.class);
                startActivity(in);
            }
        }
    }

}