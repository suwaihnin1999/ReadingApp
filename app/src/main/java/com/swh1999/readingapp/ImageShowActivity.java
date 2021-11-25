package com.swh1999.readingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ImageShowActivity extends AppCompatActivity {
String uriString,title;
Uri uri;
ImageView mChangeImg;
Button mCancel,mChange;
DatabaseReference reff;
StorageReference storageReff;
FirebaseAuth fAuth;
String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_show);
        uriString=getIntent().getStringExtra("changeImg");
        title=getIntent().getStringExtra("storyTitle");

        //todo get current user's uid
        fAuth=FirebaseAuth.getInstance();
        uid=fAuth.getCurrentUser().getUid();

        reff= FirebaseDatabase.getInstance().getReference("Story").child(uid).child(title);
        storageReff=FirebaseStorage.getInstance().getReference("Story");

        inits();
        uri=Uri.parse(uriString);
        mChangeImg.setImageURI(uri);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StorageReference sf=storageReff.child(System.currentTimeMillis()+"."+getFileExt(uri));

                //todo set profile img to storage
                sf.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        sf.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                reff.child("storyImg").setValue(uri.toString());
                                Toast.makeText(ImageShowActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                });
            }
        });

    }
    private String getFileExt(Uri uri){
        ContentResolver cr=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    private void inits() {
        mChangeImg=findViewById(R.id.imageShow_img);
        mCancel=findViewById(R.id.imageShow_cancel);
        mChange=findViewById(R.id.imageShow_change);
    }
}