package com.swh1999.readingapp.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.swh1999.readingapp.AddStoriesActivity;
import com.swh1999.readingapp.R;
import com.swh1999.readingapp.StoryAdapter;
import com.swh1999.readingapp.StoryInfo;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class AddStories_Fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddStories_Fragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static AddStories_Fragment newInstance(String param1, String param2) {
        AddStories_Fragment fragment = new AddStories_Fragment();
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
        return inflater.inflate(R.layout.fragment_add_stories_, container, false);
    }

    FloatingActionButton mAddStory;
    RecyclerView mStoriesRecycler;
    DatabaseReference reff;
    FirebaseAuth fAuth;
    String uid;
    List<StoryInfo> storyList;
    ProgressBar mProgressBar;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inits();

        //todo get current user's id
        fAuth=FirebaseAuth.getInstance();
        uid=fAuth.getCurrentUser().getUid();

        mAddStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), AddStoriesActivity.class);
                startActivity(intent);
            }
        });

        //todo set layoutManager for recyclerview
        mStoriesRecycler.setLayoutManager(new GridLayoutManager(getContext(),2));
        storyList=new ArrayList<>();
        reff=FirebaseDatabase.getInstance().getReference("Story").child(uid);
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                storyList.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    StoryInfo storyInfo=snapshot1.getValue(StoryInfo.class);
                    storyList.add(storyInfo);
                }
                mStoriesRecycler.setAdapter(new StoryAdapter(getContext(),storyList));
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void inits() {
       mAddStory=getView().findViewById(R.id.addStories_addBtn);
       mStoriesRecycler=getView().findViewById(R.id.addStories_recyclerview);
       mProgressBar=getView().findViewById(R.id.addStories_progressbar);
    }
}