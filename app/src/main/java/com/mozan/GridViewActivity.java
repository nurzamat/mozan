package com.mozan;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Point;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;
import android.widget.GridView;
import com.mozan.adapter.GridViewImageAdapter;
import com.mozan.util.AppConstant;

import java.util.ArrayList;

public class GridViewActivity extends Activity {

    private Utils utils;
    private ArrayList<String> imagePaths = new ArrayList<String>();
    private GridViewImageAdapter adapter;
    private GridView gridView;
    private int columnWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gridview);

        gridView = (GridView) findViewById(R.id.grid_view);

        utils = new Utils(this);

        // Initilizing Grid View
        InitilizeGridLayout();

        // loading all image paths from SD card
        imagePaths = utils.getFilePaths();

        // Gridview adapter
        adapter = new GridViewImageAdapter(GridViewActivity.this, imagePaths,
                columnWidth);

        // setting grid view adapter
        gridView.setAdapter(adapter);
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
            Cursor imagecursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);

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
    }
}
