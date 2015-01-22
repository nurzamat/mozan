package com.mozan;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.mozan.R;
import com.mozan.model.Post;
import com.mozan.util.ApiHelper;
import com.mozan.util.GlobalVar;

import org.json.JSONObject;

public class EditImageActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        HttpAsyncTask task = new HttpAsyncTask();
        task.execute(ApiHelper.SEND_POST_URL);
        finish();
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        private ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

          // dialog = ProgressDialog.show(EditImageActivity.this, "", "Загрузка фото...", true);
        }

        @Override
        protected String doInBackground(String... urls) {

            try
            {
                ApiHelper api = new ApiHelper();
                int length = GlobalVar.image_paths.size();
                if(length > 0)
                {
                    JSONObject jobj;
                    for (int i = 0; i <length; i++) {
                        jobj = api.sendImage(GlobalVar._Post.getId(), GlobalVar.image_paths.get(i));
                        if(jobj.has("id"))
                            continue;
                    }
                }
            }
            catch (Exception ex)
            {
                return "Ошибка при загрузке фото";
            }

            return "";
        }

        @Override
        protected void onPostExecute(String result)
        {
//            dialog.dismiss();
            if(!result.equals(""))
            {
                Toast.makeText(EditImageActivity.this, result, Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(EditImageActivity.this, "Сохранено", Toast.LENGTH_SHORT).show();
                Intent in = new Intent(EditImageActivity.this, HomeActivity.class);
                in.putExtra("case", 1);
                startActivity(in);

                //clear images
                GlobalVar._bitmaps.clear();
                GlobalVar.image_paths.clear();
                GlobalVar._Post = null;
            }
        }
    }
}
