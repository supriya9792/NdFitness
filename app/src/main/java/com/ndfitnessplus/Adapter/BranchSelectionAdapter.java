package com.ndfitnessplus.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import com.mikhaellopez.circularimageview.CircularImageView;
import com.ndfitnessplus.Activity.BranchSelectionActivity;
import com.ndfitnessplus.Activity.MainActivity;
import com.ndfitnessplus.Activity.MemberDetailsActivity;
import com.ndfitnessplus.Model.BranchList;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.ServiceUrls;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.ndfitnessplus.Activity.SelectDomainActivity.TAG;
import static com.ndfitnessplus.Utility.Constants.SELECTED_BRANCH_ID;

public class BranchSelectionAdapter extends  RecyclerView.Adapter<BranchSelectionAdapter.MyView>  {

    private Context mContext;
    private List<BranchList> subList;
    private ArrayList<BranchList> arraylist;

    public class MyView extends RecyclerView.ViewHolder   {
        TextView branch_nameTV,dailyCollectionTV,monthlyCollectionTV,locationCityTv,statusTV;
        ImageView contactIV;
        CircularImageView imageView;
        View parent;
        public MyView(View view) {
            super(view);
            branch_nameTV = (TextView) itemView.findViewById(R.id.branch_nameTV);
            locationCityTv = (TextView) itemView.findViewById(R.id.locationCityTv);
            dailyCollectionTV = (TextView) itemView.findViewById(R.id.dailyCollectionTV);
            monthlyCollectionTV = (TextView) itemView.findViewById(R.id.monthlyCollectionTV);
            statusTV = (TextView) itemView.findViewById(R.id.statusTv);
            parent=(View)itemView.findViewById(R.id.layout);
            contactIV = (ImageView) itemView.findViewById(R.id.contactIV);
            imageView = (CircularImageView) itemView.findViewById(R.id.branchImage);

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

        //holder.branchImage.setImageDrawable(drawable);
        String domainurl= SharedPrefereneceUtil.getDomainUrl((Activity)mContext);
        String url= domainurl+ServiceUrls.IMAGES_URL + enq.getImage();
        Log.d(TAG, "url: " +url);
        Glide.with(mContext).load(url).placeholder(R.drawable.nouser).into(holder.imageView);
        holder.branch_nameTV.setText(enq.getBranchName());
//        holder.contactTV.setText(branch.get(position).getContactNumber());
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


    }


    @Override
    public int getItemCount() {
        return subList.size();
    }

}


