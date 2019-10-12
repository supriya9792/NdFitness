package com.ndfitnessplus.Fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ndfitnessplus.Activity.AddEnquiryActivity;
import com.ndfitnessplus.Activity.FullImageActivity;
import com.ndfitnessplus.Activity.MemberDetailsActivity;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.ServerClass;
import com.ndfitnessplus.Utility.ServiceUrls;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static com.ndfitnessplus.Activity.MemberDetailsActivity.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class BeforeFragment extends Fragment {
    private Uri mCropImageUri;
    public  static final int RequestPermissionCode  = 1 ;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int REQUEST_IMAGE = 100;
    // Bitmap bitmap;
    String ConvertImage="";
    String member_id,Before_Photo;
   // ImageView CapturedImage;
    private Uri uri;
    private Uri filePath;
    Bitmap bitmap;
    ImageView before_image;
    FloatingActionButton addImage,SaveImage;
    public BeforeFragment() {
        // Required empty public constructor
    }
    public static BeforeFragment newInstance() {
        BeforeFragment fragment = new BeforeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_before, container, false);
        before_image=(ImageView)view.findViewById(R.id.before_image);
        addImage=(FloatingActionButton) view.findViewById(R.id.fab);
        SaveImage=(FloatingActionButton) view.findViewById(R.id.fab_save);
        Bundle bundle = getArguments();
        member_id = bundle.getString("member_id");
        Before_Photo = bundle.getString("Before_Photo");
        Before_Photo.replace("\"", "");
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.startPickImageActivity(getActivity());
            }
        });
        if(!(Before_Photo.equals("null")||Before_Photo.equals(""))){
            String domainurl= SharedPrefereneceUtil.getDomainUrl((Activity)getActivity());
            String url= domainurl+ServiceUrls.IMAGES_URL + Before_Photo;

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.nouser);
            requestOptions.error(R.drawable.nouser);


            Glide.with(this)
                    .setDefaultRequestOptions(requestOptions)
                    .load(url).into(before_image);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.TOP|Gravity.RIGHT;
            addImage.setLayoutParams(params);
            SaveImage.hide();

        }
        SaveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadimageClass();
            }
        });
        EnableRuntimePermissionToAccessCamera();
        return view;
    }
    // *********** camera permission ***********
    public void EnableRuntimePermissionToAccessCamera(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.CAMERA))
        {

            // Printing toast message after enabling runtime permission.
            //Toast.makeText(get,"CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.CAMERA}, RequestPermissionCode);

        }
    }
    @Override
    @SuppressLint("NewApi")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // handle result of pick image chooser
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(getActivity(), data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(getActivity(), imageUri)) {
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
                before_image.setVisibility(View.VISIBLE);
                addImage.hide();
                SaveImage.show();
                // ((ImageButton) findViewById(R.id.quick_start_cropped_image)).setImageURI(result.getUri());
                before_image.setImageURI(result.getUri());
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), result.getUri());
                    //uploadimageClass();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //  Toast.makeText(this, "Cropping successful, Sample: " + result.getSampleSize(), Toast.LENGTH_LONG).show();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(getContext(), "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
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
                .start(getContext(),this);
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
                //viewDialog.showDialog();
            }

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);
                Log.v(TAG, String.format("onPostExecute :: response = %s", response));
                // dismissProgressDialog();
                //viewDialog.hideDialog();
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
                        Toast.makeText(getActivity(), "Uploaded Successfully", Toast.LENGTH_LONG).show();
                        //finish();
                        // SharedPrefereneceUtil.setCode(ChatRoomActivity.this,code);
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT);
                        params.gravity = Gravity.TOP|Gravity.RIGHT;
                        addImage.setLayoutParams(params);
                        addImage.show();
                        SaveImage.hide();
                    }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero)))
                    {

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            protected String doInBackground(String... params) {
                Log.v(TAG, String.format("doInBackground ::  params= %s", params));
                HashMap<String, String> ProfileDetails = new HashMap<String, String>();
                // ProfileDetails.put("image_name", GetImageNameEditText);
                ProfileDetails.put("image_data", ConvertImage);
                Log.d(TAG, "do in background image_data: "+ConvertImage);
                ProfileDetails.put("member_id", member_id);
                Log.d(TAG, "member_id: "+member_id);
                ProfileDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId((Activity)getActivity()));
                Log.d(TAG, "comp_id: "+SharedPrefereneceUtil.getSelectedBranchId((Activity)getActivity()));
                ProfileDetails.put("before_after", "Before");
                Log.d(TAG, "before_after: "+"Before");
                ProfileDetails.put("action", "upload_photo_before_after");
                String domainurl=SharedPrefereneceUtil.getDomainUrl((Activity)getActivity());
                //EmployeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(Employee.this));
                String loginResult = ruc.sendPostRequest(domainurl+ ServiceUrls.LOGIN_URL, ProfileDetails);
                Log.v(TAG, String.format("doInBackground :: upload result= %s", loginResult));
                return loginResult;
            }
        }
        UploadImageTrackClass ru = new UploadImageTrackClass();
        ru.execute("5");
    }

}
