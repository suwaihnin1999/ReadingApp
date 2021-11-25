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

import java.util.ArrayList;

public class SimpleBookListAdapter extends RecyclerView.Adapter<SimpleBookListAdapter.myViewHolder> {
    Context context;
    ArrayList<StoryInfo> mSimpleBookList;
    public SimpleBookListAdapter(BookDetailActivity bookDetailActivity, ArrayList<StoryInfo> mSimpleBookList) {
        context=bookDetailActivity;
        this.mSimpleBookList=mSimpleBookList;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.trendingbook_layout,parent,false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        holder.mTextView.setText(mSimpleBookList.get(position).getStoryTitleNew());
        Glide.with(context).load(mSimpleBookList.get(position).getStoryImg()).into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mSimpleBookList.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        TextView mTextView;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView=itemView.findViewById(R.id.trendingbook_img);
            mTextView=itemView.findViewById(R.id.trendingbook_title);
        }
    }
}
