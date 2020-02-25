package com.ndfitnessplus.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.ndfitnessplus.Activity.WorkoutDetailsActivity;
import com.ndfitnessplus.Model.WorkOutDayList;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.Tools;
import com.ndfitnessplus.Utility.ViewAnimation;

import java.util.ArrayList;
import java.util.List;

public class WorkOutAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<WorkOutDayList> items = new ArrayList<>();
    private Context ctx;
    private WorkOutAdapter.OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, WorkOutDayList obj, int position);
    }

    public void setOnItemClickListener(final WorkOutAdapter.OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public WorkOutAdapter(Context context, List<WorkOutDayList> items) {
        this.items = items;
        ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView workout_day;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.image);
            workout_day = (TextView) v.findViewById(R.id.workout_day);
            lyt_parent = (View) v.findViewById(R.id.lyt_parent);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.work_list, parent, false);
        vh = new WorkOutAdapter.OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof WorkOutAdapter.OriginalViewHolder) {
            final WorkOutAdapter.OriginalViewHolder view = (WorkOutAdapter.OriginalViewHolder) holder;

            final WorkOutDayList p = items.get(position);

            view.workout_day.setText(p.getDay());


            view.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(ctx, WorkoutDetailsActivity.class);
                    intent.putExtra("days",p.getDay());
                    intent.putExtra("member_id",p.getMemberId());
                    ctx.startActivity(intent);
                }
            });





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

