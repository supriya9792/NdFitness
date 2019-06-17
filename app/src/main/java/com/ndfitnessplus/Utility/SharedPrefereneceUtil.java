package com.ndfitnessplus.Utility;

/**
 * Created by admin on 7/11/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import static com.ndfitnessplus.Utility.Constants.AUTHORITY;
import static com.ndfitnessplus.Utility.Constants.BRANCH;;
import static com.ndfitnessplus.Utility.Constants.COMP_NAME;
import static com.ndfitnessplus.Utility.Constants.COMPANY_AUTO_ID;
import static com.ndfitnessplus.Utility.Constants.COMP_NAME;
import static com.ndfitnessplus.Utility.Constants.EMAIL;
import static com.ndfitnessplus.Utility.Constants.IS_LOGIN;
import static com.ndfitnessplus.Utility.Constants.LOGIN_ID;
import static com.ndfitnessplus.Utility.Constants.MOBILE;
import static com.ndfitnessplus.Utility.Constants.NAME;
import static com.ndfitnessplus.Utility.Constants.PASSWORD;
import static com.ndfitnessplus.Utility.Constants.REG_DATE;
import static com.ndfitnessplus.Utility.Constants.SELECTED_BRANCH_ID;
import static com.ndfitnessplus.Utility.Constants.SMS_PASSWORD;
import static com.ndfitnessplus.Utility.Constants.SMS_ROUTE;
import static com.ndfitnessplus.Utility.Constants.SMS_SENDERID;
import static com.ndfitnessplus.Utility.Constants.SMS_USERNAME;
import static com.ndfitnessplus.Utility.Constants.STATUS;
import static com.ndfitnessplus.Utility.Constants.USER_NAME;


public class SharedPrefereneceUtil {

    public static SharedPreferences pref;
    public static SharedPreferences.Editor editor;

     String CompanyAutoId,Mobile,Email,Status,Staff_auto_id,Reg_date;

    public static String getDomainUrl(Activity activity) {
        pref =activity.getSharedPreferences("MyDomain", Context.MODE_PRIVATE);
        String domainurl = pref.getString("DomainUrl", null);
        Log.v("SharedPreferenece:: ","Stored ::");
        return domainurl;
    }



    public static void setDomainUrl(Activity activity, String domainurl) {
        pref =activity.getSharedPreferences("MyDomain", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("DomainUrl", domainurl);
        editor.commit();

        Log.v("SharedPreferenece:: ","Saved ::");

    }

    public static void setISlogin(Activity activity, boolean value) {
        pref =activity.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(IS_LOGIN, value);
        editor.commit();

        Log.v("SharedPreferenece:: ","Saved ::");

    }
    public static boolean getIsLogin(Activity activity) {
        pref =activity.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        boolean add = pref.getBoolean(IS_LOGIN, false);
        Log.v("SharedPreferenece:: ","Stored ::");
        return add;
    }
    public static void setUserId(Activity activity, String id) {
        pref =activity.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("User_id", id);
        editor.commit();

        Log.v("SharedPreferenece:: "," NAME Saved ::");

    }

    public static String getUserId(Activity activity) {
        pref =activity.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        String id = pref.getString("User_id", null);
        Log.v("SharedPreferenece:: ","NAME ::");
        return id;
    }

    public static void setName(Activity activity, String name) {
        pref =activity.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(NAME, name);
        editor.commit();

        Log.v("SharedPreferenece:: "," NAME Saved ::");

    }

    public static String getName(Activity activity) {
        pref =activity.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        String name = pref.getString(NAME, null);
        Log.v("SharedPreferenece:: ","NAME ::");
        return name;
    }



    public static void setUserNm(Activity activity, String username) {
        pref =activity.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(USER_NAME, username);
        editor.commit();

        Log.v("SharedPreferenece:: ","Saved ::");

    }
    public static String getUserNm(Activity activity) {
        pref =activity.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        String Username = pref.getString(USER_NAME, null);
        Log.v("SharedPreferenece:: ","Stored ::");
        return Username;
    }


    public static void setPassword(Activity activity, String password) {
        pref =activity.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PASSWORD, password);
        editor.commit();

        Log.v("SharedPreferenece:: ","Saved ::");

    }
    public static String getPassword(Activity activity) {
        pref =activity.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        String logintype = pref.getString(PASSWORD, null);
        Log.v("SharedPreferenece:: ","Stored ::");
        return logintype;
    }
    public static void setSelectedBranch(Activity activity, String branchautoid) {
        pref =activity.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(BRANCH, branchautoid);
        editor.commit();

        Log.v("SharedPreferenece:: ","Saved ::");

    }
    public static String getSelectedBranch(Activity activity) {
        pref =activity.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        String branchautoid = pref.getString(BRANCH, null);
        Log.v("SharedPreferenece:: ","Stored ::");
        return branchautoid;
    }
    public static void setLoginId(Activity activity, String login_id) {
        pref =activity.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(LOGIN_ID, login_id);
        editor.commit();

        Log.v("SharedPreferenece:: ","Saved ::");

    }
    public static String getLoginId(Activity activity) {
        pref =activity.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        String login_id = pref.getString(LOGIN_ID, "");

        Log.v("SharedPreferenece:: ","Stored ::");
        return login_id;
    }


    public static void setAuthority(Activity activity, String authority) {
        pref =activity.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(AUTHORITY, authority);
        editor.commit();

        Log.v("SharedPreferenece:: ","Saved ::");
    }
    public static String getAuthority(Activity activity) {
        pref =activity.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        String authority = pref.getString(AUTHORITY, "");

        Log.v("SharedPreferenece:: ","Stored ::");
        return authority;
    }

    public static String getCompanyAutoId(Activity activity) {
        pref =activity.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        String compautoid = pref.getString(COMPANY_AUTO_ID, "");

        Log.v("SharedPreferenece:: ","Stored ::");
        return compautoid;
    }

    public static void setCompanyAutoId(Activity activity,String companyAutoId) {
        pref =activity.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(COMPANY_AUTO_ID, companyAutoId);
        editor.commit();

        Log.v("SharedPreferenece:: ","Saved ::");
    }

    public static String getMobile(Activity activity) {
        pref =activity.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        String mobile = pref.getString(MOBILE, "");
        Log.v("SharedPreferenece:: ","Stored ::");
        return mobile;
    }

    public static void setMobile(Activity activity,String mobile) {
        pref =activity.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(MOBILE, mobile);
        editor.commit();

        Log.v("SharedPreferenece:: ","Saved ::");
    }

    public static String getEmail(Activity activity) {
        pref =activity.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        String email = pref.getString(EMAIL, "");

        Log.v("SharedPreferenece:: ","Stored ::");
        return email;

    }

    public static void setEmail(Activity activity,String email) {
        pref =activity.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(EMAIL, email);
        editor.commit();

        Log.v("SharedPreferenece:: ","Saved ::");
    }

    public static String getStatus(Activity activity) {
        pref =activity.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        String status = pref.getString(STATUS, "");

        Log.v("SharedPreferenece:: ","Stored ::");
        return status;
    }

    public static void setStatus(Activity activity,String status) {
        pref =activity.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(STATUS, status);
        editor.commit();

        Log.v("SharedPreferenece:: ","Saved ::");
    }

    public static String getSmsUsername(Activity activity) {
        pref =activity.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        String smsusername = pref.getString(SMS_USERNAME, "");

        Log.v("SharedPreferenece:: ","Stored ::");
        return smsusername;
    }

    public static void setSmsUsername(Activity activity,String smsusername) {
        pref =activity.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(SMS_USERNAME, smsusername);
        editor.commit();

        Log.v("SharedPreferenece:: ","Saved ::");
    }

    public static String getSmsPassword(Activity activity) {
        pref =activity.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        String sms_pass = pref.getString(SMS_PASSWORD, "");

        Log.v("SharedPreferenece:: ","Stored ::");
        return sms_pass;
    }

    public static void setSmsPassword(Activity activity,String sms_pass) {
        pref =activity.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(SMS_PASSWORD, sms_pass);
        editor.commit();

        Log.v("SharedPreferenece:: ","Saved ::");
    }
    public static String getSmsRoute(Activity activity) {
        pref =activity.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        String sms_route = pref.getString(SMS_ROUTE, "");

        Log.v("SharedPreferenece:: ","Stored ::");
        return sms_route;
    }

    public static void setSmsRoute(Activity activity,String sms_route) {
        pref =activity.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(SMS_ROUTE, sms_route);
        editor.commit();

        Log.v("SharedPreferenece:: ","Saved ::");
    }
    public static String getSmsSenderid(Activity activity) {
        pref =activity.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        String sms_senderid = pref.getString(SMS_SENDERID, "");

        Log.v("SharedPreferenece:: ","Stored ::");
        return sms_senderid;
    }

    public static void setSmsSenderid(Activity activity,String sms_senderid) {
        pref =activity.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(SMS_SENDERID, sms_senderid);
        editor.commit();

        Log.v("SharedPreferenece:: ","Saved ::");
    }
    public static String getCompanyName(Activity activity) {
        pref =activity.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        String branchname = pref.getString(COMP_NAME, "");

        Log.v("SharedPreferenece:: ","Stored ::");
        return branchname;
    }

    public static void setCompanyName(Activity activity,String branchname) {
        pref =activity.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(COMP_NAME, branchname);
        editor.commit();

        Log.v("SharedPreferenece:: ","Saved ::");
    }
    public static String getSelectedBranchId(Activity activity) {
        pref =activity.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        String selectedbranchid = pref.getString(SELECTED_BRANCH_ID, "");

        Log.v("SharedPreferenece:: ","Stored ::");
        return selectedbranchid;
    }

    public static void setSelectedBranchId(Activity activity,String selectedbranchid) {
        pref =activity.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(SELECTED_BRANCH_ID, selectedbranchid);
        editor.commit();

        Log.v("SharedPreferenece:: ","Saved ::");
    }



    public static void LogOut(Activity activity){

        pref =activity.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.remove("User_id");
        editor.remove(IS_LOGIN);
        editor.remove(NAME);
        editor.remove(USER_NAME);
        editor.remove(PASSWORD);
        editor.remove(AUTHORITY);
        editor.remove(COMPANY_AUTO_ID);
        editor.remove(MOBILE);
        editor.remove(EMAIL);
        editor.remove(STATUS);
        editor.remove(COMP_NAME);
        editor.remove(BRANCH);
        editor.remove(LOGIN_ID);
        editor.remove(SELECTED_BRANCH_ID);
        editor.remove(SMS_USERNAME);
        editor.remove(SMS_PASSWORD);
        editor.remove(SMS_ROUTE);
        editor.remove(SMS_SENDERID);
        editor.clear();
        editor.commit();

        Log.v("SharedPreferenece:: ", "LogoutFragment ::");
    }
}
