package com.swh1999.readingapp;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditPartInfoActivity extends AppCompatActivity {

    String uid,title,partTitle,key;
    EditText mPartTitle,mPartDes;
    TextView mWordCount;
    Toolbar mToolbar;
    Button mButton;
    ConstraintLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_part_info);

        uid=getIntent().getStringExtra("uid");
        title=getIntent().getStringExtra("title");
        partTitle=getIntent().getStringExtra("partTitle");
        key="";

        init();

        mToolbar.setTitle("Edit Part Info");
        setSupportActionBar(mToolbar);
        Drawable menu=getResources().getDrawable(R.drawable.ic_back);
        menu.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getPartData();

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
                    mWordCount.setText(String.valueOf(mPartDes.getText().toString().length()));
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editPartInfo();
            }
        });


    }

    private void editPartInfo() {
        String pTitle=mPartTitle.getText().toString();
        String pDes=mPartDes.getText().toString();
        FirebaseDatabase.getInstance().getReference("Part").child(uid).child(title).child(key)
                .child("partTitle").setValue(pTitle);
        FirebaseDatabase.getInstance().getReference("Part").child(uid).child(title).child(key)
                .child("partDes").setValue(pDes);

        finish();
    }

    private void getPartData() {
        Log.e("gg",title+" "+uid+" "+partTitle);
        FirebaseDatabase.getInstance().getReference("Part").child(uid).child(title)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot snapshot1:snapshot.getChildren()){
                            if(snapshot1.child("partTitle").getValue().equals(partTitle)){
                                mPartTitle.setText(snapshot1.child("partTitle").getValue().toString());
                                mPartDes.setText(snapshot1.child("partDes").getValue().toString());
                                key=snapshot1.getKey();
                                int count=mPartDes.getText().toString().length();
                                mWordCount.setText(String.valueOf(count));
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void  init(){
        mPartTitle=findViewById(R.id.editPart_pTitle);
        mPartDes=findViewById(R.id.editPart_pDes);
        mWordCount=findViewById(R.id.editPart_wordCount);
        View v=findViewById(R.id.editPart_toolbar);
        mToolbar=v.findViewById(R.id.transparentNormalToolbar);
        mButton=findViewById(R.id.editPart_btn);
        layout=findViewById(R.id.editPart_layout);
    }
}