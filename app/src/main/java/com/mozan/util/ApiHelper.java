package com.mozan.util;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;

/**
 * Created by User on 12.12.2014.
 */
public class ApiHelper {

    public static final String TAG = "[API]";
    public static final String CODE_URL = "https://mozan.trade/api/auth/registration/";
    public static final String TOKEN_URL = "https://mozan.trade/api/auth/registration/";
    public static final String MEDIA_URL = "https://mozan.trade/media/";
    public static final String API_KEY = "7dbe69719ab6a99e677f4a1948b6c5b82162c40c";
    public static final String POST_URL = "https://mozan.trade/api/post/";
    public static final String SEND_POST_URL = "https://mozan.trade/api/post/create/";
    public static final String SEARCH_POST_URL = "https://mozan.trade/api/search/?q=";
    //Categories
    public static final String REALTY_URL = "https://mozan.trade/api/category/1/";
    public static final String AVTO_URL = "https://mozan.trade/api/category/2/";
    public static final String RENT_URL = "https://mozan.trade/api/category/3/";
    public static final String HOUSE_HOLDER_URL = "https://mozan.trade/api/category/4/";
    public static final String CARS_URL = "https://mozan.trade/api/category/5/";
    public static final String ELECTRONICS_URL = "https://mozan.trade/api/category/6/";
    public static final String BUILDING_URL = "https://mozan.trade/api/category/7/";
    public static final String SERVICE_PARTS_URL = "https://mozan.trade/api/category/8/";
    public static final String REST_URL = "https://mozan.trade/api/category/9/";

    public JSONObject getCode(String phone) throws ApiException, IOException,
            JSONException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("phone", phone);
        jsonObject.put("sms_code", "");
        jsonObject.put("api_key", API_KEY);

        Log.i(TAG, "Sending request to: " + CODE_URL);
        //String response = POST(CODE_URL, jsonObject); //for http request
        HttpResponse response = request(CODE_URL, jsonObject, false); //for https request
        Log.i(TAG, "Response: " + response);
        String responseStr = responseToStr(response);
        Log.i(TAG, "ResponseStr: " + responseStr);
        return new JSONObject(responseStr);
    }

    public  JSONObject getToken(String phone, String code)
            throws ApiException, IOException, JSONException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("phone", phone);
        jsonObject.put("sms_code", code);
        jsonObject.put("api_key", API_KEY);

        Log.i(TAG, "Sending request to: " + TOKEN_URL);
        HttpResponse response = request(TOKEN_URL, jsonObject, false);

        String responseStr = responseToStr(response);

        Log.i(TAG, "Response: " + responseStr);
        return new JSONObject(responseStr);
    }

    public JSONObject sendPost(JSONObject jsonObject)
            throws ApiException, IOException, JSONException {

        Log.i(TAG, "Sending request to: " + SEND_POST_URL);
        HttpResponse response = request(SEND_POST_URL, jsonObject, true);
       //HttpResponse response = multipart_request(SEND_POST_URL);

        String responseStr = responseToStr(response);

        Log.i(TAG, "Response: " + responseStr);
        return new JSONObject(responseStr);
    }

    public JSONObject sendImage(String id, String image_path)
            throws ApiException, IOException, JSONException {

        Log.i(TAG, "Image path : " + image_path);
        String url = POST_URL + id + "/images/";

        Log.i(TAG, "Sending request to: " + url);
        HttpResponse response = multipart_request(url, image_path);

        String responseStr = responseToStr(response);

        Log.i(TAG, "Response: " + responseStr);
        return new JSONObject(responseStr);
    }

    public static String responseToStr(HttpResponse response) throws IOException
    {
        return EntityUtils.toString(response.getEntity());
    }
    public static class ApiException extends Exception {

        public ApiException(String detailMessage) {
            super(detailMessage);
        }
    }

    public static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

    public static String POST(String url, JSONObject jsonObject){
        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json = jsonObject.toString();

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        return result;
    }

    public  HttpClient getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(
                    SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    public HttpResponse request(String url, JSONObject json, boolean token_auth)
            throws IOException, IllegalStateException,
            JSONException {

        DefaultHttpClient client = (DefaultHttpClient) getNewHttpClient();

        HttpPost post = new HttpPost(url);
        StringEntity se = new StringEntity(json.toString(), HTTP.UTF_8);
        post.setEntity(se);
        post.setHeader("Accept", "application/json");
        post.setHeader("Content-type", "application/json");
        if(token_auth)
        post.setHeader("Authorization", "Token " + GlobalVar.Token);

        HttpResponse response = client.execute(post);
        return response;
    }

    public HttpResponse multipart_request(String url, String path) {

        DefaultHttpClient client = (DefaultHttpClient) getNewHttpClient();
        try {

            HttpPost httppost = new HttpPost(url);

            FileBody bin = new FileBody(new File(path));
            //StringBody comment = new StringBody("A binary file of some kind", ContentType.TEXT_PLAIN);

            //for sending bitmap
            /*
            Bitmap bitmap = GlobalVar._bitmaps.get(0);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byte[] data = bos.toByteArray();
            ByteArrayBody bab = new ByteArrayBody(data, "nokia.jpg");
            */
            //end bitmap
            HttpEntity reqEntity = MultipartEntityBuilder.create()
                    .addPart("original_image", bin)
                    //.addPart("comment", comment)
                    //.addPart("original_image",bab)   //for sending bitmap
                    .build();

            httppost.setEntity(reqEntity);
            httppost.setHeader("Authorization", "Token " + GlobalVar.Token);

            Log.d("executing request", httppost.getRequestLine().toString());

            HttpResponse response = client.execute(httppost);
            return response;
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    public String responseText(String status)
    {
        if(status.equals("ACTIVATION_CODE_SENT"))
            return "Вам отправлен SMS с Вашим кодом.";
        if(status.equals("CODE_IS_USED"))
            return "Активация c этим кодом уже производилась.";
        if(status.equals("WRONG_ACTIVATION_CODE"))
            return "Неверный код активации.";
        if(status.equals("WRONG_API_KEY"))
            return "Неверный ключ API.";
        if(status.equals("ACTIVATION_PERIOD_EXPIRED"))
            return "Истек период активации.";
        if(status.equals("LOGIN_ERROR"))
            return "При входе возникла ошибка.";
        if(status.equals("USER_ALREADY_EXISTS"))
            return "Пользователь с таким номером телефона уже зарегистрирован.";
        if(status.equals("SEND_MESSAGE_ERROR"))
            return "Ошибка при попытке отправки сообщения.";
        if(status.equals("ACCOUNT_ACTIVATED"))
            return "Ваш аккаунт был активирован. Спасибо, за регистрацию.";

        return "";
    }

    public String getCategoryId(int position)
    {
        if(position == 0)
            return "6";
        if(position == 1)
            return "7";
        if(position == 2)
            return "1";
        if(position == 3)
            return "8";
        if(position == 4)
            return "2";
        if(position == 5)
            return "9";

        return "";
    }
}


