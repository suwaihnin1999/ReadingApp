package com.swh1999.readingapp;

import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddPartActivity extends AppCompatActivity {
Toolbar mToolbar;
EditText mPartTitle,mPartDes;
Button mUpload;
String storyTitle,storyDes;
DatabaseReference reff,reffPartVote;
FirebaseAuth fAuth;
String uid;
ProgressBar mProgressbar;
RadioButton mPublicCheck,mPrivateCheck;
String privacy;
TextView mWordCount;
int maxid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_part);

        inits();
        //todo get current user id
        fAuth=FirebaseAuth.getInstance();
        uid=fAuth.getCurrentUser().getUid();

        storyTitle=getIntent().getStringExtra("storyTitle");
        storyDes=getIntent().getStringExtra("storyDes");
        privacy="";

        mToolbar.setTitle("Add Part");
        setSupportActionBar(mToolbar);
        Drawable menu1=getResources().getDrawable(R.drawable.ic_back);
        menu1.setColorFilter(getResources().getColor(R.color.lightblack), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(menu1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title=mPartTitle.getText().toString().trim();
                String des=mPartDes.getText().toString().trim();
                if((!title.equals("")) || (!des.equals(""))){
                    AlertDialog.Builder mBuilder=new AlertDialog.Builder(AddPartActivity.this);
                    mBuilder.setTitle("Alert");
                    mBuilder.setMessage("Your part info will delete.");
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
                else{
                    finish();
                }
            }
        });

        //todo set Story Part's info
        reff= FirebaseDatabase.getInstance().getReference("Part").child(uid).child(storyTitle);
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    maxid=(int)snapshot.getChildrenCount()+1;
                    Log.e("gg","max size="+maxid);
                }
                else{
                    maxid=1;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title=mPartTitle.getText().toString().trim();
                String des=mPartDes.getText().toString().trim();
                if(title.equals("")){
                    mPartTitle.setError("Part title required.");
                }
                else if(des.equals("")){
                    mPartDes.setError("Part des required");
                }
                else if(mPrivateCheck.isChecked()==false && mPublicCheck.isChecked()==false){
                    Toast.makeText(AddPartActivity.this, "Select your story's part privacy.", Toast.LENGTH_SHORT).show();
                }
                else{
                    mProgressbar.setVisibility(View.VISIBLE);
                    Log.e("gg","max id for firebase="+maxid);
                    if(mPrivateCheck.isChecked()){
                        privacy="private";
                    }
                    else{
                        privacy="public";
                    }
                    StoryPartInfo partInfo=new StoryPartInfo(title,des,0,privacy);
                    reff.child(String.valueOf(maxid)).setValue(partInfo);

                    Toast.makeText(AddPartActivity.this, "Successfully uploaded "+title, Toast.LENGTH_SHORT).show();
                    mProgressbar.setVisibility(View.GONE);
                    finish();


                }
            }
        });

        mPartDes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int l=mPartDes.getText().toString().length();
                if(l>20000){
                    mWordCount.setTextColor(getResources().getColor(R.color.red));
                }
                else{
                    mWordCount.setTextColor(getResources().getColor(R.color.orange));
                    mWordCount.setText(String.valueOf(l));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void inits() {
        View v=findViewById(R.id.addPart_toolbar);
        mToolbar=v.findViewById(R.id.transparentNormalToolbar);
        mPartTitle=findViewById(R.id.addPart_title);
        mPartDes=findViewById(R.id.addPart_des);
        mUpload=findViewById(R.id.addPart_upload);
        mProgressbar=findViewById(R.id.addPart_progressBar);
        mPublicCheck=findViewById(R.id.publicCheck);
        mPrivateCheck=findViewById(R.id.privateCheck);
        mWordCount=findViewById(R.id.addPart_wordCount);
    }
}