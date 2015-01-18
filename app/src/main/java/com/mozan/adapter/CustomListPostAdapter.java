package com.mozan.adapter;

/**
 * Created by User on 16.12.2014.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.mozan.AppController;
import com.mozan.FullScreenViewActivity;
import com.mozan.R;
import com.mozan.model.Post;

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

    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CustomListPostAdapter(Activity activity, List<Post> postItems) {
        this.activity = activity;
        this.postItems = postItems;
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
        delete.setOnClickListener(new OnImageClickListener(delete_id));

        return convertView;
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
               Toast.makeText(activity, "edit pressed", Toast.LENGTH_SHORT).show();
            }
            if(_view_id == delete_id)
            {
                Toast.makeText(activity, "delete pressed", Toast.LENGTH_SHORT).show();
            }
            //Toast.makeText(activity, "pos: " + _position + "post id:" + _id, Toast.LENGTH_LONG).show();
        }

    }

}
