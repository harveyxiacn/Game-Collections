package com.itgarage.harvey.gamecollections.adapters;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.itgarage.harvey.gamecollections.activities.NaviDrawerActivity;
import com.itgarage.harvey.gamecollections.tabs.AllGameTab;
import com.itgarage.harvey.gamecollections.tabs.FavouriteGameTab;
import com.itgarage.harvey.gamecollections.tabs.LendGameTab;
import com.itgarage.harvey.gamecollections.tabs.WishGameTab;

/**
 * Created by harvey on 2015-03-05.
 */
public class TabViewPagerAdapter extends FragmentStatePagerAdapter {

    CharSequence Titles[];
    int numberOfTabs;
    Activity activity;

    public TabViewPagerAdapter(FragmentManager fm, CharSequence[] titles, int numberOfTabs, Activity activity) {
        super(fm);
        Titles = titles;
        this.numberOfTabs = numberOfTabs;
        this.activity = activity;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                NaviDrawerActivity.CURRENT_TAB = "All";
                return new AllGameTab();
            case 1:
                NaviDrawerActivity.CURRENT_TAB = "Like";
                return new FavouriteGameTab();
            case 2:
                NaviDrawerActivity.CURRENT_TAB = "Lend";
                return new LendGameTab();
            case 3:
                NaviDrawerActivity.CURRENT_TAB = "Wish";
                return new WishGameTab();
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }
}
