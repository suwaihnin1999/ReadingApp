package com.swh1999.readingapp;

import static com.swh1999.readingapp.R.layout.birthday_alert_layout;
import static com.swh1999.readingapp.R.layout.gender_alert_layout;
import static com.swh1999.readingapp.R.layout.pass_alert_layout;
import static com.swh1999.readingapp.R.layout.username_alert_layout;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {
Toolbar mToolbar;
RecyclerView mAboutRecycler;
LinearLayout profileLayout,backLayout;
FirebaseAuth fAuth;
DatabaseReference reff;
String uid,email;
List<ProfileAbout> mProfile;
CircleImageView mProfilePic;
ImageView mBgPic;
FirebaseUser fUser;
StorageReference storageReference;
Uri uri,uriBack;
ProgressBar mProgressBar;
public static int PICK_Back_Request=1;
public static int PICK_Profile_Request=2;
private boolean isOpen=false;
CoordinatorLayout mCoordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        init();

        mToolbar.setTitle("Account Settings");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        fAuth=FirebaseAuth.getInstance();
        fUser=fAuth.getCurrentUser();
        uid=fAuth.getCurrentUser().getUid();

        mAboutRecycler.setLayoutManager(new LinearLayoutManager(EditProfileActivity.this));

        mProfile=new ArrayList<>();
        reff= FirebaseDatabase.getInstance().getReference("Profile").child(uid);
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mProfile.clear();
                Glide.with(EditProfileActivity.this).load(snapshot.child("profileImg").getValue().toString()).into(mProfilePic);
                Glide.with(EditProfileActivity.this).load(snapshot.child("backImg").getValue().toString()).into(mBgPic);

                ProfileInfo profileInfo=snapshot.getValue(ProfileInfo.class);
                ProfileAbout name=new ProfileAbout("Username",snapshot.child("username").getValue().toString());
                ProfileAbout birthday=new ProfileAbout("BirthDay",snapshot.child("birthDay").getValue().toString());
                ProfileAbout pass=new ProfileAbout("Password",snapshot.child("pass").getValue().toString());
                ProfileAbout gender=new ProfileAbout("Gender",snapshot.child("gender").getValue().toString());
                mProfile.add(name);mProfile.add(pass);mProfile.add(gender);mProfile.add(birthday);

                mAboutRecycler.setAdapter(new ProfileAdapter(EditProfileActivity.this,
                        mProfile,
                        new ProfileAdapter.onItemClickListener(){
                            @Override
                            public void onItemClick(ProfileAbout item) {
                                switch (item.title){
                                    case "Username":changeUsername(item.value);break;
                                    case "Password":changePass(item.value);break;
                                    case "BirthDay":changeBDay(item.value);break;
                                    case "Gender":changeGender(item.value);break;
                                }
                            }
                        }));
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        storageReference= FirebaseStorage.getInstance().getReference("Profile");
        //todo change profile img;
        profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();

            }
        });

        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBackGallery();

            }
        });

    }

    private void changeBDay(String value) {
        AlertDialog.Builder mBuilder=new AlertDialog.Builder(EditProfileActivity.this,R.style.AlertDialog_custom);
        mBuilder.setTitle("Edit Your Birthday");
        View view=getLayoutInflater().inflate(birthday_alert_layout,null);
        DatePicker mDatePicker=view.findViewById(R.id.birthday_dtPicker);

        FrameLayout container = new FrameLayout(EditProfileActivity.this);
        FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        params.topMargin=getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        params.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        params.bottomMargin = getResources().getDimensionPixelSize(R.dimen.dialog_marginBot);
        view.setLayoutParams(params);
        container.addView(view);

        mBuilder.setView(container);

        mBuilder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                reff.child("birthDay").setValue(mDatePicker.getDayOfMonth()+"/"+(mDatePicker.getMonth()+1)+"/"+mDatePicker.getYear());
                Snackbar snackbar_su = Snackbar
                        .make(mCoordinatorLayout, "Successfully changed!", Snackbar.LENGTH_LONG);
                snackbar_su.show();
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

    private void changeGender(String value) {
        AlertDialog.Builder mBuilder=new AlertDialog.Builder(EditProfileActivity.this,R.style.AlertDialog_custom);
        mBuilder.setTitle("Edit Your Gender");
        View view=getLayoutInflater().inflate(gender_alert_layout,null);
        Spinner mGender=view.findViewById(R.id.gender_spinner);

        FrameLayout container = new FrameLayout(EditProfileActivity.this);
        FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        params.topMargin=getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        params.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        params.bottomMargin = getResources().getDimensionPixelSize(R.dimen.dialog_marginBot);
        view.setLayoutParams(params);
        container.addView(view);

        mBuilder.setView(container);

        mBuilder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                reff.child("gender").setValue(mGender.getSelectedItem().toString());
                Snackbar snackbar_su = Snackbar
                        .make(mCoordinatorLayout, "Successfully changed!", Snackbar.LENGTH_LONG);
                snackbar_su.show();
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

    private void changePass(String value) {
        AlertDialog.Builder mBuilder=new AlertDialog.Builder(EditProfileActivity.this,R.style.AlertDialog_custom);
        mBuilder.setTitle("Edit Username");
        View view=getLayoutInflater().inflate(pass_alert_layout,null);
        EditText mPass=view.findViewById(R.id.pass_editText);
        EditText mConPass=view.findViewById(R.id.conPass_editText);
        EditText mOldPass=view.findViewById(R.id.oldPass_editText);
        ImageView passEye=view.findViewById(R.id.pass_eye);
        ImageView ConPassEye=view.findViewById(R.id.ConPass_eye);
        ImageView OldPassEye=view.findViewById(R.id.OldPass_eye);

        passEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isOpen){
                    passEye.setSelected(true);
                    isOpen=true;
                    mPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else{
                    passEye.setSelected(false);
                    isOpen=false;
                    mPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        ConPassEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isOpen){
                    ConPassEye.setSelected(true);
                    isOpen=true;
                    mConPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else{
                    ConPassEye.setSelected(false);
                    isOpen=false;
                    mConPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        mOldPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isOpen){
                    OldPassEye.setSelected(true);
                    isOpen=true;
                    mOldPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else{
                    OldPassEye.setSelected(false);
                    isOpen=false;
                    mOldPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        FrameLayout container = new FrameLayout(EditProfileActivity.this);
        FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        params.topMargin=getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        params.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        params.bottomMargin = getResources().getDimensionPixelSize(R.dimen.dialog_marginBot);
        view.setLayoutParams(params);
        container.addView(view);

        mBuilder.setView(container);

        mBuilder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(value.equals(mOldPass.getText().toString())){
                    if(mPass.getText().toString().equals(mConPass.getText().toString())){
                        reff.child("pass").setValue(mPass.getText().toString());
                        //fAuth.pas()
                        changeFAuthPass(mOldPass.getText().toString(),mPass.getText().toString());

                    }
                    else{
                        Snackbar snackbar_su = Snackbar
                                .make(mCoordinatorLayout, "Your passwords are not match!", Snackbar.LENGTH_LONG);
                        snackbar_su.show();
                    }
                }
                else{
                    Snackbar snackbar_su = Snackbar
                            .make(mCoordinatorLayout, "Your current password is wrong!", Snackbar.LENGTH_LONG);
                    snackbar_su.show();
                }
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

    private void changeFAuthPass(String oldpass,String newPass){
        email=fUser.getEmail();
        AuthCredential credential= EmailAuthProvider.getCredential(email,oldpass);
        fUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    fUser.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(!task.isSuccessful()){
                                Snackbar snackbar_fail = Snackbar
                                        .make(mCoordinatorLayout, "Something went wrong. Please try again later", Snackbar.LENGTH_LONG);
                                snackbar_fail.show();
                            }else {
                                Snackbar snackbar_su = Snackbar
                                        .make(mCoordinatorLayout, "Password Successfully Modified", Snackbar.LENGTH_LONG);
                                snackbar_su.show();
                            }
                        }
                    });
                }
                else{
                    Snackbar snackbar_su = Snackbar
                            .make(mCoordinatorLayout,"Failed! "+ task.getException().getMessage().toString(), Snackbar.LENGTH_LONG);
                    snackbar_su.show();
                }
            }
        });

    }

    private void changeUsername(String value) {
        AlertDialog.Builder mBuilder=new AlertDialog.Builder(EditProfileActivity.this,R.style.AlertDialog_custom);
        mBuilder.setTitle("Edit Username");
        View view=getLayoutInflater().inflate(username_alert_layout,null);
        EditText mUsername=view.findViewById(R.id.username_editText);
        mUsername.setText(value);

        FrameLayout container = new FrameLayout(EditProfileActivity.this);
        FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        params.topMargin=getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        params.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        params.bottomMargin = getResources().getDimensionPixelSize(R.dimen.dialog_marginBot);
        view.setLayoutParams(params);
        container.addView(view);

        mBuilder.setView(container);
        
        mBuilder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                reff.child("username").setValue(mUsername.getText().toString());
                Snackbar snackbar_su = Snackbar
                        .make(mCoordinatorLayout, "Successfully changed!", Snackbar.LENGTH_LONG);
                snackbar_su.show();
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

    private void openGallery() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(intent,PICK_Profile_Request);
    }

    private void openBackGallery(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(intent,PICK_Back_Request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("gg",requestCode+" "+PICK_Profile_Request+" "+PICK_Back_Request);
        if(requestCode==PICK_Profile_Request && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            mProgressBar.setVisibility(View.VISIBLE);
            uri=data.getData();
            Log.e("gg",uri.toString());
            //mProfilePic.setImageURI(uri);

            StorageReference sf=storageReference.child(System.currentTimeMillis()+"."+getFileExt(uri));
            sf.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    sf.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.e("gg","uri="+uri.toString());
                            reff.child("profileImg").setValue(uri.toString());
                            mProgressBar.setVisibility(View.GONE);
                            Toast.makeText(EditProfileActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        }
                    });
                    sf.getDownloadUrl().addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }

        if(requestCode==PICK_Back_Request && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            mProgressBar.setVisibility(View.VISIBLE);
            uriBack=data.getData();
            Log.e("gg",uri.toString());
            //mProfilePic.setImageURI(uri);

            StorageReference sf=storageReference.child(System.currentTimeMillis()+"."+getFileExt(uriBack));
            sf.putFile(uriBack).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    sf.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.e("gg","uri="+uri.toString());
                            reff.child("backImg").setValue(uri.toString());
                            mProgressBar.setVisibility(View.GONE);
                            Toast.makeText(EditProfileActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        }
                    });
                    sf.getDownloadUrl().addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

    private String getFileExt(Uri uri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void init(){
        View view=findViewById(R.id.editProfile_toolbar);
        mToolbar=view.findViewById(R.id.transparentNormalToolbar);
        mAboutRecycler=findViewById(R.id.editProfile_aboutRecycler);
        mProfilePic=findViewById(R.id.profilePic_profile);
        mBgPic=findViewById(R.id.profilePic_bg);
        profileLayout=findViewById(R.id.profile_linearLayout);
        backLayout=findViewById(R.id.background_linearLayout);
        mProgressBar=findViewById(R.id.editProfile_progressbar);
        mCoordinatorLayout=findViewById(R.id.coordinatorLayout);
    }
}