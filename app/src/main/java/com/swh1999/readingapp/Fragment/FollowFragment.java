package com.swh1999.readingapp.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.swh1999.readingapp.FollowActivity;
import com.swh1999.readingapp.FollowAdapter;
import com.swh1999.readingapp.ProfileInfo;
import com.swh1999.readingapp.R;

import java.util.ArrayList;


public class FollowFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FollowFragment() {
        // Required empty public constructor
    }


    public static FollowFragment newInstance(String param1, String param2) {
        FollowFragment fragment = new FollowFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_follow, container, false);
    }

    String uid;
    RecyclerView mRecyclerview;
    ArrayList<String> followerList;
    ArrayList<ProfileInfo> followerInfoList;
    FollowAdapter followAdapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        uid= FollowActivity.followUid;


        followerList=new ArrayList<>();
        followerInfoList=new ArrayList<>();

        mRecyclerview=getView().findViewById(R.id.fragmentFollow_recyclerview);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        followAdapter=new FollowAdapter(getContext(),followerInfoList);
        mRecyclerview.setAdapter(followAdapter);
        getFollowerList();



    }

    private void getFollowerInfo(){
        FirebaseDatabase.getInstance().getReference("Profile")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followerInfoList.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    ProfileInfo pf=snapshot1.getValue(ProfileInfo.class);
                    for(String id:followerList){
                        if(id.equals(snapshot1.getKey())){
                            followerInfoList.add(pf);
                        }
                    }
                }
                followAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void getFollowerList() {
        FirebaseDatabase.getInstance().getReference("Follow").child(uid).child("Follower")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        followerList.clear();
                        for (DataSnapshot snapshot1:snapshot.getChildren()){
                            Log.e("gg","follower="+snapshot1.getKey().toString());
                            followerList.add(snapshot1.getKey());
                        }
                        getFollowerInfo();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}