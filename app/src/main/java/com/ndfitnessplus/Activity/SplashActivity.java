package com.ndfitnessplus.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;

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
                    Log.v(TAG, String.format("userLogin :: logintype,password = %s,%s", user, password));
                   // String logintype=SharedPrefereneceUtil.getLoginType(SplashActivity.this);

                   // Toast.makeText(SplashActivity.this,"selected branch id "+selectdBranch_id,Toast.LENGTH_SHORT).show();
                    if ((!user.equalsIgnoreCase("") && !password.equalsIgnoreCase(""))) {
                        //Toast.makeText(SplashActivity.this,"login ",Toast.LENGTH_SHORT).show();
                        Log.v(TAG, String.format("userLogin :: Selected branch id = %s", selectdBranch_id));
                         if(!selectdBranch_id.equalsIgnoreCase("")){
                             nextadminActivity();
                         }
                         else{
                             nextBranchActivity();
                         }

                        //userLogin(user, password);
                    } else {
                        //Toast.makeText(SplashActivity.this,"not login",Toast.LENGTH_SHORT).show();
                        // Toast.makeText(SplashActivity.this,"not login",Toast.LENGTH_SHORT).show();
                        loginActivity();
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
}
