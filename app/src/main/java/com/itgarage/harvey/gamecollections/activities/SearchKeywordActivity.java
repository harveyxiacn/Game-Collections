package com.itgarage.harvey.gamecollections.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    EditText keywordInput;
    ImageButton btnSearch;
    private Toolbar toolbar;
    private GamesDataSource dataSource;
    RecyclerView gamesCardListView;
    OnlineResultGameListAdapter gamesAdapter;
    RecyclerView.LayoutManager gamesCardListLayoutManager;
    List<Game> gamesList;
    TextView noResultTextView;
    Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_keyword);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Search by keyword");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        noResultTextView = (TextView) findViewById(R.id.noResultSearchTextView);
        noResultTextView.setVisibility(View.GONE);

        keywordInput = (EditText) findViewById(R.id.keywordInput);
        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE))
                .showSoftInput(keywordInput, InputMethodManager.SHOW_FORCED);
        //keywordInput.requestFocus();
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        btnSearch = (ImageButton) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("SearchKeyword", "onSearchKeyword");
                ItemSearchArgs.KEYWORDS = keywordInput.getText().toString();

                new SearchAmazonTask().execute();
                hideKeyboard();
            }
        });
        gamesList = new ArrayList<>();
        /*gamesCardListView = (RecyclerView) findViewById(R.id.gameResultCardList);
        gamesCardListLayoutManager = new LinearLayoutManager(this);
        gamesCardListView.setLayoutManager(gamesCardListLayoutManager);
        gamesList = null;
        gamesAdapter = new OnlineResultGameListAdapter(gamesList, this);
        gamesCardListView.setAdapter(gamesAdapter);*/
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
                for(int position=0; position<nodeList.getLength(); position++) {
                    Game game = parser.getSearchObject(nodeList, position);
                    Log.i("add Game", "" + game.getTitle());
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
            //gamesAdapter = new OnlineResultGameListAdapter(gamesList, passActivity);
            //gamesCardListView.setAdapter(gamesAdapter);
            //Log.i("RecyclerView", "setting adapter");
            //if (gamesAdapter.getItemCount() == 0) {
            if (gamesList!=null){
                gamesCardListView = new RecyclerView(SearchKeywordActivity.this);
                gamesCardListLayoutManager = new LinearLayoutManager(SearchKeywordActivity.this);
                gamesCardListView.setLayoutManager(gamesCardListLayoutManager);
                gamesAdapter = new OnlineResultGameListAdapter(gamesList, SearchKeywordActivity.this);
                gamesCardListView.setAdapter(gamesAdapter);
                Log.i("RecyclerView", "setting adapter");
                gamesCardListView.setVisibility(View.VISIBLE);
                LinearLayout container = (LinearLayout) findViewById(R.id.rvContainer);
                container.addView(gamesCardListView);
                noResultTextView.setVisibility(View.GONE);
            } else {
                //gamesCardListView.setVisibility(View.GONE);
                noResultTextView.setVisibility(View.VISIBLE);
            }

            if (pd != null) {
                pd.dismiss();
            }
        }
    }
}
