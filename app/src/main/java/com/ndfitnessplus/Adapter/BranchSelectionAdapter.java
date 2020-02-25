package com.ndfitnessplus.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.ndfitnessplus.Activity.BranchSelectionActivity;
import com.ndfitnessplus.Activity.LoginActivity;
import com.ndfitnessplus.Activity.MainActivity;
import com.ndfitnessplus.Activity.MemberDetailsActivity;
import com.ndfitnessplus.Model.BranchList;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.NetworkUtils;
import com.ndfitnessplus.Utility.ServerClass;
import com.ndfitnessplus.Utility.ServiceUrls;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.ndfitnessplus.Activity.SelectDomainActivity.TAG;
import static com.ndfitnessplus.Utility.Constants.SELECTED_BRANCH_ID;

public class BranchSelectionAdapter extends  RecyclerView.Adapter<BranchSelectionAdapter.MyView>  {

private Context mContext;
private List<BranchList> subList;
 private ArrayList<BranchList> arraylist;
private String ipadd;

public class MyView extends RecyclerView.ViewHolder   {
    private TextView branch_nameTV,dailyCollectionTV,monthlyCollectionTV,locationCityTv,statusTV;
    ImageView contactIV;
    CircularImageView imageView;
    View parent;
    private MyView(View view) {
        super(view);
        branch_nameTV =  itemView.findViewById(R.id.branch_nameTV);
        locationCityTv =  itemView.findViewById(R.id.locationCityTv);
        dailyCollectionTV =  itemView.findViewById(R.id.dailyCollectionTV);
        monthlyCollectionTV =  itemView.findViewById(R.id.monthlyCollectionTV);
        statusTV =  itemView.findViewById(R.id.statusTv);
        parent=itemView.findViewById(R.id.layout);
        contactIV =  itemView.findViewById(R.id.contactIV);
        imageView = itemView.findViewById(R.id.branchImage);

    }


}

    public BranchSelectionAdapter(Context mContext, ArrayList<BranchList> albumList) {
        this.mContext = mContext;
        this.subList = albumList;
        this.arraylist = new ArrayList<BranchList>();
        this.arraylist.addAll(albumList);
    }

    @Override
    public BranchSelectionAdapter.MyView onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.branch_selection_list, parent, false);

        return new BranchSelectionAdapter.MyView(itemView);
    }

    @Override
    public void onBindViewHolder(final BranchSelectionAdapter.MyView holder, int position) {

        final BranchList enq = subList.get(position);

        Resources res = mContext.getResources();
        Bitmap src = BitmapFactory.decodeResource(res, R.drawable.gym);
        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(res,src);
        drawable.setCircular(true);

        String domainurl= SharedPrefereneceUtil.getDomainUrl((Activity)mContext);
        String url= domainurl+ServiceUrls.IMAGES_URL + enq.getImage();
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.nouser);
        requestOptions.error(R.drawable.nouser);


        Glide.with(mContext)
                .setDefaultRequestOptions(requestOptions)
                .load(url).into(holder.imageView);

        holder.branch_nameTV.setText(enq.getBranchName());
        holder.locationCityTv.setText(enq.getCity());
        holder.statusTV.setText(enq.getStatus());
        double ttlcol=Double.parseDouble(enq.getDailyCollection());
        DecimalFormat df = new DecimalFormat("##,##,##,##,##,##,##0.00");
        String rtd= df.format(ttlcol);
        holder.dailyCollectionTV.setText(rtd);

        double ttlcolmonth=Double.parseDouble(enq.getMonthlyCollection());
        String rtm= df.format(ttlcolmonth);
        holder.monthlyCollectionTV.setText(rtm);
        holder.contactIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:"+enq.getContactNumber()));
                mContext.startActivity(dialIntent);
            }
        });
        if(enq.getStatus().equals("Active")){
            holder.parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPrefereneceUtil.setSelectedBranchId((Activity) mContext,enq.getBranchId());
                    SharedPrefereneceUtil.setCompanyName((Activity) mContext,enq.getBranchName());
                    SharedPrefereneceUtil.setSelectedBranch((Activity) mContext,enq.getCity());
                    logTrackerclass();
                    Intent intent = new Intent(mContext, MainActivity.class);

                    mContext.startActivity(intent);
                    ((BranchSelectionActivity) mContext).finish();
                }
            });
        }else{
            holder.parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext,"Your Company is Inactive",Toast.LENGTH_SHORT).show();
                }
            });
        }
        final ConnectivityManager connMgr = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifi.isConnectedOrConnecting ()) {
            ipadd= NetworkUtils.getWifiIPAddress(mContext);
        } else if (mobile.isConnectedOrConnecting ()) {
            ipadd= NetworkUtils.getMobileIPAddress();
        } else {
            Toast.makeText(mContext, "No Network ", Toast.LENGTH_LONG).show();
        }

    }


    @Override
    public int getItemCount() {
        return subList.size();
    }

    private void logTrackerclass() {
        LoginTrackclass ru = new LoginTrackclass();
        ru.execute("5");
    }
    class LoginTrackclass extends AsyncTask<String, Void, String> {

        ServerClass ruc = new ServerClass();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            LoginDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            String device_id = NetworkUtils.getIMEINo(mContext);
            HashMap<String, String> LoginDetails = new HashMap<String, String>();
            LoginDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId((Activity)mContext));
            LoginDetails.put("name",SharedPrefereneceUtil.getName((Activity)mContext));
            LoginDetails.put("contact",SharedPrefereneceUtil.getMobile((Activity)mContext));
            LoginDetails.put("comp_name",SharedPrefereneceUtil.getCompanyName((Activity)mContext));
            LoginDetails.put("branch_name",SharedPrefereneceUtil.getSelectedBranch((Activity)mContext));
            LoginDetails.put("username",SharedPrefereneceUtil.getUserNm((Activity)mContext));
            LoginDetails.put("imei_no",device_id);
            LoginDetails.put("ip_address",ipadd);
            LoginDetails.put("mode","AdminApp");
            LoginDetails.put("action", "add_log_details");
            String domainurl=SharedPrefereneceUtil.getDomainUrl((Activity)mContext);
            String loginResult2 = ruc.sendPostRequest( domainurl+ServiceUrls.LOGIN_URL, LoginDetails);
            return loginResult2;
        }
    }
    private void LoginDetails(String jsonResponse) {

        JSONObject jsonObjLoginResponse = null;
        try {
            jsonObjLoginResponse = new JSONObject(jsonResponse);
            String success = jsonObjLoginResponse.getString(mContext.getResources().getString(R.string.success));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}


