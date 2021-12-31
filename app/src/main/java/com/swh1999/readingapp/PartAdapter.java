package com.swh1999.readingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class PartAdapter extends RecyclerView.Adapter<PartAdapter.myViewHolder> {
    Context context;
    List<StoryPartInfo> partList;
    onItemClickListener listener;
    public PartAdapter(MyStoryDetailActivity myStoryDetailActivity, List<StoryPartInfo> partList, onItemClickListener listener) {
        context=myStoryDetailActivity;
        this.partList=partList;
        this.listener=listener;
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
        String privacy=partList.get(position).getPrivacy();
        if(privacy.equals("public")){
            Glide.with(context).load(R.drawable.worldwide).into(holder.mPartPrivacy);

        }
        else{
            Glide.with(context).load(R.drawable.ic_lock).into(holder.mPartPrivacy);
        }
        holder.bind(partList.get(position),listener);
    }

    @Override
    public int getItemCount() {
        return partList.size();
    }

    public interface onItemClickListener{
        public void onItemClick(StoryPartInfo storyPartInfo);
    }



    public class myViewHolder extends RecyclerView.ViewHolder {
        TextView mPartTitle;
        ImageView mPartPrivacy;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            mPartTitle=itemView.findViewById(R.id.partLayout_partTitle);
            mPartPrivacy=itemView.findViewById(R.id.partLayout_privacy);
        }

        public void bind(StoryPartInfo storyPartInfo, onItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(storyPartInfo);
                }
            });

        }
    }
}
