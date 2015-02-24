package com.itgarage.harvey.gamecollections.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.itgarage.harvey.gamecollections.R;
import com.itgarage.harvey.gamecollections.amazon_product_advertising_api.ItemLookupArgs;
import com.itgarage.harvey.gamecollections.amazon_product_advertising_api.Parser;
import com.itgarage.harvey.gamecollections.amazon_product_advertising_api.SignedRequestsHelper;
import com.itgarage.harvey.gamecollections.amazon_product_advertising_api.UrlParameterHandler;
import com.itgarage.harvey.gamecollections.db.GamesDataSource;
import com.itgarage.harvey.gamecollections.fragments.GamesFragment;
import com.itgarage.harvey.gamecollections.fragments.HomeFragment;
import com.itgarage.harvey.gamecollections.models.Game;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import org.w3c.dom.NodeList;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.xiaopan.android.spear.SpearImageView;


public class BarcodeResultActivity extends ActionBarActivity implements View.OnClickListener{

    TextView resultTextView;
    String resultStr;
    public static String UPC_CODE = null;
    public static final String BARCODE_SCAN_RESULT_SAVED_TAG = "BARCODE_SCAN_RESULT";
    Toolbar toolbar;
    CardView gamesCardListView;
    //RecyclerView gamesCardListView;
    //OnlineResultGameListAdapter gamesAdapter;
    //RecyclerView.LayoutManager gamesCardListLayoutManager;

    SpearImageView gameImage;
    TextView titleTextView, platformTextview;
    TextView genreTextView, hardwarePlatformTextView, manufacturerTextView,
            editionTextView, publicationDateTextView, releaseDateTextView, ratingTextView;
    RatingBar gameRating;
    LinearLayout gameAttributesLayout, gameRatingLayout, borrowerInfoLayout;

    SubActionButton addToDBButton, addContactButton, addRatingButton;
    ImageView itemAddToDBIcon, itemAddContactIcon, itemAddRatingIcon;
    SubActionButton.Builder itemBuilder;
    FloatingActionMenu addGameActionMenu;
    FloatingActionButton addGameActionButton;
    private static final String TAG_ADD_TO_DB = "TAG_ADD_TO_DB";
    private static final String TAG_ADD_BORROWER = "TAG_ADD_BORROWER";
    private static final String TAG_ADD_RATING = "TAG_ADD_RATING";

    public Game game;
    public Context context = this;

    List<Game> gamesList;
    TextView noResultTextView;
    Activity passActivity;
    public static boolean LOCAL_GAME = false;
    GamesDataSource dataSource;
    String title;

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
        UPC_CODE = resultStr;

        ItemLookupArgs.ITEM_ID = resultStr;

        gamesCardListView = (CardView) findViewById(R.id.card_view);

        /*gamesCardListView = (RecyclerView) findViewById(R.id.gameCardList);
        gamesCardListLayoutManager = new LinearLayoutManager(this);
        gamesCardListView.setLayoutManager(gamesCardListLayoutManager);
        Log.i("RecyclerView", "setting layout manager");*/
        noResultTextView = (TextView) findViewById(R.id.noResultSearchTextView);
        noResultTextView.setVisibility(View.GONE);
        /*start to search on Amazon Product Advertising API*/
        new SearchAmazonTask().execute();
    }

    public void createDisplayUIs(){
        titleTextView = (TextView)findViewById(R.id.textViewGameTitle);
        title = game.getTitle();
        titleTextView.setText(title);
        String platform = game.getPlatform();
        if (!platform.equals("")) {
            platformTextview = (TextView) findViewById(R.id.textViewGamePlatform);
            platformTextview.setText(game.getPlatform());
            platformTextview.setText(platform);
        }

        String mediumImage = game.getMediumImage();
        gameImage = (SpearImageView) findViewById(R.id.imageViewGameImage);
        gameImage.setImageFromUri(mediumImage);

        gameAttributesLayout = (LinearLayout) findViewById(R.id.gameAttributesLayout);

        String genre = game.getGenre();
        if (!genre.equals("")) {
            genreTextView = new TextView(this);
            genreTextView.setTextSize(20);
            gameAttributesLayout.addView(genreTextView);
            genreTextView.setText("Genre: " + genre);
            genreTextView.setVisibility(View.VISIBLE);
        }

        String hardwarePlatform = game.getHardwarePlatform();
        if (!hardwarePlatform.equals("")) {
            hardwarePlatformTextView = new TextView(this);
            hardwarePlatformTextView.setTextSize(20);
            gameAttributesLayout.addView(hardwarePlatformTextView);
            hardwarePlatformTextView.setText("Hardware Platform: " + hardwarePlatform);
            hardwarePlatformTextView.setVisibility(View.VISIBLE);
        }

        String edition = game.getEdition();
        if (!edition.equals("")) {
            editionTextView = new TextView(this);
            editionTextView.setTextSize(20);
            gameAttributesLayout.addView(editionTextView);
            editionTextView.setText("Edition: " + edition);
            editionTextView.setVisibility(View.VISIBLE);
        }

        String manufacturer = game.getManufacturer();
        if (!manufacturer.equals("")) {
            manufacturerTextView = new TextView(this);
            manufacturerTextView.setTextSize(20);
            gameAttributesLayout.addView(manufacturerTextView);
            manufacturerTextView.setText("Manufacturer: " + manufacturer);
            manufacturerTextView.setVisibility(View.VISIBLE);
        }

        String publicationDate = game.getPublicationDate();
        if (!publicationDate.equals("")) {
            publicationDateTextView = new TextView(this);
            publicationDateTextView.setTextSize(20);
            gameAttributesLayout.addView(publicationDateTextView);
            publicationDateTextView.setText("Publication Date: " + publicationDate);
            publicationDateTextView.setVisibility(View.VISIBLE);
        }

        String releaseDate = game.getReleaseDate();
        if (!releaseDate.equals("")) {
            releaseDateTextView = new TextView(this);
            releaseDateTextView.setTextSize(20);
            gameAttributesLayout.addView(releaseDateTextView);
            releaseDateTextView.setText("Release Date: " + releaseDate);
            releaseDateTextView.setVisibility(View.VISIBLE);
        }
        gameRatingLayout = (LinearLayout) findViewById(R.id.gameRatingLayout);

        /*int rating = game.getRating();
        if (rating != -1) {
            gameRating = (RatingBar) findViewById(R.id.gameRatingBar);
            ratingTextView = (TextView) findViewById(R.id.gameRatingText);
            gameRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    ratingTextView.setText("Rating: "+String.valueOf((int)ratingBar.getRating()));
                }
            });
            gameRating.setRating((float)rating);
        }*/
        gameRating = (RatingBar) findViewById(R.id.gameRatingBar);
        ratingTextView = (TextView) findViewById(R.id.gameRatingText);
        gameRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingTextView.setText("Rating: "+String.valueOf((int)ratingBar.getRating()));
            }
        });

        borrowerInfoLayout = (LinearLayout) findViewById(R.id.gameBorrowerInfoLayout);

        createGameDetailFloatingActionButtons();
    }

    public void createGameDetailFloatingActionButtons() {
        ImageView floatingActionButtonIcon = new ImageView(this);
        floatingActionButtonIcon.setImageResource(R.drawable.ic_action_game);
        // Create a button to attach the menu:
        addGameActionButton = new FloatingActionButton.Builder(this)
                .setContentView(floatingActionButtonIcon)
                .setBackgroundDrawable(R.drawable.selector_button_cyan)
                .build();
        // Create menu items:
        itemBuilder = new SubActionButton.Builder(this);
        itemBuilder.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_button_cyan));
        // repeat many times:
        itemAddToDBIcon = new ImageView(this);
        itemAddToDBIcon.setImageResource(R.drawable.ic_add_to_db);
        addToDBButton = itemBuilder.setContentView(itemAddToDBIcon).build();
        addToDBButton.setOnClickListener(this);
        addToDBButton.setTag(TAG_ADD_TO_DB);

        itemAddContactIcon = new ImageView(this);
        itemAddContactIcon.setImageResource(R.drawable.ic_add_borrower);
        addContactButton = itemBuilder.setContentView(itemAddContactIcon).build();
        addContactButton.setOnClickListener(this);
        addContactButton.setTag(TAG_ADD_BORROWER);

        /*itemAddRatingIcon = new ImageView(this);
        itemAddRatingIcon.setImageResource(R.drawable.ic_add_rating);
        addRatingButton = itemBuilder.setContentView(itemAddRatingIcon).build();
        addRatingButton.setOnClickListener(this);
        addRatingButton.setTag(TAG_ADD_RATING);*/
        // Create the menu with the items:
        addGameActionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(addToDBButton)
                .addSubActionView(addContactButton)
                //.addSubActionView(addRatingButton)
                .attachTo(addGameActionButton)
                .build();
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

    @Override
    public void onClick(View v) {
        /* Add game to database*/
        if (v.getTag().equals(TAG_ADD_TO_DB)) {
            Toast.makeText(this, "Add to DB", Toast.LENGTH_SHORT).show();
            Game game = gamesList.get(0);
            if (gameRatingLayout.getVisibility() == View.VISIBLE) {
                RatingBar ratingBar = (RatingBar) findViewById(R.id.gameRatingBar);
                game.setRating((int) ratingBar.getRating());
            }
            GamesDataSource dataSource = new GamesDataSource(this);
            dataSource.open();
            Log.i("DB operation", "DB opened.");
            if(BarcodeResultActivity.UPC_CODE!=null){
                game.setUpcCode(BarcodeResultActivity.UPC_CODE);
            }
            long insertId = dataSource.addGame(game);
            if(insertId != -1){
                gamesList = dataSource.getAllGames();
                Toast.makeText(this, "Successfully Add to DB", Toast.LENGTH_SHORT).show();
                if(GamesFragment.gamesAdapter!=null)
                    GamesFragment.gamesAdapter.updateList(gamesList);
                if(HomeFragment.imageSlideAdapter!=null)
                    HomeFragment.imageSlideAdapter.updateList(gamesList);
                finish();
            }
            dataSource.close();
            addGameActionMenu.close(true);
        }
            /* add borrower */
        if (v.getTag().equals(TAG_ADD_BORROWER)) {
            if(borrowerInfoLayout.getVisibility() == View.GONE) {
                Toast.makeText(this, "Add Borrower", Toast.LENGTH_SHORT).show();
                addGameActionMenu.close(true);
                itemAddContactIcon.setImageResource(R.drawable.ic_remove_contact);
            }else {
                Toast.makeText(this, "hide Borrower layout", Toast.LENGTH_SHORT).show();
                addGameActionMenu.close(true);
                itemAddContactIcon.setImageResource(R.drawable.ic_add_borrower);
            }
        }
            /* add rating*/
        if (v.getTag().equals(TAG_ADD_RATING)) {
            if(gameRatingLayout.getVisibility() == View.GONE) {
                Toast.makeText(this, "Add Rating", Toast.LENGTH_SHORT).show();
                gameRatingLayout.setVisibility(View.VISIBLE);
                itemAddRatingIcon.setImageResource(R.drawable.ic_hide_rating_bar);
            }else if(gameRatingLayout.getVisibility() == View.VISIBLE){
                Toast.makeText(this, "Remove Rating", Toast.LENGTH_SHORT).show();
                gameRatingLayout.setVisibility(View.GONE);
                itemAddRatingIcon.setImageResource(R.drawable.ic_add_rating);
            }
            addGameActionMenu.close(true);
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
            //gamesAdapter = new OnlineResultGameListAdapter(gamesList, passActivity);
            //gamesCardListView.setAdapter(gamesAdapter);
            //Log.i("RecyclerView", "setting adapter");
            //if (gamesAdapter.getItemCount() == 0) {
            if (gamesList!=null){
                gamesCardListView.setVisibility(View.VISIBLE);
                noResultTextView.setVisibility(View.GONE);
                game = gamesList.get(0);
                createDisplayUIs();
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
    protected void onDestroy() {
        //dataSource.close();
        Log.i("database operation", "db close");
        super.onDestroy();
    }
}
