package com.mozan;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mozan.util.ApiHelper;
import com.mozan.util.GlobalVar;

import org.json.JSONObject;

public class RegisterActivity extends Activity {

    EditText etCode;
    Button btnPost;
    String code;
    String result;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        etCode = (EditText) findViewById(R.id.code);
        btnPost = (Button) findViewById(R.id.btnRegister);
    }

    private boolean validate(){
        code = etCode.getText().toString().trim();
        return !code.equals("");
    }

    public void clickButton(View view){
        if(!validate())
        {
            Toast.makeText(RegisterActivity.this, "Enter the code!", Toast.LENGTH_LONG).show();
        }
        else
        {
            HttpAsyncTask task = new HttpAsyncTask();
            task.execute(ApiHelper.TOKEN_URL);
        }
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = ProgressDialog.show(RegisterActivity.this, "",
                    "Loading, please wait...", true);
        }

        @Override
        protected String doInBackground(String... urls) {

            try
            {
                ApiHelper api = new ApiHelper();
                JSONObject obj = api.getToken(GlobalVar.Phone, code);
                result = "";
                if(obj.has("token")) {
                    GlobalVar.Token = obj.getString("token");
                }
                else
                {
                    result = api.responseText(obj.getString("status"));
                    //result = obj.getString("response");  //problems with utf
                }
            }
            catch (Exception ex)
            {
                return ex.getMessage();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result)
        {
            dialog.dismiss();

            if(!GlobalVar.Token.equals(""))
            {
                SharedPreferences sp = getSharedPreferences(GlobalVar.MOZAN, 0);
                SharedPreferences.Editor Ed=sp.edit();
                Ed.putString(GlobalVar.MOZAN_PHONE, GlobalVar.Phone);
                Ed.putString(GlobalVar.MOZAN_TOKEN,GlobalVar.Token);
                Ed.commit();

                Intent in = new Intent(RegisterActivity.this, HomeActivity.class);
                startActivity(in);
                //Intent in2 = new Intent(RegisterActivity.this, CodeActivity.class);
                //in.putExtra("EXIT", true);
                //startActivity(in2);
                CodeActivity.fa.finish();
                finish();
            }
            else
            {
                if(!result.equals(""))
                Toast.makeText(RegisterActivity.this, result, Toast.LENGTH_SHORT).show();
            }
        }
    }

}