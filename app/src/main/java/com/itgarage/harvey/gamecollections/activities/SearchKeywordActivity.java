package com.itgarage.harvey.gamecollections.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.itgarage.harvey.gamecollections.R;
import com.itgarage.harvey.gamecollections.adapters.OnlineResultGameListAdapter;
import com.itgarage.harvey.gamecollections.amazon_product_advertising_api.ItemSearchArgs;
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


public class SearchKeywordActivity extends ActionBarActivity {
    SearchView keywordInput;
    RecyclerView gamesCardListView;
    OnlineResultGameListAdapter gamesAdapter;
    RecyclerView.LayoutManager gamesCardListLayoutManager;
    List<Game> gamesList;
    TextView noResultTextView;
    GamesDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_keyword);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Search by keyword");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dataSource = new GamesDataSource(this);
        noResultTextView = (TextView) findViewById(R.id.noResultSearchTextView);
        noResultTextView.setVisibility(View.GONE);

        keywordInput = (SearchView) findViewById(R.id.keyWordInput);
        keywordInput.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                doSearch(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        /*btnScan = (ImageButton) findViewById(R.id.cameraScan);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(SearchKeywordActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.PRODUCT_CODE_TYPES);
                integrator.initiateScan();
            }
        });*/
        gamesList = new ArrayList<>();
        gamesCardListView = new RecyclerView(SearchKeywordActivity.this);
        gamesCardListLayoutManager = new LinearLayoutManager(SearchKeywordActivity.this);
        gamesCardListView.setLayoutManager(gamesCardListLayoutManager);
        gamesAdapter = new OnlineResultGameListAdapter(gamesList, SearchKeywordActivity.this);
        gamesCardListView.setAdapter(gamesAdapter);
        Log.i("RecyclerView", "setting adapter");
        LinearLayout container = (LinearLayout) findViewById(R.id.rvContainer);
        container.addView(gamesCardListView);
    }

    private void doSearch(String s){
        Log.i("SearchKeyword", "onSearchKeyword");
        ItemSearchArgs.KEYWORDS = s;
        new SearchAmazonTask().execute();
        //hideKeyboard();
    }

    public void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) SearchKeywordActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        try {
            inputManager.hideSoftInputFromWindow(SearchKeywordActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }
    /* Get games list from Amazon product advertising api*/
    public void getGameList() {
        UrlParameterHandler urlParameterHandler = UrlParameterHandler.getInstance();
        Map<String, String> myparams = urlParameterHandler.buildMapForItemSearch();
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
                if(gamesList == null){
                    gamesList = new ArrayList<>();
                }
                gamesList.clear();
                for(int position=0; position<nodeList.getLength(); position++) {
                    Game game = parser.getSearchObject(nodeList, position);
                    //Log.i("add Game", "" + game.getTitle());
                    gamesList.add(game);
                }
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
            pd = new ProgressDialog(SearchKeywordActivity.this);
            pd.setTitle("One Sec...");
            pd.setMessage("Loading...");
            pd.show();
            //Log.i("pre", "pre execute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            //Log.i("exe", "executing");
            getGameList();
            //Log.i("gameList", "" + gamesList);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (gamesList!=null){
                gamesAdapter.update(gamesList);
                gamesCardListView.setVisibility(View.VISIBLE);
                noResultTextView.setVisibility(View.GONE);
            } else {
                gamesCardListView.setVisibility(View.GONE);
                noResultTextView.setVisibility(View.VISIBLE);
            }

            if (pd != null) {
                pd.dismiss();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            // Barcode scan result
            IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (scanResult != null) {
                String resultStr = scanResult.getContents();
                Log.d("code", resultStr);
                dataSource.open();
                Game game = dataSource.getGameByUPC(resultStr);
                dataSource.close();
                if (game == null) {// not found in local DB, search on Amazon
                    Intent intent = new Intent(this, BarcodeResultActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(NaviDrawerActivity.BARCODE_SCAN_RESULT, resultStr);
                    bundle.putString("operation", "Barcode Scan");
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                } else {// found in local DB, show game detail
                    Intent intent = new Intent(this, GameDetailActivity.class);
                    intent.putExtra(NaviDrawerActivity.BARCODE_PASS, resultStr);
                    startActivity(intent);
                    finish();
                }
            }
        } else {
            // gracefully handle failure
            Log.w("Debug", "Warning: activity result not ok");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_navi_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Camera button to start barcode scanner and go to result activity
        if(id == R.id.action_camera){
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.PRODUCT_CODE_TYPES);
            integrator.initiateScan();
        }
        return super.onOptionsItemSelected(item);
    }
}
