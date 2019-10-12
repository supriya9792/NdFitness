package com.ndfitnessplus.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.widget.Toast;

import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.InternetConnection;
import com.ndfitnessplus.Utility.ServerClass;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.ndfitnessplus.Utility.HTTPRequestQueue.isOnline;
import static com.ndfitnessplus.Utility.ServiceUrls.LOGIN_URL;

public class SplashActivity extends AppCompatActivity {
    private final String TAG = SplashActivity.class.getName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


       /****** Create Thread that will sleep for 5 seconds *************/
        new Handler().postDelayed(new Runnable() {
public void run() {

        // This method will be executed once the timer is over
        // Start your app main activity


        try {
        // Thread will sleep for 5 seconds
        SharedPreferences pref = getSharedPreferences("MyPref", MODE_PRIVATE);
        String user = pref.getString("user_name", "");
        String password = pref.getString("password", "");
        String selectdBranch_id = SharedPrefereneceUtil.getSelectedBranchId(SplashActivity.this);

        String domain_url= SharedPrefereneceUtil.getDomainUrl(SplashActivity.this);
        //Log.d("Domain url", domain_url);
        Log.v(TAG, String.format("domain url ::  = %s", domain_url));
//        if(!(domain_url.equals(" "))){
//            startActivity(new Intent(SelectDomainActivity.this,LoginActivity.class));
//            finish();
//        }
        if(domain_url == null || domain_url.equals("")){
        Log.v(TAG, String.format("domain url ::  = %s", domain_url));
        loginActivity();
        Toast.makeText(SplashActivity.this,"domainurl is blank",Toast.LENGTH_SHORT).show();
        }else {

        Log.v(TAG, String.format("userLogin :: Domain Url = %s", domain_url));
        // String logintype=SharedPrefereneceUtil.getLoginType(SplashActivity.this);
        // Toast.makeText(SplashActivity.this,"selected branch id "+selectdBranch_id,Toast.LENGTH_SHORT).show()
        Log.v(TAG, String.format("userLogin :: username,password = %s,%s", user, password));
        if ((!user.equalsIgnoreCase("") && !password.equalsIgnoreCase(""))) {
        //Toast.makeText(SplashActivity.this,"login ",Toast.LENGTH_SHORT).show();
        Log.v(TAG, String.format("userLogin :: Selected branch id = %s", selectdBranch_id));
//                            if (!selectdBranch_id.equalsIgnoreCase("")) {
//                                userLogin(user, password);
//                            } else {
//                                nextBranchActivity();
//                            }
        //nextBranchActivity();
        userLogin(user, password);
        } else {
        //Toast.makeText(SplashActivity.this,"not login",Toast.LENGTH_SHORT).show();
        // Toast.makeText(SplashActivity.this,"not login",Toast.LENGTH_SHORT).show();
        nextloginActivity();
        }
        }


//                    if(logintype.equals("facebook")||logintype.equals("google")){
//                        nextadminActivity();
//                    }else{
//                        loginActivity();
//                    }



        } catch (Exception e) {

        }
        }
        }, 3000);
        }
private void loginActivity() {
        Intent intent = new Intent(SplashActivity.this, SelectDomainActivity.class);
        SplashActivity.this.startActivity(intent);
        }

private void nextadminActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);

        }
private void nextBranchActivity() {
        Intent intent = new Intent(SplashActivity.this, BranchSelectionActivity.class);
        startActivity(intent);

        }
private void nextloginActivity() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);

        }
private void userLogin(final String name, final String password) {
        Log.v(TAG, String.format("userLogin :: mobileno,password = %s,%s", name, password));
        if (isOnline(SplashActivity.this)) {
        SplashActivity.UserLoginClass ulc = new SplashActivity.UserLoginClass();
        ulc.execute(name, password);
        }
        else {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SplashActivity.this);
        builder.setMessage(R.string.internet_unavailable);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
public void onClick(DialogInterface dialog, int id) {
        dialog.dismiss();
        }
        });
        android.app.AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
        }
        }
class UserLoginClass extends AsyncTask<String, Void, String> {
    @Override
    protected void onPreExecute() {
        Log.v(TAG, "onPreExecute");
        super.onPreExecute();
        //showProgressDialog();
    }

    private void showToastMessage(String message) {
        Log.v(TAG, String.format("showToastMessage :: message = %s", message));
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        Log.v(TAG, String.format("onPostExecute :: response = %s", response));
        // dismissProgressDialog();
        // Toast.makeText(SplashActivity.this,response,Toast.LENGTH_SHORT).show();
        JSONObject jsonObjLoginResponse = null;
        try {
            jsonObjLoginResponse = new JSONObject(response);
            String success = jsonObjLoginResponse.getString(getResources().getString(R.string.success));

            if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
//                nextBranchActivity();
                nextadminActivity();
                //loginActivity();
            } else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))) {
                // Toast.makeText(SplashActivity.this,"Your an Inactive User",Toast.LENGTH_SHORT).show();
                nextloginActivity();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected String doInBackground(String... params) {
//            Log.v(TAG, String.format("doInBackground ::  params= %s", params));

        HashMap<String, String> loginData = new HashMap<>();
        loginData.put("username", params[0]);
        loginData.put("password", params[1]);
        loginData.put(ServerClass.ACTION, "login");
        ServerClass ruc = new ServerClass();
        String d=SharedPrefereneceUtil.getDomainUrl(SplashActivity.this);
        String loginResult = ruc.sendPostRequest(d+LOGIN_URL, loginData);

        Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
        return loginResult;

    }
}
}
