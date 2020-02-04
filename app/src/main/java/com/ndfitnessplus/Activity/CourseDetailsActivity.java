package com.ndfitnessplus.Activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.css.CssFile;
import com.itextpdf.tool.xml.css.StyleAttrCSSResolver;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.net.FileRetrieve;
import com.itextpdf.tool.xml.net.FileRetrieveImpl;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CSSResolver;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import com.itextpdf.tool.xml.pipeline.html.AbstractImageProvider;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;
import com.itextpdf.tool.xml.pipeline.html.LinkProvider;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.ndfitnessplus.Adapter.AddEnquirySpinnerAdapter;
import com.ndfitnessplus.Adapter.BalanceTrasactionAdapter;
import com.ndfitnessplus.MailUtility.Mail;
import com.ndfitnessplus.Model.BalanceTrasactionList;
import com.ndfitnessplus.Model.CourseList;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import static com.itextpdf.tool.xml.html.HTML.Tag.HTML;

public class CourseDetailsActivity extends AppCompatActivity {
    public static final String CSS_DIR = "D:\\Tulsa\\NDFitness_final\\NDFitness_14_9_2019\\NDFitness_2_10_2019Final\\" +
            "NDFitness\\app\\src\\main\\res\\assets";
    public static final String HTML = "D:\\\\Tulsa\\\\NDFitness_final\\\\NDFitness_14_9_2019\\\\NDFitness_2_10_2019Final\\\\\" +\n" +
            "            \"NDFitness\\\\app\\\\src\\\\main\\\\res\\\\assets\\HtmlPage1.html";
    private static final boolean TODO = true;

    public static String TAG = CourseDetailsActivity.class.getName();
    private ProgressDialog pd;
    TextView nameTV, regdateTV, packagenameTV, start_to_end_dateTV, rateTV, paidTV, balanceTV, contactTV, executiveNameTV, durationTv, invoice_idTV;
    ImageView contactIV;
    CircularImageView imageView;

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    ProgressBar progressBar;
    String member_id, invoice_id, Email, FinancialYear;
    View nodata;
    BalanceTrasactionAdapter adapter;
    ArrayList<BalanceTrasactionList> subListArrayList = new ArrayList<BalanceTrasactionList>();
    BalanceTrasactionList subList;
    String NextFollowupDate;
    Button balance, followup, attendence;
    String Tax;
    String TaxAmount;
    String finalBalance;
    String subtotal;
    String afterEnquirySms;
    private AwesomeValidation awesomeValidation;
    //pop up content
    //Spinner Adapter
    public Spinner spinPaymentype;
    Spinner_List paymentTypeList;
    public AddEnquirySpinnerAdapter paymentTypeadapter;
    ArrayList<Spinner_List> paymentTypeArrayList = new ArrayList<Spinner_List>();
    public EditText inputPaymentDtl, inputPaid, inputNextFollDate, inputComment, inputBalance;
    private int mYear, mMonth, mDay;
    String paymentType;
    TextView txtPaymentType;
    CourseList filterArrayList;
    ImageButton phone;
    ImageView whatsapp, message;

    //setting followup popup
    EditText inputNextFollowupdate, inputfollComment;
    Spinner spinCallResponce, spinRating, spinFollType;
    Spinner_List spinCallReslist, ratingList, spinFollTypeList;

    private static final int PERMISSION_REQUEST_CODE = 1;
    AddEnquirySpinnerAdapter callresponceadapter, ratingadapter;
    String callResponce = "";
    String Rating = "";
    String FollowupType = "";
    TextView txtcallres, txtrating, txtFollType;
    String[] callresponce;
    String[] folltype;
    String start_date, end_date, remSession;
    //Loading gif
    ViewDialog viewDialog;
    private BaseFont bfBold, bfnormal;
    private static final String LOG_TAG = "GeneratePDF";

    private EditText preparedBy;
    private File pdfFile;
    private String filename = "Sample.pdf";
    private String filepath = "MyInvoices";
    String FilePath;
    String fname = "";
    String Email_ID, Password, Header, Footer, ContactNumber;
    String Logo;
    String NextPaymentDate;
    int pages = 4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_course_details);
        initToolbar();
        requestPermission();
        initializeFonts();
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.v(LOG_TAG, "External Storage not available or you don't have permission to write");
        }
        else {
            String root = Environment.getExternalStorageDirectory().getPath();
            File myDir = new File(root + "/MyInvoices");
            myDir.mkdirs();
            long n = System.currentTimeMillis() / 1000L;
            String iname=SharedPrefereneceUtil.getSelectedBranchId(CourseDetailsActivity.this)+member_id;
            fname = "Invoice" + n + ".pdf";
            FilePath = root + "/MyInvoices/" + fname;
            pdfFile = new File(myDir, fname);
            if (pdfFile.exists())
                pdfFile.delete();

            try {
                pdfFile.createNewFile();
                FileOutputStream out = new FileOutputStream(pdfFile);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            MediaScannerConnection.scanFile(this, new String[]{pdfFile.toString()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });
        }
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.receipt_dtl));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponent();
    }

    private void initComponent() {


        contactTV = (TextView) findViewById(R.id.contactTV);
        rateTV = (TextView) findViewById(R.id.rateTV);
        nameTV = (TextView) findViewById(R.id.nameTV);
        contactIV = (ImageView) findViewById(R.id.contactIV);
        imageView = (CircularImageView) findViewById(R.id.input_image);

        viewDialog = new ViewDialog(this);

        regdateTV = (TextView) findViewById(R.id.reg_dateTV);
        packagenameTV = (TextView) findViewById(R.id.package_nameTV);
        start_to_end_dateTV = (TextView) findViewById(R.id.start_to_end_date_TV);
        paidTV = (TextView) findViewById(R.id.paidTV);
        executiveNameTV = (TextView) findViewById(R.id.excecutive_nameTV);
        balanceTV = (TextView) findViewById(R.id.balanceTV);
        durationTv = (TextView) findViewById(R.id.duration);
        invoice_idTV = (TextView) findViewById(R.id.invoice_idTV);

        phone = findViewById(R.id.phone_call);
        message = findViewById(R.id.message);
        whatsapp = findViewById(R.id.whatsapp);

        progressBar = findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        balance = findViewById(R.id.btn_balance);
        followup = findViewById(R.id.btn_followup);
        attendence = findViewById(R.id.btn_attendence);
        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        if (args != null) {
            filterArrayList = (CourseList) args.getSerializable("filter_array_list");

            String cont = filterArrayList.getContact();
            ContactNumber = cont;
//                Log.v(TAG, String.format("Selected  ::contact= %s", cont));
//                Log.v(TAG, String.format("Selected  ::name= %s", filterArrayList.getName()));
            contactTV.setText(cont);
            nameTV.setText(filterArrayList.getName());
            String fpaid = "₹ " + filterArrayList.getPaid();
            String ttl = "₹ " + filterArrayList.getRate();
            rateTV.setText(ttl);
            String regdate = Utility.formatDate(filterArrayList.getRegistrationDate());
            regdateTV.setText(regdate);
            packagenameTV.setText(filterArrayList.getPackageName());
            String dur = filterArrayList.getPackageNameWithDS();
            durationTv.setText(dur);
            String rm[] = dur.split(",");
            String rms[] = rm[1].split(":");
            // remSession=rms[1];
            start_to_end_dateTV.setText(filterArrayList.getStartToEndDate());
            paidTV.setText(fpaid);
            executiveNameTV.setText(filterArrayList.getExecutiveName());
            balanceTV.setText(filterArrayList.getBalance());
            invoice_id = filterArrayList.getInvoiceID();
            FinancialYear = filterArrayList.getFinancialYear();
            invoice_idTV.setText(invoice_id);
            member_id = filterArrayList.getID();
            Tax = filterArrayList.getTax();
            Email = filterArrayList.getEmail();

            FollowupType = filterArrayList.getFollowuptype();
            if (FollowupType == null) {
                FollowupType = "";
            }

            Log.v(TAG, String.format("Selected  ::invoice_id= %s", invoice_id));
            Log.v(TAG, String.format("Email= %s", Email));
            Log.v(TAG, String.format("Selected  ::Paid= %s", fpaid));
            Log.v(TAG, String.format("Selected  ::Balance= %s", filterArrayList.getBalance()));
            String domainurl = SharedPrefereneceUtil.getDomainUrl(CourseDetailsActivity.this);
            String url = domainurl + ServiceUrls.IMAGES_URL + filterArrayList.getImage();

            // Glide.with(this).load(url).placeholder(R.drawable.nouser).into(imageView);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.nouser);
            requestOptions.error(R.drawable.nouser);


            Glide.with(this)
                    .setDefaultRequestOptions(requestOptions)
                    .load(url).into(imageView);

            pdfGenerationdataclass();
            folldetailsclass();
            balanceTrasactionclass();
            coursedetailsclass();
        }
//        requestPermission();
//        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
//            Log.v(LOG_TAG, "External Storage not available or you don't have permission to write");
//        } else {
//            String root = Environment.getExternalStorageDirectory().getPath();
//            File myDir = new File(root + "/MyInvoices");
//            myDir.mkdirs();
//            long n = System.currentTimeMillis() / 1000L;
//            String iname = SharedPrefereneceUtil.getSelectedBranchId(CourseDetailsActivity.this) + member_id;
//            fname = "Invoice" + n + ".pdf";
//            FilePath = root + "/MyInvoices/" + fname;
//            pdfFile = new File(myDir, fname);
//            if (pdfFile.exists())
//                pdfFile.delete();
//
//            try {
//                pdfFile.createNewFile();
//                FileOutputStream out = new FileOutputStream(pdfFile);
//                out.flush();
//                out.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            MediaScannerConnection.scanFile(this, new String[]{pdfFile.toString()}, null,
//                    new MediaScannerConnection.OnScanCompletedListener() {
//                        public void onScanCompleted(String path, Uri uri) {
//                            Log.i("ExternalStorage", "Scanned " + path + ":");
//                            Log.i("ExternalStorage", "-> uri=" + uri);
//                        }
//                    });
//        }
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:" + contactTV.getText().toString()));
                startActivity(dialIntent);
            }
        });
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFollowupDialog();
            }
        });
        followup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFollowupDialog();
            }
        });
        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                PackageManager pm = getPackageManager();
                try {
                    // Uri uri = Uri.parse("smsto:" + Contact);
                    Uri uri = Uri.parse("whatsapp://send?phone=+91" + contactTV.getText().toString());
                    Intent waIntent = new Intent(Intent.ACTION_VIEW, uri);
                    //waIntent.setType("text/plain");
                    String text = "YOUR TEXT HERE";

                    PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                    //Check if package exists or not. If not then code
                    //in catch block will be called
                    waIntent.setPackage("com.whatsapp");

                    // waIntent.putExtra(Intent.EXTRA_TEXT, text);
                    startActivity(waIntent);

                } catch (PackageManager.NameNotFoundException e) {
                    Toast.makeText(CourseDetailsActivity.this, "WhatsApp not Installed", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CourseDetailsActivity.this, FullImageActivity.class);
                intent.putExtra("image", filterArrayList.getImage());
                intent.putExtra("contact", filterArrayList.getContact());
                intent.putExtra("id", member_id);
                intent.putExtra("user", "Member");
                startActivity(intent);
            }
        });


        if (!(balanceTV.getText().toString().equals("0.00"))) {
            balance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCustomDialog();
                }
            });
        } else {
            Toast.makeText(CourseDetailsActivity.this, "No Outstanding Remaining", Toast.LENGTH_SHORT).show();
        }
        attendence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CourseDetailsActivity.this, CourseWiseAttendanceActivity.class);
                intent.putExtra("invoice_id", invoice_id);
                intent.putExtra("financial_yr", FinancialYear);
                intent.putExtra("member_id", member_id);
                intent.putExtra("start_date", start_date);
                intent.putExtra("end_date", end_date);
                intent.putExtra("remaining_session", remSession);
                startActivity(intent);
            }
        });

    }

    private void showCustomDialog() {
        final Dialog dialog = new Dialog(CourseDetailsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.balance_payment_popup);
        dialog.setCancelable(true);

        spinPaymentype = (Spinner) dialog.findViewById(R.id.spinner_payment_type);
        inputComment = (EditText) dialog.findViewById(R.id.input_enquiry_comment);
        //final EditText veri_otp=(EditText)dialog.findViewById(R.id.et_otp);
        inputNextFollDate = (EditText) dialog.findViewById(R.id.input_next_foll_date);
        inputPaymentDtl = (EditText) dialog.findViewById(R.id.input_payment_details);
        inputPaid = (EditText) dialog.findViewById(R.id.input_paid);
        inputNextFollDate = (EditText) dialog.findViewById(R.id.input_nextfollDate);
        inputComment = (EditText) dialog.findViewById(R.id.input_comment);
        inputBalance = (EditText) dialog.findViewById(R.id.input_balance);
        txtPaymentType = dialog.findViewById(R.id.txt_payment_type);
        inputBalance.setText(balanceTV.getText().toString());
        String curr_date = Utility.getCurrentDate();
        inputNextFollDate.setText(curr_date);
        // *********** validation *************

        ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        //  if(i==0){
        SetSpinner();
        //i++;
        //}
        PaymenttypeClass();
        // ************* rate and paid same then next followup date is null ***********
        inputPaid.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (inputPaid.getText().length() > 0) {
                    double rate = Double.parseDouble(balanceTV.getText().toString());
                    double paid = Double.parseDouble(inputPaid.getText().toString());
                    double tax = Double.parseDouble(Tax);

                    double finalbal = rate - paid;
                    finalBalance = String.valueOf(finalbal);
                    inputBalance.setText(finalBalance);
                    double i = (paid / ((tax / 100) + 1));
                    double tax_amt = paid - i;
                    TaxAmount = String.valueOf(tax_amt);
                    subtotal = String.valueOf(i);
                    // Log.v(TAG, String.format("Max Discount  :: souble max discout= %s", maxdisc));
                    if (rate == paid) {
                        inputNextFollDate.getText().clear();
                        inputNextFollDate.setEnabled(false);
                        inputNextFollDate.setKeyListener(null);
                        //   awesomeValidation.clear();
                    } else {
                        inputNextFollDate.setEnabled(true);
                        // awesomeValidation.addValidation(CourseDetailsActivity.this,R.id.input_nextfollDate, RegexTemplate.NOT_EMPTY,R.string.err_msg_next_foll_date);
                    }
                    if (paid > rate) {
                        Toast.makeText(CourseDetailsActivity.this, "Your paying more than your fees", Toast
                                .LENGTH_SHORT).show();
                        inputBalance.setText(balanceTV.getText().toString());
                        inputPaid.setText("");
                        //awesomeValidation.clear();
                        inputNextFollDate.getText().clear();
                        inputNextFollDate.setEnabled(false);
                        inputNextFollDate.setKeyListener(null);
                    } else {
                        // inputBalance.setText(balanceTV.getText().toString());
                    }

                } else {
                    inputBalance.setText(balanceTV.getText().toString());
                }


            }
        });
        inputPaid.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }


            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void afterTextChanged(Editable s) {
                if (inputPaid.getText().length() > 0) {
                    double rate = Double.parseDouble(balanceTV.getText().toString());
                    double paid = Double.parseDouble(inputPaid.getText().toString());
                    double tax = Double.parseDouble(Tax);

                    double finalbal = rate - paid;
                    finalBalance = String.valueOf(finalbal);
                    inputBalance.setText(finalBalance);
                    double i = (paid / ((tax / 100) + 1));
                    double tax_amt = paid - i;
                    TaxAmount = String.valueOf(tax_amt);
                    subtotal = String.valueOf(i);
                    if (rate == paid) {
                        inputNextFollDate.getText().clear();
                        inputNextFollDate.setEnabled(false);
                        inputNextFollDate.setKeyListener(null);
                        // awesomeValidation.clear();
                    } else {
                        inputNextFollDate.setEnabled(true);

                    }
                    if (paid > rate) {
                        Toast.makeText(CourseDetailsActivity.this, "Your paying more than your fees", Toast
                                .LENGTH_SHORT).show();
                        // awesomeValidation.clear();
                        inputBalance.setText(balanceTV.getText().toString());
                        inputPaid.setText("");
                        inputNextFollDate.getText().clear();
                        inputNextFollDate.setEnabled(false);
                        inputNextFollDate.setKeyListener(null);
                    }
                } else {
                    inputBalance.setText(balanceTV.getText().toString());
                }


            }
        });
        inputNextFollDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(CourseDetailsActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                inputNextFollDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                //tiemPicker();
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                datePickerDialog.show();
            }
        });

        ((Button) dialog.findViewById(R.id.btn_submit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(inputBalance.getText().toString().equals("0.0"))) {
                    // Toast.makeText(this, "Please fill Payment type or Package type", Toast.LENGTH_LONG).show();
                    //awesomeValidation.addValidation(CourseDetailsActivity.this,R.id.input_nextfollDate,RegexTemplate.NOT_EMPTY,R.string.err_msg_next_payment_date);
                    if (inputNextFollDate.getText().length() > 0) {
                        // Log.v(TAG, String.format("Remaining balance= %s", inputBalance.getText().toString()));
                        double paid = 0;
                        if (inputPaid.getText().length() > 0) {
                            paid = Double.parseDouble(inputPaid.getText().toString());
                        }

                        if (paymentType.equals(getResources().getString(R.string.payment_type))) {
                            Toast.makeText(CourseDetailsActivity.this, "Please fill Payment type or Package type", Toast.LENGTH_LONG).show();
                        } else {
                            if (paid <= 0) {
                                inputPaid.getText().clear();
                                inputBalance.getText().clear();
                                inputPaid.requestFocus();
                                Toast.makeText(CourseDetailsActivity.this, "Please pay some amount", Toast
                                        .LENGTH_SHORT).show();
                            } else {
                                AddBalanceReceiptClass();
                                dialog.dismiss();
                            }

                            Log.v(TAG, String.format("calling function= %s", "Add Balnce receipt"));
                        }

                    } else {
                        Toast.makeText(CourseDetailsActivity.this, "Please select next payment date", Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Log.v(TAG, String.format("Remaining balance= %s", inputBalance.getText().toString()));
                    double paid = Double.parseDouble(inputPaid.getText().toString());
                    if (paymentType.equals(getResources().getString(R.string.payment_type))) {
                        Toast.makeText(CourseDetailsActivity.this, "Please fill Payment type or Package type", Toast.LENGTH_LONG).show();
                    } else {
                        if (paid <= 0) {
                            Toast.makeText(CourseDetailsActivity.this, "Please pay some amount", Toast
                                    .LENGTH_SHORT).show();
                            inputPaid.getText().clear();
                            inputBalance.getText().clear();
                            inputPaid.requestFocus();

                        } else {
                            AddBalanceReceiptClass();
                            dialog.dismiss();
                        }
                        Log.v(TAG, String.format("calling function= %s", "Add Balnce receipt"));
                    }

                }
            }
        });

        dialog.show();
    }

    public void SetSpinner() {
        spinPaymentype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int index = parent.getSelectedItemPosition();
                if (view != null) {
                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                    View layout = (View) view.findViewById(R.id.layout);
                    layout.setPadding(0, 0, 0, 0);


                    if (index == 0) {
                        tv.setTextColor((Color.GRAY));
                    } else {
                        tv.setTextColor((Color.BLACK));
                    }
                    paymentType = tv.getText().toString();
                    if (index != 0) {
                        txtPaymentType.setVisibility(View.VISIBLE);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showFollowupDialog() {
        final Dialog dialog = new Dialog(CourseDetailsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.take_all_followup_popup);
        dialog.setCancelable(true);
        spinCallResponce = (Spinner) dialog.findViewById(R.id.spinner_call_res);
        spinRating = (Spinner) dialog.findViewById(R.id.spinner_rating);
        spinFollType = (Spinner) dialog.findViewById(R.id.spinner_folltype);
        txtcallres = (TextView) dialog.findViewById(R.id.txt_callres);
        txtrating = (TextView) dialog.findViewById(R.id.txt_rating);
        txtFollType = (TextView) dialog.findViewById(R.id.txt_folltype);

        inputfollComment = (EditText) dialog.findViewById(R.id.input_enquiry_comment);
        //final EditText veri_otp=(EditText)dialog.findViewById(R.id.et_otp);
        inputNextFollowupdate = (EditText) dialog.findViewById(R.id.input_next_foll_date);

        ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        //  if(i==0){
        SetFollowupSpinner();
        //i++;
        //}
        callResponseClass();
        follTypeClass();
        // inputNextFollowupdate.setText(NextFollowupDate);
        String curr_date = Utility.getCurrentDate();
        inputNextFollowupdate.setText(curr_date);

        inputNextFollowupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(CourseDetailsActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                inputNextFollowupdate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                //tiemPicker();
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                datePickerDialog.show();
            }
        });

        ((Button) dialog.findViewById(R.id.btn_submit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callResponce.equals(getResources().getString(R.string.call_res)) || Rating.equals(getResources().getString(R.string.rating)) || FollowupType.equals(getResources().getString(R.string.hint_foll_type))) {
                    Toast.makeText(getApplicationContext(), "Please select Call Response or Rating or Followup Type", Toast.LENGTH_SHORT).show();
                } else {
                    if (inputNextFollowupdate.getText().length() == 0) {
                        if (!(Rating.equals("Not Interested") || Rating.equals("Converted") || FollowupType.equals("Member BirthDay") || FollowupType.equals("Staff BirthDay"))) {
                            Toast.makeText(getApplicationContext(), "Please select Next Followup Date", Toast.LENGTH_SHORT).show();
                        } else {
                            if (inputfollComment.getText().length() > 0) {
                                if (FollowupType.equals("Payment") && (balanceTV.getText().toString().equals("0.0"))) {
                                    dialog.dismiss();
                                    Toast.makeText(CourseDetailsActivity.this, "No Outstanding Remainig", Toast.LENGTH_LONG).show();
                                } else {
                                    takefollowupclass();
                                    dialog.dismiss();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Please enter comment", Toast.LENGTH_SHORT).show();

                            }
                        }
                    } else {
                        if (inputfollComment.getText().length() > 0) {
                            if (FollowupType.equals("Payment") && (balanceTV.getText().toString().equals("0.0"))) {
                                dialog.dismiss();
                                Toast.makeText(CourseDetailsActivity.this, "No Outstanding Remainig", Toast.LENGTH_LONG).show();
                            } else {
                                takefollowupclass();
                                dialog.dismiss();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Please enter comment", Toast.LENGTH_SHORT).show();

                        }
                    }
                }
            }
        });

        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.balance_pdf_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_print) {
            requestPermission();
         if(checkPermission()){
             Thread thread = new Thread(new Runnable() {
                 //
                 @Override
                 public void run() {
                     try  {
                         if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                             Log.d("FilePath", FilePath);
                             PrintManager printManager = (PrintManager)CourseDetailsActivity.this.getSystemService(Context.PRINT_SERVICE);
                             final String jobName = CourseDetailsActivity.this.getString(R.string.app_name) + " Document";
                             PrintDocumentAdapter pda = new PrintDocumentAdapter() {

                                 @Override
                                 public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
                                     InputStream input = null;
                                     OutputStream output = null;

                                     try {
//                        AssetManager assetManager = getAssets();
//                        String root = Environment.getExternalStorageDirectory().getPath();
//                        String path=root+"/MyInvoices/Invoice1578297168.pdf";
                                         File file = new File(FilePath);
                                         Log.d("FilePath", FilePath);
                                         input = new FileInputStream(file);
                                         output = new FileOutputStream(destination.getFileDescriptor());
                                         byte[] buf = new byte[1024];
                                         int bytesRead;

                                         while ((bytesRead = input.read(buf)) > 0) {
                                             output.write(buf, 0, bytesRead);
                                         }

                                         callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});

                                     } catch (FileNotFoundException ee) {
                                         //Catch exception
                                     } catch (Exception e) {
                                         //Catch exception
                                     } finally {
                                         try {
                                             input.close();
                                             output.close();
                                         } catch (IOException e) {
                                             e.printStackTrace();
                                         }
                                     }
                                 }

                                 @Override
                                 public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {

                                     if (cancellationSignal.isCanceled()) {
                                         callback.onLayoutCancelled();
                                         return;
                                     }
                                     Log.d("FilePath", FilePath);
//                    File file = new File(FilePath);
                                     // int pages = computePageCount(newAttributes);
//                                     PDDocument myDocument = null;
//                                     try {
//                                         myDocument = PDDocument.load(new File(FilePath));
//                                     } catch (IOException e) {
//                                         e.printStackTrace();
//                                     }
//                                     pages = myDocument.getNumberOfPages();

                                     if (pages > 0) {
                                         PrintDocumentInfo pdi = new PrintDocumentInfo.Builder("invoice.pdf")
                                                 .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT) .setPageCount(pages).build();
                                         callback.onLayoutFinished(pdi, true);
                                     }else {
                                         // Otherwise report an error to the print framework
                                         callback.onLayoutFailed("Page count calculation failed.");
                                     }
                                 }
                             };
                             printManager.print(jobName, pda, null);
                         }
                     } catch (Exception e) {
                         e.printStackTrace();
                     }
                 }
             });
             thread.start();

      }

            return true;
        } else if (id == R.id.whatsapp) {
            PackageManager pm = getPackageManager();
            try {
                // Uri uri = Uri.parse("smsto:" + Contact);
                Uri uri = Uri.parse("whatsapp://send?phone=+91" + contactTV.getText().toString());
                Intent waIntent = new Intent(Intent.ACTION_VIEW, uri);
                //waIntent.setType("text/plain");
                String text = "YOUR TEXT HERE";

                PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                //Check if package exists or not. If not then code
                //in catch block will be called
                waIntent.setPackage("com.whatsapp");

                // waIntent.putExtra(Intent.EXTRA_TEXT, text);
                startActivity(waIntent);

            } catch (PackageManager.NameNotFoundException e) {
                Toast.makeText(CourseDetailsActivity.this, "WhatsApp not Installed", Toast.LENGTH_SHORT)
                        .show();
            }
            return true;
        } else if (id == R.id.share_pdf) {
            try {
                File outputFile = new File(FilePath);
//                   Uri uri = Uri.parse("whatsapp://send?phone=+91" + contactTV.getText().toString());
                String toNumber = "91" + ContactNumber; // contains spaces.
                toNumber = toNumber.replace("+", "").replace(" ", "");

                    Intent sendIntent = new Intent("android.intent.action.MAIN");
                    if (Build.VERSION.SDK_INT >= 24) {
                        try {
                            Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                            m.invoke(null);
                            sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(pdfFile));
//                            shareImage(Uri.fromFile(new File(Path)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else{
                            sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(pdfFile));

                    }
                   //  sendIntent.setComponent(new ComponentName("com.whatsapp","com.whatsapp.Conversation"));
                    sendIntent.putExtra("jid", toNumber + "@s.whatsapp.net");
//                      sendIntent.putExtra(Intent.ACTION_VIEW, uri);
//                    sendIntent.putExtra("jid", toNumber + "@s.whatsapp.net");
//                sendIntent.setType("*/*");
//                String[] mimetypes = {"image/*", "text/plain"};
//                sendIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);       Image with message
                    sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.setPackage("com.whatsapp");
                    sendIntent.setType("application/pdf");
                    startActivity(sendIntent);

                }  catch (ActivityNotFoundException e) {
                    Toast.makeText(CourseDetailsActivity.this, "WhatsApp not Installed", Toast.LENGTH_SHORT)
                            .show();
                }catch (ClassCastException e){
                    e.printStackTrace();

                }catch (Exception e){
                e.printStackTrace();
                }

            return true;
        }else if (id == R.id.send_reminders) {
            if(!(balanceTV.getText().toString().equals("0.00"))){

                        Display display = getWindowManager().getDefaultDisplay();
                        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        final  View shareview = inflater.inflate(R.layout.reminder_share_view, null,false);

                        TextView CompanyName=shareview.findViewById(R.id.gym_name);
                        TextView Balance=shareview.findViewById(R.id.text_balance);
                        TextView payment_date=shareview.findViewById(R.id.payment_date);
                        final  ImageView compLogo=shareview.findViewById(R.id.input_logo);

                        String comp_name= SharedPrefereneceUtil.getCompanyName(CourseDetailsActivity.this)+ "-"+
                                SharedPrefereneceUtil.getSelectedBranch(CourseDetailsActivity.this);
                        CompanyName.setText(comp_name);
                        String bal="₹ "+balanceTV.getText().toString();
                        Balance.setText(bal);
                        if(NextPaymentDate !=""){
                            String folldate=Utility.formatDate(NextPaymentDate);
                            String pdate="As of "+folldate;
                            payment_date.setText(pdate);
                        }

//            try {
                        String domainurl= SharedPrefereneceUtil.getDomainUrl(CourseDetailsActivity.this);
                        final  String strurl= domainurl+ServiceUrls.IMAGES_URL + Logo;
                        Log.e("Imageurl", strurl);

                        shareview.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                        shareview.layout(0,0,0,0);

                        shareview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                            @Override
                            public void onGlobalLayout() {

                                shareview.getWidth();

                            }
                        });
//            shareview.setMinimumWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
//            shareview.setMinimumHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
//            lp.copyFrom(shareview.getWindowVisibility());
//            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
//            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        int width=shareview.getMeasuredWidth();
                        int height=shareview.getMeasuredHeight();

                        Bitmap bitmap1 = loadBitmapFromView(shareview, width,height);
                        saveBitmap(bitmap1);
                        //  String str_screenshot = "/sdcard/Testing/"+"testing" + ".jpg";
                        String str_screenshot = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/testing" + ".png";
                        fn_share(str_screenshot);

            }else{
                Toast.makeText(CourseDetailsActivity.this, "No Outstanding Remaining", Toast.LENGTH_SHORT).show();
            }
//            LinearLayout wrapper = new LinearLayout(this);
//            wrapper.setLayoutParams(new LinearLayout.LayoutParams(500,500,1));

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
//    private int computePageCount(PrintAttributes printAttributes) {
//        int itemsPerPage = 4; // default item count for portrait mode
//
//        PrintAttributes.MediaSize pageSize = printAttributes.getMediaSize();
//        if (!pageSize.isPortrait()) {
//            // Six items per page in landscape orientation
//            itemsPerPage = 6;
//        }
//
//        // Determine number of print items
//        int printItemCount = getPrintItemCount();
//
//        return (int) Math.ceil(printItemCount / itemsPerPage);
//    }
    public void saveBitmap(Bitmap bitmap) {
        File imagePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/testing" + ".png");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();

            Log.e("ImageSave", "Saveimage");
        } catch (FileNotFoundException e) {
            Log.e("GREC", e.getMessage(), e);
        } catch (IOException e) {
            Log.e("GREC", e.getMessage(), e);
        }
    }

    public static Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        BitmapShader shader = new BitmapShader(b,  Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setShader(shader);
        Canvas c = new Canvas(b);
        c.drawCircle(v.getWidth() / 2, v.getWidth() / 2, v.getWidth() / 2, paint);
        Drawable bgDrawable = v.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(c);
        else
            c.drawColor(Color.WHITE);
        v.draw(c);

        return b;
    }

    public void fn_share(String path) {

        File file = new File(path);
        Uri uri = Uri.parse("whatsapp://send?phone=+91" + contactTV.getText().toString());
        Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
        // Uri uri = Uri.fromFile(file);
        try {
            File outputFile = new File(FilePath);
            String toNumber = "91"+ContactNumber; // contains spaces.
            toNumber = toNumber.replace("+", "").replace(" ", "");

            Intent sendIntent = new Intent("android.intent.action.MAIN");
            if(Build.VERSION.SDK_INT>=24){
                try{
                    Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                    m.invoke(null);
                    sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
//                            shareImage(Uri.fromFile(new File(Path)));
                }catch(Exception e){
                    e.printStackTrace();
                }
            }else{
                    sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
//                            shareImage(Uri.fromFile(new File(Path)));
            }
           // sendIntent.setComponent(new  ComponentName("com.whatsapp","com.whatsapp.Conversation"));
            sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(toNumber) + "@s.whatsapp.net");
//            sendIntent.putExtra(Intent.ACTION_VIEW, uri);
//                sendIntent.setType("*/*");
//                String[] mimetypes = {"image/*", "text/plain"};
//                sendIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);       Image with message
            sendIntent.setAction(Intent.ACTION_SEND);

            sendIntent.setPackage("com.whatsapp");
            sendIntent.setType("image/*");
            startActivity(sendIntent);

        }  catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(CourseDetailsActivity.this, "WhatsApp not Installed", Toast.LENGTH_SHORT)
                    .show();
        }catch (Exception e){
            e.printStackTrace();
        }


    }
    private String contactIdByPhoneNumber(String phoneNumber) {
        String contactId = null;
        if (phoneNumber != null && phoneNumber.length() > 0) {
            ContentResolver contentResolver = getContentResolver();
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
            String[] projection = new String[]{ContactsContract.PhoneLookup._ID};

            Cursor cursor = contentResolver.query(uri, projection, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
                }
                cursor.close();
            }
        }
        return contactId;
    }
    public String hasWhatsApp(String contactID) {
        String rowContactId = null;
        boolean hasWhatsApp;

        String[] projection;
        projection = new String[]{ContactsContract.RawContacts._ID};
        String selection = ContactsContract.RawContacts.CONTACT_ID + " = ? AND " + ContactsContract.RawContacts.ACCOUNT_TYPE + " = ?";
        String[] selectionArgs = new String[]{contactID, "com.whatsapp"};
        Cursor cursor = getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI, projection, selection, selectionArgs, null);
        if (cursor != null) {
            hasWhatsApp = cursor.moveToNext();
            if (hasWhatsApp) {
                rowContactId = cursor.getString(0);
            }
            cursor.close();
        }
        return rowContactId;
    }
    public  void SetFollowupSpinner(){

        spinCallResponce.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int index = parent.getSelectedItemPosition();
                if (view != null) {
                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                    view.setPadding(0, 0, 0,0);
                    tv.setTextColor(getResources().getColor(R.color.black));
                    if(!(callResponce==null)) {
                        if (callResponce.equals(tv.getText().toString())) {
                            spinCallResponce.setSelection(index);
                            txtcallres.setVisibility(View.VISIBLE);
                        }
                        callResponce = tv.getText().toString();
                    }
                    if(index==0){
                        txtcallres.setVisibility(View.VISIBLE);
                        tv.setText(callResponce);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinFollType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int index = parent.getSelectedItemPosition();
                if (view != null) {
                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                    view.setPadding(0, 0, 0, 0);
                    tv.setTextColor(getResources().getColor(R.color.black));
                    if(!(FollowupType==null)) {
                        if (FollowupType.equals(tv.getText().toString())) {
                            spinFollType.setSelection(index);
                            txtFollType.setVisibility(View.VISIBLE);
                        }
                        FollowupType = tv.getText().toString();
                    }
                    if(index==0){
                        txtFollType.setVisibility(View.VISIBLE);
                        tv.setText(FollowupType);
                    }
                    if (!(FollowupType == null)) {
                        if (FollowupType.equals("Member BirthDay")||FollowupType.equals("Staff BirthDay")) {
                            //Toast.makeText(parent.getContext(), "no interetsed: ", Toast.LENGTH_LONG).show();
                            inputNextFollowupdate.setText("");
                            inputNextFollowupdate.setEnabled(false);
                            inputNextFollowupdate.setKeyListener(null);
                        } else {
                            inputNextFollowupdate.setEnabled(true);
                        }
//                        if(FollowupType.equals("Payment")&&(Rating.equals("Not Interested"))){
//                            inputNextFollowupdate.setEnabled(true);
//                            String curr_date = Utility.getCurrentDate();
//                            inputNextFollowupdate.setText(curr_date);
//                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //Call Responce spinner adapter setting
        final  String[] ratingarray = getResources().getStringArray(R.array.rating_array);
        ArrayAdapter<String> spinnerArrayAdapter1 = new ArrayAdapter<String>(
                this, R.layout.spinner_item,ratingarray ){
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
                if(view !=null){
                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
//                    View layView = super.getView(position, convertView, parent);
//                    layView.setPadding(0, 0, layView.getPaddingRight(), 0);
                    if(position == 0){
                        // Set the hint text color gray
                        tv.setTextColor(Color.GRAY);
                        tv.setText(getResources().getString(R.string.rating));
                        // tv.setTextColor(Color.GRAY);
                    }
                    else {
                        tv.setTextColor(Color.BLACK);
                    }
                }

                return view;
            }
        };
        spinnerArrayAdapter1.setDropDownViewResource(R.layout.spinner_item);
        spinRating.setAdapter(spinnerArrayAdapter1);
        int spinnerPositionrating = spinnerArrayAdapter1.getPosition(Rating);
        //Toast.makeText(this, "position: " + spinnerPositionrating, Toast.LENGTH_LONG).show();
        spinRating.setSelection(spinnerPositionrating);
        ArrayList<Spinner_List> ratingArrayList = new ArrayList<Spinner_List>();
//
        spinRating.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int index = parent.getSelectedItemPosition();
                if (view != null) {
                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                    //View layView = super.getView(position, convertView, parent);
                    view.setPadding(0, 0, 0, 0);
                    tv.setTextColor(getResources().getColor(R.color.black));
                    if (!(Rating == null)) {
                        if (Rating.equals(tv.getText().toString())) {
                            spinRating.setSelection(index);
                            txtrating.setVisibility(View.VISIBLE);
                        }
                        Rating = tv.getText().toString();
                    }
                    if (!(Rating == null)) {
                        if (Rating.equals("Not Interested") || Rating.equals("Converted")) {
                            //Toast.makeText(parent.getContext(), "no interetsed: ", Toast.LENGTH_LONG).show();
                            inputNextFollowupdate.setText("");
                            inputNextFollowupdate.setEnabled(false);
                            inputNextFollowupdate.setKeyListener(null);
                        } else {
                            inputNextFollowupdate.setEnabled(true);
                        }
//                        if(FollowupType.equals("Payment")&&(Rating.equals("Not Interested"))){
//                            inputNextFollowupdate.setEnabled(true);
//                            String curr_date = Utility.getCurrentDate();
//                            inputNextFollowupdate.setText(curr_date);
//                        }else{
                            if (Rating.equals("Not Interested") || Rating.equals("Converted")) {
                                //Toast.makeText(parent.getContext(), "no interetsed: ", Toast.LENGTH_LONG).show();
                                inputNextFollowupdate.setText("");
                                inputNextFollowupdate.setEnabled(false);
                                inputNextFollowupdate.setKeyListener(null);
                            }
//                        }
                    }
                    if(index==0){
                        txtrating.setVisibility(View.VISIBLE);
                        tv.setText(Rating);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void showProgressDialog() {
        Log.v(TAG, String.format("showProgressDialog"));
        pd = new ProgressDialog(CourseDetailsActivity.this);
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
    //*********** Asycc class for loading data for database **************
    private void balanceTrasactionclass() {
        CourseDetailsActivity.BalanceTrasactionTrackclass ru = new CourseDetailsActivity.BalanceTrasactionTrackclass();
        ru.execute("5");
    }

    class BalanceTrasactionTrackclass extends AsyncTask<String, Void, String> {


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
            BalanceTrasactionDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> BalanceTrasactionDetails = new HashMap<String, String>();
            BalanceTrasactionDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(CourseDetailsActivity.this));
            BalanceTrasactionDetails.put("member_id",member_id );
            BalanceTrasactionDetails.put("invoice_id",invoice_id );
            BalanceTrasactionDetails.put("financial_yr",FinancialYear );
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(CourseDetailsActivity.this)));
            Log.v(TAG, String.format("doInBackground :: member_id = %s", member_id));
            Log.v(TAG, String.format("doInBackground :: invoice_id = %s", invoice_id));
            Log.v(TAG, String.format("doInBackground :: FinancialYear = %s", FinancialYear));
            BalanceTrasactionDetails.put("action","show_balance_trasaction_details");
            String domainurl= SharedPrefereneceUtil.getDomainUrl(CourseDetailsActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, BalanceTrasactionDetails);
            //Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }

    private void BalanceTrasactionDetails(String jsonResponse) {

        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
//        RelativeLayout relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayoutPrabhagDetails);
        if (jsonResponse != null) {


            try {
                Log.v(TAG, "JsonResponseOpeartion :: test");
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    progressBar.setVisibility(View.GONE);
                    if (object != null) {
                        JSONArray jsonArrayResult = object.getJSONArray("result");
//                        if(jsonArrayResult.length() >10){
//                            totalPage=jsonArrayResult.length()/10;
//                        }
                        ArrayList<BalanceTrasactionList> item = new ArrayList<BalanceTrasactionList>();
                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {

                            for (int i = 0; i < jsonArrayResult.length(); i++) {


                                subList = new BalanceTrasactionList();
                                Log.d(TAG, "i: " + i);

                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {

                                    String Paid = jsonObj.getString("Paid");
                                    String RemainingBalance = jsonObj.getString("RemainingBalance");
                                    String PaymentType = jsonObj.getString("PaymentType");
                                    String ReceiptOwnerExecutive = jsonObj.getString("ReceiptOwnerExecutive");
                                    String ReceiptDate = jsonObj.getString("ReceiptDate");
                                    String Receipt_Id = jsonObj.getString("Receipt_Id");


                                    //  for (int j = 0; j < 5; j++) {

                                    subList.setPaid(Paid);
                                    subList.setBalance(RemainingBalance);
                                    subList.setPaymentType(PaymentType);
                                    subList.setExecutiveName(ReceiptOwnerExecutive);
                                    String rdate= Utility.formatDate(ReceiptDate);
                                    subList.setPaymentDate(rdate);
                                    subList.setID(Receipt_Id);

                                    item.add(subList);
                                    adapter = new BalanceTrasactionAdapter( item, CourseDetailsActivity.this);
                                    recyclerView.setAdapter(adapter);

                                }
                            }
                        } else if (jsonArrayResult.length() == 0) {
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    //nodata.setVisibility(View.VISIBLE);
                    // recyclerView.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(CourseDetailsActivity.this);
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
    // *************** Payment Mode *******************
    public void  PaymenttypeClass() {
        CourseDetailsActivity.PaymentTypeTrackClass ru = new CourseDetailsActivity.PaymentTypeTrackClass();
        ru.execute("5");
    }
    class PaymentTypeTrackClass extends AsyncTask<String, Void, String> {

        ServerClass ruc = new ServerClass();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(TAG, "onPreExecute");
            //showProgressDialog();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.v(TAG, String.format("onPostExecute :: response = %s", response));
            //dismissProgressDialog();
            //Toast.makeText(Employee.this, response, Toast.LENGTH_LONG).show();
            PaymentTypeDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
           // Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> PaymentTypeDetails = new HashMap<String, String>();
            PaymentTypeDetails.put("action", "show_payment_type_list");
            //PaymentTypeloyeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(PaymentTypeloyee.this));
            String domainurl= SharedPrefereneceUtil.getDomainUrl(CourseDetailsActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, PaymentTypeDetails);
            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }


    private void PaymentTypeDetails(String jsonResponse) {


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
                        paymentTypeArrayList.clear();
                        paymentTypeList = new Spinner_List();
                        paymentTypeList.setName(getResources().getString(R.string.payment_type));
                        paymentTypeArrayList.add(0,paymentTypeList);
                        if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                paymentTypeList = new Spinner_List();
                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String PaymentType     = jsonObj.getString("PaymentType");

                                    String id=jsonObj.getString("Auto_Id");
//                               if(i==0){
//                                   paymentTypeList.setName(getResources().getString(R.string.promt_country));
//                                   enqF.add(0,paymentTypeList);
//                               }
                                    paymentTypeList.setName(PaymentType);
                                    paymentTypeList.setId(id);

                                    paymentTypeArrayList.add(paymentTypeList);

                                    paymentTypeadapter = new AddEnquirySpinnerAdapter(CourseDetailsActivity.this, paymentTypeArrayList){
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
                                                tv.setText(getResources().getString(R.string.prompt_payment_type));
                                                // tv.setTextColor(Color.GRAY);
                                            }
                                            else {
                                                tv.setTextColor(Color.BLACK);
                                            }
                                            return view;
                                        }

                                    };
                                    spinPaymentype.setAdapter(paymentTypeadapter);


                                }
                            }
                        }else if(jsonArrayCountry.length()==0){
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){

                    //forumCount.setVisibility(View.INVISBLE);
                    // queCount.setVisibility(View.INVISIBLE);
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
            }
        }
    }
    // ************* Call response Spinner ***************
    public void  callResponseClass() {
        CourseDetailsActivity.CallResponseTrackClass ru = new CourseDetailsActivity.CallResponseTrackClass();
        ru.execute("5");
    }
    class CallResponseTrackClass extends AsyncTask<String, Void, String> {

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
            CallResponseDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> CallResponseDetails = new HashMap<String, String>();
            CallResponseDetails.put("action", "show_call_response_list");
            String domainurl= SharedPrefereneceUtil.getDomainUrl(CourseDetailsActivity.this);
            //CallResponseloyeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(CallResponseloyee.this));
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, CallResponseDetails);
            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }
    private void CallResponseDetails(String jsonResponse) {


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
                        callresponce=new String[ jsonArrayCountry.length()+1];
                        if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
                            callresponce[0]=getResources().getString(R.string.call_res);
                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                spinCallReslist = new Spinner_List();
                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String CallResponse     = jsonObj.getString("CallResponse");

                                    String id=jsonObj.getString("Auto_Id");
//                               if(i==0){
//                                   spinCallReslist.setName(getResources().getString(R.string.promt_country));
//                                   enqF.add(0,spinCallReslist);
//                               }
                                    spinCallReslist.setName(CallResponse);
                                    spinCallReslist.setId(id);


                                    callresponce[i+1]=CallResponse;



                                }
                            }
                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                                    this, R.layout.spinner_item,callresponce ){
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
//                                    View layView = super.getView(position, convertView, parent);
//                                    layView.setPadding(0, 0, layView.getPaddingRight(), 0);
                                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                                    if(position == 0){
                                        // Set the hint text color gray
                                        tv.setTextColor(Color.GRAY);
                                        tv.setText(getResources().getString(R.string.call_res));
                                        // tv.setTextColor(Color.GRAY);
                                    }
                                    else {
                                        tv.setTextColor(Color.BLACK);
                                    }
                                    return view;
                                }
                            };

                            spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                            spinCallResponce.setAdapter(spinnerArrayAdapter);
                            int spinnerPosition = spinnerArrayAdapter.getPosition(callResponce);
                            // Toast.makeText(this, "position: " + spinnerPosition, Toast.LENGTH_LONG).show();
                            spinCallResponce.setSelection(spinnerPosition);
                        }else if(jsonArrayCountry.length()==0){
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){

                    //forumCount.setVisibility(View.INVISBLE);
                    // queCount.setVisibility(View.INVISIBLE);
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
            }
        }
    }
    // ************* Follwup Type Spinner ***************
    public void follTypeClass() {
        CourseDetailsActivity.FollTypeTrackClass ru = new CourseDetailsActivity.FollTypeTrackClass();
        ru.execute("5");
    }
    class FollTypeTrackClass extends AsyncTask<String, Void, String> {

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
            FollTypeDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
           // Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> FollTypeDetails = new HashMap<String, String>();
            FollTypeDetails.put("action", "show_master_followup_type_list_for_course");
            String domainurl= SharedPrefereneceUtil.getDomainUrl(CourseDetailsActivity.this);
            //FollTypeloyeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(FollTypeloyee.this));
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, FollTypeDetails);
            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }
    private void FollTypeDetails(String jsonResponse) {


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
                        folltype=new String[ jsonArrayCountry.length()+1];
                        if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
                            folltype[0]=getResources().getString(R.string.hint_foll_type);
                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                spinFollTypeList = new Spinner_List();
                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String Followup     = jsonObj.getString("Followup");

                                    String id=jsonObj.getString("Auto_Id");
//                               if(i==0){
//                                   spinFollTypeList.setName(getResources().getString(R.string.promt_country));
//                                   enqF.add(0,spinFollTypeList);
//                               }
                                    spinFollTypeList.setName(Followup);
                                    spinFollTypeList.setId(id);


                                    folltype[i+1]=Followup;



                                }
                            }
                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                                    this, R.layout.spinner_item,folltype ){
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
//                                    View layView = super.getView(position, convertView, parent);
//                                    layView.setPadding(0, 0, layView.getPaddingRight(), 0);
                                    if(position == 0){
                                        // Set the hint text color gray
                                        tv.setTextColor(Color.GRAY);
                                        tv.setText(getResources().getString(R.string.hint_foll_type));
                                        // tv.setTextColor(Color.GRAY);
                                    }
                                    else {
                                        tv.setTextColor(Color.BLACK);
                                    }
                                    return view;
                                }
                            };

                            spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                            spinFollType.setAdapter(spinnerArrayAdapter);
                            int spinnerPosition = spinnerArrayAdapter.getPosition(FollowupType);
                            // Toast.makeText(this, "position: " + spinnerPosition, Toast.LENGTH_LONG).show();
                            spinFollType.setSelection(spinnerPosition);
                        }else if(jsonArrayCountry.length()==0){
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){

                    //forumCount.setVisibility(View.INVISBLE);
                    // queCount.setVisibility(View.INVISIBLE);
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
            }
        }
    }
    // ************* take followup ************************
    private void takefollowupclass() {
        CourseDetailsActivity.TakeFollowupTrackclass ru = new CourseDetailsActivity.TakeFollowupTrackclass();
        ru.execute("5");
    }
    class TakeFollowupTrackclass extends AsyncTask<String, Void, String> {

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
          //  dismissProgressDialog();
            viewDialog.hideDialog();
            //Toast.makeText(CandiateListView.this, response, Toast.LENGTH_LONG).show();
            //  Toast.makeText(NewCustomerActivity.this, response, Toast.LENGTH_LONG).show();
            TakeFollowupDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> TakeFollowupDetails = new HashMap<String, String>();
            TakeFollowupDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(CourseDetailsActivity.this));
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(CourseDetailsActivity.this)));
            TakeFollowupDetails.put("id",member_id);
            Log.v(TAG, String.format("doInBackground :: enquiry_id = %s", member_id));
            TakeFollowupDetails.put("comment",inputfollComment.getText().toString());
            Log.v(TAG, String.format("doInBackground :: comment = %s", inputfollComment.getText().toString()));
            TakeFollowupDetails.put("rating",Rating);
            Log.v(TAG, String.format("doInBackground :: Rating = %s", Rating));
            TakeFollowupDetails.put("call_res",callResponce);
            Log.v(TAG, String.format("doInBackground :: callResponce = %s", callResponce));
            TakeFollowupDetails.put("next_foll_date",inputNextFollowupdate.getText().toString());
            Log.v(TAG, String.format("doInBackground :: next_foll_date = %s", inputNextFollowupdate.getText().toString()));
            TakeFollowupDetails.put("exe_name", SharedPrefereneceUtil.getName(CourseDetailsActivity.this));
            TakeFollowupDetails.put("name",nameTV.getText().toString());
            TakeFollowupDetails.put("contact",contactTV.getText().toString());
            TakeFollowupDetails.put("foll_type",FollowupType);
            TakeFollowupDetails.put("invoice_id",invoice_id);
            TakeFollowupDetails.put("financial_yr",FinancialYear);
            Log.v(TAG, String.format("doInBackground :: exe_name = %s", SharedPrefereneceUtil.getName(CourseDetailsActivity.this)));
            TakeFollowupDetails.put("action", "add_other_followup");
            String domainurl= SharedPrefereneceUtil.getDomainUrl(CourseDetailsActivity.this);
            String loginResult2 = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, TakeFollowupDetails);

            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult2));
            return loginResult2;
        }
    }
    private void TakeFollowupDetails(String jsonResponse) {

        Log.v(TAG, String.format("loginServerResponse :: response = %s", jsonResponse));

        JSONObject jsonObjLoginResponse = null;
        try {
            jsonObjLoginResponse = new JSONObject(jsonResponse);
            String success = jsonObjLoginResponse.getString(getResources().getString(R.string.success));

            if (success.equalsIgnoreCase(getResources().getString(R.string.two))||success.equalsIgnoreCase(getResources().getString(R.string.one))) {
                Toast.makeText(CourseDetailsActivity.this,"Followup added succesfully",Toast.LENGTH_SHORT).show();
                finish();
                overridePendingTransition(0, 0);
                Intent intent=new Intent(this, CourseDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("filter_array_list", filterArrayList);
                intent.putExtra("BUNDLE",bundle);
                startActivity(intent);
                overridePendingTransition(0, 0);
                moveTaskToBack(false);
                // showCustomDialog();

                //inputEmail, inputPhone,inputAdd,inputReq,inputFollowupdate;
            }

            else if (success.equalsIgnoreCase(getResources().getString(R.string.zero)))
            {
                Toast.makeText(CourseDetailsActivity.this,"Something went wrong",Toast.LENGTH_SHORT).show();

                // inputContact.getText().clear();
                //Toast.makeText(CourseDetailsActivity.this,"Please Enter New Mobile Number",Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void  AddBalanceReceiptClass() {
        CourseDetailsActivity.AddBalanceReceiptTrackClass ru = new CourseDetailsActivity.AddBalanceReceiptTrackClass();
        ru.execute("5");
    }
    class AddBalanceReceiptTrackClass extends AsyncTask<String, Void, String> {

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
            Log.v(TAG, String.format("onPostExecute :: response = %s", response));
            //dismissProgressDialog();
            viewDialog.hideDialog();
            //Toast.makeText(CandiateListView.this, response, Toast.LENGTH_LONG).show();
            //  Toast.makeText(NewCustomerActivity.this, response, Toast.LENGTH_LONG).show();
            AddBalanceReceiptDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
          //  Log.v(TAG, String.format("doInBackground ::  params= %s", params));


            HashMap<String, String> AddBalanceReceiptDetails = new HashMap<String, String>();
            AddBalanceReceiptDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(CourseDetailsActivity.this));
            AddBalanceReceiptDetails.put("member_id",member_id);
            AddBalanceReceiptDetails.put("name",nameTV.getText().toString());
            AddBalanceReceiptDetails.put("contact",contactTV.getText().toString());
            AddBalanceReceiptDetails.put("email",Email);
            AddBalanceReceiptDetails.put("invoice_id",invoice_id);
            AddBalanceReceiptDetails.put("subtotal",subtotal);
            AddBalanceReceiptDetails.put("tax",Tax);
            AddBalanceReceiptDetails.put("tax_amount",TaxAmount);
            AddBalanceReceiptDetails.put("payment_type",paymentType);
            Log.v(TAG, String.format("doInBackground :: payment_type= %s", paymentType));
            AddBalanceReceiptDetails.put("payment_details",inputPaymentDtl.getText().toString());
            AddBalanceReceiptDetails.put("paid",inputPaid.getText().toString());
            AddBalanceReceiptDetails.put("balance",finalBalance);
            AddBalanceReceiptDetails.put("comment",inputComment.getText().toString());
            AddBalanceReceiptDetails.put("next_payment_date",inputNextFollDate.getText().toString());
            AddBalanceReceiptDetails.put("mem_own_exe", SharedPrefereneceUtil.getName(CourseDetailsActivity.this));
            Log.v(TAG, String.format("doInBackground :: executive name= %s", SharedPrefereneceUtil.getName(CourseDetailsActivity.this)));
            AddBalanceReceiptDetails.put("financial_year",FinancialYear);
            AddBalanceReceiptDetails.put("mode","AdminApp");
            Log.v(TAG, String.format("doInBackground :: financial_year= %s", FinancialYear));

            AddBalanceReceiptDetails.put("action", "add_balance_receipt");
            String domainurl= SharedPrefereneceUtil.getDomainUrl(CourseDetailsActivity.this);
            String loginResult2 = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, AddBalanceReceiptDetails);

            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult2));
            return loginResult2;
        }
    }


    private void AddBalanceReceiptDetails(String jsonResponse) {

        Log.v(TAG, String.format("loginServerResponse :: response = %s", jsonResponse));

        JSONObject jsonObjLoginResponse = null;
        try {
            jsonObjLoginResponse = new JSONObject(jsonResponse);
            String success = jsonObjLoginResponse.getString(getResources().getString(R.string.success));

            if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                Toast.makeText(CourseDetailsActivity.this,"Balance Paid succesfully",Toast.LENGTH_SHORT).show();
               // inputName.getText().clear();
                //inputContact.getText().clear();
                SendEnquirySmsClass();
                EmailLoginClass();
                // imageView.setImageResource(R.drawable.add_photo);

                // showCustomDialog();

                //inputEmail, inputPhone,inputAdd,inputReq,inputFollowupdate;
            }


            else if (success.equalsIgnoreCase(getResources().getString(R.string.zero)))
            {
                Toast.makeText(CourseDetailsActivity.this,"Your Balance is Already Paid",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(CourseDetailsActivity.this, CourseDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("filter_array_list", filterArrayList);
                intent.putExtra("BUNDLE",bundle);
                startActivity(intent);
               // inputContact.getText().clear();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void  EmailLoginClass() {
        CourseDetailsActivity.EmailLoginTrackClass ru = new CourseDetailsActivity.EmailLoginTrackClass();
        ru.execute("5");
    }

    class EmailLoginTrackClass extends AsyncTask<String, Void, String> {

        ServerClass ruc = new ServerClass();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(TAG, "onPreExecute");
            //showProgressDialog();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.v(TAG, String.format("onPostExecute :: response = %s", response));
            // dismissProgressDialog();
            //Toast.makeText(CandiateListView.this, response, Toast.LENGTH_LONG).show();
            //  Toast.makeText(NewCustomerActivity.this, response, Toast.LENGTH_LONG).show();
            EmailLoginDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> EmailLoginDetails = new HashMap<String, String>();
            EmailLoginDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(CourseDetailsActivity.this) );
            EmailLoginDetails.put("action", "show_email_login");
            String domainurl= SharedPrefereneceUtil.getDomainUrl(CourseDetailsActivity.this);
            //EnquiryForloyeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(EnquiryForloyee.this));
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, EmailLoginDetails);

            Log.v(TAG, String.format("doInBackground :: show_email_login= %s", loginResult));
            return loginResult;
        }
    }
    private void EmailLoginDetails(String jsonResponse) {

        Log.v(TAG, String.format("loginServerResponse :: response = %s", jsonResponse));

        JSONObject object = null;
        try {
            object = new JSONObject(jsonResponse);
            String success = object.getString(getResources().getString(R.string.success));

            if (success.equalsIgnoreCase(getResources().getString(R.string.zero))) {

                // showCustomDialog();

                //inputEmail, inputPhone,inputAdd,inputReq,inputFollowupdate;
            }
            else if (success.equalsIgnoreCase(getResources().getString(R.string.two)))
            {
                if (object != null) {
                    JSONArray jsonArrayCountry = object.getJSONArray("result");

                    if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
                        for (int i = 0; i < jsonArrayCountry.length(); i++) {
                            Log.v(TAG, "JsonResponseOpeartion ::");
                            JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                            if (jsonObj != null) {

                                Email_ID     = jsonObj.getString("Email_ID");
                                Password=jsonObj.getString("Password");
                                String Email_Status=jsonObj.getString("Email_Status");
                                Header=jsonObj.getString("Header");
                                Footer=jsonObj.getString("Footer");
                                if(Email_Status.equals("ON")){
                                    if(!Email.equals("")){
                                        receiptdatalass();
                                    }
                                }else {
                                    System.out.println("Email Status Is Off");
                                }
                            }
                        }
                    }else if(jsonArrayCountry.length()==0){
                        System.out.println("No records found");
                    }
                }
            }

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void receiptdatalass() {
        CourseDetailsActivity.ReceiptDataTrackclass ru = new CourseDetailsActivity.ReceiptDataTrackclass();
        ru.execute("5");
    }
    class ReceiptDataTrackclass extends AsyncTask<String, Void, String> {

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
            Log.v(TAG, String.format("onPostExecute :: show_receipt_data = %s", response));
            //dismissProgressDialog();
            viewDialog.hideDialog();
            //Toast.makeText(Employee.this, response, Toast.LENGTH_LONG).show();
            ReceiptDataDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> ReceiptDataDetails = new HashMap<String, String>();
            ReceiptDataDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(CourseDetailsActivity.this));
            ReceiptDataDetails.put("invoice_id", invoice_id);
            Log.v(TAG, String.format("doInBackground :: receipt data invoice_id= %s", invoice_id));
            ReceiptDataDetails.put("financial_yr", FinancialYear);
            ReceiptDataDetails.put("member_id", member_id);
            Log.v(TAG, String.format("doInBackground :: receipt data company id = %s", FinancialYear));
            Log.v(TAG, String.format("doInBackground :: receipt data company id = %s", SharedPrefereneceUtil.getSelectedBranchId(CourseDetailsActivity.this)));
            ReceiptDataDetails.put("action","show_receipt_data");
            String domainurl= SharedPrefereneceUtil.getDomainUrl(CourseDetailsActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, ReceiptDataDetails);
            Log.v(TAG, String.format("doInBackground :: show_receipt_data= %s", loginResult));
            return loginResult;
        }


    }

    private void ReceiptDataDetails(String jsonResponse) {

        Log.v(TAG, String.format("show_receipt_data :: jsonResponse = %s", jsonResponse));
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

                                    String Name = nameTV.getText().toString();
                                    String Member_Contact = contactTV.getText().toString();
                                    String Invoice_date = jsonObj.getString("Invoice_date");
                                    String invoice_date= Utility.formatDate(Invoice_date);
                                    String Package_Name = jsonObj.getString("Package_Name");
                                    String Duration_Days =jsonObj.getString("Duration_Days");
                                    String Session = jsonObj.getString("Session");
                                    String Start_Date = jsonObj.getString("Start_Date");
                                    String s_date= Utility.formatDateDB(Start_Date);
                                    String End_Date = jsonObj.getString("End_Date");
                                    String edate= Utility.formatDateDB(End_Date);
                                    String Rate = jsonObj.getString("Rate");
                                    String Final_paid = jsonObj.getString("Final_paid");;
                                    String Final_Balance =  jsonObj.getString("Final_Balance");
                                    if(Final_Balance.equals(".00")){
                                        Final_Balance="0.00";
                                    }
                                    String Invoice_ID = invoice_id;
//                                    String tax = Tax;
//                                    if(tax.equals(".00")){
//                                        tax="0.00";
//                                    }
                                    start_date=Start_Date;
                                    end_date=End_Date;
                                    String Member_Email_ID = Email;
                                    String Time =  jsonObj.getString("Time");;
                                    String Instructor_Name = jsonObj.getString("Instructor_Name");;
                                    String Package_Fees =  jsonObj.getString("Package_Fees");;
                                    String Discount =  jsonObj.getString("Discount");;
                                    if(Discount.equals(".00")){
                                        Discount="0.00";
                                    }
                                    String Registration_Fees =  jsonObj.getString("Registration_Fees");;
                                    if(Registration_Fees.equals(".00")){
                                        Registration_Fees="0.00";
                                    }

                                    String Company_Name = SharedPrefereneceUtil.getCompanyName(CourseDetailsActivity.this)+"-"+ SharedPrefereneceUtil.getSelectedBranch(CourseDetailsActivity.this);
                                    String Branch=SharedPrefereneceUtil.getSelectedBranch(CourseDetailsActivity.this);
                                    String Address = jsonObj.getString("Address");
                                    String Contact = jsonObj.getString("Contact");
                                    String MemberGST_No = jsonObj.getString("MemberGST_No");
                                    String GST_No = jsonObj.getString("GST_No");
                                    String TermsAndConditions = jsonObj.getString("TermsAndConditions");
                                    TermsAndConditions = TermsAndConditions.replace("\r\n", "<br />");
                                     Logo = jsonObj.getString("Logo");
                                    String l=Logo.replaceAll("\\s+","%20");
                                    // Logo.replace(" ","%20");
                                    String domainurl= SharedPrefereneceUtil.getDomainUrl(CourseDetailsActivity.this);
                                    final String imgurl=domainurl+ServiceUrls.IMAGES_URL+l;
                                    Log.d(TAG, "imgurl: " +imgurl);

                                    String textBody = "";
                                    String CGST="";
                                    JSONArray jsonArrayPayTrasa = jsonObj.getJSONArray("payment_transa");
                                    if (jsonArrayPayTrasa != null && jsonArrayPayTrasa.length() > 0) {
                                        for (int loopCount = 0; loopCount < jsonArrayPayTrasa.length(); loopCount++)
                                        {
                                            JSONObject jsonObj1 = jsonArrayPayTrasa.getJSONObject(loopCount);
                                            if (jsonObj1 != null) {
                                                String Receipt_Id = jsonObj1.getString("Receipt_Id");
                                                // String start_date=Utility.formatDateDB(Start_Date);
                                                String ReceiptDate = jsonObj1.getString("ReceiptDate");
                                                String receipt_date= Utility.formatDateDB(ReceiptDate);
                                                String Tax = jsonObj1.getString("Tax");
                                                if(Tax.equals(".00")){
                                                    Tax="0.00";
                                                }
                                                String TaxAmount = jsonObj1.getString("TaxAmount");
                                                // String Taxamount = TaxAmount;
                                                if(TaxAmount.equals(".00")){
                                                    TaxAmount="0.00";
                                                }
                                                String Paid =  jsonObj1.getString("Paid");
                                                String PaymentType =  jsonObj1.getString("PaymentType");
                                                String PaymentDetails =  jsonObj1.getString("PaymentDetails");
                                                String ReceiptOwnerExecutive =  jsonObj1.getString("ReceiptOwnerExecutive");
                                                String SubTotal =  jsonObj1.getString("SubTotal");
                                                double cgst =Double.parseDouble(Tax)/2;
                                                CGST=String.valueOf(cgst);
                                                double sgst=Double.parseDouble(TaxAmount)/2;
                                                String SGST=String.valueOf(sgst);
                                                textBody += "  <tr >\n \n" +
                                                        "    <td width='10%'>"+Receipt_Id+"</td>\n \n" +
                                                        "     <td width='15%'>"+receipt_date+"</td>\n\n" +
                                                        "     <td width='12%'>"+SubTotal+"</td>\n\n" +
                                                        "    <td width='8%'>"+Tax+"</td> \n\n" +
                                                        "    <td width='9%'>"+SGST+"</td> \n\n" +
                                                        "    <td width='9%'>"+SGST+"</td> \n\n" +
                                                        "    <td width='12.5%'>"+TaxAmount+"</td>\n\n" +
                                                        "    <td width='12.5%'>"+Paid+"</td>\n\n" +
                                                        "    <td width='12.5%'>"+PaymentType+"</td>\n\n" +
                                                        "    <td width='12.5%'>"+PaymentDetails+"</td>\n\n" +
                                                        "    <td width='13%'>"+ReceiptOwnerExecutive+"</td>\n\n" +
                                                        "    </tr>\n";
                                            }
                                        }

                                    }
//                                    final String messagehtml = "<!DOCTYPE html>\n" +
//                                            "\n" +
//                                            "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
//                                            "<head runat=\"server\">\n" +
//                                            "    <title></title>\n" +
//                                            "</head>\n" +
//                                            "<body>\n" +
//                                            "    <form runat=\"server\">\n" +
//                                            "\n" +
//                                            " <div class=\"row\">\n" +
//                                            "                <div class=\"column\" >\n" +
//                                            "                    <div >\n" +
//                                            "                        <address>\n" +
//                                            "                            <strong style=\"font: 700;\">\n" +
//                                            "                               </strong><br/>" +
//                                            "                          <br/>" +
//                                            "                           <br/>" +
//                                            "                        </address>" +
//                                            "                    </div>" +
//                                            "                    <div >" +
//                                            "                        <address>" +
//                                            "                        " +
////                                            "                            <img src="+ imgurl +">" +
//                                            "                        </address>\n" +
//                                            "                    </div>" +
//                                            "                </div>" +
//                                            "                <div class=\"column\" >\n" +
//                                            "                    <div  >\n" +
//                                            "                        <address>\n" +
//                                            "                            <strong></strong><br/>\n" +
//                                            "                            <strong style=\"font: 900;\">"+"</strong><br/>\n" +
//                                            "                           <br/>\n" +
//                                            "                        </address>\n" +
//                                            "                    </div>" +
//                                            "                    <div >" +
//                                            "                        <address>" +
//                                            "                            <strong>"+"</strong><br/>\n" +
//                                            "\n" +
//                                            "                            <strong> "+"</strong><br/>\n" +
//                                            "                            <strong>  "+"</strong>\n" +
//                                            "                        </address>\n" +
//                                            "                    </div>\n" +
//                                            "                </div>\n" +
//                                            "      </div>\n" +
//                                            "    <div >\n" +
//
//                                            "            <div >\n" +
//                                            "                <div  >\n" +
//                                            "                        <h3 ><strong>Bill To</strong></h3>\n" +
//                                            "                    </div>\n" +
//                                            "                <div >\n" +
//                                            "              <div >\n" +
//                                            "             <table border = '1' cellpadding=\"6\"  width=\"100%\" >\n" +
//                                            "             <thead height=\"100\">\n" +
//                                            "                   <tr height=\"100\" >\n" +
//                                            "                      <th ><strong>ID</strong></th>\n" +
//                                            "                      <th ><strong>Name</strong></th>\n" +
//                                            "                      <th ><strong>Email Id</strong></th>                                    \n" +
//                                            "                      <th ><strong>Contact</strong></th>\n" +
//                                            "                      <th ><strong>GST No</strong></th>\n" +
//                                            "                    </tr>\n" +
//                                            "             </thead>\n" +
//                                            "           <tbody >\n" +
//                                            "  <tr >\n \n" +
//                                            "    <td width='12%'>"+member_id+"</td>\n \n" +
//                                            "     <td width='25%'>"+Name+"</td>\n\n" +
//                                            "    <td width='30%'>"+Email+"</td> \n\n" +
//                                            "    <td width='16%'>"+Member_Contact+"</td>\n\n" +
//                                            "    <td width='17%'>"+MemberGST_No+"</td>\n\n" +
//                                            "    </tr>\n"+
//                                            "          </tbody>\n" +
//                                            "       </table>\n" +
//                                            "       </div>\n" +
//                                            "       </div>\n" +
//                                            "                     \n" +
//                                            "      </div>\n" +
//                                            "     </div>\n" +
//                                            "                           <br/>\n" +
//                                            "\n" +
//                                            "        <div  >\n" +
//                                            "\n" +
//                                            "            <div >\n" +
//                                            "                <div >\n" +
//                                            "                    <div  >\n" +
//                                            "                        <h3 ><strong>Package Summary</strong></h3>\n" +
//                                            "                    </div>\n" +
//                                            "                    <div  >\n" +
//                                            "                        <div  >\n" +
//                                            "   <table border = '1' cellpadding=\"6\"  width=\"100%\" >\n" +
//                                            "                             <thead height=\"100\" >\n" +
//                                            "                                    <tr height=\"100\" >\n" +
//                                            "                                      <th ><strong>Package</strong></th>\n" +
//                                            "                                       <th ><strong>Duration</strong></th>\n" +
//                                            "                                       <th ><strong>Session</strong></th>                                    \n" +
//                                            "                                       <th ><strong>StartDate</strong></th>\n" +
//                                            "                                       <th ><strong>EndDate</strong></th>\n" +
//                                            "                                       <th ><strong>Time</strong></th>\n" +
//                                            "                                       <th ><strong>Instructor</strong></th>\n" +
//                                            "                                       <th ><strong>Package Fees</strong></th>\n" +
//                                            "                                    </tr>\n" +
//                                            "                                </thead>\n" +
//                                            "                               <tbody height=\"100\" >\n" +
//                                            "                                    <tr height=\"100\" >\n" +
//                                            "                                        <td width='12.5%'>"+Package_Name+"</td>\n" +
//                                            "                                         <td width='11.5%'>"+Duration_Days+"</td>\n" +
//                                            "                                        <td width='11%'>"+Session+"</td>                                       \n" +
//                                            "                                        <td width='14%'>"+s_date+"</td>\n" +
//                                            "                                        <td width='14%'>"+edate+"</td>\n" +
//                                            "                                        <td width='8%'>"+Time+"</td>\n" +
//                                            "                                        <td width='13%'>"+Instructor_Name+"</td>\n" +
//                                            "                                        <td width='12%'>"+Package_Fees+"</td>\n" +
//                                            "                                    </tr>\n" +
//                                            "                                </tbody>\n" +
//                                            "                            </table>\n" +
//                                            "                        </div>\n" +
//                                            "                    </div>\n" +
//                                            "                    <div >\n" +
//                                            "                        <div  >\n" +
//                                            "                            <table border = '1' cellpadding=\"6\"  width=\"100%\" >\n" +
//                                            "                                <thead height=\"100\">\n" +
//                                            "                                    <tr height=\"100\" >\n" +
//                                            "                                        <th ><strong>Discount</strong></th>\n" +
//                                            "                                        <th ><strong>Reg Fees</strong></th>\n" +
//                                            "                                        <th ><strong>Total Amount</strong></th>\n" +
//                                            "                                        <th ><strong>Paid Amount</strong></th>\n" +
//                                            "                                        <th ><strong>Balance</strong></th>\n" +
//                                            "                                    </tr>\n" +
//                                            "                                </thead>\n" +
//                                            "                                <tbody >\n" +
//                                            "                                    <tr>\n" +
//                                            "                                        <td >"+Discount+"</td>\n" +
//                                            "                                        <td >"+Registration_Fees+"</td>\n" +
//                                            "                                       \n" +
//                                            "                                        <td >"+Rate+"</td>\n" +
//                                            "                                        <td >"+Final_paid+"</td>\n" +
//                                            "                                        <td >"+Final_Balance+"</td>\n" +
//                                            "                                    </tr>\n" +
//                                            "                                </tbody>\n" +
//                                            "                            </table>\n" +
//                                            "                        </div>\n" +
//                                            "                    </div>\n" +
//                                            "\n" +
//                                            "                </div>\n" +
//                                            "            </div>\n" +
//                                            "        </div>\n" +
//                                            "\n" +
//                                            "                           <br/>\n" +
//                                            "       \n" +
//                                            " <div  >\n" +
//                                            "\n" +
//                                            "    <div >\n" +
//
//                                            "            <div >\n" +
//                                            "                <div  >\n" +
//                                            "                        <h3 ><strong>Payment Transaction</strong></h3>\n" +
//                                            "                    </div>\n" +
//                                            "                <div >\n" +
//                                            "              <div >\n" +
//                                            "             <table border = '1' cellpadding=\"6\"  width=\"100%\" >\n" +
//                                            "             <thead height=\"100\">\n" +
//                                            "                   <tr height=\"100\" >\n" +
//                                            "                      <th ><strong>#RNo</strong></th>\n" +
//                                            "                      <th ><strong>Date</strong></th>\n" +
//                                            "                      <th ><strong>Tax</strong></th>                                    \n" +
//                                            "                      <th ><strong>Tax Amount</strong></th>\n" +
//                                            "                      <th ><strong>Paid Amount</strong></th>\n" +
//                                            "                       <th ><strong>Payment Mode</strong></th>\n" +
//                                            "                       <th ><strong>Payment Details</strong></th>\n" +
//                                            "                       <th ><strong>Executive</strong></th>\n" +
//                                            "                    </tr>\n" +
//                                            "             </thead>\n" +
//                                            "           <tbody >\n" +
//                                            textBody+
//                                            "          </tbody>\n" +
//                                            "       </table>\n" +
//                                            "       </div>\n" +
//                                            "       </div>\n" +
//                                            "                     \n" +
//                                            "      </div>\n" +
//                                            "     </div>\n" +
//                                            "  </div>\n" +
//                                            "\n" +
//                                            "      \n" + "                           <br/>\n" +
//                                            "\n" +
//                                            "  <div  >\n" +
//                                            "    <div >\n" +
//                                            "       <div  >\n" +
//                                            "          <h3 ><strong>Terms And Conditions</strong></h3>\n" +
//                                            "       </div>\n" +
//                                            "                  \n" +
//                                            "                           "+TermsAndConditions+"\n" +
//                                            "                     \n" +
//                                            "    \n" +
//                                            "                \n" +
//                                            "            </div>\n" +
//                                            "        </div>\n" +
//                                            "\n" +
//                                            "        </form>\n" +
//                                            "</body>\n" +
//                                            "</html>";
                                   final  Document document = new Document(PageSize.A4);

                                    try {

                                        PdfWriter docWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
                                        document.open();
//

                                        PdfContentByte cb = docWriter.getDirectContent();
                                        //initialize fonts for text printing
                                        initializeFonts();

                                        //Add Image from some URL
                                        Thread thread = new Thread(new Runnable() {
                                            //
                                            @Override
                                            public void run() {
                                                try  {
                                                    Image image = Image.getInstance(new URL(imgurl));
                                                    image.setAbsolutePosition(50,730);
                                                    image.scalePercent(10);
                                                    image.scaleToFit(100, 70);
                                                    document.add(image);
                                                    //Your code goes here
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                        thread.start();
//
                                        // creating a sample invoice with some customer data
                                        String delimiter = " ";
                                        int partitionSize = 6;
                                        String add="";
                                        int x=120;
                                        int y=765;
                                        for (Iterable<String> iterable : Iterables.partition(Splitter.on(delimiter).split(Address), partitionSize)) {
                                            System.out.println(Joiner.on(delimiter).join(iterable));
                                            add+=Joiner.on(delimiter).join(iterable)+"<br/>";

                                            //createText(cb,x,y,Joiner.on(delimiter).join(iterable));
                                            y= y-10;
                                        }

                                        String csstext=" .text-right { text-align: right;} .p{border: 1px solid black;font-family: Helvetica,Arial,sans-serif;font-size: 12px;}" +
                                                "        .addr {" +
                                                "            margin-bottom: 10px;" +
                                                "            font-style: normal;" +
                                                "            margin-left: 20px;font-size: 12px;" +
                                                "            line-height: 1.428571429;" +
                                                "        }" +
                                                "        .table > thead > tr > td {" +
                                                "            padding: 1px;" +
                                                "            line-height: 1.428571429;" +
                                                "            vertical-align: top;" +
                                                "            border-top: 1px solid #ddd;" +
                                                "            margin-left: 10px;" +
                                                "font-family: Helvetica,Arial,sans-serif;font-size: 12px;" +
                                                "        }\n" +
                                                "        .column {\n" +
                                                "          float: left;\n" +
                                                "          width: 30.33%;\n" +
                                                "          padding: 10px;" +
                                                "        }\n" +
                                                "        .col-3{\n" +
                                                "            float: left;\n" +
                                                "          width: 20%;\n" +
                                                "          padding: 10px;\n" +
                                                "        }\n" +
                                                "        .col-6{\n" +
                                                "            float: left;\n" +
                                                "          width: 70%;\n" +
                                                "        }\n" +
                                                "         .col-4{\n" +
                                                "            float: left;\n" +
                                                "          width: 10%;\n" +
                                                "text-align: right;" +
                                                "        }\n" +
                                                "        .id{\n" +
                                                "            float: left;\n" +
                                                "          width: 50%;\n" +
                                                "          margin-left: 5px;\n" +
                                                "        }\n" +
                                                "        .email{\n" +
                                                "            float: left;\n" +
                                                "          width: 49%;\n" +
                                                "        }" +
                                                "        .row:after {\n" +
                                                "          content: \"\";\n" +
                                                "          display: table;\n" +
                                                "          clear: both;\n" +
                                                "border-bottom: 1px solid black;margin-top: 13px;" +
                                                "        }\n" +
                                                "        .col-md-3{\n" +
                                                "             float: left;\n" +
                                                "           width: 28%;\n" +
                                                "          height: 100%;\n" +
                                                "    overflow: hidden;\n" +
                                                "        } .col-md-4{" +
                                                "    float: left; width: 20%;height: 100%; overflow: hidden;}\n" +
                                                "        .table > thead > tr > td {\n" +
                                                "            line-height: 1.428571429;\n" +
                                                "            vertical-align: top;\n" +
                                                "            border: 1px solid #ddd;\n" +
                                                "            margin-left: 0px;\n" +
                                                "             table-layout: fixed;\n" +
                                                "           }\n" +
                                                "           .table-bordered {\n" +
                                                "            empty-cells: show;\n" +
                                                "        }\n" +
                                                "        table {\n" +
                                                "        border-spacing: 0;\n" +
                                                "         empty-cells: show;\n" +
                                                "         }\n" +
                                                "        table td[class*=col-], table th[class*=col-] {\n" +
                                                "            position: static;\n" +
                                                "            float: none;\n" +
                                                "            display: table-cell;\n" +
                                                "        }\n" +
                                                "        .container {\n" +
                                                "          display: flex;\n" +
                                                "          flex-wrap: wrap;\n" +
                                                "        }\n" +
                                                "        thead {\n" +
                                                "    display: table-header-group;\n" +
                                                "    vertical-align: middle;\n" +
                                                "    border-color: inherit;\n" +
                                                "}" +
                                                " td{font-family: Helvetica,Arial,sans-serif;font-size: 12px; }";

                                        String HTML = "<html><body style=\"font-family: Helvetica,Arial,sans-serif;color: #333\">\n" +
                                                "        <div class=\"container\" >\n" +
                                                "            <div class=\"row\">\n" +
                                                "                    <div class=\"row\" style=\"border-bottom: 1px solid black;margin-top: 13px;\">\n" +
                                                "                        <div class=\"col-3\"> \n" +
                                                "                                <img src=\"http://demo.gymtime.in/CompanyLogo/NavkarDemo_2020125141355.png\" style=\"width: 120px; height: 100px;\" />                                                                                   \n" +
                                                "                        </div>\n" +
                                                "                        <div class=\"col-6\" style=\"margin-left: 0px; padding :5px;\">\n" +
                                                "                            <div class=\"addr\">\n" +
                                                "                                 <table cellpadding=\"4\">\n" +
                                                "                                <thead>\n" +
                                                "                                    <tr>\n" +
                                                "                                        <td ><strong>"+Company_Name+"</strong></td>\n" +
                                                "                                    </tr>\n" +
                                                "                                    <tr>\n" +
                                                "                                        <td  >"+add+"</td>\n" +
                                                "                                    </tr>\n" +
                                                "                                    <tr>\n" +
                                                "                                        <td  >"+Branch+"</td>\n" +
                                                "                                    </tr>\n" +
                                                "                                    <tr>\n" +
                                                "                                        <td  >"+Contact+"</td>\n" +
                                                "                                    </tr>\n" +
                                                "                                    <tr>\n" +
                                                "                                        <td  >"+GST_No+"</td>\n" +
                                                "                                    </tr>\n" +
                                                "                                </thead>\n" +
                                                "                            </table>\n" +
                                                "                               \n" +
                                                "                            </div>\n" +
                                                "                        </div>\n" +
                                                "                        <div class=\"col-4\" style=\"text-align: right;\">\n" +
                                                "                               <table cellpadding=\"4\">\n" +
                                                "                                <thead>\n" +
                                                "                                    <tr>\n" +
                                                "                                        <td  ><strong>Invoice No : </strong>"+invoice_id+"</td>\n" +
                                                "                                    </tr>\n" +
                                                "                                    <tr>\n" +
                                                "                                        <td  ><strong>Invoice Date :</strong>"+invoice_date+"</td>\n" +
                                                "                                    </tr>\n" +
                                                "                                   \n" +
                                                "                                </thead>\n" +
                                                "                            </table>\n" +
                                                "                        </div>\n" +
                                                "                    </div> " +
                                                "  <hr/>  " +
                                                "<div class=\"row\" style=\"margin-top: 9px; border-bottom: 1px solid black\">\n" +
                                                "                        <div class=\"id\" style=\"text-align: inherit\">\n" +
                                                "                            <table cellpadding=\"4\">\n" +
                                                "                                <thead>\n" +
                                                "                                    <tr>\n" +
                                                "                                        <td  ><strong>ID:</strong></td>\n" +
                                                "                                        <td > "+member_id+"</td>\n" +
                                                "                                       \n" +
                                                "                                    </tr>\n" +
                                                "                                    <tr>\n" +
                                                "                                        <td  ><strong>Name:</strong></td>\n" +
                                                "                                        <td  >"+Name+"</td>\n" +
                                                "                                        \n" +
                                                "                                    </tr>\n" +
                                                "                                    <tr>\n" +
                                                "                                        <td  ><strong>Contact:</strong></td>\n" +
                                                "                                        <td  >"+Member_Contact+"</td>\n" +
                                                "                                    </tr>\n" +
                                                "                                </thead>\n" +
                                                "                            </table>\n" +
                                                "                        </div>\n" +
                                                "                         <div class=\"email\" style=\"margin-left: 0px;\">\n" +
                                                "                              <table cellpadding=\"4\">\n" +
                                                "                                <thead>\n" +
                                                "                                    <tr>\n" +
                                                "                                         <td ><strong>Email:</strong></td>\n" +
                                                "                                        <td >"+Email+"</td>\n" +
                                                "                                     </tr>\n" +
                                                "                                     <tr>\n" +
                                                "                                          <td class=\"text-right\" ><strong>GST No:</strong></td>\n" +
                                                "                                        <td >"+MemberGST_No+"</td>\n" +
                                                "                                     </tr>\n" +
                                                "                                </thead>\n" +
                                                "                            </table>\n" +
                                                "                         </div>\n" +
                                                "                    </div>" +
                                                " <hr/>" +
                                                "<div class=\"row\">                                                                                       \n" +
                                                "                        <div  style=\"text-align: inherit;\">\n" +
                                                "                            <div class=\"container\" style=\"width:100%; overflow: hidden;padding: 0px 5px 5px 5px;\">\n" +
                                                "                                <table cellpadding=\"1\" class=\"table table-bordered\" style=\"margin-bottom: 0px;width:100%;padding: 5px;\">\n" +
                                                "                                    <thead >\n" +
                                                "                                        <tr  style=\"width:100%;height:10px;border: thick; border-color: black;\">\n" +
                                                "                                            <td class=\"col-md-3\"><strong>Package :</strong></td>\n" +
                                                "                                            <td  class=\"col-md-3\">"+Package_Name+"</td>\n" +
                                                "                                            <td  class=\"col-md-4\"><strong>Amount</strong></td>\n" +
                                                "                                            <td  class=\"col-md-4\" style=\"text-align: right; \">"+Package_Fees+"</td>\n" +
                                                "                                        </tr>\n" +
                                                "                                        <tr style=\"width:100%;height:10px;border: thick; border-color: black;\">\n" +
                                                "                                            <td class=\"col-md-3\"><strong>Duration/Sessions :</strong></td>\n" +
                                                "                                             <td class=\"col-md-3\">"+Duration_Days+"/"+Session+"</td>\n" +
                                                "                                            <td class=\"col-md-4\"><strong>Discount</strong></td>\n" +
                                                "                                            <td class=\"col-md-4\" style=\"text-align: right; \">"+Discount+"</td>\n" +
                                                "                                        </tr>\n" +
                                                "                                        <tr style=\"width:100%;height:10px;border: thick; border-color: black;\">\n" +
                                                "\n" +
                                                "                                            <td class=\"col-md-3\"><strong>Date :</strong></td>\n" +
                                                "                                            <td class=\"col-md-3\">"+s_date+" to "+edate+"</td>\n" +
                                                "                                            <td class=\"col-md-4\"><strong>Reg Fees</strong></td>\n" +
                                                "                                            <td class=\"col-md-4\" style=\"text-align: right; \">"+Registration_Fees+"</td>\n" +
                                                "                                        </tr>\n" +
                                                "                                        <tr style=\"width:100%;height:10px;border: thick; border-color: black;\">\n" +
                                                "\n" +
                                                "                                            <td class=\"col-md-3\"><strong>Time  :</strong></td>\n" +
                                                "                                            <td class=\"col-md-3\">"+Time+"</td>\n" +
                                                "                                            <td class=\"col-md-4\"><strong>Total Amount</strong></td>\n" +
                                                "                                            <td class=\"col-md-4\" style=\"text-align: right; \">"+Rate+"</td>\n" +
                                                "                                        </tr>\n" +
                                                "                                        <tr style=\"width:100%;height:10px;border: thick; border-color: black;\">\n" +
                                                "\n" +
                                                "                                            <td class=\"col-md-3\"><strong>Instructor:</strong></td>\n" +
                                                "                                             <td class=\"col-md-3\">"+Instructor_Name+"</td>\n" +
                                                "                                            <td class=\"col-md-4\"><strong>Paid Amount</strong></td>\n" +
                                                "                                            <td class=\"col-md-4\" style=\"text-align: right; \">"+Final_paid+"</td>\n" +
                                                "                                        </tr>\n" +
                                                "                                        <tr style=\"width:100%;height:10px;border: thick; border-color: black;\">\n" +
                                                "                                            <td class=\"col-md-3\"><strong>&nbsp;  </strong></td>                          \n" +
                                                "                                            <td class=\"col-md-3\"><strong>&nbsp; </strong></td>\n" +
                                                "                                            <td class=\"col-md-4\"><strong>Balance</strong></td>\n" +
                                                "                                            <td class=\"col-md-4\" style=\"text-align: right; \">"+Final_Balance+"</td>\n" +
                                                "                                        </tr>                                                                             \n" +
                                                "                                    </thead>\n" +
                                                "                                </table>\n" +
                                                "                            </div>\n" +
                                                "                        </div>\n" +
                                                "                    </div>" +
                                                " <div class=\"row\">\n" +
                                                "                                <div style=\"padding-left: 5px;\">\n" +
                                                "                                    <h3 ><strong>Payment Transaction:</strong></h3>\n" +
                                                "                                </div><br/>\n" +
                                                "                                <div  style=\"width:99%; overflow: hidden;padding: 0px 5px 5px 5px;\">\n" +
                                                "                                      <font size=\"2\" face=\"Courier New\" >  <table cellpadding=\"1\" class=\"table table-bordered\" style=\"margin-bottom: 0px;width:100%;\" >\n" +
                                                "                                            <thead>\n" +
                                                "                                                <tr style=\"width:100%;height:10px;border: thick; border-color: black;\">\n" +
                                                "                                                    <td style=\"padding: 1px;\"><strong>#RNo</strong></td>\n" +
                                                "                                                    <td style=\"padding: 1px;\"><strong>Pay Date</strong></td>\n" +
                                                "                                                    <td style=\"padding: 1px;\"><strong>Subtotal</strong></td>\n" +
                                                "                                                    <td style=\"padding: 1px;\"><strong>Tax</strong></td>\n" +
                                                "                                                    <td style=\"padding: 1px;\"><strong>CGST ("+CGST+"%)</strong></td>\n" +
                                                "                                                    <td style=\"padding: 1px;\"><strong>SGST ("+CGST+"%)</strong></td>\n" +
                                                "                                                    <td style=\"padding: 1px;\"><strong>Tax Amount</strong></td>\n" +
                                                "                                                    <td style=\"padding: 1px;\"><strong>Paid Amount</strong></td>\n" +
                                                "                                                    <td style=\"padding: 1px;\"><strong>Payment Mode</strong></td>\n" +
                                                "                                                    <td style=\"padding: 1px;\"><strong>Payment Details</strong></td>\n" +
                                                "                                                    <td style=\"padding: 1px;\"><strong>Executive</strong></td>\n" +
                                                "                                                </tr>\n" +
                                                textBody+

                                                "                                            </thead>\n" +
                                                "                                        </table></font>\n" +
                                                "                                    </div>\n" +
                                                "                        </div>           \n" +
                                                "                    <div class=\"row\">\n" +
                                                "                                <div style=\"margin-top: 4px; margin-bottom: 4px;padding: 5px;\">\n" +
                                                "                                    <h3  style=\"margin-bottom: 6px\"><strong>Terms And Conditions : </strong></h3>\n" +
                                                "                                    <div>                                          \n" +
                                                "                                        <div  ><table cellpadding=\"4\" style=\"width:100%\">\n" +
                                                "  <tr><td class=\"p\">"+TermsAndConditions+"</td> </tr>\n" +
                                                "</table>\n" +
                                                "                                        </div>\n" +
                                                "                                    </div>\n" +
                                                "                                </div>\n" +
                                                "                    </div>" +
                                                "                </div>\n" +
                                                "            </div>\n" +
                                                "</body></html>";
                                        InputStream is = new ByteArrayInputStream(HTML.getBytes());
                                        InputStream cssggh = new ByteArrayInputStream(csstext.getBytes());
                                        XMLWorkerHelper.getInstance().parseXHtml(docWriter, document, is, cssggh,Charset.forName("UTF-8"));

                                        document.close();
                                    }
                                    catch(Exception e){
                                        e.printStackTrace();
                                    }

                                    final String subject=Company_Name+" Receipt";
                                    final String message=Header+"\nPlease find the attachment of Your Package Details\n\n"+Footer;


                                    CourseDetailsActivity.this.runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                            //second async stared within a asynctask but on the main thread
                                            (new AsyncTask<String, String, Void>() {
                                                ServerClass ruc = new ServerClass();

                                                @Override
                                                protected Void doInBackground(String... params) {
                                                    Mail m = new Mail(Email_ID, Password);

                                                    String[] toArr = { Email, "tulsababar01@gmail.com"};
                                                    // Log.v(TAG, String.format(" Email array to send = %s", toArr));
                                                    m.setTo(toArr);
                                                    m.setFrom("tulsababar.ndsoft@gmail.com");
                                                    m.setSubject(subject);
                                                    m.setBody(message);
                                                    if (Build.VERSION.SDK_INT >= 23)
                                                    {
                                                        if (checkPermission())
                                                        {
                                                            // final File  file = BitmapSaver.saveImageToExternalStorage(RenewActivity.this, bmpqr);
                                                            m.setAttachment(pdfFile);
                                                            m.setAttachmentName(FilePath);
                                                            m.setAttachmentNamePath("BalanceDetails.pdf");
                                                            // Code for above or equal 23 API Oriented Device
                                                            // Your Permission granted already .Do next code
                                                        } else {
                                                            requestPermission(); // Code for permission
                                                        }
                                                    }else{
                                                        // final File  file = BitmapSaver.saveImageToExternalStorage(RenewActivity.this, bmpqr);
                                                        m.setAttachment(pdfFile);
                                                        m.setAttachmentName(FilePath);
                                                        m.setAttachmentNamePath("BalanceDetails.pdf");
                                                    }
                                                    try {
                                                        if(m.send()) {
                                                            runOnUiThread(new Runnable() {
                                                                public void run() {

                                                                    Toast.makeText(CourseDetailsActivity.this, "Email was sent successfully.", Toast.LENGTH_LONG).show();

                                                                }
                                                            });
                                                        } else {
                                                            Toast.makeText(CourseDetailsActivity.this, "Email was not sent.", Toast.LENGTH_LONG).show();
                                                        }
                                                    } catch(Exception e) {
                                                        Log.e("MailApp", "Could not send email", e);
                                                    }
                                                    return null;
                                                }

                                                @Override
                                                protected void onPostExecute(Void aVoid) {
                                                    super.onPostExecute(aVoid);


                                                }
                                            }).execute();

                                        }
                                    });

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
    // ******************* send sms for add enquiry **************
    public void  SendEnquirySmsClass() {
        CourseDetailsActivity.SendEnquirySmsTrackClass ru = new CourseDetailsActivity.SendEnquirySmsTrackClass();
        ru.execute("5");
    }

    class SendEnquirySmsTrackClass extends AsyncTask<String, Void, String> {

        ServerClass ruc = new ServerClass();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(TAG, "onPreExecute");
           // showProgressDialog();
           // viewDialog.showDialog();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.v(TAG, String.format("onPostExecute :: response = %s", response));
           // dismissProgressDialog();
           // viewDialog.hideDialog();
            SendEnquirySmsDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
           // Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> EnquiryForDetails = new HashMap<String, String>();

            EnquiryForDetails.put("type","balancepaid" );
            EnquiryForDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(CourseDetailsActivity.this) );
            EnquiryForDetails.put("action", "sms_for_add_enquiry");
            String domainurl= SharedPrefereneceUtil.getDomainUrl(CourseDetailsActivity.this);
            //EnquiryForloyeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(EnquiryForloyee.this));
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, EnquiryForDetails);
            Log.v(TAG, String.format("doInBackground :: sms_for_add_enquiry= %s", loginResult));
            return loginResult;
        }
    }
    private void SendEnquirySmsDetails(String jsonResponse) {

        Log.v(TAG, String.format("loginServerResponse :: response = %s", jsonResponse));

        JSONObject object = null;
        try {
            object = new JSONObject(jsonResponse);
            String success = object.getString(getResources().getString(R.string.success));

            if (success.equalsIgnoreCase(getResources().getString(R.string.zero))) {
                coursedetailsclass();
                finish();
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
            else if (success.equalsIgnoreCase(getResources().getString(R.string.two)))
            {
                if (object != null) {
                    JSONArray jsonArrayResult = object.getJSONArray("result");


                    if (jsonArrayResult != null && jsonArrayResult.length() > 0) {

                        for (int i = 0; i < jsonArrayResult.length(); i++) {

                            Log.v(TAG, "JsonResponseOpeartion ::");
                            JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                            if (jsonObj != null) {

                                afterEnquirySms = jsonObj.getString("balancepaid");
                                afterEnquirySms = afterEnquirySms.replace(".", "");
                                afterEnquirySms = afterEnquirySms.replace("#REC#", inputPaid.getText().toString());
                                afterEnquirySms = afterEnquirySms.replace("#RemBal#", inputBalance.getText().toString());
                                afterEnquirySms = afterEnquirySms.replace("#NextBalanceDate#", inputNextFollDate.getText().toString());

                                if(!afterEnquirySms.equals("")) {
                                    CourseDetailsActivity.this.runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                            //second async stared within a asynctask but on the main thread
                                            (new AsyncTask<String, String, String>() {
                                                ServerClass ruc = new ServerClass();

                                                @Override
                                                protected String doInBackground(String... params) {
                                                    String loginResult2 = ruc.SendSMS(contactTV.getText().toString(), afterEnquirySms,
                                                            SharedPrefereneceUtil.getSmsUsername(CourseDetailsActivity.this),
                                                            SharedPrefereneceUtil.getSmsPassword(CourseDetailsActivity.this),
                                                            SharedPrefereneceUtil.getSmsRoute(CourseDetailsActivity.this),
                                                            SharedPrefereneceUtil.getSmsSenderid(CourseDetailsActivity.this));
                                                    Log.v(TAG, String.format("doInBackground :: Send Sms after enquiry= %s", loginResult2));
                                                    return loginResult2;
                                                }

                                                @Override
                                                protected void onPostExecute(String response) {
                                                    super.onPostExecute(response);
                                                    Log.v(TAG, String.format("onPostExecute :: response = %s", response));
                                                    coursedetailsclass();
                                                    finish();
                                                    //startActivity(getIntent());
                                                    //overridePendingTransition(0, 0);
                                                    Intent intent=new Intent(CourseDetailsActivity.this, CourseDetailsActivity.class);
                                                    Bundle bundle = new Bundle();
                                                    bundle.putSerializable("filter_array_list", filterArrayList);
                                                    intent.putExtra("BUNDLE",bundle);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    startActivity(intent);
                                                    overridePendingTransition(0, 0);
                                                   // moveTaskToBack(false);
                                                }
                                            }).execute();

                                        }
                                    });
                                }else{
                                    coursedetailsclass();
                                    finish();
                                    startActivity(getIntent());
                                    overridePendingTransition(0, 0);

                            }
                            }
                        }
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void coursedetailsclass() {
        CourseDetailsActivity. CourseDetailsTrackclass ru = new CourseDetailsActivity. CourseDetailsTrackclass();
        ru.execute("5");
    }
    class  CourseDetailsTrackclass extends AsyncTask<String, Void, String> {
        ServerClass ruc = new ServerClass();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(TAG, "onPreExecute");
            //showProgressDialog();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.v(TAG, String.format("onPostExecute :: response = %s", response));
            //dismissProgressDialog();
            //Toast.makeText(Employee.this, response, Toast.LENGTH_LONG).show();
             CourseDetailsDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String>  CourseDetailsDetails = new HashMap<String, String>();
             CourseDetailsDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(CourseDetailsActivity.this));
             CourseDetailsDetails.put("member_id",member_id );
             CourseDetailsDetails.put("invoice_id",invoice_id );
             CourseDetailsDetails.put("financial_yr",FinancialYear );
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(CourseDetailsActivity.this)));
            Log.v(TAG, String.format("doInBackground :: member_id id = %s", member_id));
            String domainurl= SharedPrefereneceUtil.getDomainUrl(CourseDetailsActivity.this);
             CourseDetailsDetails.put("action","show_course_details_by_member_id");
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL,  CourseDetailsDetails);
            Log.v(TAG, String.format("doInBackground :: show_course_details_by_member_id= %s", loginResult));
            return loginResult;
        }


    }

    private void  CourseDetailsDetails(String jsonResponse) {

        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
//        RelativeLayout relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayoutPrabhagDetails);
        if (jsonResponse != null) {


            try {
                Log.v(TAG, "JsonResponseOpeartion :: test");
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    progressBar.setVisibility(View.GONE);
                    if (object != null) {
                        JSONArray jsonArrayResult = object.getJSONArray("result");
//                        if(jsonArrayResult.length() >10){
//                            totalPage=jsonArrayResult.length()/10;
//                        }
                        ArrayList<BalanceTrasactionList> item = new ArrayList<BalanceTrasactionList>();
                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {

                            for (int i = 0; i < jsonArrayResult.length(); i++) {


                                filterArrayList = new CourseList();
                                Log.d(TAG, "i: " + i);

                                Log.v(TAG, "JsonResponseOpeartion ::");
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

                                    //  for (int j = 0; j < 5; j++) {
                                    start_date=Start_Date;
                                    end_date=End_Date;
                                    filterArrayList.setName(name);
                                    String sdate= Utility.formatDate(Start_Date);
                                    String edate= Utility.formatDate(End_Date);
                                    String todate=sdate+" to "+edate;
                                    filterArrayList.setStartToEndDate(todate);
                                    filterArrayList.setContact(Contact);
//                                    String pack=Package_Name;
                                    filterArrayList.setPackageName(Package_Name);
                                    filterArrayList.setExecutiveName(ExecutiveName);
                                    filterArrayList.setTax(Tax);
                                    String dur_sess="Duration:"+Duration_Days+","+"Session:"+Session;
                                    filterArrayList.setPackageNameWithDS(dur_sess);
                                    remSession=Session;
                                    String reg_date= Utility.formatDate(RegistrationDate);
                                    filterArrayList.setRegistrationDate(reg_date);
                                    filterArrayList.setID(Member_ID);
                                    filterArrayList.setInvoiceID(Invoice_ID);

                                    filterArrayList.setRate(Rate);
                                    // String fpaid="₹ "+Final_paid;
                                    filterArrayList.setPaid(Final_paid);
                                    if(Final_Balance.equals(".00")){
                                        Final_Balance="0.00";
                                    }
                                    //String fbalance="₹ "+Final_Balance;
                                    filterArrayList.setBalance(Final_Balance);
                                    Image.replace("\"", "");
                                    filterArrayList.setImage(Image);
                                    filterArrayList.setEmail(Member_Email_ID);
                                    filterArrayList.setFinancialYear(Financial_Year);
//                                    String domainurl= SharedPrefereneceUtil.getDomainUrl(CourseDetailsActivity.this);
//                                    String url= domainurl+ServiceUrls.IMAGES_URL + filterArrayList.getImage();
//
//                                    Glide.with(this).load(url).placeholder(R.drawable.nouser).into(imageView);
                                    contactTV.setText(Contact);
                                    nameTV.setText(name);
                                    String fpaid="₹ "+Final_paid;
                                    String ttl="₹ "+Rate;
                                    rateTV.setText(ttl);
                                    regdateTV.setText(reg_date);
                                    packagenameTV.setText(Package_Name);
                                    durationTv.setText(dur_sess);

//                                    String sdate=Utility.formatDate(Start_Date);
//                                    String edate=Utility.formatDate(End_Date);
//                                    String todate=sdate+" to "+edate;
//                                    start_to_end_dateTV.setText(todate);
                                    paidTV.setText(fpaid);
                                    executiveNameTV.setText(ExecutiveName);
                                    balanceTV.setText(Final_Balance);
                                    invoice_id=Invoice_ID;
                                    invoice_idTV.setText(invoice_id);
                                    member_id=Member_ID;
                                    Tax=Tax;
                                    Email=Member_Email_ID;


                                }
                            }
                        } else if (jsonArrayResult.length() == 0) {
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    //nodata.setVisibility(View.VISIBLE);
                    // recyclerView.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(CourseDetailsActivity.this);
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
    private void folldetailsclass() {
        CourseDetailsActivity.FollowupDetailsTrackclass ru = new CourseDetailsActivity. FollowupDetailsTrackclass();
        ru.execute("5");
    }

    class  FollowupDetailsTrackclass extends AsyncTask<String, Void, String> {


        ServerClass ruc = new ServerClass();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(TAG, "onPreExecute");
            //showProgressDialog();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.v(TAG, String.format("onPostExecute :: response = %s", response));
            //dismissProgressDialog();
            //Toast.makeText(Employee.this, response, Toast.LENGTH_LONG).show();
            FollowupDetailsDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String>  FollowupDetailsDetails = new HashMap<String, String>();
            FollowupDetailsDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(CourseDetailsActivity.this));
           FollowupDetailsDetails.put("member_id",member_id );
           FollowupDetailsDetails.put("foll_type",FollowupType );
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(CourseDetailsActivity.this)));
            Log.v(TAG, String.format("doInBackground :: member_id id = %s", member_id));
            Log.v(TAG, String.format("doInBackground :: foll_type = %s", FollowupType));
            String domainurl= SharedPrefereneceUtil.getDomainUrl(CourseDetailsActivity.this);
           FollowupDetailsDetails.put("action","show_other_followup_details");
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, FollowupDetailsDetails);
            //Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }

    private void FollowupDetailsDetails(String jsonResponse) {

        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
//        RelativeLayout relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayoutPrabhagDetails);
        if (jsonResponse != null) {


            try {
                Log.v(TAG, "JsonResponseOpeartion :: test");
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    progressBar.setVisibility(View.GONE);
                    if (object != null) {
                        JSONArray jsonArrayResult = object.getJSONArray("result");
//                        if(jsonArrayResult.length() >10){
//                            totalPage=jsonArrayResult.length()/10;
//                        }
                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {

                            for (int i = 0; i < jsonArrayResult.length(); i++) {


                                filterArrayList = new CourseList();
                                Log.d(TAG, "i: " + i);

                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {

                                     NextFollowupDate = jsonObj.getString("NextFollowupDate");
                                     NextFollowupDate= Utility.formatDateDB(NextFollowupDate);
                                     String Call_Response = jsonObj.getString("Call_Response");
                                     Rating = jsonObj.getString("Rating");
                                     callResponce=Call_Response;

                                }
                            }
                        } else if (jsonArrayResult.length() == 0) {
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    //nodata.setVisibility(View.VISIBLE);
                    // recyclerView.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(CourseDetailsActivity.this);
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
    private void createHeadings(PdfContentByte cb, float x, float y, String text){

        cb.beginText();
        cb.setFontAndSize(bfBold, 11);
        cb.setTextMatrix(x,y);
        cb.showText(text.trim());
        cb.endText();

    }
    private void createText(PdfContentByte cb, float x, float y, String text){

        cb.beginText();
        cb.setFontAndSize(bfnormal, 8);
        cb.setTextMatrix(x,y);
        cb.showText(text.trim());
        cb.endText();

    }
    private void initializeFonts(){


        try {
            bfBold = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            bfnormal = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

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
    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }
    private boolean checkPermission() {
            int result = ContextCompat.checkSelfPermission(CourseDetailsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (result == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(CourseDetailsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(CourseDetailsActivity.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(CourseDetailsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    private void pdfGenerationdataclass() {
        CourseDetailsActivity.PDFGenerationDataTrackclass ru = new CourseDetailsActivity.PDFGenerationDataTrackclass();
        ru.execute("5");
    }
    class PDFGenerationDataTrackclass extends AsyncTask<String, Void, String> {

        ServerClass ruc = new ServerClass();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(TAG, "onPreExecute");
            //viewDialog.showDialog();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.v(TAG, String.format("onPostExecute :: show_receipt_data = %s", response));
            //viewDialog.hideDialog();
            PDFGenerationDataDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> PDFGenerationDataDetails = new HashMap<String, String>();
            PDFGenerationDataDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(CourseDetailsActivity.this));
            PDFGenerationDataDetails.put("invoice_id", invoice_id);
            Log.v(TAG, String.format("doInBackground :: receipt data invoice_id= %s", invoice_id));
            PDFGenerationDataDetails.put("financial_yr", FinancialYear);
            PDFGenerationDataDetails.put("member_id", member_id);
            Log.v(TAG, String.format("doInBackground :: receipt data company id = %s", FinancialYear));
            Log.v(TAG, String.format("doInBackground :: receipt data company id = %s", SharedPrefereneceUtil.getSelectedBranchId(CourseDetailsActivity.this)));
            PDFGenerationDataDetails.put("action","show_receipt_data");
            String domainurl= SharedPrefereneceUtil.getDomainUrl(CourseDetailsActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, PDFGenerationDataDetails);
            Log.v(TAG, String.format("doInBackground :: Pdf Generation= %s", loginResult));
            return loginResult;
        }
    }

    private void PDFGenerationDataDetails(String jsonResponse) {

        Log.v(TAG, String.format("show_receipt_data :: Pdf Generation = %s", jsonResponse));
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

                                    String Name = nameTV.getText().toString();
                                    String Member_Contact = contactTV.getText().toString();
                                    String Invoice_date = jsonObj.getString("Invoice_date");
                                    String invoice_date= Utility.formatDateDB(Invoice_date);
                                    String Package_Name = jsonObj.getString("Package_Name");
                                    String Duration_Days =jsonObj.getString("Duration_Days");
                                    String Session = jsonObj.getString("Session");
                                    String Start_Date = jsonObj.getString("Start_Date");
                                    String s_date= Utility.formatDateDB(Start_Date);
                                    String End_Date = jsonObj.getString("End_Date");
                                    String edate= Utility.formatDateDB(End_Date);
                                    String Rate = jsonObj.getString("Rate");
                                    String Final_paid = jsonObj.getString("Final_paid");;
                                    String Final_Balance =  jsonObj.getString("Final_Balance");
                                    if(Final_Balance.equals(".00")){
                                        Final_Balance="0.00";
                                    }
                                    String Invoice_ID = invoice_id;

                                    start_date=Start_Date;
                                    end_date=End_Date;
                                    String Member_Email_ID = Email;
                                    String Time =  jsonObj.getString("Time");;
                                    String Instructor_Name = jsonObj.getString("Instructor_Name");;
                                    String Package_Fees =  jsonObj.getString("Package_Fees");;
                                    String Discount =  jsonObj.getString("Discount");;
                                    if(Discount.equals(".00")){
                                        Discount="0.00";
                                    }
                                    String Registration_Fees =  jsonObj.getString("Registration_Fees");;
                                    if(Registration_Fees.equals(".00")){
                                        Registration_Fees="0.00";
                                    }

                                    String Company_Name = SharedPrefereneceUtil.getCompanyName(CourseDetailsActivity.this)+"-"+ SharedPrefereneceUtil.getSelectedBranch(CourseDetailsActivity.this);
                                   String Branch=SharedPrefereneceUtil.getSelectedBranch(CourseDetailsActivity.this);
                                    String Address = jsonObj.getString("Address");
                                    String Contact = jsonObj.getString("Contact");
                                    String MemberGST_No = jsonObj.getString("MemberGST_No");
                                    String GST_No = jsonObj.getString("GST_No");
                                     NextPaymentDate = jsonObj.getString("NextPaymentDate");
                                    String TermsAndConditions = jsonObj.getString("TermsAndConditions");
                                    TermsAndConditions = TermsAndConditions.replace("\r\n", "<br />");
                                     Logo = jsonObj.getString("Logo");
                                    String l=Logo.replaceAll("\\s+","%20");
                                    String domainurl= SharedPrefereneceUtil.getDomainUrl(CourseDetailsActivity.this);
                                    final String imgurl=domainurl+ServiceUrls.IMAGES_URL+l;
                                    Log.d(TAG, "imgurl: " +imgurl);

                                    String textBody = "";
                                    String CGST="";
                                    JSONArray jsonArrayPayTrasa = jsonObj.getJSONArray("payment_transa");
                                    if (jsonArrayPayTrasa != null && jsonArrayPayTrasa.length() > 0) {
                                        for (int loopCount = 0; loopCount < jsonArrayPayTrasa.length(); loopCount++)
                                        {
                                            JSONObject jsonObj1 = jsonArrayPayTrasa.getJSONObject(loopCount);
                                            if (jsonObj1 != null) {
                                                String Receipt_Id = jsonObj1.getString("Receipt_Id");
                                                String ReceiptDate = jsonObj1.getString("ReceiptDate");
                                                String receipt_date= Utility.formatDateDB(ReceiptDate);
                                                String Tax = jsonObj1.getString("Tax");
                                                if(Tax.equals(".00")){
                                                    Tax="0.00";
                                                }
                                                String TaxAmount = jsonObj1.getString("TaxAmount");
                                                if(TaxAmount.equals(".00")){
                                                    TaxAmount="0.00";
                                                }
                                                String Paid =  jsonObj1.getString("Paid");
                                                String PaymentType =  jsonObj1.getString("PaymentType");
                                                String PaymentDetails =  jsonObj1.getString("PaymentDetails");
                                                String ReceiptOwnerExecutive =  jsonObj1.getString("ReceiptOwnerExecutive");
                                                String SubTotal =  jsonObj1.getString("SubTotal");
                                                double cgst =Double.parseDouble(Tax)/2;
                                                 CGST=String.valueOf(cgst);
                                                 double sgst=Double.parseDouble(TaxAmount)/2;
                                                 String SGST=String.valueOf(sgst);

                                                textBody += "  <tr >\n \n" +
                                                        "    <td width='10%'>"+Receipt_Id+"</td>\n \n" +
                                                        "     <td width='15%'>"+receipt_date+"</td>\n\n" +
                                                        "     <td width='12%'>"+SubTotal+"</td>\n\n" +
                                                        "    <td width='8%'>"+Tax+"</td> \n\n" +
                                                        "    <td width='9%'>"+SGST+"</td> \n\n" +
                                                        "    <td width='9%'>"+SGST+"</td> \n\n" +
                                                        "    <td width='12.5%'>"+TaxAmount+"</td>\n\n" +
                                                        "    <td width='12.5%'>"+Paid+"</td>\n\n" +
                                                        "    <td width='12.5%'>"+PaymentType+"</td>\n\n" +
                                                        "    <td width='12.5%'>"+PaymentDetails+"</td>\n\n" +
                                                        "    <td width='13%'>"+ReceiptOwnerExecutive+"</td>\n\n" +
                                                        "    </tr>\n";
                                            }
                                        }

                                    }
//                                    final String messagehtml = "<!DOCTYPE html>\n" +
//                                            "\n" +
//                                            "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
//                                            "<head runat=\"server\">\n" +
//                                            "    <title></title>\n" +
//                                            "</head>\n" +
//                                            "<body>\n" +
//                                            "    <form runat=\"server\">\n" +
//                                            "\n" +
//                                            " <div class=\"row\">\n" +
//                                            "                <div class=\"column\" >\n" +
//                                            "                    <div >\n" +
//                                            "                        <address>\n" +
//                                            "                            <strong style=\"font: 700;\">\n" +
//                                            "                               </strong><br/>" +
//                                            "                          <br/>" +
//                                            "                           <br/>" +
//                                            "                        </address>" +
//                                            "                    </div>" +
//                                            "                    <div >" +
//                                            "                        <address>" +
//                                            "                        " +
////                                            "                            <img src="+ imgurl +">" +
//                                            "                        </address>\n" +
//                                            "                    </div>" +
//                                            "                </div>" +
//                                            "                <div class=\"column\" >\n" +
//                                            "                    <div  >\n" +
//                                            "                        <address>\n" +
//                                            "                            <strong></strong><br/>\n" +
//                                            "                            <strong style=\"font: 900;\">"+"</strong><br/>\n" +
//                                            "                           <br/>\n" +
//                                            "                        </address>\n" +
//                                            "                    </div>" +
//                                            "                    <div >" +
//                                            "                        <address>" +
//                                            "                            <strong>"+"</strong><br/>\n" +
//                                            "\n" +
//                                            "                            <strong> "+"</strong><br/>\n" +
//                                            "                            <strong>  "+"</strong>\n" +
//                                            "                        </address>\n" +
//                                            "                    </div>\n" +
//                                            "                </div>\n" +
//                                            "      </div>\n" +
//                                            "    <div >\n" +
//
//                                            "            <div >\n" +
//                                            "                <div  >\n" +
//                                            "                        <h3 ><strong>Bill To</strong></h3>\n" +
//                                            "                    </div>\n" +
//                                            "                <div >\n" +
//                                            "              <div >\n" +
//                                            "             <table border = '1' cellpadding=\"6\"  width=\"100%\" >\n" +
//                                            "             <thead height=\"100\">\n" +
//                                            "                   <tr height=\"100\" >\n" +
//                                            "                      <th ><strong>ID</strong></th>\n" +
//                                            "                      <th ><strong>Name</strong></th>\n" +
//                                            "                      <th ><strong>Email Id</strong></th>                                    \n" +
//                                            "                      <th ><strong>Contact</strong></th>\n" +
//                                            "                      <th ><strong>GST No</strong></th>\n" +
//                                            "                    </tr>\n" +
//                                            "             </thead>\n" +
//                                            "           <tbody >\n" +
//                                            "  <tr >\n \n" +
//                                            "    <td width='12%'>"+member_id+"</td>\n \n" +
//                                            "     <td width='25%'>"+Name+"</td>\n\n" +
//                                            "    <td width='30%'>"+Email+"</td> \n\n" +
//                                            "    <td width='16%'>"+Member_Contact+"</td>\n\n" +
//                                            "    <td width='17%'>"+MemberGST_No+"</td>\n\n" +
//                                            "    </tr>\n"+
//                                            "          </tbody>\n" +
//                                            "       </table>\n" +
//                                            "       </div>\n" +
//                                            "       </div>\n" +
//                                            "                     \n" +
//                                            "      </div>\n" +
//                                            "     </div>\n" +
//                                            "                           <br/>\n" +
//                                            "\n" +
//                                            "        <div  >\n" +
//                                            "\n" +
//                                            "            <div >\n" +
//                                            "                <div >\n" +
//                                            "                    <div  >\n" +
//                                            "                        <h3 ><strong>Package Summary</strong></h3>\n" +
//                                            "                    </div>\n" +
//                                            "                    <div  >\n" +
//                                            "                        <div  >\n" +
//                                            "   <table border = '1' cellpadding=\"6\"  width=\"100%\" >\n" +
//                                            "                             <thead height=\"100\" >\n" +
//                                            "                                    <tr height=\"100\" >\n" +
//                                            "                                      <th ><strong>Package</strong></th>\n" +
//                                            "                                       <th ><strong>Duration</strong></th>\n" +
//                                            "                                       <th ><strong>Session</strong></th>                                    \n" +
//                                            "                                       <th ><strong>StartDate</strong></th>\n" +
//                                            "                                       <th ><strong>EndDate</strong></th>\n" +
//                                            "                                       <th ><strong>Time</strong></th>\n" +
//                                            "                                       <th ><strong>Instructor</strong></th>\n" +
//                                            "                                       <th ><strong>Package Fees</strong></th>\n" +
//                                            "                                    </tr>\n" +
//                                            "                                </thead>\n" +
//                                            "                               <tbody height=\"100\" >\n" +
//                                            "                                    <tr height=\"100\" >\n" +
//                                            "                                        <td width='12.5%'>"+Package_Name+"</td>\n" +
//                                            "                                         <td width='11.5%'>"+Duration_Days+"</td>\n" +
//                                            "                                        <td width='11%'>"+Session+"</td>                                       \n" +
//                                            "                                        <td width='14%'>"+s_date+"</td>\n" +
//                                            "                                        <td width='14%'>"+edate+"</td>\n" +
//                                            "                                        <td width='8%'>"+Time+"</td>\n" +
//                                            "                                        <td width='13%'>"+Instructor_Name+"</td>\n" +
//                                            "                                        <td width='12%'>"+Package_Fees+"</td>\n" +
//                                            "                                    </tr>\n" +
//                                            "                                </tbody>\n" +
//                                            "                            </table>\n" +
//                                            "                        </div>\n" +
//                                            "                    </div>\n" +
//                                            "                    <div >\n" +
//                                            "                        <div  >\n" +
//                                            "                            <table border = '1' cellpadding=\"6\"  width=\"100%\" >\n" +
//                                            "                                <thead height=\"100\">\n" +
//                                            "                                    <tr height=\"100\" >\n" +
//                                            "                                        <th ><strong>Discount</strong></th>\n" +
//                                            "                                        <th ><strong>Reg Fees</strong></th>\n" +
//                                            "                                        <th ><strong>Total Amount</strong></th>\n" +
//                                            "                                        <th ><strong>Paid Amount</strong></th>\n" +
//                                            "                                        <th ><strong>Balance</strong></th>\n" +
//                                            "                                    </tr>\n" +
//                                            "                                </thead>\n" +
//                                            "                                <tbody >\n" +
//                                            "                                    <tr>\n" +
//                                            "                                        <td >"+Discount+"</td>\n" +
//                                            "                                        <td >"+Registration_Fees+"</td>\n" +
//                                            "                                       \n" +
//                                            "                                        <td >"+Rate+"</td>\n" +
//                                            "                                        <td >"+Final_paid+"</td>\n" +
//                                            "                                        <td >"+Final_Balance+"</td>\n" +
//                                            "                                    </tr>\n" +
//                                            "                                </tbody>\n" +
//                                            "                            </table>\n" +
//                                            "                        </div>\n" +
//                                            "                    </div>\n" +
//                                            "\n" +
//                                            "                </div>\n" +
//                                            "            </div>\n" +
//                                            "        </div>\n" +
//                                            "\n" +  "                           <br/>\n" +
//                                            "       \n" +
//                                            " <div  >\n" +
//                                            "\n" +
//                                            "    <div >\n" +
//
//                                            "            <div >\n" +
//                                            "                <div  >\n" +
//                                            "                        <h3 ><strong>Payment Transaction</strong></h3>\n" +
//                                            "                    </div>\n" +
//                                            "                <div >\n" +
//                                            "              <div >\n" +
//                                            "             <table border = '1' cellpadding=\"6\"  width=\"100%\" >\n" +
//                                            "             <thead height=\"100\">\n" +
//                                            "                   <tr height=\"100\" >\n" +
//                                            "                      <th ><strong>#RNo</strong></th>\n" +
//                                            "                      <th ><strong>Date</strong></th>\n" +
//                                            "                      <th ><strong>Tax</strong></th>                                    \n" +
//                                            "                      <th ><strong>Tax Amount</strong></th>\n" +
//                                            "                      <th ><strong>Paid Amount</strong></th>\n" +
//                                            "                       <th ><strong>Payment Mode</strong></th>\n" +
//                                            "                       <th ><strong>Payment Details</strong></th>\n" +
//                                            "                       <th ><strong>Executive</strong></th>\n" +
//                                            "                    </tr>\n" +
//                                            "             </thead>\n" +
//                                            "           <tbody >\n" +
//                                            textBody+
//                                            "          </tbody>\n" +
//                                            "       </table>\n" +
//                                            "       </div>\n" +
//                                            "       </div>\n" +
//                                            "                     \n" +
//                                            "      </div>\n" +
//                                            "     </div>\n" +
//                                            "  </div>\n" +
//                                            "\n" +  "                           <br/>\n" +
//                                            "      \n" +
//                                            "\n" +
//                                            "  <div  >\n" +
//                                            "    <div >\n" +
//                                            "       <div  >\n" +
//                                            "          <h3 ><strong>Terms And Conditions</strong></h3>\n" +
//                                            "       </div>\n" +
//                                            "                  \n" +
//                                            "                           "+TermsAndConditions+"\n" +
//                                            "                     \n" +
//                                            "    \n" +
//                                            "                \n" +
//                                            "            </div>\n" +
//                                            "        </div>\n" +
//                                            "\n" +
//                                            "        </form>\n" +
//                                            "</body>\n" +
//                                            "</html>";
                                    final Document documentpdf = new Document(PageSize.A4);

                                    try {

                                        OutputStream file = new FileOutputStream(pdfFile);
                                        PdfWriter docWriter = PdfWriter.getInstance(documentpdf, file);
                                        documentpdf.open();
//
//                                        PdfContentByte cb = docWriter.getDirectContent();
//                                        //initialize fonts for text printing
//                                        initializeFonts();
//                                        //Add Image from some URL
                                        Thread thread = new Thread(new Runnable() {
                                            //
                                            @Override
                                            public void run() {
                                                try  {
                                                    Image image = Image.getInstance(new URL(imgurl));
                                                    image.setAbsolutePosition(50,730);
                                                    image.scalePercent(10);
                                                    image.scaleToFit(100, 70);
                                                    documentpdf.add(image);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                        thread.start();
//                                        createHeadings(cb,50,780,Company_Name);
                                        String delimiter = " ";
                                        int partitionSize = 6;
                                        String add="";
                                        int x=50;
                                        int y=765;
                                        for (Iterable<String> iterable : Iterables.partition(Splitter.on(delimiter).split(Address), partitionSize)) {
                                            System.out.println(Joiner.on(delimiter).join(iterable));
                                            add+=Joiner.on(delimiter).join(iterable)+"<br/>";

                                           // createText(cb,x,y,Joiner.on(delimiter).join(iterable));
                                            y= y-10;
                                        }

                                        String csstext=" .text-right { text-align: right;} .p{border: 1px solid black;font-family: Helvetica,Arial,sans-serif;font-size: 12px;}" +
                                                "        .addr {" +
                                                "            margin-bottom: 10px;" +
                                                "            font-style: normal;" +
                                                "            margin-left: 20px;font-size: 12px;" +
                                                "            line-height: 1.428571429;" +
                                                "        }" +
                                                "        .table > thead > tr > td {" +
                                                "            padding: 1px;" +
                                                "            line-height: 1.428571429;" +
                                                "            vertical-align: top;" +
                                                "            border-top: 1px solid #ddd;" +
                                                "            margin-left: 10px;" +
                                                "font-family: Helvetica,Arial,sans-serif;font-size: 12px;" +
                                                "        }\n" +
                                                "        .column {\n" +
                                                "          float: left;\n" +
                                                "          width: 30.33%;\n" +
                                                "          padding: 10px;" +
                                                "        }\n" +
                                                "        .col-3{\n" +
                                                "            float: left;\n" +
                                                "          width: 20%;\n" +
                                                "          padding: 10px;\n" +
                                                "        }\n" +
                                                "        .col-6{\n" +
                                                "            float: left;\n" +
                                                "          width: 70%;\n" +
                                                "        }\n" +
                                                "         .col-4{\n" +
                                                "            float: left;\n" +
                                                "          width: 10%;\n" +
                                                "text-align: right;" +
                                                "        }\n" +
                                                "        .id{\n" +
                                                "            float: left;\n" +
                                                "          width: 50%;\n" +
                                                "          margin-left: 5px;\n" +
                                                "        }\n" +
                                                "        .email{\n" +
                                                "            float: left;\n" +
                                                "          width: 49%;\n" +
                                                "        }" +
                                                "        .row:after {\n" +
                                                "          content: \"\";\n" +
                                                "          display: table;\n" +
                                                "          clear: both;\n" +
                                                "border-bottom: 1px solid black;margin-top: 13px;" +
                                                "        }\n" +
                                                "        .col-md-3{\n" +
                                                "             float: left;\n" +
                                                "           width: 28%;\n" +
                                                "          height: 100%;\n" +
                                                "    overflow: hidden;\n" +
                                                "        } .col-md-4{" +
                                                "    float: left; width: 20%;height: 100%; overflow: hidden;}\n" +
                                                "        .table > thead > tr > td {\n" +
                                                "            line-height: 1.428571429;\n" +
                                                "            vertical-align: top;\n" +
                                                "            border: 1px solid #ddd;\n" +
                                                "            margin-left: 0px;\n" +
                                                "             table-layout: fixed;\n" +
                                                "           }\n" +
                                                "           .table-bordered {\n" +
                                                "            empty-cells: show;\n" +
                                                "        }\n" +
                                                "        table {\n" +
                                                "        border-spacing: 0;\n" +
                                                "         empty-cells: show;\n" +
                                                "         }\n" +
                                                "        table td[class*=col-], table th[class*=col-] {\n" +
                                                "            position: static;\n" +
                                                "            float: none;\n" +
                                                "            display: table-cell;\n" +
                                                "        }\n" +
                                                "        .container {\n" +
                                                "          display: flex;\n" +
                                                "          flex-wrap: wrap;\n" +
                                                "        }\n" +
                                                "        thead {\n" +
                                                "    display: table-header-group;\n" +
                                                "    vertical-align: middle;\n" +
                                                "    border-color: inherit;\n" +
                                                "}" +
                                                " td{font-family: Helvetica,Arial,sans-serif;font-size: 12px; }";

                                        String HTML = "<html><body style=\"font-family: Helvetica,Arial,sans-serif;color: #333\">\n" +
                                                "        <div class=\"container\" >\n" +
                                                "            <div class=\"row\">\n" +
                                                "                    <div class=\"row\" style=\"border-bottom: 1px solid black;margin-top: 13px;\">\n" +
                                                "                        <div class=\"col-3\"> \n" +
                                                "                                <img src=\"http://demo.gymtime.in/CompanyLogo/NavkarDemo_2020125141355.png\" style=\"width: 120px; height: 100px;\" />                                                                                   \n" +
                                                "                        </div>\n" +
                                                "                        <div class=\"col-6\" style=\"margin-left: 0px; padding :5px;\">\n" +
                                                "                            <div class=\"addr\">\n" +
                                                "                                 <table cellpadding=\"4\">\n" +
                                                "                                <thead>\n" +
                                                "                                    <tr>\n" +
                                                "                                        <td ><strong>"+Company_Name+"</strong></td>\n" +
                                                "                                    </tr>\n" +
                                                "                                    <tr>\n" +
                                                "                                        <td  >"+add+"</td>\n" +
                                                "                                    </tr>\n" +
                                                "                                    <tr>\n" +
                                                "                                        <td  >"+Branch+"</td>\n" +
                                                "                                    </tr>\n" +
                                                "                                    <tr>\n" +
                                                "                                        <td  >"+Contact+"</td>\n" +
                                                "                                    </tr>\n" +
                                                "                                    <tr>\n" +
                                                "                                        <td  >"+GST_No+"</td>\n" +
                                                "                                    </tr>\n" +
                                                "                                </thead>\n" +
                                                "                            </table>\n" +
                                                "                               \n" +
                                                "                            </div>\n" +
                                                "                        </div>\n" +
                                                "                        <div class=\"col-4\" style=\"text-align: right;\">\n" +
                                                "                               <table cellpadding=\"4\">\n" +
                                                "                                <thead>\n" +
                                                "                                    <tr>\n" +
                                                "                                        <td  ><strong>Invoice No : </strong>"+invoice_id+"</td>\n" +
                                                "                                    </tr>\n" +
                                                "                                    <tr>\n" +
                                                "                                        <td  ><strong>Invoice Date :</strong>"+invoice_date+"</td>\n" +
                                                "                                    </tr>\n" +
                                                "                                   \n" +
                                                "                                </thead>\n" +
                                                "                            </table>\n" +
                                                "                        </div>\n" +
                                                "                    </div> " +
                                                "  <hr/>  " +
                                                "<div class=\"row\" style=\"margin-top: 9px; border-bottom: 1px solid black\">\n" +
                                                "                        <div class=\"id\" style=\"text-align: inherit\">\n" +
                                                "                            <table cellpadding=\"4\">\n" +
                                                "                                <thead>\n" +
                                                "                                    <tr>\n" +
                                                "                                        <td  ><strong>ID:</strong></td>\n" +
                                                "                                        <td > "+member_id+"</td>\n" +
                                                "                                       \n" +
                                                "                                    </tr>\n" +
                                                "                                    <tr>\n" +
                                                "                                        <td  ><strong>Name:</strong></td>\n" +
                                                "                                        <td  >"+Name+"</td>\n" +
                                                "                                        \n" +
                                                "                                    </tr>\n" +
                                                "                                    <tr>\n" +
                                                "                                        <td  ><strong>Contact:</strong></td>\n" +
                                                "                                        <td  >"+Member_Contact+"</td>\n" +
                                                "                                    </tr>\n" +
                                                "                                </thead>\n" +
                                                "                            </table>\n" +
                                                "                        </div>\n" +
                                                "                         <div class=\"email\" style=\"margin-left: 0px;\">\n" +
                                                "                              <table cellpadding=\"4\">\n" +
                                                "                                <thead>\n" +
                                                "                                    <tr>\n" +
                                                "                                         <td ><strong>Email:</strong></td>\n" +
                                                "                                        <td >"+Email+"</td>\n" +
                                                "                                     </tr>\n" +
                                                "                                     <tr>\n" +
                                                "                                          <td class=\"text-right\" ><strong>GST No:</strong></td>\n" +
                                                "                                        <td >"+MemberGST_No+"</td>\n" +
                                                "                                     </tr>\n" +
                                                "                                </thead>\n" +
                                                "                            </table>\n" +
                                                "                         </div>\n" +
                                                "                    </div>" +
                                                " <hr/>" +
                                                "<div class=\"row\">                                                                                       \n" +
                                                "                        <div  style=\"text-align: inherit;\">\n" +
                                                "                            <div class=\"container\" style=\"width:100%; overflow: hidden;padding: 0px 5px 5px 5px;\">\n" +
                                                "                                <table cellpadding=\"1\" class=\"table table-bordered\" style=\"margin-bottom: 0px;width:100%;padding: 5px;\">\n" +
                                                "                                    <thead >\n" +
                                                "                                        <tr  style=\"width:100%;height:10px;border: thick; border-color: black;\">\n" +
                                                "                                            <td class=\"col-md-3\"><strong>Package :</strong></td>\n" +
                                                "                                            <td  class=\"col-md-3\">"+Package_Name+"</td>\n" +
                                                "                                            <td  class=\"col-md-4\"><strong>Amount</strong></td>\n" +
                                                "                                            <td  class=\"col-md-4\" style=\"text-align: right; \">"+Package_Fees+"</td>\n" +
                                                "                                        </tr>\n" +
                                                "                                        <tr style=\"width:100%;height:10px;border: thick; border-color: black;\">\n" +
                                                "                                            <td class=\"col-md-3\"><strong>Duration/Sessions :</strong></td>\n" +
                                                "                                             <td class=\"col-md-3\">"+Duration_Days+"/"+Session+"</td>\n" +
                                                "                                            <td class=\"col-md-4\"><strong>Discount</strong></td>\n" +
                                                "                                            <td class=\"col-md-4\" style=\"text-align: right; \">"+Discount+"</td>\n" +
                                                "                                        </tr>\n" +
                                                "                                        <tr style=\"width:100%;height:10px;border: thick; border-color: black;\">\n" +
                                                "\n" +
                                                "                                            <td class=\"col-md-3\"><strong>Date :</strong></td>\n" +
                                                "                                            <td class=\"col-md-3\">"+s_date+" to "+edate+"</td>\n" +
                                                "                                            <td class=\"col-md-4\"><strong>Reg Fees</strong></td>\n" +
                                                "                                            <td class=\"col-md-4\" style=\"text-align: right; \">"+Registration_Fees+"</td>\n" +
                                                "                                        </tr>\n" +
                                                "                                        <tr style=\"width:100%;height:10px;border: thick; border-color: black;\">\n" +
                                                "\n" +
                                                "                                            <td class=\"col-md-3\"><strong>Time  :</strong></td>\n" +
                                                "                                            <td class=\"col-md-3\">"+Time+"</td>\n" +
                                                "                                            <td class=\"col-md-4\"><strong>Total Amount</strong></td>\n" +
                                                "                                            <td class=\"col-md-4\" style=\"text-align: right; \">"+Rate+"</td>\n" +
                                                "                                        </tr>\n" +
                                                "                                        <tr style=\"width:100%;height:10px;border: thick; border-color: black;\">\n" +
                                                "\n" +
                                                "                                            <td class=\"col-md-3\"><strong>Instructor:</strong></td>\n" +
                                                "                                             <td class=\"col-md-3\">"+Instructor_Name+"</td>\n" +
                                                "                                            <td class=\"col-md-4\"><strong>Paid Amount</strong></td>\n" +
                                                "                                            <td class=\"col-md-4\" style=\"text-align: right; \">"+Final_paid+"</td>\n" +
                                                "                                        </tr>\n" +
                                                "                                        <tr style=\"width:100%;height:10px;border: thick; border-color: black;\">\n" +
                                                "                                            <td class=\"col-md-3\"><strong>&nbsp;  </strong></td>                          \n" +
                                                "                                            <td class=\"col-md-3\"><strong>&nbsp; </strong></td>\n" +
                                                "                                            <td class=\"col-md-4\"><strong>Balance</strong></td>\n" +
                                                "                                            <td class=\"col-md-4\" style=\"text-align: right; \">"+Final_Balance+"</td>\n" +
                                                "                                        </tr>                                                                             \n" +
                                                "                                    </thead>\n" +
                                                "                                </table>\n" +
                                                "                            </div>\n" +
                                                "                        </div>\n" +
                                                "                    </div>" +
                                                " <div class=\"row\">\n" +
                                                "                                <div style=\"padding-left: 5px;\">\n" +
                                                "                                    <h3 ><strong>Payment Transaction:</strong></h3>\n" +
                                                "                                </div><br/>\n" +
                                                "                                <div  style=\"width:99%; overflow: hidden;padding: 0px 5px 5px 5px;\">\n" +
                                                "                                      <font size=\"2\" face=\"Courier New\" >  <table cellpadding=\"1\" class=\"table table-bordered\" style=\"margin-bottom: 0px;width:100%;\" >\n" +
                                                "                                            <thead>\n" +
                                                "                                                <tr style=\"width:100%;height:10px;border: thick; border-color: black;\">\n" +
                                                "                                                    <td style=\"padding: 1px;\"><strong>#RNo</strong></td>\n" +
                                                "                                                    <td style=\"padding: 1px;\"><strong>Pay Date</strong></td>\n" +
                                                "                                                    <td style=\"padding: 1px;\"><strong>Subtotal</strong></td>\n" +
                                                "                                                    <td style=\"padding: 1px;\"><strong>Tax</strong></td>\n" +
                                                "                                                    <td style=\"padding: 1px;\"><strong>CGST ("+CGST+"%)</strong></td>\n" +
                                                "                                                    <td style=\"padding: 1px;\"><strong>SGST ("+CGST+"%)</strong></td>\n" +
                                                "                                                    <td style=\"padding: 1px;\"><strong>Tax Amount</strong></td>\n" +
                                                "                                                    <td style=\"padding: 1px;\"><strong>Paid Amount</strong></td>\n" +
                                                "                                                    <td style=\"padding: 1px;\"><strong>Payment Mode</strong></td>\n" +
                                                "                                                    <td style=\"padding: 1px;\"><strong>Payment Details</strong></td>\n" +
                                                "                                                    <td style=\"padding: 1px;\"><strong>Executive</strong></td>\n" +
                                                "                                                </tr>\n" +
                                                textBody+

                                                "                                            </thead>\n" +
                                                "                                        </table></font>\n" +
                                                "                                    </div>\n" +
                                                "                        </div>           \n" +
                                                "                    <div class=\"row\">\n" +
                                                "                                <div style=\"margin-top: 4px; margin-bottom: 4px;padding: 5px;\">\n" +
                                                "                                    <h3  style=\"margin-bottom: 6px\"><strong>Terms And Conditions : </strong></h3>\n" +
                                                "                                    <div>                                          \n" +
                                                "                                        <div  ><table cellpadding=\"4\" style=\"width:100%\">\n" +
                                                "  <tr><td class=\"p\">"+TermsAndConditions+"</td> </tr>\n" +
                                                "</table>\n" +
                                                "                                        </div>\n" +
                                                "                                    </div>\n" +
                                                "                                </div>\n" +
                                                "                    </div>" +
                                                "                </div>\n" +
                                                "            </div>\n" +
                                                "</body></html>";
//                                        CSSResolver cssResolver = new StyleAttrCSSResolver();
//                                        CssFile cssFile = XMLWorkerHelper.getCSS(new ByteArrayInputStream(csstext.getBytes()));
//                                        cssResolver.addCss(cssFile);
//
////                                        CSSResolver cssResolver =
////                                                XMLWorkerHelper.getInstance().getDefaultCssResolver(false);
////                                        FileRetrieve retrieve = new FileRetrieveImpl(CSS_DIR);
////                                        cssResolver.setFileRetrieve(retrieve);
//
//                                        HtmlPipelineContext htmlContext = new HtmlPipelineContext(null);
//                                        htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());
//                                        htmlContext.setImageProvider(new AbstractImageProvider() {
//                                            public String getImageRootPath() {
//                                                return imgurl;
//                                            }
//                                        });
//                                        htmlContext.setLinkProvider(new LinkProvider() {
//                                            public String getLinkRoot() {
//                                                return imgurl;
//                                            }
//                                        });
//
//                                        PdfWriterPipeline pdf = new PdfWriterPipeline(documentpdf, docWriter);
//                                        HtmlPipeline html = new HtmlPipeline(htmlContext, pdf);
//                                        CssResolverPipeline css = new CssResolverPipeline(cssResolver, html);
//
//                                        XMLWorker worker = new XMLWorker(css, true);
//                                        XMLParser p = new XMLParser(worker);
//                                        p.parse(new ByteArrayInputStream(HTML.getBytes()));
//                                      p.parse(new FileInputStream(HTML));

                                        InputStream is = new ByteArrayInputStream(HTML.getBytes());
                                        InputStream cssggh = new ByteArrayInputStream(csstext.getBytes());
                                        XMLWorkerHelper.getInstance().parseXHtml(docWriter, documentpdf, is, cssggh,Charset.forName("UTF-8"));

//                                        HTMLWorker htmlWorker = new HTMLWorker(documentpdf);
//                                        htmlWorker.parse(new StringReader(messagehtml));
                                        documentpdf.close();
                                        file.close();


                                    }
                                    catch(Exception e){
                                        e.printStackTrace();
                                    }

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
}
