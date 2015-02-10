package trade.mozan;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.QBSettings;
import com.quickblox.users.model.QBUser;

import trade.mozan.model.Category;
import trade.mozan.util.ApiHelper;
import trade.mozan.util.Constants;
import trade.mozan.util.GlobalVar;
import trade.mozan.util.JsonArrayRequest;
import trade.mozan.util.PlayServicesHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class StartActivity extends Activity {

    private static final String TAG =  "[CATEGORIES response]";
    private ProgressDialog pDialog;
    TextView etText;
    private PlayServicesHelper playServicesHelper;
    private QBChatService chatService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        etText = (TextView) findViewById(R.id.no_internet);

        Intent intent = getIntent();
        if(intent.hasExtra("qid") && !intent.getStringExtra("qid").equals(""))
            GlobalVar.quickbloxID = intent.getStringExtra("qid");

        SharedPreferences sp = this.getSharedPreferences(GlobalVar.MOZAN,0);
        GlobalVar.Phone = sp.getString(GlobalVar.MOZAN_PHONE, "");
        GlobalVar.Token = sp.getString(GlobalVar.MOZAN_TOKEN, "");
        GlobalVar.Uid = sp.getString(GlobalVar.MOZAN_UID, "");
        GlobalVar.Qid = sp.getString(GlobalVar.MOZAN_QID, "");

        Log.d("StartActivity", "Phone/token/uid: " + GlobalVar.Phone  + " / " + GlobalVar.Token + " / " + GlobalVar.Uid);

        if(!ApiHelper.isConnected(StartActivity.this)){
            Toast.makeText(StartActivity.this, "No Internet connection!", Toast.LENGTH_LONG).show();
            //return;
            etText.setText(R.string.connection_problem);
        }
        else
        {
            if(!GlobalVar.quickbloxLogin && !GlobalVar.Phone.equals("") && !GlobalVar.Token.equals(""))
            {
                // Init Chat
                //
                QBChatService.setDebugEnabled(true);
                QBSettings.getInstance().fastConfigInit(Constants.APP_ID, Constants.AUTH_KEY, Constants.AUTH_SECRET);
                if (!QBChatService.isInitialized()) {
                    QBChatService.init(this);
                }
                chatService = QBChatService.getInstance();

                // create QB user
                //
                final QBUser user = new QBUser();
                user.setLogin(GlobalVar.Phone);
                user.setPassword(GlobalVar.Token);
                // TODO: Проверить время действия сессии. Вроде всего на два часа сессия. А тут только при
                // первой регистрации
                QBAuth.createSession(user, new QBEntityCallbackImpl<QBSession>() {
                    @Override
                    public void onSuccess(QBSession session, Bundle args) {
                        // save current user
                        //
                        user.setId(session.getUserId());
                        GlobalVar.quickbloxToken = session.getToken();
                        ((AppController) getApplication()).setCurrentUser(user);

                        // login to Chat
                        //
                        loginToChat(user);

                        // Push Notification registration
                        playServicesHelper = new PlayServicesHelper(StartActivity.this);

                    }

                    @Override
                    public void onError(List<String> errors) {
                        Toast.makeText(StartActivity.this, errors.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            //end messages

            // Use Volley or Async Task
            //VolleyRequest();
            HttpAsyncTask task = new HttpAsyncTask();
            task.execute(ApiHelper.CATEGORIES_URL);
        }
    }

    private void VolleyRequest() {
        pDialog = ProgressDialog.show(StartActivity.this, "", "Загрузка...", true);
        GlobalVar._categories.clear();
        JsonArrayRequest Req = new JsonArrayRequest(ApiHelper.CATEGORIES_URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        // Parsing json array
                        for (int i = 0; i < response.length(); i++) {
                            try
                            {
                                JSONObject obj = response.getJSONObject(i);
                                Category category = new Category();
                                category.setId(obj.getString("id"));
                                category.setName(obj.getString("name"));
                                category.setParent(obj.getString("parent"));

                                GlobalVar._categories.add(category);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        Intent in = new Intent(StartActivity.this, HomeActivity.class);
                        startActivity(in);
                        finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });
        // Adding request to request queue
        AppController appcon = AppController.getInstance();
        appcon.addToRequestQueue(Req);
        pDialog.dismiss();
    }


    private class HttpAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = ProgressDialog.show(StartActivity.this, "", "Загрузка...", true);
        }

        @Override
        protected String doInBackground(String... urls) {

            try
            {
                GlobalVar._categories.clear();
                ApiHelper api = new ApiHelper();
                JSONArray response = api.getCategories();
                // Parsing json array
                for (int i = 0; i < response.length(); i++)
                {
                    JSONObject obj = response.getJSONObject(i);
                    Category category = new Category();
                    category.setId(obj.getString("id"));
                    category.setName(obj.getString("name"));
                    category.setParent(obj.getString("parent"));

                    GlobalVar._categories.add(category);
                }
            }
            catch (Exception ex)
            {
                String exText = ex.getMessage();
                Log.d("code activity", "Exeption: " + exText);
                return "Ошибка";
            }

            return "";
        }

        @Override
        protected void onPostExecute(String result)
        {
            pDialog.dismiss();
            if(!result.equals(""))
            {
                Toast.makeText(StartActivity.this, result, Toast.LENGTH_SHORT).show();
                etText.setText(R.string.connection_problem);
            }
            else
            {
                if(GlobalVar.quickbloxID.equals(""))
                {
                    Intent in = new Intent(StartActivity.this, HomeActivity.class);
                    startActivity(in);
                }
                finish();
            }
        }
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
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loginToChat(final QBUser user)
    {
        chatService.login(user, new QBEntityCallbackImpl() {
            @Override
            public void onSuccess() {
                // Start sending presences
                //
                try {
                    GlobalVar.quickbloxLogin = true;
                    chatService.startAutoSendPresence(Constants.AUTO_PRESENCE_INTERVAL_IN_SECONDS);

                    if(!GlobalVar.quickbloxID.equals(""))
                    {
                        QBDialog dialog = null;
                        if(GlobalVar.quickbloxDialogs.size() > 0)
                        {
                            for (QBDialog qdialog : GlobalVar.quickbloxDialogs)
                            {

                                if(qdialog.getOccupants().contains(Integer.parseInt(GlobalVar.quickbloxID)))
                                {
                                    dialog = qdialog;
                                }
                            }
                        }

                        Intent in = new Intent(StartActivity.this, ChatActivity.class);
                        in.putExtra(ChatActivity.EXTRA_MODE, ChatActivity.Mode.PRIVATE);
                        in.putExtra(ChatActivity.EXTRA_DIALOG, dialog);
                        startActivity(in);
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(List errors) {
                Toast.makeText(StartActivity.this, errors.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
