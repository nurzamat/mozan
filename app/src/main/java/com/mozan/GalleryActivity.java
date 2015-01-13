package com.mozan;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import com.mozan.util.AppConstant;
import com.mozan.util.GlobalVar;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class GalleryActivity extends FragmentActivity {

    private GridView gridView;
    private GalleryGridviewAdapter customGridAdapter;
    private int count;
    private Bitmap[] thumbnails;
    private boolean[] thumbnailsselection;
    private String[] arrPath;
    private Utils utils;
    private int columnWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        utils = new Utils(this);
        gridView = (GridView) findViewById(R.id.gridView);
        InitilizeGridLayout();
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
                GlobalVar.image_paths.clear();
                for (int i =0; i<len; i++)
                {
                    if (thumbnailsselection[i]){
                        cnt++;
                        GlobalVar._bitmaps.add(thumbnails[i]);
                        GlobalVar.image_paths.add(arrPath[i]);
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

    private void InitilizeGridLayout() {
        Resources r = getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                AppConstant.GRID_PADDING, r.getDisplayMetrics());

        columnWidth = (int) ((utils.getScreenWidth() - ((AppConstant.NUM_OF_COLUMNS + 1) * padding)) / AppConstant.NUM_OF_COLUMNS);

        gridView.setNumColumns(AppConstant.NUM_OF_COLUMNS);
        gridView.setColumnWidth(columnWidth);
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setPadding((int) padding, (int) padding, (int) padding,
                (int) padding);
        gridView.setHorizontalSpacing((int) padding);
        gridView.setVerticalSpacing((int) padding);
    }

    private ArrayList getData() {
        final ArrayList imageItems = new ArrayList();

        final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        Cursor imagecursor = getContentResolver().query(
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
            //BitmapFactory.Options options = new BitmapFactory.Options();
            //options.inJustDecodeBounds = false;
            //options.inPreferredConfig = Bitmap.Config.RGB_565;
            //options.inDither = true;
            //thumbnails[i] = BitmapFactory.decodeFile(imagecursor.getString(dataColumnIndex), options);
            //thumbnails[i] = MediaStore.Images.Thumbnails.getThumbnail(getApplicationContext().getContentResolver(), id, MediaStore.Images.Thumbnails.MINI_KIND, null);

            thumbnails[i] = utils.decodeFile(imagecursor.getString(dataColumnIndex), columnWidth, columnWidth);
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
            holder.imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
            //holder.imageview.setLayoutParams(new GridView.LayoutParams(columnWidth, columnWidth));

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

    public class Utils {

        private Context _context;

        // constructor
        public Utils(Context context) {
            this._context = context;
        }

        // Reading file paths from SDCard
        public ArrayList<String> getFilePaths() {
            ArrayList<String> filePaths = new ArrayList<String>();

            final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };

            final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
            Cursor imagecursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);

            for (int i = 0; i < imagecursor.getCount(); i++) {
                imagecursor.moveToPosition(i);
                int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
                filePaths.add(imagecursor.getString(dataColumnIndex));
            }
            imagecursor.close();

            return filePaths;
        }

        /*
         * getting screen width
         */
        public int getScreenWidth() {
            int columnWidth;
            WindowManager wm = (WindowManager) _context
                    .getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();

            final Point point = new Point();
            try {
                display.getSize(point);
            } catch (java.lang.NoSuchMethodError ignore) { // Older device
                point.x = display.getWidth();
                point.y = display.getHeight();
            }
            columnWidth = point.x;
            return columnWidth;
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
    }
}
