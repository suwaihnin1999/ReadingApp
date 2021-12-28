package com.swh1999.readingapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class WorkListAdapter extends RecyclerView.Adapter<WorkListAdapter.myViewHolder> {
    Context context;
    ArrayList<StoryInfo> storylist;
    public WorkListAdapter(ProfileActivity profileActivity, ArrayList<StoryInfo> storylist) {
        context=profileActivity;
        this.storylist=storylist;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.worklist_layout,parent,false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        Glide.with(context).load(storylist.get(holder.getAdapterPosition()).getStoryImg()).into(holder.mImageView);
        holder.mTitle.setText(storylist.get(holder.getAdapterPosition()).getStoryTitleNew());
        holder.mDes.setText(storylist.get(holder.getAdapterPosition()).getStoryDes());
        int totalVote=storylist.get(holder.getAdapterPosition()).getTotalVote();
        if(totalVote>999){
            double tVote=totalVote/1000;
            holder.mVoter.setText(String.format("%.1f",tVote)+"k");
        }
        else {
            holder.mVoter.setText(String.valueOf(totalVote));
        }
        int totalView=storylist.get(holder.getAdapterPosition()).getTotalView();
        if(totalView>999){
            double tVote=totalView/1000;
            holder.mViewer.setText(String.format("%.1f",tVote)+"k");
        }
        else {
            holder.mViewer.setText(String.valueOf(totalView));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,BookDetailActivity.class);
                intent.putExtra("uid",storylist.get(holder.getAdapterPosition()).getUid());
                intent.putExtra("title",storylist.get(holder.getAdapterPosition()).getStoryTitle());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return storylist.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        TextView mViewer,mVoter,mPart,mDes,mTitle;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitle=itemView.findViewById(R.id.worklist_title);
            mImageView=itemView.findViewById(R.id.worklist_bookImg);
            mViewer=itemView.findViewById(R.id.workList_viewer);
            mVoter=itemView.findViewById(R.id.workList_voter);
            mPart=itemView.findViewById(R.id.workList_parts);
            mDes=itemView.findViewById(R.id.worklist_des);
        }
    }
}
