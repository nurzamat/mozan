package com.mozan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;

import com.mozan.adapter.PlaceSlidesFragmentAdapter;
import com.mozan.util.AppConstant;
import com.mozan.util.GlobalVar;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created by nurzamat on 1/15/15.
 */

public class MultiPhotoSelectActivity extends Activity {
    private ArrayList<String> imageUrls;
    private DisplayImageOptions options;
    private ImageAdapter imageAdapter;
    private ImageLoader imageLoader;
    private int columnWidth;
    private int screenWidth;
    private GridView gridView;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//image loader
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .memoryCacheSize(1500000) // 1.5 Mb
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .build();
        // Initialize ImageLoader with configuration.

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
//
        setContentView(R.layout.gallery_gridview);
        gridView = (GridView) findViewById(R.id.gridview);
        InitilizeGridLayout();
        final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        Cursor imagecursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, orderBy + " DESC");
        this.imageUrls = new ArrayList<String>();
        imageUrls.add("");
        for (int i = 0; i < imagecursor.getCount(); i++) {
            imagecursor.moveToPosition(i);
            int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
            imageUrls.add(imagecursor.getString(dataColumnIndex));

        }

        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.default_img)
                .showImageForEmptyUri(R.drawable.default_img)
                .cacheInMemory()
                .cacheOnDisc()
                .build();
        imageAdapter = new ImageAdapter(this, imageUrls);
        gridView.setAdapter(imageAdapter);
        /*
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> parent, View view, int position, long id)
          {
           // startImageGalleryActivity(position);
          }
          });
          */

    }

    @Override
    protected void onStop() {
        imageLoader.stop();
        super.onStop();
    }
    public void btnChoosePhotosClick(View v){
        ArrayList<String> selectedItems = imageAdapter.getCheckedItems();
        //Toast.makeText(MultiPhotoSelectActivity.this, "Total photos selected:: " + selectedItems.size(), Toast.LENGTH_SHORT).show();
        Log.d(MultiPhotoSelectActivity.class.getSimpleName(), "Selected Items: " + selectedItems.toString());

        final int len = selectedItems.size();
        int cnt = 0;
        String selectImages = "";
        GlobalVar._bitmaps.clear();
        GlobalVar.image_paths.clear();
        for (int i =0; i<len; i++)
        {
            cnt++;
            if(i < AppConstant.MAX_IMAGES){

                String path = selectedItems.get(i);
                GlobalVar._bitmaps.add(decodeFile(path, columnWidth, columnWidth));
                GlobalVar.image_paths.add(path);
            }
        }
        if (cnt == 0){
            Toast.makeText(getApplicationContext(),
                    "Please select at least one image",
                    Toast.LENGTH_LONG).show();
        } else {

            Log.d("SelectedImages", selectImages);

            Intent in = new Intent(MultiPhotoSelectActivity.this, HomeActivity.class);
            if(GlobalVar.profile_edit)
            in.putExtra("case", 2); //profile
            else in.putExtra("case", 6); //post
            startActivity(in);
        }
    }
 /*private void startImageGalleryActivity(int position) {
  Intent intent = new Intent(this, ImagePagerActivity.class);
  intent.putExtra(Extra.IMAGES, imageUrls);
  intent.putExtra(Extra.IMAGE_POSITION, position);
  startActivity(intent);
 }*/
 private void InitilizeGridLayout() {
     Resources r = getResources();
     float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
             AppConstant.GRID_PADDING, r.getDisplayMetrics());

     DisplayMetrics metrics = new DisplayMetrics();
     getWindowManager().getDefaultDisplay().getMetrics(metrics);
     screenWidth = metrics.widthPixels;

     columnWidth = (int) ((screenWidth - ((3 + 1) * padding)) / 3);
     gridView.setNumColumns(3);
     gridView.setColumnWidth(columnWidth);
     gridView.setStretchMode(GridView.NO_STRETCH);
     gridView.setPadding((int) padding, (int) padding, (int) padding,
             (int) padding);
     gridView.setHorizontalSpacing((int) padding);
     gridView.setVerticalSpacing((int) padding);
 }

    public class ImageAdapter extends BaseAdapter {
        ArrayList<String> mList;

        LayoutInflater mInflater;

        Context mContext;

        //SparseBooleanArray mSparseBooleanArray;

        public ImageAdapter(Context context, ArrayList<String> imageList) {
            // TODO Auto-generated constructor stub
            mContext = context;
            mInflater = LayoutInflater.from(mContext);
           // mSparseBooleanArray = new SparseBooleanArray();
            mList = new ArrayList<String>();
            this.mList = imageList;
        }
        public ArrayList<String> getCheckedItems() {

            ArrayList<String> mTempArry = new ArrayList<String>();
            for(int i=0;i<mList.size();i++) {

                if(GlobalVar.mSparseBooleanArray.get(i)) {

                    mTempArry.add(mList.get(i));
                }

            }

            return mTempArry;
        }

        @Override
        public int getCount() {
            return imageUrls.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = mInflater.inflate(R.layout.row_multiphoto_item, null);
            }
            CheckBox mCheckBox = (CheckBox) convertView.findViewById(R.id.checkBox1);
            final ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView1);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setTag(position);
            mCheckBox.setTag(position);

            if(position == 0)
                imageView.setImageResource(R.drawable.camera59);
            else
            {
                imageLoader.displayImage("file://"+imageUrls.get(position), imageView, options, new SimpleImageLoadingListener()
                {
                /*
                @Override
                public void onLoadingComplete(Bitmap loadedImage) {
                    Animation anim = AnimationUtils.loadAnimation(MultiPhotoSelectActivity.this, 0); // R.anim.fade_in
                    imageView.setAnimation(anim);
                    anim.start();
                }
                */
                });
            }

            if(mCheckBox.getTag().equals(0))
                mCheckBox.setVisibility(View.INVISIBLE);
            else
            {
                mCheckBox.setVisibility(View.VISIBLE);
                mCheckBox.setChecked(GlobalVar.mSparseBooleanArray.get(position));
                mCheckBox.setOnCheckedChangeListener(mCheckedChangeListener);
            }

            imageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v)
                {
                    if(v.getTag().equals(0))
                    {
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                    }
                }
            });

            return convertView;
        }
        OnCheckedChangeListener mCheckedChangeListener = new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                GlobalVar.mSparseBooleanArray.put((Integer) buttonView.getTag(), isChecked);
            }
        };
    }

    public Bitmap decodeFile(String filePath, int WIDTH, int HIGHT) {
        try {

            File f = new File(filePath);

            BitmapFactory.Options o = new BitmapFactory.Options();
            // o.inJustDecodeBounds = true;
            o.inJustDecodeBounds = false;
            o.inPreferredConfig = Bitmap.Config.RGB_565;
            o.inDither = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            final int REQUIRED_WIDTH = WIDTH;
            final int REQUIRED_HIGHT = HIGHT;
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_WIDTH
                    && o.outHeight / scale / 2 >= REQUIRED_HIGHT)
                scale *= 2;

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK)
        {
            Intent in = new Intent(MultiPhotoSelectActivity.this, MultiPhotoSelectActivity.class);
            MultiPhotoSelectActivity.this.startActivity(in);
            finish();
        }
         */
        try
        {
            GlobalVar.mSparseBooleanArray.clear();
            Context context = PlaceSlidesFragmentAdapter.context;
            Intent in = new Intent(context, MultiPhotoSelectActivity.class);
            context.startActivity(in);
            finish();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
