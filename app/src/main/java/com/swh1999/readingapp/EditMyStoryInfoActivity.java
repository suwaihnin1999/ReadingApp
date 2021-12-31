package com.swh1999.readingapp;

import static com.swh1999.readingapp.R.layout.username_alert_layout;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditMyStoryInfoActivity extends AppCompatActivity {
    String uid, title,key;
    DatabaseReference reff,reffStory;
    ImageView mBookCover;
    TextView mBookTitle, mBookDes, mBookTag;
    Button mBookChangCover;
    Toolbar mToolbar;
    LinearLayout title_Linear,des_Linear,tag_Linear;
    public static  int PICK_Back_Request=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_my_story_info);

        uid = getIntent().getStringExtra("uid");
        title = getIntent().getStringExtra("title");

        Log.e("gg", uid + " " + title);

        init();


        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        //todo get story's info
        reff = FirebaseDatabase.getInstance().getReference("Story");
        reff.orderByChild("uid").equalTo(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    if (snapshot1.child("storyTitle").getValue().equals(title)) {
                        key=snapshot1.getKey();
                        Glide.with(EditMyStoryInfoActivity.this).load(snapshot1.child("storyImg").getValue()).into(mBookCover);
                        mBookTitle.setText(snapshot1.child("storyTitleNew").getValue().toString());
                        mBookDes.setText(snapshot1.child("storyDes").getValue().toString());
                        mBookTag.setText(snapshot1.child("tag").getValue().toString());
                        mToolbar.setTitle(snapshot1.child("storyTitleNew").getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //todo edit story's title
        title_Linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder=new AlertDialog.Builder(EditMyStoryInfoActivity.this,R.style.AlertDialog_custom);
                mBuilder.setTitle("Edit Story's Title");
                View v=getLayoutInflater().inflate(username_alert_layout,null);
                TextView alertTitle=v.findViewById(R.id.textView_username);
                EditText mUsername=v.findViewById(R.id.username_editText);
                alertTitle.setText("New story title");
                mUsername.setText(mBookTitle.getText().toString());

                FrameLayout container = new FrameLayout(EditMyStoryInfoActivity.this);
                FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                params.topMargin=getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                params.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                params.bottomMargin = getResources().getDimensionPixelSize(R.dimen.dialog_marginBot);
                v.setLayoutParams(params);
                container.addView(v);

                mBuilder.setView(container);

                mBuilder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //todo database reff of commom story query
                        reffStory=FirebaseDatabase.getInstance().getReference("Story").child(key);
                        reffStory.child("storyTitleNew").setValue(mUsername.getText().toString());
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

        //todo edit story des
        des_Linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder=new AlertDialog.Builder(EditMyStoryInfoActivity.this,R.style.AlertDialog_custom);
                mBuilder.setTitle("Edit Story's Des");
                View v=getLayoutInflater().inflate(username_alert_layout,null);
                TextView alertTitle=v.findViewById(R.id.textView_username);
                EditText mUsername=v.findViewById(R.id.username_editText);
                mUsername.setText("");
                alertTitle.setText("New story des");

                FrameLayout container = new FrameLayout(EditMyStoryInfoActivity.this);
                FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                params.topMargin=getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                params.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                params.bottomMargin = getResources().getDimensionPixelSize(R.dimen.dialog_marginBot);
                v.setLayoutParams(params);
                container.addView(v);

                mBuilder.setView(container);

                mBuilder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //todo database reff of commom story query
                        reffStory=FirebaseDatabase.getInstance().getReference("Story").child(key);
                        reffStory.child("storyDes").setValue(mUsername.getText().toString());
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

        //todo edit story tag
        tag_Linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder=new AlertDialog.Builder(EditMyStoryInfoActivity.this,R.style.AlertDialog_custom);
                mBuilder.setTitle("Edit Story's Tag");
                View v=getLayoutInflater().inflate(username_alert_layout,null);
                TextView alertTitle=v.findViewById(R.id.textView_username);
                EditText mUsername=v.findViewById(R.id.username_editText);
                alertTitle.setText("New story tag");
                mUsername.setText(mBookTag.getText().toString());

                FrameLayout container = new FrameLayout(EditMyStoryInfoActivity.this);
                FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                params.topMargin=getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                params.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                params.bottomMargin = getResources().getDimensionPixelSize(R.dimen.dialog_marginBot);
                v.setLayoutParams(params);
                container.addView(v);

                mBuilder.setView(container);

                mBuilder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //todo database reff of commom story query
                        reffStory=FirebaseDatabase.getInstance().getReference("Story").child(key);
                        reffStory.child("tag").setValue(mUsername.getText().toString());
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

        //todo change img btn click
        mBookChangCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
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
            Intent intent=new Intent(EditMyStoryInfoActivity.this,ImageShowActivity.class);
            intent.putExtra("changeImg",uri.toString());
            intent.putExtra("storyTitle",title);
            startActivity(intent);
        }
    }

    public void init() {
        mBookCover = findViewById(R.id.editMyStory_img);
        mBookTitle = findViewById(R.id.editMyStory_title);
        mBookDes = findViewById(R.id.eidtMyStory_des);
        mBookTag = findViewById(R.id.eidtMyStory_tag);
        mBookChangCover = findViewById(R.id.editMyStory_btn);
        View view=findViewById(R.id.editMyStory_toolbar);
        mToolbar=view.findViewById(R.id.transparentNormalToolbar);
        title_Linear=findViewById(R.id.linearLayout_title);
        des_Linear=findViewById(R.id.linearLayout_des);
        tag_Linear=findViewById(R.id.linearLayout_tag);
    }
}