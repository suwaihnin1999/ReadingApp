package com.swh1999.readingapp;

import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ReadBookActivity extends AppCompatActivity implements DrawerLayout.DrawerListener {
    String title, authorId;
    Toolbar mToolbar;
    DatabaseReference reff;
    TextView mPartTitle, mPartDes, mVote, mViewer, mCmt;
    DrawerLayout mDrawer;
    DatabaseReference reffBook, reffUser, reffPart, reffSelectedPart, reffVoterInfo, reffVote, reffVote1, reffTotVoteIncrease, reffTotVoteDecrease;
    ArrayList<StoryPartInfo> partList;
    BottomNavigationView botNaviView;
    String key, uid, totalkey, keyStory;
    FirebaseAuth fAuth;
    ProgressBar mProgress;
    NestedScrollView mScroll;
    int vote, viewer, totalVote;
    NavigationView nav_view;
    TextView bookName,authorName;
    ImageView bookImg;
    ConstraintLayout mLayout;
    String LibraryKey;
    int scrollYPos,progressMax,NaviItem;
    String partTitle;
    boolean checkLibrary;
    CoordinatorLayout layout;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_book);

        title = getIntent().getStringExtra("title");
        authorId = getIntent().getStringExtra("authorId");

        Log.e("gg", authorId + " " + title);

        fAuth = FirebaseAuth.getInstance();
        uid = fAuth.getCurrentUser().getUid();

        inits();
        partList = new ArrayList<>();
        checkLibrary=false;
        NaviItem=0;//partTitle position


        setSupportActionBar(mToolbar);
        Drawable menu1 = getResources().getDrawable(R.drawable.ic_back);
        menu1.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(menu1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkLibrary==true){
                    updateReadingPos();
                }
                else{

                }
                Log.e("gg","scrollY="+mScroll.getScrollY());
                finish();
            }
        });

        partTitle="";
        scrollYPos=0;

        //todo check library and get partTitle and scrollPos
        checkLibraryBook();


        //todo get storyInfo(name,image,(totalvote for inc/dec)
        getStoryInfo();

        //todo get author info(name)
        getAuthorInfo();


        //todo set no.of part add to navigation view
        setStoryPartCount();


        //todo part from nav_view click event
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                removeColor(nav_view);
                for (int i = 0; i < partList.size(); i++) {
                    if (i == item.getItemId()) {
                        item.setChecked(true);
                        partTitle = partList.get(i).getPartTitle();

                        //todo get selected partInfo(name,vote,view)
                        getSelectedPartInfo(partTitle,0,0);

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
                switch (item.getItemId()) {
                    case R.id.vote:
                        item.setChecked(true);

                        //todo set Vote history under Voter
                        FirebaseDatabase.getInstance().getReference("Voter").child(authorId).child(title).child(key)
                                .child("vote").child(uid).setValue(true);

                        //todo set story total like increase
                        Log.e("gg", "totalKey" + keyStory + " total Vote" + totalVote);
                        reffTotVoteIncrease = FirebaseDatabase.getInstance().getReference("Story").child(keyStory);
                        reffTotVoteIncrease.child("totalVote").setValue(totalVote + 1);

                        Snackbar.make(layout, "Thanks for Voting.", Snackbar.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        //todo remove vote event
        botNaviView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.vote:
                        item.setChecked(false);
                        //todo remove Vote history under Voetr
                         FirebaseDatabase.getInstance().getReference("Voter").child(authorId).child(title).child(key)
                        .child("vote").child(uid).removeValue();

                        //todo set story total like decrese
                        Log.e("gg", "totalKey" + keyStory + " total Vote" + totalVote);
                        reffTotVoteDecrease = FirebaseDatabase.getInstance().getReference("Story").child(keyStory);
                        reffTotVoteDecrease.child("totalVote").setValue(totalVote - 1);

                        Snackbar.make(layout, "Your vote has been removed.", Snackbar.LENGTH_SHORT).show();
                }
            }
        });


        //todo set progressbar color
        mProgress.getProgressDrawable().setColorFilter(
                getResources().getColor(R.color.orange),PorterDuff.Mode.SRC_IN);

        mScroll.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                Log.e("gg",mScroll.getChildAt(0).getHeight()+" "+mScroll.getHeight());

                int totalScrollLength=mScroll.getChildAt(0).getHeight()-mScroll.getHeight();
                mProgress.setMax(totalScrollLength);
                mProgress.setProgress(i1);
                progressMax=totalScrollLength;
                scrollYPos=i1;
                Log.e("gg",totalScrollLength+" "+i1);
            }
        });
    }

    private void canScroll(){
        ViewTreeObserver viewTreeObserver = mScroll.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(mScroll.canScrollVertically(1) || mScroll.canScrollVertically(-1)){

                }
                else{
                    mProgress.setMax(100);
                    mProgress.setProgress(100);
                }
            }
        });

    }

    private void checkLibraryBook(){
        FirebaseDatabase.getInstance().getReference("Library").child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot snapshot1:snapshot.getChildren()){
                            if(snapshot1.child("authorId").getValue().equals(authorId)
                                    && snapshot1.child("title").getValue().equals(title)){
                                partTitle=snapshot1.child("partTitle").getValue().toString();
                                scrollYPos=Integer.parseInt(snapshot1.child("scrollPos").getValue().toString());
                                progressMax=Integer.parseInt(snapshot1.child("progressMax").getValue().toString());
                                checkLibrary=true;
                                LibraryKey=snapshot1.getKey();
                                Log.e("gg","libraryPart="+partTitle+" "+LibraryKey);
                            }
                        }


                        //todo partInfo(title,des,vote,view)
                        getFirstPartInfo();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void updateReadingPos(){
        FirebaseDatabase.getInstance().getReference("Library").child(uid).child(LibraryKey)
                .child("partTitle").setValue(partTitle);
        FirebaseDatabase.getInstance().getReference("Library").child(uid).child(LibraryKey)
                .child("scrollPos").setValue(scrollYPos);
        FirebaseDatabase.getInstance().getReference("Library").child(uid).child(LibraryKey)
                .child("progressMax").setValue(progressMax);
    }




    private void getSelectedPartInfo(String partTitle,int pos,int progressMax) {
        FirebaseDatabase.getInstance().getReference("Part").child(authorId).child(title)
        .orderByChild("partTitle").equalTo(partTitle).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    mPartTitle.setText(snapshot1.child("partTitle").getValue().toString());
                    mPartDes.setText(snapshot1.child("partDes").getValue().toString());

                    viewer = Integer.parseInt(snapshot1.child("partView").getValue().toString());
                    if (viewer >= 1000) {
                        double view = Integer.parseInt(snapshot1.child("partView").getValue().toString()) / 1000;
                        mViewer.setText(view + "k");
                    } else {
                        mViewer.setText(String.valueOf(viewer));
                    }

                    key = snapshot1.getKey();
                    getPartVoteCount();
                    checkVoteOrNot();

                    if(progressMax==0){
                        mScroll.scrollTo(0,0);
                        mProgress.setProgress(0);
                    }
                    else{
                        mProgress.setMax(progressMax);
                        mProgress.setProgress(pos);
                        mScroll.post(new Runnable() {
                            public void run() {
                                mScroll.scrollTo(0,pos);
                            }
                        });
                    }
                    Log.e("gg","pos="+pos);

                    canScroll();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setStoryPartCount() {
        reffPart = FirebaseDatabase.getInstance().getReference("Part").child(authorId).child(title);
        reffPart.addValueEventListener(new ValueEventListener()  {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                partList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Log.e("gg", "count" + snapshot.getChildrenCount());
                    StoryPartInfo partInfo = snapshot1.getValue(StoryPartInfo.class);
                    partList.add(partInfo);
                }
                Log.e("gg", "size=" + partList.size());
                Menu menu = nav_view.getMenu();
                nav_view.getMenu().clear();

                for (int i = 0; i < partList.size(); i++) {
                    menu.add(0, i, Menu.NONE, partList.get(i).getPartTitle());
                    if(partTitle.equals(partList.get(i).getPartTitle())){
                        NaviItem=i;
                    }
                }
                nav_view.getMenu().getItem(NaviItem).setChecked(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getAuthorInfo() {
        FirebaseDatabase.getInstance().getReference("Profile").child(authorId)
        .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                authorName.setText(snapshot.child("username").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getStoryInfo() {
        FirebaseDatabase.getInstance().getReference("Story")
        .orderByChild("uid").equalTo(authorId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    if (snapshot1.child("storyTitle").getValue().toString().equals(title)) {
                        keyStory = snapshot1.getKey();
                        totalVote=Integer.parseInt(snapshot1.child("totalVote").getValue().toString());
                        mToolbar.setTitle(snapshot1.child("storyTitleNew").getValue().toString());
                        bookName.setText(snapshot1.child("storyTitleNew").getValue().toString());
                        Glide.with(getApplicationContext()).load(snapshot1.child("storyImg").getValue().toString()).into(bookImg);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFirstPartInfo() {
        Log.e("gg","readingPos="+partTitle+" "+scrollYPos);
        if(checkLibrary==false){
            FirebaseDatabase.getInstance().getReference("Part").child(authorId).child(title)
                    .orderByKey().limitToFirst(1).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        mPartTitle.setText(snapshot1.child("partTitle").getValue().toString());
                        mPartDes.setText(snapshot1.child("partDes").getValue().toString());
                        /*vote = Integer.parseInt(snapshot1.child("like").getValue().toString());
                        if (vote >= 1000) {
                            double v = vote / 1000;
                            mVote.setText(v + "k");
                        } else {
                            mVote.setText(String.valueOf(vote));
                        }*/
                        viewer = Integer.parseInt(snapshot1.child("partView").getValue().toString());
                        if (viewer >= 1000) {
                            double view = Integer.parseInt(snapshot1.child("partView").getValue().toString()) / 1000;
                            mViewer.setText(view + "k");
                        } else {
                            mViewer.setText(String.valueOf(viewer));
                        }
                        key = snapshot1.getKey();

                        getPartVoteCount();

                        checkVoteOrNot();
                        canScroll();

                    }



                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else{
            getSelectedPartInfo(partTitle,scrollYPos,progressMax);
        }

    }

    private void getPartVoteCount(){
        FirebaseDatabase.getInstance().getReference("Voter").child(authorId).child(title).child(key).child("vote")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        vote=Integer.parseInt(String.valueOf(snapshot.getChildrenCount()));
                        if (vote >= 1000 && vote<1000000) {
                            double v = vote / 1000;
                            mVote.setText(v + "k");
                        }
                        else if(vote>=1000000){
                            double v=vote/1000000;
                            mVote.setText(v+"M");
                        }
                        else if(vote<1000) {
                            mVote.setText(String.valueOf(vote));
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }


    private void checkVoteOrNot() {
        reffVote = FirebaseDatabase.getInstance().getReference("Voter").child(authorId).child(title).child(key).child("vote");
        reffVote.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(uid).exists()) {
                    botNaviView.getMenu().getItem(0).setChecked(true);
                }
                else{
                    removeVote(botNaviView);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void removeVote(BottomNavigationView botNaviView) {
        for (int i = 0; i < botNaviView.getMenu().size(); i++) {
            MenuItem item = botNaviView.getMenu().getItem(i);
            item.setChecked(false);
        }
    }

    private void removeColor(NavigationView nav_view) {
        for (int i = 0; i < nav_view.getMenu().size(); i++) {
            MenuItem item = nav_view.getMenu().getItem(i);
            item.setChecked(false);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.END)) {
            mDrawer.closeDrawer(GravityCompat.END);
        } else {
            if(checkLibrary==true){
                updateReadingPos();
                finish();
            }
            else{
                finish();
            }
        }
    }

    private void inits() {
        View v = findViewById(R.id.readBook_toolbar);
        mToolbar = v.findViewById(R.id.navi_toolbar);
        mPartTitle = findViewById(R.id.readBook_partTitle);
        mPartDes = findViewById(R.id.readBook_partDes);
        mDrawer = findViewById(R.id.readBook_drawer);
        botNaviView = findViewById(R.id.readBook_botNavi);
        mVote = findViewById(R.id.readBook_vote);
        mViewer = findViewById(R.id.readBook_viewer);
        mCmt = findViewById(R.id.readBook_cmt);
        mScroll=findViewById(R.id.nestedScrollView);
        mProgress=findViewById(R.id.readBook_progressbar);
        mLayout=findViewById(R.id.readBook_layout);
        layout=findViewById(R.id.readBook_Coordinatorlayout);

        //todo get header from drawer layout
        nav_view = findViewById(R.id.readBook_naviView);
        View view = nav_view.getHeaderView(0);
        bookName = view.findViewById(R.id.bookNaviHeader_bookTitle);
        authorName = view.findViewById(R.id.bookNaviHeader_authorName);
        bookImg = view.findViewById(R.id.bookNaviHeader_img);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.navi_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navi_part:
                if (mDrawer.isDrawerOpen(Gravity.RIGHT)) {
                    mDrawer.closeDrawer(Gravity.RIGHT);
                } else {
                    mDrawer.openDrawer(Gravity.RIGHT);
                }
                return true;
            case R.id.navi_textSize:
                uploadChangeTextSize();
                return true;
            case R.id.navi_color:
                changeTextColor();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeTextColor() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ReadBookActivity.this);
        mBuilder.setTitle("Change Text Color");
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.change_textcolor_layout, null);
        mBuilder.setView(view);
        Button yellow = view.findViewById(R.id.lightyellow_btn);
        Button black = view.findViewById(R.id.black_btn);
        Button green = view.findViewById(R.id.green_btn);
        Button orange = view.findViewById(R.id.orange_btn);
        Button grey = view.findViewById(R.id.grey_btn);

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
        AlertDialog dialog = mBuilder.create();
        dialog.show();

    }

    private void uploadChangeTextSize() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ReadBookActivity.this);
        mBuilder.setTitle("Change Text Size");
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.change_textsize_layout, null);
        mBuilder.setView(view);
        TextView textSize = view.findViewById(R.id.textSize);

        Button increaseBtn = view.findViewById(R.id.increase_textSize);
        increaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int Tsize = Integer.parseInt(textSize.getText().toString());
                if (Tsize >= 25) {
                    textSize.setText(String.valueOf(25));
                } else {
                    textSize.setText(String.valueOf(Tsize + 1));
                }

            }
        });
        Button decreaseBtn = view.findViewById(R.id.decrease_textSize);
        decreaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int Tsize = Integer.parseInt(textSize.getText().toString());
                if (Tsize <= 12) {
                    textSize.setText(String.valueOf(12));
                } else {
                    textSize.setText(String.valueOf(Tsize - 1));
                }

            }
        });
        mBuilder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String size = textSize.getText().toString();
                mPartDes.setTextSize(Integer.parseInt(size));
                mPartTitle.setTextSize(Integer.parseInt(size) + 4);
                mVote.setTextSize(Integer.parseInt(size) - 1);
                mViewer.setTextSize(Integer.parseInt(size) - 1);
                mCmt.setTextSize(Integer.parseInt(size) - 1);
            }
        });
        mBuilder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        mBuilder.setCancelable(false);
        AlertDialog dialog = mBuilder.create();
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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}