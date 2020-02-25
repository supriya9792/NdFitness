package com.ndfitnessplus.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.ndfitnessplus.Activity.POSDetailsTrasactionActivity;
import com.ndfitnessplus.Model.POSSellList;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.ServiceUrls;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ndfitnessplus.Activity.EnquiryActivity.TAG;

public class POSSellAdapter  extends RecyclerView.Adapter<POSSellAdapter.BaseViewHolder> {
    ArrayList<POSSellList> arrayList;
    private ArrayList<POSSellList> subList;
    Context context;

    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;

    public POSSellAdapter(ArrayList<POSSellList> enquiryList, Context context) {
        this.subList = enquiryList;
        this.arrayList = new ArrayList<POSSellList>();
        this.context = context;
        this.arrayList.addAll(enquiryList);
    }

    @Override
    public POSSellAdapter.BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new POSSellAdapter.ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.pos_sell_details, parent, false));
            case VIEW_TYPE_LOADING:
                return new POSSellAdapter.FooterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false));
            default:
                return null;
        }

    }

    @Override
    public void onBindViewHolder(POSSellAdapter.BaseViewHolder holder, int position) {
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
    public void add(POSSellList response) {
        arrayList.add(response);
        notifyItemInserted(arrayList.size() - 1);
    }

    public void addAll(ArrayList<POSSellList> postItems) {
        for (POSSellList response : postItems) {
            add(response);
            subList.add(response);
        }
    }


    private void remove(POSSellList postItems) {
        int position = arrayList.indexOf(postItems);
        if (position > -1) {
            arrayList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void addLoading() {
        isLoaderVisible = false;
        add(new POSSellList());

    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = arrayList.size() - 1;
        POSSellList item = getItem(position);
        if (item != null) {
            arrayList.remove(position);
            notifyItemRemoved(position);
        }
    }
    public void removeblank(){
        isLoaderVisible = false;
        int position = arrayList.size() - 1;
        POSSellList item = getItem(position);
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
    POSSellList getItem(int position) {
        return arrayList.get(position);
    }
    //filter for search
    public int filter(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());
        arrayList.clear();
        if (charText.length() == 0) {

            arrayList.addAll(subList);
        } else {
            for (POSSellList wp : subList) {
                if (wp.getCustContact().toLowerCase(Locale.getDefault())
                        .contains(charText) || wp.getCustName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    arrayList.add(wp);

                }
            }
        }
        notifyDataSetChanged();
        return arrayList.size();

    }
    //filter for search
    public int search( String charTex,final ArrayList<POSSellList> subList) {

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
                    for (final POSSellList wp : subList) {
                        if (wp.getCustContact().toLowerCase(Locale.getDefault())
                                .contains(charText) || wp.getCustName().toLowerCase(Locale.getDefault()).contains(charText)) {
                            arrayList.add(wp);

                        }
                    }
                }
            });

        }
        notifyDataSetChanged();
        return arrayList.size();
    }

    public class ViewHolder extends POSSellAdapter.BaseViewHolder  {
        TextView invoiceTotalTV,executveNameTv,Invoicedatetv,nameTv,contactTv,invoice_idTV;
        View layoutparent;
        public ViewHolder(View itemView) {
            super(itemView);

            executveNameTv =  itemView.findViewById(R.id.excecutive_nameTV);
            Invoicedatetv =  itemView.findViewById(R.id.invoicedateTV);
            invoice_idTV =  itemView.findViewById(R.id.invoice_idTV);
            nameTv =  itemView.findViewById(R.id.nameTV);
            contactTv =  itemView.findViewById(R.id.contactTV);
            invoiceTotalTV =  itemView.findViewById(R.id.invoic_ttlTV);


            layoutparent=(View)itemView.findViewById(R.id.lyt_parent);
        }

        @Override
        protected void clear() {

        }

        public void onBind(final int position) {
            super.onBind(position);
            final POSSellList enq = arrayList.get(position);

            executveNameTv.setText(enq.getSaleExecutive());
            Invoicedatetv.setText(enq.getInvoiceDate());
            nameTv.setText(enq.getCustName());
            contactTv.setText(enq.getContactEncrypt());
            invoiceTotalTV.setText(enq.getTotalAmount());
            invoice_idTV.setText(enq.getInvoiceId());
            layoutparent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context, POSDetailsTrasactionActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("filter_array_list", enq);
                    intent.putExtra("BUNDLE",bundle);
                    context.startActivity(intent);
                }
            });
        }
    }

    //view for loading on swipe of recyclerview
    public class FooterHolder extends POSSellAdapter.BaseViewHolder {

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

