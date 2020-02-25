package com.ndfitnessplus.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.ndfitnessplus.Activity.EnquiryFollowupDetailsActivity;
import com.ndfitnessplus.Activity.FullImageActivity;
import com.ndfitnessplus.Activity.MemberDetailsActivity;
import com.ndfitnessplus.Activity.RenewActivity;
import com.ndfitnessplus.Model.EnquiryList;
import com.ndfitnessplus.Model.MemberDataList;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.ServerClass;
import com.ndfitnessplus.Utility.ServiceUrls;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;
import com.ndfitnessplus.Utility.ViewDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ndfitnessplus.Activity.EnquiryActivity.TAG;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.BaseViewHolder> {
    ArrayList<MemberDataList> arrayList;
    private ArrayList<MemberDataList> subList;
    Context context;

    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;
    String member_id,name,contact,status,End_Date,FinalBalance;
    //Loading gif
    ViewDialog viewDialog;
    public MemberAdapter(ArrayList<MemberDataList> enquiryList, Context context) {
        this.subList = enquiryList;
        this.arrayList = new ArrayList<MemberDataList>();
        this.context = context;
        this.arrayList.addAll(enquiryList);
    }

    @Override
    public MemberAdapter.BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new MemberAdapter.ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.member_list_item, parent, false));
            case VIEW_TYPE_LOADING:
                return new MemberAdapter.FooterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false));
            default:
                return null;
        }

    }

    @Override
    public void onBindViewHolder(MemberAdapter.BaseViewHolder holder, int position) {
        holder.onBind(position);
    }
    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible) {
            return position == arrayList.size() - 1 ? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        return arrayList == null ? 0 : arrayList.size();
    }
    public void add(MemberDataList response) {
        arrayList.add(response);
        notifyItemInserted(arrayList.size() - 1);
    }

    public void addAll(ArrayList<MemberDataList> postItems) {
        for (MemberDataList response : postItems) {
            add(response);
            subList.add(response);
        }
    }


    private void remove(MemberDataList postItems) {
        int position = arrayList.indexOf(postItems);
        if (position > -1) {
            arrayList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void addLoading() {
        isLoaderVisible = false;
        add(new MemberDataList());

    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = arrayList.size() - 1;
        MemberDataList item = getItem(position);
        if (item != null) {
            arrayList.remove(position);
            notifyItemRemoved(position);
        }
    }
    public void removeblank(){
        isLoaderVisible = false;
        int position = arrayList.size() - 1;
        MemberDataList item = getItem(position);
        if (item != null) {
            arrayList.remove(position);
            notifyItemRemoved(position);
        }
    }
    public void clear() {
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }
    MemberDataList getItem(int position) {
        return arrayList.get(position);
    }
    //filter for search
    public int filter(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());
        arrayList.clear();
        if (charText.length() == 0) {

            arrayList.addAll(subList);
        } else {
            for (MemberDataList wp : subList) {
                if (wp.getName().toLowerCase(Locale.getDefault())
                        .contains(charText)
                        ||wp.getContact().toLowerCase(Locale.getDefault()).contains(charText)) {
                    arrayList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
        return  arrayList.size();
    }
    //filter for search
    public int search( String charTex,final ArrayList<MemberDataList> subList) {

        final String charText = charTex.toLowerCase(Locale.getDefault());
        arrayList.clear();
        if (charText.length() == 0) {

            arrayList.addAll(subList);
            return 0;
        } else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                public void run() {
                    // UI code goes here
                    for (final MemberDataList wp : subList) {
                        if (wp.getName().toLowerCase(Locale.getDefault())
                                .contains(charText)
                                ||wp.getContact().toLowerCase(Locale.getDefault()).contains(charText)) {
                            arrayList.add(wp);

                        }
                    }
                }
            });

        }
        notifyDataSetChanged();
        return arrayList.size();
    }
    //View for showing enquiry
    public class ViewHolder extends MemberAdapter.BaseViewHolder implements View.OnClickListener {
        TextView nameTV,excecutive_nameTV,dobTV,genderTV,occupationTV,bloodgroupTV,contactTV,memberidTv;
        ImageView statusIv,attendanceIv;
        CircularImageView imageView;
        View layoutparent;
        public ViewHolder(View itemView) {
            super(itemView);

            contactTV = (TextView) itemView.findViewById(R.id.contactTV);
            occupationTV = (TextView) itemView.findViewById(R.id.occupationTV);
            nameTV = (TextView) itemView.findViewById(R.id.nameTV);

            imageView=(CircularImageView) itemView.findViewById(R.id.input_image);
            excecutive_nameTV = (TextView) itemView.findViewById(R.id.excecutive_nameTV);
            dobTV = (TextView) itemView.findViewById(R.id.dobTV);
            genderTV = (TextView) itemView.findViewById(R.id.genderTV);
            bloodgroupTV = (TextView) itemView.findViewById(R.id.bloodgrpTV);
            memberidTv = (TextView) itemView.findViewById(R.id.member_idTV);
            statusIv = (ImageView) itemView.findViewById(R.id.status);
            attendanceIv = (ImageView) itemView.findViewById(R.id.attendanceIV);
            layoutparent=(View)itemView.findViewById(R.id.lyt_parent);
            viewDialog = new ViewDialog((Activity) context);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.contactTV){
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:"+arrayList.get(getAdapterPosition()).getContact()));
                context.startActivity(dialIntent);
            }
        }
        @Override
        protected void clear() {

        }

        public void onBind(final int position) {
            super.onBind(position);
            final MemberDataList enq = arrayList.get(position);
            nameTV.setText(enq.getName());

            contactTV.setText(enq.getContactEncrypt());
            excecutive_nameTV.setText(enq.getExecutiveName());
            dobTV.setText(enq.getBirthDate());
            genderTV.setText(enq.getGender());
            bloodgroupTV.setText(enq.getBlodGroup());
            occupationTV.setText(enq.getOccupation());
            if(enq.getStatus()!=null){
             if(enq.getStatus().equals("Active")){
                 statusIv.setColorFilter(ContextCompat.getColor(context, R.color.green), android.graphics.PorterDuff.Mode.SRC_IN);
             }else if(enq.getStatus().equals("InActive")){
                 statusIv.setColorFilter(ContextCompat.getColor(context, R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
             }
            }

                attendanceIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(enq.getStatus().equals("Active")) {
                        member_id = enq.getID();
                        name = enq.getName();
                        status = enq.getStatus();
                        contact = enq.getContact();
                        End_Date=enq.getEndDate();
                        FinalBalance=enq.getFinalBalance();
                        makeattendanceclass();
                        }else{
                            Toast.makeText(context,"Your An Inactive Member",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            String domainurl= SharedPrefereneceUtil.getDomainUrl((Activity) context);
            String url= domainurl+ServiceUrls.IMAGES_URL + enq.getImage();


            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.nouser);
            requestOptions.error(R.drawable.nouser);


            Glide.with(context)
                    .setDefaultRequestOptions(requestOptions)
                    .load(url).into(imageView);

            memberidTv.setText(enq.getID());
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCustomDialog(position);
                }
            });

            layoutparent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String   member_id=enq.getID();
                    Intent intent=new Intent(context, MemberDetailsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("filter_array_list", enq);
                    intent.putExtra("BUNDLE",bundle);
                    context.startActivity(intent);
                }
            });
        }
    }
    private void showCustomDialog(int position) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.item_grid_image_two_line_light);
        dialog.setCancelable(true);
        final MemberDataList enq = arrayList.get(position);

        ImageView imageView = (ImageView) dialog. findViewById(R.id.image);
        TextView name = (TextView) dialog. findViewById(R.id.name);
        ImageButton phone=(ImageButton)dialog.findViewById(R.id.phone_call);
        ImageView whatsapp=(ImageView)dialog.findViewById(R.id.whatsapp);
        String domainurl= SharedPrefereneceUtil.getDomainUrl((Activity) context);
        String url= domainurl+ServiceUrls.IMAGES_URL + enq.getImage();

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.nouser);
        requestOptions.error(R.drawable.nouser);


        Glide.with(context)
                .setDefaultRequestOptions(requestOptions)
                .load(url).into(imageView);
        name.setText(enq.getName());
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:"+enq.getContact()));
                context.startActivity(dialIntent);
            }
        });
        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                PackageManager pm=context.getPackageManager();
                try {

                    Uri uri = Uri.parse("whatsapp://send?phone=+91" + enq.getContact());
                    Intent waIntent = new Intent(Intent.ACTION_VIEW,uri);
                    PackageInfo info=pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                    waIntent.setPackage("com.whatsapp");
                    context.startActivity(waIntent);

                } catch (PackageManager.NameNotFoundException e) {
                    Toast.makeText(context, "WhatsApp not Installed", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, FullImageActivity.class);
                intent.putExtra("image",enq.getImage());
                intent.putExtra("contact",enq.getContact());
                intent.putExtra("id",enq.getID());
                intent.putExtra("user","Member");
                context.startActivity(intent);
            }
        });
        dialog.show();
    }

    //view for loading on swipe of recyclerview
    public class FooterHolder extends MemberAdapter.BaseViewHolder {

        @BindView(R.id.progressBar)
        ProgressBar mProgressBar;


        FooterHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void clear() {

        }

    }
    //base class for both the views
    public abstract class BaseViewHolder extends RecyclerView.ViewHolder {

        private int mCurrentPosition;

        public BaseViewHolder(View itemView) {
            super(itemView);
        }

        protected abstract void clear();

        public void onBind(int position) {
            mCurrentPosition = position;
            clear();
        }

        public int getCurrentPosition() {
            return mCurrentPosition;
        }

    }
    private void makeattendanceclass() {
        MakeAttendanceTrackclass ru = new MakeAttendanceTrackclass();
        ru.execute("5");
    }
    class MakeAttendanceTrackclass extends AsyncTask<String, Void, String> {

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
            MakeAttendanceDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> MakeAttendanceDetails = new HashMap<String, String>();
            MakeAttendanceDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId((Activity)context));
            MakeAttendanceDetails.put("member_id",member_id);
            MakeAttendanceDetails.put("invoice_id","");
            MakeAttendanceDetails.put("pack_name","");
            MakeAttendanceDetails.put("name",name);
            MakeAttendanceDetails.put("contact",contact);
            MakeAttendanceDetails.put("balance",FinalBalance);
            MakeAttendanceDetails.put("expiry_date",End_Date);
            MakeAttendanceDetails.put("status",status);
            MakeAttendanceDetails.put("mode", "AdminApp");
            MakeAttendanceDetails.put("financial_yr", "");
            MakeAttendanceDetails.put("action", "make_attendence");
            String domainurl=SharedPrefereneceUtil.getDomainUrl((Activity)context);
            String loginResult2 = ruc.sendPostRequest( domainurl+ServiceUrls.LOGIN_URL, MakeAttendanceDetails);
            return loginResult2;
        }
    }
    private void MakeAttendanceDetails(String jsonResponse) {

        JSONObject jsonObjLoginResponse = null;
        try {
            jsonObjLoginResponse = new JSONObject(jsonResponse);
            String success = jsonObjLoginResponse.getString(context.getResources().getString(R.string.success));

            if (success.equalsIgnoreCase(context.getResources().getString(R.string.two))) {
                ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
                Toast.makeText(context,"Your Attendance Marked Successfully",Toast.LENGTH_SHORT).show();
            }

            else if (success.equalsIgnoreCase(context.getResources().getString(R.string.one)))
            {
                Toast.makeText(context,"Please try after 1 hour ,Your attendance is already marked",Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
