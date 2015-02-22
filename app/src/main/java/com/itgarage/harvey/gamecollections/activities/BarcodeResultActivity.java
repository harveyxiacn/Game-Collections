package com.itgarage.harvey.gamecollections.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.itgarage.harvey.gamecollections.R;
import com.itgarage.harvey.gamecollections.adapters.GameListAdapter;
import com.itgarage.harvey.gamecollections.amazon_product_advertising_api.ItemLookupArgs;
import com.itgarage.harvey.gamecollections.amazon_product_advertising_api.Parser;
import com.itgarage.harvey.gamecollections.amazon_product_advertising_api.SignedRequestsHelper;
import com.itgarage.harvey.gamecollections.amazon_product_advertising_api.UrlParameterHandler;
import com.itgarage.harvey.gamecollections.db.GamesDataSource;
import com.itgarage.harvey.gamecollections.models.Game;

import org.w3c.dom.NodeList;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class BarcodeResultActivity extends ActionBarActivity{

    TextView resultTextView;
    String resultStr;
    public static final String BARCODE_SCAN_RESULT_SAVED_TAG = "BARCODE_SCAN_RESULT";
    Toolbar toolbar;
    RecyclerView gamesCardListView;
    RecyclerView.Adapter gamesAdapter;
    RecyclerView.LayoutManager gamesCardListLayoutManager;

    List<Game> gamesList;
    TextView noResultTextView;
    Activity passActivity;

    /*private static final String TAG_ADD_TO_DB = "TAG_ADD_TO_DB";
    private static final String TAG_ADD_BORROWER = "TAG_ADD_BORROWER";
    private static final String TAG_ADD_RATING = "TAG_ADD_RATING";*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        passActivity = this;
        setContentView(R.layout.activity_barcode_result);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.barcode_scan_result_activity_title));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        gamesList = new ArrayList<Game>();

        resultTextView = (TextView) findViewById(R.id.barcodeScanResultTextView);
        Intent intent = getIntent();
        resultStr = intent.getStringExtra(NaviDrawerActivity.BARCODE_SCAN_RESULT);
        resultTextView.setText(resultStr);
        if (savedInstanceState != null) {
            String barcodeScanResult = savedInstanceState.getString(BARCODE_SCAN_RESULT_SAVED_TAG);
            resultTextView.setText(barcodeScanResult);
        }

        ItemLookupArgs.ITEM_ID = resultStr;

        gamesCardListView = (RecyclerView) findViewById(R.id.gameCardList);
        gamesCardListView.setHasFixedSize(true);
        gamesCardListLayoutManager = new LinearLayoutManager(this);
        gamesCardListView.setLayoutManager(gamesCardListLayoutManager);
        Log.i("RecyclerView", "setting layout manager");
        noResultTextView = (TextView) findViewById(R.id.noResultSearchTextView);
        noResultTextView.setVisibility(View.GONE);

        /*ImageView floatingActionButtonIcon = new ImageView(this);
        floatingActionButtonIcon.setImageResource(R.drawable.ic_action_game);
        // Create a button to attach the menu:
        FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                .setContentView(floatingActionButtonIcon)
                .setBackgroundDrawable(R.drawable.selector_button_cyan)
                .build();
        // Create menu items:
        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
        itemBuilder.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_button_cyan));
        // repeat many times:
        ImageView itemAddToDBIcon = new ImageView(this);
        itemAddToDBIcon.setImageResource(R.drawable.ic_add_to_db);
        SubActionButton addToDBButton = itemBuilder.setContentView(itemAddToDBIcon).build();
        addToDBButton.setOnClickListener(this);
        addToDBButton.setTag(TAG_ADD_TO_DB);

        ImageView itemAddContactIcon = new ImageView(this);
        itemAddContactIcon.setImageResource(R.drawable.ic_add_borrower);
        SubActionButton addContactButton = itemBuilder.setContentView(itemAddContactIcon).build();
        addContactButton.setOnClickListener(this);
        addContactButton.setTag(TAG_ADD_BORROWER);

        ImageView itemAddRatingIcon = new ImageView(this);
        itemAddRatingIcon.setImageResource(R.drawable.ic_add_rating);
        SubActionButton addRatingButton = itemBuilder.setContentView(itemAddRatingIcon).build();
        addRatingButton.setOnClickListener(this);
        addRatingButton.setTag(TAG_ADD_RATING);
        // Create the menu with the items:
        FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(addToDBButton)
                .addSubActionView(addContactButton)
                .addSubActionView(addRatingButton)
                .attachTo(actionButton)
                .build();*/
        GamesDataSource dataSource = new GamesDataSource(this);
        dataSource.open();
        Log.i("DB operation", "DB opened.");
        Game game = null;
        game = dataSource.getGameByUPC(resultStr);
        new SearchAmazonTask().execute();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(BARCODE_SCAN_RESULT_SAVED_TAG, resultStr);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_barcode_result, menu);
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

    public void getGameList() {
        UrlParameterHandler urlParameterHandler = UrlParameterHandler.getInstance();
        Map<String, String> myparams = urlParameterHandler.buildMapForItemLookUp();
        SignedRequestsHelper signedRequestsHelper = null;
        try {
            signedRequestsHelper = new SignedRequestsHelper();
        } catch (UnsupportedEncodingException | InvalidKeyException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (signedRequestsHelper != null) {
            String signedUrl = signedRequestsHelper.sign(myparams);
            Parser parser = new Parser();
            NodeList nodeList = parser.getResponseNodeList(signedUrl);
            if (nodeList != null) {
                int position = 0;
                Game game = parser.getSearchObject(nodeList, position);
                Log.i("add Game", "" + game.getTitle());
                gamesList.add(game);
            } else {
                gamesList = null;
            }
        }
    }

    private class SearchAmazonTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(BarcodeResultActivity.this);
            pd.setTitle("One Sec...");
            pd.setMessage("Loading...");
            pd.show();
            Log.i("pre", "pre execute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i("exe", "executing");
            getGameList();
            Log.i("gameList", "" + gamesList);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            gamesAdapter = new GameListAdapter(gamesList, passActivity);
            gamesCardListView.setAdapter(gamesAdapter);
            Log.i("RecyclerView", "setting adapter");
            if (gamesAdapter.getItemCount() == 0) {
                gamesCardListView.setVisibility(View.GONE);
                noResultTextView.setVisibility(View.VISIBLE);
            } else {
                gamesCardListView.setVisibility(View.VISIBLE);
                noResultTextView.setVisibility(View.GONE);
            }

            if (pd != null) {
                pd.dismiss();
            }
        }
    }
}
