package com.mozan.util;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nurzamat on 2/5/15.
 */
public class qbGetMessages extends JsonRequest<JSONObject> {

    public qbGetMessages(String url, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, null, listener, errorListener);
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String header = HttpHeaderParser.parseCharset(response.headers);
            Log.d("JsonObjectLog:", header);
            // String jsonString = new String(response.data, header);
            String jsonString = new String(response.data, "UTF-8");
            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String>  params = new HashMap<String, String>();
        //params.put("Accept", "application/json");
        params.put("Content-type", "application/json");
        params.put("QB-Token", GlobalVar.quickbloxToken);
        return params;
    }
}

