package com.itgarage.harvey.gamecollections.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.itgarage.harvey.gamecollections.amazon_web_services.CognitoSyncClientManager;
import com.itgarage.harvey.gamecollections.db.GamesDataSource;
import com.itgarage.harvey.gamecollections.fragments.HomeFragment;
import com.itgarage.harvey.gamecollections.models.DrawerListItems;
import com.itgarage.harvey.gamecollections.models.Game;
import com.itgarage.harvey.gamecollections.utils.NetworkStatus;


public class NaviDrawerActivity extends ActionBarActivity{

    private Toolbar toolbar;

    public DrawerListAdapter mAdapter;
    private DrawerLayout mDrawer;
    private CharSequence mTitle;

    private GamesDataSource dataSource;

    public final static String BARCODE_SCAN_RESULT = "Barcode Scan Result";
    public final static String BARCODE_PASS = "pass barcode to game detail";
    public final static String TOOL_BAR_TITLE_SAVED_TAG = "Tool Bar Title Saved";
    public static String CURRENT_FRAGMENT = "";
    public static String CURRENT_TAB = "";

    SharedPreferences sharedPreferences;

    final String TAG = "Navi";

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
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment.newInstance()).commit();
            mTitle = getString(R.string.home_page_text);
            getSupportActionBar().setTitle(mTitle);
            CURRENT_FRAGMENT = "home";
            //Log.i("toolbar", "toolbar first in:"+mTitle);
        }

        // get drawer list items' names and icons from DrawerListItems
        String[] ITEM_NAMES;
        int[] ITEM_ICONS;
        if(NetworkStatus.isNetworkAvailable(this)) {
            ITEM_NAMES = DrawerListItems.ITEM_NAMES_ONLINE;
            ITEM_ICONS = DrawerListItems.ITEM_ICONS_ONLINE;
        }else {
            ITEM_NAMES = DrawerListItems.ITEM_NAMES_OFFLINE;
            ITEM_ICONS = DrawerListItems.ITEM_ICONS_OFFLINE;
        }
        // setup recyclerview of drawer layout
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.drawer_list);
        mRecyclerView.setHasFixedSize(true);
        // get saved shred preferences
        sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
        // get saved username
        String username = sharedPreferences.getString("username", "");
        // get saved profile id to get profile image
        String PROFILE = sharedPreferences.getString("profileId", "");
        // get save status that describes login with facebook or google
        String FB_OR_GOOGLE = sharedPreferences.getString("is fb or google", "");
        // set up the adapter of Drawer list
        mAdapter = new DrawerListAdapter(ITEM_NAMES, ITEM_ICONS, username, PROFILE, FB_OR_GOOGLE, NaviDrawerActivity.this, NaviDrawerActivity.this);
        mRecyclerView.setAdapter(mAdapter);
        // create a GestureDetector object to detect SingleTapUp touch
        // can be later called to verify if the touch event is a SingleTapUp type of touch or some other type of touch (swipe touch, long touch)
        final GestureDetector mGestureDetector = new GestureDetector(NaviDrawerActivity.this, new GestureDetector.SimpleOnGestureListener() {

            @Override public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

        });
        // add touch listener to deal with touch events
        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if (child != null && mGestureDetector.onTouchEvent(motionEvent)) {
                    mDrawer.closeDrawers();
                    int position = recyclerView.getChildPosition(child);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    if (position == 1) {// get home page fragment
                        toolbar.setTitle(mTitle);
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment.newInstance()).commit();
                        CURRENT_FRAGMENT = "home";
                    } else if (position == 2) {// start online keyword search activity
                        Intent intent = new Intent(NaviDrawerActivity.this, SearchKeywordActivity.class);
                        startActivity(intent);
                        /*toolbar.setTitle(mTitle);
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, SearchFragment.newInstance()).commit();
                        CURRENT_FRAGMENT = "search";*/
                    }/* else if (position == 3) {// get setting fragment
                        toolbar.setTitle(mTitle);
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, SettingsFragment.newInstance()).commit();
                        CURRENT_FRAGMENT = "settings";
                    }*/
                    return true;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

            }
        });
        // set up layout manager of drawer list recycler view
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        // get drawer layout instance
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        // set up toggle
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close) {
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
        // open database
        onCreateDB();
        // initialize Amazon client before use.
        CognitoSyncClientManager.init(this);

        setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);
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

        // Camera button to start barcode scanner and go to result activity
        if(id == R.id.action_camera){
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.PRODUCT_CODE_TYPES);
            integrator.initiateScan();
        }
        // Search button to start search view
        /*if (id == R.id.action_search) {
            //onSearchRequested();
        }*/
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            // Barcode scan result
            IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (scanResult != null) {
                String resultStr = scanResult.getContents();
                Log.d("code", resultStr);
                Game game = dataSource.getGameByUPC(resultStr);
                if (game == null) {// not found in local DB, search on Amazon
                    if(NetworkStatus.isNetworkAvailable(this)) {
                        Intent intent = new Intent(this, BarcodeResultActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString(BARCODE_SCAN_RESULT, resultStr);
                        bundle.putString("operation", "Barcode Scan");
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }else {
                        Toast.makeText(this, "Can not find the game in local database. You do not connect to Internet so " +
                                "you can not search it online.", Toast.LENGTH_SHORT).show();
                    }
                } else {// found in local DB, show game detail
                    Intent intent = new Intent(this, GameDetailActivity.class);
                    intent.putExtra(BARCODE_PASS, resultStr);
                    startActivity(intent);
                }
            }
        } else {
            // gracefully handle failure
            Log.w("Debug", "Warning: activity result not ok");
        }
    }

    /**
     * Set mTitle and change title in toolbar when fragment attach.
     * @param number Position that touched.
     */
    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.home_page_text);
                break;
            case 4:
                mTitle = getString(R.string.settings_page_text);
                break;
        }
    }

    /**
     * Open DB.
     */
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
        //Log.i(TAG, "onDestroy");
        super.onDestroy();
    }



    @Override
    protected void onResume() {
        Log.i(TAG, "onResume");
        /*username = sharedPreferences.getString("username", "");
        PROFILE = sharedPreferences.getString("profileId", "");
        mAdapter.update(username, PROFILE);*/
        //updateDateSet();
        super.onResume();
    }

    @Override
    protected void onPause() {
        //Log.i(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void finish() {
        super.finish();
        SharedPreferences preferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(LoginActivity.LOGIN_ACTIVITY_KEY, false);
        editor.apply();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");

    }
}
