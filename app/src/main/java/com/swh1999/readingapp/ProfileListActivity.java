package com.swh1999.readingapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ProfileListActivity extends AppCompatActivity {
Toolbar mToolbar;
ListView mListView;
ArrayList<String> mlist;
FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_list);

        init();
        fAuth=FirebaseAuth.getInstance();

        mToolbar.setTitle("Setting");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mlist=new ArrayList<>();
        mlist.add("Account Settings");
        mlist.add("About App");
        mlist.add("Log out");

        ArrayAdapter adapter=new ArrayAdapter(ProfileListActivity.this, android.R.layout.simple_list_item_1,mlist);

        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("gg","i="+i);
                int pos=mListView.getSelectedItemPosition();
                Log.e("gg","pos="+pos);
                switch (i){
                    case 0:
                        Intent intent=new Intent(ProfileListActivity.this, EditProfileActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        AlertDialog.Builder mBuilder=new AlertDialog.Builder(ProfileListActivity.this,R.style.AlertDialog_custom);
                        mBuilder.setTitle("Message");
                        mBuilder.setMessage("Are you sure to log out from this email?");
                        mBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                fAuth.signOut();
                                Intent intent1=new Intent(ProfileListActivity.this,LoginActivity.class);
                                startActivity(intent1);
                            }
                        });
                        mBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
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
                        break;

                }
            }
        });



    }
    public void init(){
        View view=findViewById(R.id.profileList_toolbar);
        mToolbar=view.findViewById(R.id.transparentNormalToolbar);
        mListView=findViewById(R.id.profileList_listView);
    }
}