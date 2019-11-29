package com.ndfitnessplus.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ndfitnessplus.Activity.WorkoutDetailsActivity;
import com.ndfitnessplus.Activity.WorkoutDetailsDescriptionActivity;
import com.ndfitnessplus.Model.WorkOutDetailsList;
import com.ndfitnessplus.Model.WorkOutDetailsList;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.ServiceUrls;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;
import com.ndfitnessplus.Utility.Tools;
import com.ndfitnessplus.Utility.ViewAnimation;

import java.util.ArrayList;
import java.util.List;

public class WorkoutLevelDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_ITEM = 1;
    private final int VIEW_SECTION = 0;

    private List<WorkOutDetailsList> items = new ArrayList<>();
    private Context ctx;
    private WorkoutLevelDetailsAdapter.OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, WorkOutDetailsList obj, int position);
    }

    public void setOnItemClickListener(final WorkoutLevelDetailsAdapter.OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public WorkoutLevelDetailsAdapter(Context context, List<WorkOutDetailsList> items) {
        this.items = items;
        ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
//        public ImageView image;
        public TextView mulsclegrpTv,workoutnameTv,setTv,repitationsTv,timeTv;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
//            image = (ImageView) v.findViewById(R.id.image);
            mulsclegrpTv = (TextView) v.findViewById(R.id.musclegrpTV);
            workoutnameTv = (TextView) v.findViewById(R.id.workout_nameTV);
            setTv = (TextView) v.findViewById(R.id.setsTV);
            repitationsTv = (TextView) v.findViewById(R.id.repitationsTV);
            timeTv = (TextView) v.findViewById(R.id.timeTV);
            lyt_parent = (View) v.findViewById(R.id.lyt_parent);
        }
    }

    public static class SectionViewHolder extends RecyclerView.ViewHolder {
        public TextView title_section;

        public SectionViewHolder(View v) {
            super(v);
            title_section = (TextView) v.findViewById(R.id.title_section);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_level_details, parent, false);
            vh = new WorkoutLevelDetailsAdapter.OriginalViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_section, parent, false);
            vh = new WorkoutLevelDetailsAdapter.SectionViewHolder(v);
        }
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final   WorkOutDetailsList p = items.get(position);
        if (holder instanceof WorkoutLevelDetailsAdapter.OriginalViewHolder) {
            WorkoutLevelDetailsAdapter.OriginalViewHolder view = (WorkoutLevelDetailsAdapter.OriginalViewHolder) holder;

            //final WorkOutDetailsList p = items.get(position);

            view.mulsclegrpTv.setText(p.getBodyPart());
            view.workoutnameTv.setText(p.getWorkoutName());
            view.setTv.setText(p.getSet());
            view.repitationsTv.setText(p.getRepitation());
            view.timeTv.setText(p.getTime());



//            view.lyt_parent.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent=new Intent(ctx, WorkoutDetailsActivity.class);
//                    intent.putExtra("days",p.getDay());
//                    intent.putExtra("member_id",p.getMemberId());
//                    ctx.startActivity(intent);
//                }
//            });

        } else {
            WorkoutLevelDetailsAdapter.SectionViewHolder view = (WorkoutLevelDetailsAdapter.SectionViewHolder) holder;
            view.title_section.setText(p.getDay());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return this.items.get(position).section ? VIEW_SECTION : VIEW_ITEM;
    }

    public void insertItem(int index, WorkOutDetailsList people){
        items.add(index, people);
        notifyItemInserted(index);
    }

}
