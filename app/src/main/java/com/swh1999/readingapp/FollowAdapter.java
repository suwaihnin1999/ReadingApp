package com.swh1999.readingapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.myViewHolder> {
    Context context;
    ArrayList<ProfileInfo> followerInfoList;
    public FollowAdapter(Context context, ArrayList<ProfileInfo> followerInfoList) {
        this.context=context;
        this.followerInfoList=followerInfoList;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.follow_layout,parent,false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        Log.e("gg","followAdapter="+followerInfoList.get(position).getUsername());
        Glide.with(context).load(followerInfoList.get(position).getProfileImg()).into(holder.mImageView);
        holder.mUsername.setText(followerInfoList.get(position).getUsername());
    }

    @Override
    public int getItemCount() {
        return followerInfoList.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        CircleImageView mImageView;
        TextView mUsername;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView=itemView.findViewById(R.id.followLayout_circleImageView);
            mUsername=itemView.findViewById(R.id.followLayout_username);
        }
    }
}
