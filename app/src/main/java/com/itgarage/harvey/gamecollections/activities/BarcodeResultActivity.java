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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.itgarage.harvey.gamecollections.R;
import com.itgarage.harvey.gamecollections.adapters.GameListAdapter;
import com.itgarage.harvey.gamecollections.adapters.ImageSlideAdapter;
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

    private static final int CONTACT_PICKER_RESULT = 1;
    final String PHONE_TEXTVIEW_TAG = "delete contact";
    int contactId = -1;
    LinearLayout emailLinearLayout, phoneLinearLayout;
    static final String DEBUG_TAG = "DEBUG_TAG";

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
                if(NaviDrawerActivity.CURRENT_FRAGMENT.equals("games")) {
                    if (GamesFragment.gamesAdapter != null) {
                        GamesFragment.gamesAdapter.updateList(gamesList);
                        GamesFragment.changeUIsWhenDataSetChange(true);
                    } else {
                        GamesFragment.gamesAdapter = new GameListAdapter(gamesList, GamesFragment.naviDrawerActivity);
                        GamesFragment.changeUIsWhenDataSetChange(true);
                    }
                }else if(NaviDrawerActivity.CURRENT_FRAGMENT.equals("home")) {
                    if (HomeFragment.imageSlideAdapter != null) {
                        Log.i("imageSlideAdapter", "not null, update list");
                        HomeFragment.imageSlideAdapter.updateList(gamesList);
                        HomeFragment.changeUIsWhenDataSetChange(true);
                    } else {
                        Log.i("imageSlideAdapter", "null, create, update list");
                        HomeFragment.imageSlideAdapter = new ImageSlideAdapter(HomeFragment.activity.getContext(), gamesList, HomeFragment.viewPager);
                        HomeFragment.changeUIsWhenDataSetChange(true);
                    }
                }
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
                game.setContactId(-1);
                TextView nameTextView = (TextView) findViewById(R.id.borrowerNameTextView);
                nameTextView.setText("Name");
                phoneLinearLayout.removeAllViews();
                emailLinearLayout.removeAllViews();
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
            //addGameActionMenu.close(true);
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

    public void doLaunchContactPicker(View view) {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
    }

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
            }

            TextView emailAddress = new TextView(this);
            emailAddress.setText(email);


            emailLinearLayout = (LinearLayout) findViewById(R.id.emailLinearLayout);
            emailLinearLayout.addView(emailAddress);

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
                    phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER));
                    phoneType = phoneCursor.getInt(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
                    if(phoneType == -1){
                        phoneType = phoneCursor.getInt(ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE);
                    }
                    Log.v(DEBUG_TAG, "Got phone: " + phoneNumber);
                    Log.i(DEBUG_TAG, "Got phone type "+phoneType);
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
                    game.setContactId(contactId);
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
