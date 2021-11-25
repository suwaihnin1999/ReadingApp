package com.swh1999.readingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class BookDetailActivity extends AppCompatActivity {
Toolbar mToolbar;
ImageView mBookImg;
CircleImageView mAuthorImg;
TextView mTitle,mDes;
TextView mAuthor,mView,mVote,mPart;
Button mReadBtn;
String uid,title;
DatabaseReference reff,reffUser,reffView,reffPart;
ChipGroup mChipGroup;
String[] tag;
RecyclerView mSimpleRecycler;
ArrayList<StoryInfo> mSimpleBookTitle;
ArrayList<StoryInfo> mSimpleBookList;
ScrollView mScroll;
int temp;
boolean scrollViewFlag;
View view1;
RelativeLayout mMainToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        uid=getIntent().getStringExtra("uid");
        title=getIntent().getStringExtra("title");

        inits();

        mToolbar.setTitle("Book Details");
        setSupportActionBar(mToolbar);
        Drawable menu1=getResources().getDrawable(R.drawable.ic_back);
        menu1.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(menu1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mSimpleBookTitle=new ArrayList<>();
        mSimpleBookList=new ArrayList<>();
        //todo to show story title,img,name
        reff= FirebaseDatabase.getInstance().getReference("Story").child(uid).child(title);
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Glide.with(BookDetailActivity.this).load(snapshot.child("storyImg").getValue()).into(mBookImg);
                mTitle.setText(snapshot.child("storyTitle").getValue().toString());
                mDes.setText(snapshot.child("storyDes").getValue().toString());
                tag=snapshot.child("tag").getValue().toString().split(",");

                //todo to show tag chip
                for(int i=0;i<tag.length;i++){
                    Chip chip=new Chip(BookDetailActivity.this);
                    chip.setText(tag[i]);
                    chip.setTextSize(12);
                    chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            Toast.makeText(BookDetailActivity.this, chip.getText(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    mChipGroup.addView(chip);
                }

                mSimpleRecycler.setLayoutManager(new LinearLayoutManager(BookDetailActivity.this));
                //todo to get simple book list
                String tagg=tag[0];
                DatabaseReference reffStory=FirebaseDatabase.getInstance().getReference("Story").child(uid);
                reffStory.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            mSimpleBookTitle.clear();
                            for(DataSnapshot snapshot1:snapshot.getChildren()){
                                String tag_fire=snapshot1.child("tag").getValue().toString();
                                String title_fire=snapshot1.child("storyTitle").getValue().toString();
                                if(tag_fire.contains(tagg) && (!title.equals(title_fire))){
                                    mSimpleBookTitle.add(snapshot1.getValue(StoryInfo.class));
                                }
                            }
                            Log.e("gg","simple book list="+mSimpleBookTitle.size());

                            mSimpleBookList.clear();
                            temp=0;
                            for(int k=0;k<mSimpleBookTitle.size();k++){
                                String simpleUID=mSimpleBookTitle.get(k).getUid();
                                String simpleTitle=mSimpleBookTitle.get(k).getStoryTitle();
                                Log.e("gg",simpleUID+" "+simpleTitle);

                                DatabaseReference reff=FirebaseDatabase.getInstance().getReference("Story").child(simpleUID).child(simpleTitle);
                                reff.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                       mSimpleBookList.add(snapshot.getValue(StoryInfo.class));
                                       temp++;
                                       Log.e("gg",mSimpleBookTitle.size()+" "+temp);
                                       if(temp==mSimpleBookTitle.size()){
                                           Log.e("gg",mSimpleBookList.get(0).getStoryTitleNew());
                                           mSimpleRecycler.setAdapter(new SimpleBookListAdapter(BookDetailActivity.this,mSimpleBookList));
                                       }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //todo to show no. of viewer and votes
        reffView=FirebaseDatabase.getInstance().getReference("StoryViewer");
        reffView.orderByChild("authorId").equalTo(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    if(snapshot1.child("storyTitle").getValue().toString().equals(title)){
                        mView.setText(snapshot1.child("totalView").getValue().toString()+" Views");
                        mVote.setText(snapshot1.child("totalLike").getValue().toString()+" Votes");
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //todo show no.of part
        reffPart=FirebaseDatabase.getInstance().getReference("Part").child(uid).child(title);
        reffPart.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mPart.setText(snapshot.getChildrenCount()+" Parts");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //todo to show author name & profile img
        reffUser=FirebaseDatabase.getInstance().getReference("Profile").child(uid);
        reffUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mAuthor.setText("By "+snapshot.child("username").getValue().toString());
                Glide.with(BookDetailActivity.this).load(snapshot.child("profileImg").getValue()).into(mAuthorImg);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mReadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(BookDetailActivity.this,ReadBookActivity.class);
                intent.putExtra("authorId",uid);
                intent.putExtra("title",title);
                startActivity(intent);
            }
        });




    }

    private void inits() {
        view1=findViewById(R.id.bookDetail_toolbar);
        mToolbar=view1.findViewById(R.id.toolbar);
        mBookImg=findViewById(R.id.bookDetail_image);
        mTitle=findViewById(R.id.bookDetail_title);
        mAuthor=findViewById(R.id.bookDetail_authorName);
        mAuthorImg=findViewById(R.id.bookDetail_authorProfile);
        mReadBtn=findViewById(R.id.bookDetail_btn);
        mDes=findViewById(R.id.bookDetail_des);
        mChipGroup=findViewById(R.id.bookDetail_chipGroup);
        mSimpleRecycler=findViewById(R.id.bookDetail_simpleRecycler);
        mView=findViewById(R.id.bookDetail_viewer);
        mVote=findViewById(R.id.bookDetail_vote);
        mPart=findViewById(R.id.bookDetail_part);
        mScroll=findViewById(R.id.scrollView2);

    }
}