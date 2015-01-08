package com.mozan;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import com.mozan.util.GlobalVar;

import java.util.ArrayList;

public class GalleryActivity extends FragmentActivity {

    private GridView gridView;
    private GalleryGridviewAdapter customGridAdapter;
    private int count;
    private Bitmap[] thumbnails;
    private boolean[] thumbnailsselection;
    private String[] arrPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        gridView = (GridView) findViewById(R.id.gridView);
        getData();
        customGridAdapter = new GalleryGridviewAdapter();
        gridView.setAdapter(customGridAdapter);

        final Button selectBtn = (Button) findViewById(R.id.button1);
        selectBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                final int len = thumbnailsselection.length;
                int cnt = 0;
                String selectImages = "";
                GlobalVar._bitmaps.clear();
                for (int i =0; i<len; i++)
                {
                    if (thumbnailsselection[i]){
                        cnt++;
                        GlobalVar._bitmaps.add(thumbnails[i]);
                    }
                }
                if (cnt == 0){
                    Toast.makeText(getApplicationContext(),
                            "Please select at least one image",
                            Toast.LENGTH_LONG).show();
                } else {

                    Log.d("SelectedImages", selectImages);

                    Intent in = new Intent(GalleryActivity.this, HomeActivity.class);
                    in.putExtra("id_resource", 0);
                    in.putExtra("paths", selectImages);
                    startActivity(in);
                }
            }
        });
    }

    private ArrayList getData() {
        final ArrayList imageItems = new ArrayList();

        final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        Cursor imagecursor = managedQuery(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);
        int image_column_index = imagecursor.getColumnIndex(MediaStore.Images.Media._ID);
        this.count = imagecursor.getCount();
        this.thumbnails = new Bitmap[this.count];
        this.thumbnailsselection = new boolean[this.count];
        this.arrPath = new String[this.count];

        for (int i = 0; i < count; i++) {
            imagecursor.moveToPosition(i);
            int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
            int id = imagecursor.getInt(image_column_index);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inDither = true;
            //thumbnails[i] = BitmapFactory.decodeFile(imagecursor.getString(dataColumnIndex), options);
            thumbnails[i] = MediaStore.Images.Thumbnails.getThumbnail(getApplicationContext().getContentResolver(), id,
                    MediaStore.Images.Thumbnails.MINI_KIND, null);
            arrPath[i]= imagecursor.getString(dataColumnIndex);
        }
        imagecursor.close();
        return imageItems;

    }

    public class GalleryGridviewAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public GalleryGridviewAdapter() {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return count;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(
                        R.layout.gallery_gridview_row, null);
                holder.imageview = (ImageView) convertView.findViewById(R.id.image);
                holder.checkbox = (CheckBox) convertView.findViewById(R.id.checkBox1);

                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.checkbox.setId(position);
            holder.imageview.setId(position);
            holder.checkbox.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    OnClick((CheckBox) v);
                }
            });
            holder.imageview.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    /*
                    int id = v.getId();
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse("file://" + arrPath[id]), "image/*");
                    startActivity(intent);
                    */
                    //OnClick((CheckBox) v);
                }
            });
            holder.imageview.setImageBitmap(thumbnails[position]);
            holder.checkbox.setChecked(thumbnailsselection[position]);
            holder.id = position;
            return convertView;
        }

        private void OnClick(CheckBox v) {
            int id = v.getId();
            if (thumbnailsselection[id]){
                v.setChecked(false);
                thumbnailsselection[id] = false;
            } else {
                v.setChecked(true);
                thumbnailsselection[id] = true;
            }
        }
    }

    class ViewHolder {
        ImageView imageview;
        CheckBox checkbox;
        int id;
    }


}
