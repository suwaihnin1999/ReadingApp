package com.swh1999.readingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    CircleImageView mProfile;
    TextView mUsername, mWorkCount, mFollowerCount, mFollowingCount, mWorkCountTitle;
    Button mFollowBtn;
    RecyclerView mWorkRecycler;
    Toolbar mToolbar;
    DatabaseReference mReffProfile, mReffWork, mReff;
    FirebaseAuth fAuth;
    String uid, intentUid;
    ArrayList<StoryInfo> storylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        init();

        intentUid = getIntent().getStringExtra("uid");


        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        fAuth = FirebaseAuth.getInstance();
        uid = fAuth.getCurrentUser().getUid();

        if (intentUid.equals(uid)) {
            mFollowBtn.setVisibility(View.GONE);
        }

        mReffProfile = FirebaseDatabase.getInstance().getReference("Profile").child(uid);
        mReffProfile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsername.setText(snapshot.child("username").getValue().toString());
                mWorkCountTitle.setText("Stories by " + snapshot.child("username").getValue().toString());
                Glide.with(ProfileActivity.this).load(snapshot.child("profileImg").getValue().toString()).into(mProfile);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        storylist = new ArrayList<>();
        mReffWork = FirebaseDatabase.getInstance().getReference("Story");
        mReffWork.orderByChild("uid").equalTo(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mWorkCount.setText(String.valueOf(snapshot.getChildrenCount()));
                if (snapshot.getChildrenCount() == 0) {
                    mWorkCountTitle.setVisibility(View.GONE);
                }
                storylist.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    StoryInfo storyInfo = snapshot1.getValue(StoryInfo.class);
                    storylist.add(storyInfo);
                }
                mWorkRecycler.setLayoutManager(new LinearLayoutManager(ProfileActivity.this));
                mWorkRecycler.setAdapter(new WorkListAdapter(ProfileActivity.this, storylist));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.mybookdetail_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.bookDetail_menu:
                Intent intent=new Intent(ProfileActivity.this, ProfileListActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        View view = findViewById(R.id.profile_toolbar);
        mToolbar = view.findViewById(R.id.transparentNormalToolbar);
        mUsername = findViewById(R.id.profile_username);
        mWorkCount = findViewById(R.id.profile_workCount);
        mFollowerCount = findViewById(R.id.profile_followCount);
        mFollowingCount = findViewById(R.id.profile_followingCount);
        mFollowBtn = findViewById(R.id.profile_followBtn);
        mWorkRecycler = findViewById(R.id.profile_workRecycler);
        mProfile = findViewById(R.id.profile_profileImg);
        mWorkCountTitle = findViewById(R.id.profile_workCountTitle);
    }
}