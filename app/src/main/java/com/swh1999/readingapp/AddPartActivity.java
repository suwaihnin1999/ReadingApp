package com.swh1999.readingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

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
                else{
                    mProgressbar.setVisibility(View.VISIBLE);
                    Log.e("gg","max id for firebase="+maxid);
                    StoryPartInfo partInfo=new StoryPartInfo(title,des,0,0);
                    reff.child(String.valueOf(maxid)).setValue(partInfo);

                    //todo set voter info
                    /*reffPartVote=FirebaseDatabase.getInstance().getReference("Voter").child(uid).child(storyTitle);
                    reffPartVote.child(title).setValue("");*/
                    Toast.makeText(AddPartActivity.this, "Successfully uploaded "+title, Toast.LENGTH_SHORT).show();
                    mProgressbar.setVisibility(View.GONE);
                    finish();


                }
            }
        });
    }

    private void inits() {
        View v=findViewById(R.id.addPart_toolbar);
        mToolbar=v.findViewById(R.id.transparentToolbar);
        mPartTitle=findViewById(R.id.addPart_title);
        mPartDes=findViewById(R.id.addPart_des);
        mUpload=findViewById(R.id.addPart_upload);
        mProgressbar=findViewById(R.id.addPart_progressBar);
    }
}