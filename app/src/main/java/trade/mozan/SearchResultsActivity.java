package trade.mozan;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

import trade.mozan.util.GlobalVar;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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
            try
            {
                GlobalVar.query = URLEncoder.encode(query, "UTF-8");
            }
            catch (UnsupportedEncodingException ex)
            {
                ex.printStackTrace();
            }
            Intent in = new Intent(SearchResultsActivity.this, HomeActivity.class);
            in.putExtra("case", 7);
            startActivity(in);
        }
           // Toast.makeText(this, query, Toast.LENGTH_LONG).show();
        }

        finish();
    }
}
