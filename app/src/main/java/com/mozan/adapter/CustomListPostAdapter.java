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
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        thumbNail.setOnClickListener(new OnImageClickListener(position, m.getId(), m.getImageUrls()));

        return convertView;
    }

    class OnImageClickListener implements View.OnClickListener {

        int _position;
        String _id;
        ArrayList<String> _image_urls;

        // constructor
        public OnImageClickListener(int position, String id, ArrayList<String> _image_urls)
        {
            this._position = position;
            this._id = id;
            this._image_urls = _image_urls;
        }

        @Override
        public void onClick(View v) {
            // on selecting grid view image
            // launch full screen activity

            Intent i = new Intent(activity, FullScreenViewActivity.class);
            i.putExtra("position", _position);
            i.putExtra("id", _id);
            i.putExtra("image_urls", _image_urls);
            activity.startActivity(i);

            //Toast.makeText(activity, "pos: " + _position + "post id:" + _id, Toast.LENGTH_LONG).show();
        }

    }

}
