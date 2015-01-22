package com.mozan.adapter;

/**
 * Created by User on 16.12.2014.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.mozan.AppController;
import com.mozan.DeletePostActivity;
import com.mozan.FullScreenViewActivity;
import com.mozan.HomeActivity;
import com.mozan.R;
import com.mozan.model.Image;
import com.mozan.model.Post;
import com.mozan.util.ApiHelper;
import com.mozan.util.GlobalVar;
import java.util.ArrayList;
import java.util.List;

public class MyPostListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Post> postItems;
    //views
    private int thumbnail_id;
    private int menu_id;
    private Fragment fragment_base;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public MyPostListAdapter(Activity activity, Fragment fragment, List<Post> postItems) {
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
        TextView category_name = (TextView) convertView.findViewById(R.id.category);
        TextView price = (TextView) convertView.findViewById(R.id.price);
        ImageButton menu = (ImageButton) convertView.findViewById(R.id.btnMenu);

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
        category_name.setText(m.getCategoryName());
        // price
        price.setText(String.valueOf(m.getPrice()));

        // image view click listener
        thumbnail_id = thumbNail.getId();
        menu_id = menu.getId();

        thumbNail.setOnClickListener(new OnImageClickListener(thumbnail_id, position, m));
        menu.setOnClickListener(new OnImageClickListener(menu_id, position, m));

        return convertView;
    }

    public void deleteItem(int position) {
        postItems.remove(position);
    }

    class OnImageClickListener implements View.OnClickListener {

        int _position;
        int _view_id;
        Post _m;

        // constructor
        public OnImageClickListener(int view_id, int position, Post m)
        {
            this._view_id = view_id;
            this._position = position;
            this._m = m;
        }

        @Override
        public void onClick(View v) {

            // on selecting grid view image
            // launch full screen activity
            if(_view_id == thumbnail_id)
            {
                if(_m.getImages() != null && _m.getImages().size() > 0)
                {
                    Intent i = new Intent(activity, FullScreenViewActivity.class);
                    GlobalVar._Post = _m;
                    activity.startActivity(i);
                }
                else Toast.makeText(activity, R.string.no_photo, Toast.LENGTH_SHORT).show();
            }
            if(_view_id == menu_id)
            {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(activity, v);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_menu1, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        String title = item.getTitle().toString();
                        if(title.equals("Редактировать"))
                        {
                            editPost();
                        }
                        if(title.equals("Удалить"))
                        {
                            deletePost();
                        }
                        return true;
                    }
                });
                popup.show();//showing popup menu
            }
        }

        public void deletePost()
        {
            //Toast.makeText(activity, "delete pressed", Toast.LENGTH_SHORT).show();

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);

            // Setting Dialog Title
            alertDialog.setTitle("Удаление");

            // Setting Dialog Message
            alertDialog.setMessage("Вы действительно хотите удалить обьявление?");

            // Setting Icon to Dialog
            alertDialog.setIcon(R.drawable.ic_menu_delete);

            // Setting Positive "Yes" Button
            alertDialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int which) {

                    // Write your code here to invoke YES event
                    //Toast.makeText(activity, "You clicked on YES", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(activity, DeletePostActivity.class);
                    i.putExtra("position", _position);
                    i.putExtra("id", _m.getId());
                    activity.startActivity(i);
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
        public void editPost()
        {
            GlobalVar._Post = _m;
            GlobalVar.Mode = false;
            GlobalVar._bitmaps.clear();
            GlobalVar.image_paths.clear();
            GlobalVar.mSparseBooleanArray.clear();

            Intent in = new Intent(activity, HomeActivity.class);
            in.putExtra("case", 6);
            activity.startActivity(in);
        }
    }

}
