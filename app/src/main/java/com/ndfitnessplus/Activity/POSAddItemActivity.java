package com.ndfitnessplus.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.ndfitnessplus.Fragment.AddItemFragment;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.ServerClass;
import com.ndfitnessplus.Utility.ServiceUrls;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;
import com.ndfitnessplus.Utility.ViewDialog;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class POSAddItemActivity extends AppCompatActivity {
    private EditText inputProdName,inputProdCode, inputProdDisc, inputQuantity,
            inputTax,inputRate,inputPurchaseAmount,inputMaxDiscount;
    private TextInputLayout inputLayoutProdName,inputLayoutProdCode, inputLayoutProdDisc,inputLayoutQuantity,
            inputLayoutTax,inputLayoutRate,inputLayoutPurchaseAmount,inputLayoutMaxDiscount;

    FloatingActionButton imageView;
    public final String TAG = POSAddItemActivity.class.getName();
    private ProgressDialog pd;
    private AwesomeValidation awesomeValidation;

    Bitmap arrey[];

    //Camera
    String imgPath, fileName;
    public  static final int RequestPermissionCode  = 1 ;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    ;
    // Bitmap bitmap;
    String ConvertImage="";
    ImageView CapturedImage;
    private Uri uri;
    private Uri filePath;
    Bitmap bitmap;
    String afterEnquirySms;
    //Loading gif
    ViewDialog viewDialog;
    private Uri mCropImageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_posadd_item);
        initToolbar();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.pos_add_item));
        // getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponent();
    }
    private void initComponent(){
        inputLayoutProdCode = (TextInputLayout) findViewById(R.id.input_layout_prod_code);
        inputLayoutProdName = (TextInputLayout) findViewById(R.id.input_layout_prod_name);
        inputLayoutProdDisc = (TextInputLayout) findViewById(R.id.input_layout_prod_disc);
        inputLayoutQuantity = (TextInputLayout) findViewById(R.id.input_layout_quantity);
        inputLayoutTax = (TextInputLayout) findViewById(R.id.input_layout_tax);
        inputLayoutRate = (TextInputLayout) findViewById(R.id.input_layout_rate);
        inputLayoutPurchaseAmount = (TextInputLayout) findViewById(R.id.input_layout_pur_amt);
        inputLayoutMaxDiscount = (TextInputLayout) findViewById(R.id.input_layout_max_discount);


        viewDialog = new ViewDialog(POSAddItemActivity.this);

        imageView=findViewById(R.id.input_image);
        CapturedImage=findViewById(R.id.captured_image);

        inputProdCode = (EditText) findViewById(R.id.input_prod_code);
        inputProdName = (EditText) findViewById(R.id.input_prod_name);
        inputProdDisc = (EditText) findViewById(R.id.input_prod_disc);
        inputQuantity = (EditText) findViewById(R.id.input_quantity);
        inputTax = (EditText) findViewById(R.id.input_tax);
        inputRate = (EditText) findViewById(R.id.input_rate);
        inputPurchaseAmount = (EditText) findViewById(R.id.input_pur_amt);
        inputMaxDiscount=(EditText)findViewById(R.id.input_max_discount);


        //defining AwesomeValidation object
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        awesomeValidation.addValidation(POSAddItemActivity.this, R.id.input_prod_code, RegexTemplate.NOT_EMPTY, R.string.err_msg_prod_code);
        awesomeValidation.addValidation(POSAddItemActivity.this, R.id.input_prod_name, RegexTemplate.NOT_EMPTY, R.string.err_msg_prod_name);
        awesomeValidation.addValidation(POSAddItemActivity.this, R.id.input_quantity, RegexTemplate.NOT_EMPTY, R.string.err_msg_qty);
        awesomeValidation.addValidation(POSAddItemActivity.this, R.id.input_rate, RegexTemplate.NOT_EMPTY, R.string.err_msg_rate);
        awesomeValidation.addValidation(POSAddItemActivity.this, R.id.input_pur_amt, RegexTemplate.NOT_EMPTY, R.string.err_msg_pur_amt);
        awesomeValidation.addValidation(POSAddItemActivity.this, R.id.input_max_discount, RegexTemplate.NOT_EMPTY, R.string.err_msg_max_discount);
//        save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                submitForm();
//            }
//        });
        inputProdCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(inputProdCode.getText().length()>0){
                    CheckProdCodeClass();
                }else{
                    inputProdCode.setText("");

                }


            }
        });
        inputProdName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(inputProdName.getText().length()>0){
                    CheckProdNameClass();
                }else{
                    inputProdName.setText("");

                }


            }
        });

        inputPurchaseAmount.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }



            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void afterTextChanged(Editable s) {
                if(inputPurchaseAmount.getText().length()>0) {
                    double rate=0;
                    if(inputRate.getText().length()>0){
                        rate=Double.parseDouble(inputRate.getText().toString());
                    }

                    double purchaseamt=Double.parseDouble(inputPurchaseAmount.getText().toString());

                    if(purchaseamt>rate){
                        Toast.makeText(POSAddItemActivity.this,"Purchase amount shound not be greater than rate!",Toast
                                .LENGTH_SHORT).show();
                        awesomeValidation.clear();
                        inputPurchaseAmount.getText().clear();
                    }
                }

            }
        });

        EnableRuntimePermissionToAccessCamera();
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
//            }
//        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.startPickImageActivity(POSAddItemActivity.this);
            }
        });
    }
    //************ Submit button on action bar ***********
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_enquiry_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save_enquiry) {

            submitForm();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // *********** camera permission ***********
    public void EnableRuntimePermissionToAccessCamera(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(POSAddItemActivity.this,
                Manifest.permission.CAMERA))
        {

            // Printing toast message after enabling runtime permission.
            //Toast.makeText(AddEnquiryActivity.this,"CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(POSAddItemActivity.this,new String[]{Manifest.permission.CAMERA}, RequestPermissionCode);

        }
    }
    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // handle result of pick image chooser
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                mCropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            } else {
                // no permissions required or already grunted, can start crop image activity
                startCropImageActivity(imageUri);
            }
        }

        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                CapturedImage.setVisibility(View.VISIBLE);
                // ((ImageButton) findViewById(R.id.quick_start_cropped_image)).setImageURI(result.getUri());
                CapturedImage.setImageURI(result.getUri());
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), result.getUri());
                    uploadimageClass();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //  Toast.makeText(this, "Cropping successful, Sample: " + result.getSampleSize(), Toast.LENGTH_LONG).show();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // required permissions granted, start crop image activity
            startCropImageActivity(mCropImageUri);
        } else {
            //  Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Start crop image activity for the given image.
     */
    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .setAspectRatio(1,1)
                .setFixAspectRatio(true)
                .start(this);
    }
//    @Override
//    public void onActivityResult(int RC, int RQC, Intent I) {
//
//        super.onActivityResult(RC, RQC, I);
//        if (RC == REQUEST_IMAGE_CAPTURE ) {
//
//
////            uri = I.getData();
////            Log.v(TAG, String.format("camera request "));
////            Log.v(TAG, String.format("camera capture :: uri= %s", uri));
////            try {
//            if(I !=null){
//                Bundle extras = I.getExtras();
//                if (extras != null) {
//                    Bitmap bitmap1 = (Bitmap) extras.get("data");
//                    CapturedImage.setImageBitmap(bitmap1);
//                    bitmap = Bitmap.createScaledBitmap(bitmap1, 75,
//                            75, true);
//                    //return newBitmap;
//
//                    CapturedImage.setVisibility(View.VISIBLE);
////                String timeStamp =
////                        new SimpleDateFormat("yyyyMMdd_HHmmss",
////                                Locale.getDefault()).format(new Date());
////                String imageFileName = "IMG_" + timeStamp;
//
//                    // bitmap=Utility.resizeAndCompressImageBeforeSend(POSAddItemActivity.this,bitmap1,imageFileName);
//                    Log.v(TAG, String.format(" Bitmap= %s", bitmap));
//                    uploadimageClass();
//                }
//            }
////
//        }
//    }
    //*************** Add enquiry to database *************
    public void  AddProductClass() {
        POSAddItemActivity.AddProductTrackClass ru = new POSAddItemActivity.AddProductTrackClass();
        ru.execute("5");
    }



    class AddProductTrackClass extends AsyncTask<String, Void, String> {

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
            AddProductDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> AddProductDetails = new HashMap<String, String>();
            AddProductDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(POSAddItemActivity.this));
            AddProductDetails.put("prod_code",inputProdCode.getText().toString());
            AddProductDetails.put("prod_name",inputProdName.getText().toString());
            AddProductDetails.put("prod_disc",inputProdDisc.getText().toString());
            AddProductDetails.put("quantity",inputQuantity.getText().toString());
            AddProductDetails.put("tax",inputTax.getText().toString());
            AddProductDetails.put("rate", inputRate.getText().toString());
            AddProductDetails.put("purchase_amount",inputPurchaseAmount.getText().toString());
            AddProductDetails.put("max_discount",inputMaxDiscount.getText().toString());
            AddProductDetails.put("prod_image",ConvertImage);
            AddProductDetails.put("exe_name",SharedPrefereneceUtil.getName(POSAddItemActivity.this));
            Log.v(TAG, String.format("doInBackground :: executive name= %s", SharedPrefereneceUtil.getName(POSAddItemActivity.this)));
            AddProductDetails.put("action", "add_pos_items");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(POSAddItemActivity.this);
            String loginResult2 = ruc.sendPostRequest(domainurl+ ServiceUrls.LOGIN_URL, AddProductDetails);

            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult2));
            return loginResult2;
        }
    }


    private void AddProductDetails(String jsonResponse) {

        Log.v(TAG, String.format("loginServerResponse :: response = %s", jsonResponse));

        JSONObject jsonObjLoginResponse = null;
        try {
            jsonObjLoginResponse = new JSONObject(jsonResponse);
            String success = jsonObjLoginResponse.getString(getResources().getString(R.string.success));

            if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                Toast.makeText(POSAddItemActivity.this,"Product added succesfully",Toast.LENGTH_SHORT).show();
                inputProdCode.getText().clear();
                inputProdName.getText().clear();
                inputProdDisc.getText().clear();
                inputQuantity.getText().clear();
                inputTax.getText().clear();
                inputRate.getText().clear();
                inputPurchaseAmount.getText().clear();
                inputMaxDiscount.getText().clear();
                CapturedImage.setVisibility(View.GONE);
               Intent intent=new Intent(POSAddItemActivity.this,POSShowItemActivity.class);
               startActivity(intent);
            }


            else if (success.equalsIgnoreCase(getResources().getString(R.string.one)))
            {
                Toast.makeText(POSAddItemActivity.this,"Product Already Added",Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    // *********** upload image to server ************
    private void uploadimageClass() {

        ByteArrayOutputStream byteArrayOutputStreamObject ;

        byteArrayOutputStreamObject = new ByteArrayOutputStream();
        if(bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreamObject);
        }
        byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();

        ConvertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);
        Log.v(TAG, String.format(" ConvertImage= %s", ConvertImage));

    }

    private void submitForm() {
        if (awesomeValidation.validate()) {
            if (!(inputQuantity.getText().toString().equals("0") )) {

                AddProductClass();

            } else {
                Toast.makeText(POSAddItemActivity.this, "Quantity  should be greater than zero", Toast.LENGTH_SHORT).show();
            }

        }


    }
    //  ********************** Check Email Already Exist or Not *********************
    public void  CheckProdNameClass() {
        POSAddItemActivity.CheckProdNameTrackClass ru = new POSAddItemActivity.CheckProdNameTrackClass();
        ru.execute("5");
    }

    class CheckProdNameTrackClass extends AsyncTask<String, Void, String> {

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
            CheckProdNameDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
          // Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> EnquiryForDetails = new HashMap<String, String>();

            EnquiryForDetails.put("prod_name",inputProdName.getText().toString() );
            EnquiryForDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(POSAddItemActivity.this) );
            EnquiryForDetails.put("action", "check_prod_name_exist");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(POSAddItemActivity.this);
            //EnquiryForloyeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(EnquiryForloyee.this));
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, EnquiryForDetails);
            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }
    }
    private void CheckProdNameDetails(String jsonResponse) {

        Log.v(TAG, String.format("loginServerResponse :: response = %s", jsonResponse));

        JSONObject jsonObjLoginResponse = null;
        try {
            jsonObjLoginResponse = new JSONObject(jsonResponse);
            String success = jsonObjLoginResponse.getString(getResources().getString(R.string.success));

            if (success.equalsIgnoreCase(getResources().getString(R.string.zero))) {

                // showCustomDialog();

                //inputEmail, inputPhone,inputAdd,inputReq,inputFollowupdate;
            }
            else if (success.equalsIgnoreCase(getResources().getString(R.string.two)))
            {
                Toast.makeText(POSAddItemActivity.this,"Product Name Already Exits. Please Enter New Product Name",Toast.LENGTH_SHORT).show();
                inputProdName.getText().clear();
                //Toast.makeText(AddMemberActivity.this,"Please Enter New Mobile Number",Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //  ********************** Check Email Already Exist or Not *********************
    public void  CheckProdCodeClass() {
        POSAddItemActivity.CheckProdCodeTrackClass ru = new POSAddItemActivity.CheckProdCodeTrackClass();
        ru.execute("5");
    }

    class CheckProdCodeTrackClass extends AsyncTask<String, Void, String> {

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
            //dismissProgressDialog();
         //   viewDialog.hideDialog();
            //Toast.makeText(CandiateListView.this, response, Toast.LENGTH_LONG).show();
            //  Toast.makeText(NewCustomerActivity.this, response, Toast.LENGTH_LONG).show();
            CheckProdCodeDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
          //  Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> EnquiryForDetails = new HashMap<String, String>();

            EnquiryForDetails.put("prod_code",inputProdCode.getText().toString());
            EnquiryForDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(POSAddItemActivity.this) );
            EnquiryForDetails.put("action", "check_prod_code_exist");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(POSAddItemActivity.this);
            //EnquiryForloyeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(EnquiryForloyee.this));
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, EnquiryForDetails);
            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }
    }
    private void CheckProdCodeDetails(String jsonResponse) {

        Log.v(TAG, String.format("loginServerResponse :: response = %s", jsonResponse));

        JSONObject jsonObjLoginResponse = null;
        try {
            jsonObjLoginResponse = new JSONObject(jsonResponse);
            String success = jsonObjLoginResponse.getString(getResources().getString(R.string.success));

            if (success.equalsIgnoreCase(getResources().getString(R.string.zero))) {

                // showCustomDialog();

                //inputProdCode, inputPhone,inputAdd,inputReq,inputFollowupdate;
            }
            else if (success.equalsIgnoreCase(getResources().getString(R.string.two)))
            {
                Toast.makeText(POSAddItemActivity.this,"Product Code Already Exits. Please Enter New Product Code",Toast.LENGTH_SHORT).show();
                inputProdCode.getText().clear();
                //Toast.makeText(AddMemberActivity.this,"Please Enter New Mobile Number",Toast.LENGTH_SHORT).show();
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
