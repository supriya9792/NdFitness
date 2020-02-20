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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ndfitnessplus.Activity.Notification.TodaysEnrollmentActivity;
import com.ndfitnessplus.Adapter.AddEnquirySpinnerAdapter;
import com.ndfitnessplus.Adapter.SpinnerAdapter;
import com.ndfitnessplus.Model.EnquiryList;
import com.ndfitnessplus.Model.MemberDataList;
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

public class MemberFilterActivity extends AppCompatActivity {
    public final String TAG = MemberFilterActivity.class.getName();
    private ProgressDialog pd;
    //Spinner Adapter
    public Spinner spinDateWise,spinOccupation,spinBloodgrp,spinGender,spinEnqExecutive,spinMemExecutive,spinStatus;
    Spinner_List dateWiselist,Occupationlist,bloodGrouplist,GenderList,EnqExecutiveNameList,spinMemExecutiveNameList,statusList;
    ArrayList<Spinner_List> dateWiseArrayList = new ArrayList<Spinner_List>();
    ArrayList<Spinner_List> OccupationArrayList = new ArrayList<Spinner_List>();
    ArrayList<Spinner_List>bloodGroupArrayList = new ArrayList<Spinner_List>();
    ArrayList<Spinner_List> GenderArrayList = new ArrayList<Spinner_List>();
    ArrayList<Spinner_List> EnqExecutiveNameArrayList = new ArrayList<Spinner_List>();
    ArrayList<Spinner_List> MemExecutiveNameArrayList = new ArrayList<Spinner_List>();
    ArrayList<Spinner_List> StatusArrayList = new ArrayList<Spinner_List>();

    public SpinnerAdapter enqdateadapter,occupationadpater,bloodgroupadapter,genderadapter,enqExecutiveadapter,memExecutiveAdapter,
          statusAdapter;
    String Datewise,occupation,bloodgroup,gender,enqExecutiveName,memExecutiveName,status;

    TextView todate,fromdate;
    ImageButton toDatebtn,fromDateBtn;
    private int mYear, mMonth, mDay;
    String month,day;
    ArrayList<MemberDataList> subListArrayList = new ArrayList<MemberDataList>();
    MemberDataList subList;
    Button btn_applyFilter;
    //Loading gif
    ViewDialog viewDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_member_filter);
        initToolbar();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.member_filters));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponent();
    }
    private void initComponent(){

        //spinners
        spinDateWise = (Spinner) findViewById(R.id.spinner_date_wise);
        spinOccupation = (Spinner) findViewById(R.id.spinner_occupation);
        spinBloodgrp = (Spinner) findViewById(R.id.spinner_blood_grp);
        spinGender = (Spinner) findViewById(R.id.spinner_enq_gender);
        spinEnqExecutive = (Spinner) findViewById(R.id.spinner_executive);
        spinMemExecutive = (Spinner) findViewById(R.id.spinner_mem_executive);
        spinStatus = (Spinner) findViewById(R.id.spinner_status);
        viewDialog = new ViewDialog(this);


        todate=findViewById(R.id.to_date);
        fromdate=findViewById(R.id.from_date);
        fromDateBtn=findViewById(R.id.btn_from_date);
        toDatebtn=findViewById(R.id.btn_to_date);
        btn_applyFilter=findViewById(R.id.btn_apply_filters);

        String firstday= Utility.getFirstDayofMonth();
        todate.setText(firstday);
        String curr_date=Utility.getCurrentDate();
        fromdate.setText(curr_date);


        //date pickers for to date and from date
        toDatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(MemberFilterActivity.this,
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


                DatePickerDialog datePickerDialog = new DatePickerDialog(MemberFilterActivity.this,
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
       // enqforClass();
       // enqtypeClass();
       // enqsourceClass();
        //callResponseClass();
        //locationClass();
        occupationClass();
        enquiryExecutiveClass();
        memberExecutiveClass();


        //setting data to the spinners


        //Gender spinners
        final  String[] genderarray = getResources().getStringArray(R.array.gender_array);
        GenderList = new Spinner_List();
        GenderList.setName(getResources().getString(R.string.prompt_gender));
        GenderArrayList.add(0,GenderList);
        GenderList.setName(getResources().getString(R.string.all));
        GenderArrayList.add(1,GenderList);
        for(int i=1;i<genderarray.length;i++) {
            GenderList = new Spinner_List();
            GenderList.setName(genderarray[i]);
            GenderArrayList.add(GenderList);
            genderadapter = new SpinnerAdapter(MemberFilterActivity.this, GenderArrayList) {
                @Override
                public boolean isEnabled(int position) {
                    if (position == 0) {
                        // Disable the first item from Spinner
                        // First item will be use for hint
                        return false;
                    } else {
                        return true;
                    }
                }

                @Override
                public View getDropDownView(int position, View convertView,
                                            ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                    if (position == 0) {
                        // Set the hint text color gray
                        tv.setTextColor(Color.GRAY);
                        tv.setText(getResources().getString(R.string.prompt_gender));
                        // tv.setTextColor(Color.GRAY);
                    } else {
                        tv.setTextColor(Color.BLACK);
                    }
                    return view;
                }

            };
            spinGender.setAdapter(genderadapter);
        }
        //Toast.makeText(MainActivity.this,genderradioButton.getText(), Toast.LENGTH_SHORT).show();
        spinGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int index = parent.getSelectedItemPosition();
                if (view != null) {
                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                    tv.setTextSize(10);
                    if (index == 0) {
                        tv.setText(getResources().getString(R.string.prompt_gender));
                    }
//                tv.setTextColor(getResources().getColor(R.color.black));
                    gender = tv.getText().toString();
                    if ((gender.equals(getResources().getString(R.string.prompt_gender))) ||
                            (gender.equals(getResources().getString(R.string.all)))) {
                        gender = "";
                    }
                    // ((TextView) spinEnquiryType.getSelectedView()).setTextColor(getResources().getColor(R.color.white));
                    // Showing selected spinner item
                    //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //Date Wise date type spinners
        final  String[] memberDateArray = getResources().getStringArray(R.array.mem_date_array);

        for(int i=0;i<memberDateArray.length;i++) {
            dateWiselist = new Spinner_List();
            dateWiselist.setName(memberDateArray[i]);
            dateWiseArrayList.add(dateWiselist);
            enqdateadapter = new SpinnerAdapter(MemberFilterActivity.this, dateWiseArrayList) {
                @Override
                public boolean isEnabled(int position) {
                    if (position == 0) {
                        // Disable the first item from Spinner
                        // First item will be use for hint
                        return false;
                    } else {
                        return true;
                    }
                }

                @Override
                public View getDropDownView(int position, View convertView,
                                            ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                    if (position == 0) {
                        // Set the hint text color gray
                        tv.setTextColor(Color.GRAY);
                        tv.setText(getResources().getString(R.string.prompt_mem_date));
                        // tv.setTextColor(Color.GRAY);
                    } else {
                        tv.setTextColor(Color.BLACK);
                    }
                    return view;
                }

            };
            spinDateWise.setAdapter(enqdateadapter);
        }
        spinDateWise.setSelection(1);
        //Toast.makeText(MainActivity.this,genderradioButton.getText(), Toast.LENGTH_SHORT).show();
        spinDateWise.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int index = parent.getSelectedItemPosition();
                if (view != null) {
                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                    tv.setTextSize(10);
                    if (index == 0) {
                        tv.setText(getResources().getString(R.string.prompt_mem_date));
                    }

//                tv.setTextColor(getResources().getColor(R.color.black));
                    Datewise = tv.getText().toString();
                    if ((Datewise.equals(getResources().getString(R.string.prompt_mem_date))) ||
                            (Datewise.equals(getResources().getString(R.string.all)))) {
                        Datewise = "";
                    }
                    // ((TextView) spinEnquiryType.getSelectedView()).setTextColor(getResources().getColor(R.color.white));
                    // Showing selected spinner item
                    //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinOccupation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int index = parent.getSelectedItemPosition();
                if(view != null) {
                TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                tv.setTextSize(10);
                if(index==0){
                    tv.setText(getResources().getString(R.string.prompt_occupation));
                }
//                tv.setTextColor(getResources().getColor(R.color.black));
                occupation = tv.getText().toString();
                if((occupation.equals(getResources().getString(R.string.prompt_occupation)))||
                        (occupation.equals(getResources().getString(R.string.all)))){
                    occupation="";
                }
                // ((TextView) spinEnquiryType.getSelectedView()).setTextColor(getResources().getColor(R.color.white));
                // Showing selected spinner item
                //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
            }}

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        final  String[] bloodgrouparray = getResources().getStringArray(R.array.blood_group_array);

        for(int i=0;i<bloodgrouparray.length;i++) {
            bloodGrouplist = new Spinner_List();
            bloodGrouplist.setName(bloodgrouparray[i]);
            bloodGroupArrayList.add(bloodGrouplist);
            bloodgroupadapter = new SpinnerAdapter(MemberFilterActivity.this, bloodGroupArrayList) {
                @Override
                public boolean isEnabled(int position) {
                    if (position == 0) {
                        // Disable the first item from Spinner
                        // First item will be use for hint
                        return false;
                    } else {
                        return true;
                    }
                }

                @Override
                public View getDropDownView(int position, View convertView,
                                            ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                    if (position == 0) {
                        // Set the hint text color gray
                        tv.setTextColor(Color.GRAY);
                        tv.setText(getResources().getString(R.string.prompt_blood_group));
                        // tv.setTextColor(Color.GRAY);
                    } else {
                        tv.setTextColor(Color.BLACK);
                    }
                    return view;
                }

            };
            spinBloodgrp.setAdapter(bloodgroupadapter);
        }

        //setting data to the spinners

        spinBloodgrp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int index = parent.getSelectedItemPosition();
                if(view != null) {
                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                    tv.setTextSize(10);

                    bloodgroup = tv.getText().toString();
                    if ((bloodgroup.equals(getResources().getString(R.string.blood_group))) ||
                            (bloodgroup.equals(getResources().getString(R.string.all)))) {
                        bloodgroup = "";
                    }
                }

                // ((TextView) spinEnquiryType.getSelectedView()).setTextColor(getResources().getColor(R.color.black));
                // Showing selected spinner item
                //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //Location item selection listener
        spinEnqExecutive.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int index = parent.getSelectedItemPosition();
                if (view != null) {
                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                    tv.setTextSize(10);
                    if (index == 0) {
                        tv.setText(getResources().getString(R.string.prompt_enq_executive));
                    }
//                tv.setTextColor(getResources().getColor(R.color.black));
                    enqExecutiveName = tv.getText().toString();
                    if ((enqExecutiveName.equals(getResources().getString(R.string.prompt_enq_executive))) ||
                            (enqExecutiveName.equals(getResources().getString(R.string.all)))) {
                        enqExecutiveName = "";
                    }
                    // ((TextView) spinEnquiryType.getSelectedView()).setTextColor(getResources().getColor(R.color.white));
                    // Showing selected spinner item
                    //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinMemExecutive.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int index = parent.getSelectedItemPosition();
                if (view != null) {
                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                    tv.setTextSize(10);
                    if (index == 0) {
                        tv.setText(getResources().getString(R.string.prompt_mem_executive));
                    }
//                tv.setTextColor(getResources().getColor(R.color.black));
                    memExecutiveName = tv.getText().toString();
                    if ((memExecutiveName.equals(getResources().getString(R.string.prompt_mem_executive))) ||
                            (memExecutiveName.equals(getResources().getString(R.string.all)))) {
                        memExecutiveName = "";
                    }
                    // ((TextView) spinEnquiryType.getSelectedView()).setTextColor(getResources().getColor(R.color.white));
                    // Showing selected spinner item
                    //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
// *********** status spinner
        final  String[] statusArray = getResources().getStringArray(R.array.status_array);

        for(int i=0;i<statusArray.length;i++) {
            statusList = new Spinner_List();
            statusList.setName(statusArray[i]);
            StatusArrayList.add(statusList);
            statusAdapter = new SpinnerAdapter(MemberFilterActivity.this, StatusArrayList) {
                @Override
                public boolean isEnabled(int position) {
                    if (position == 0) {
                        // Disable the first item from Spinner
                        // First item will be use for hint
                        return false;
                    } else {
                        return true;
                    }
                }

                @Override
                public View getDropDownView(int position, View convertView,
                                            ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                    if (position == 0) {
                        // Set the hint text color gray
                        tv.setTextColor(Color.GRAY);
                        tv.setText(getResources().getString(R.string.prompt_status));
                        // tv.setTextColor(Color.GRAY);
                    } else {
                        tv.setTextColor(Color.BLACK);
                    }
                    return view;
                }

            };
            spinStatus.setAdapter(statusAdapter);
        }
        //Toast.makeText(MainActivity.this,genderradioButton.getText(), Toast.LENGTH_SHORT).show();
        spinStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int index = parent.getSelectedItemPosition();
                if (view != null) {
                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                    tv.setTextSize(10);
                    if (index == 0) {
                        tv.setText(getResources().getString(R.string.prompt_status));
                    }
//                tv.setTextColor(getResources().getColor(R.color.black));
                    status = tv.getText().toString();
                    if ((status.equals(getResources().getString(R.string.prompt_status))) ||
                            (status.equals(getResources().getString(R.string.all)))) {
                        status = "";
                    }
                    // ((TextView) spinEnquiryType.getSelectedView()).setTextColor(getResources().getColor(R.color.white));
                    // Showing selected spinner item
                    //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btn_applyFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchEnquiryClass();
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
                //.setText("true");
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
                //.setText("true");
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
                Intent intent = new Intent(MemberFilterActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void showProgressDialog() {
        Log.v(TAG, String.format("showProgressDialog"));
        pd = new ProgressDialog(MemberFilterActivity.this);
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
    public void  occupationClass() {
        MemberFilterActivity.OccupationTrackClass ru = new MemberFilterActivity.OccupationTrackClass();
        ru.execute("5");
    }
    class OccupationTrackClass extends AsyncTask<String, Void, String> {

        ServerClass ruc = new ServerClass();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(TAG, "onPreExecute");
            // showProgressDialog();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.v(TAG, String.format("onPostExecute :: response = %s", response));
            // dismissProgressDialog();
            //Toast.makeText(Employee.this, response, Toast.LENGTH_LONG).show();
            OccupationDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
           // Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> OccupationDetails = new HashMap<String, String>();
            OccupationDetails.put("action", "show_occupation_list");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(MemberFilterActivity.this);
            //OccupationloyeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(Occupationloyee.this));
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, OccupationDetails);
            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }


    private void OccupationDetails(String jsonResponse) {


        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
//        RelativeLayout relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayoutPrabhagDetails);
        if (jsonResponse != null) {


            try {
                Log.v(TAG, "JsonResponseOpeartion :: test");
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    if (object != null) {
                        JSONArray jsonArrayCountry = object.getJSONArray("result");
                        OccupationArrayList.clear();
                        //for(int j=0;j<2;j++){
                        Occupationlist = new Spinner_List();

                        Occupationlist.setName(getResources().getString(R.string.prompt_occupation));

                        OccupationArrayList.add(0,Occupationlist);

                        // }
                        if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
                            Occupationlist.setName(getResources().getString(R.string.all));

                            OccupationArrayList.add(1,Occupationlist);
                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                Occupationlist = new Spinner_List();
                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String Occupation     = jsonObj.getString("Occupation");

                                    String id=jsonObj.getString("Auto_Id");
//                               if(i==0){
//                                   Occupationlist.setName(getResources().getString(R.string.promt_country));
//                                   enqF.add(0,Occupationlist);
//                               }


                                    Occupationlist.setName(Occupation);
                                    Occupationlist.setId(id);

                                    OccupationArrayList.add(Occupationlist);

                                    occupationadpater = new SpinnerAdapter(MemberFilterActivity.this, OccupationArrayList){
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
                                                tv.setText(getResources().getString(R.string.prompt_occupation));
                                                // tv.setTextColor(Color.GRAY);
                                            }
                                            else {
                                                tv.setTextColor(Color.BLACK);
                                            }
                                            return view;
                                        }

                                    };
                                    spinOccupation.setAdapter(occupationadpater);


                                }
                            }
                        }else if(jsonArrayCountry.length()==0){
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    OccupationArrayList.clear();
                    //for(int j=0;j<2;j++){
                    Occupationlist = new Spinner_List();

                    Occupationlist.setName(getResources().getString(R.string.prompt_occupation));

                    OccupationArrayList.add(0,Occupationlist);
                    Occupationlist.setName(getResources().getString(R.string.all));

                    OccupationArrayList.add(1,Occupationlist);
                    occupationadpater = new SpinnerAdapter(MemberFilterActivity.this, OccupationArrayList){
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
                                tv.setText(getResources().getString(R.string.prompt_occupation));
                                // tv.setTextColor(Color.GRAY);
                            }
                            else {
                                tv.setTextColor(Color.BLACK);
                            }
                            return view;
                        }

                    };
                    spinOccupation.setAdapter(occupationadpater);
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
            }
        }
    }
    public void  enquiryExecutiveClass() {
        MemberFilterActivity.EnqExecutiveTrackClass ru = new MemberFilterActivity.EnqExecutiveTrackClass();
        ru.execute("5");
    }
    class EnqExecutiveTrackClass extends AsyncTask<String, Void, String> {

        ServerClass ruc = new ServerClass();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(TAG, "onPreExecute");
            // showProgressDialog();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.v(TAG, String.format("onPostExecute :: response = %s", response));
            // dismissProgressDialog();
            //Toast.makeText(Employee.this, response, Toast.LENGTH_LONG).show();
            EnqExecutiveDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
          //  Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> EnqExecutiveDetails = new HashMap<String, String>();
            EnqExecutiveDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(MemberFilterActivity.this));
            EnqExecutiveDetails.put("action", "show_enquiry_executive_list_for_member");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(MemberFilterActivity.this);
            //EnqExecutiveloyeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(EnqExecutiveloyee.this));
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, EnqExecutiveDetails);
            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }


    private void EnqExecutiveDetails(String jsonResponse) {


        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
//        RelativeLayout relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayoutPrabhagDetails);
        if (jsonResponse != null) {


            try {
                Log.v(TAG, "JsonResponseOpeartion :: test");
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    if (object != null) {
                        JSONArray jsonArrayCountry = object.getJSONArray("result");
                        EnqExecutiveNameArrayList.clear();
                        //for(int j=0;j<2;j++){
                        EnqExecutiveNameList = new Spinner_List();

                        EnqExecutiveNameList.setName(getResources().getString(R.string.prompt_enq_executive));

                        EnqExecutiveNameArrayList.add(0,EnqExecutiveNameList);

                        // }
                        if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
                            EnqExecutiveNameList.setName(getResources().getString(R.string.all));

                            EnqExecutiveNameArrayList.add(1,EnqExecutiveNameList);
                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                EnqExecutiveNameList = new Spinner_List();
                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String EnquiryOwnerExecutive     = jsonObj.getString("EnquiryOwnerExecutive");

                                   // String id=jsonObj.getString("Auto_Id");
//                               if(i==0){
//                                   EnqExecutiveNameList.setName(getResources().getString(R.string.promt_country));
//                                   enqF.add(0,EnqExecutiveNameList);
//                               }


                                    EnqExecutiveNameList.setName(EnquiryOwnerExecutive);
                                    //EnqExecutiveNameList.setId(id);

                                    EnqExecutiveNameArrayList.add(EnqExecutiveNameList);

                                    enqExecutiveadapter = new SpinnerAdapter(MemberFilterActivity.this, EnqExecutiveNameArrayList){
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
                                                tv.setText(getResources().getString(R.string.prompt_enq_executive));
                                                // tv.setTextColor(Color.GRAY);
                                            }
                                            else {
                                                tv.setTextColor(Color.BLACK);
                                            }
                                            return view;
                                        }

                                    };
                                    spinEnqExecutive.setAdapter(enqExecutiveadapter);


                                }
                            }
                        }else if(jsonArrayCountry.length()==0){
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    EnqExecutiveNameArrayList.clear();
                    //for(int j=0;j<2;j++){
                    EnqExecutiveNameList = new Spinner_List();

                    EnqExecutiveNameList.setName(getResources().getString(R.string.prompt_enq_executive));

                    EnqExecutiveNameArrayList.add(0,EnqExecutiveNameList);
                    EnqExecutiveNameList.setName(getResources().getString(R.string.all));

                    EnqExecutiveNameArrayList.add(1,EnqExecutiveNameList);
                    enqExecutiveadapter = new SpinnerAdapter(MemberFilterActivity.this, EnqExecutiveNameArrayList){
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
                                tv.setText(getResources().getString(R.string.prompt_enq_executive));
                                // tv.setTextColor(Color.GRAY);
                            }
                            else {
                                tv.setTextColor(Color.BLACK);
                            }
                            return view;
                        }

                    };
                    spinEnqExecutive.setAdapter(enqExecutiveadapter);
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
            }
        }
    }
    public void  memberExecutiveClass() {
        MemberFilterActivity.MemberExecutiveTrackClass ru = new MemberFilterActivity.MemberExecutiveTrackClass();
        ru.execute("5");
    }
    class MemberExecutiveTrackClass extends AsyncTask<String, Void, String> {

        ServerClass ruc = new ServerClass();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(TAG, "onPreExecute");
            // showProgressDialog();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.v(TAG, String.format("onPostExecute :: response = %s", response));
            // dismissProgressDialog();
            //Toast.makeText(Employee.this, response, Toast.LENGTH_LONG).show();
            MemberExecutiveDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
           // Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> MemberExecutiveDetails = new HashMap<String, String>();
            MemberExecutiveDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(MemberFilterActivity.this));
            MemberExecutiveDetails.put("action", "show_member_executive_list_for_member");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(MemberFilterActivity.this);
            //MemberExecutiveloyeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(MemberExecutiveloyee.this));
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, MemberExecutiveDetails);
            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }


    private void MemberExecutiveDetails(String jsonResponse) {


        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
//        RelativeLayout relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayoutPrabhagDetails);
        if (jsonResponse != null) {


            try {
                Log.v(TAG, "JsonResponseOpeartion :: test");
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    if (object != null) {
                        JSONArray jsonArrayCountry = object.getJSONArray("result");
                        MemExecutiveNameArrayList.clear();
                        //for(int j=0;j<2;j++){
                        spinMemExecutiveNameList = new Spinner_List();

                        spinMemExecutiveNameList.setName(getResources().getString(R.string.prompt_mem_executive));

                        MemExecutiveNameArrayList.add(0,spinMemExecutiveNameList);

                        // }
                        if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
                            spinMemExecutiveNameList.setName(getResources().getString(R.string.all));

                            MemExecutiveNameArrayList.add(1,spinMemExecutiveNameList);
                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                spinMemExecutiveNameList = new Spinner_List();
                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String MemberOwnerExecutive     = jsonObj.getString("MemberOwnerExecutive");

                                    // String id=jsonObj.getString("Auto_Id");
//                               if(i==0){
//                                   spinMemExecutiveNameList.setName(getResources().getString(R.string.promt_country));
//                                   enqF.add(0,spinMemExecutiveNameList);
//                               }


                                    spinMemExecutiveNameList.setName(MemberOwnerExecutive);
                                    //spinMemExecutiveNameList.setId(id);

                                    MemExecutiveNameArrayList.add(spinMemExecutiveNameList);

                                    memExecutiveAdapter = new SpinnerAdapter(MemberFilterActivity.this, MemExecutiveNameArrayList){
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
                                                tv.setText(getResources().getString(R.string.prompt_mem_executive));
                                                // tv.setTextColor(Color.GRAY);
                                            }
                                            else {
                                                tv.setTextColor(Color.BLACK);
                                            }
                                            return view;
                                        }

                                    };
                                    spinMemExecutive.setAdapter(memExecutiveAdapter);


                                }
                            }
                        }else if(jsonArrayCountry.length()==0){
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    MemExecutiveNameArrayList.clear();
                    //for(int j=0;j<2;j++){
                    spinMemExecutiveNameList = new Spinner_List();

                    spinMemExecutiveNameList.setName(getResources().getString(R.string.prompt_mem_executive));

                    MemExecutiveNameArrayList.add(0,spinMemExecutiveNameList);
                    spinMemExecutiveNameList.setName(getResources().getString(R.string.all));

                    MemExecutiveNameArrayList.add(1,spinMemExecutiveNameList);
                    memExecutiveAdapter = new SpinnerAdapter(MemberFilterActivity.this, MemExecutiveNameArrayList){
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
                                tv.setText(getResources().getString(R.string.prompt_mem_executive));
                                // tv.setTextColor(Color.GRAY);
                            }
                            else {
                                tv.setTextColor(Color.BLACK);
                            }
                            return view;
                        }

                    };
                    spinMemExecutive.setAdapter(memExecutiveAdapter);
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
            }
        }
    }
    public void  SearchEnquiryClass() {
        MemberFilterActivity.SearchEnquiryTrackClass ru = new MemberFilterActivity.SearchEnquiryTrackClass();
        ru.execute("5");
    }


    class SearchEnquiryTrackClass extends AsyncTask<String, Void, String> {

        ServerClass ruc = new ServerClass();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(TAG, "onPreExecute");
            //showProgressDialog();
            viewDialog.showDialog();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.v(TAG, String.format("onPostExecute :: response = %s", response));
           // dismissProgressDialog();
            viewDialog.hideDialog();
            //Toast.makeText(CandiateListView.this, response, Toast.LENGTH_LONG).show();
            //  Toast.makeText(NewCustomerActivity.this, response, Toast.LENGTH_LONG).show();
            SearchEnquiryDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> SearchEnquiryDetails = new HashMap<String, String>();
            SearchEnquiryDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(MemberFilterActivity.this));
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(MemberFilterActivity.this)));
            SearchEnquiryDetails.put("to_date",todate.getText().toString());
            Log.v(TAG, String.format("doInBackground :: to_date = %s",todate.getText().toString() ));
            SearchEnquiryDetails.put("from_date",fromdate.getText().toString());
            Log.v(TAG, String.format("doInBackground :: from_date = %s", fromdate.getText().toString()));
            SearchEnquiryDetails.put("gender", gender);
            Log.v(TAG, String.format("doInBackground :: gender = %s", gender));
            SearchEnquiryDetails.put("occupation",occupation);
            Log.v(TAG, String.format("doInBackground :: occupation = %s", occupation));
            SearchEnquiryDetails.put("mem_date_type",Datewise);
            Log.v(TAG, String.format("doInBackground :: mem_date_type = %s",Datewise));
            SearchEnquiryDetails.put("blood_group",bloodgroup);
            Log.v(TAG, String.format("doInBackground :: bloodgroup = %s",bloodgroup));
            SearchEnquiryDetails.put("enq_exe_name",enqExecutiveName);
            Log.v(TAG, String.format("doInBackground :: enq_exe_name = %s",enqExecutiveName));
            SearchEnquiryDetails.put("mem_exe_name",memExecutiveName);
            Log.v(TAG, String.format("doInBackground :: mem_exe_name = %s",memExecutiveName));
            SearchEnquiryDetails.put("status",status);
            Log.v(TAG, String.format("doInBackground :: status = %s",status));
            SearchEnquiryDetails.put("action", "search_member_filter");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(MemberFilterActivity.this);
            String loginResult2 = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, SearchEnquiryDetails);

            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult2));
            return loginResult2;
        }
    }


    private void SearchEnquiryDetails(String jsonResponse) {

        Log.v(TAG, String.format("loginServerResponse :: response = %s", jsonResponse));

        JSONObject object = null;
        try {
            object = new JSONObject(jsonResponse);
            String success = object.getString(getResources().getString(R.string.success));

            if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                //Toast.makeText(MemberFilterActivity.this,"Enquiry added succesfully",Toast.LENGTH_SHORT).show();

                if (object != null) {
                    JSONArray jsonArrayResult = object.getJSONArray("result");
                               Log.d(TAG, "array size: " + jsonArrayResult.length());
                    if (jsonArrayResult != null && jsonArrayResult.length() > 0) {
                        for (int i = 0; i < jsonArrayResult.length(); i++) {


                            subList = new MemberDataList();
                            Log.d(TAG, "i: " + i);
                            // Log.d(TAG, "run: " + itemCount);
                            Log.v(TAG, "JsonResponseOpeartion ::");
                            JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                            if (jsonObj != null) {

                                String name = jsonObj.getString("Name");
                                String gender = jsonObj.getString("Gender");
                                String Contact = jsonObj.getString("Contact");
                                String DOB = jsonObj.getString("DOB");
                                String ExecutiveName = jsonObj.getString("ExecutiveName");
                                String BloodGroup = jsonObj.getString("BloodGroup");
                                String occupation = jsonObj.getString("Occupation");
                                String MemberID = jsonObj.getString("MemberID");
                                String Image = jsonObj.getString("Image");
                                String status=jsonObj.getString("MemberStatus");
                                String Email=jsonObj.getString("Email");
                                String End_Date=jsonObj.getString("End_Date");
                                String FinalBalance=jsonObj.getString("FinalBalance");
                                //  for (int j = 0; j < 5; j++) {

                                subList.setName(name);
                                subList.setGender(gender);
                                String cont=Utility.lastFour(Contact);
                                subList.setContact(Contact);
                                subList.setContactEncrypt(cont);
                                String dob= Utility.formatDate(DOB);
                                subList.setBirthDate(dob);
                                subList.setExecutiveName(ExecutiveName);
                                subList.setBlodGroup(BloodGroup);
                                subList.setOccupation(occupation);
                                subList.setID(MemberID);
                                Image.replace("\"", "");
                                subList.setImage(Image);
                                subList.setStatus(status);
                                subList.setEmail(Email);
                                String enddate= Utility.formatDateDB(End_Date);
                                subList.setEndDate(enddate);
                                subList.setFinalBalance(FinalBalance);
                                //Toast.makeText(EnquiryActivity.this, "followup date: "+next_foll_date, Toast.LENGTH_SHORT).show();
                                subListArrayList.add(subList);


                            }
                        }
                        Intent intent=new Intent(MemberFilterActivity.this,MemberActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("filter_array_list", subListArrayList);
                        intent.putExtra("BUNDLE",bundle);
                        startActivity(intent);

                    } else if (jsonArrayResult.length() == 0) {
                        System.out.println("No records found");
                    }
                }

                // showCustomDialog();

                //inputEmail, inputPhone,inputAdd,inputReq,inputFollowupdate;
            }
            else if (success.equalsIgnoreCase(getResources().getString(R.string.zero)))
            {
                //Toast.makeText(MemberFilterActivity.this,"Mobile Number Already Exits",Toast.LENGTH_SHORT).show();
                //inputContact.getText().clear();
                Toast.makeText(MemberFilterActivity.this,"No Records Found",Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onSupportNavigateUp(){
        Intent intent=new Intent(MemberFilterActivity.this, MemberActivity.class);
        startActivity(intent);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(MemberFilterActivity.this,MemberActivity.class);
        startActivity(intent);
    }
}
