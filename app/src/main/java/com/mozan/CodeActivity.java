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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            Toast.makeText(this, "Not Valid Number!", Toast.LENGTH_LONG).show();
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

    public  boolean isPhoneNumberValid(String phoneNumber){
        boolean isValid = false;
/* Phone Number formats: (nnn)nnn-nnnn; nnnnnnnnnn; nnn-nnn-nnnn
    ^\\(? : May start with an option "(" .
    (\\d{3}): Followed by 3 digits.
    \\)? : May have an optional ")"
    [- ]? : May have an optional "-" after the first 3 digits or after optional ) character.
    (\\d{3}) : Followed by 3 digits.
     [- ]? : May have another optional "-" after numeric digits.
     (\\d{4})$ : ends with four digits.

         Examples: Matches following phone numbers:
         (123)456-7890, 123-456-7890, 1234567890, (123)-456-7890

*/
//Initialize reg ex for phone number.
        String expression = "^[+]?\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$";
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(phoneNumber);
        if(matcher.matches()){
            isValid = true;
        }

        phone = phone.replace("+", "").replace("(", "").replace(")", "").replace("-", "");
        if(phone.length() < 9 || phone.length() > 15)
        {
            isValid = false;
        }
        return isValid;
    }
}