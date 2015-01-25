package com.mozan;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
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
      try
      {
        HttpAsyncTask task = new HttpAsyncTask();
        task.execute(ApiHelper.SEND_POST_URL);
      }
      catch (Exception ex)
      {
          if(ex != null)
          Toast.makeText(AddPostFragment.ctx, ex.getMessage(), Toast.LENGTH_SHORT).show();
      }
      finish();
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {

        ProgressDialog progdialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progdialog = ProgressDialog.show(AddPostFragment.ctx, "","Загрузка...", true);
            progdialog.show();
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
                    String url = ApiHelper.POST_URL + GlobalVar._Post.getId() + "/images/";
                    for (int i = 0; i <length; i++)
                    {
                        jobj = api.sendImage(url, GlobalVar.image_paths.get(i), true);
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
            progdialog.dismiss();
            if(!result.equals(""))
            {
                Toast.makeText(AddPostFragment.ctx, result, Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(AddPostFragment.ctx, "Сохранено", Toast.LENGTH_SHORT).show();
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
