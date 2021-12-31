package com.swh1999.readingapp;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
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
import java.util.List;

public class MyStoryDetailActivity extends AppCompatActivity {
ImageView mImageView;
TextView mStoryTitle,mStoryDes;
ImageView mStoryTitleEdit,mStoryDesEdit;
Button mStoryChangeButton;
Toolbar mToolbar;
String title,uid;
CardView mAddPart;
DatabaseReference reff,reffPart;
FirebaseAuth fAuth;
RecyclerView mPartRecyclerview;
List<StoryPartInfo> partList;
public static  int PICK_Back_Request=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_story_detail);

        inits();
        title=getIntent().getStringExtra("storyTitle");

        //todo get current user's uid
        fAuth=FirebaseAuth.getInstance();
        uid=fAuth.getCurrentUser().getUid();
        Log.e("gg",uid);

        reff= FirebaseDatabase.getInstance().getReference("Story");
        reff.orderByChild("uid").equalTo(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    if(snapshot1.child("storyTitle").getValue().toString().equals(title)){
                        mStoryTitle.setText(snapshot1.child("storyTitleNew").getValue().toString());
                        mStoryDes.setText(snapshot1.child("storyDes").getValue().toString());
                        Glide.with(MyStoryDetailActivity.this).load(snapshot1.child("storyImg").getValue().toString()).into(mImageView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mToolbar.setTitle("Edit Story");
        setSupportActionBar(mToolbar);
        Drawable menu=getResources().getDrawable(R.drawable.ic_back);
        menu.setColorFilter(getResources().getColor(R.color.lightblack), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        partList=new ArrayList<>();
        mPartRecyclerview.setLayoutManager(new LinearLayoutManager(MyStoryDetailActivity.this));
        reffPart=FirebaseDatabase.getInstance().getReference("Part").child(uid).child(title);
        reffPart.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                partList.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    StoryPartInfo partInfo=snapshot1.getValue(StoryPartInfo.class);
                    partList.add(partInfo);
                }
                mPartRecyclerview.setAdapter(new PartAdapter(MyStoryDetailActivity.this,partList,
                        new PartAdapter.onItemClickListener(){
                            @Override
                            public void onItemClick(StoryPartInfo storyPartInfo) {
                                Intent intent=new Intent(MyStoryDetailActivity.this,ViewPartActivity.class);
                                intent.putExtra("storyTitle",title);
                                intent.putExtra("storyTitleNew",mStoryTitle.getText().toString());
                                intent.putExtra("uid",uid);
                                intent.putExtra("partTitle",storyPartInfo.getPartTitle());
                                startActivity(intent);
                            }
                        }));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        //todo addPart Card click event
        mAddPart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MyStoryDetailActivity.this,AddPartActivity.class);
                intent.putExtra("storyTitle",title);
                intent.putExtra("storyDes",mStoryDes.getText().toString());
                startActivity(intent);
            }
        });



    }

    private void openGallery() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(intent,PICK_Back_Request);
    }

    Uri uri;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_Back_Request && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            uri=data.getData();
            Intent intent=new Intent(MyStoryDetailActivity.this,ImageShowActivity.class);
            intent.putExtra("changeImg",uri.toString());
            intent.putExtra("storyTitle",title);
            startActivity(intent);
        }
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
            case R.id.bookDetail_menu:Intent intent=new Intent(MyStoryDetailActivity.this,EditMyStoryInfoActivity.class);
                                        intent.putExtra("uid",uid);
                                        intent.putExtra("title",title);
                                        startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void inits() {
        mImageView=findViewById(R.id.myStoryDetail_img);
        mStoryTitle=findViewById(R.id.myStoryDetail_title);
        mStoryDes=findViewById(R.id.myStoryDetail_Des);
        View v=findViewById(R.id.myStoryDetail_toolbar);
        mToolbar=v.findViewById(R.id.transparentNormalToolbar);
        mPartRecyclerview=findViewById(R.id.myStoryDetail_partRecycler);
        mAddPart=findViewById(R.id.myStoryDetail_addNewPart);
    }
}