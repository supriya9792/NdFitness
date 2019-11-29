package com.ndfitnessplus.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ndfitnessplus.Activity.WorkoutDetailsDescriptionActivity;
import com.ndfitnessplus.Model.EnquiryList;
import com.ndfitnessplus.Model.WorkOutDayList;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.ServiceUrls;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;
import com.ndfitnessplus.Utility.Tools;
import com.ndfitnessplus.Utility.ViewAnimation;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.ndfitnessplus.Activity.EnquiryActivity.TAG;

public class ShowWorkoutAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //private List<WorkOutDayList> items = new ArrayList<>();
    ArrayList<WorkOutDayList> arrayList;
    private ArrayList<WorkOutDayList> subList;

    private Context ctx;
    private ShowWorkoutAdapter.OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, WorkOutDayList obj, int position);
    }

    public void setOnItemClickListener(final ShowWorkoutAdapter.OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public ShowWorkoutAdapter(Context context, ArrayList<WorkOutDayList> items) {
        this.subList = items;
        this.arrayList = new ArrayList<WorkOutDayList>();
        this.ctx = context;
        this.arrayList.addAll(items);
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView Name,Contact,Date,ExerciseId,LevelName,ExecutiveName;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.input_image);
            Name = (TextView) v.findViewById(R.id.nameTV);
            Contact = (TextView) v.findViewById(R.id.contactTV);
            Date = (TextView) v.findViewById(R.id.reg_dateTV);
            ExerciseId = (TextView) v.findViewById(R.id.exercise_idTV);
            LevelName = (TextView) v.findViewById(R.id.level_nameTV);
            ExecutiveName = (TextView) v.findViewById(R.id.excecutive_nameTV);
            lyt_parent = (View) v.findViewById(R.id.lyt_parent);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_assigned_workout_list, parent, false);
        vh = new ShowWorkoutAdapter.OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ShowWorkoutAdapter.OriginalViewHolder) {
            final ShowWorkoutAdapter.OriginalViewHolder view = (ShowWorkoutAdapter.OriginalViewHolder) holder;

            final WorkOutDayList p = arrayList.get(position);

            view.Name.setText(p.getMemberName());
            view.Contact.setText(p.getMemberContact());
            view.Date.setText(p.getAssignDate());
            view.ExerciseId.setText(p.getExerciseId());
            view.LevelName.setText(p.getLevel());
            view.ExecutiveName.setText(p.getInstructorName());
            String domainurl= SharedPrefereneceUtil.getDomainUrl((Activity)ctx);
            String url= domainurl+ ServiceUrls.IMAGES_URL + p.getMemberImage();

            // Glide.with(context).load(url).placeholder(R.drawable.nouser).into(imageView);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.nouser);
            requestOptions.error(R.drawable.nouser);

            Glide.with(ctx)
                    .setDefaultRequestOptions(requestOptions)
                    .load(url).into(view.image);

//            view.lyt_parent.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent=new Intent(ctx, WorkoutDetailsDescriptionActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("filter_array_list", p);
//                    intent.putExtra("BUNDLE",bundle);
//                    ctx.startActivity(intent);
//                }
//            });

        }
    }

    private boolean toggleLayoutExpand(boolean show, View view, View lyt_expand) {
        Tools.toggleArrow(show, view);
        if (show) {
            ViewAnimation.expand(lyt_expand);
        } else {
            ViewAnimation.collapse(lyt_expand);
        }
        return show;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
    public ArrayList<WorkOutDayList> filter(String charText) {
        // subList=arrayList;

        charText = charText.toLowerCase(Locale.getDefault());
        Log.d(TAG, "sublist size whentext  filter: "+String.valueOf(subList.size()) );
        arrayList.clear();
        if (charText.length() == 0) {
            arrayList.addAll(subList);
        } else {
            for (WorkOutDayList wp : subList) {
                if (wp.getMemberName().toLowerCase(Locale.getDefault())
                        .contains(charText) || wp.getMemberContact().toLowerCase(Locale.getDefault()).contains(charText)||
                        wp.getInstructorName().toLowerCase(Locale.getDefault()).contains(charText)
                       ) {
                    arrayList.add(wp);
                    // return arrayList.size();
                }
            }
        }
        notifyDataSetChanged();
        return arrayList;
        //Log.d(TAG, "sublist size filter: "+String.valueOf(subList.size()) );

    }
}
