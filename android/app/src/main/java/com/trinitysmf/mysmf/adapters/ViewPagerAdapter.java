package com.trinitysmf.mysmf.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.trinitysmf.mysmf.ui.fragments.FeedFragment;
import com.trinitysmf.mysmf.ui.fragments.HomeFragment;
import com.trinitysmf.mysmf.ui.fragments.EventsFragment;
import com.trinitysmf.mysmf.ui.fragments.NewsFragment;

/**
 * Created by namxn_000 on 21/10/2017.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {
    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new HomeFragment();
            case 1:
                return new FeedFragment();
            case 2:
                return new EventsFragment();
            //case 3:
            //    return new NewsFragment();
        }
        return new HomeFragment();

    }

    @Override
    public int getCount() {
        return 3;
    }
}
