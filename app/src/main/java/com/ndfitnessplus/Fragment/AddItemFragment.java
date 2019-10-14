package com.ndfitnessplus.Fragment;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.ndfitnessplus.Activity.AddEnquiryActivity;
import com.ndfitnessplus.Activity.AddMemberActivity;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.ServerClass;
import com.ndfitnessplus.Utility.ServiceUrls;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;
import com.ndfitnessplus.Utility.ViewDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddItemFragment extends Fragment {

    private EditText inputProdName,inputProdCode, inputProdDisc, inputQuantity,
            inputTax,inputRate,inputPurchaseAmount,inputMaxDiscount;
    private TextInputLayout inputLayoutProdName,inputLayoutProdCode, inputLayoutProdDisc,inputLayoutQuantity,
            inputLayoutTax,inputLayoutRate,inputLayoutPurchaseAmount,inputLayoutMaxDiscount;

    FloatingActionButton imageView,save;
    public final String TAG = AddItemFragment.class.getName();
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
    public AddItemFragment() {
        // Required empty public constructor
    }
    public static AddItemFragment newInstance() {
        AddItemFragment fragment = new AddItemFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
      View view=  inflater.inflate(R.layout.fragment_add_item, container, false);

        inputLayoutProdCode = (TextInputLayout)view. findViewById(R.id.input_layout_prod_code);
        inputLayoutProdName = (TextInputLayout)view. findViewById(R.id.input_layout_prod_name);
        inputLayoutProdDisc = (TextInputLayout)view. findViewById(R.id.input_layout_prod_disc);
        inputLayoutQuantity = (TextInputLayout)view. findViewById(R.id.input_layout_quantity);
        inputLayoutTax = (TextInputLayout)view. findViewById(R.id.input_layout_tax);
        inputLayoutRate = (TextInputLayout)view. findViewById(R.id.input_layout_rate);
        inputLayoutPurchaseAmount = (TextInputLayout)view. findViewById(R.id.input_layout_pur_amt);
        inputLayoutMaxDiscount = (TextInputLayout)view. findViewById(R.id.input_layout_max_discount);


        viewDialog = new ViewDialog((Activity)getActivity());

        imageView=view.findViewById(R.id.input_image);
        CapturedImage=view.findViewById(R.id.captured_image);
        save=(FloatingActionButton) view.findViewById(R.id.btn_save);
        inputProdCode = (EditText)view. findViewById(R.id.input_prod_code);
        inputProdName = (EditText)view. findViewById(R.id.input_prod_name);
        inputProdDisc = (EditText) view.findViewById(R.id.input_prod_disc);
        inputQuantity = (EditText)view. findViewById(R.id.input_quantity);
        inputTax = (EditText)view. findViewById(R.id.input_tax);
        inputRate = (EditText)view. findViewById(R.id.input_rate);
        inputPurchaseAmount = (EditText) view.findViewById(R.id.input_pur_amt);
        inputMaxDiscount=(EditText)view.findViewById(R.id.input_max_discount);


        //defining AwesomeValidation object
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);



        EnableRuntimePermissionToAccessCamera();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        });

        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        awesomeValidation.addValidation(getActivity(), R.id.input_prod_code, RegexTemplate.NOT_EMPTY, R.string.err_msg_prod_code);
        awesomeValidation.addValidation(getActivity(), R.id.input_prod_name, RegexTemplate.NOT_EMPTY, R.string.err_msg_prod_name);
        awesomeValidation.addValidation(getActivity(), R.id.input_quantity, RegexTemplate.NOT_EMPTY, R.string.err_msg_qty);
        awesomeValidation.addValidation(getActivity(), R.id.input_rate, RegexTemplate.NOT_EMPTY, R.string.err_msg_rate);
        awesomeValidation.addValidation(getActivity(), R.id.input_pur_amt, RegexTemplate.NOT_EMPTY, R.string.err_msg_pur_amt);
        awesomeValidation.addValidation(getActivity(), R.id.input_max_discount, RegexTemplate.NOT_EMPTY, R.string.err_msg_max_discount);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });
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
    }


    // *********** camera permission ***********
    public void EnableRuntimePermissionToAccessCamera(){

        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity)getActivity(),
                Manifest.permission.CAMERA))
        {

            // Printing toast message after enabling runtime permission.
            //Toast.makeText(AddEnquiryActivity.this,"CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions((Activity)getActivity(),new String[]{Manifest.permission.CAMERA}, RequestPermissionCode);

        }
    }
    @Override
    public void onActivityResult(int RC, int RQC, Intent I) {

        super.onActivityResult(RC, RQC, I);
        if (RC == REQUEST_IMAGE_CAPTURE ) {


//            uri = I.getData();
//            Log.v(TAG, String.format("camera request "));
//            Log.v(TAG, String.format("camera capture :: uri= %s", uri));
//            try {
            if(I !=null){
                Bundle extras = I.getExtras();
                if (extras != null) {
                    Bitmap bitmap1 = (Bitmap) extras.get("data");
                    CapturedImage.setImageBitmap(bitmap1);
                    bitmap = Bitmap.createScaledBitmap(bitmap1, 75,
                            75, true);
                    //return newBitmap;

                    CapturedImage.setVisibility(View.VISIBLE);
//                String timeStamp =
//                        new SimpleDateFormat("yyyyMMdd_HHmmss",
//                                Locale.getDefault()).format(new Date());
//                String imageFileName = "IMG_" + timeStamp;

                    // bitmap=Utility.resizeAndCompressImageBeforeSend((Activity)getActivity(),bitmap1,imageFileName);
                    Log.v(TAG, String.format(" Bitmap= %s", bitmap));
                    uploadimageClass();
                }
            }
//
        }
    }
    //*************** Add enquiry to database *************
    public void  AddEnquiryClass() {
        AddProductTrackClass ru = new AddProductTrackClass();
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
           // Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> AddProductDetails = new HashMap<String, String>();
            AddProductDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId((Activity)getActivity()));
            AddProductDetails.put("prod_code",inputProdCode.getText().toString());
            AddProductDetails.put("prod_name",inputProdName.getText().toString());
            AddProductDetails.put("prod_disc",inputProdDisc.getText().toString());
            AddProductDetails.put("quantity",inputQuantity.getText().toString());
            AddProductDetails.put("tax",inputTax.getText().toString());
            AddProductDetails.put("rate", inputRate.getText().toString());
            AddProductDetails.put("purchase_amount",inputPurchaseAmount.getText().toString());
            AddProductDetails.put("max_discount",inputMaxDiscount.getText().toString());
            AddProductDetails.put("prod_image",ConvertImage);
            AddProductDetails.put("exe_name",SharedPrefereneceUtil.getName((Activity)getActivity()));
            Log.v(TAG, String.format("doInBackground :: executive name= %s", SharedPrefereneceUtil.getName((Activity)getActivity())));
            AddProductDetails.put("action", "add_pos_items");
            String domainurl=SharedPrefereneceUtil.getDomainUrl((Activity)getActivity());
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
                Toast.makeText((Activity)getActivity(),"Product added succesfully",Toast.LENGTH_SHORT).show();
                inputProdCode.getText().clear();
                inputProdName.getText().clear();
                inputProdDisc.getText().clear();
                inputQuantity.getText().clear();
                inputTax.getText().clear();
                inputRate.getText().clear();
                inputPurchaseAmount.getText().clear();
                inputMaxDiscount.getText().clear();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                if (Build.VERSION.SDK_INT >= 26) {
                    ft.setReorderingAllowed(false);
                }
                ft.detach(this).attach(this).commit();

                // showCustomDialog();

                //inputEmail, inputPhone,inputAdd,inputReq,inputFollowupdate;
            }


            else if (success.equalsIgnoreCase(getResources().getString(R.string.one)))
            {
                Toast.makeText((Activity)getActivity(),"Something Went Wrong",Toast.LENGTH_SHORT).show();
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
           AddEnquiryClass();

       }


    }
    //  ********************** Check Email Already Exist or Not *********************
    public void  CheckProdNameClass() {
        CheckProdNameTrackClass ru = new CheckProdNameTrackClass();
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
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> EnquiryForDetails = new HashMap<String, String>();

            EnquiryForDetails.put("prod_name",inputProdName.getText().toString() );
            EnquiryForDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId((Activity)getActivity()) );
            EnquiryForDetails.put("action", "check_prod_name_exist");
            String domainurl=SharedPrefereneceUtil.getDomainUrl((Activity)getActivity());
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
                Toast.makeText((Activity)getActivity(),"Product Name Already Exits. Please Enter New Product Name",Toast.LENGTH_SHORT).show();
                inputProdName.getText().clear();
                //Toast.makeText(AddMemberActivity.this,"Please Enter New Mobile Number",Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //  ********************** Check Email Already Exist or Not *********************
    public void  CheckProdCodeClass() {
        CheckProdCodeTrackClass ru = new CheckProdCodeTrackClass();
        ru.execute("5");
    }

    class CheckProdCodeTrackClass extends AsyncTask<String, Void, String> {

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
            CheckProdCodeDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> EnquiryForDetails = new HashMap<String, String>();

            EnquiryForDetails.put("prod_code",inputProdCode.getText().toString());
            EnquiryForDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId((Activity)getActivity()) );
            EnquiryForDetails.put("action", "check_prod_code_exist");
            String domainurl=SharedPrefereneceUtil.getDomainUrl((Activity)getActivity());
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
                Toast.makeText(getActivity(),"Product Code Already Exits. Please Enter New Product Code",Toast.LENGTH_SHORT).show();
                inputProdCode.getText().clear();
                //Toast.makeText(AddMemberActivity.this,"Please Enter New Mobile Number",Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
