package com.swh1999.readingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.myViewHolder> {
    public static Object onItemClickListener;
    Context context;
    List<ProfileAbout> mProfile;
    onItemClickListener listener;
    public ProfileAdapter(EditProfileActivity editProfileActivity, List<ProfileAbout> mProfile,onItemClickListener listener) {
        context=editProfileActivity;
        this.mProfile=mProfile;
        this.listener=listener;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.aboutprofile_layout,parent,false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        holder.mTitle.setText(mProfile.get(position).getTitle());
        holder.mValue.setText(mProfile.get(position).getValue());
        holder.bind(mProfile.get(position),listener);


    }

    @Override
    public int getItemCount() {
        return mProfile.size();
    }

    public interface onItemClickListener{
        void onItemClick(ProfileAbout item);
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        TextView mTitle,mValue;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitle=itemView.findViewById(R.id.aboutProfile_title);
            mValue=itemView.findViewById(R.id.aboutProfile_value);
        }

        public void bind(ProfileAbout profileAbout, ProfileAdapter.onItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(profileAbout);
                }
            });
        }
    }
}
