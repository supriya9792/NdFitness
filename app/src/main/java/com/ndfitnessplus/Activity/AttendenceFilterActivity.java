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

import com.ndfitnessplus.Adapter.SpinnerAdapter;
import com.ndfitnessplus.Model.AttendanceList;
import com.ndfitnessplus.Model.MeasurementList;
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

public class AttendenceFilterActivity extends AppCompatActivity {
    public final String TAG = AttendenceFilterActivity.class.getName();
    private ProgressDialog pd;
    //Spinner Adapter
    public Spinner spinDateWise,spinAttendaceMode;
    Spinner_List dateWiselist,AttendaceModeList;
    ArrayList<Spinner_List> dateWiseArrayList = new ArrayList<Spinner_List>();
    ArrayList<Spinner_List> AttendaceModeArrayList = new ArrayList<Spinner_List>();


    public SpinnerAdapter datewiseadapter,AttendaceModeAdapter;
    String Datewise,AttendaceModeName;

    TextView todate,fromdate;
    ImageButton toDatebtn,fromDateBtn;
    private int mYear, mMonth, mDay;
    String month,day;
    ArrayList<AttendanceList> subListArrayList = new ArrayList<AttendanceList>();
    AttendanceList subList;
    Button btn_applyFilter;

    //Loading gif
    ViewDialog viewDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_attendence_filter);
        initToolbar();
        initComponent();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.attendance_filters));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private void initComponent() {

        //spinners
        spinDateWise = (Spinner) findViewById(R.id.spinner_date_wise);
        spinAttendaceMode = (Spinner) findViewById(R.id.spinner_att_mode);

        viewDialog = new ViewDialog(this);

        todate = findViewById(R.id.to_date);
        fromdate = findViewById(R.id.from_date);
        fromDateBtn = findViewById(R.id.btn_from_date);
        toDatebtn = findViewById(R.id.btn_to_date);
        btn_applyFilter = findViewById(R.id.btn_apply_filters);

        String firstday = Utility.getFirstDayofMonth();
        todate.setText(firstday);
        String curr_date = Utility.getCurrentDate();
        fromdate.setText(curr_date);


        //date pickers for to date and from date
        toDatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(AttendenceFilterActivity.this,
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


                DatePickerDialog datePickerDialog = new DatePickerDialog(AttendenceFilterActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String date=(year + "-"
                                        + (monthOfYear + 1) + "-" + dayOfMonth).toString();
                                String cdate=Utility.formatDateDB(date);
                                fromdate.setText(cdate);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        //Date Wise date type spinners
        final  String[] memberDateArray = getResources().getStringArray(R.array.attendance_date_array);

        for(int i=0;i<memberDateArray.length;i++) {
            dateWiselist = new Spinner_List();
            dateWiselist.setName(memberDateArray[i]);
            dateWiseArrayList.add(dateWiselist);
            datewiseadapter = new SpinnerAdapter(AttendenceFilterActivity.this, dateWiseArrayList) {
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
            spinDateWise.setAdapter(datewiseadapter);
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
        //api of spinners
        //executiveClass();


        //Date Wise date type spinners
        final  String[] AttendaceModeArray = getResources().getStringArray(R.array.attendance_mode_array);

        for(int i=0;i<AttendaceModeArray.length;i++) {
            AttendaceModeList = new Spinner_List();
            AttendaceModeList.setName(AttendaceModeArray[i]);
            AttendaceModeArrayList.add(AttendaceModeList);
            AttendaceModeAdapter = new SpinnerAdapter(AttendenceFilterActivity.this, AttendaceModeArrayList) {
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
                        tv.setText(getResources().getString(R.string.prompt_att_mode));
                        // tv.setTextColor(Color.GRAY);
                    } else {
                        tv.setTextColor(Color.BLACK);
                    }
                    return view;
                }

            };
            spinAttendaceMode.setAdapter(AttendaceModeAdapter);
        }
        spinAttendaceMode.setSelection(1);

        spinAttendaceMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int index = parent.getSelectedItemPosition();
                if(view != null) {
                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                    tv.setTextSize(10);
                    if(index==0){
                        tv.setText(getResources().getString(R.string.prompt_att_mode));
                    }
//                tv.setTextColor(getResources().getColor(R.color.black));
                    AttendaceModeName = tv.getText().toString();
                    if((AttendaceModeName.equals(getResources().getString(R.string.prompt_att_mode)))||
                            (AttendaceModeName.equals(getResources().getString(R.string.all)))){
                        AttendaceModeName="";
                    }
                    // ((TextView) spinEnquiryType.getSelectedView()).setTextColor(getResources().getColor(R.color.white));
                    // Showing selected spinner item
                    //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
                }}

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        btn_applyFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchAttendanceClass();
            }
        });
    }
    public void CampareTwoDates(){
        //******************campare two dates****************
//        String date = "03/26/2012 11:00:00";
//        String dateafter = "03/26/2012 11:59:00";
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

    public void  SearchAttendanceClass() {
        AttendenceFilterActivity.SearchAttendanceTrackClass ru = new AttendenceFilterActivity.SearchAttendanceTrackClass();
        ru.execute("5");
    }


    class SearchAttendanceTrackClass extends AsyncTask<String, Void, String> {

        ServerClass ruc = new ServerClass();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(TAG, "onPreExecute");
            //   showProgressDialog();
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
            SearchAttendanceDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
          //  Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> SearchAttendanceDetails = new HashMap<String, String>();
            SearchAttendanceDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(AttendenceFilterActivity.this));
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(AttendenceFilterActivity.this)));
            SearchAttendanceDetails.put("to_date",todate.getText().toString());
            Log.v(TAG, String.format("doInBackground :: to_date = %s",todate.getText().toString() ));
            SearchAttendanceDetails.put("from_date",fromdate.getText().toString());
            Log.v(TAG, String.format("doInBackground :: from_date = %s", fromdate.getText().toString()));
            SearchAttendanceDetails.put("date_wise", Datewise);
            Log.v(TAG, String.format("doInBackground :: Datewise = %s", Datewise));
            SearchAttendanceDetails.put("attendance_mode",AttendaceModeName);
            Log.v(TAG, String.format("doInBackground :: exe_name = %s",AttendaceModeName));
            SearchAttendanceDetails.put("action", "search_attendance_filter");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(AttendenceFilterActivity.this);
            String loginResult2 = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, SearchAttendanceDetails);

            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult2));
            return loginResult2;
        }
    }


    private void SearchAttendanceDetails(String jsonResponse) {

        Log.v(TAG, String.format("loginServerResponse :: response = %s", jsonResponse));

        JSONObject object = null;
        try {
            object = new JSONObject(jsonResponse);
            String success = object.getString(getResources().getString(R.string.success));

            if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                //Toast.makeText(AttendenceFilterActivity.this,"Attendance added succesfully",Toast.LENGTH_SHORT).show();

                if (object != null) {
                    JSONArray jsonArrayResult = object.getJSONArray("result");
//                        if(jsonArrayResult.length() >10){
//                            totalPage=jsonArrayResult.length()/10;
//                        }
                    if (jsonArrayResult != null && jsonArrayResult.length() > 0) {
                        for (int i = 0; i < jsonArrayResult.length(); i++) {

                            subList = new AttendanceList();
                            Log.d(TAG, "i: " + i);

                            Log.v(TAG, "JsonResponseOpeartion ::");
                            JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                            if (jsonObj != null) {

                                String MemberID = jsonObj.getString("MemberID");
                                String Contact = jsonObj.getString("Contact");
                                String Name = jsonObj.getString("Name");
                                String PackageName = jsonObj.getString("PackageName");
                                String InDateTime = jsonObj.getString("InDateTime");
                                String AttendanceDate = jsonObj.getString("AttendanceDate");
                                String Remaining_Session = jsonObj.getString("Remaining_Session");
                                String Start_Date = jsonObj.getString("Start_Date");
                                String End_Date = jsonObj.getString("End_Date");
                                String Attendance_Mode = jsonObj.getString("Attendance_Mode");
                                String Image = jsonObj.getString("Image");
                                String Status = jsonObj.getString("Status");

                                subList.setMemberID(MemberID);
                                subList.setContact(Contact);
                                String cont=Utility.lastFour(Contact);
                                subList.setContactEncrypt(cont);
                                subList.setName(Name);
                                subList.setPackageName(PackageName);
                                String[] timearr=InDateTime.split(" ");
                                SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm:ss");
                                SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
                                Date _24HourDt = null;
                                try {
                                    _24HourDt = _24HourSDF.parse(timearr[1]);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                System.out.println(_24HourDt);
                                System.out.println(_12HourSDF.format(_24HourDt));
                                String format12=_12HourSDF.format(_24HourDt);
                                subList.setTime(format12);
                                String adate=Utility.formatDate(AttendanceDate);
                                subList.setAttendanceDate(adate);
                                String edate=Utility.formatDate(End_Date);
                                subList.setExpiryDate(edate);
                                subList.setAttendanceMode(Attendance_Mode);
                                subList.setImage(Image);
                                subList.setStatus(Status);
                                //Toast.makeText(MeasurementActivity.this, "followup date: "+next_foll_date, Toast.LENGTH_SHORT).show();

                                //Toast.makeText(MainActivity.this, "j "+j, Toast.LENGTH_SHORT).show();
                                subListArrayList.add(subList);




                            }
                        }
                        Intent intent=new Intent(AttendenceFilterActivity.this,AttendenceActivity.class);
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
                //Toast.makeText(AttendenceFilterActivity.this,"Mobile Number Already Exits",Toast.LENGTH_SHORT).show();
                //inputContact.getText().clear();
                Toast.makeText(AttendenceFilterActivity.this,"No Records Found",Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        Intent intent=new Intent(AttendenceFilterActivity.this, AttendenceActivity.class);
        startActivity(intent);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(AttendenceFilterActivity.this,AttendenceActivity.class);
        startActivity(intent);
    }
}
