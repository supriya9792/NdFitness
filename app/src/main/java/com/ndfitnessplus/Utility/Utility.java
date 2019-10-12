package com.ndfitnessplus.Utility;

/**
 * Created by admin on 7/11/2017.
 */


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.ndfitnessplus.R;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class Utility {
    private static final String ALPHA_NUMERIC_STRING = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final String NUMERIC = "0123456789";

    private static final String TAG = Utility.class.getSimpleName();
    private static ProgressDialog sProgressDialog;

    /**
     * This method will make the navigation bar and status bar transparent only for api 21+
     *
     * @param activity
     */
    public static void makeFullScreen(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // Set the status bar and navigation bar as transparent
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    public static boolean isValidEmailAddress(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static void setBadgeCount(Context context, LayerDrawable icon, String count) {

        BadgeDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
    }
    public static void showProgressDialog(Context context) {
        if (null != sProgressDialog && sProgressDialog.isShowing()) {
            return;
        }
        sProgressDialog = new ProgressDialog(context);

        sProgressDialog.setMessage("loading");
        sProgressDialog.setCancelable(false);

        if (context instanceof Activity) {
            if (!((Activity) context).isFinishing()) {
                sProgressDialog.show();
            }
        } else {
            sProgressDialog.show();
        }
    }

    public static void hideProgressBar() {
        try {
            if (null != sProgressDialog && sProgressDialog.isShowing()) {

                Context context = sProgressDialog.getContext();

                if (context instanceof Activity) {

                    if (!((Activity) context).isFinishing()) {


                        sProgressDialog.dismiss();
                        sProgressDialog = null;
                    }
                } else {


                    sProgressDialog.dismiss();
                    sProgressDialog = null;
                }
            }
        } catch (IllegalArgumentException e) {
            Log.w(TAG, "Simple ignore the exceprion", e);
        }
    }

    //checks if device connected to internet
    public static boolean isConnectedToInternet(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = connectivityManager.getNetworkInfo(mNetwork);

                if (networkInfo != null && networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    return true;
                }
            }
        } else {
            if (connectivityManager != null) {
                //noinspection deprecation
                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo networkInfo : info) {
                        if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                            Log.d(TAG,
                                    "NETWORKNAME: " + networkInfo.getTypeName());
                            return true;
                        }
                    }
                }
            }
        }
        Log.v(TAG, "not connected to internet");
        return false;
    }

    //hides soft keyboard
    public static void hideKeyBordActivity(Activity activity) {
        activity.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    public static void hideSoftKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    /* public static void setSpinnerAdapter(Context mContext, Spinner spinner, ArrayList<String> arrayList) {
         ArrayAdapter spinnerAdapter = new ArrayAdapter(mContext, R.layout.spinner_item, arrayList);
         spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
         spinner.setAdapter(spinnerAdapter);

     }*/
    public static String getDateFormatAsLastSeen(Calendar date) {

        //   String dayNumberSuffix = getDayNumberSuffix(date.get(Calendar.DAY_OF_MONTH));
        DateFormat dateFormat = new SimpleDateFormat(" d MMM yyyy");
        return dateFormat.format(date.getTime());
    }

    public static String getCurrentDate() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy");//"yyyy-MM-dd"
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }
    public static String getFirstDayofMonth() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("MM-yyyy");//"yyyy-MM-dd"
        Date now = new Date();
        Calendar cal = Calendar.getInstance();

       // return cal.getTime();
        String strDate = sdfDate.format(now);
        return "01-"+strDate;
    }
    public static String getCurrentDateTime() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");//"yyyy-MM-dd"
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }
    public static String formatDate(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("d MMM yyyy");
            return fmtOut.format(date);
        } catch (ParseException e) {

        }

        return "";
    }
    public static String formatDateDB(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("dd-MM-yyyy");
            return fmtOut.format(date);
        } catch (ParseException e) {

        }

        return "";
    }
    public String formatMonYear(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("MMM yyyy");
            return fmtOut.format(date);
        } catch (ParseException e) {

        }

        return "";
    }
    public String formatMonNumber(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("MMM yyyy");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("MM yyyy");
            return fmtOut.format(date);
        } catch (ParseException e) {

        }

        return "";
    }
    public String formatDayMon(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("dd MMM,h:mm a");
            return fmtOut.format(date);
        } catch (ParseException e) {

        }

        return "";
    }
    public static String CalulateDateFromGivenDays(String date,int days){
    String enddate="";
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");
            Date startdate = null;
            startdate = fmt.parse(date);
            Calendar c = Calendar.getInstance();
            c.setTime(startdate);
            c.add(Calendar.DATE, days);
            Date expDate = c.getTime();
          enddate=fmt.format(expDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return enddate;
    }
    public static String getNextDate(String  curDate) {
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        final Date date;
        String nextdate="";
        try {
            date = format.parse(curDate);
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");
             nextdate=fmt.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
       return  nextdate;
    }
    public String get_time_ago(String cret_date){
        String ago="";
        try
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date past = format.parse(cret_date);
            // Date past = format.parse("2016.02.05 AD at 23:59:30");
            Date now = new Date();
            System.out.println("Now date" +now);
            System.out.println("cret date" +past);
            System.out.println("Now time" +now.getTime());
            System.out.println("Past time" + past.getTime());
            long seconds= TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
            long minutes=TimeUnit.MILLISECONDS.toMinutes( now.getTime() - past.getTime());
            long hours=TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
            long days=TimeUnit.MILLISECONDS.toDays( now.getTime() - past.getTime());

//            long seconds= TimeUnit.MILLISECONDS.toSeconds( past.getTime()- now.getTime());
//            long minutes=TimeUnit.MILLISECONDS.toMinutes(past.getTime() - now.getTime() );
//            long hours=TimeUnit.MILLISECONDS.toHours(past.getTime() - now.getTime());
//            long days=TimeUnit.MILLISECONDS.toDays( past.getTime() -  now.getTime());

//          System.out.println(TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime()) + " milliseconds ago");
//          System.out.println(TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime()) + " minutes ago");
//          System.out.println(TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime()) + " hours ago");
//          System.out.println(TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime()) + " days ago");

            if(seconds<60)
            {
                System.out.println(seconds+" seconds ago");
                ago= seconds+" seconds ago";
            }
            else if(minutes<60)
            {
                System.out.println(minutes+" minutes ago");
                ago= minutes+" minutes ago";
            }
            else if(hours<24)
            {
                System.out.println(hours+" hours ago");
                ago= hours+" hours ago";
            }
            else
            {
                System.out.println(days+" days ago");
                ago= days+" days ago";
            }
        }
        catch (Exception j){
            j.printStackTrace();
        }
        return ago;

    }
    public  static  String getWishes(){
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if(timeOfDay >= 0 && timeOfDay < 12){
            return "Good Morning";
          //  Toast.makeText(this, "Good Morning", Toast.LENGTH_SHORT).show();
        }else if(timeOfDay >= 12 && timeOfDay < 16){
            return "Good Afternoon";
          //  Toast.makeText(this, "Good Afternoon", Toast.LENGTH_SHORT).show();
        }else if(timeOfDay >= 16 && timeOfDay < 21){
            return "Good Evening";
           // Toast.makeText(this, "Good Evening", Toast.LENGTH_SHORT).show();
        }else if(timeOfDay >= 21 && timeOfDay < 24){
            return "Good Night";
          //  Toast.makeText(this, "Good Night", Toast.LENGTH_SHORT).show();
        }
        return "";
    }
    public static String getLocation(double latitude, double longitude, Context context) {

        Geocoder gc = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = gc.getFromLocation(latitude, longitude, 1);
            StringBuilder sb = new StringBuilder();
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                //for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
                //  sb.append(address.getAddressLine(i)).append("\n");
                sb.append(address.getSubLocality()).append("\n");
                //    sb.append(address.getPostalCode()).append("\n");
                //sb.append(address.getCountryName());
                Toast.makeText(context, sb.toString(), Toast.LENGTH_SHORT).show();
                return sb.toString();
            }
        } catch (Exception e) {
            return "";
        }
        return "";
    }


    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkPermission(final Context context)
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>= Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();

                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
    public static String randomEncodededCompId(int count,String comp_id) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            if(count == 6){
                builder.append(comp_id);
            }
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }
    public static String lastFour(String s) {
        StringBuilder lastFour = new StringBuilder();
        int check = 0;
        for (int i = s.length() - 1; i >= 0; i--) {
            if (Character.isDigit(s.charAt(i))) {
                check++;
            }
            if (check <= 2||check >=s.length()-1) {
                lastFour.append(s.charAt(i));
            } else {
               lastFour.append(Character.isDigit(s.charAt(i)) ? "*" : s.charAt(i));
            }
        }
        return lastFour.reverse().toString();
    }
    public static String getyesterdayDate() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");//"yyyy-MM-dd"
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String strDate = sdfDate.format(cal.getTime());
        return strDate;
    }
    public static String formatDateMonthDB(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("d MMM yyyy");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("yyyy-MM-dd");
            return fmtOut.format(date);
        } catch (ParseException e) {

        }

        return "";
    }
}
