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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ndfitnessplus.Activity.BranchSelectionActivity;
import com.ndfitnessplus.Activity.MainActivity;
import com.ndfitnessplus.Model.BranchList;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;

import java.util.ArrayList;
import java.util.List;

import static com.ndfitnessplus.Utility.Constants.SELECTED_BRANCH_ID;

public class BranchSelectionAdapter extends  RecyclerView.Adapter<BranchSelectionAdapter.MyView>  {

    private Context mContext;
    private List<BranchList> subList;
    private ArrayList<BranchList> arraylist;

    public class MyView extends RecyclerView.ViewHolder implements View.OnClickListener  {
        TextView branch_nameTV,dailyCollectionTV,monthlyCollectionTV,locationCityTv;
        ImageView branchImage,contactIV;
        public MyView(View view) {
            super(view);
            branch_nameTV = (TextView) itemView.findViewById(R.id.branch_nameTV);
            locationCityTv = (TextView) itemView.findViewById(R.id.locationCityTv);
            dailyCollectionTV = (TextView) itemView.findViewById(R.id.dailyCollectionTV);
            monthlyCollectionTV = (TextView) itemView.findViewById(R.id.monthlyCollectionTV);

            contactIV = (ImageView) itemView.findViewById(R.id.contactIV);
            branchImage = (ImageView) itemView.findViewById(R.id.branchImage);
           view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            SharedPrefereneceUtil.setSelectedBranchId((Activity) mContext,arraylist.get(getAdapterPosition()).getBranchId());
            SharedPrefereneceUtil.setCompanyName((Activity) mContext,arraylist.get(getAdapterPosition()).getBranchName());
            SharedPrefereneceUtil.setSelectedBranch((Activity) mContext,arraylist.get(getAdapterPosition()).getCity());

            Intent intent = new Intent(mContext, MainActivity.class);
            mContext.startActivity(intent);
            ((BranchSelectionActivity) mContext).finish();
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

        holder.branchImage.setImageDrawable(drawable);
        holder.branch_nameTV.setText(enq.getBranchName());
//        holder.contactTV.setText(branch.get(position).getContactNumber());
        holder.locationCityTv.setText(enq.getCity());


        holder.contactIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:"+enq.getContactNumber()));
                mContext.startActivity(dialIntent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return subList.size();
    }

}


