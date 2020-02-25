package com.ndfitnessplus.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ndfitnessplus.Activity.EnquiryFollowupDetailsActivity;
import com.ndfitnessplus.Listeners.BaseViewHolder;
import com.ndfitnessplus.Model.FollowupList;
import com.ndfitnessplus.R;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ndfitnessplus.Activity.EnquiryActivity.TAG;

    public class FollowupDetailsAdapter extends RecyclerView.Adapter<BaseViewHolder> {
        public ArrayList<FollowupList> arrayList;
        private ArrayList<FollowupList> subList;
        public Context context;
        private static final int VIEW_TYPE_LOADING = 0;
        private static final int VIEW_TYPE_NORMAL = 1;
        private boolean isLoaderVisible = false;


        String enquiryId;
    public FollowupDetailsAdapter(ArrayList<FollowupList> followupList, Context context) {
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
        return new FollowupDetailsAdapter.ViewHolder(
        LayoutInflater.from(parent.getContext()).inflate(R.layout.followup_list_item, parent, false));
        case VIEW_TYPE_LOADING:
        return new FollowupDetailsAdapter.FooterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false));
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

        }
        }
    public void clear() {
        while (getItemCount() > 0) {
        remove(getItem(0));
        }
        }
        FollowupList getItem(int position) {
        return arrayList.get(position);
        }
    public class ViewHolder extends BaseViewHolder {
    TextView nameTV,followup_dateTV,nextfollowupdate,commentTV,excecutive_nameTV,ratingTV,callRespondTV;

    View layoutparent;
    public ViewHolder(View itemView) {
        super(itemView);

        followup_dateTV =  itemView.findViewById(R.id.followup_dateTV);
        nextfollowupdate =  itemView.findViewById(R.id.Nextfollowup_dateTV);

        commentTV =  itemView.findViewById(R.id.commentTV);
        excecutive_nameTV =  itemView.findViewById(R.id.excecutive_nameTV);
        ratingTV =  itemView.findViewById(R.id.ratingTV);
        callRespondTV =  itemView.findViewById(R.id.callRespondTV);
        layoutparent=itemView.findViewById(R.id.lyt_parent);
    }

    @Override
    protected void clear() {

    }

    public void onBind(int position) {
        super.onBind(position);
        final FollowupList enq = arrayList.get(position);

        followup_dateTV.setText(enq.getFollowupDate());
        nextfollowupdate.setText(enq.getNextFollowupDate());
        commentTV.setText(enq.getComment());
        excecutive_nameTV.setText(enq.getExecutiveName());
        ratingTV.setText(enq.getRating());
        callRespondTV.setText(enq.getCallRespond());

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
    public void filter(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());
        arrayList.clear();
        if (charText.length() == 0) {

            arrayList.addAll(subList);
        } else {
            for (FollowupList wp : subList) {
                if (wp.getName().toLowerCase(Locale.getDefault())
                        .contains(charText) || wp.getRating().toLowerCase(Locale.getDefault()).contains(charText)||
                        wp.getCallRespond().toLowerCase(Locale.getDefault()).contains(charText)||wp.getExecutiveName().toLowerCase(Locale.getDefault()).contains(charText)
                        ||wp.getComment().toLowerCase(Locale.getDefault()).contains(charText)) {
                    arrayList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

}

