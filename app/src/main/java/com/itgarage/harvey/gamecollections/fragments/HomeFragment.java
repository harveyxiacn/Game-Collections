package com.itgarage.harvey.gamecollections.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itgarage.harvey.gamecollections.R;
import com.itgarage.harvey.gamecollections.activities.NaviDrawerActivity;
import com.itgarage.harvey.gamecollections.adapters.ImageSlideAdapter;
import com.itgarage.harvey.gamecollections.db.GamesDataSource;
import com.itgarage.harvey.gamecollections.models.Game;

import java.util.List;

//import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;

/**
 * Created by harvey on 2015/2/16.
 */
public class HomeFragment extends Fragment {
    static int focusedPage;
    NaviDrawerActivity activity;
    public static ImageSlideAdapter imageSlideAdapter;
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
        //final AutoScrollViewPager viewPager = (AutoScrollViewPager) rootView.findViewById(R.id.homeSlider);
        final ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.homeSlider);
        GamesDataSource dataSource = new GamesDataSource(rootView.getContext());
        dataSource.open();
        List<Game> gameList = dataSource.getAllGames();
        dataSource.close();
        imageSlideAdapter = new ImageSlideAdapter(rootView.getContext(), gameList, viewPager);
        //viewPager.setAdapter(adapter);
        //viewPager.setCurrentItem(0);

        //viewPager.startAutoScroll(1000);
        /*viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                focusedPage = position;
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //viewPager.setCurrentItem(0, false);
            }
        });*/
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((NaviDrawerActivity) activity).onSectionAttached(1);
        this.activity = (NaviDrawerActivity) activity;
    }

}
