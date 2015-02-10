package trade.mozan.util;

import android.graphics.Bitmap;
import android.util.SparseBooleanArray;

import trade.mozan.model.Category;
import trade.mozan.model.Post;
import com.quickblox.chat.model.QBDialog;

import java.util.ArrayList;

/**
 * Created by User on 12.12.2014.
 */
public class GlobalVar {

    public static final String MOZAN = "mozan";               // mozan, mozan_phone, mozan_token are preferences
    public static final String MOZAN_PHONE = "mozan_phone";
    public static final String MOZAN_TOKEN = "mozan_token";
    public static final String MOZAN_UID = "mozan_uid";
    public static final String MOZAN_QID = "mozan_qid";   //for quickblox id
    public static String Phone = "";
    public static String Token = "";
    public static String Uid = "";
    public static String Qid = ""; // quickblox id
    public static ArrayList<Category> _categories = new ArrayList<Category>();
    public static ArrayList<Bitmap> _bitmaps = new ArrayList<Bitmap>();
    public static ArrayList<String> image_paths = new ArrayList<String>();
    public static boolean adv_position;
    public static boolean profile_position;
    public static boolean messages_position;
    public static String query = "";
    public static boolean isHomeFragment;
    public static boolean isCodeSent = false;
    public static SparseBooleanArray mSparseBooleanArray = new SparseBooleanArray();
    public static Post _Post = null;
    public static boolean Mode = true; // true = add mode, false = edit mode
    public static boolean profile_edit = false;
    public static String DisplayedName = "";
    public static Category SelectedCategory = null;
    public static String postContent = "";
    public static String postPrice = "";
    public static String postPriceCurrency = "";
    public static String quickbloxToken = "";
    public static boolean quickbloxLogin = false;
    public static String quickbloxID = "";
    public static ArrayList<QBDialog> quickbloxDialogs = new ArrayList<QBDialog>();
}
