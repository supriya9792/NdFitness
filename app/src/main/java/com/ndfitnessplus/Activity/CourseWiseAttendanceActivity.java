package com.ndfitnessplus.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ndfitnessplus.Model.BalanceTrasactionList;
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
import java.util.List;

import sun.bob.mcalendarview.MarkStyle;
import sun.bob.mcalendarview.listeners.OnDateClickListener;
import sun.bob.mcalendarview.listeners.OnExpDateClickListener;
import sun.bob.mcalendarview.listeners.OnMonthScrollListener;
import sun.bob.mcalendarview.views.ExpCalendarView;
import sun.bob.mcalendarview.vo.DateData;

public class CourseWiseAttendanceActivity extends AppCompatActivity {
    private TextView YearMonthTv;
    private ExpCalendarView expCalendarView;
    private DateData selectedDate;
    ViewDialog viewDialog;
    String invoice_id;
    String member_id;
    String FinancialYear;
    String start_date="";
    String end_date="";
    String RemainingSession="";
    TextView missed,attended,remaining;
    public final String TAG = CourseWiseAttendanceActivity.class.getName();
    ImageButton backmonth, nextmonth;
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_wise_attendance);

        initToolbar();
        expCalendarView = ((ExpCalendarView) findViewById(R.id.calendar_exp));
        YearMonthTv = (TextView) findViewById(R.id.year_month);
        Intent intent = getIntent();
        if(intent!=null){
            invoice_id = intent.getStringExtra("invoice_id");
            FinancialYear =intent.getStringExtra("financial_yr");
            member_id = intent.getStringExtra("member_id");
            start_date = intent.getStringExtra("start_date");
            end_date = intent.getStringExtra("end_date");
            RemainingSession = intent.getStringExtra("remaining_session");
        }

        this.viewDialog = new ViewDialog(this);

        missed=(TextView)findViewById(R.id.missed);
        attended=(TextView)findViewById(R.id.attended);
        remaining=(TextView)findViewById(R.id.remaining);

        backmonth=(ImageButton)findViewById(R.id.backmonth);
        nextmonth=(ImageButton)findViewById(R.id.nextmonth);

        missed.setVisibility(View.GONE);

        Calendar cal=Calendar.getInstance();
        expCalendarView.getMarkedDates().removeAdd();
        SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
        String month_name = month_date.format(cal.getTime());
        String monthyear=month_name+" "+Calendar.getInstance().get(Calendar.YEAR);
        YearMonthTv.setText(monthyear);
        attedanceDetailsclass();

        // Set up listeners.
        expCalendarView.setOnDateClickListener(new OnExpDateClickListener()).setOnMonthScrollListener(new OnMonthScrollListener() {
            @Override
            public void onMonthChange(int year, int month) {

                String dateStr=month+" "+year;
                SimpleDateFormat fmtOut = new SimpleDateFormat("MM yyyy");
                Date date = null;
                try {
                    date = fmtOut.parse(dateStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
                String month_name = month_date.format(date);
                String monthyear=month_name+" "+year;
                YearMonthTv.setText(monthyear);

            }

            @Override
            public void onMonthScroll(float positionOffset) {
//                Log.i("listener", "onMonthScroll:" + positionOffset);
            }
        });

        expCalendarView.setOnDateClickListener(new OnDateClickListener() {
            @Override
            public void onDateClick(View view, DateData date) {
                // expCalendarView.getMarkedDates().removeAdd();
                //expCalendarView.markDate(date);
                selectedDate = date;
            }
        });
        backmonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });

        Calendar calendar = Calendar.getInstance();
        selectedDate = new DateData(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
        // expCalendarView.markDate(selectedDate);

    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.attendance));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.attendance_list_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_home) {
            Intent intent = new Intent(CourseWiseAttendanceActivity.this, MainActivity.class);
            startActivity(intent);
        }else if(id ==R.id.action_attendance){
            Intent intent = new Intent(CourseWiseAttendanceActivity.this, CourseWiseAttendanceDetailsActivity.class);
            intent.putExtra("invoice_id",invoice_id);
            intent.putExtra("financial_yr",FinancialYear);
            intent.putExtra("member_id",member_id);
            startActivity(intent);
        }else if(id== android.R.id.home){
            //Toast.makeText(this,"Navigation back pressed",Toast.LENGTH_SHORT).show();
            // NavUtils.navigateUpFromSameTask(this);
            finish();
        }

        return true;
    }
    private void attedanceDetailsclass() {
        AttendanceTrackclass ru = new AttendanceTrackclass();
        ru.execute("5");
    }

    class AttendanceTrackclass extends AsyncTask<String, Void, String> {


        ServerClass ruc = new ServerClass();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(TAG, "onPreExecute");
            // showProgressDialog();
            viewDialog.showDialog();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.v(TAG, String.format("onPostExecute :: show_balance_trasaction_details = %s", response));
            //  dismissProgressDialog();
            viewDialog.hideDialog();
            //Toast.makeText(Employee.this, response, Toast.LENGTH_LONG).show();
            AttendanceDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> AttendanceDetails = new HashMap<String, String>();
            AttendanceDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(CourseWiseAttendanceActivity.this));
            AttendanceDetails.put("member_id",member_id );
            AttendanceDetails.put("invoice_id",invoice_id );
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(CourseWiseAttendanceActivity.this)));
            Log.v(TAG, String.format("doInBackground :: member_id = %s", member_id));
            Log.v(TAG, String.format("doInBackground :: invoice_id = %s", invoice_id));
            AttendanceDetails.put("action","show_attendance");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(CourseWiseAttendanceActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, AttendanceDetails);
            Log.v(TAG, String.format("doInBackground :: show_attendance= %s", loginResult));
            return loginResult;
        }


    }

    private void AttendanceDetails(String jsonResponse) {

        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
//        RelativeLayout relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayoutPrabhagDetails);
        if (jsonResponse != null) {
            ArrayList<String> stringArrayListPresent = new ArrayList<>();

            try {
                Log.v(TAG, "JsonResponseOpeartion :: test");
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    //progressBar.setVisibility(View.GONE);
                    if (object != null) {
                        JSONArray jsonArrayResult = object.getJSONArray("result");
//                        if(jsonArrayResult.length() >10){
//                            totalPage=jsonArrayResult.length()/10;
//                        }
                        ArrayList<BalanceTrasactionList> item = new ArrayList<BalanceTrasactionList>();
                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {
                            String yest="";
                            for (int i = 0; i < jsonArrayResult.length(); i++) {



                                Log.d(TAG, "i: " + i);

                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {

                                    String Name = jsonObj.getString("Name");
                                    String Contact = jsonObj.getString("Contact");
                                    String PackageName = jsonObj.getString("PackageName");
                                    String InDateTime = jsonObj.getString("InDateTime");
                                    String AttendanceDate = jsonObj.getString("AttendanceDate");
                                    String MemberID = jsonObj.getString("MemberID");
                                    String Remaining_Session = jsonObj.getString("Remaining_Session");
                                    String Start_Date = jsonObj.getString("Start_Date");
                                    String End_Date = jsonObj.getString("End_Date");

                                    String adate=Utility.formatDateDB(AttendanceDate);
                                    stringArrayListPresent.add(AttendanceDate);
                                    String today=Utility.getCurrentDate();
                                    SimpleDateFormat dateFormat = new SimpleDateFormat(
                                            "dd-MM-yyyy");
                                    Date endDate = new Date();
                                    Date currentdate = new Date();
                                    String endc=Utility.formatDateDB(End_Date);
                                    try {
                                        endDate = dateFormat.parse(endc);
                                        currentdate = dateFormat.parse(Utility.getCurrentDate());
                                        Log.v(TAG, String.format(" ::endDate = %s", endDate));
                                        Log.v(TAG, String.format(" :: currentdate = %s",currentdate));
                                        if (currentdate.before(endDate)|| currentdate.equals(endDate) ) {
                                            if(today.equals(adate)){
                                                yest=AttendanceDate;
                                            }else{
                                                yest= Utility.getyesterdayDate();
                                            }
                                        }
                                        else {
                                            yest= End_Date;
                                        }
                                    } catch (ParseException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }


                                    start_date=Start_Date;
                                    //  for (int j = 0; j < 5; j++) {
                                    String att="Attended:"+String.valueOf(stringArrayListPresent.size());
                                    if(Remaining_Session.equals("null") ){
                                        Remaining_Session="0";
                                    }
                                    String rem="Remaning:"+Remaining_Session;
                                    attended.setText(att);
                                    remaining.setText(rem);

                                }
                            }
                            // String yest=Utility.getyesterdayDate();
                            showAttendance(start_date,yest,stringArrayListPresent);
                        } else if (jsonArrayResult.length() == 0) {
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    String today=Utility.getCurrentDate();
                    String yest="";
                    SimpleDateFormat dateFormat = new SimpleDateFormat(
                            "dd-MM-yyyy");
                    Date endDate = new Date();
                    Date currentdate = new Date();
                    String endc=Utility.formatDateDB(end_date);
                    try {
                        endDate = dateFormat.parse(endc);
                        currentdate = dateFormat.parse(Utility.getCurrentDate());
                        Log.v(TAG, String.format(" ::endDate = %s", endDate));
                        Log.v(TAG, String.format(" :: currentdate = %s",currentdate));
                        if (currentdate.before(endDate)|| currentdate.equals(endDate) ) {

                                yest= Utility.getyesterdayDate();

                        }
                        else {
                            yest= end_date;
                        }
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    // yest=Utility.getyesterdayDate();
                    String sdate=start_date;
                    showAttendance(sdate,yest,stringArrayListPresent);
                    String rem="Remaning:"+RemainingSession;
                    remaining.setText(rem);
                    //nodata.setVisibility(View.VISIBLE);
                    // recyclerView.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
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
    public  void showAttendance(String startdate,String today,ArrayList<String> attendancearray){

        List<Date> dates = getDates(startdate,today);
        //IF you don't want to reverse then remove Collections.reverse(dates);

        System.out.println(dates.size());
        for(Date date:dates)
        {
            System.out.println(date);
            for (int i = 0; i < attendancearray.size(); i++) {

                try {
                    Date parsedDate = dateFormat.parse(attendancearray.get(i));
                    SimpleDateFormat fmtOut = new SimpleDateFormat("dd-MM-yyyy");
                    String adate= fmtOut.format(parsedDate);
                    String ad[]=adate.split("-");
                    int day= Integer.parseInt(ad[0]);
                    int month= Integer.parseInt(ad[1]);
                    int year= Integer.parseInt(ad[2]);
                    Log.e("date   ============", parsedDate + "");
                    //Log.e("date   ============", dateFormat.format(parsedDate));
                    if(parsedDate.equals(date)){
                        expCalendarView.setMarkedStyle(MarkStyle.RIGHTSIDEBAR)
                                .markDate(new DateData(year, month, day).setMarkStyle(new MarkStyle(MarkStyle.BACKGROUND,getResources().getColor(R.color.green))))
                        ;
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
            int miss=dates.size()-attendancearray.size();
            String misse="Missed:"+miss;
            missed.setText(misse);
            SimpleDateFormat fmtOut = new SimpleDateFormat("dd-MM-yyyy");
            String adate= fmtOut.format(date);
            String ad[]=adate.split("-");
            int day= Integer.parseInt(ad[0]);
            int month= Integer.parseInt(ad[1]);
            int year= Integer.parseInt(ad[2]);
            expCalendarView.setMarkedStyle(MarkStyle.RIGHTSIDEBAR)
                    .markDate(new DateData(year, month, day).setMarkStyle(new MarkStyle(MarkStyle.BACKGROUND, getResources().getColor(R.color.red))))
            ;
        }
    }
    public static List<Date> getDates(String fromDate, String toDate)
    {
        ArrayList<Date> dates = new ArrayList<Date>();

        try {

            Calendar fromCal = Calendar.getInstance();
            fromCal.setTime(dateFormat .parse(fromDate));

            Calendar toCal = Calendar.getInstance();
            toCal.setTime(dateFormat .parse(toDate));

            while(!fromCal.after(toCal))
            {
                dates.add(fromCal.getTime());
                fromCal.add(Calendar.DATE, 1);
            }


        } catch (Exception e) {
            System.out.println(e);
        }
        return dates;
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        //dismissProgressDialog();
        finish();
    }
}