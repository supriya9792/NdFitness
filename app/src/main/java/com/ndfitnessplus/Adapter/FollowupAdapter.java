package com.ndfitnessplus.Adapter;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ndfitnessplus.Activity.AddEnquiryActivity;
import com.ndfitnessplus.Activity.EnquiryFollowupDetailsActivity;
import com.ndfitnessplus.Activity.MemberDetailsActivity;
import com.ndfitnessplus.Listeners.BaseViewHolder;
import com.ndfitnessplus.Model.EnquiryList;
import com.ndfitnessplus.Model.FollowupList;
import com.ndfitnessplus.Model.MemberDataList;
import com.ndfitnessplus.Model.Spinner_List;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.Utility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ndfitnessplus.Activity.EnquiryActivity.TAG;

public class FollowupAdapter extends RecyclerView.Adapter<BaseViewHolder> {
   public ArrayList<FollowupList> arrayList;
    private ArrayList<FollowupList> subList;
   public Context context;
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;


    String enquiryId;
    public FollowupAdapter(ArrayList<FollowupList> followupList, Context context) {
        arrayList = followupList;
        this.context = context;
        this.subList = followupList;
        this.arrayList = new ArrayList<FollowupList>();
        this.arrayList.addAll(followupList);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new FollowupAdapter.ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.done_followup_list, parent, false));
            case VIEW_TYPE_LOADING:
                return new FollowupAdapter.FooterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible) {
            return position == arrayList.size() - 1 ? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }
    public void add(FollowupList response) {
        arrayList.add(response);
        notifyItemInserted(arrayList.size() - 1);
    }

    public void addAll(ArrayList<FollowupList> postItems) {
        for (FollowupList response : postItems) {
            add(response);
            subList.add(response);
        }

    }


    private void remove(FollowupList postItems) {
        int position = arrayList.indexOf(postItems);
        if (position > -1) {
            arrayList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void addLoading() {
        isLoaderVisible = false;
        add(new FollowupList());

    }

    public void removeLoading() {
        isLoaderVisible = false;
         int position = arrayList.size() - 1;
        FollowupList item = getItem(position);
        if (item != null) {
            arrayList.remove(position);
            notifyItemRemoved(position);
          }
    }
    public void removeblank(){
        isLoaderVisible = false;
        int position = arrayList.size() - 1;
        FollowupList item = getItem(position);
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
    //filter for search
    public int search( String charTex,final ArrayList<FollowupList> subList) {


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
                    for (final FollowupList wp : subList) {
                        if (wp.getName().toLowerCase(Locale.getDefault())
                                .contains(charText) ||wp.getExecutiveName().toLowerCase(Locale.getDefault()).contains(charText)
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
    FollowupList getItem(int position) {
        return arrayList.get(position);
    }
    public class ViewHolder extends BaseViewHolder implements View.OnClickListener  {
        TextView nameTV,followup_dateTV,nextfollowupdate,commentTV,excecutive_nameTV,ratingTV,callRespondTV,contactTV,foll_typeTV;
        ImageView contactIV;
        View layoutparent;
        public ViewHolder(View itemView) {
            super(itemView);
            nameTV =  itemView.findViewById(R.id.nameTV);
            contactTV =  itemView.findViewById(R.id.contactTV);
            followup_dateTV =  itemView.findViewById(R.id.followup_dateTV);
            nextfollowupdate =  itemView.findViewById(R.id.Nextfollowup_dateTV);
            contactIV =  itemView.findViewById(R.id.contactIV);
            contactIV.setOnClickListener(this);
            commentTV =  itemView.findViewById(R.id.commentTV);
            excecutive_nameTV =  itemView.findViewById(R.id.excecutive_nameTV);
            ratingTV =  itemView.findViewById(R.id.ratingTV);
            callRespondTV =  itemView.findViewById(R.id.callRespondTV);
            foll_typeTV =  itemView.findViewById(R.id.foll_typeTV);
            layoutparent=itemView.findViewById(R.id.lyt_parent);
        }

        @Override
        protected void clear() {

        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.contactIV){
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:"+arrayList.get(getAdapterPosition()).getContact()));
                context.startActivity(dialIntent);
            }

        }
        public void onBind(int position) {
            super.onBind(position);
           final FollowupList enq = arrayList.get(position);
            nameTV.setText(enq.getName());
            contactTV.setText(arrayList.get(position).getContactEncrypt());
            followup_dateTV.setText(enq.getFollowupDate());
            nextfollowupdate.setText(enq.getNextFollowupDate());
            commentTV.setText(enq.getComment());
            excecutive_nameTV.setText(enq.getExecutiveName());
            ratingTV.setText(enq.getRating());
            callRespondTV.setText(enq.getCallRespond());
            foll_typeTV.setText(enq.getFollowupType());
            final MemberDataList subList = new MemberDataList();
            subList.setName(enq.getName());
            subList.setGender("NA");

            try {
                if (!(enq.getContact().equals("null") || enq.getContact().equals(""))) {
                    String cont = Utility.lastFour(enq.getContact());
                    subList.setContactEncrypt(cont);
                    subList.setContact(enq.getContact());
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            subList.setBirthDate("");
            subList.setExecutiveName(enq.getExecutiveName());
            subList.setBlodGroup("NA");
            subList.setOccupation("NA");
            subList.setID(enq.getID());
            String image=enq.getImage();
            subList.setImage(enq.getImage());
            subList.setStatus("");
            subList.setEmail("");

                layoutparent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(enq.getFollowupType().equals("Enquiry")) {
                            enquiryId = enq.getID();
                             if (!enq.getRating().equals("Converted")) {
                                Intent intent = new Intent(context, EnquiryFollowupDetailsActivity.class);
                                intent.putExtra("enquiry_id", enquiryId);
                                intent.putExtra("rating", enq.getRating());
                                intent.putExtra("call_response", enq.getCallRespond());
                                context.startActivity(intent);
                            }
                        }else{
                            String   member_id=enq.getID();
                            Intent intent=new Intent(context, MemberDetailsActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("filter_array_list", subList);
                            intent.putExtra("BUNDLE",bundle);
                            context.startActivity(intent);
                        }
                    }
                });



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

    //filter for search
    public int filter(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());

        arrayList.clear();
        if (charText.length() == 0) {

            arrayList.addAll(subList);
        } else {
            for (FollowupList wp : subList) {
                if (wp.getName().toLowerCase(Locale.getDefault())
                        .contains(charText) || wp.getContact().toLowerCase(Locale.getDefault()).contains(charText)||
                        wp.getCallRespond().toLowerCase(Locale.getDefault()).contains(charText)||wp.getExecutiveName().toLowerCase(Locale.getDefault()).contains(charText)
                        ||wp.getComment().toLowerCase(Locale.getDefault()).contains(charText)) {
                    arrayList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
        return  arrayList.size();
    }

}

