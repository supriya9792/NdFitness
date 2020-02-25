package com.ndfitnessplus.Activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ndfitnessplus.Activity.Notification.OtherFollowupActivity;
import com.ndfitnessplus.Adapter.SpinnerAdapter;
import com.ndfitnessplus.Model.EnquiryList;
import com.ndfitnessplus.Model.FollowupList;
import com.ndfitnessplus.Model.Spinner_List;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.ServerClass;
import com.ndfitnessplus.Utility.ServiceUrls;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;
import com.ndfitnessplus.Utility.Utility;
import com.ndfitnessplus.Utility.ViewDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class OtherFollowupFilterActivity extends AppCompatActivity {
    public final String TAG = OtherFollowupFilterActivity.class.getName();
    private ProgressDialog pd;
    //Spinner Adapter
    public Spinner spinFollType;
    Spinner_List followuptypelist;
    ArrayList<Spinner_List> followupTypeArrayList = new ArrayList<Spinner_List>();
    public SpinnerAdapter followuptypeadapter;
    String followupType;
    String[] folltype ;
    TextView todate,fromdate;
    ImageButton toDatebtn,fromDateBtn;
    private int mYear, mMonth, mDay;

    ArrayList<FollowupList> subListArrayList = new ArrayList<FollowupList>();
    FollowupList subList;
    Button btn_applyFilter;
    //Loading gif
    ViewDialog viewDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_other_followup_filter);
        initToolbar();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.other_foll_filters));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponent();
    }
    private void initComponent(){

        //spinners
        spinFollType = (Spinner) findViewById(R.id.spinner_folltype);

        todate=findViewById(R.id.to_date);
        fromdate=findViewById(R.id.from_date);
        fromDateBtn=findViewById(R.id.btn_from_date);
        toDatebtn=findViewById(R.id.btn_to_date);
        btn_applyFilter=findViewById(R.id.btn_apply_filters);

        String firstday= Utility.getFirstDayofMonth();
        todate.setText(firstday);
        String curr_date=Utility.getCurrentDate();
        fromdate.setText(curr_date);

        viewDialog = new ViewDialog(this);
        //date pickers for to date and from date
        toDatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(OtherFollowupFilterActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String date=(year + "-"
                                        + (monthOfYear + 1) + "-" + dayOfMonth).toString();
                                String cdate=Utility.formatDateDB(date);
                                todate.setText(cdate);
                                CampareTwoDates();

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        fromDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(OtherFollowupFilterActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String date=(year + "-"
                                        + (monthOfYear + 1) + "-" + dayOfMonth).toString();
                                String cdate=Utility.formatDateDB(date);
                                fromdate.setText(cdate);
                                CampareFronTwoDates();
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        //api's for spinners
       follTypeClass();


        spinFollType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int index = parent.getSelectedItemPosition();
                if(view != null) {
                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                    tv.setTextSize(10);
                    if(index==0){
                        tv.setText(getResources().getString(R.string.hint_foll_type));
                    }

                    followupType = tv.getText().toString();

                    if((followupType.equals(getResources().getString(R.string.hint_foll_type)))||
                            (followupType.equals(getResources().getString(R.string.all)))){
                        followupType="";
                    }

                }}

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btn_applyFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchOtherFollowupClass();
            }
        });

    }
    public void CampareTwoDates(){
        //******************campare two dates****************
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd-MM-yyyy");
        Date convertedDate = new Date();
        Date convertedDate2 = new Date();
        try {
            convertedDate = dateFormat.parse(todate.getText().toString());
            convertedDate2 = dateFormat.parse(fromdate.getText().toString());
            if (convertedDate2.after(convertedDate) || convertedDate2.equals(convertedDate)) {

            } else {
                String firstday= Utility.getFirstDayofMonth();
                todate.setText(firstday);
                Toast.makeText(this, "From date should not be greater than to date: " , Toast.LENGTH_LONG).show();
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void CampareFronTwoDates(){
        //******************campare two dates****************
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd-MM-yyyy");
        Date convertedDate = new Date();
        Date convertedDate2 = new Date();
        try {
            convertedDate = dateFormat.parse(fromdate.getText().toString());
            convertedDate2 = dateFormat.parse(todate.getText().toString());
            if (convertedDate2.before(convertedDate) || convertedDate2.equals(convertedDate)) {

            } else {
                String firstday= Utility.getCurrentDate();
                fromdate.setText(firstday);
                Toast.makeText(this, "From date should not be less than to date: " , Toast.LENGTH_LONG).show();
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_action_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                Intent intent = new Intent(OtherFollowupFilterActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ************* Follwup Type Spinner ***************
    public void follTypeClass() {
        OtherFollowupFilterActivity.FollTypeTrackClass ru = new OtherFollowupFilterActivity.FollTypeTrackClass();
        ru.execute("5");
    }
    class FollTypeTrackClass extends AsyncTask<String, Void, String> {

        ServerClass ruc = new ServerClass();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(TAG, "onPreExecute");

        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.v(TAG, String.format("onPostExecute :: response = %s", response));
            FollTypeDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {

            HashMap<String, String> FollTypeDetails = new HashMap<String, String>();
            FollTypeDetails.put("action", "show_master_followup_type_list");
            String domainurl= SharedPrefereneceUtil.getDomainUrl(OtherFollowupFilterActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ ServiceUrls.LOGIN_URL, FollTypeDetails);
            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }
    private void FollTypeDetails(String jsonResponse) {


        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
        if (jsonResponse != null) {


            try {

                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    if (object != null) {
                        JSONArray jsonArrayCountry = object.getJSONArray("result");
                        folltype=new String[ jsonArrayCountry.length()+1];
                        if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
                            folltype[0]=getResources().getString(R.string.hint_foll_type);
                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                followuptypelist = new Spinner_List();

                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String Followup     = jsonObj.getString("Followup");

                                    String id=jsonObj.getString("Auto_Id");
                                    if(!Followup.equals("Member BirthDay")){
                                        followuptypelist.setName(Followup);
                                        followuptypelist.setId(id);
                                        followupTypeArrayList.add(followuptypelist);
                                    }
                                }
                            }
                            followuptypeadapter = new SpinnerAdapter(OtherFollowupFilterActivity.this, followupTypeArrayList){
                                @Override
                                public boolean isEnabled(int position){
                                    if(position == 0)
                                    {
                                        // Disable the first item from Spinner
                                        // First item will be use for hint
                                        return false;
                                    }
                                    else
                                    {
                                        return true;
                                    }
                                }
                                @Override
                                public View getDropDownView(int position, View convertView,
                                                            ViewGroup parent) {
                                    View view = super.getDropDownView(position, convertView, parent);
                                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                                    if(position == 0){
                                        // Set the hint text color gray
                                        tv.setTextColor(Color.GRAY);
                                        tv.setText(getResources().getString(R.string.hint_foll_type));
                                    }
                                    else {
                                        tv.setTextColor(Color.BLACK);
                                    }
                                    return view;
                                }

                            };

                            spinFollType.setAdapter(followuptypeadapter);
                        }else if(jsonArrayCountry.length()==0){
                            System.out.println("No records found");
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void  SearchOtherFollowupClass() {
        OtherFollowupFilterActivity.SearchOtherFollowupTrackClass ru = new OtherFollowupFilterActivity.SearchOtherFollowupTrackClass();
        ru.execute("5");
    }

    class SearchOtherFollowupTrackClass extends AsyncTask<String, Void, String> {

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
            SearchOtherFollowupDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> SearchOtherFollowupDetails = new HashMap<String, String>();
            SearchOtherFollowupDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(OtherFollowupFilterActivity.this));
            SearchOtherFollowupDetails.put("to_date",todate.getText().toString());
            SearchOtherFollowupDetails.put("from_date",fromdate.getText().toString());
            SearchOtherFollowupDetails.put("foll_type", followupType);
            SearchOtherFollowupDetails.put("action", "search_other_followup_filter");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(OtherFollowupFilterActivity.this);
            String loginResult2 = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, SearchOtherFollowupDetails);
            return loginResult2;
        }
    }


    private void SearchOtherFollowupDetails(String jsonResponse) {

        Log.v(TAG, String.format("loginServerResponse :: response = %s", jsonResponse));

        JSONObject object = null;
        try {
            object = new JSONObject(jsonResponse);
            String success = object.getString(getResources().getString(R.string.success));

            if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {

                if (object != null) {
                    JSONArray jsonArrayResult = object.getJSONArray("result");
                    if (jsonArrayResult != null && jsonArrayResult.length() > 0) {
                        for (int i = 0; i < jsonArrayResult.length(); i++) {


                            subList = new FollowupList();

                            JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                            if (jsonObj != null) {

                                String name = jsonObj.getString("Name");
                                String Rating = jsonObj.getString("Rating");
                                String Contact = jsonObj.getString("Contact");
                                String CallResponse = jsonObj.getString("CallResponse");
                                String ExecutiveName = jsonObj.getString("ExecutiveName");
                                String Comment = jsonObj.getString("Comment");
                                String FollowupType = jsonObj.getString("FollowupType");
                                String NextFollowup_Date = jsonObj.getString("NextFollowupDate");
                                String Member_ID = jsonObj.getString("Member_ID");
                                String Followup_Date = jsonObj.getString("FollowupDate");


                                subList.setName(name);
                                subList.setRating(Rating);
                                subList.setContact(Contact);
                                subList.setCallRespond(CallResponse);
                                subList.setExecutiveName(ExecutiveName);
                                subList.setComment(Comment);
                                subList.setFollowupType(FollowupType);
                                String next_foll_date= Utility.formatDate(NextFollowup_Date);
                                subList.setNextFollowupDate(next_foll_date);
                                String foll_date= Utility.formatDate(Followup_Date);


                                subList.setFollowupDate(foll_date);
                                subList.setID(Member_ID);
                                subList.setImage("");

                                subListArrayList.add(subList);


                            }
                        }
                        Intent intent=new Intent(OtherFollowupFilterActivity.this, OtherFollowupActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("filter_array_list", subListArrayList);
                        intent.putExtra("BUNDLE",bundle);
                        startActivity(intent);

                    } else if (jsonArrayResult.length() == 0) {
                        System.out.println("No records found");
                    }
                }
            }


            else if (success.equalsIgnoreCase(getResources().getString(R.string.zero)))
            {

                Toast.makeText(OtherFollowupFilterActivity.this,"No Records Found",Toast.LENGTH_SHORT).show();
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
