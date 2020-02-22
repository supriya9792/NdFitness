package com.ndfitnessplus.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ndfitnessplus.Adapter.BranchSelectionAdapter;
import com.ndfitnessplus.Model.BranchList;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.ServerClass;
import com.ndfitnessplus.Utility.ServiceUrls;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;
import com.ndfitnessplus.Utility.ViewDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class BranchSelectionActivity extends AppCompatActivity {
    BranchSelectionAdapter adapter;
    ArrayList<BranchList> subListArrayList = new ArrayList<BranchList>();
    BranchList subList;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    ProgressBar progressBar;
    TextView todayDate;
    ArrayList<BranchList> branchList;
    public static String TAG = BranchSelectionActivity.class.getName();
    private ProgressDialog pd;
    //Loading gif
    ViewDialog viewDialog;
    public  String ImeiNo;
    TelephonyManager telephonyManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_branch_selection);
        initToolbar();
        initComponent();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.wel_gymtime));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private  void initComponent(){

        deviceId();
        todayDate=findViewById(R.id.todayDate);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        viewDialog = new ViewDialog(this);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        String currentDateandTime = sdf.format(new Date());
        todayDate.setText(currentDateandTime);
        branchclass();
    }
    //************ Submit button on action bar ***********
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_logout, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            SharedPrefereneceUtil.LogOut(BranchSelectionActivity.this);
            Intent intent=new Intent(BranchSelectionActivity.this,LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return true;
        }else  if (id == R.id.action_refresh) {

            Intent intent=new Intent(BranchSelectionActivity.this,BranchSelectionActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void showProgressDialog() {
        Log.v(TAG, String.format("showProgressDialog"));
        pd = new ProgressDialog(BranchSelectionActivity.this);
        pd.setMessage("loading");
        pd.setCancelable(false);
        pd.show();
    }

    /**
     * Dismiss Progress Dialog.
     */
    private void dismissProgressDialog() {
        Log.v(TAG, String.format("dismissProgressDialog"));

        pd.cancel();
    }

    private void branchclass() {
        BranchSelectionActivity.BranchSelectionTrackclass ru = new BranchSelectionActivity.BranchSelectionTrackclass();
        ru.execute("5");
    }
    class BranchSelectionTrackclass extends AsyncTask<String, Void, String> {

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
            BranchSelctionDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {

            HashMap<String, String> BranchSelctionDetails = new HashMap<String, String>();
            BranchSelctionDetails.put("Company_Id", SharedPrefereneceUtil.getCompanyAutoId(BranchSelectionActivity.this));
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getCompanyAutoId(BranchSelectionActivity.this)));
            BranchSelctionDetails.put("action","show_branch_details");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(BranchSelectionActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, BranchSelctionDetails);

            return loginResult;
        }


    }

    private void BranchSelctionDetails(String jsonResponse) {

        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));

        if (jsonResponse != null) {


            try {

                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    if (object != null) {
                        JSONArray jsonArrayResult = object.getJSONArray("result");

                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {
                            for (int i = 0; i < jsonArrayResult.length(); i++) {
                                subList = new BranchList();

                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {

                                    String branch_name = jsonObj.getString("Name");
                                    String branch = jsonObj.getString("Branch");
                                    String Contact = jsonObj.getString("Contact");
                                    String Branch_id = jsonObj.getString("Branch_id");
                                    String Status = jsonObj.getString("Status");
                                    String Logo = jsonObj.getString("Logo");
                                    String Todays_Collection = jsonObj.getString("Todays_Collection");
                                    String Month_Collection = jsonObj.getString("Month_Collection");

                                   subList.setBranchName(branch_name);
                                   subList.setCity(branch);
                                   subList.setContactNumber(Contact);
                                   subList.setBranchId(Branch_id);
                                   subList.setStatus(Status);
                                   subList.setImage(Logo);
                                   subList.setDailyCollection(Todays_Collection);
                                   subList.setMonthlyCollection(Month_Collection);

                                    subListArrayList.add(subList);
                                    adapter = new BranchSelectionAdapter(BranchSelectionActivity.this, subListArrayList);
                                    recyclerView.setAdapter(adapter);

                                }
                            }
                        } else if (jsonArrayResult.length() == 0) {
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){

                    recyclerView.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private void deviceId() {
        telephonyManager = (TelephonyManager) getSystemService(this.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 101);
            return;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 101);
                        return;
                    }
                    ImeiNo = telephonyManager.getDeviceId();
                                   } else {
                    Toast.makeText(BranchSelectionActivity.this,"Without permission we check",Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
