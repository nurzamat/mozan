package com.mozan;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

import com.mozan.util.GlobalVar;

public class SearchResultsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_search_results);
        // get the action bar
        ActionBar actionBar = getActionBar();
        // Enabling Back navigation on Action Bar icon
        actionBar.setDisplayHomeAsUpEnabled(true);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    /**
     * Handling intent data
     */
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
        if(!query.equals(""))
        {
            GlobalVar.query = query;
            GlobalVar.search = true;
            Intent in = new Intent(SearchResultsActivity.this, HomeActivity.class);
            startActivity(in);
        }
           // Toast.makeText(this, query, Toast.LENGTH_LONG).show();
        }

        finish();
    }
}
