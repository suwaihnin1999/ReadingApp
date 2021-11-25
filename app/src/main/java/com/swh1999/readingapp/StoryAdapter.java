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

import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.myViewHolder> {
    Context context;
    List<StoryInfo> storyList;
    public StoryAdapter(Context context, List<StoryInfo> storyList) {
        this.context=context;
        this.storyList=storyList;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.story_layout,parent,false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        holder.mStoryTitle.setText(storyList.get(position).getStoryTitleNew());
        Glide.with(context).load(storyList.get(position).getStoryImg()).into(holder.mStoryImg);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,MyStoryDetailActivity.class);
                intent.putExtra("storyTitle",storyList.get(position).getStoryTitle());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return storyList.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        ImageView mStoryImg;
        TextView mStoryTitle;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            mStoryImg=itemView.findViewById(R.id.story_img);
            mStoryTitle=itemView.findViewById(R.id.story_title);
        }
    }
}
