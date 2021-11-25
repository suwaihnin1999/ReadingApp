package com.swh1999.readingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddStoriesActivity extends AppCompatActivity {
    CardView mStoryCard;
    ImageView mStoryImg;
    EditText mStoryTitle,mStoryDes,mStoryTag;
    Button mStoryBtn;
    public static int PICK_Img_Request=0;
    Uri uri;
    Toolbar mToolbar;
    DatabaseReference reff,reffStory;
    FirebaseAuth fAuth;
    StorageReference storageReff;
    String UID;
    ProgressBar mProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stories);

        inits();

        //todo get current user's info
        fAuth=FirebaseAuth.getInstance();
        UID=fAuth.getCurrentUser().getUid();

        //todo Story's database reff & storage reff
        reff= FirebaseDatabase.getInstance().getReference("Story").child(UID);
        storageReff=FirebaseStorage.getInstance().getReference("Story");
        reffStory=FirebaseDatabase.getInstance().getReference("StoryViewer");

        mToolbar.setTitle("Add Story's Info");
        setSupportActionBar(mToolbar);
        Drawable menu1=getResources().getDrawable(R.drawable.ic_back);
        menu1.setColorFilter(getResources().getColor(R.color.lightblack), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(menu1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title=mStoryTitle.getText().toString();
                String des=mStoryDes.getText().toString();
                String tag=mStoryTag.getText().toString();
                if((!title.equals("")) || (!des.equals("")) || (!tag.equals(""))){
                    AlertDialog.Builder mBuilder=new AlertDialog.Builder(AddStoriesActivity.this);
                    mBuilder.setTitle("Alert");
                    mBuilder.setMessage("Your story info will delete.");
                    mBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
                    mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    AlertDialog dialog=mBuilder.create();
                    dialog.show();
                }
            }
        });

        mStoryCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        mStoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title=mStoryTitle.getText().toString();
                String des=mStoryDes.getText().toString();
                String tag=mStoryTag.getText().toString();
                if(title.equals("")){
                    mStoryTitle.setError("Story's Title required.");
                }
                else if(uri!=null){
                    mProgressBar.setVisibility(View.VISIBLE);
                    //todo add story info to firebase
                    StorageReference sf=storageReff.child(System.currentTimeMillis()+"."+getFileExt(uri));
                    sf.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            sf.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //todo set about story to under Story
                                    StoryInfo info=new StoryInfo(UID,title,des,uri.toString(),title,tag);
                                    reff.child(title).setValue(info);

                                    //todo set about story's total viewer and like to StoryView
                                    StoryViewerInfo viewerInfo=new StoryViewerInfo(title,UID,0,0);
                                    reffStory.push().setValue(viewerInfo);

                                    Intent intent=new Intent(AddStoriesActivity.this,AddPartActivity.class);
                                    intent.putExtra("storyTitle",title);
                                    intent.putExtra("storyDes",des);
                                    startActivity(intent);
                                    mProgressBar.setVisibility(View.GONE);
                                    finish();
                                }
                            });
                            sf.getDownloadUrl().addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AddStoriesActivity.this, "Error="+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });

                }
            }
        });
    }

    private String getFileExt(Uri uri) {
        ContentResolver cr=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getMimeTypeFromExtension(cr.getType(uri));
    }

    private void openGallery() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(intent,PICK_Img_Request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_Img_Request && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            uri=data.getData();
            mStoryImg.setImageURI(uri);
        }
    }

    private void inits() {
        mStoryCard=findViewById(R.id.addStoriesActivity_cardView);
        mStoryImg=findViewById(R.id.addStoriesActivity_image);
        mStoryTitle=findViewById(R.id.addStoriesActivity_title);
        mStoryTag=findViewById(R.id.addStoriesActivity_tag);
        mStoryDes=findViewById(R.id.addStoriesActivity_des);
        mStoryBtn=findViewById(R.id.addStoriesActivity_btn);
        View v=findViewById(R.id.addStoriesActivity_toolbar);
        mToolbar=v.findViewById(R.id.transparentToolbar);
        mProgressBar=findViewById(R.id.addStoriesActivity_progressbar);
    }
}