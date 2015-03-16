package com.itgarage.harvey.gamecollections.adapters;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.itgarage.harvey.gamecollections.activities.NaviDrawerActivity;
import com.itgarage.harvey.gamecollections.tabs.AllGameTab;
import com.itgarage.harvey.gamecollections.tabs.FavouriteGameTab;
import com.itgarage.harvey.gamecollections.tabs.LendGameTab;
import com.itgarage.harvey.gamecollections.tabs.WishGameTab;

/**
 * This class is used for fill the tabs in HomeFragment.
 */
public class TabViewPagerAdapter extends FragmentStatePagerAdapter {

    CharSequence Titles[];
    int numberOfTabs;
    Activity activity;
    final String TAG = "tabAdapter";

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
                Log.i(TAG, "Tab all");
                return new AllGameTab();
            case 1:
                NaviDrawerActivity.CURRENT_TAB = "Like";
                Log.i(TAG, "Tab like");
                return new FavouriteGameTab();
            case 2:
                NaviDrawerActivity.CURRENT_TAB = "Lend";
                Log.i(TAG, "Tab lend");
                return new LendGameTab();
            case 3:
                NaviDrawerActivity.CURRENT_TAB = "Wish";
                Log.i(TAG, "Tab wish");
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
