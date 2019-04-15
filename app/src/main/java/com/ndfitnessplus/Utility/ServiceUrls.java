package com.ndfitnessplus.Utility;

import android.app.Activity;
import android.content.Context;

/**
 * Created by admin on 7/11/2017.
 SHA1: "6b2731c2d44526e3630bf5829d6b9b53448827b7"
 */
public class ServiceUrls {
    static Context context;
    //public static String  root_path = "http://www.nobletree.in/GoatTalkAdmin/interface/api.php";
    // public static String site_path =  "http://www.nobletree.in/GoatTalkAdmin/interface/";
     //Domain url =newgymtime.ndgymsoftware.com/AndroidApi
//  public   static String domainurl=SharedPrefereneceUtil.getDomainUrl((Activity) context);
   // public static String site_path =  "http://192.168.133.2:8080/";//mysql database
   // public static String root_path =  site_path+"api.php";
    //public static String site_path =  "http://192.168.133.2:81/api/";
   // public static String site_path =  "http://a.ndgymsoftware.com/";
    public static String site_path =  "http://gymtimenew.ndgymsoftware.com/";
    public static String root_path =  site_path+"AndroidApi/sql_server_api.php";
    public static String LOGIN_URL = root_path;
    // static String LOGIN_URL = site_path;
    public static final String UPLOAD_URL = site_path+"upload.php";


    // SHA1: "6b2731c2d44526e3630bf5829d6b9b53448827b7"
    // public static final String LOGIN_URL = "http://192.168.94.1/Android/LoginLogout/login.php";

    //Keys for email and password as defined in our $_POST['key'] in login.php
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";

    //If server response is equal to this that means login is successful
    public static final String LOGIN_SUCCESS = "success";

    //Keys for Sharedpreferences
    //This would be the name of our shared preferences
    public static final String SHARED_PREF_NAME = "myloginapp";

    //This would be used to store the email of current logged in user
    public static final String EMAIL_SHARED_PREF = "email";

    //We will use this to store the boolean in sharedpreference to track user is loggedin or not
    public static final String LOGGEDIN_SHARED_PREF = "loggedIn";
    public static final String IMAGES_URL = site_path;
    public static final String VIDEO_URL = site_path+"videos/";

}
