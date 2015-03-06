package com.itgarage.harvey.gamecollections.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.itgarage.harvey.gamecollections.R;
import com.itgarage.harvey.gamecollections.activities.NaviDrawerActivity;
import com.itgarage.harvey.gamecollections.adapters.ImageSlideAdapter;
import com.itgarage.harvey.gamecollections.adapters.TabViewPagerAdapter;
import com.itgarage.harvey.gamecollections.db.GamesDataSource;
import com.itgarage.harvey.gamecollections.tabs.SlidingTabLayout;

//import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;

/**
 * Created by harvey on 2015/2/16.
 */
public class HomeFragment extends Fragment {
    ImageSlideAdapter imageSlideAdapter;
    ViewPager viewPager;
    LinearLayout noResultLinearLayout;
    ViewPager tabViewPager;
    TabViewPagerAdapter tabViewPagerAdapter;
    SlidingTabLayout tabs;
    CharSequence Titles[] = {"All", "Like", "Lend", "Wish"};
    int numberOfTabs = Titles.length;
    GamesDataSource dataSource;
    /**
     * Returns a new instance of this fragment for the given section number.
     */
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container,
                false);
        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        tabViewPagerAdapter = new TabViewPagerAdapter(getActivity().getSupportFragmentManager(), Titles, numberOfTabs, getActivity());
        // Assigning ViewPager View and setting the adapter
        tabViewPager = (ViewPager) rootView.findViewById(R.id.tabPager);
        tabViewPager.setAdapter(tabViewPagerAdapter);
        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) rootView.findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true);// To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(tabViewPager);
        // Setup home slide box
        /*viewPager = (ViewPager) rootView.findViewById(R.id.homeSlider);
        dataSource = new GamesDataSource(rootView.getContext());
        dataSource.open();
        List<Game> gameList = dataSource.getAllGames();
        dataSource.close();
        imageSlideAdapter = new ImageSlideAdapter(getActivity(), gameList, viewPager);
        noResultLinearLayout = (LinearLayout) rootView.findViewById(R.id.noGameInDataBaseLinearLayout);
        if(gameList!=null){
            changeUIsWhenDataSetChange(true);
        }else {
            changeUIsWhenDataSetChange(false);
        }*/
        return rootView;
    }

    /**
     * Change the visibility of no result layout and view pager by the data set is empty or not.
     * @param hasData Boolean variable describes has data in data set or not.
     */
    public void changeUIsWhenDataSetChange(boolean hasData){
        if(hasData){
            noResultLinearLayout.setVisibility(View.GONE);
            viewPager.setVisibility(View.VISIBLE);
        }else {
            noResultLinearLayout.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((NaviDrawerActivity) activity).onSectionAttached(1);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("HomeFragment", "onResume");
        /*dataSource.open();
        List<Game> gameList = dataSource.getAllGames();
        dataSource.close();
        imageSlideAdapter = new ImageSlideAdapter(getActivity(), gameList, viewPager);
        if(gameList!=null){
            changeUIsWhenDataSetChange(true);
        }else {
            changeUIsWhenDataSetChange(false);
        }*/
    }
}
