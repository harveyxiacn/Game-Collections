package com.itgarage.harvey.gamecollections.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.itgarage.harvey.gamecollections.R;
import com.itgarage.harvey.gamecollections.activities.NaviDrawerActivity;
import com.itgarage.harvey.gamecollections.adapters.TabViewPagerAdapter;
import com.itgarage.harvey.gamecollections.db.GamesDataSource;
import com.itgarage.harvey.gamecollections.tabs.SlidingTabLayout;
import com.itgarage.harvey.gamecollections.utils.SortListener;
import com.itgarage.harvey.gamecollections.utils.UpdateListListener;

//import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;

/**
 * This class is used for create home fragment with slide tabs.
 */
public class HomeFragment extends Fragment {
    ViewPager viewPager;
    LinearLayout noResultLinearLayout;
    ViewPager tabViewPager;
    TabViewPagerAdapter tabViewPagerAdapter;
    SlidingTabLayout tabs;
    CharSequence Titles[] = {"All", "Like", "Lend", "Wish"};
    int numberOfTabs = Titles.length;
    GamesDataSource dataSource;
    boolean isGridLayout;
    SharedPreferences preferences;
    RecyclerView.LayoutManager gamesCardListLayoutManager;
    RecyclerView.LayoutManager gamesCardGridLayoutManager;
    final String TAG = "home";
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
        tabViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
        preferences = getActivity().getSharedPreferences("layoutPreference", Context.MODE_PRIVATE);
        isGridLayout = preferences.getBoolean("isGameListLayout", false);
        gamesCardListLayoutManager = new LinearLayoutManager(getActivity());
        // 3 is span size, 3 items in a row
        gamesCardGridLayoutManager = new GridLayoutManager(getActivity(), 3);
        dataSource = new GamesDataSource(getActivity());
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((NaviDrawerActivity) activity).onSectionAttached(1);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_game_list, menu);
        MenuItem switchLayout = menu.findItem(R.id.action_switch_layout);
        if(isGridLayout){
            switchLayout.setIcon(R.drawable.listview);
        }else {
            switchLayout.setIcon(R.drawable.gridview);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int currentPosition = tabViewPager.getCurrentItem();
        Fragment fragment = (Fragment) tabViewPagerAdapter.instantiateItem(tabViewPager, currentPosition);
        int rightPosition = currentPosition + 1;
        Fragment fragmentR = null;
        if(rightPosition>0&&rightPosition<tabViewPager.getChildCount()) {
            fragmentR = (Fragment) tabViewPagerAdapter.instantiateItem(tabViewPager, rightPosition);
        }
        int leftPosition = currentPosition - 1;
        Fragment fragmentL = null;
        if(leftPosition>0&&leftPosition<tabViewPager.getChildCount()) {
            fragmentL = (Fragment) tabViewPagerAdapter.instantiateItem(tabViewPager, leftPosition);
        }
        switch (item.getItemId()){
            case R.id.action_switch_layout:
                isGridLayout = !isGridLayout;
                //gamesAdapter.setGridLayout(isGridLayout);
                if(isGridLayout){
                    RecyclerView.LayoutManager gamesCardGridLayoutManagerCurrent = new GridLayoutManager(getActivity(), 3);
                    ((UpdateListListener)fragment).updateLayoutManager(gamesCardGridLayoutManagerCurrent, isGridLayout);
                    //gamesCardListView.setLayoutManager(gamesCardGridLayoutManager);
                    item.setIcon(R.drawable.listview);
                    if(fragmentL!=null){
                        Log.i(TAG, "Left is not null, update layout");
                        RecyclerView.LayoutManager gamesCardGridLayoutManager = new GridLayoutManager(getActivity(), 3);
                        ((UpdateListListener)fragmentL).updateLayoutManager(gamesCardGridLayoutManager, isGridLayout);
                    }
                    if(fragmentR!=null){
                        Log.i(TAG, "Right is not null, update layout");
                        RecyclerView.LayoutManager gamesCardGridLayoutManager = new GridLayoutManager(getActivity(), 3);
                        ((UpdateListListener)fragmentR).updateLayoutManager(gamesCardGridLayoutManager, isGridLayout);
                    }
                }else {
                    RecyclerView.LayoutManager gamesCardListLayoutManagerCurrent = new LinearLayoutManager(getActivity());
                    ((UpdateListListener)fragment).updateLayoutManager(gamesCardListLayoutManagerCurrent, isGridLayout);
                    //gamesCardListView.setLayoutManager(gamesCardListLayoutManager);
                    item.setIcon(R.drawable.gridview);
                    if(fragmentL!=null){
                        RecyclerView.LayoutManager gamesCardListLayoutManager = new LinearLayoutManager(getActivity());
                        ((UpdateListListener)fragmentL).updateLayoutManager(gamesCardListLayoutManager, isGridLayout);
                    }
                    if(fragmentR!=null){
                        RecyclerView.LayoutManager gamesCardListLayoutManager = new LinearLayoutManager(getActivity());
                        ((UpdateListListener)fragmentR).updateLayoutManager(gamesCardListLayoutManager, isGridLayout);
                    }
                }
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("isGameListLayout", isGridLayout);
                editor.apply();
                break;
            case R.id.action_sort_title:
                Toast.makeText(getActivity(), "Sort title", Toast.LENGTH_SHORT).show();
                ((SortListener)fragment).onSortByTitle();
                if(fragmentL!=null){
                    ((SortListener)fragmentL).onSortByTitle();
                }
                if(fragmentR!=null){
                    ((SortListener)fragmentR).onSortByTitle();
                }
                //onSortByTitle();
                break;
            case R.id.action_sort_platform:
                Toast.makeText(getActivity(), "Sort platform", Toast.LENGTH_SHORT).show();
                ((SortListener)fragment).onSortByPlatform();
                if(fragmentL!=null){
                    ((SortListener)fragmentL).onSortByPlatform();
                }
                if(fragmentR!=null){
                    ((SortListener)fragmentR).onSortByPlatform();
                }
                //onSortByPlatform();
                break;
            case R.id.action_sort_rating:
                Toast.makeText(getActivity(), "Sort rating", Toast.LENGTH_SHORT).show();
                ((SortListener)fragment).onSortByRating();
                if(fragmentL!=null){
                    ((SortListener)fragmentL).onSortByRating();
                }
                if(fragmentR!=null){
                    ((SortListener)fragmentR).onSortByRating();
                }
                //onSortByRating();
                break;
        }
        return true;
    }
}
