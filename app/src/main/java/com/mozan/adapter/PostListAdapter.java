package com.mozan.adapter;

/**
 * Created by User on 16.12.2014.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.mozan.AppController;
import com.mozan.ChatActivity;
import com.mozan.CodeActivity;
import com.mozan.FullScreenViewActivity;
import com.mozan.MyProfileFragment;
import com.mozan.R;
import com.mozan.RegisterActivity;
import com.mozan.SplashActivity;
import com.mozan.model.Post;
import com.mozan.util.ApiHelper;
import com.mozan.util.GlobalVar;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PostListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Post> postItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    //views
    private int thumbnail_id;
    private int menu_id;
    private int call_id;
    private int chat_id;

    public PostListAdapter(Activity activity, List<Post> postItems) {
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
        NetworkImageView avatar = (NetworkImageView) convertView
                .findViewById(R.id.avatar);
        ProgressBar spin = (ProgressBar) convertView.findViewById(R.id.progressBar1);
        spin.setVisibility(View.VISIBLE);

        TextView content = (TextView) convertView.findViewById(R.id.content);
        TextView hitcount = (TextView) convertView.findViewById(R.id.hitcount);
        TextView displayed_name = (TextView) convertView.findViewById(R.id.displayed_name);
        TextView category_name = (TextView) convertView.findViewById(R.id.category);
        TextView price = (TextView) convertView.findViewById(R.id.price);
        ImageButton menu = (ImageButton) convertView.findViewById(R.id.btnMenu2);
        ImageButton call = (ImageButton) convertView.findViewById(R.id.show_phone);
        ImageButton chat = (ImageButton) convertView.findViewById(R.id.show_chat);

        // getting post data for the row
        Post m = postItems.get(position);
        String image_url = m.getThumbnailUrl();
        // thumbnail image
        if(image_url.equals(""))
           thumbNail.setDefaultImageResId(R.drawable.default_img);
        thumbNail.setImageUrl(image_url, imageLoader);
        if(thumbNail.getDrawable() != null)
           spin.setVisibility(View.GONE);

        avatar.setImageUrl(m.getAvatarUrl(), imageLoader);
        // title
        content.setText(m.getContent());
        hitcount.setText(m.getHitcount());
        // username
        if(!m.getDisplayedName().equals(""))
        displayed_name.setText(m.getDisplayedName());
        else displayed_name.setText(m.getUsername());
        category_name.setText(m.getCategoryName());

        // price
        price.setText(String.valueOf(m.getPrice()));

        // image view click listener
        thumbnail_id = thumbNail.getId();
        menu_id = menu.getId();
        call_id = call.getId();
        chat_id = chat.getId();

        thumbNail.setOnClickListener(new OnImageClickListener(thumbnail_id, position, m));
        menu.setOnClickListener(new OnImageClickListener(menu_id, position, m));
        call.setOnClickListener(new OnImageClickListener(call_id, position, m));
        chat.setOnClickListener(new OnImageClickListener(chat_id, position, m));

        return convertView;
    }


    class OnImageClickListener implements View.OnClickListener {

        int _position;
        int _view_id = 0;
        Post _m;

        // constructors
        public OnImageClickListener(int view_id, int _position, Post m)
        {
            this._view_id = view_id;
            this._position = _position;
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
                    GlobalVar._Post = _m;
                    Intent i = new Intent(activity, FullScreenViewActivity.class);
                    activity.startActivity(i);
                }
                else Toast.makeText(activity, R.string.no_photo, Toast.LENGTH_SHORT).show();
            }
            if(_view_id == call_id)
            {
                if (!GlobalVar.Phone.equals("") && !GlobalVar.Token.equals(""))
                {
                    String phone = _m.getUsername();
                    boolean isPhone = PhoneNumberUtils.isGlobalPhoneNumber("+"+phone);
                    if(isPhone)
                    {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:"+"+"+phone));
                        activity.startActivity(intent);
                    }
                    else Toast.makeText(activity, "call pressed /"+phone+"/", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Intent in;
                    if(GlobalVar.isCodeSent)
                        in = new Intent(activity, RegisterActivity.class);
                    else in = new Intent(activity, CodeActivity.class);
                    activity.startActivity(in);
                }
            }
            if(_view_id == chat_id)
            {
                if (!GlobalVar.Phone.equals("") && !GlobalVar.Token.equals(""))
                {
                    GlobalVar._Post = _m;

                    if(!GlobalVar.quickbloxLogin)
                    {
                        Intent in = new Intent(activity, SplashActivity.class);
                        activity.startActivity(in);
                    }
                    else
                    {
                        QBDialog dialog = null;
                        if(GlobalVar.quickbloxDialogs.size() > 0)
                        {
                            for (QBDialog qdialog : GlobalVar.quickbloxDialogs) {

                                if(qdialog.getOccupants().contains(Integer.parseInt(_m.getQuickbloxId())))
                                {
                                    dialog = qdialog;
                                }
                            }
                        }

                        Bundle bundle = new Bundle();
                        bundle.putSerializable(ChatActivity.EXTRA_MODE, ChatActivity.Mode.PRIVATE);
                        bundle.putSerializable(ChatActivity.EXTRA_DIALOG, dialog);

                        ChatActivity.start(activity, bundle);
                    }
                }
                else
                {
                    Intent in;
                    if(GlobalVar.isCodeSent)
                        in = new Intent(activity, RegisterActivity.class);
                    else in = new Intent(activity, CodeActivity.class);
                    activity.startActivity(in);
                }

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
                                intent.setData(Uri.parse("tel:"+"+"+phone));
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
