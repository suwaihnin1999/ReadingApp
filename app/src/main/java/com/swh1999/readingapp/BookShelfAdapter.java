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

public class BookShelfAdapter extends RecyclerView.Adapter<BookShelfAdapter.myViewHolder> {
    Context context;
    ArrayList<StoryInfo> mLibraryList;
    onItemClickListener listener;
    public BookShelfAdapter(Context context, ArrayList<StoryInfo> mLibraryList,onItemClickListener listener) {
        this.context=context;
        this.mLibraryList=mLibraryList;
        this.listener=listener;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.library_layout,parent,false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        holder.mTextView.setText(mLibraryList.get(position).getStoryTitleNew());
        Glide.with(context).load(mLibraryList.get(position).getStoryImg()).into(holder.mImageView);
        holder.bind(mLibraryList.get(position),listener);

    }

    @Override
    public int getItemCount() {
        return mLibraryList.size();
    }

    public interface onItemClickListener{
        void onItemClick(StoryInfo storyInfo);
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        TextView mTextView;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView=itemView.findViewById(R.id.library_bookImg);
            mTextView=itemView.findViewById(R.id.library_bookTitle);
        }

        public void bind(StoryInfo storyInfo, onItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(storyInfo);
                }
            });
        }
    }
}
