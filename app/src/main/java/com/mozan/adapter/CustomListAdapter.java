package com.mozan.adapter;

/**
 * Created by User on 16.12.2014.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.PhoneNumberUtils;
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
import com.mozan.FullScreenViewActivity;
import com.mozan.R;
import com.mozan.model.Post;
import java.util.ArrayList;
import java.util.List;

public class CustomListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Post> postItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    //views
    private int thumbnail_id;
    private int message_id;
    private int call_id;
    private int menu_id;
    //phone number
    private String phone;

    public CustomListAdapter(Activity activity, List<Post> postItems) {
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
            convertView = inflater.inflate(R.layout.list_row, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) convertView
                .findViewById(R.id.thumbnail);
        TextView content = (TextView) convertView.findViewById(R.id.content);
        TextView username = (TextView) convertView.findViewById(R.id.username);
        TextView category_name = (TextView) convertView.findViewById(R.id.category);
        TextView price = (TextView) convertView.findViewById(R.id.price);
        //ImageButton message = (ImageButton) convertView.findViewById(R.id.btnMessage);
        //ImageButton call = (ImageButton) convertView.findViewById(R.id.btnCall);
        ImageButton menu = (ImageButton) convertView.findViewById(R.id.btnMenu2);

        // getting post data for the row
        Post m = postItems.get(position);
        String image_url = m.getThumbnailUrl();
        // thumbnail image
        thumbNail.setDefaultImageResId(R.drawable.default_img);
        thumbNail.setImageUrl(image_url, imageLoader);
        // title
        content.setText(m.getContent());

        // username
        phone = m.getUsername();
        username.setText("Username: " + phone);
        category_name.setText(m.getCategoryName());

        // image_urls to string
        /*
        String urlsStr = "";
        for (String str : m.getImageUrls()) {
            urlsStr += str + ", ";
        }
        urlsStr = urlsStr.length() > 0 ? urlsStr.substring(0,
                urlsStr.length() - 2) : urlsStr;
        */
        //

        // price
        price.setText(String.valueOf(m.getPrice()));

        // image view click listener
        thumbnail_id = thumbNail.getId();
       // message_id = message.getId();
       // call_id = call.getId();
        menu_id = menu.getId();

        thumbNail.setOnClickListener(new OnImageClickListener(thumbnail_id, position, m.getId(), m.getImageUrls()));
        //message.setOnClickListener(new OnImageClickListener(message_id));
        //call.setOnClickListener(new OnImageClickListener(call_id));

        menu.setOnClickListener(new OnImageClickListener(menu_id, position, m));

        return convertView;
    }


    class OnImageClickListener implements View.OnClickListener {

        int _position;
        String _id;
        ArrayList<String> _image_urls = null;
        int _view_id = 0;
        Post _m;

        // constructors
        public OnImageClickListener(int view_id)
        {
            this._view_id = view_id;
        }
        public OnImageClickListener(int view_id, int _position, Post m)
        {
            this._view_id = view_id;
            this._position = _position;
            this._m = m;
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
            if(_view_id == message_id)
            {
                Toast.makeText(activity, "message pressed", Toast.LENGTH_SHORT).show();
            }
            if(_view_id == call_id)
            {
                boolean isPhone = PhoneNumberUtils.isGlobalPhoneNumber("+"+phone);
                if(isPhone)
                {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:"+phone));
                    activity.startActivity(intent);
                }
                else Toast.makeText(activity, "call pressed /"+phone+"/", Toast.LENGTH_SHORT).show();
            }
            if(_view_id == menu_id)
            {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(activity, v);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_menu2, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        String title = item.getTitle().toString();
                        if(title.equals("Сообщение"))
                        {
                            Toast.makeText(activity, "message pressed", Toast.LENGTH_SHORT).show();
                        }
                        if(title.equals("Позвонить"))
                        {
                            String phone = _m.getUsername();
                            boolean isPhone = PhoneNumberUtils.isGlobalPhoneNumber("+"+phone);
                            if(isPhone)
                            {
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:"+phone));
                                activity.startActivity(intent);
                            }
                            else Toast.makeText(activity, "call pressed /"+phone+"/", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                });
                popup.show();//showing popup menu

            }
        }

    }

}
