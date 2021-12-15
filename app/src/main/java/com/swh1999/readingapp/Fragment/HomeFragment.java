package com.swh1999.readingapp.Fragment;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.swh1999.readingapp.R;
import com.swh1999.readingapp.StoryInfo;
import com.swh1999.readingapp.StoryViewerInfo;
import com.swh1999.readingapp.TrendingBookAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Chip scienceChip,horrorChip,adventureChip,romanceChip,actionChip,transChip,comedyChip,teenagerChip,highschoolChip;
    ChipGroup chipGroup;
    SearchView mSearchView;
    RecyclerView mTrendingRecycler;
    List<StoryInfo> storyList;
    List<StoryInfo> storyViewerInfos;
    DatabaseReference reff,reffStory;
    FirebaseAuth fAuth;
    String uid;
    int temp;
    ProgressBar mProgressbar;

    public HomeFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inits();
        storyList=new ArrayList<>();
        storyViewerInfos=new ArrayList<>();
        chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                for(int i=0;i<group.getChildCount();i++){
                    Chip chip=group.findViewById(i);
                    if(chip!=null){
                        if(chip.isChecked()==true){
                            mSearchView.setQuery(mSearchView.getQuery().toString()+chip.getText()+",",false);
                        }
                    }

                }

            }
        });

        //todo get current user's uid
        fAuth=FirebaseAuth.getInstance();
        uid=fAuth.getCurrentUser().getUid();


        mTrendingRecycler.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));

        //todo get viewer 10000 story Name
        reff=FirebaseDatabase.getInstance().getReference("Story");
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                storyViewerInfos.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    if(Integer.parseInt(snapshot1.child("totalView").getValue().toString())>100000){
                        storyViewerInfos.add(snapshot1.getValue(StoryInfo.class));
                    }
                }

                mTrendingRecycler.setAdapter(new TrendingBookAdapter(getContext(),storyViewerInfos));
                mProgressbar.setVisibility(View.GONE);
               /* storyList.clear();
                Log.e("gg","viewer list"+storyViewerInfos.size());
                for(int i=0;i<storyViewerInfos.size();i++){
                    temp=0;
                    String uid=storyViewerInfos.get(i).getAuthorId();
                    String title=storyViewerInfos.get(i).getStoryTitle();
                    Log.e("gg","viewer="+uid+" "+title);

                    DatabaseReference refStory=FirebaseDatabase.getInstance().getReference("Story").child(uid).child(title);
                    refStory.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            StoryInfo info=snapshot.getValue(StoryInfo.class);
                            Log.e("gg","info="+info.getStoryTitle());
                            storyList.add(info);

                            //todo to check last looping?
                            temp++;
                            if(temp==storyViewerInfos.size()){
                                Log.e("gg","storyList="+storyList.size());
                                mTrendingRecycler.setAdapter(new TrendingBookAdapter(getContext(),storyList));
                                mProgressbar.setVisibility(View.GONE);
                            }
                            Log.e("gg","temp="+temp);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void inits() {
        scienceChip=getView().findViewById(R.id.main_chipScience);
        horrorChip=getView().findViewById(R.id.main_chipHorror);
        adventureChip=getView().findViewById(R.id.main_chipAdventure);
        actionChip=getView().findViewById(R.id.main_chipAction);
        romanceChip=getView().findViewById(R.id.main_chipRomance);
        transChip=getView().findViewById(R.id.main_chipTranslation);
        comedyChip=getView().findViewById(R.id.main_chipComedy);
        teenagerChip=getView().findViewById(R.id.main_chipTeenager);
        highschoolChip=getView().findViewById(R.id.main_chipHighSchool);
        chipGroup=getView().findViewById(R.id.chipGroup);
        mSearchView=getView().findViewById(R.id.searchView);
        mTrendingRecycler=getView().findViewById(R.id.main_trendingRecycler);
        mProgressbar=getView().findViewById(R.id.main_progressbar);
    }

}