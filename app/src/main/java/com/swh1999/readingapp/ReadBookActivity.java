package com.swh1999.readingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class ReadBookActivity extends AppCompatActivity implements DrawerLayout.DrawerListener {
String title,authorId;
Toolbar mToolbar;
DatabaseReference reff;
TextView mPartTitle,mPartDes,mVote,mViewer,mCmt;
DrawerLayout mDrawer;
DatabaseReference reffBook,reffUser,reffPart,reffSelectedPart,reffVoterInfo,reffVote,reffVote1,reffTotVoteIncrease,reffTotVoteDecrease;
ArrayList<StoryPartInfo> partList;
BottomNavigationView botNaviView;
String key,uid,totalkey;
FirebaseAuth fAuth;
int vote,viewer,totalVote;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_book);

        title=getIntent().getStringExtra("title");
        authorId=getIntent().getStringExtra("authorId");

        Log.e("gg",authorId+" "+title);

        fAuth=FirebaseAuth.getInstance();
        uid=fAuth.getCurrentUser().getUid();

        inits();

        mToolbar.setTitle(title);
        setSupportActionBar(mToolbar);
        Drawable menu1=getResources().getDrawable(R.drawable.ic_back);
        menu1.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(menu1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        //todo get header from drawer layout
        NavigationView nav_view=findViewById(R.id.readBook_naviView);
        View view=nav_view.getHeaderView(0);
        TextView bookName=view.findViewById(R.id.bookNaviHeader_bookTitle);
        TextView authorName=view.findViewById(R.id.bookNaviHeader_authorName);
        ImageView bookImg=view.findViewById(R.id.bookNaviHeader_img);

        //todo to get story's part title,des,vote,view
        reff= FirebaseDatabase.getInstance().getReference("Part").child(authorId).child(title);
        reff.orderByKey().limitToFirst(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    mPartTitle.setText(snapshot1.child("partTitle").getValue().toString());
                    mPartDes.setText(snapshot1.child("partDes").getValue().toString());
                    vote=Integer.parseInt(snapshot1.child("like").getValue().toString());
                    if(vote>=1000){
                        double v=vote/1000;
                        mVote.setText(v+"k");
                    }
                    else{
                        mVote.setText(String.valueOf(vote));
                    }
                    viewer=Integer.parseInt(snapshot1.child("partView").getValue().toString());
                    if(viewer>=1000){
                        double view=Integer.parseInt(snapshot1.child("partView").getValue().toString())/1000;
                        mViewer.setText(view+"k");
                    }
                    else{
                        mViewer.setText(String.valueOf(viewer));
                    }
                    key=snapshot1.getKey();

                    checkVoteOrNot();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //todo get story total like
        getStoryTotalVote();

        //todo to get story's name and image
        reffBook=FirebaseDatabase.getInstance().getReference("Story").child(authorId).child(title);
        reffBook.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookName.setText(snapshot.child("storyTitleNew").getValue().toString());
                Glide.with(ReadBookActivity.this).load(snapshot.child("storyImg").getValue().toString()).into(bookImg);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //todo to get author name
        reffUser=FirebaseDatabase.getInstance().getReference("Profile").child(authorId);
        reffUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                authorName.setText(snapshot.child("username").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //todo get part info
        partList=new ArrayList<>();
        reffPart=FirebaseDatabase.getInstance().getReference("Part").child(authorId).child(title);
        reffPart.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                partList.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    Log.e("gg","count"+snapshot.getChildrenCount());
                    StoryPartInfo partInfo=snapshot1.getValue(StoryPartInfo.class);
                    partList.add(partInfo);
                }
                Log.e("gg","size="+partList.size());
                Menu menu=nav_view.getMenu();
                nav_view.getMenu().clear();

                for(int i=0;i<partList.size();i++){
                    menu.add(0,i,Menu.NONE,partList.get(i).getPartTitle());
                }
                nav_view.getMenu().getItem(0).setChecked(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //todo part from nav_view click event
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                removeColor(nav_view);
                for(int i=0;i<partList.size();i++){
                    if(i==item.getItemId()){
                        item.setChecked(true);
                        String partTitle=partList.get(i).getPartTitle();
                        reffSelectedPart=FirebaseDatabase.getInstance().getReference("Part").child(authorId).child(title);
                        reffSelectedPart.orderByChild("partTitle").equalTo(partTitle).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot snapshot1:snapshot.getChildren()){
                                    mPartTitle.setText(snapshot1.child("partTitle").getValue().toString());
                                    mPartDes.setText(snapshot1.child("partDes").getValue().toString());
                                    vote=Integer.parseInt(snapshot1.child("like").getValue().toString());
                                    if(vote>=1000){
                                        double v=vote/1000;
                                        mVote.setText(v+"k");
                                    }
                                    else{
                                        mVote.setText(String.valueOf(vote));
                                    }
                                    viewer=Integer.parseInt(snapshot1.child("partView").getValue().toString());
                                    if(viewer>=1000){
                                        double view=Integer.parseInt(snapshot1.child("partView").getValue().toString())/1000;
                                        mViewer.setText(view+"k");
                                    }
                                    else{
                                        mViewer.setText(String.valueOf(viewer));
                                    }

                                    key=snapshot1.getKey();
                                    checkVoteOrNot();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        mDrawer.closeDrawer(GravityCompat.END);

                    }
                }
                return true;
            }
        });

        //todo vote btn click event
        botNaviView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.vote:item.setChecked(true);
                                    //todo set Vote history under Voter
                                    VoterInfo voterInfo=new VoterInfo(uid);
                                    reffVoterInfo=FirebaseDatabase.getInstance().getReference("Voter").child(authorId).child(title).child(key);
                                    reffVoterInfo.child(uid).setValue(voterInfo);

                                    //todo set story part's like increase
                                    reffVote1=FirebaseDatabase.getInstance().getReference("Part").child(authorId).child(title).child(key);
                                    reffVote1.child("like").setValue(vote+1);

                                    //todo set story total like increase
                                    Log.e("gg","totalKey"+totalkey+" total Vote"+totalVote);
                                    reffTotVoteIncrease=FirebaseDatabase.getInstance().getReference("StoryViewer").child(totalkey);
                                    reffTotVoteIncrease.child("totalLike").setValue(totalVote+1);

                                    //todo get story total like and key
                                    getStoryTotalVote();
                                    Toast.makeText(ReadBookActivity.this, "Thanks for voting.", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        //todo remove vote event
        botNaviView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.vote:item.setChecked(false);
                        //todo remove Vote history under Voetr
                        reffVoterInfo=FirebaseDatabase.getInstance().getReference("Voter").child(authorId).child(title).child(key);
                        reffVoterInfo.child(uid).removeValue();

                        reffVote1=FirebaseDatabase.getInstance().getReference("Part").child(authorId).child(title).child(key);
                        reffVote1.child("like").setValue(vote-1);

                        //todo set story total like increase
                        Log.e("gg","totalKey"+totalkey+" total Vote"+totalVote);
                        reffTotVoteDecrease=FirebaseDatabase.getInstance().getReference("StoryViewer").child(totalkey);
                        reffTotVoteDecrease.child("totalLike").setValue(totalVote-1);

                        //todo get story total like and key
                        getStoryTotalVote();
                        Toast.makeText(ReadBookActivity.this, "Your vote has been removed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void getStoryTotalVote() {
        //todo get story total like and key to increase
        reffTotVoteIncrease= FirebaseDatabase.getInstance().getReference("StoryViewer");
        reffTotVoteIncrease.orderByChild("storyTitle").equalTo(title).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    if(snapshot1.child("authorId").getValue().equals(authorId)){
                        totalkey=snapshot1.getKey().toString();
                        totalVote=Integer.parseInt(snapshot1.child("totalLike").getValue().toString());

                        Log.e("gg","total="+totalkey+" "+totalVote);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkVoteOrNot() {
        reffVote=FirebaseDatabase.getInstance().getReference("Voter").child(authorId).child(title).child(key);
        reffVote.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean check=false;
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    if(snapshot1.child("voterId").getValue().equals(uid)){
                        check =true;
                    }
                }
                Log.e("gg","vote or not"+check);
                if(check==false){
                    removeVote(botNaviView);
                }
                else{
                    botNaviView.getMenu().getItem(0).setChecked(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void removeVote(BottomNavigationView botNaviView) {
        for(int i=0;i<botNaviView.getMenu().size();i++){
            MenuItem item=botNaviView.getMenu().getItem(i);
            item.setChecked(false);
        }
    }

    private void removeColor(NavigationView nav_view) {
        for(int i=0;i<nav_view.getMenu().size();i++){
            MenuItem item=nav_view.getMenu().getItem(i);
            item.setChecked(false);
        }
    }

    @Override
    public void onBackPressed() {
        if(mDrawer.isDrawerOpen(GravityCompat.END)){
            mDrawer.closeDrawer(GravityCompat.END);
        }
        else{
            finish();
        }
    }

    private void inits() {
        View v=findViewById(R.id.readBook_toolbar);
        mToolbar=v.findViewById(R.id.navi_toolbar);
        mPartTitle=findViewById(R.id.readBook_partTitle);
        mPartDes=findViewById(R.id.readBook_partDes);
        mDrawer=findViewById(R.id.readBook_drawer);
        botNaviView=findViewById(R.id.readBook_botNavi);
        mVote=findViewById(R.id.readBook_vote);
        mViewer=findViewById(R.id.readBook_viewer);
        mCmt=findViewById(R.id.readBook_cmt);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.navi_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.navi_part: if(mDrawer.isDrawerOpen(Gravity.RIGHT)){
                                 mDrawer.closeDrawer(Gravity.RIGHT);
                                    }
                                 else{
                                  mDrawer.openDrawer(Gravity.RIGHT);
                                    }
                                 return true;
            case R.id.navi_textSize:uploadChangeTextSize();
                                    return true;
            case R.id.navi_color:changeTextColor();
                                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeTextColor() {
        AlertDialog.Builder mBuilder=new AlertDialog.Builder(ReadBookActivity.this);
        mBuilder.setTitle("Change Text Color");
        LayoutInflater inflater=this.getLayoutInflater();
        View view=inflater.inflate(R.layout.change_textcolor_layout,null);
        mBuilder.setView(view);
        Button yellow=view.findViewById(R.id.lightyellow_btn);
        Button black=view.findViewById(R.id.black_btn);
        Button green=view.findViewById(R.id.green_btn);
        Button orange=view.findViewById(R.id.orange_btn);
        Button grey=view.findViewById(R.id.grey_btn);

        yellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPartTitle.setTextColor(getResources().getColor(R.color.lightyellow));
                mPartDes.setTextColor(getResources().getColor(R.color.lightyellow));
                mVote.setTextColor(getResources().getColor(R.color.lightyellow));
                mViewer.setTextColor(getResources().getColor(R.color.lightyellow));
                mCmt.setTextColor(getResources().getColor(R.color.lightyellow));
            }
        });
        black.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPartTitle.setTextColor(getResources().getColor(R.color.black));
                mPartDes.setTextColor(getResources().getColor(R.color.black));
                mVote.setTextColor(getResources().getColor(R.color.black));
                mViewer.setTextColor(getResources().getColor(R.color.black));
                mCmt.setTextColor(getResources().getColor(R.color.black));
            }
        });
        orange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPartTitle.setTextColor(getResources().getColor(R.color.orange));
                mPartDes.setTextColor(getResources().getColor(R.color.orange));
                mVote.setTextColor(getResources().getColor(R.color.orange));
                mViewer.setTextColor(getResources().getColor(R.color.orange));
                mCmt.setTextColor(getResources().getColor(R.color.orange));
            }
        });
        green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPartTitle.setTextColor(getResources().getColor(R.color.brown));
                mPartDes.setTextColor(getResources().getColor(R.color.brown));
                mVote.setTextColor(getResources().getColor(R.color.brown));
                mViewer.setTextColor(getResources().getColor(R.color.brown));
                mCmt.setTextColor(getResources().getColor(R.color.brown));
            }
        });
        grey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPartTitle.setTextColor(getResources().getColor(R.color.grey));
                mPartDes.setTextColor(getResources().getColor(R.color.grey));
                mVote.setTextColor(getResources().getColor(R.color.grey));
                mViewer.setTextColor(getResources().getColor(R.color.grey));
                mCmt.setTextColor(getResources().getColor(R.color.grey));
            }
        });
        mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        mBuilder.setCancelable(false);
        AlertDialog dialog=mBuilder.create();
        dialog.show();

    }

    private void uploadChangeTextSize() {
        AlertDialog.Builder mBuilder=new AlertDialog.Builder(ReadBookActivity.this);
        mBuilder.setTitle("Change Text Size");
        LayoutInflater inflater=this.getLayoutInflater();
        View view=inflater.inflate(R.layout.change_textsize_layout,null);
        mBuilder.setView(view);
        TextView textSize=view.findViewById(R.id.textSize);

        Button increaseBtn=view.findViewById(R.id.increase_textSize);
        increaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int Tsize=Integer.parseInt(textSize.getText().toString());
                if(Tsize>=25){
                    textSize.setText(String.valueOf(25));
                }
                else{
                    textSize.setText(String.valueOf(Tsize+1));
                }

            }
        });
        Button decreaseBtn=view.findViewById(R.id.decrease_textSize);
        decreaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int Tsize=Integer.parseInt(textSize.getText().toString());
                if(Tsize<=12){
                    textSize.setText(String.valueOf(12));
                }
                else{
                    textSize.setText(String.valueOf(Tsize-1));
                }

            }
        });
        mBuilder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String size=textSize.getText().toString();
                mPartDes.setTextSize(Integer.parseInt(size));
                mPartTitle.setTextSize(Integer.parseInt(size)+4);
                mVote.setTextSize(Integer.parseInt(size)-1);
                mViewer.setTextSize(Integer.parseInt(size)-1);
                mCmt.setTextSize(Integer.parseInt(size)-1);
            }
        });
        mBuilder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        mBuilder.setCancelable(false);
        AlertDialog dialog=mBuilder.create();
        dialog.show();

    }

    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(@NonNull View drawerView) {

    }

    @Override
    public void onDrawerClosed(@NonNull View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }
}