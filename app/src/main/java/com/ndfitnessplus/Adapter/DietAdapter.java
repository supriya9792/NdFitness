package com.ndfitnessplus.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
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
import com.ndfitnessplus.Activity.BranchSelectionActivity;
import com.ndfitnessplus.Activity.EnquiryActivity;
import com.ndfitnessplus.Activity.EnquiryFollowupDetailsActivity;
import com.ndfitnessplus.Activity.FullImageActivity;
import com.ndfitnessplus.Activity.MainActivity;
import com.ndfitnessplus.Model.DietList;
import com.ndfitnessplus.Model.DietList;
import com.ndfitnessplus.Model.POSSellList;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.ServiceUrls;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ndfitnessplus.Activity.SelectDomainActivity.TAG;

public class DietAdapter extends RecyclerView.Adapter<DietAdapter.BaseViewHolder> {
    ArrayList<DietList> arrayList;
    private ArrayList<DietList> subList;
    Context context;

    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;

    public DietAdapter( Context context,ArrayList<DietList> enquiryList) {
        //this.arrayList = enquiryList;
        this.subList = enquiryList;
        this.arrayList = new ArrayList<DietList>();
        this.context = context;
        this.arrayList.addAll(enquiryList);
    }

    @Override
    public DietAdapter.BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(EnquiryActivity.TAG, "view type: "+viewType );
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new DietAdapter.ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.diet_list, parent, false));
            case VIEW_TYPE_LOADING:
                return new DietAdapter.FooterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false));
            default:
                return null;
        }

    }

    @Override
    public void onBindViewHolder(DietAdapter.BaseViewHolder holder, int position) {
        holder.onBind(position);

        Log.d(EnquiryActivity.TAG, "call onbind method of viewholder: " );
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
    public void add(DietList response) {
        arrayList.add(response);
        //subList.add(response);
        Log.d(EnquiryActivity.TAG, "sublist size after add : "+String.valueOf(subList.size()) );
        notifyItemInserted(arrayList.size() - 1);
    }

    public void addAll(ArrayList<DietList> postItems) {
        for (DietList response : postItems) {
            add(response);
            subList.add(response);
        }

        Log.d(EnquiryActivity.TAG, "arraylist size after adding new data: "+String.valueOf(arrayList.size()) );

    }


    private void remove(DietList postItems) {
        int position = arrayList.indexOf(postItems);
        if (position > -1) {
            arrayList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void addLoading() {
        isLoaderVisible = false;
        add(new DietList());

    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = arrayList.size() - 1;
        DietList item = getItem(position);
        if (item != null) {
            arrayList.remove(position);
            notifyItemRemoved(position);
            // notifyDataSetChanged();
        }
    }
    public void removeblank(){
        isLoaderVisible = false;
        int position = arrayList.size() - 1;
        DietList item = getItem(position);
        if (item != null) {
            arrayList.remove(position);
            notifyItemRemoved(position);
            //notifyDataSetChanged();
        }
    }
    public void clear() {
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }
    DietList getItem(int position) {
        return arrayList.get(position);
    }
    //filter for search
    public int filter(String charText) {
        // subList=arrayList;

        charText = charText.toLowerCase(Locale.getDefault());
        Log.d(EnquiryActivity.TAG, "sublist size whentext  filter: "+String.valueOf(subList.size()) );
        arrayList.clear();
        if (charText.length() == 0) {

            arrayList.addAll(subList);
        } else {
            for (DietList wp : subList) {
                if (wp.getName().toLowerCase(Locale.getDefault())
                        .contains(charText) || wp.getDietitionName().toLowerCase(Locale.getDefault()).contains(charText)
                        ||wp.getContact().toLowerCase(Locale.getDefault()).contains(charText)) {
                    arrayList.add(wp);
                    // return arrayList.size();
                }
            }
        }
        notifyDataSetChanged();
        return arrayList.size();
        //Log.d(TAG, "sublist size filter: "+String.valueOf(subList.size()) );

    }
    //filter for search
    public int search( String charTex,final ArrayList<DietList> subList) {
        // subList=arrayList;

        final String charText = charTex.toLowerCase(Locale.getDefault());
        Log.d(EnquiryActivity.TAG, "sublist size whentext  filter: "+String.valueOf(subList.size()) );
        arrayList.clear();
        if (charText.length() == 0) {

            arrayList.addAll(subList);
            return 0;
        } else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                public void run() {
                    // UI code goes here
                    for (final DietList wp : subList) {
                        if (wp.getName().toLowerCase(Locale.getDefault())
                                .contains(charText) || wp.getDietitionName().toLowerCase(Locale.getDefault()).contains(charText)
                                ||wp.getContact().toLowerCase(Locale.getDefault()).contains(charText)) {
                            arrayList.add(wp);
                            // return arrayList.size();
                        }
                    }
                }
            });



        }
        Log.d(EnquiryActivity.TAG, "sublist size search: "+String.valueOf(subList.size()) );
        notifyDataSetChanged();
        return arrayList.size();
    }
    //View for showing enquiry
    public class ViewHolder extends DietAdapter.BaseViewHolder implements View.OnClickListener {
        TextView nameTV,dietition_TV,diet_dateTV,diet_idTV,purposeTv,advoiceTV,start_to_end_date_TV,contactTV,chargesTV;
        //  ImageView contactIV;
        CircularImageView imageView;
        View layoutparent;
        public ViewHolder(View itemView) {
            super(itemView);

            contactTV = (TextView) itemView.findViewById(R.id.contactTV);
            purposeTv = (TextView) itemView.findViewById(R.id.purposeTv);
            nameTV = (TextView) itemView.findViewById(R.id.nameTV);
            // contactIV = (ImageView) itemView.findViewById(R.id.contactIV);
            imageView=(CircularImageView) itemView.findViewById(R.id.input_image);
            // contactIV.setOnClickListener(this);
            dietition_TV = (TextView) itemView.findViewById(R.id.dietition_TV);
            diet_dateTV = (TextView) itemView.findViewById(R.id.diet_dateTV);
            diet_idTV = (TextView) itemView.findViewById(R.id.diet_idTV);
            advoiceTV = (TextView) itemView.findViewById(R.id.advoiceTV);
            start_to_end_date_TV = (TextView) itemView.findViewById(R.id.start_to_end_date_TV);
            chargesTV = (TextView) itemView.findViewById(R.id.chargesTV);
            layoutparent=(View)itemView.findViewById(R.id.lyt_parent);
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
            final DietList enq = arrayList.get(position);
            //Log.d(TAG, "enquiry name: " + enq.getName());
            // idTV.setText(enq.getID());
            nameTV.setText(enq.getName());
            //Log.d(TAG, "textview name: " + nameTV.getText().toString());
            contactTV.setText(enq.getContactEncrypt());
            dietition_TV.setText(enq.getDietitionName());
            diet_dateTV.setText(enq.getDietStartDate());
            diet_idTV.setText(enq.getDietId());
            advoiceTV.setText(enq.getAdvoice());
            purposeTv.setText(enq.getPurpose());
            start_to_end_date_TV.setText(enq.getDietStartToEndDate());
            chargesTV.setText(enq.getCharges());

            // Glide.with(context).load(url).placeholder(R.drawable.nouser).into(imageView);




        }
    }

    //view for loading on swipe of recyclerview
    public class FooterHolder extends DietAdapter.BaseViewHolder {

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
}



