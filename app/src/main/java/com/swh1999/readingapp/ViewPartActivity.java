package com.swh1999.readingapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewPartActivity extends AppCompatActivity {

    String title,uid,partTitle,titleNew,privacy,key;
    TextView mPartTitle,mPartDes;
    Toolbar mToolbar;
    ConstraintLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_part);

        title=getIntent().getStringExtra("storyTitle");
        uid=getIntent().getStringExtra("uid");
        partTitle=getIntent().getStringExtra("partTitle");
        titleNew=getIntent().getStringExtra("storyTitleNew");
        privacy="";

        init();

        mToolbar.setTitle(titleNew);
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


        getPartData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.editpart_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.partPublic:changePublic();break;
            case R.id.partPrivate:changePrivate();break;
            case R.id.partdelete:deletePart();break;
            case R.id.partedit:Intent intent=new Intent(ViewPartActivity.this,EditPartInfoActivity.class);
                                intent.putExtra("title",title);
                                intent.putExtra("uid",uid);
                                intent.putExtra("partTitle",mPartTitle.getText().toString());
                                startActivity(intent);break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deletePart() {
        AlertDialog.Builder mBuilder=new AlertDialog.Builder(ViewPartActivity.this,R.style.AlertDialog_custom);
        mBuilder.setTitle("Alert");
        mBuilder.setMessage("Are you sure to delete this part?");
        mBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseDatabase.getInstance().getReference("Voter").child(uid).child(title).child(key).child("vote")
                        .removeValue();

                FirebaseDatabase.getInstance().getReference("Part").child(uid).child(title).child(key)
                        .removeValue();
                Snackbar.make(layout,"Successfully deleted.",Snackbar.LENGTH_LONG).show();
                finish();
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

    private void changePrivate() {
        if(privacy.equals("private")){
            Snackbar.make(layout,"Your privacy is already private.",Snackbar.LENGTH_LONG).show();
        }
        else{
            Log.e("gg",key);
            FirebaseDatabase.getInstance().getReference("Part").child(uid).child(title).child(key)
                    .child("privacy").setValue("private");
            Snackbar.make(layout,"Successfully change your part's privacy.",Snackbar.LENGTH_LONG).show();
        }
    }

    private void changePublic() {
        if(privacy.equals("public")){
            Snackbar.make(layout,"Your privacy is already public.",Snackbar.LENGTH_LONG).show();
        }
        else{
            Log.e("gg",key);
            FirebaseDatabase.getInstance().getReference("Part").child(uid).child(title).child(key)
                    .child("privacy").setValue("public");
            Snackbar.make(layout,"Successfully change your part's privacy.",Snackbar.LENGTH_LONG).show();
        }

    }


    private void getPartData() {
        Log.e("gg",title+" "+uid+" "+partTitle);
        FirebaseDatabase.getInstance().getReference("Part").child(uid).child(title)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    if(snapshot1.child("partTitle").getValue().equals(partTitle)){
                        //mPartTitle.setText(snapshot1.child("partTitle").getValue().toString());
                        //mPartDes.setText(snapshot1.child("partDes").getValue().toString());
                        //privacy=snapshot1.child("privacy").getValue().toString();
                        key=snapshot1.getKey();
                    }
                }
                getPartInfo();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPartInfo() {
        Log.e("gg","key="+key);
        FirebaseDatabase.getInstance().getReference("Part").child(uid).child(title).child(key)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //for(DataSnapshot snapshot1:snapshot.getChildren()){
                                mPartTitle.setText(snapshot.child("partTitle").getValue().toString());
                                mPartDes.setText(snapshot.child("partDes").getValue().toString());
                                privacy=snapshot.child("privacy").getValue().toString();
                        //}

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void init(){
        mPartTitle=findViewById(R.id.viewPart_partTitle);
        mPartDes=findViewById(R.id.viewPart_partDes);
        View v=findViewById(R.id.viewPart_toolbar);
        mToolbar=v.findViewById(R.id.transparentNormalToolbar);
        layout=findViewById(R.id.viewPart_layout);

    }
}