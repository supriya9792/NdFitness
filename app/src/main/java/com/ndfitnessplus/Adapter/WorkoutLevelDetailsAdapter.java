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
import com.ndfitnessplus.Activity.WorkoutDetailsDescriptionActivity;
import com.ndfitnessplus.Model.WorkOutDetailsList;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.ServiceUrls;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;
import com.ndfitnessplus.Utility.Tools;
import com.ndfitnessplus.Utility.ViewAnimation;

import java.util.ArrayList;
import java.util.List;

public class WorkoutLevelDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<WorkOutDetailsList> items = new ArrayList<>();


    private Context ctx;
    private WorkOutAdapter.OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, WorkOutDetailsList obj, int position);
    }

    public void setOnItemClickListener(final WorkOutAdapter.OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public WorkoutLevelDetailsAdapter(Context context, List<WorkOutDetailsList> items) {
        this.items = items;
        ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView workout_name, set, repitation, time, musculargroup,levelname,day;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            workout_name = (TextView) v.findViewById(R.id.workout_nameTV);
            set = (TextView) v.findViewById(R.id.setsTV);
            repitation = (TextView) v.findViewById(R.id.repitationsTV);
            time = (TextView) v.findViewById(R.id.timeTV);
            musculargroup = (TextView) v.findViewById(R.id.musclegrpTV);
            levelname = (TextView) v.findViewById(R.id.level_nameTV);
            day = (TextView) v.findViewById(R.id.daysTV);
            lyt_parent = (View) v.findViewById(R.id.lyt_parent);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_level_details, parent, false);
        vh = new WorkoutLevelDetailsAdapter.OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof WorkoutLevelDetailsAdapter.OriginalViewHolder) {
            final WorkoutLevelDetailsAdapter.OriginalViewHolder view = (WorkoutLevelDetailsAdapter.OriginalViewHolder) holder;

            final WorkOutDetailsList p = items.get(position);

            view.workout_name.setText(p.getWorkoutName());
            view.set.setText(p.getSet());
            view.repitation.setText(p.getRepitation());
            view.time.setText(p.getTime());
            view.musculargroup.setText(p.getBodyPart());
            view.levelname.setText(p.getLevelName());
            view.day.setText(p.getDay());
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
        return items.size();
    }
}
