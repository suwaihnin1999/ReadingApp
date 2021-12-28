package com.swh1999.readingapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    ArrayList<Fragment> fragmentList;
    FragmentManager supportFragmentManager;
    String [] title={"Follower","Following"};
    public ViewPagerAdapter(FragmentManager supportFragmentManager, ArrayList<Fragment> fragmentList) {
        super(supportFragmentManager);
        this.supportFragmentManager=supportFragmentManager;
        this.fragmentList=fragmentList;
    }



    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    public CharSequence getPageTitle(int position){
        return title[position];
    }
}
