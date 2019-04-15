package com.ndfitnessplus.Utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static android.content.Context.MODE_PRIVATE;
import static com.ndfitnessplus.Utility.Constants.DOMAIN_NAME;
import static com.ndfitnessplus.Utility.Constants.PREFS_NAME;

/**
 * Created by G50-70 on 07/07/2016.
 */
public class HTTPRequestQueue {
    private static HTTPRequestQueue mInstance;
    private RequestQueue mRequestQueue;
    private static ImageLoader mImageLoader;
    private static Context mCtx;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor prefsEdit;

    public static final String key = "1234";
    public String domainURL ;
//"http://android.ndgymsoftware.com/"
    public static DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(0, 0, 0);

    private HTTPRequestQueue(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
        sharedPreferences = mCtx.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefsEdit = sharedPreferences.edit();
        domainURL = sharedPreferences.getString(DOMAIN_NAME,"");
        /*ImageLoader.ImageCache imageCache = new BitmapLruCache();
        mImageLoader = new ImageLoader(mRequestQueue,imageCache);*/
    }

    public static synchronized HTTPRequestQueue getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new HTTPRequestQueue(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public static ImageLoader getImageLoader() {
        return mImageLoader;
    }

    public static String md5(String s) {


        MessageDigest md = null;
        String digest = null;
        try {
            md = MessageDigest.getInstance("MD5");

            byte[] hash = md.digest(s.getBytes("UTF-8")); //converting byte array to Hexadecimal String
            StringBuilder sb = new StringBuilder(2 * hash.length);

            for (byte b : hash) {
                sb.append(String.format("%02x", b & 0xff));
            }

            digest = sb.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return digest;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
