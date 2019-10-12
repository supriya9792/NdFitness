package com.ndfitnessplus.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.InternetConnection;
import com.ndfitnessplus.Utility.ServerClass;
import com.ndfitnessplus.Utility.ServiceUrls;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;
import com.ndfitnessplus.Utility.Utility;
import com.ndfitnessplus.Utility.ViewDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ForgotPasswordActivity extends AppCompatActivity {
    TextInputEditText username, email,contact;
    TextView backtologin;
    Button recoverpassword;
    private final String TAG = ForgotPasswordActivity.class.getName();
    private RequestQueue queue;
    String domain;
    String forgotPassMsg;
    //Loading gif
    ViewDialog viewDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_forgot_password);


        username=findViewById(R.id.username);
        email=findViewById(R.id.email);
        contact=findViewById(R.id.contact);
        backtologin=findViewById(R.id.back_to_login);
        viewDialog = new ViewDialog(this);

        recoverpassword=findViewById(R.id.btn_recover_pass);
        recoverpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userForgotPassword();
            }
        });

        backtologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }
    private void showProgressDialog() {
        Log.v(TAG, String.format("showProgressDialog"));
        Utility.showProgressDialog(this);

    }

    /**
     * Dismiss the Progress dialog.
     */

    private void dismissProgressDialog() {
        Log.v(TAG, String.format("dismissProgressDialog"));
        Utility.hideProgressBar();

    }
    private void userForgotPassword() {

        if(InternetConnection.checkConnection(ForgotPasswordActivity.this)) {
            ForgotPasswordActivity.UserForgotPasswordClass ulc = new ForgotPasswordActivity.UserForgotPasswordClass();
            Log.v(TAG, String.format("userForgotPassword :: username,password = %s", username.getText().toString()));
            ulc.execute();
        }
        else
        {

            Toast.makeText(ForgotPasswordActivity.this,"Please Connect to Internet",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Perform the asyncTask sends data to server and gets response.
     */

    class UserForgotPasswordClass extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            Log.v(TAG,"onPreExecute");
            super.onPreExecute();
//            showProgressDialog();
            viewDialog.showDialog();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.v(TAG, String.format("onPostExecute :: response = %s", response));
//            dismissProgressDialog();
            viewDialog.hideDialog();
//            showToastMessage(response);
            //Toast.makeText(ForgotPasswordActivity.this, response, Toast.LENGTH_LONG).show();
            forgotPasswordResponse(response);
        }

        /**
         *
         * @param params
         * @return
         */
        @Override
        protected String doInBackground(String... params) {
//            Log.v(TAG, String.format("doInBackground ::  params= %s", params));

            HashMap<String, String> loginData = new HashMap<>();
            loginData.put("username",username.getText().toString());
            SharedPrefereneceUtil.setUserNm(ForgotPasswordActivity.this, username.getText().toString());
            loginData.put(ServerClass.ACTION,"forgot_password");
            ServerClass ruc = new ServerClass();
            String domainurl=SharedPrefereneceUtil.getDomainUrl(ForgotPasswordActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, loginData);

            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;

        }
    }



    /**
     * Server response Operations.
     */

    private void forgotPasswordResponse(String response) {
        Log.v(TAG, String.format("loginServerResponse :: response = %s", response));

        // jsonObjLoginResponse = null;
        try {
            // jsonObjLoginResponse = new JSONObject(response);
            JSONObject object = new JSONObject(response);
            String success = object.getString(getResources().getString(R.string.success));

            if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                if (object != null) {
                    SharedPrefereneceUtil.setISlogin(ForgotPasswordActivity.this,true);
                    JSONArray jsonArrayResult = object.getJSONArray("Data");

                    if (jsonArrayResult != null && jsonArrayResult.length() > 0) {
                        for (int i = 0; i < jsonArrayResult.length(); i++) {

                            Log.v(TAG, "JsonResponseOpeartion ::");
                            JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                            if (jsonObj != null) {

                                String password = jsonObj.getString("password");
                                String name = jsonObj.getString("name");
                                String user_email = jsonObj.getString("user_email");
                                String Authority = jsonObj.getString("Authority");
                                String Company_Id = jsonObj.getString("Company_Id");
                                String Contact = jsonObj.getString("Contact");
                                forgotPassMsg="Hi "+name+",\n"+"your Login details\n\n"+"Username:"+username.getText().toString()+"\nPassword:"+password
                                +"\n\nThanks\nGymTime";

                                if(!password.equals("")) {
                                    ForgotPasswordActivity.this.runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                            //second async stared within a asynctask but on the main thread
                                            (new AsyncTask<String, String, String>() {
                                                ServerClass ruc = new ServerClass();

                                                @Override
                                                protected String doInBackground(String... params) {
                                                    String loginResult2 = ruc.SendSMS(contact.getText().toString(), forgotPassMsg,
                                                           "aaa1",
                                                            "Navkaraaa1",
                                                            "trans1",
                                                            "Gymmmm");
                                                    Log.v(TAG, String.format("doInBackground :: Send Sms after enquiry= %s", loginResult2));
                                                    return loginResult2;
                                                }

                                                @Override
                                                protected void onPostExecute(String response) {
                                                    super.onPostExecute(response);
                                                    Log.v(TAG, String.format("onPostExecute :: response = %s", response));
                                                    Toast.makeText(ForgotPasswordActivity.this, "Sms Has been sent to you", Toast.LENGTH_LONG).show();
                                                    Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                                                    startActivity(intent);

                                                }
                                            }).execute();

                                        }
                                    });
                                }


                            }
                        }
                    } else if (jsonArrayResult.length() == 0) {
                        System.out.println("No records found");
                    }
                }

            }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero)))
            {
                SharedPrefereneceUtil.setISlogin(ForgotPasswordActivity.this,false);
                AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
                builder.setMessage(R.string.invalid_username);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.setCancelable(false);
                dialog.show();
                // Toast.makeText(ForgotPasswordActivity.this,getResources().getString(R.string.inavlidlogin),Toast.LENGTH_LONG).show();
            }else if (success.equalsIgnoreCase(getResources().getString(R.string.one)))
            {
                Toast.makeText(ForgotPasswordActivity.this,getResources().getString(R.string.inavlidlogin),Toast.LENGTH_LONG).show();

            }

        } catch (JSONException e) {
            e.printStackTrace();
            AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
            builder.setMessage(R.string.server_exception);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.show();
        }

    }
    @Override
    public void onBackPressed() {
        finish();
        moveTaskToBack(true);
    }

}
