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

import java.util.ArrayList;
import java.util.List;

public class WorkoutLevelAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

private final int VIEW_ITEM = 1;
private final int VIEW_SECTION = 0;

private List<WorkOutDayList> items = new ArrayList<>();
private Context ctx;
private OnItemClickListener mOnItemClickListener;

public interface OnItemClickListener {
    void onItemClick(View view, WorkOutDayList obj, int position);
}

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public WorkoutLevelAdapter(Context context, List<WorkOutDayList> items) {
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
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.work_list, parent, false);
            vh = new OriginalViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_section, parent, false);
            vh = new SectionViewHolder(v);
        }
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final   WorkOutDayList p = items.get(position);
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;

            //final WorkOutDayList p = items.get(position);

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

        } else {
            SectionViewHolder view = (SectionViewHolder) holder;
            view.title_section.setText(p.getLevel());
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

    public void insertItem(int index, WorkOutDayList people){
        items.add(index, people);
        notifyItemInserted(index);
    }

}
