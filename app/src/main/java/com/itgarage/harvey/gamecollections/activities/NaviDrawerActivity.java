package com.itgarage.harvey.gamecollections.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.itgarage.harvey.gamecollections.R;
import com.itgarage.harvey.gamecollections.adapters.DrawerListAdapter;
import com.itgarage.harvey.gamecollections.db.GamesDataSource;
import com.itgarage.harvey.gamecollections.fragments.GamesFragment;
import com.itgarage.harvey.gamecollections.fragments.HomeFragment;
import com.itgarage.harvey.gamecollections.fragments.SearchFragment;
import com.itgarage.harvey.gamecollections.fragments.SettingsFragment;
import com.itgarage.harvey.gamecollections.models.DrawerListItems;


public class NaviDrawerActivity extends ActionBarActivity {

    private Toolbar toolbar;
    private String NAME = "Harvey Xia";
    private String EMAIL = "harvey1991cn@gmail.com";
    private int PROFILE = R.drawable.ic_user;
    private String[] ITEM_NAMES;
    private int[] ITEM_ICONS;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private FragmentManager fragmentManager;
    private CharSequence mTitle;

    private GamesDataSource dataSource;

    public final static String BARCODE_SCAN_RESULT = "Barcode Scan Result";
    public final static String TOOL_BAR_TITLE_SAVED_TAG = "Tool Bar Title Saved";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navi_drawer_overlay_toolbar); //R.layout.activity_navi_drawer is not overlay the toolbar
        // set up toolbar
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        // get title from toolbar
        mTitle = getTitle();
        if(savedInstanceState!=null){
            mTitle = savedInstanceState.getCharSequence(TOOL_BAR_TITLE_SAVED_TAG);
            getSupportActionBar().setTitle(mTitle);
            /*Log.i("toolbar", "toolbar:"+mTitle);
            Log.i("toolbar", "get toolbar title:"+toolbar.getTitle());*/
        }else {
            // set default fragment to home page
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment.newInstance()).commit();
            mTitle = getString(R.string.home_page_text);
            getSupportActionBar().setTitle(mTitle);
            //Log.i("toolbar", "toolbar first in:"+mTitle);
        }

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
                        Log.i("On attach", "mTitle:"+mTitle);
                        toolbar.setTitle(mTitle);
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment.newInstance()).commit();
                    }else if(position==2){
                        Log.i("On attach", "mTitle:"+mTitle);
                        toolbar.setTitle(mTitle);
                        Bundle bundle = new Bundle();

                        fragmentManager.beginTransaction().replace(R.id.fragment_container, GamesFragment.newInstance()).commit();
                    }else if(position==3){
                        Log.i("On attach", "mTitle:"+mTitle);
                        toolbar.setTitle(mTitle);
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, SearchFragment.newInstance()).commit();
                    }else if(position==4){
                        Log.i("On attach", "mTitle:"+mTitle);
                        toolbar.setTitle(mTitle);
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
                getSupportActionBar().setTitle(mTitle);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getSupportActionBar().setTitle(mTitle);
            }
        };
        mDrawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        onCreateDB();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(TOOL_BAR_TITLE_SAVED_TAG, mTitle);
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
        // Camera button to start barcode scanner and go to result activity
        if(id == R.id.action_camera){
            IntentIntegrator integrator = new IntentIntegrator(NaviDrawerActivity.this);
            integrator.initiateScan();
        }
        // Search button to start search view
        // for db testing
        if (id == R.id.action_search) {
            Intent intent = new Intent(this, DatabaseActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null) {
            String re = scanResult.getContents();
            Log.d("code", re);
            Intent intent = new Intent(this, BarcodeResultActivity.class);
            intent.putExtra(BARCODE_SCAN_RESULT, re);
            startActivity(intent);
        }
        super.onActivityResult(requestCode, resultCode, data);
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
            /*default:
                mTitle = getString(R.string.default_mtitle);*/
        }
    }

    public void onCreateDB() {
        try {
            dataSource = new GamesDataSource(this);
            dataSource.open();
        } catch (Exception e) {
            Log.e("GAMES ERROR", "Error Creating Database");
        } finally {
            Log.i("DB operation", "DB opened.");
        }
    }

    public GamesDataSource getDataSource() {
        return dataSource;
    }

    public Context getContext() {
        return this;
    }

    @Override
    protected void onDestroy() {
        dataSource.close();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        dataSource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        dataSource.close();
        super.onPause();
    }
}
