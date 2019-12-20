package com.ndfitnessplus.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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

import static com.ndfitnessplus.Utility.HTTPRequestQueue.isOnline;

public class LoginActivity extends AppCompatActivity {
    EditText username, password;
    TextView forgetpwd, termsNConditions,privacy;
    Button login;
    private final String TAG = LoginActivity.class.getName();
    private RequestQueue queue;
    String domain;
    //Loading gif
    ViewDialog viewDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_login);
           intiComponent();
}
private  void intiComponent(){

        domain= SharedPrefereneceUtil.getDomainUrl(LoginActivity.this);

        username=findViewById(R.id.username);
        password=findViewById(R.id.password);
        forgetpwd=findViewById(R.id.forgotPassword);
        termsNConditions=findViewById(R.id.termsNConditions);
        privacy=findViewById(R.id.privacy);
        login=findViewById(R.id.btn_login);
        viewDialog = new ViewDialog(this);

        login.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {
        userLogin();
        }
        });
        login.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {
        if (isValidUsername()) {
        if (validatePassword()) {
        if (isOnline(LoginActivity.this)) {
        userLogin();// check login details are valid or not from server
        }
        else {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
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
        } else
        password.setError(getString(R.string.password_error));
        } else
        username.setError(getString(R.string.invalid_username_error));
        }
        });
        forgetpwd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        Intent intent=new Intent(LoginActivity.this,ForgotPasswordActivity.class);
                        intent.putExtra("username",username.getText().toString());
                        startActivity(intent);
                        }
        });
        }
//  ************  Validation of username and password **********
private boolean isValidUsername() {
        if (username.getText().length() < 4) {
        return false;
        }
        return true;
        }
private boolean validatePassword() {
        if (password.getText().length() < 4) {
        return false;
        }
        return true;
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
private void userLogin() {

        if(InternetConnection.checkConnection(LoginActivity.this)) {
        UserLoginClass ulc = new UserLoginClass();
        Log.v(TAG, String.format("userLogin :: username,password = %s,%s", username.getText().toString(), password.getText().toString()));
        ulc.execute();
        }
        else
        {

        Toast.makeText(LoginActivity.this,"Please Connect to Internet",Toast.LENGTH_SHORT).show();
        }
        }

/**
 * Perform the asyncTask sends data to server and gets response.
 */

class UserLoginClass extends AsyncTask<String, Void, String> {
    @Override
    protected void onPreExecute() {
        Log.v(TAG,"onPreExecute");
        super.onPreExecute();
        //showProgressDialog();
        viewDialog.showDialog();
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        Log.v(TAG, String.format("onPostExecute :: response = %s", response));
        // dismissProgressDialog();
        viewDialog.hideDialog();
//            showToastMessage(response);
        //Toast.makeText(LoginActivity.this, response, Toast.LENGTH_LONG).show();
        loginServerResponse(response);
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
        Log.v(TAG, String.format("doInBackground :: username= %s",username.getText().toString()));
        loginData.put("password",password.getText().toString());
        Log.v(TAG, String.format("doInBackground :: password= %s", password.getText().toString()));
        SharedPrefereneceUtil.setUserNm(LoginActivity.this, username.getText().toString());
        loginData.put("action","login");

        ServerClass ruc = new ServerClass();
        String domainurl=SharedPrefereneceUtil.getDomainUrl(LoginActivity.this);
        Log.v(TAG, String.format("doInBackground :: loginResult= %s", domainurl+ServiceUrls.LOGIN_URL));
        String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, loginData);

        Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));

        return loginResult;

    }
}



    /**
     * Server response Operations.
     */

    private void loginServerResponse(String response) {
        Log.v(TAG, String.format("loginServerResponse :: response = %s", response));

        // jsonObjLoginResponse = null;
        try {
            // jsonObjLoginResponse = new JSONObject(response);
            JSONObject object = new JSONObject(response);
            String success = object.getString(getResources().getString(R.string.success));

            if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                if (object != null) {
                    SharedPrefereneceUtil.setISlogin(LoginActivity.this,true);
                    JSONArray jsonArrayResult = object.getJSONArray("Data");

                    if (jsonArrayResult != null && jsonArrayResult.length() > 0) {
                        for (int i = 0; i < jsonArrayResult.length(); i++) {

                            Log.v(TAG, "JsonResponseOpeartion ::");
                            JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                            if (jsonObj != null) {

                                String user_id = jsonObj.getString("user_id");
                                String name = jsonObj.getString("name");
                                String user_email = jsonObj.getString("user_email");
                                String Authority = jsonObj.getString("Authority");
                                String Company_Id = jsonObj.getString("Company_Id");
                                String Contact = jsonObj.getString("Contact");
                                SharedPrefereneceUtil.setName(LoginActivity.this,name);
                                SharedPrefereneceUtil.setUserId(LoginActivity.this,user_id);
                                //  SharedPrefereneceUtil.setBranchAutoId(LoginActivity.this,jObject.getString("Branch_AutoID"));
                                //  SharedPrefereneceUtil.setLoginId(LoginActivity.this,jObject.getString("Login_AutoID"));
                                SharedPrefereneceUtil.setAuthority(LoginActivity.this,Authority);
                                SharedPrefereneceUtil.setCompanyAutoId(LoginActivity.this,Company_Id);
                                Log.v(TAG, String.format("responce :: company id = %s", Company_Id));
                                SharedPrefereneceUtil.setMobile(LoginActivity.this,Contact);
                                SharedPrefereneceUtil.setEmail(LoginActivity.this,user_email);
                                //SharedPrefereneceUtil.setStatus(LoginActivity.this,jObject.getString("Status"));
                                // SharedPrefereneceUtil.setStaff_auto_id(LoginActivity.this,jObject.getString("Staff_AutoID"));
                                //  SharedPrefereneceUtil.setReg_date(LoginActivity.this,jObject.getString("RegDate"));
                                SharedPrefereneceUtil.setUserNm(LoginActivity.this,username.getText().toString());
                                SharedPrefereneceUtil.setPassword(LoginActivity.this,password.getText().toString());


                            }
                        }
                    } else if (jsonArrayResult.length() == 0) {
                        System.out.println("No records found");
                    }
                }
                nextadminActivity();
            }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero)))
            {
                SharedPrefereneceUtil.setISlogin(LoginActivity.this,false);
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setMessage(R.string.invalid_login);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.setCancelable(false);
                dialog.show();
                // Toast.makeText(LoginActivity.this,getResources().getString(R.string.inavlidlogin),Toast.LENGTH_LONG).show();
            }else if (success.equalsIgnoreCase(getResources().getString(R.string.one)))
            {
                Toast.makeText(LoginActivity.this,getResources().getString(R.string.inavlidlogin),Toast.LENGTH_LONG).show();

            }

        } catch (JSONException e) {
            e.printStackTrace();
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setMessage(R.string.server_exception+e.getMessage());
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
    public  void nextadminActivity(){
        Intent intent=new Intent(LoginActivity.this,BranchSelectionActivity.class);
        startActivity(intent);
    }

}
