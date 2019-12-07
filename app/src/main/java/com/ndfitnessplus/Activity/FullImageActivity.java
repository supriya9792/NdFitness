package com.ndfitnessplus.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.ndfitnessplus.Activity.Notification.TodaysEnrollmentActivity;
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

import static com.ndfitnessplus.Activity.EnquiryActivity.TAG;

public class FullImageActivity extends AppCompatActivity {
    private Uri mCropImageUri;
    String imageurl,contact,id,user;
    ImageView imageView;
    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    private View bottom_sheet;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    public  static final int RequestPermissionCode  = 1 ;
    Bitmap bitmap;
    private Uri uri;
    private static final String TAG = FullImageActivity.class.getSimpleName();
    private ProgressDialog pd;
    Button cancel,done;
    View bottomView;
    Toolbar toolbar;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    //Loading gif
    ViewDialog viewDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_full_image);
        initToolbar();
    }
    private void initToolbar() {
         toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.profile_photo));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponent();
    }
    private  void initComponent(){

        imageView=findViewById(R.id.image);
        cancel=findViewById(R.id.btn_cencel);
        done=findViewById(R.id.btn_done);
        bottomView=findViewById(R.id.botm_btn);
        viewDialog = new ViewDialog(this);

        bottom_sheet = findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(bottom_sheet);

        // Receiving the data from previous activity
        EnableRuntimePermissionToAccessCamera();

        Intent intent = getIntent();
        // Bundle args = intent.getBundleExtra("BUNDLE");
        if (intent != null) {
            imageurl=intent.getStringExtra("image");
            contact=intent.getStringExtra("contact");
            id=intent.getStringExtra("id");
            user=intent.getStringExtra("user");
            Log.d(TAG, "image: "+imageurl);
            Log.d(TAG, "contact: "+contact);
            Log.d(TAG, "id: "+id);
            Log.d(TAG, "user: "+user);
            String domainurl= SharedPrefereneceUtil.getDomainUrl(FullImageActivity.this);
            String url= domainurl+ServiceUrls.IMAGES_URL + imageurl;
            if(imageurl!=null) {
                //Glide.with(FullImageActivity.this).load(url).placeholder(R.drawable.nouser).into(imageView);
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.nouser);
                requestOptions.error(R.drawable.nouser);

                Glide.with(this)
                        .setDefaultRequestOptions(requestOptions)
                        .load(url)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                if(bitmap!=null){

                                    imageView.setImageBitmap(bitmap);
                                    return  true;
                                }
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        }).into(imageView);
            }

        }
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 uploadimageClass();
            }
        });
    }

    private void showBottomSheetDialog() {
        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        final View view = getLayoutInflater().inflate(R.layout.sheet_list, null);

        ((View) view.findViewById(R.id.lyt_browse)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialog.dismiss();
                Intent intent = new Intent();

                intent.setType("image/*");

                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intent, "Select Image From Gallery"), 7);
            }
        });

        ((View) view.findViewById(R.id.lyt_camera)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialog.dismiss();
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        });



        mBottomSheetDialog = new BottomSheetDialog(this);
        mBottomSheetDialog.setContentView(view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mBottomSheetDialog = null;
            }
        });
    }
//    @Override
//    protected void onActivityResult(int RC, int RQC, Intent I) {
//
//        super.onActivityResult(RC, RQC, I);
//
//        if (RC == 7 && RQC == RESULT_OK && I != null && I.getData() != null) {
//
//            uri = I.getData();
//            Log.v(TAG, String.format("doInBackground :: uri= %s", uri));
//                try {
//
//
//                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
//                    Intent intent = getIntent();
//                    // Bundle args = intent.getBundleExtra("BUNDLE");
//                    if (intent != null) {
//                        imageurl=intent.getStringExtra("image");
//                        contact=intent.getStringExtra("contact");
//                        id=intent.getStringExtra("id");
//                        user=intent.getStringExtra("user");
//                        Log.d(TAG, "result image: "+imageurl);
//                        Log.d(TAG, "result contact: "+contact);
//                        Log.d(TAG, "result id: "+id);
//                        Log.d(TAG, "result user: "+user);
//
//                    }
//                imageView.setImageBitmap(bitmap);
//                toolbar.setVisibility(View.GONE);
//                bottomView.setVisibility(View.VISIBLE);
//
//            } catch (IOException e) {
//
//                e.printStackTrace();
//            }
//        }else  if (RC == REQUEST_IMAGE_CAPTURE ) {
//
//
//           if(I !=null){
//            Bundle extras = I.getExtras();
//            if (extras != null) {
//                Bitmap bitmap1 = (Bitmap) extras.get("data");
//
//                Intent intent = getIntent();
//                // Bundle args = intent.getBundleExtra("BUNDLE");
//                if (intent != null) {
//                    imageurl=intent.getStringExtra("image");
//                    contact=intent.getStringExtra("contact");
//                    id=intent.getStringExtra("id");
//                    user=intent.getStringExtra("user");
//                    Log.d(TAG, "result image: "+imageurl);
//                    Log.d(TAG, "result contact: "+contact);
//                    Log.d(TAG, "result id: "+id);
//                    Log.d(TAG, "result user: "+user);
//
//                }
//                Log.v(TAG, String.format("doInBackground :: bitmap= %s", bitmap1));
//
//                bitmap = Bitmap.createScaledBitmap(bitmap1, 200,
//                        200, true);
//                //return newBitmap;
//                imageView.setImageBitmap(bitmap);
//                toolbar.setVisibility(View.GONE);
//                bottomView.setVisibility(View.VISIBLE);
//
////                String timeStamp =
////                        new SimpleDateFormat("yyyyMMdd_HHmmss",
////                                Locale.getDefault()).format(new Date());
////                String imageFileName = "IMG_" + timeStamp;
//
//               // bitmap=Utility.resizeAndCompressImageBeforeSend(AddEnquiryActivity.this,bitmap1,imageFileName);
//                Log.v(TAG, String.format(" Bitmap= %s", bitmap));
//               // uploadimageClass();
//            }
//             }
//
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_photo_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_home) {
            Intent intent = new Intent(FullImageActivity.this, MainActivity.class);
            startActivity(intent);
        }else if(id ==R.id.action_edit){
           //showBottomSheetDialog();
            CropImage.startPickImageActivity(FullImageActivity.this);
        }else if(id== android.R.id.home){
            //Toast.makeText(this,"Navigation back pressed",Toast.LENGTH_SHORT).show();
            // NavUtils.navigateUpFromSameTask(this);
            finish();
        }

        return true;
    }
    public void EnableRuntimePermissionToAccessCamera(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(FullImageActivity.this,
                Manifest.permission.CAMERA))
        {

            // Printing toast message after enabling runtime permission.
            Toast.makeText(FullImageActivity.this,"CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(FullImageActivity.this,new String[]{Manifest.permission.CAMERA}, RequestPermissionCode);

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
                Intent intent = getIntent();
                // Bundle args = intent.getBundleExtra("BUNDLE");
                if (intent != null) {
                    imageurl=intent.getStringExtra("image");
                    contact=intent.getStringExtra("contact");
                    id=intent.getStringExtra("id");
                    user=intent.getStringExtra("user");
                    Log.d(TAG, "result image: "+imageurl);
                    Log.d(TAG, "result contact: "+contact);
                    Log.d(TAG, "result id: "+id);
                    Log.d(TAG, "result user: "+user);

                }
              //  imageView.setVisibility(View.VISIBLE);
                // ((ImageButton) findViewById(R.id.quick_start_cropped_image)).setImageURI(result.getUri());
                imageView.setImageURI(result.getUri());
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), result.getUri());
                    imageView.setImageURI(result.getUri());
                  //  imageView.setImageBitmap(bitmap);
                    toolbar.setVisibility(View.GONE);
                    bottomView.setVisibility(View.VISIBLE);
                   // uploadimageClass();
                } catch (IOException e) {
                    e.printStackTrace();
                }
               // Toast.makeText(this, "Cropping successful, Sample: " + result.getSampleSize(), Toast.LENGTH_LONG).show();
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
    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .setAspectRatio(1,1)
                .setFixAspectRatio(true)
                .start(this);
    }
    private void showProgressDialog() {
        Log.v(TAG, String.format("showProgressDialog"));
        pd = new ProgressDialog(FullImageActivity.this);
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

    private void uploadimageClass() {

        ByteArrayOutputStream byteArrayOutputStreamObject ;

        byteArrayOutputStreamObject = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreamObject);

        byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();

        final String  ConvertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);
        Log.v(TAG, String.format(" ConvertImage= %s", ConvertImage));



        class UploadImageTrackClass extends AsyncTask<String, Void, String> {

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
               // dismissProgressDialog();
                viewDialog.hideDialog();
                //Toast.makeText(Employee.this, response, Toast.LENGTH_LONG).show();
                ProfileDetails(response);

            }
            private void ProfileDetails(String response) {
                Log.v(TAG, String.format("uploadResponse :: response = %s", response));

                JSONObject jsonObjLoginResponse = null;
                try {
                    jsonObjLoginResponse = new JSONObject(response);
                    String success = jsonObjLoginResponse.getString(getResources().getString(R.string.success));

                    if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                        Toast.makeText(getApplicationContext(), "Uploaded Successfully", Toast.LENGTH_LONG).show();
                        finish();
                        // SharedPrefereneceUtil.setCode(ChatRoomActivity.this,code);

                    }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero)))
                    {

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            protected String doInBackground(String... params) {
               // Log.v(TAG, String.format("doInBackground ::  params= %s", params));
                HashMap<String, String> ProfileDetails = new HashMap<String, String>();
                // ProfileDetails.put("image_name", GetImageNameEditText);
                ProfileDetails.put("image_data", ConvertImage);
                Log.d(TAG, "do in background image_data: "+ConvertImage);
                ProfileDetails.put("contact", contact);
                Log.d(TAG, "contact: "+contact);
                ProfileDetails.put("id", id);
                Log.d(TAG, "id: "+id);
                ProfileDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(FullImageActivity.this));
                Log.d(TAG, "comp_id: "+SharedPrefereneceUtil.getSelectedBranchId(FullImageActivity.this));
                ProfileDetails.put("user", user);
                Log.d(TAG, "user: "+user);
                ProfileDetails.put("action", "upload_image");
                String domainurl=SharedPrefereneceUtil.getDomainUrl(FullImageActivity.this);
                //EmployeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(Employee.this));
                String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, ProfileDetails);
                Log.v(TAG, String.format("doInBackground :: upload result= %s", loginResult));
                return loginResult;
            }
        }
        UploadImageTrackClass ru = new UploadImageTrackClass();
        ru.execute("5");
    }
    public void onBackPressed() {
        finish();
    }
}
