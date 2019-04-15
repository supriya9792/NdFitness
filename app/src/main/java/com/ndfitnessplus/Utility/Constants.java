package com.ndfitnessplus.Utility;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


import com.ndfitnessplus.R;

import static android.view.View.GONE;

/**
 * Created by Admin on 26/12/2017.
 */

public class Constants {
    public static final String PREFS_NAME = "NDF_Pref";

    public static final String DOMAIN_NAME = "domain";

    public static final String IS_LOGIN = "is_login";
    public static final String COMP_NAME = "company_name";
    public static final String LOGIN_ID = "login_id";
    public static final String AUTHORITY = "authority";
    public static final String BRANCH = "branch";
    public static final String COMPANY_AUTO_ID = "company_id";
    public static final String MOBILE = "mobile";
    public static final String EMAIL = "email";
    public static final String STATUS = "status";
    public static final String STAFF_AUTO_ID = "staff_id";
    public static final String REG_DATE = "reg_date";
    public static final String NAME = "name";
    public static final String USER_NAME = "user_name";
    public static final String PASSWORD = "password";
    public static final String SELECTED_BRANCH_ID = "selected_branch_id";

    // constants for notification trigger

    /*public static final String Enquiry = "enquiry";
    public static final String EnquiryFollowup = "enquiry_followup";
    public static final String Admission = "admission";
    public static final String ActiveMember = "active_member";
    public static final String DeactiveMember = "deactive_member";
    public static final String StaffBirthday = "staff_birthday";
    public static final String MemberBirthday = "member_birthday";
    public static final String MemberAnniversary = "member_anniversary";
    public static final String MemberEndDate = "member_end_date";
    public static final String PaymentDate = "payment_date";
    public static final String TodayPostDatedCheque = "post_dated_cheque";
    public static final String OtherFollowup = "other_followups";*/

    public static  final String USER_TOP_NAV_ADMISSIONS = "Admissions";
    public static  final String USER_TOP_NAV_FOLLOW_UPS = "Follow-Ups";
    public static  final String USER_TOP_NAV_MEMBER_INFO = "Member Info";
    public static  final String USER_TOP_NAV_COLLECTION = "Collection";
    public static  final String USER_TOP_NAV_ENQUIRY = "Enquiry";
    public static  final String USER_TOP_NAV_NOTIFICATIONS = "Notifications";

    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void removeUnderlines(Spannable p_Text) {
        URLSpan[] spans = p_Text.getSpans(0, p_Text.length(), URLSpan.class);

        for(URLSpan span:spans) {
            int start = p_Text.getSpanStart(span);
            int end = p_Text.getSpanEnd(span);
            p_Text.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL());
            p_Text.setSpan(span, start, end, 0);
        }
    }

//    public static Drawable buildCounterDrawable(int count, int backgroundImageId, Context context) {
//        LayoutInflater inflater = LayoutInflater.from(context);
//        View view = inflater.inflate(R.layout.notification_counter_layout, null);
//        view.setBackgroundResource(backgroundImageId);
//
//        if (count == 0) {
//            View counterTextPanel = view.findViewById(R.id.counterValuePanel);
//            counterTextPanel.setVisibility(GONE);
//        } else {
//            TextView textView = (TextView) view.findViewById(R.id.count);
//            textView.setText(String.valueOf(count));
//        }
//        view.measure(
//                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
//
//        view.setDrawingCacheEnabled(true);
//        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
//        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
//        view.setDrawingCacheEnabled(false);
//
//        return new BitmapDrawable(context.getResources(), bitmap);
//    }
}
