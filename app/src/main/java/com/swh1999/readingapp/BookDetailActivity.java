package com.swh1999.readingapp;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class BookDetailActivity extends AppCompatActivity {
    Toolbar mToolbar;
    ImageView mBookImg;
    CircleImageView mAuthorImg;
    TextView mTitle, mDes;
    TextView mAuthor, mView, mVote, mPart;
    Button mReadBtn, mAddLibrary;
    String authorId, title, uid;
    DatabaseReference reff, reffUser, reffView, reffPart, reffLibrary, reffCheckLibrary;
    ChipGroup mChipGroup;
    String[] tag;
    RecyclerView mSimpleRecycler;
    ArrayList<StoryInfo> mSimpleBookTitle;
    ArrayList<StoryInfo> mSimpleBookList;
    FirebaseAuth fAuth;
    ConstraintLayout parent;
    int temp;
    View view1;

    ArrayList<String> partId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        authorId = getIntent().getStringExtra("uid");
        title = getIntent().getStringExtra("title");

        inits();

        //todo get current user's id
        fAuth = FirebaseAuth.getInstance();
        uid = fAuth.getCurrentUser().getUid();

        Log.e("gg", "author id=" + authorId + " uid=" + uid + " " + authorId.equals(uid) + " " + title);

        if (authorId.equals(uid)) {
            mAddLibrary.setVisibility(View.GONE);
        }


        mToolbar.setTitle("Book Details");
        setSupportActionBar(mToolbar);
        Drawable menu1 = getResources().getDrawable(R.drawable.ic_back);
        menu1.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(menu1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //todo check this book is already library or not
        reffCheckLibrary = FirebaseDatabase.getInstance().getReference("Library").child(uid);
        reffCheckLibrary.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    String library_AId = snapshot1.child("authorId").getValue().toString();
                    String library_title = snapshot1.child("title").getValue().toString();

                    if (library_AId.equals(authorId) && library_title.equals(title)) {
                        mReadBtn.setText("Continuous");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        partId = new ArrayList<>();
        //getTotalVote();

        mSimpleBookTitle = new ArrayList<>();
        mSimpleBookList = new ArrayList<>();
        //todo to show story title,img,name
        reff = FirebaseDatabase.getInstance().getReference("Story");
        reff.orderByChild("uid").equalTo(authorId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    if (snapshot1.child("storyTitle").getValue().toString().equals(title)) {
                        Log.e("gg", "equal=" + snapshot1.child("storyTitleNew").getValue().toString().equals(title));
                        Glide.with(getApplicationContext()).load(snapshot1.child("storyImg").getValue()).into(mBookImg);
                        mTitle.setText(snapshot1.child("storyTitleNew").getValue().toString());
                        mDes.setText(snapshot1.child("storyDes").getValue().toString());
                        int totalView = Integer.parseInt(snapshot1.child("totalView").getValue().toString());
                        if (totalView > 999 && totalView < 1000000) {
                            double tView = totalView / 1000;
                            mView.setText(String.format("%.1f", tView) + "k Views");
                        } else if (totalView >= 1000000) {
                            double tView = totalView / 1000;
                            mView.setText(String.format("%.1f", tView) + "M Views");
                        } else if (totalView <= 999) {
                            mView.setText(totalView + " Views");
                        }
                        int totalVote = Integer.parseInt(snapshot1.child("totalVote").getValue().toString());
                        if (totalVote > 999 && totalVote < 1000000) {
                            double tVote = totalVote / 1000;
                            mVote.setText(String.format("%.1f", tVote) + "k Votes");
                        } else if (totalVote >= 1000000) {
                            double tVote = totalVote / 1000000;
                            mVote.setText(String.format("%.1f", tVote) + "M Votes");
                        } else if (totalVote <= 999) {
                            mVote.setText(totalVote + " Votes");
                        }

                        tag = null;
                        mChipGroup.removeAllViews();
                        //mVote.setText(snapshot1.child("totalVote").getValue().toString()+" Votes");
                        tag = snapshot1.child("tag").getValue().toString().split(",");

                        //todo to show tag chip
                        for (int i = 0; i < tag.length; i++) {
                            Chip chip = new Chip(BookDetailActivity.this);
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
                        //todo to get similar book list
                        String tagg = tag[0];
                        DatabaseReference reffStory = FirebaseDatabase.getInstance().getReference("Story");
                        reffStory.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                mSimpleBookTitle.clear();
                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                    String tag_fire = snapshot1.child("tag").getValue().toString();
                                    String title_fire = snapshot1.child("storyTitle").getValue().toString();
                                    if (tag_fire.contains(tagg) && (!title.equals(title_fire))) {
                                        mSimpleBookTitle.add(snapshot1.getValue(StoryInfo.class));
                                    }
                                }
                                Log.e("gg", "simple book list=" + mSimpleBookTitle.size());


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //todo show no.of part
        reffPart = FirebaseDatabase.getInstance().getReference("Part").child(authorId).child(title);
        reffPart.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mPart.setText(snapshot.getChildrenCount() + " Parts");
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    partId.add(snapshot1.getKey());
                }
                getTotalVote();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //todo to show author name & profile img
        reffUser = FirebaseDatabase.getInstance().getReference("Profile").child(authorId);
        reffUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mAuthor.setText("By " + snapshot.child("username").getValue().toString());
                Glide.with(getApplicationContext()).load(snapshot.child("profileImg").getValue()).into(mAuthorImg);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mReadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookDetailActivity.this, ReadBookActivity.class);
                intent.putExtra("authorId", authorId);
                intent.putExtra("title", title);
                startActivity(intent);
            }
        });

        mAddLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mReadBtn.getText().equals("Continuous")) {
                    FirebaseDatabase.getInstance().getReference("Library").child(uid).removeValue();
                    mReadBtn.setText("Read");

                    Snackbar.make(parent,"Removed from your book shelf.",Snackbar.LENGTH_SHORT).show();


                }
                else {
                    reffLibrary = FirebaseDatabase.getInstance().getReference("Library").child(uid);
                    LibraryBookInfo lb = new LibraryBookInfo(authorId, title,"",0,0);
                    reffLibrary.push().setValue(lb);
                    mReadBtn.setText("Continuous");

                    Snackbar.make(parent, "Successfully added to your book shelf.", Snackbar.LENGTH_SHORT).show();


                }


            }
        });

        mAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookDetailActivity.this, ProfileActivity.class);
                intent.putExtra("uid", authorId);
                startActivity(intent);
            }
        });
        mAuthorImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookDetailActivity.this, ProfileActivity.class);
                intent.putExtra("uid", authorId);
                startActivity(intent);
            }
        });


    }



    int count;

    private void getTotalVote() {
        count = 0;
        FirebaseDatabase.getInstance().getReference("Voter").child(uid)
                .child(title).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (String id : partId) {
                    Log.e("gg", "partid=" + id);
                    count += (int) snapshot.child(id).getChildrenCount();
                }

                Log.e("gg", "totalVote=" + count);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void inits() {
        view1 = findViewById(R.id.bookDetail_toolbar);
        mToolbar = view1.findViewById(R.id.toolbar);
        mBookImg = findViewById(R.id.bookDetail_image);
        mTitle = findViewById(R.id.bookDetail_title);
        mAuthor = findViewById(R.id.bookDetail_authorName);
        mAuthorImg = findViewById(R.id.bookDetail_authorProfile);
        mReadBtn = findViewById(R.id.bookDetail_btn);
        mDes = findViewById(R.id.bookDetail_des);
        mChipGroup = findViewById(R.id.bookDetail_chipGroup);
        mSimpleRecycler = findViewById(R.id.bookDetail_simpleRecycler);
        mView = findViewById(R.id.bookDetail_viewer);
        mVote = findViewById(R.id.bookDetail_vote);
        mPart = findViewById(R.id.bookDetail_part);
        mAddLibrary = findViewById(R.id.bookDetail_addLibrary);
        parent = findViewById(R.id.bookDetail_layout);
        //mScroll=findViewById(R.id.scrollView2);

    }
}