package trade.mozan;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import trade.mozan.util.ApiHelper;
import trade.mozan.util.DeleteRequest;

public class DeletePostActivity extends Activity {

    // Log tag
    private static final String TAG =  "[delete post response]";
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_delete_post);
        Intent i = getIntent();
        final int position = i.getIntExtra("position", 0);
        String id = i.getStringExtra("id");

        pDialog = new ProgressDialog(DeletePostActivity.this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Загрузка...");
        pDialog.show();
        String url = ApiHelper.POST_URL + id + "/";
        finish();
        DeleteRequest dr = new DeleteRequest(url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d(TAG, response);
                        if(response.equals("")) // if response is ok
                        {
                            MyPostsFragment.adapter.deleteItem(position);
                            MyPostsFragment.adapter.notifyDataSetChanged();
                            Toast.makeText(DeletePostActivity.this, "Удалено", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        // hide the progress dialog
                    }
                }
        );
        hidePDialog();
        // Adding request to request queue
        AppController appcon = AppController.getInstance();
        appcon.addToRequestQueue(dr);
        finish();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }
}
