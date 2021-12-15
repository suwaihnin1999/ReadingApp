package com.swh1999.readingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.swh1999.readingapp.Fragment.AddStories_Fragment;
import com.swh1999.readingapp.Fragment.BookShelf_Fragment;
import com.swh1999.readingapp.Fragment.HomeFragment;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
Toolbar mToolbar;
BottomNavigationView bottomNavigationView;
CircleImageView mProfile;
DatabaseReference mReff;
FirebaseAuth fAuth;
String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inits();
        openFragment(HomeFragment.newInstance("",""));
        changeToolbar("home");

        fAuth=FirebaseAuth.getInstance();
        uid=fAuth.getCurrentUser().getUid();

        mReff= FirebaseDatabase.getInstance().getReference("Profile").child(uid);
        mReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Glide.with(MainActivity.this).load(snapshot.child("profileImg").getValue()).into(mProfile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:openFragment(HomeFragment.newInstance("",""));
                    changeToolbar("home");
                    return true;

                    case R.id.bookShelf:openFragment(BookShelf_Fragment.newInstance("",""));
                    changeToolbar("bookshelf");
                    return true;

                    case R.id.addStories:openFragment(AddStories_Fragment.newInstance("",""));
                    changeToolbar("addStories");
                    return true;

                }
               return false;
            }
        });

        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:break;
                    case R.id.bookShelf:break;
                    case R.id.addStories:break;
                }
            }
        });

        mProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,ProfileActivity.class);
                intent.putExtra("uid",uid);
                startActivity(intent);
            }
        });

    }
    public void openFragment(Fragment fragment){
        FragmentManager fm=getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        ft.replace(R.id.frameLayout,fragment);
        ft.commit();
    }

    private void changeToolbar(String fragment){
        switch (fragment){
            case "home":
            mToolbar.setTitle("Home");
            mProfile.setVisibility(View.VISIBLE);
            setSupportActionBar(mToolbar);

//            Drawable menu=getResources().getDrawable(R.drawable.ic_menu);
//            menu.setColorFilter(getResources().getColor(R.color.lightblack), PorterDuff.Mode.SRC_ATOP);
//            getSupportActionBar().setHomeAsUpIndicator(menu);
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            break;

            case "bookshelf":
                mToolbar.setTitle("BookShelf");
                mProfile.setVisibility(View.GONE);
                setSupportActionBar(mToolbar);
                getSupportActionBar().setHomeAsUpIndicator(null);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                break;

            case "addStories":
                mToolbar.setTitle("My Story");
                setSupportActionBar(mToolbar);
                mProfile.setVisibility(View.GONE);
                Drawable menu1=getResources().getDrawable(R.drawable.ic_back);
                menu1.setColorFilter(getResources().getColor(R.color.lightblack), PorterDuff.Mode.SRC_ATOP);
                getSupportActionBar().setHomeAsUpIndicator(menu1);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                break;


        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    private void inits() {
        View v=findViewById(R.id.main_toolbar);
        mToolbar=v.findViewById(R.id.transparentToolbar);
        mProfile=v.findViewById(R.id.profile_img);
        bottomNavigationView=findViewById(R.id.main_bottomNavigation);
    }
}