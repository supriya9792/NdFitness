package com.ndfitnessplus.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;

public class SelectDomainActivity extends AppCompatActivity {
    public static String TAG = SelectDomainActivity.class.getName();

    Button btn_save;
    TextInputEditText domain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_domain);
        initToolbar();
        initComponent();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.select_domain));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initComponent() {
       btn_save=findViewById(R.id.save);
       domain=findViewById(R.id.domainurl);

        domain.setSelection(7);
       String domain_url= SharedPrefereneceUtil.getDomainUrl(SelectDomainActivity.this);
        //Log.d("Domain url", domain_url);
        Log.v(TAG, String.format("domain url ::  = %s", domain_url));
//        if(!(domain_url.equals(" "))){
//            startActivity(new Intent(SelectDomainActivity.this,LoginActivity.class));
//            finish();
//        }
        if(domain_url == null || domain_url.equals("")){
            Log.v(TAG, String.format("domain url ::  = %s", domain_url));
        }else{
            startActivity(new Intent(SelectDomainActivity.this,LoginActivity.class));
            finish();
        }

        btn_save.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (!(domain.getText().toString().equals("https://"))) {
                   SharedPrefereneceUtil.setDomainUrl(SelectDomainActivity.this,domain.getText().toString());
                   startActivity(new Intent(SelectDomainActivity.this,LoginActivity.class));
                   finish();
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
}
