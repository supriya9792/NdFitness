package com.ndfitnessplus.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.ndfitnessplus.Activity.Notification.TodaysEnrollmentActivity;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.ServerClass;
import com.ndfitnessplus.Utility.ServiceUrls;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;
import com.ndfitnessplus.Utility.ViewDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.ndfitnessplus.Activity.SelectDomainActivity.TAG;

public class TermsAndConditionActivity extends AppCompatActivity {
   TextView companyname,branchname,termasandcondition;
    public final String TAG = TermsAndConditionActivity.class.getName();
    private ProgressDialog pd;
    CircularImageView logo;
    //Loading gif
    ViewDialog viewDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_terms_and_condition);
        initToolbar();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.menu_term_condi));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponent();
    }
    private void initComponent() {
        companyname=findViewById(R.id.companyname);
        branchname=findViewById(R.id.branch_nameTV);
        termasandcondition=findViewById(R.id.termsNConditions);
        logo=findViewById(R.id.company_logo);
        viewDialog = new ViewDialog(this);

        CheckCompanyClass();
        companyname.setText(SharedPrefereneceUtil.getCompanyName(TermsAndConditionActivity.this));
        branchname.setText(SharedPrefereneceUtil.getSelectedBranch(TermsAndConditionActivity.this));

    }
    // ************** Check Company is active or not **************
    public void  CheckCompanyClass() {
        TermsAndConditionActivity.CheckCompanyTrackClass ru = new TermsAndConditionActivity.CheckCompanyTrackClass();
        ru.execute("5");
    }

    class CheckCompanyTrackClass extends AsyncTask<String, Void, String> {

        ServerClass ruc = new ServerClass();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(TAG, "onPreExecute");
            viewDialog.showDialog();
        }


        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.v(TAG, String.format("onPostExecute :: response = %s", response));
            viewDialog.hideDialog();
            CheckCompanyDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> CheckCompanyDetails = new HashMap<String, String>();
            CheckCompanyDetails.put("comp_id",SharedPrefereneceUtil.getSelectedBranchId(TermsAndConditionActivity.this) );
            CheckCompanyDetails.put("action", "show_terms_and_coditions_by_company_id");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(TermsAndConditionActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, CheckCompanyDetails);
            return loginResult;
        }
    }
    private void CheckCompanyDetails(String jsonResponse) {
        Log.v(TAG, String.format("loginServerResponse :: response = %s", jsonResponse));

        try {

            JSONObject object = new JSONObject(jsonResponse);
            String success = object.getString(getResources().getString(R.string.success));

            if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                if (object != null) {

                    JSONArray jsonArrayResult = object.getJSONArray("Data");

                    if (jsonArrayResult != null && jsonArrayResult.length() > 0) {
                        for (int i = 0; i < jsonArrayResult.length(); i++) {

                            JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                            if (jsonObj != null) {

                                String Logo = jsonObj.getString("Logo");
                                String TermsAndConditions = jsonObj.getString("TermsAndConditions");
                                String domainurl= SharedPrefereneceUtil.getDomainUrl(TermsAndConditionActivity.this);
                                String url=domainurl+ ServiceUrls.IMAGES_URL + Logo;

                                RequestOptions requestOptions = new RequestOptions();
                                requestOptions.placeholder(R.drawable.nouser);
                                requestOptions.error(R.drawable.nouser);


                                Glide.with(this)
                                        .setDefaultRequestOptions(requestOptions)
                                        .load(url).into(logo);
                                termasandcondition.setText(TermsAndConditions);



                            }
                        }
                    } else if (jsonArrayResult.length() == 0) {
                        System.out.println("No records found");
                    }
                }
            }
            else if (success.equalsIgnoreCase(getResources().getString(R.string.zero)))
            {

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
       finish();
    }
}
