package trade.mozan.util;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.HttpStack;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by User on 17.12.2014.
 */
public class SslHttpStack implements HttpStack {

    private boolean mIsConnectingToYourServer = false;
    private final static String HEADER_CONTENT_TYPE = "Content-Type";

    public SslHttpStack(boolean isYourServer) {
        mIsConnectingToYourServer = isYourServer;
    }

    private static void addHeaders(HttpUriRequest httpRequest, Map<String, String> headers) {
        for (String key : headers.keySet()) {
            httpRequest.setHeader(key, headers.get(key));
        }
    }

    @SuppressWarnings("unused")
    private static List<NameValuePair> getPostParameterPairs(Map<String, String> postParams) {
        List<NameValuePair> result = new ArrayList<NameValuePair>(postParams.size());
        for (String key : postParams.keySet()) {
            result.add(new BasicNameValuePair(key, postParams.get(key)));
        }
        return result;
    }

    @Override
    public HttpResponse performRequest(Request<?> request, Map<String, String> additionalHeaders)
            throws IOException, AuthFailureError {
        try
        {
            HttpUriRequest httpRequest = createHttpRequest(request, additionalHeaders);
            addHeaders(httpRequest, additionalHeaders);
            addHeaders(httpRequest, request.getHeaders());
            onPrepareRequest(httpRequest);
            HttpParams httpParams = httpRequest.getParams();
            int timeoutMs = request.getTimeoutMs();
            // TODO: Reevaluate this connection timeout based on more wide-scale
            // data collection and possibly different for wifi vs. 3G.
            HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
            HttpConnectionParams.setSoTimeout(httpParams, timeoutMs);
        /* Register schemes, HTTP and HTTPS */
            SchemeRegistry registry = new SchemeRegistry();
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            registry.register(new Scheme("http", new PlainSocketFactory(), 80));
            registry.register(new Scheme("https", new MySSLSocketFactory(trustStore), 443));

        /* Make a thread safe connection manager for the client */
            ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager(httpParams, registry);
            HttpClient httpClient = new DefaultHttpClient(manager, httpParams);

            return httpClient.execute(httpRequest);
        }
        catch (Exception e)
        {
            Log.d("error", e.getMessage());
            return null;
        }
    }

    /**
     * Creates the appropriate subclass of HttpUriRequest for passed in request.
     */
    @SuppressWarnings("deprecation")
    /* protected */ static HttpUriRequest createHttpRequest(Request<?> request,
                                                            Map<String, String> additionalHeaders) throws AuthFailureError {
        switch (request.getMethod()) {
            case Method.DEPRECATED_GET_OR_POST: {
                // This is the deprecated way that needs to be handled for backwards compatibility.
                // If the request's post body is null, then the assumption is that the request is
                // GET.  Otherwise, it is assumed that the request is a POST.
                byte[] postBody = request.getPostBody();
                if (postBody != null) {
                    HttpPost postRequest = new HttpPost(request.getUrl());
                    postRequest.addHeader(HEADER_CONTENT_TYPE, request.getPostBodyContentType());
                    HttpEntity entity;
                    entity = new ByteArrayEntity(postBody);
                    postRequest.setEntity(entity);
                    return postRequest;
                } else {
                    return new HttpGet(request.getUrl());
                }
            }
            case Method.GET:
                return new HttpGet(request.getUrl());
            case Method.DELETE:
                return new HttpDelete(request.getUrl());
            case Method.POST: {
                HttpPost postRequest = new HttpPost(request.getUrl());
                postRequest.addHeader(HEADER_CONTENT_TYPE, request.getBodyContentType());
                setMultiPartBody(postRequest,request);
                setEntityIfNonEmptyBody(postRequest, request);
                return postRequest;
            }
            case Method.PUT: {
                HttpPut putRequest = new HttpPut(request.getUrl());
                putRequest.addHeader(HEADER_CONTENT_TYPE, request.getBodyContentType());
                setMultiPartBody(putRequest,request);
                setEntityIfNonEmptyBody(putRequest, request);
                return putRequest;
            }
            // Added in source code of Volley libray.
            case Method.PATCH: {
                HttpPatch patchRequest = new HttpPatch(request.getUrl());
                patchRequest.addHeader(HEADER_CONTENT_TYPE, request.getBodyContentType());
                setEntityIfNonEmptyBody(patchRequest, request);
                return patchRequest;
            }
            default:
                throw new IllegalStateException("Unknown request method.");
        }
    }

    private static void setEntityIfNonEmptyBody(HttpEntityEnclosingRequestBase httpRequest,
                                                Request<?> request) throws AuthFailureError {
        byte[] body = request.getBody();
        if (body != null) {
            HttpEntity entity = new ByteArrayEntity(body);
            httpRequest.setEntity(entity);
        }
    }

    /**
     * If Request is MultiPartRequest type, then set MultipartEntity in the httpRequest object.
     * @param httpRequest
     * @param request
     * @throws com.android.volley.AuthFailureError
     */
    private static void setMultiPartBody(HttpEntityEnclosingRequestBase httpRequest,
                                         Request<?> request) throws AuthFailureError {
/*
        // Return if Request is not MultiPartRequest
        if(request instanceof MultiPartRequest == false) {
            return;
        }

        MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        //Iterate the fileUploads
        Map<String,File> fileUpload = ((MultiPartRequest)request).getFileUploads();
        for (Map.Entry<String, File> entry : fileUpload.entrySet()) {
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
            multipartEntity.addPart(((String)entry.getKey()), new FileBody((File)entry.getValue()));
        }

        //Iterate the stringUploads
        Map<String,String> stringUpload = ((MultiPartRequest)request).getStringUploads();
        for (Map.Entry<String, String> entry : stringUpload.entrySet()) {
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
            try {
                multipartEntity.addPart(((String)entry.getKey()), new StringBody((String)entry.getValue()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        httpRequest.setEntity(multipartEntity);
        */
    }

    /**
     * Called before the request is executed using the underlying HttpClient.
     *
     * <p>Overwrite in subclasses to augment the request.</p>
     */
    protected void onPrepareRequest(HttpUriRequest request) throws IOException {
        // Nothing.
    }

}
