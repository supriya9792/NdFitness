package com.ndfitnessplus.Activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.FileUriExposedException;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mikhaellopez.circularimageview.CircularImageView;
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

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public class CourseCongratulationActivity extends AppCompatActivity {
   // private View root_view;
    String member_id,invoice_id,FinancialYear,filepath;
    TextView Name,Packagename,StartDate,EndDate,PaymentMode,Paid,InvoiceId,Date,SessionTv,DurationTv,MobileNo,BalanceTV,Total,MemberID;
    CircularImageView imageView;
    public static String TAG = CourseDetailsActivity.class.getName();
    ImageView sendWhatsapp;
    File pdfFile;
    ViewDialog viewDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_course_congratulation);

        Name=findViewById(R.id.name);
        Packagename=findViewById(R.id.package_name);
        StartDate=findViewById(R.id.start_date);
        EndDate=findViewById(R.id.end_date);
        Paid=findViewById(R.id.paid);
        InvoiceId=findViewById(R.id.invoice_idTV);
        Date=findViewById(R.id.reg_dateTV);
        SessionTv=findViewById(R.id.session);
        DurationTv=findViewById(R.id.duration);
        MobileNo=findViewById(R.id.contact);
        BalanceTV=findViewById(R.id.balance);
        MemberID=findViewById(R.id.member_id);
        imageView=findViewById(R.id.user_image);
        sendWhatsapp=findViewById(R.id.send_to_whatsapp);

        viewDialog=new ViewDialog(this);
        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        if (intent != null) {
            member_id=intent.getStringExtra("member_id");
            invoice_id=intent.getStringExtra("invoice_id");
            FinancialYear=intent.getStringExtra("financial_yr");
            pdfFile= (File) intent.getSerializableExtra("filepath");
            ;

        }
        coursedetailsclass();
        ((FloatingActionButton) findViewById(R.id.fab)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CourseCongratulationActivity.this, CourseActivity.class);
                startActivity(intent);

            }
        });
        sendWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                                try {

                    String toNumber = "+91"+MobileNo.getText().toString(); // contains spaces.
                    toNumber = toNumber.replace("+", "").replace(" ", "");

                    Intent sendIntent = new Intent("android.intent.action.MAIN");
                    if(Build.VERSION.SDK_INT>=24){
                        try{
                            Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                            m.invoke(null);
                            sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(pdfFile));
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }else{
                        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(pdfFile));

                    }
                    sendIntent.putExtra("jid", toNumber + "@s.whatsapp.net");
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.setPackage("com.whatsapp");
                    sendIntent.setType("application/pdf");
                    startActivity(sendIntent);

                }  catch (ActivityNotFoundException e) {
                    Toast.makeText(CourseCongratulationActivity.this, "WhatsApp not Installed", Toast.LENGTH_SHORT)
                            .show();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
    private void coursedetailsclass() {
        CourseCongratulationActivity.CourseDetailsTrackclass ru = new CourseCongratulationActivity.CourseDetailsTrackclass();
        ru.execute("5");
    }

    class  CourseDetailsTrackclass extends AsyncTask<String, Void, String> {


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
            CourseDetailsDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> CourseDetailsDetails = new HashMap<String, String>();
            CourseDetailsDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(CourseCongratulationActivity.this));
            CourseDetailsDetails.put("member_id",member_id );
            CourseDetailsDetails.put("invoice_id",invoice_id );
            CourseDetailsDetails.put("financial_yr",FinancialYear );
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(CourseCongratulationActivity.this)));
            Log.v(TAG, String.format("doInBackground :: member_id  = %s", member_id));
            String domainurl=SharedPrefereneceUtil.getDomainUrl(CourseCongratulationActivity.this);
            CourseDetailsDetails.put("action","show_course_details_by_member_id");
            String loginResult = ruc.sendPostRequest(domainurl+ ServiceUrls.LOGIN_URL,  CourseDetailsDetails);
            return loginResult;
        }


    }

    private void  CourseDetailsDetails(String jsonResponse) {

        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
        if (jsonResponse != null) {

                try {
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {

                    if (object != null) {
                        JSONArray jsonArrayResult = object.getJSONArray("result");

                        ArrayList<BalanceTrasactionList> item = new ArrayList<BalanceTrasactionList>();
                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {

                            for (int i = 0; i < jsonArrayResult.length(); i++) {

                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {

                                    String name = jsonObj.getString("Name");
                                    String RegistrationDate = jsonObj.getString("RegistrationDate");
                                    String Contact = jsonObj.getString("Contact");
                                    String Package_Name = jsonObj.getString("Package_Name");
                                    String ExecutiveName = jsonObj.getString("ExecutiveName");
                                    String Duration_Days = jsonObj.getString("Duration_Days");
                                    String Session = jsonObj.getString("Session");
                                    String Member_ID = jsonObj.getString("Member_ID");
                                    String Image = jsonObj.getString("Image");
                                    String Start_Date = jsonObj.getString("Start_Date");
                                    String End_Date = jsonObj.getString("End_Date");
                                    String Rate = jsonObj.getString("Rate");
                                    String Final_paid = jsonObj.getString("Final_paid");
                                    String Final_Balance = jsonObj.getString("Final_Balance");
                                    String Invoice_ID = jsonObj.getString("Invoice_ID");
                                    String Tax = jsonObj.getString("Tax");
                                    String Member_Email_ID = jsonObj.getString("Member_Email_ID");
                                    String Financial_Year = jsonObj.getString("Financial_Year");
                                    String PaymentType = jsonObj.getString("PaymentType");



                                    String sdate= Utility.formatDate(Start_Date);
                                    String edate=Utility.formatDate(End_Date);
                                    String todate=sdate+" to "+edate;

                                    String dur_sess="Duration: "+Duration_Days;
                                    String session="Session: "+Session;

                                    String reg_date= Utility.formatDate(RegistrationDate);



                                    if(Final_Balance.equals(".00")){
                                        Final_Balance="0.00";
                                    }
                                    String fbalance="Remaining : ₹ "+Final_Balance;

                                    Image.replace("\"", "");

                                    String fpaid="₹ "+Final_paid;
                                    String ttl="₹ "+Rate;

                                    invoice_id=Invoice_ID;

                                    member_id=Member_ID;
                                    String mid="ID: "+Member_ID;
                                    MemberID.setText(mid);

                                    Name.setText(name);
                                    MobileNo.setText(Contact);
                                    Packagename.setText(Package_Name);

                                    StartDate.setText(sdate);
                                    EndDate.setText(edate);

                                    Paid.setText(fpaid);
                                    String ino="Receipt No: "+Invoice_ID;
                                    InvoiceId.setText(ino);
                                    Date.setText(reg_date);
                                    SessionTv.setText(session);
                                    DurationTv.setText(dur_sess);

                                    BalanceTV.setText(fbalance);

                                    String domainurl= SharedPrefereneceUtil.getDomainUrl(CourseCongratulationActivity.this);
                                    String url= domainurl+ServiceUrls.IMAGES_URL + Image;


                                    RequestOptions requestOptions = new RequestOptions();
                                    requestOptions.placeholder(R.drawable.nouser);
                                    requestOptions.error(R.drawable.nouser);

                                    if(!(Image.equals("null")||Image.equals(""))){
                                        Glide.with(this)
                                                .setDefaultRequestOptions(requestOptions)
                                                .load(url).into(imageView);
                                    }

                                }
                            }
                        } else if (jsonArrayResult.length() == 0) {
                            System.out.println("No records found");
                        }
                    }
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
}
