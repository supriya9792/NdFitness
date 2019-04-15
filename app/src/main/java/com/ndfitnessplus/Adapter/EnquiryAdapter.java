package com.ndfitnessplus.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.ndfitnessplus.Activity.EnquiryActivity;
import com.ndfitnessplus.Model.BranchList;
import com.ndfitnessplus.Model.EnquiryList;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.ServiceUrls;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ndfitnessplus.Activity.EnquiryActivity.TAG;

public class EnquiryAdapter extends RecyclerView.Adapter<EnquiryAdapter.BaseViewHolder> {
    ArrayList<EnquiryList> arrayList;
    private ArrayList<EnquiryList> subList;
    Context context;

    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;

    public EnquiryAdapter(ArrayList<EnquiryList> enquiryList, Context context) {
        //this.arrayList = enquiryList;
        this.subList = enquiryList;
        this.arrayList = new ArrayList<EnquiryList>();
        this.context = context;
        this.arrayList.addAll(enquiryList);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "view type: "+viewType );
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.enquiry_list_item, parent, false));
            case VIEW_TYPE_LOADING:
                return new FooterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false));
            default:
                return null;
        }

    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
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
    public void add(EnquiryList response) {
        arrayList.add(response);
        //subList.add(response);
        Log.d(TAG, "sublist size after add : "+String.valueOf(subList.size()) );
        notifyItemInserted(arrayList.size() - 1);
    }

    public void addAll(ArrayList<EnquiryList> postItems) {
        for (EnquiryList response : postItems) {
            add(response);
            subList.add(response);
        }

        Log.d(TAG, "arraylist size after adding new data: "+String.valueOf(arrayList.size()) );

    }


    private void remove(EnquiryList postItems) {
        int position = arrayList.indexOf(postItems);
        if (position > -1) {
            arrayList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void addLoading() {
        isLoaderVisible = false;
        add(new EnquiryList());

    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = arrayList.size() - 1;
        EnquiryList item = getItem(position);
        if (item != null) {
            arrayList.remove(position);
            notifyItemRemoved(position);
           // notifyDataSetChanged();
        }
    }
    public void removeblank(){
        isLoaderVisible = false;
         int position = arrayList.size() - 1;
        EnquiryList item = getItem(position);
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
    EnquiryList getItem(int position) {
        return arrayList.get(position);
    }
    //filter for search
    public void filter(String charText) {
       // subList=arrayList;

        charText = charText.toLowerCase(Locale.getDefault());
        Log.d(TAG, "sublist size whentext  filter: "+String.valueOf(subList.size()) );
        arrayList.clear();
        if (charText.length() == 0) {

            arrayList.addAll(subList);
        } else {
            for (EnquiryList wp : subList) {
                if (wp.getName().toLowerCase(Locale.getDefault())
                        .contains(charText) || wp.getGender().toLowerCase(Locale.getDefault()).contains(charText)||
                         wp.getAddress().toLowerCase(Locale.getDefault()).contains(charText)||wp.getExecutiveName().toLowerCase(Locale.getDefault()).contains(charText)
                        ||wp.getComment().toLowerCase(Locale.getDefault()).contains(charText)) {
                    arrayList.add(wp);
                }
            }
        }
        Log.d(TAG, "sublist size filter: "+String.valueOf(subList.size()) );
        notifyDataSetChanged();
    }
    //filter for search
    public int search( String charTex,final ArrayList<EnquiryList> subList) {
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
                    for (final EnquiryList wp : subList) {
                        if (wp.getName().toLowerCase(Locale.getDefault())
                                .contains(charText) || wp.getGender().toLowerCase(Locale.getDefault()).contains(charText)||
                                wp.getAddress().toLowerCase(Locale.getDefault()).contains(charText)||wp.getExecutiveName().toLowerCase(Locale.getDefault()).contains(charText)
                                ||wp.getComment().toLowerCase(Locale.getDefault()).contains(charText)) {
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
    public class ViewHolder extends BaseViewHolder implements View.OnClickListener {
        TextView nameTV,excecutive_nameTV,commentTV,nextFollowupdateTV,idTV;
        ImageView contactIV;
        CircularImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);

            //idTV = (TextView) itemView.findViewById(R.id.idTV);
            nameTV = (TextView) itemView.findViewById(R.id.nameTV);
            contactIV = (ImageView) itemView.findViewById(R.id.contactIV);
            imageView=(CircularImageView) itemView.findViewById(R.id.input_image);
            contactIV.setOnClickListener(this);
            excecutive_nameTV = (TextView) itemView.findViewById(R.id.excecutive_nameTV);
            commentTV = (TextView) itemView.findViewById(R.id.commentTV);
            nextFollowupdateTV = (TextView) itemView.findViewById(R.id.nextFollowupdateTV);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.contactIV){
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:"+arrayList.get(getAdapterPosition()).getContact()));
                context.startActivity(dialIntent);
            }
        }
        @Override
        protected void clear() {

        }

        public void onBind(int position) {
            super.onBind(position);
            EnquiryList enq = arrayList.get(position);
            //Log.d(TAG, "enquiry name: " + enq.getName());
           // idTV.setText(enq.getID());
            nameTV.setText(enq.getName());
            //Log.d(TAG, "textview name: " + nameTV.getText().toString());
//        contactTV.setText(enq.getContact());
            excecutive_nameTV.setText(enq.getExecutiveName());
            commentTV.setText(enq.getComment());
            nextFollowupdateTV.setText(enq.getNextFollowUpDate());
            String url= ServiceUrls.IMAGES_URL + enq.getImage();
            Log.d(TAG, "image url: "+url );
            Glide.with(context).load(url).placeholder(R.drawable.nouser).into(imageView);
        }
    }
    //view for loading on swipe of recyclerview
    public class FooterHolder extends BaseViewHolder {

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
