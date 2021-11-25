package com.swh1999.readingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

        reff= FirebaseDatabase.getInstance().getReference("Story").child(uid).child(title);
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mStoryTitle.setText(snapshot.child("storyTitleNew").getValue().toString());
                    mStoryDes.setText(snapshot.child("storyDes").getValue().toString());
                    Glide.with(MyStoryDetailActivity.this).load(snapshot.child("storyImg").getValue().toString()).into(mImageView);

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
                mPartRecyclerview.setAdapter(new PartAdapter(MyStoryDetailActivity.this,partList));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //todo change image button click event
        mStoryChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
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

        //todo edit Story title click event
        mStoryTitleEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder=new AlertDialog.Builder(MyStoryDetailActivity.this,R.style.AlertDialog_custom);
                mBuilder.setTitle("Edit Story's Title");
                EditText tv=new EditText(MyStoryDetailActivity.this);
                tv.setHint("new title");
                tv.setTextColor(getResources().getColor(R.color.white));
                mBuilder.setView(tv);

                mBuilder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        reff.child("storyTitleNew").setValue(tv.getText().toString());
                    }
                });
                mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog dialog=mBuilder.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        Button posBtn=dialog.getButton(dialogInterface.BUTTON_POSITIVE);
                        posBtn.setBackgroundColor(getResources().getColor(R.color.orange));
                        posBtn.setTextColor(getResources().getColor(R.color.black));
                        posBtn.setTextSize(13);
                        posBtn.setAllCaps(false);

                        Button negBtn=dialog.getButton(dialogInterface.BUTTON_NEGATIVE);
                        negBtn.setBackgroundColor(getResources().getColor(R.color.green));
                        negBtn.setTextColor(getResources().getColor(R.color.black));
                        negBtn.setTextSize(13);
                        negBtn.setAllCaps(false);


                    }
                });
                dialog.show();

            }
        });

        mStoryDesEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder=new AlertDialog.Builder(MyStoryDetailActivity.this,R.style.AlertDialog_custom);
                mBuilder.setTitle("Edit Story's Des");
                EditText tv=new EditText(MyStoryDetailActivity.this);
                tv.setHint("new description");
                tv.setTextColor(getResources().getColor(R.color.white));
                mBuilder.setView(tv);

                mBuilder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        reff.child("storyDes").setValue(tv.getText().toString());
                    }
                });
                mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog dialog=mBuilder.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        Button posBtn=dialog.getButton(dialogInterface.BUTTON_POSITIVE);
                        posBtn.setBackgroundColor(getResources().getColor(R.color.orange));
                        posBtn.setTextColor(getResources().getColor(R.color.black));
                        posBtn.setTextSize(13);
                        posBtn.setAllCaps(false);

                        Button negBtn=dialog.getButton(dialogInterface.BUTTON_NEGATIVE);
                        negBtn.setBackgroundColor(getResources().getColor(R.color.green));
                        negBtn.setTextColor(getResources().getColor(R.color.black));
                        negBtn.setTextSize(13);
                        negBtn.setAllCaps(false);


                    }
                });
                dialog.show();
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

    private void inits() {
        mImageView=findViewById(R.id.myStoryDetail_img);
        mStoryTitle=findViewById(R.id.myStoryDetail_title);
        mStoryDes=findViewById(R.id.myStoryDetail_Des);
        mStoryTitleEdit=findViewById(R.id.myStoryDetail_titleEdit);
        mStoryDesEdit=findViewById(R.id.myStoryDetail_desEdit);
        mStoryChangeButton=findViewById(R.id.myStoryDetail_changeImg);
        View v=findViewById(R.id.myStoryDetail_toolbar);
        mToolbar=v.findViewById(R.id.transparentToolbar);
        mPartRecyclerview=findViewById(R.id.myStoryDetail_partRecycler);
        mAddPart=findViewById(R.id.myStoryDetail_addNewPart);
    }
}