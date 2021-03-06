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
import com.itgarage.harvey.gamecollections.utils.NetworkStatus;
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

/**
 * This activity for display the result from search online game.
 *
 * Search UPC if it is passed in. Display the result
 *
 * Display teh result if passed in a game from keyword search result list.
 */
public class OnlineGameResultActivity extends ActionBarActivity implements View.OnClickListener{

    TextView resultTextView;
    String resultStr;
    public static String UPC_CODE = null;
    Toolbar toolbar;
    CardView gamesCardListView;

    SpearImageView gameImage;
    TextView titleTextView, platformTextView;
    ImageView imageViewPlatform;
    TextView genreTextView, hardwarePlatformTextView, manufacturerTextView,
            editionTextView, publicationDateTextView, releaseDateTextView;
    RatingBar gameRating;
    LinearLayout gameAttributesLayout, gameRatingLayout, borrowerInfoLayout;

    SubActionButton addToDBButton, addContactButton;
    ImageView itemAddToDBIcon, itemAddContactIcon;
    SubActionButton.Builder itemBuilder;
    FloatingActionMenu addGameActionMenu;
    FloatingActionButton addGameActionButton;
    private static final String TAG_ADD_TO_DB = "TAG_ADD_TO_DB";
    private static final String TAG_ADD_BORROWER = "TAG_ADD_BORROWER";

    public Game downloadGame;
    public Context context = this;

    List<Game> gamesList;
    TextView noResultTextView;
    Activity passActivity;
    String title;

    private static final int CONTACT_PICKER_RESULT = 1;
    final String PHONE_TEXT_VIEW_TAG = "delete contact";
    int contactId = -1;
    LinearLayout emailLinearLayout, phoneLinearLayout;
    static final String DEBUG_TAG = "DEBUG_TAG";

    String operation;

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
        imageViewPlatform = (ImageView) findViewById(R.id.imageViewGamePlatform);
        if (!platform.equals("")) {
            platformTextView = (TextView) findViewById(R.id.textViewGamePlatform);
            platformTextView.setVisibility(View.GONE);
            /*platformTextView.setText(downloadGame.getPlatform());
            platformTextView.setText(platform);*/
            switch (platform.toLowerCase()){
                case "neogeo pocket":
                    imageViewPlatform.setImageResource(R.drawable.systemlogos_0000_neo_geo_pocket);
                    break;
                case "sega master system":
                    imageViewPlatform.setImageResource(R.drawable.systemlogos_0001_sega_master_system);
                    break;
                case "sega game gear":
                    imageViewPlatform.setImageResource(R.drawable.systemlogos_0002_sega_game_gear);
                    break;
                case "sega genesis":
                    imageViewPlatform.setImageResource(R.drawable.systemlogos_0006_sega_genesis);
                    break;
                case "sega dreamcast":
                    imageViewPlatform.setImageResource(R.drawable.systemlogos_0007_dream_cast);
                    break;
                case "xbox one":
                    imageViewPlatform.setImageResource(R.drawable.systemlogos_0009_xbox_one);
                    break;
                case "xbox 360":
                    imageViewPlatform.setImageResource(R.drawable.systemlogos_0010_xbox_360);
                    break;
                case "xbox":
                    imageViewPlatform.setImageResource(R.drawable.systemlogos_0011_xbox);
                    break;
                case "playstation vita":
                    imageViewPlatform.setImageResource(R.drawable.systemlogos_0012_ps_vita);
                    break;
                case "sony psp":
                    imageViewPlatform.setImageResource(R.drawable.systemlogos_0013_psp);
                    break;
                case "playstation":
                    imageViewPlatform.setImageResource(R.drawable.systemlogos_0017_playstation);
                    break;
                case "playstation 2":
                case "playstation2":
                    imageViewPlatform.setImageResource(R.drawable.systemlogos_0016_playstation2);
                    break;
                case "playstation 3":
                    imageViewPlatform.setImageResource(R.drawable.systemlogos_0015_playstation3);
                    break;
                case "playstation 4":
                    imageViewPlatform.setImageResource(R.drawable.systemlogos_0014_playstation4);
                    break;
                case "game boy":
                    imageViewPlatform.setImageResource(R.drawable.systemlogos_0018_gameboy);
                    break;
                case "game boy advance":
                    imageViewPlatform.setImageResource(R.drawable.systemlogos_0019_gba);
                    break;
                case "nintendo wii u":
                    imageViewPlatform.setImageResource(R.drawable.systemlogos_0020_wiiu);
                    break;
                case "nintendo wii":
                    imageViewPlatform.setImageResource(R.drawable.systemlogos_0021_wii);
                    break;
                case "gamecube":
                    imageViewPlatform.setImageResource(R.drawable.systemlogos_0022_gamecube);
                    break;
                case "nintendo 3ds":
                    imageViewPlatform.setImageResource(R.drawable.systemlogos_0023_3ds);
                    break;
                case "nintendo ds":
                    imageViewPlatform.setImageResource(R.drawable.systemlogos_0024_ds);
                    break;
                case "nintendo super nes":
                    imageViewPlatform.setImageResource(R.drawable.systemlogos_0025_snes);
                    break;
                case "nintendo nes":
                    imageViewPlatform.setImageResource(R.drawable.systemlogos_0026_nes);
                    break;
                case "nintendo 64":
                    imageViewPlatform.setImageResource(R.drawable.systemlogos_0027_n64);
                    break;
                case "game boy color":
                    imageViewPlatform.setImageResource(R.drawable.systemlogos_0023_gbc);
                    break;
                default:
                    imageViewPlatform.setVisibility(View.GONE);
                    platformTextView.setVisibility(View.VISIBLE);
                    platformTextView.setText(platform);
            }
        }else {
            imageViewPlatform.setVisibility(View.GONE);
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
            genreTextView.setTextColor(getResources().getColor(R.color.ColorTextIcon));
        }

        String hardwarePlatform = downloadGame.getHardwarePlatform();
        if (!hardwarePlatform.equals("")) {
            hardwarePlatformTextView = new TextView(this);
            hardwarePlatformTextView.setTextSize(20);
            gameAttributesLayout.addView(hardwarePlatformTextView);
            hardwarePlatformTextView.setText("Hardware Platform: " + hardwarePlatform);
            hardwarePlatformTextView.setVisibility(View.VISIBLE);
            hardwarePlatformTextView.setTextColor(getResources().getColor(R.color.ColorTextIcon));
        }

        String edition = downloadGame.getEdition();
        if (!edition.equals("")) {
            editionTextView = new TextView(this);
            editionTextView.setTextSize(20);
            gameAttributesLayout.addView(editionTextView);
            editionTextView.setText("Edition: " + edition);
            editionTextView.setVisibility(View.VISIBLE);
            editionTextView.setTextColor(getResources().getColor(R.color.ColorTextIcon));
        }

        String manufacturer = downloadGame.getManufacturer();
        if (!manufacturer.equals("")) {
            manufacturerTextView = new TextView(this);
            manufacturerTextView.setTextSize(20);
            gameAttributesLayout.addView(manufacturerTextView);
            manufacturerTextView.setText("Manufacturer: " + manufacturer);
            manufacturerTextView.setVisibility(View.VISIBLE);
            manufacturerTextView.setTextColor(getResources().getColor(R.color.ColorTextIcon));
        }

        String publicationDate = downloadGame.getPublicationDate();
        if (!publicationDate.equals("")) {
            publicationDateTextView = new TextView(this);
            publicationDateTextView.setTextSize(20);
            gameAttributesLayout.addView(publicationDateTextView);
            publicationDateTextView.setText("Publication Date: " + publicationDate);
            publicationDateTextView.setVisibility(View.VISIBLE);
            publicationDateTextView.setTextColor(getResources().getColor(R.color.ColorTextIcon));
        }

        String releaseDate = downloadGame.getReleaseDate();
        if (!releaseDate.equals("")) {
            releaseDateTextView = new TextView(this);
            releaseDateTextView.setTextSize(20);
            gameAttributesLayout.addView(releaseDateTextView);
            releaseDateTextView.setText("Release Date: " + releaseDate);
            releaseDateTextView.setVisibility(View.VISIBLE);
            releaseDateTextView.setTextColor(getResources().getColor(R.color.ColorTextIcon));
        }
        gameRatingLayout = (LinearLayout) findViewById(R.id.gameRatingLayout);

        gameRating = (RatingBar) findViewById(R.id.gameRatingBar);

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
        floatingActionButtonIcon.setImageResource(R.drawable.ic_game);
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
        itemAddContactIcon.setImageResource(R.drawable.ic_add_contact);
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
            RatingBar ratingBar = (RatingBar) findViewById(R.id.gameRatingBar);
            downloadGame.setRating((int) ratingBar.getRating());

            GamesDataSource dataSource = new GamesDataSource(this);
            dataSource.open();
            //Log.i("DB operation", "DB opened.");
            if(OnlineGameResultActivity.UPC_CODE!=null){
                downloadGame.setUpcCode(OnlineGameResultActivity.UPC_CODE);
            }
            long insertId = dataSource.addGame(downloadGame);
            Log.i(TAG_ADD_TO_DB, ""+insertId);
            if(insertId != -1){
                if(NetworkStatus.isNetworkAvailable(this)) {
                    CognitoSyncGames cognitoSyncGames = new CognitoSyncGames(this);
                    cognitoSyncGames.addRecord(downloadGame);
                }
                gamesList = dataSource.getAllGames();
                Toast.makeText(this, "Successfully Add to DB", Toast.LENGTH_SHORT).show();
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
                itemAddContactIcon.setImageResource(R.drawable.ic_add_contact);
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
            pd = new ProgressDialog(OnlineGameResultActivity.this);
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
                Intent intent = new Intent(OnlineGameResultActivity.this, SearchKeywordActivity.class);
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
                //Log.v(DEBUG_TAG, "Got name: " + name);
            }else {
                //Log.w(DEBUG_TAG, "No results");
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
                //Log.v(DEBUG_TAG, "Got email: " + email);
            } else {
                //Log.w(DEBUG_TAG, "No results");
            }
            TextView emailTag = (TextView) findViewById(R.id.borrowerEmailTag);
            if(email.length()==0){
                emailTag.setVisibility(View.GONE);
            }else {
                emailTag.setVisibility(View.VISIBLE);
                TextView emailAddress = new TextView(this);
                emailAddress.setText(email);
                emailAddress.setTextColor(getResources().getColor(R.color.ColorTextIcon));
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
                    //Log.v(DEBUG_TAG, "Got phone: " + phoneNumber);
                    TextView phoneNumberTextView = new TextView(this);
                    phoneNumberTextView.setText(phoneNumber);
                    phoneNumberTextView.setTextColor(getResources().getColor(R.color.ColorTextIcon));
                    phoneNumberTextView.setTag(PHONE_TEXT_VIEW_TAG + String.valueOf(phoneIndex));
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
                    itemAddContactIcon.setImageResource(R.drawable.ic_add_contact);
                    break;
            }
        }
    }
}
