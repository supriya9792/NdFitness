package com.ndfitnessplus.Activity;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.ndfitnessplus.MailUtility.Mail;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.BitmapSaver;
import com.ndfitnessplus.Utility.ServerClass;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;
import com.ndfitnessplus.Utility.Utility;
import com.ndfitnessplus.Utility.ViewDialog;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static android.graphics.Color.WHITE;
import static com.ndfitnessplus.Utility.ServerClass.Email;

public class GenerateQRActivity extends AppCompatActivity {
    Button send_mail;
    ImageView qrimage;
    String FilePath;
    public final String TAG = GenerateQRActivity.class.getName();
    ViewDialog viewDialog;
    Bitmap bmpqr;
    private static final int PERMISSION_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_generate_qr);
        initToolbar();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.generate_qr));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponent();
        requestPermission();
    }
    private void initComponent(){
        qrimage = (ImageView)findViewById(R.id.qrcode);
        send_mail = (Button)findViewById(R.id.btn_send_mail);


        // ****************** generating QR code Bitmap ***************
        try {
            //setting size of qr code
            int width =300;
            int height = 300;
            int smallestDimension = width < height ? width : height;
            String encodedid= Utility.randomEncodededCompId(16, SharedPrefereneceUtil.getSelectedBranchId(GenerateQRActivity.this));
            //setting parameters for qr code
            String charset = "UTF-8";
            Map<EncodeHintType, ErrorCorrectionLevel> hintMap =new HashMap<EncodeHintType, ErrorCorrectionLevel>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
             bmpqr=CreateQRCode(encodedid, charset, hintMap, smallestDimension, smallestDimension);
            qrimage.setImageBitmap(bmpqr);
        } catch (Exception ex) {
            Log.e("QrGenerate",ex.getMessage());
        }

        final Bitmap bitmap = getBitmapFromView(qrimage,500,500);
        final String subject=" QR code of "+SharedPrefereneceUtil.getCompanyName(GenerateQRActivity.this)+ " "+SharedPrefereneceUtil.getSelectedBranch(GenerateQRActivity.this);
        final String message=" Please find the attachment of Qr code";




        send_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GenerateQRActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        //second async stared within a asynctask but on the main thread
                        (new AsyncTask<String, String, Void>() {
                            ServerClass ruc = new ServerClass();

                            @Override
                            protected Void doInBackground(String... params) {
                                Mail m = new Mail("tulsababar.ndsoft@gmail.com", "Tulsa@2019");

                                String[] toArr = { SharedPrefereneceUtil.getEmail(GenerateQRActivity.this), "tulsababar01@gmail.com"};
                               // Log.v(TAG, String.format(" Email array to send = %s", toArr));
                                m.setTo(toArr);
                                m.setFrom("tulsababar.ndsoft@gmail.com");
                                m.setSubject(subject);
                                m.setBody(message);
                                if (Build.VERSION.SDK_INT >= 23)
                                {
                                    if (checkPermission())
                                    {
                                        final File  file = BitmapSaver.saveImageToExternalStorage(GenerateQRActivity.this, bmpqr);
                                        m.setAttachment(file);
                                        long n  = System.currentTimeMillis() / 1000L;
                                        String fname = "Image-" + n + ".jpg";
                                        FilePath=Environment.getExternalStorageDirectory().getPath()+ "/Pictures/saved_images/"+fname;
                                        m.setAttachmentName(FilePath);
                                        m.setAttachmentNamePath(fname);
                                        // Code for above or equal 23 API Oriented Device
                                        // Your Permission granted already .Do next code
                                    } else {
                                        requestPermission(); // Code for permission
                                    }
                                }else{
                                    final File  file = BitmapSaver.saveImageToExternalStorage(GenerateQRActivity.this, bmpqr);
                                    m.setAttachment(file);
                                    long n  = System.currentTimeMillis() / 1000L;
                                    String fname = "Image-" + n + ".jpg";
                                    FilePath=Environment.getExternalStorageDirectory().getPath()+ "/Pictures/saved_images/"+fname;
                                    m.setAttachmentName(FilePath);
                                    m.setAttachmentNamePath(fname);
                                }


                                try {
                                    if(m.send()) {
                                        runOnUiThread(new Runnable() {
                                            public void run() {

                                                Toast.makeText(GenerateQRActivity.this, "Email was sent successfully.", Toast.LENGTH_LONG).show();

                                            }
                                        });
                                    } else {
                                        Toast.makeText(GenerateQRActivity.this, "Email was not sent.", Toast.LENGTH_LONG).show();
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
        });
    }
    public Bitmap getBitmapFromView(View view, int totalHeight, int totalWidth) {
        Bitmap returnedBitmap = Bitmap.createBitmap(totalWidth,totalHeight , Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
    }
    // ************************* generating QR code ********************************
    public Bitmap CreateQRCode(String qrCodeData, String charset, Map hintMap, int qrCodeheight, int qrCodewidth){


        try {
            //generating qr code in bitmatrix type
            BitMatrix matrix = new MultiFormatWriter().encode(new String(qrCodeData.getBytes(charset), charset),
                    BarcodeFormat.QR_CODE, qrCodewidth, qrCodeheight, hintMap);
            //converting bitmatrix to bitmap

            int width = matrix.getWidth();
            int height = matrix.getHeight();
            int[] pixels = new int[width * height];
            // All are 0, or black, by default
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    //pixels[offset + x] = matrix.get(x, y) ? BLACK : WHITE;
                    pixels[offset + x] = matrix.get(x, y) ?
                            ResourcesCompat.getColor(getResources(),R.color.QRCodeBlackColor,null) :WHITE;
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            //setting bitmap to image view

            Bitmap overlay = BitmapFactory.decodeResource(getResources(), R.drawable.nd40);
            Bitmap bmOverlay1 =   Bitmap.createScaledBitmap(overlay, 40,
                    32, true);

            Bitmap bmp=mergeBitmaps(bmOverlay1,bitmap);
            return bmp;

        }catch (Exception er){
            Log.e("QrGenerate",er.getMessage());
        }
        return  null;
    }



    public Bitmap mergeBitmaps(Bitmap overlay, Bitmap bitmap) {

        int height = bitmap.getHeight();
        int width = bitmap.getWidth();

        Bitmap combined = Bitmap.createBitmap(width, height, bitmap.getConfig());
        Canvas canvas = new Canvas(combined);
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        canvas.drawBitmap(bitmap, new Matrix(), null);

        int centreX = (canvasWidth  - overlay.getWidth()) /2;
        int centreY = (canvasHeight - overlay.getHeight()) /2 ;
        canvas.drawBitmap(overlay, centreX, centreY, null);

        return combined;
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(GenerateQRActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(GenerateQRActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(GenerateQRActivity.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(GenerateQRActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
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
