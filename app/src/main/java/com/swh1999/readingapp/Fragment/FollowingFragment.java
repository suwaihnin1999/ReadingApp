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


public class FollowingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FollowingFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static FollowingFragment newInstance(String param1, String param2) {
        FollowingFragment fragment = new FollowingFragment();
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
        return inflater.inflate(R.layout.fragment_following, container, false);
    }

    RecyclerView mRecyclerview;
    String uid;
    FollowAdapter followAdapter;
    ArrayList<String> followingList;
    ArrayList<ProfileInfo> followingInfoList;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        uid= FollowActivity.followUid;
        Log.e("gg","follow Uid="+uid);

        followingList=new ArrayList<>();
        followingInfoList=new ArrayList<>();

        mRecyclerview=getView().findViewById(R.id.fragmentFollowing_recycler);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        followAdapter=new FollowAdapter(getContext(),followingInfoList);
        mRecyclerview.setAdapter(followAdapter);



        getFollowingList();
    }

    private void getFollowingList() {
        FirebaseDatabase.getInstance().getReference("Follow").child(uid)
                .child("Following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingList.clear();
                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    Log.e("gg","following id="+snapshot1.getKey());
                    followingList.add(snapshot1.getKey());
                }

                getFollowingInfoList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowingInfoList() {
        FirebaseDatabase.getInstance().getReference("Profile")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        followingInfoList.clear();
                        for(DataSnapshot snapshot1:snapshot.getChildren()){
                            ProfileInfo pI=snapshot1.getValue(ProfileInfo.class);
                            for(String Fuid:followingList){
                                Log.e("gg","followingId="+Fuid+" profile key="+snapshot1.getKey());
                                if(Fuid.equals(snapshot1.getKey())){
                                    Log.e("gg",Fuid);
                                    Log.e("gg",pI.getUsername());
                                    followingInfoList.add(pI);
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
}