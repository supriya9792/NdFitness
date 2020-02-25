package com.ndfitnessplus.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SelectDomainActivity extends AppCompatActivity {
    public static String TAG = SelectDomainActivity.class.getName();

    Button btn_save;
    EditText domain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_domain);
        initToolbar();
        initComponent();
    }
    private void initToolbar() {
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.select_domain));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initComponent() {
       btn_save=findViewById(R.id.save);
       domain=findViewById(R.id.domainurl);

       String domain_url= SharedPrefereneceUtil.getDomainUrl(SelectDomainActivity.this);

        if(domain_url == null || domain_url.equals("")){
            Log.v(TAG, String.format("domain url ::  = %s", domain_url));
        }else{
            startActivity(new Intent(SelectDomainActivity.this,LoginActivity.class));
            finish();
        }

        btn_save.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (!(domain.getText().toString().equals(""))) {
                   enquiryclass();

               }else {
                   android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(SelectDomainActivity.this);
                   builder.setMessage(R.string.enter_url);
                   builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                           dialog.dismiss();
                       }
                   });
                   android.support.v7.app.AlertDialog dialog = builder.create();
                   dialog.setCancelable(false);
                   dialog.show();
               }

           }
       });
    }
    @Override
    public void onBackPressed() {
        finish();
        moveTaskToBack(true);
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        moveTaskToBack(true);
        return true;
    }
    private void enquiryclass(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://demo.gymtime.in/AndroidApi/sql_server_api.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v(TAG, String.format("doInBackground :: show_domain_url_list= %s", response));
                        parseData(response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("action","show_domain_url_list");
                params.put("auto_id",domain.getText().toString());

                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void parseData(String jsonResponse) {

        if (jsonResponse != null) {


            try {
                Log.v(TAG, "JsonResponseOpeartion :: show_domain_url_list "+jsonResponse);
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));

                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {

                    if (object != null) {
                        JSONArray jsonArrayResult = object.getJSONArray("result");
                        int count=0;
                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {

                            for (int i = 0; i < jsonArrayResult.length(); i++) {

                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {

                                    String Domain_Url = jsonObj.getString("Domain_Url");
                                    String Auto_Id = jsonObj.getString("Auto_Id");

                                    SharedPrefereneceUtil.setDomainUrl(SelectDomainActivity.this,Domain_Url);
                                    startActivity(new Intent(SelectDomainActivity.this,LoginActivity.class));
                                    finish();

                                }
                            }
                        } else if (jsonArrayResult.length() == 0) {
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SelectDomainActivity.this);
                    builder.setMessage(R.string.error_unique_code);
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
            } catch (JSONException e) {
                e.printStackTrace();
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SelectDomainActivity.this);
                builder.setMessage(R.string.server_exception);
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

    }
}
