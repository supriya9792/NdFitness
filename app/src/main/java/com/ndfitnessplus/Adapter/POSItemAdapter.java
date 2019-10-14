package com.ndfitnessplus.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
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
import com.ndfitnessplus.Model.EnquiryList;
import com.ndfitnessplus.Model.POSItemList;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.ServiceUrls;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ndfitnessplus.Activity.EnquiryActivity.TAG;

public class POSItemAdapter extends RecyclerView.Adapter<POSItemAdapter.BaseViewHolder> {
    ArrayList<POSItemList> arrayList;
    private ArrayList<POSItemList> subList;
    Context context;

    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;

    public POSItemAdapter(ArrayList<POSItemList> enquiryList, Context context) {
        //this.arrayList = enquiryList;
        this.subList = enquiryList;
        this.arrayList = new ArrayList<POSItemList>();
        this.context = context;
        this.arrayList.addAll(enquiryList);
    }

    @Override
    public POSItemAdapter.BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "view type: "+viewType );
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new POSItemAdapter.ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.pos_item_list, parent, false));
            case VIEW_TYPE_LOADING:
                return new POSItemAdapter.FooterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false));
            default:
                return null;
        }

    }

    @Override
    public void onBindViewHolder(POSItemAdapter.BaseViewHolder holder, int position) {
        holder.onBind(position);

        Log.d(TAG, "call onbind method of viewholder: " );
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
    public void add(POSItemList response) {
        arrayList.add(response);
        //subList.add(response);
        Log.d(TAG, "sublist size after add : "+String.valueOf(subList.size()) );
        notifyItemInserted(arrayList.size() - 1);
    }

    public void addAll(ArrayList<POSItemList> postItems) {
        for (POSItemList response : postItems) {
            add(response);
            subList.add(response);
        }

        Log.d(TAG, "arraylist size after adding new data: "+String.valueOf(arrayList.size()) );

    }


    private void remove(POSItemList postItems) {
        int position = arrayList.indexOf(postItems);
        if (position > -1) {
            arrayList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void addLoading() {
        isLoaderVisible = false;
        add(new POSItemList());

    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = arrayList.size() - 1;
        POSItemList item = getItem(position);
        if (item != null) {
            arrayList.remove(position);
            notifyItemRemoved(position);
            // notifyDataSetChanged();
        }
    }
    public void removeblank(){
        isLoaderVisible = false;
        int position = arrayList.size() - 1;
        POSItemList item = getItem(position);
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
    POSItemList getItem(int position) {
        return arrayList.get(position);
    }
    //filter for search
    public int filter(String charText) {
        // subList=arrayList;

        charText = charText.toLowerCase(Locale.getDefault());
        Log.d(TAG, "sublist size whentext  filter: "+String.valueOf(subList.size()) );
        arrayList.clear();
        if (charText.length() == 0) {

            arrayList.addAll(subList);
        } else {
            for (POSItemList wp : subList) {
                if (wp.getProductCode().toLowerCase(Locale.getDefault())
                        .contains(charText) || wp.getProductName().toLowerCase(Locale.getDefault()).contains(charText)) {
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
    public int search( String charTex,final ArrayList<POSItemList> subList) {
        // subList=arrayList;

        final String charText = charTex.toLowerCase(Locale.getDefault());
        Log.d(TAG, "sublist size whentext  filter: "+String.valueOf(subList.size()) );
        arrayList.clear();
        if (charText.length() == 0) {

            arrayList.addAll(subList);
            return 0;
        } else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                public void run() {
                    // UI code goes here
                    for (final POSItemList wp : subList) {
                        if (wp.getProductCode().toLowerCase(Locale.getDefault())
                                .contains(charText) || wp.getProductName().toLowerCase(Locale.getDefault()).contains(charText)) {
                            arrayList.add(wp);

                        }
                    }
                }
            });



        }
        Log.d(TAG, "sublist size search: "+String.valueOf(subList.size()) );
        notifyDataSetChanged();
        return arrayList.size();
    }
    //View for showing enquiry
    public class ViewHolder extends POSItemAdapter.BaseViewHolder  {
        TextView  prodCodeTV, prodnameTV,prodDiscTV,quantityTV,rateTV,purchaseAmountTV,taxTV,MaxdiscountTV;
        //  ImageView contactIV;
        CircularImageView imageView;
        View layoutparent;
        public ViewHolder(View itemView) {
            super(itemView);

            prodCodeTV = (TextView) itemView.findViewById(R.id.prodcodeTV);
            prodnameTV = (TextView) itemView.findViewById(R.id.ProdnameTV);
            prodDiscTV = (TextView) itemView.findViewById(R.id.prodDiscTV);
            // contactIV = (ImageView) itemView.findViewById(R.id.contactIV);
            imageView=(CircularImageView) itemView.findViewById(R.id.input_image);
            // contactIV.setOnClickListener(this);
            quantityTV = (TextView) itemView.findViewById(R.id.quantityTV);
            rateTV = (TextView) itemView.findViewById(R.id.rateTV);
            purchaseAmountTV = (TextView) itemView.findViewById(R.id.purchaseAmountTV);
            taxTV = (TextView) itemView.findViewById(R.id.taxTV);
            MaxdiscountTV = (TextView) itemView.findViewById(R.id.maxDiscountTV);
            layoutparent=(View)itemView.findViewById(R.id.lyt_parent);
        }

        @Override
        protected void clear() {

        }

        public void onBind(final int position) {
            super.onBind(position);
            final POSItemList enq = arrayList.get(position);
            //Log.d(TAG, "enquiry name: " + enq.getName());
            // idTV.setText(enq.getID());
            prodCodeTV.setText(enq.getProductCode());
            //Log.d(TAG, "textview name: " + nameTV.getText().toString());
            prodnameTV.setText(enq.getProductName());
            prodDiscTV.setText(enq.getProductDisc());
            rateTV.setText(enq.getRate());
            taxTV.setText(enq.getTax());
            purchaseAmountTV.setText(enq.getPurchaseAmount());
            MaxdiscountTV.setText(enq.getMaxDiscount());
            quantityTV.setText(enq.getQuantity());
            String domainurl= SharedPrefereneceUtil.getDomainUrl((Activity)context);
            String url= domainurl+ ServiceUrls.IMAGES_URL + enq.getProductImage();
            Log.d(TAG, "product image: "+enq.getProductImage() );
          //  Glide.with(context).load(url).placeholder(R.drawable.nouser).into(imageView);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.nouser);
            requestOptions.error(R.drawable.nouser);


            Glide.with(context)
                    .setDefaultRequestOptions(requestOptions)
                    .load(url).into(imageView);
            final android.view.animation.Animation animation_1 = android.view.animation.AnimationUtils.loadAnimation(context,R.anim.zoom_out);
            final android.view.animation.Animation animation_2 = android.view.animation.AnimationUtils.loadAnimation(context,R.anim.zoom_in);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            layoutparent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    String   enquiryId=enq.getID();
//                    Intent intent=new Intent(context, EnquiryFollowupDetailsActivity.class);
//                    intent.putExtra("enquiry_id",enquiryId);
//                    intent.putExtra("rating",enq.getRating());
//                    intent.putExtra("call_response",enq.getCallResponse());
//                    context.startActivity(intent);
                }
            });
        }
    }

    //view for loading on swipe of recyclerview
    public class FooterHolder extends POSItemAdapter.BaseViewHolder {

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
