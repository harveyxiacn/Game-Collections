package com.itgarage.harvey.gamecollections.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.itgarage.harvey.gamecollections.amazon_web_services.CognitoSyncGames;
import com.itgarage.harvey.gamecollections.db.GamesDataSource;
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

    public Game downloadGame;
    public Context context = this;

    List<Game> gamesList;
    TextView noResultTextView;
    Activity passActivity;
    public static boolean LOCAL_GAME = false;
    GamesDataSource dataSource;
    String title;

    private static final int CONTACT_PICKER_RESULT = 1;
    final String PHONE_TEXTVIEW_TAG = "delete contact";
    int contactId = -1;
    LinearLayout emailLinearLayout, phoneLinearLayout;
    static final String DEBUG_TAG = "DEBUG_TAG";

    String operation;

    boolean isScanContinuous = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // pass activity?
        passActivity = this;
        setContentView(R.layout.activity_barcode_result);
        // tool bar set up
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // initialize games list
        gamesList = new ArrayList<Game>();
        // get instances
        resultTextView = (TextView) findViewById(R.id.barcodeScanResultTextView);
        noResultTextView = (TextView) findViewById(R.id.noResultSearchTextView);
        // get intent
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        operation = extras.getString("operation");

        isScanContinuous = Boolean.parseBoolean(extras.getString(NaviDrawerActivity.SCAN_CONTINUOUS));

        if(operation.equals("Keyword Search")){// if start this activity from keyword search
            operationKeywordSearch(extras);
            getSupportActionBar().setTitle(getString(R.string.result_game_detail_title));
        }else if(operation.equals("Barcode Scan")){// if start this activity from barcode scan
            operationBarcodeScan(extras);
            getSupportActionBar().setTitle(getString(R.string.barcode_scan_result_activity_title));
        }
    }

    /**
     * Operations of keyword search result game details.
     * @param extras Extras include all information of the game need to display details.
     */
    public void operationKeywordSearch(Bundle extras){
        // set up the game with extras
        downloadGame = new Game();
        downloadGame.setUpcCode(extras.getString("upc"));
        downloadGame.setTitle(extras.getString("title"));
        downloadGame.setPlatform(extras.getString("platform"));
        downloadGame.setHardwarePlatform(extras.getString("hardPlatform"));
        downloadGame.setGenre(extras.getString("genre"));
        downloadGame.setMediumImage(extras.getString("mediumImage"));
        downloadGame.setEdition(extras.getString("edition"));
        downloadGame.setManufacturer(extras.getString("manufacturer"));
        downloadGame.setPublicationDate(extras.getString("publicationDate"));
        downloadGame.setReleaseDate(extras.getString("releaseDate"));
        downloadGame.setRating(extras.getInt("rating"));
        downloadGame.setSmallImage(extras.getString("smallImage"));
        downloadGame.setLargeImage(extras.getString("largeImage"));
        // create the display UIs
        createDisplayUIs();
        // remove no result text view
        noResultTextView.setVisibility(View.GONE);
        // remove the barcode scan result text view
        resultTextView.setVisibility(View.GONE);
    }

    /**
     * Operations of barcode search result, display result game.
     * @param extras Extras include all information of the game need to display details.
     */
    public void operationBarcodeScan(Bundle extras){
        // get barcode
        resultStr = extras.getString(NaviDrawerActivity.BARCODE_SCAN_RESULT);
        // set barcode textView
        resultTextView.setText(resultStr);
        // set global variable
        UPC_CODE = resultStr;
        // set item id to search through Amazon Product Advertising API
        ItemLookupArgs.ITEM_ID = resultStr;
        // get instances card view
        gamesCardListView = (CardView) findViewById(R.id.card_view);
        // remove the no result text view
        noResultTextView.setVisibility(View.GONE);
        /*start to search on Amazon Product Advertising API*/
        new SearchAmazonTask().execute();
    }

    /**
     * Create display UIs to display the game details.
     */
    public void createDisplayUIs(){
        titleTextView = (TextView)findViewById(R.id.textViewGameTitle);
        title = downloadGame.getTitle();
        titleTextView.setText(title);
        String platform = downloadGame.getPlatform();
        if (!platform.equals("")) {
            platformTextview = (TextView) findViewById(R.id.textViewGamePlatform);
            platformTextview.setText(downloadGame.getPlatform());
            platformTextview.setText(platform);
        }

        String mediumImage = downloadGame.getMediumImage();
        gameImage = (SpearImageView) findViewById(R.id.imageViewGameImage);
        gameImage.setImageFromUri(mediumImage);

        gameAttributesLayout = (LinearLayout) findViewById(R.id.gameAttributesLayout);

        String genre = downloadGame.getGenre();
        if (!genre.equals("")) {
            genreTextView = new TextView(this);
            genreTextView.setTextSize(20);
            gameAttributesLayout.addView(genreTextView);
            genreTextView.setText("Genre: " + genre);
            genreTextView.setVisibility(View.VISIBLE);
        }

        String hardwarePlatform = downloadGame.getHardwarePlatform();
        if (!hardwarePlatform.equals("")) {
            hardwarePlatformTextView = new TextView(this);
            hardwarePlatformTextView.setTextSize(20);
            gameAttributesLayout.addView(hardwarePlatformTextView);
            hardwarePlatformTextView.setText("Hardware Platform: " + hardwarePlatform);
            hardwarePlatformTextView.setVisibility(View.VISIBLE);
        }

        String edition = downloadGame.getEdition();
        if (!edition.equals("")) {
            editionTextView = new TextView(this);
            editionTextView.setTextSize(20);
            gameAttributesLayout.addView(editionTextView);
            editionTextView.setText("Edition: " + edition);
            editionTextView.setVisibility(View.VISIBLE);
        }

        String manufacturer = downloadGame.getManufacturer();
        if (!manufacturer.equals("")) {
            manufacturerTextView = new TextView(this);
            manufacturerTextView.setTextSize(20);
            gameAttributesLayout.addView(manufacturerTextView);
            manufacturerTextView.setText("Manufacturer: " + manufacturer);
            manufacturerTextView.setVisibility(View.VISIBLE);
        }

        String publicationDate = downloadGame.getPublicationDate();
        if (!publicationDate.equals("")) {
            publicationDateTextView = new TextView(this);
            publicationDateTextView.setTextSize(20);
            gameAttributesLayout.addView(publicationDateTextView);
            publicationDateTextView.setText("Publication Date: " + publicationDate);
            publicationDateTextView.setVisibility(View.VISIBLE);
        }

        String releaseDate = downloadGame.getReleaseDate();
        if (!releaseDate.equals("")) {
            releaseDateTextView = new TextView(this);
            releaseDateTextView.setTextSize(20);
            gameAttributesLayout.addView(releaseDateTextView);
            releaseDateTextView.setText("Release Date: " + releaseDate);
            releaseDateTextView.setVisibility(View.VISIBLE);
        }
        gameRatingLayout = (LinearLayout) findViewById(R.id.gameRatingLayout);

        gameRating = (RatingBar) findViewById(R.id.gameRatingBar);
        ratingTextView = (TextView) findViewById(R.id.gameRatingText);
        gameRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingTextView.setText("Rating: "+String.valueOf((int)ratingBar.getRating()));
            }
        });

        borrowerInfoLayout = (LinearLayout) findViewById(R.id.gameBorrowerInfoLayout);
        emailLinearLayout = (LinearLayout) findViewById(R.id.emailLinearLayout);
        phoneLinearLayout = (LinearLayout) findViewById(R.id.phoneLinearLayout);

        createGameDetailFloatingActionButtons();
    }

    /**
     * Create the floating action button to deal with operations of adding to DB, adding contact
     */
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
        // add the game to DB
        itemAddToDBIcon = new ImageView(this);
        itemAddToDBIcon.setImageResource(R.drawable.ic_add_to_db);
        addToDBButton = itemBuilder.setContentView(itemAddToDBIcon).build();
        addToDBButton.setOnClickListener(this);
        addToDBButton.setTag(TAG_ADD_TO_DB);
        // add/remove contact
        itemAddContactIcon = new ImageView(this);
        itemAddContactIcon.setImageResource(R.drawable.ic_add_borrower);
        addContactButton = itemBuilder.setContentView(itemAddContactIcon).build();
        addContactButton.setOnClickListener(this);
        addContactButton.setTag(TAG_ADD_BORROWER);

        // Create the menu with the items:
        addGameActionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(addToDBButton)
                .addSubActionView(addContactButton)
                .attachTo(addGameActionButton)
                .build();
    }

    /**
     * Get game list from result from Amazon
     */
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
                downloadGame = parser.getSearchObject(nodeList, position);
            } else {
                downloadGame = null;
            }
        }
    }

    @Override
    public void onClick(View v) {
        /* Add game to database*/
        if (v.getTag().equals(TAG_ADD_TO_DB)) {
            Toast.makeText(this, "Add to DB", Toast.LENGTH_SHORT).show();
            if (gameRatingLayout.getVisibility() == View.VISIBLE) {
                RatingBar ratingBar = (RatingBar) findViewById(R.id.gameRatingBar);
                downloadGame.setRating((int) ratingBar.getRating());
            }
            GamesDataSource dataSource = new GamesDataSource(this);
            dataSource.open();
            //Log.i("DB operation", "DB opened.");
            if(BarcodeResultActivity.UPC_CODE!=null){
                downloadGame.setUpcCode(BarcodeResultActivity.UPC_CODE);
            }
            long insertId = dataSource.addGame(downloadGame);
            if(insertId != -1){
                CognitoSyncGames cognitoSyncGames = new CognitoSyncGames(this);
                cognitoSyncGames.addRecord(downloadGame);
                gamesList = dataSource.getAllGames();
                Toast.makeText(this, "Successfully Add to DB", Toast.LENGTH_SHORT).show();
                /*if(NaviDrawerActivity.CURRENT_FRAGMENT.equals("games")) {
                    if (GamesFragment.gamesAdapter != null) {
                        GamesFragment.gamesAdapter.updateList(gamesList);
                        GamesFragment.changeUIsWhenDataSetChange(true);
                    } else {
                        GamesFragment.gamesAdapter = new GameListAdapter(gamesList, GamesFragment.naviDrawerActivity, false);
                        GamesFragment.changeUIsWhenDataSetChange(true);
                    }
                }else if(NaviDrawerActivity.CURRENT_FRAGMENT.equals("home")) {
                    if (HomeFragment.imageSlideAdapter != null) {
                        //Log.i("imageSlideAdapter", "not null, update list");
                        HomeFragment.imageSlideAdapter.updateList(gamesList);
                        HomeFragment.changeUIsWhenDataSetChange(true);
                    } else {
                        //Log.i("imageSlideAdapter", "null, create, update list");
                        HomeFragment.imageSlideAdapter = new ImageSlideAdapter(HomeFragment.activity.getContext(), gamesList, HomeFragment.viewPager);
                        HomeFragment.changeUIsWhenDataSetChange(true);
                    }
                }*/
                finish();
            }
            dataSource.close();
            addGameActionMenu.close(true);
        }
            /* add borrower */
        if (v.getTag().equals(TAG_ADD_BORROWER)) {
            if(borrowerInfoLayout.getVisibility() == View.GONE) {
                Toast.makeText(this, "Add Borrower", Toast.LENGTH_SHORT).show();
                //addGameActionMenu.close(true);
                itemAddContactIcon.setImageResource(R.drawable.ic_remove_contact);
                doLaunchContactPicker(v);
                borrowerInfoLayout.setVisibility(View.VISIBLE);
            }else {
                Toast.makeText(this, "hide Borrower layout", Toast.LENGTH_SHORT).show();
                addGameActionMenu.close(true);
                downloadGame.setContactId(-1);
                TextView nameTextView = (TextView) findViewById(R.id.borrowerNameTextView);
                nameTextView.setText("Name");
                phoneLinearLayout.removeAllViews();
                emailLinearLayout.removeAllViews();
                itemAddContactIcon.setImageResource(R.drawable.ic_add_borrower);
            }
        }
    }

    /**
     * Task to do search through Amazon Product Advertising API.
     */
    private class SearchAmazonTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(BarcodeResultActivity.this);
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
            if (downloadGame!=null){
                /*if(isScanContinuous){

                }*/
                gamesCardListView.setVisibility(View.VISIBLE);
                noResultTextView.setVisibility(View.GONE);
                createDisplayUIs();
            } else {
                gamesCardListView.setVisibility(View.GONE);
                noResultTextView.setVisibility(View.VISIBLE);
                Intent intent = new Intent(BarcodeResultActivity.this, SearchKeywordActivity.class);
                startActivity(intent);
                finish();
            }

            if (pd != null) {
                pd.dismiss();
            }
        }
    }

    /**
     * Launch contact picker for user to pick a contact.
     * @param view
     */
    public void doLaunchContactPicker(View view) {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
    }

    /**
     * Get contact from contact DB by contact id.
     * @param id Contact id.
     */
    public void getContact(String id){
        if(id.equals("")){
            // get contact by the contactId from SQLite database
            id = String.valueOf(contactId);
        }
        // if the id is not empty, get contact by the id back from contact picker intent
        try{

            String name = "";
            // get display name
            String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
            Cursor nameCursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                    null, ContactsContract.Contacts._ID + "=?", new String[] { id },
                    null);
            if(nameCursor.moveToFirst()) {
                name = nameCursor.getString(nameCursor.getColumnIndex(DISPLAY_NAME));
                Log.v(DEBUG_TAG, "Got name: " + name);
            }else {
                Log.w(DEBUG_TAG, "No results");
            }

            TextView nameTextView = (TextView) findViewById(R.id.borrowerNameTextView);
            nameTextView.setText(name);

            String email = "";
            // query for everything email
            Cursor emailCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=?", new String[] { id },
                    null);

            int emailIdx = emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);

            // get the first email
            if (emailCursor.moveToFirst()) {
                email = emailCursor.getString(emailIdx);
                Log.v(DEBUG_TAG, "Got email: " + email);
            } else {
                Log.w(DEBUG_TAG, "No results");
            }
            TextView emailTag = (TextView) findViewById(R.id.borrowerEmailTag);
            if(email.length()==0){
                emailTag.setVisibility(View.GONE);
            }else {
                emailTag.setVisibility(View.VISIBLE);
                TextView emailAddress = new TextView(this);
                emailAddress.setText(email);
                emailLinearLayout = (LinearLayout) findViewById(R.id.emailLinearLayout);
                emailLinearLayout.addView(emailAddress);
            }

            emailCursor.close();

            // get phone numbers
            TextView phoneTag = (TextView) findViewById(R.id.borrowerPhoneTag);
            String phoneNumber = "";
            int phoneType = -1;
            int hasPhoneNumber = Integer.parseInt(nameCursor.getString(nameCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
            if(hasPhoneNumber>0) {
                phoneTag.setVisibility(View.VISIBLE);
                Cursor phoneCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{id}, null);
                phoneLinearLayout = (LinearLayout) findViewById(R.id.phoneLinearLayout);
                int phoneIndex = 0;
                while (phoneCursor.moveToNext()) {
                    phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    Log.v(DEBUG_TAG, "Got phone: " + phoneNumber);
                    TextView phoneNumberTextView = new TextView(this);
                    phoneNumberTextView.setText(phoneNumber);
                    phoneNumberTextView.setTag(PHONE_TEXTVIEW_TAG + String.valueOf(phoneIndex));
                    phoneLinearLayout.addView(phoneNumberTextView);
                }
                phoneCursor.close();
            }else {
                phoneTag.setVisibility(View.GONE);
            }
            nameCursor.close();
        } catch (Exception e) {
            Log.e(DEBUG_TAG, "Failed to get email data", e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CONTACT_PICKER_RESULT:
                    // handle contact results
                    Cursor cursor = null;
                    Uri result = data.getData();
                    Log.v(DEBUG_TAG, "Got a contact result: "
                            + result.toString());
                    // get the contact id from the Uri
                    String id = result.getLastPathSegment();
                    contactId = Integer.parseInt(id);
                    getContact(id);
                    downloadGame.setContactId(contactId);
                    break;
            }
        } else {
            // gracefully handle failure
            Log.w(DEBUG_TAG, "Warning: activity result not ok");
            switch (requestCode) {
                case CONTACT_PICKER_RESULT:
                    borrowerInfoLayout.setVisibility(View.GONE);
                    itemAddContactIcon.setImageResource(R.drawable.ic_add_borrower);
                    break;
            }
        }
    }
}
