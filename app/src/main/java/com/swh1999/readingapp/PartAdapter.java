package com.swh1999.readingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PartAdapter extends RecyclerView.Adapter<PartAdapter.myViewHolder> {
    Context context;
    List<StoryPartInfo> partList;
    public PartAdapter(MyStoryDetailActivity myStoryDetailActivity, List<StoryPartInfo> partList) {
        context=myStoryDetailActivity;
        this.partList=partList;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.part_layout,parent,false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        holder.mPartTitle.setText(partList.get(position).getPartTitle());
    }

    @Override
    public int getItemCount() {
        return partList.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        TextView mPartTitle;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            mPartTitle=itemView.findViewById(R.id.partLayout_partTitle);
        }
    }
}
