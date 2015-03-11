package com.itgarage.harvey.gamecollections.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itgarage.harvey.gamecollections.R;
import com.itgarage.harvey.gamecollections.activities.NaviDrawerActivity;
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

/**
 * Created by harvey on 2015/2/16.
 */
public class SearchFragment extends Fragment {
    SearchView keywordInput;
    RecyclerView gamesCardListView;
    OnlineResultGameListAdapter gamesAdapter;
    RecyclerView.LayoutManager gamesCardListLayoutManager;
    List<Game> gamesList;
    TextView noResultTextView;
    ImageButton btnScan;
    GamesDataSource dataSource;
    /**
     * Returns a new instance of this fragment for the given section number.
     */
    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    public SearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container,
                false);
        dataSource = new GamesDataSource(getActivity());
        noResultTextView = (TextView) rootView.findViewById(R.id.noResultSearchTextView);
        noResultTextView.setVisibility(View.GONE);

        keywordInput = (SearchView) rootView.findViewById(R.id.keyWordInput);
        ((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                .showSoftInput(keywordInput, InputMethodManager.SHOW_FORCED);
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
        /*btnScan = (ImageButton) rootView.findViewById(R.id.cameraScan);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(getActivity());
                integrator.setDesiredBarcodeFormats(IntentIntegrator.PRODUCT_CODE_TYPES);
                integrator.initiateScan();
            }
        });*/
        gamesList = new ArrayList<>();
        gamesCardListView = new RecyclerView(getActivity());
        gamesCardListLayoutManager = new LinearLayoutManager(getActivity());
        gamesCardListView.setLayoutManager(gamesCardListLayoutManager);
        gamesAdapter = new OnlineResultGameListAdapter(gamesList, getActivity());
        gamesCardListView.setAdapter(gamesAdapter);
        Log.i("RecyclerView", "setting adapter");
        LinearLayout containerView = (LinearLayout) rootView.findViewById(R.id.rvContainer);
        containerView.addView(gamesCardListView);
        return rootView;
    }

    private void doSearch(String s){
        Log.i("SearchKeyword", "onSearchKeyword");
        ItemSearchArgs.KEYWORDS = s;
        new SearchAmazonTask().execute();
        hideKeyboard();
    }

    public void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        try {
            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((NaviDrawerActivity) activity).onSectionAttached(3);
    }

    private class SearchAmazonTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(getActivity());
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

}
