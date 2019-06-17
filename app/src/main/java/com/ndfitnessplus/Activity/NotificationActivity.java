package com.ndfitnessplus.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ndfitnessplus.Activity.Notification.ActiveMemberActivity;
import com.ndfitnessplus.Activity.Notification.DeactiveMemberActivity;
import com.ndfitnessplus.Activity.Notification.DoneFollowupActivity;
import com.ndfitnessplus.Activity.Notification.EnquiryFollowupActivity;
import com.ndfitnessplus.Activity.Notification.MemberBirthdayActivity;
import com.ndfitnessplus.Activity.Notification.MembershipEndDateActivity;
import com.ndfitnessplus.Activity.Notification.OtherFollowupActivity;
import com.ndfitnessplus.Activity.Notification.PaymentDateActivity;
import com.ndfitnessplus.Activity.Notification.RenewFollowupActivity;
import com.ndfitnessplus.Activity.Notification.StaffBirthdayActivity;
import com.ndfitnessplus.Activity.Notification.TodaysEnrollmentActivity;
import com.ndfitnessplus.Activity.Notification.TodaysEnquiryActivity;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.ServerClass;
import com.ndfitnessplus.Utility.ServiceUrls;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;
import com.ndfitnessplus.Utility.ViewDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.ndfitnessplus.Utility.HTTPRequestQueue.isOnline;

public class NotificationActivity extends AppCompatActivity {
    private ViewPager view_pager;
    private TabLayout tab_layout;
    private ProgressDialog pd;
    private static final String TAG = NotificationActivity.class.getSimpleName();

    int count=5;
    public String[] title ={"Member Birthday", "Staff Birthday", "Anniversary", "Enquiry","Enquiry Followup","Todays Admission",
            "Active Member","Deactive Member","Membership End Date","Payment Date","Post Dated Cheque","Other Followup"};
    public static int[] tabicons = {R.drawable.ic_cake_black,R.drawable.birthdaycake, R.drawable.anniversary, R.drawable.enquiry
            ,R.drawable.followup,R.drawable.admission,R.drawable.activemember,R.drawable.deactivemember,R.drawable.mem_end_date,R.drawable.payment_date,R.drawable.cheque,R.drawable.otherfollowup};

    //Notification Menu
    LinearLayout member_bday,staff_bday,enquiry,enquiry_followup,todays_admission,active_member,deactive_member,mem_end_date,payment_date,renew_followup,other_followup,done_followup;
    TextView mem_bday_count,staff_bday_count,enquiry_count,enq_foll_count,todays_admission_count,active_mem_count,
    deactive_mem_count,mem_end_date_count,payment_date_count,renew_followup_count,other_foll_count,done_foll_count;

    //Loading gif
    ViewDialog viewDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        initToolbar();
        initComponent();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.noti));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private void initComponent() {
         //intialization
        member_bday=findViewById(R.id.mem_bday);
        staff_bday=findViewById(R.id.staff_bday);
        //anniversary=findViewById(R.id.anniversary);
        enquiry=findViewById(R.id.todays_enquiry);
        enquiry_followup=findViewById(R.id.enquiry_followup);
        todays_admission=findViewById(R.id.todays_admission);
        active_member=findViewById(R.id.activemember);
        deactive_member=findViewById(R.id.deactivemember);
        mem_end_date=findViewById(R.id.mem_end_date);
        payment_date=findViewById(R.id.paymentdate);
        renew_followup=findViewById(R.id.renew_followup);
        other_followup=findViewById(R.id.other_followup);
        done_followup=findViewById(R.id.done_followup);
        viewDialog = new ViewDialog(this);
        //count textviews
        mem_bday_count=findViewById(R.id.mem_bday_count);
        staff_bday_count=findViewById(R.id.staff_bday_count);
        //anniversary_count=findViewById(R.id.anniversary_count);
        enquiry_count=findViewById(R.id.enq_count);
        enq_foll_count=findViewById(R.id.enquiry_follwup_count);
        todays_admission_count=findViewById(R.id.todays_admission_count);
        active_mem_count=findViewById(R.id.active_count);
        deactive_mem_count=findViewById(R.id.deactive_mem_count);
        mem_end_date_count=findViewById(R.id.mem_end_date_count);
        payment_date_count=findViewById(R.id.paymentdate_count);
        renew_followup_count=findViewById(R.id.renew_followup_count);
        other_foll_count=findViewById(R.id.other_followup_count);
        done_foll_count=findViewById(R.id.done_followup_count);


        if (isOnline(NotificationActivity.this)) {
            countclass();// check login details are valid or not from server
        }
        else {
            //Toast.makeText(EnquiryActivity.this, R.string.internet_unavailable, Toast.LENGTH_LONG).show();
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(NotificationActivity.this);
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
        member_bday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(NotificationActivity.this, MemberBirthdayActivity.class);
                startActivity(intent);
            }
        });

        staff_bday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(NotificationActivity.this, StaffBirthdayActivity.class);
                startActivity(intent);
            }
        });
//        anniversary.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(NotificationActivity.this, AnniversaryActivity.class);
//                startActivity(intent);
//            }
//        });
        enquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(NotificationActivity.this, TodaysEnquiryActivity.class);
                startActivity(intent);
            }
        });
        enquiry_followup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(NotificationActivity.this, EnquiryFollowupActivity.class);
                startActivity(intent);
            }
        });
        todays_admission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(NotificationActivity.this, TodaysEnrollmentActivity.class);
                startActivity(intent);
            }
        });
        active_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(NotificationActivity.this, ActiveMemberActivity.class);
                startActivity(intent);
            }
        });
        deactive_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(NotificationActivity.this, DeactiveMemberActivity.class);
                startActivity(intent);
            }
        });
        mem_end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(NotificationActivity.this, MembershipEndDateActivity.class);
                startActivity(intent);
            }
        });
        payment_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(NotificationActivity.this, PaymentDateActivity.class);
                startActivity(intent);
            }
        });
        renew_followup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(NotificationActivity.this, RenewFollowupActivity.class);
                startActivity(intent);
            }
        });
        other_followup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(NotificationActivity.this, OtherFollowupActivity.class);
                startActivity(intent);
            }
        });
        done_followup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(NotificationActivity.this, DoneFollowupActivity.class);
                startActivity(intent);
            }
        });
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
                Intent intent = new Intent(NotificationActivity.this,MainActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void showProgressDialog() {
        Log.v(TAG, String.format("showProgressDialog"));
        pd = new ProgressDialog(NotificationActivity.this);
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

    private void countclass() {
        NotificationActivity.CountTrackclass ru = new NotificationActivity.CountTrackclass();
        ru.execute("5");
    }
    class CountTrackclass extends AsyncTask<String, Void, String> {

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
            //dismissProgressDialog();
            viewDialog.hideDialog();
            //Toast.makeText(Employee.this, response, Toast.LENGTH_LONG).show();
            CountDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> CountDetails = new HashMap<String, String>();
            CountDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(NotificationActivity.this));
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(NotificationActivity.this)));
            CountDetails.put("action","show_notification_count");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(NotificationActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, CountDetails);
            //Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }

    private void CountDetails(String jsonResponse) {

        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
//        RelativeLayout relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayoutPrabhagDetails);
        if (jsonResponse != null) {


            try {
                Log.v(TAG, "JsonResponseOpeartion :: test");
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    if (object != null) {
                        JSONArray jsonArrayResult = object.getJSONArray("result");

                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {
                            for (int i = 0; i < jsonArrayResult.length(); i++) {

                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {

                                    String enquiry_followup_count = jsonObj.getString("enquiry_followup_count");
                                    String todays_enq_cnt = jsonObj.getString("todays_enq_cnt");
                                    String done_followup_cnt = jsonObj.getString("done_followup_cnt");
                                    String member_birthday = jsonObj.getString("member_birthday");
                                    String active_member = jsonObj.getString("active_member");
                                    String deactive_member = jsonObj.getString("deactive_member");
                                    String todays_enddate = jsonObj.getString("todays_enddate");
                                    String payment_date = jsonObj.getString("payment_date");
                                    String staff_birthday = jsonObj.getString("staff_birthday");
                                    String enquiry_enrollment = jsonObj.getString("enquiry_enrollment");
                                    String other_followup = jsonObj.getString("other_followup");
                                    String renew_followup = jsonObj.getString("renew_followup");

                                    enq_foll_count.setText(enquiry_followup_count);
                                    enquiry_count.setText(todays_enq_cnt);
                                    done_foll_count.setText(done_followup_cnt);
                                    mem_bday_count.setText(member_birthday);
                                    active_mem_count.setText(active_member);
                                    deactive_mem_count.setText(deactive_member);
                                    mem_end_date_count.setText(todays_enddate);
                                    payment_date_count.setText(payment_date);
                                    staff_bday_count.setText(staff_birthday);
                                    todays_admission_count.setText(enquiry_enrollment);
                                    other_foll_count.setText(other_followup);
                                    renew_followup_count.setText(renew_followup);

                                }
                            }
                        } else if (jsonArrayResult.length() == 0) {
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    //nodata.setVisibility(View.VISIBLE);
                    //.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
            }
        }
    }
    @Override
    public boolean onSupportNavigateUp(){
        Intent intent=new Intent(NotificationActivity.this,MainActivity.class);
        startActivity(intent);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(NotificationActivity.this,MainActivity.class);
        startActivity(intent);
    }

}
