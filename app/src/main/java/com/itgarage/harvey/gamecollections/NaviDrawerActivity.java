package com.itgarage.harvey.gamecollections;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;


public class NaviDrawerActivity extends ActionBarActivity {

    private Toolbar toolbar;
    String NAME = "Harvey Xia";
    String EMAIL = "harvey1991cn@gmail.com";
    int PROFILE = R.drawable.ic_user;
    String[] ITEM_NAMES;
    int[] ITEM_ICONS;

    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    DrawerLayout mDrawer;
    ActionBarDrawerToggle mDrawerToggle;
    FragmentManager fragmentManager;
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navi_drawer_overlay_toolbar); //R.layout.activity_navi_drawer is not overlay the toolbar
        // set up toolbar
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        // get title from toolbar
        mTitle = getTitle();
        // set default fragment to home page
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment.newInstance()).commit();
        // get drawer list items' names and icons from DrawerListItems
        ITEM_NAMES = DrawerListItems.ITEM_NAMES;
        ITEM_ICONS = DrawerListItems.ITEM_ICONS;
        // setup recyclerview
        mRecyclerView = (RecyclerView) findViewById(R.id.drawer_list);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new DrawerListAdapter(ITEM_NAMES, ITEM_ICONS, NAME, EMAIL, PROFILE, NaviDrawerActivity.this, NaviDrawerActivity.this);
        mRecyclerView.setAdapter(mAdapter);
        // create a GestureDetector object to detect SingleTapUp touch
        // can be later called to verify if the touch event is a SingleTapUp type of touch or some other type of touch (swipe touch, long touch)
        final GestureDetector mGestureDetector = new GestureDetector(NaviDrawerActivity.this, new GestureDetector.SimpleOnGestureListener() {

            @Override public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

        });


        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                View child = recyclerView.findChildViewUnder(motionEvent.getX(),motionEvent.getY());



                if(child!=null && mGestureDetector.onTouchEvent(motionEvent)){
                    mDrawer.closeDrawers();
                    Toast.makeText(NaviDrawerActivity.this, "The Item Clicked is: " + recyclerView.getChildPosition(child), Toast.LENGTH_SHORT).show();
                    int position = recyclerView.getChildPosition(child);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    if(position==1){
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment.newInstance()).commit();
                    }else if(position==2){
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, GamesFragment.newInstance()).commit();
                    }else if(position==3){
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, SearchFragment.newInstance()).commit();
                    }else if(position==4){
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, SettingsFragment.newInstance()).commit();
                    }
                    return true;

                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

            }
        });

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        mDrawer.setStatusBarBackground(R.color.ColorPrimaryDark);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_navi_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // set mTitle and change title in toolbar when fragment attach
    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.home_page_text);
                break;
            case 2:
                mTitle = getString(R.string.games_list_text);
                break;
            case 3:
                mTitle = getString(R.string.search_page_text);
                break;
            case 4:
                mTitle = getString(R.string.settings_page_text);
                break;
        }
        toolbar.setTitle(mTitle);
    }

}
