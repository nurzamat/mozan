package com.mozan.util;

import android.util.Log;

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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;

/**
 * Created by User on 12.12.2014.
 */
public class ApiHelper {

    public static final String TAG = "[API]";
    public static final String CODE_URL = "https://mozan.market/api/auth/registration/";
    public static final String TOKEN_URL = "https://mozan.market/api/auth/registration/";
    public static final String POSTS_URL = "https://mozan.market/api/post/list/";
    public static final String IMAGES_URL = "https://mozan.market/api/image/list/";
    public static final String MEDIA_URL = "https://mozan.market/media/";
    public static final String API_KEY = "7dbe69719ab6a99e677f4a1948b6c5b82162c40c";

    public JSONObject getCode(String phone) throws ApiException, IOException,
            JSONException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("phone", phone);
        jsonObject.put("api_key", API_KEY);

        Log.i(TAG, "Sending request to: " + CODE_URL);
        //String response = POST(CODE_URL, jsonObject); //for http request
        HttpResponse response = request(CODE_URL, jsonObject); //for https request

        String responseStr = responseToStr(response);
        Log.i(TAG, "Response: " + responseStr);
        return new JSONObject(responseStr);
    }

    public  JSONObject getToken(String phone, String code)
            throws ApiException, IOException, JSONException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("phone", phone);
        jsonObject.put("sms_code", code);
        jsonObject.put("api_key", API_KEY);

        Log.i(TAG, "Sending request to: " + TOKEN_URL);
        HttpResponse response = request(TOKEN_URL, jsonObject);

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

    public HttpResponse request(String url, JSONObject request)
            throws IOException, IllegalStateException,
            JSONException {

        DefaultHttpClient client = (DefaultHttpClient) getNewHttpClient();

        HttpPost post = new HttpPost(url);
        StringEntity se = new StringEntity(request.toString());
        post.setEntity(se);
        post.setHeader("Accept", "application/json");
        post.setHeader("Content-type", "application/json");


        HttpResponse response = client.execute(post);
        return response;
    }
}


