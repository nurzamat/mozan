package com.mozan;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.mozan.util.GlobalVar;

public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sp = this.getSharedPreferences(GlobalVar.MOZAN,0);
        GlobalVar.Phone = sp.getString(GlobalVar.MOZAN_PHONE, "");
        GlobalVar.Token = sp.getString(GlobalVar.MOZAN_TOKEN, "");

        Log.d("StartActivity", "Phone/token: " + GlobalVar.Phone  + " / " + GlobalVar.Token);

        Intent in = new Intent(StartActivity.this, HomeActivity.class);
        startActivity(in);
        finish();
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
