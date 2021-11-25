package com.swh1999.readingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;

public class AddProfileActivity extends AppCompatActivity {
Toolbar mToolbar;
EditText mUsername;
Spinner mGender;
Button mBDay,mCreate;
ImageView mProfileImg,mBackImg;
CardView mProfileCard,mBackCard;
public static int PICK_Profile_Request=1;
public static int PICK_Back_Request=2;
String email,pass;
FirebaseAuth fAuth;
DatabaseReference reff;
StorageReference mStorageReff;
int year,month,day;
ProgressBar mProgressBar;
Uri uriProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_profile);

        email=getIntent().getStringExtra("email");
        pass=getIntent().getStringExtra("pass");

        mStorageReff= FirebaseStorage.getInstance().getReference("Profile");
        fAuth=FirebaseAuth.getInstance();
        reff=FirebaseDatabase.getInstance().getReference("Profile");

        inits();
        mToolbar.setTitle("Additional information");
        setSupportActionBar(mToolbar);
        Drawable menu=getResources().getDrawable(R.drawable.ic_back);
        menu.setColorFilter(getResources().getColor(R.color.lightblack), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username=mUsername.getText().toString().trim();
                String mBirthDay=mBDay.getText().toString().trim();
                String gender=mGender.getSelectedItem().toString();
                if((!username.equals("")) || (!mBirthDay.equals("")) || (!gender.equals(""))){
                    AlertDialog.Builder mBuilder=new AlertDialog.Builder(AddProfileActivity.this);
                    mBuilder.setTitle("Alert");
                    mBuilder.setMessage("Your info will delete.");
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

        mProfileCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
        mBackCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGalleryForBack();
            }
        });

        mBDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar=Calendar.getInstance();
                year=calendar.get(Calendar.YEAR);
                month=calendar.get(Calendar.MONTH);
                day=calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePicker=new DatePickerDialog(AddProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        mBDay.setText(i2+"/"+(i1+1)+"/"+i);
                    }
                },year,month,day);
                datePicker.show();
            }
        });

        mCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username=mUsername.getText().toString().trim();
                String mBirthDay=mBDay.getText().toString().trim();
                String gender=mGender.getSelectedItem().toString();
                if(username.equals("")){
                    mUsername.setError("Username required!");
                }
                else if(mBirthDay.equals("00/00/0000")){
                    Toast.makeText(AddProfileActivity.this, "Select your birthday.", Toast.LENGTH_SHORT).show();
                }
                else if(uri==null || uriBack==null){
                    Toast.makeText(AddProfileActivity.this, "Select profile and background image.", Toast.LENGTH_SHORT).show();
                }
                else{
                    mProgressBar.setVisibility(View.VISIBLE);
                    //todo set Authentication
                    fAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                StorageReference sfProfile=mStorageReff.child(System.currentTimeMillis()+"."+getFileExt(uri));
                                StorageReference sfBack=mStorageReff.child(System.currentTimeMillis()+"."+getFileExt(uriBack));

                                //todo set profile img to storage
                                sfProfile.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        sfProfile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                uriProfile=uri;
                                            }
                                        });
                                    }
                                });

                                //todo set background img to storage
                                sfBack.putFile(uriBack).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        sfBack.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                ProfileInfo profileInfo=new ProfileInfo(email,pass,username,uriProfile.toString(),uri.toString(),gender,mBirthDay);
                                                reff.child(fAuth.getUid()).setValue(profileInfo);

                                                mProgressBar.setVisibility(View.GONE);
                                                Intent intent=new Intent(AddProfileActivity.this,LoginActivity.class);
                                                startActivity(intent);
                                            }
                                        });
                                    }
                                });
                            }
                            else{
                                Toast.makeText(AddProfileActivity.this, "Error "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });



                }
            }
        });

    }

    private void openGalleryForBack() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(intent,PICK_Back_Request);
    }

    private void openGallery() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(intent,PICK_Profile_Request);
    }

    private String getFileExt(Uri uri){
        ContentResolver cr=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }
    Uri uri,uriBack;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_Profile_Request && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            uri=data.getData();
            mProfileImg.setImageURI(uri);
        }
        if(requestCode==PICK_Back_Request && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            uriBack=data.getData();
            mBackImg.setImageURI(uriBack);
        }
    }

    private void inits() {
        View v=findViewById(R.id.addProfile_toolbar);
        mToolbar=v.findViewById(R.id.transparentToolbar);
        mUsername=findViewById(R.id.addProfile_username);
        mGender=findViewById(R.id.genderSpinner);
        mBDay=findViewById(R.id.addProfile_date);
        mCreate=findViewById(R.id.addProfile_create);
        mProfileImg=findViewById(R.id.addProfile_profileImg);
        mBackImg=findViewById(R.id.addProfile_bgImg);
        mProfileCard=findViewById(R.id.addProfile_profileCard);
        mBackCard=findViewById(R.id.addProfile_bgCard);
        mProgressBar=findViewById(R.id.addProfile_progressBar);
    }
}