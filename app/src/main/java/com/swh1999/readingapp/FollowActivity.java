package com.swh1999.readingapp;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.swh1999.readingapp.Fragment.FollowFragment;
import com.swh1999.readingapp.Fragment.FollowingFragment;

import java.util.ArrayList;

public class FollowActivity extends AppCompatActivity {
Toolbar mToolbar;
TabLayout mTabLayout;
ViewPager mViewPager;
public static String followUid,username;
int temp;

ArrayList<Fragment>fragmentList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);

        followUid=getIntent().getStringExtra("uid");
        username=getIntent().getStringExtra("username");
        temp=Integer.parseInt(getIntent().getStringExtra("temp"));

        init();

        mToolbar.setTitle(username);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        fragmentList=new ArrayList<>();
        fragmentList.add(new FollowFragment());
        fragmentList.add(new FollowingFragment());



        mViewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(),fragmentList));
        mViewPager.setCurrentItem(temp);

        mTabLayout.setupWithViewPager(mViewPager);

    }
    private void init(){
        View view=findViewById(R.id.follow_toolbar);
        mToolbar=view.findViewById(R.id.transparentNormalToolbar);
        mTabLayout=findViewById(R.id.follow_tabLayout);
        mViewPager=findViewById(R.id.follow_viewPager);
    }
}