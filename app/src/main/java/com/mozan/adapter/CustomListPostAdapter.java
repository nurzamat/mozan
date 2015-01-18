package com.mozan.adapter;

/**
 * Created by User on 16.12.2014.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.mozan.AddPostFragment;
import com.mozan.AppController;
import com.mozan.DeletePostActivity;
import com.mozan.FullScreenViewActivity;
import com.mozan.R;
import com.mozan.model.Post;
import com.mozan.util.GlobalVar;
import java.util.ArrayList;
import java.util.List;

public class CustomListPostAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Post> postItems;
    //views
    private int thumbnail_id;
    private int edit_id;
    private int delete_id;
    private Fragment fragment_base;

    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CustomListPostAdapter(Activity activity, Fragment fragment, List<Post> postItems) {
        this.activity = activity;
        this.postItems = postItems;
        this.fragment_base = fragment;
    }

    @Override
    public int getCount() {
        return postItems.size();
    }

    @Override
    public Object getItem(int location) {
        return postItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row_post, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) convertView
                .findViewById(R.id.thumbnail);
        TextView content = (TextView) convertView.findViewById(R.id.content);
        TextView username = (TextView) convertView.findViewById(R.id.username);
        TextView category = (TextView) convertView.findViewById(R.id.category);
        TextView price = (TextView) convertView.findViewById(R.id.price);
        ImageButton edit = (ImageButton) convertView.findViewById(R.id.btnEdit);
        ImageButton delete = (ImageButton) convertView.findViewById(R.id.btnDelete);

        // getting post data for the row
        Post m = postItems.get(position);
        String image_url = m.getThumbnailUrl();
        // thumbnail image
        thumbNail.setDefaultImageResId(R.drawable.default_img);
        thumbNail.setImageUrl(image_url, imageLoader);
        // title
        content.setText(m.getContent());

        // username
        username.setText("Username: " + String.valueOf(m.getUsername()));
        category.setText(m.getCategory());
        // price
        price.setText(String.valueOf(m.getPrice()));

        // image view click listener
        thumbnail_id = thumbNail.getId();
        edit_id = edit.getId();
        delete_id = delete.getId();
        thumbNail.setOnClickListener(new OnImageClickListener(thumbnail_id, position, m.getId(), m.getImageUrls()));
        edit.setOnClickListener(new OnImageClickListener(edit_id));
        delete.setOnClickListener(new OnImageClickListener(delete_id, position, m.getId()));

        return convertView;
    }

    public void deleteItem(int position) {
        postItems.remove(position);
    }

    class OnImageClickListener implements View.OnClickListener {

        int _position;
        String _id;
        ArrayList<String> _image_urls = null;
        int _view_id = 0;
        // constructors

        public OnImageClickListener(int view_id)
        {
            this._view_id = view_id;
        }
        public OnImageClickListener(int view_id, int position, String id)
        {
            this._view_id = view_id;
            this._position = position;
            this._id = id;
        }
        public OnImageClickListener(int view_id, int position, String id, ArrayList<String> _image_urls)
        {
            this._view_id = view_id;
            this._position = position;
            this._id = id;
            this._image_urls = _image_urls;
        }

        @Override
        public void onClick(View v) {

            // on selecting grid view image
            // launch full screen activity
            if(_view_id == thumbnail_id)
            {
                if(_image_urls != null && _image_urls.size() > 0)
                {
                    Intent i = new Intent(activity, FullScreenViewActivity.class);
                    i.putExtra("position", _position);
                    i.putExtra("id", _id);
                    i.putExtra("image_urls", _image_urls);
                    activity.startActivity(i);
                }
                else Toast.makeText(activity, R.string.no_photo, Toast.LENGTH_SHORT).show();
            }
            if(_view_id == edit_id)
            {
               //Toast.makeText(activity, "edit pressed", Toast.LENGTH_SHORT).show();
                editPost();
            }
            if(_view_id == delete_id)
            {
                //Toast.makeText(activity, "delete pressed", Toast.LENGTH_SHORT).show();

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);

                // Setting Dialog Title
                alertDialog.setTitle("Удаление");

                // Setting Dialog Message
                alertDialog.setMessage("Вы действительно хотите удалить обьявление?");

                // Setting Icon to Dialog
                alertDialog.setIcon(R.drawable.ic_action_delete);

                // Setting Positive "Yes" Button
                alertDialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {

                        // Write your code here to invoke YES event
                        //Toast.makeText(activity, "You clicked on YES", Toast.LENGTH_SHORT).show();
                        deletePost();
                    }
                });

                // Setting Negative "NO" Button
                alertDialog.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to invoke NO event
                        //Toast.makeText(activity, "You clicked on NO", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });

                // Showing Alert Message
                alertDialog.show();

            }
            //Toast.makeText(activity, "pos: " + _position + "post id:" + _id, Toast.LENGTH_LONG).show();
        }

        public void deletePost()
        {
            Intent i = new Intent(activity, DeletePostActivity.class);
            i.putExtra("position", _position);
            i.putExtra("id", _id);
            activity.startActivity(i);
        }
        public void editPost()
        {
            Bundle bundle = new Bundle();
            bundle.putInt("id", R.drawable.camera);
            bundle.putString("paths", "");
            Fragment fragment = (Fragment) new AddPostFragment();
            fragment.setArguments(bundle);
            if (fragment != null) {
                FragmentManager fragmentManager = fragment_base.getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, fragment).commit();

                GlobalVar.isHomeFragment = false;

            } else {
                // error in creating fragment
                Log.e("MyPosts Adapter", "Error in creating fragment");
            }
        }
    }

}
