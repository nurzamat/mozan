package com.mozan.util;

import android.graphics.Bitmap;
import android.util.SparseBooleanArray;
import com.mozan.model.Post;
import java.util.ArrayList;

/**
 * Created by User on 12.12.2014.
 */
public class GlobalVar {

    public static final String MOZAN = "mozan";               // mozan, mozan_phone, mozan_token are preferences
    public static final String MOZAN_PHONE = "mozan_phone";
    public static final String MOZAN_TOKEN = "mozan_token";
    public static final String MOZAN_UID = "mozan_uid";
    public static String Phone = "";
    public static String Token = "";
    public static String Uid = "";
    public static ArrayList<Bitmap> _bitmaps = new ArrayList<Bitmap>();
    public static ArrayList<String> image_paths = new ArrayList<String>();
    public static boolean adv_position;
    public static String query = "";
    public static boolean isHomeFragment;
    public static SparseBooleanArray mSparseBooleanArray = new SparseBooleanArray();
    public static Post _Post;
    public static boolean Mode; // add = true, edit = false
}
