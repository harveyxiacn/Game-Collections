package com.itgarage.harvey.gamecollections.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.itgarage.harvey.gamecollections.R;
import com.itgarage.harvey.gamecollections.adapters.GameListAdapter;
import com.itgarage.harvey.gamecollections.amazon_web_services.CognitoSyncGames;
import com.itgarage.harvey.gamecollections.db.GamesDataSource;
import com.itgarage.harvey.gamecollections.models.Game;
import com.itgarage.harvey.gamecollections.utils.NetworkStatus;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.util.List;

import me.xiaopan.android.spear.SpearImageView;

/**
 * This Activity for display local game detail. Game is passed from Navigation Drawer Activity.
 */

public class GameDetailActivity extends ActionBarActivity implements View.OnClickListener {
    Toolbar toolbar;
    SpearImageView gameImage;
    TextView titleTextView, platformTextview;
    TextView genreTextView, hardwarePlatformTextView, manufacturerTextView,
            editionTextView, publicationDateTextView, releaseDateTextView;
    RatingBar gameRatingSmall;
    CheckBox favouriteCheckBox, wishCheckBox;
    static final String FAVOURITE = "favourite";
    static final String WISH = "wish";
    LinearLayout gameAttributesLayout, borrowerInfoLayout;

    LinearLayout emailLinearLayout, phoneLinearLayout;

    static final String DELETE_GAME = "delete game";
    static final String DELETE_CONTACT = "delete contact";

    final String PHONE_TEXTVIEW_TAG = "delete contact";
    int contactId = -1;
    CardView gameDetailCardView;

    SubActionButton removeContactButton, deleteFromDBButton;
    ImageView itemRemoveContactIcon, itemDeleteFromDBIcon;
    SubActionButton.Builder itemBuilder;
    FloatingActionMenu updateGameActionMenu;
    public GamesDataSource dataSource;
    public Game game;
    public Context context = this;

    private static final int CONTACT_PICKER_RESULT = 1;
    static final String DEBUG_TAG = "DEBUG_TAG";

    CognitoSyncGames cognitoSyncGames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail);
        // set up tool bar
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // get intent
        Intent intent = getIntent();
        // get passed position from gameListAdapter
        String strId = intent.getStringExtra(GameListAdapter.GameListViewHolder.GAME_ID_EXTRA);
        int id;
        if(strId==null) {
            id = -1;
        }else {
            id = Integer.parseInt(strId);
        }

        String intentTitle = intent.getStringExtra("game title");
        // get pass barcode from navigation activity if the barcode exists in DB
        String intentBarcode = intent.getStringExtra(NaviDrawerActivity.BARCODE_PASS);
        // set up dataSource to handle DB
        dataSource = new GamesDataSource(this);
        dataSource.open();
        // get all games from DB
        List<Game> gameList = dataSource.getAllGames();
        // initialize game
        game = null;
        // if start this activity from gameListAdapter
        if (id != -1) {
            game = dataSource.getGame(id);
        }
        // if start this activity from imageSlideAdapter
        if(intent.getStringExtra("adapter")!=null) {
            if (intent.getStringExtra("adapter").equals("slideAdapter")) {
                id = intent.getIntExtra("game id", -1);
                game = dataSource.getGame(id);
            }
        }
        // ???
        if (intentTitle != null) {
            String gameTitle;
            int position = 0;
            Game gameTemp;
            do {
                gameTemp = gameList.get(position);
                gameTitle = gameTemp.getTitle();
                if (gameTitle.equals(intentTitle))
                    break;
                position++;
            } while (position < gameList.size());
            game = gameTemp;
        }
        // if start this activity from naviActivity
        if (intentBarcode != null) {
            game = dataSource.getGameByUPC(intentBarcode);
        }

        dataSource.close();
        // set up UIs in this activity
        String title;
        gameDetailCardView = (CardView) findViewById(R.id.card_view);
        titleTextView = (TextView) findViewById(R.id.textViewGameTitle);
        platformTextview = (TextView) findViewById(R.id.textViewGamePlatform);
        gameImage = (SpearImageView) findViewById(R.id.imageViewGameImage);
        gameAttributesLayout = (LinearLayout) findViewById(R.id.gameAttributesLayout);
        gameRatingSmall = (RatingBar) findViewById(R.id.gameRatingBarSmall);
        favouriteCheckBox = (CheckBox) findViewById(R.id.favouriteCheckBox);
        favouriteCheckBox.setOnClickListener(this);
        favouriteCheckBox.setTag(FAVOURITE);
        wishCheckBox = (CheckBox) findViewById(R.id.wishCheckBox);
        wishCheckBox.setOnClickListener(this);
        wishCheckBox.setTag(WISH);
        if (game != null) {
            // set up the title
            title = game.getTitle();
            getSupportActionBar().setTitle(title);
            titleTextView.setText(title);
            // set up the platform
            String platform = game.getPlatform();
            if (platform != null) {
                platformTextview.setText(game.getPlatform());
                platformTextview.setText(platform);
            }
            // download the image
            String mediumImage = game.getMediumImage();
            gameImage.setImageFromUri(mediumImage);
            // set up the genre
            String genre = game.getGenre();
            if (genre != null) {
                genreTextView = new TextView(this);
                genreTextView.setTextSize(20);
                gameAttributesLayout.addView(genreTextView);
                genreTextView.setText("Genre: " + genre);
                genreTextView.setVisibility(View.VISIBLE);
            }
            // set up the hardware platform
            String hardwarePlatform = game.getHardwarePlatform();
            if (hardwarePlatform != null) {
                hardwarePlatformTextView = new TextView(this);
                hardwarePlatformTextView.setTextSize(20);
                gameAttributesLayout.addView(hardwarePlatformTextView);
                hardwarePlatformTextView.setText("Hardware Platform: " + hardwarePlatform);
                hardwarePlatformTextView.setVisibility(View.VISIBLE);
            }
            // set up the edition
            String edition = game.getEdition();
            if (edition != null) {
                Log.i("Edition", edition);
                editionTextView = new TextView(this);
                editionTextView.setTextSize(20);
                gameAttributesLayout.addView(editionTextView);
                editionTextView.setText("Edition: " + edition);
                editionTextView.setVisibility(View.VISIBLE);
            }
            // set up the manufacturer
            String manufacturer = game.getManufacturer();
            if (manufacturer != null) {
                Log.i("Manu", manufacturer);
                manufacturerTextView = new TextView(this);
                manufacturerTextView.setTextSize(20);
                gameAttributesLayout.addView(manufacturerTextView);
                manufacturerTextView.setText("Manufacturer: " + manufacturer);
                manufacturerTextView.setVisibility(View.VISIBLE);
            }
            // set up the publication date
            String publicationDate = game.getPublicationDate();
            if (publicationDate != null) {
                Log.i("Public", publicationDate);
                publicationDateTextView = new TextView(this);
                publicationDateTextView.setTextSize(20);
                gameAttributesLayout.addView(publicationDateTextView);
                publicationDateTextView.setText("Publication Date: " + publicationDate);
                publicationDateTextView.setVisibility(View.VISIBLE);
            }
            // set up the release date
            String releaseDate = game.getReleaseDate();
            if (releaseDate != null) {
                releaseDateTextView = new TextView(this);
                releaseDateTextView.setTextSize(20);
                gameAttributesLayout.addView(releaseDateTextView);
                releaseDateTextView.setText("Release Date: " + releaseDate);
                releaseDateTextView.setVisibility(View.VISIBLE);
            }
            // set up the editable rating bar
            //gameRatingLayout = (LinearLayout) findViewById(R.id.gameRatingLayout);

            int rating = game.getRating();
            /*if (rating != -1) {

                gameRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        ratingTextView.setText("Rating: " + String.valueOf((int) ratingBar.getRating()));
                    }
                });
                gameRating.setRating((float) rating);
                gameRatingSmall.setRating((float) rating);
                gameRatingSmall.setVisibility(View.VISIBLE);
            }*/
            gameRatingSmall.setRating((float) rating);
            gameRatingSmall.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    game.setRating((int) rating);
                    dataSource.open();
                    dataSource.updateGame(game);
                    dataSource.close();
                    if(NetworkStatus.isNetworkAvailable(GameDetailActivity.this))
                        cognitoSyncGames.updateGame(game);
                }
            });
            // set up borrower info layout
            borrowerInfoLayout = (LinearLayout) findViewById(R.id.gameBorrowerInfoLayout);
            contactId = game.getContactId();
            emailLinearLayout = (LinearLayout) findViewById(R.id.emailLinearLayout);
            phoneLinearLayout = (LinearLayout) findViewById(R.id.phoneLinearLayout);
            //contactDetailView = (CardView) findViewById(R.id.borrowerCardView);
            if (contactId != -1) {
                borrowerInfoLayout.setVisibility(View.VISIBLE);
                getContact("");
            } else {
                borrowerInfoLayout.setVisibility(View.GONE);
            }

            // check the checkbox if the game is marked as favourite
            int favourite = game.getFavourite();
            if(favourite == 0){
                favouriteCheckBox.setChecked(false);
            }else {
                favouriteCheckBox.setChecked(true);
            }
            // check the checkbox if the game is marked as wish
            int wish = game.getWish();
            if(wish == 0){
                wishCheckBox.setChecked(false);
            }else {
                wishCheckBox.setChecked(true);
            }
            createGameUpdateFloatingActionButtons();

        } else {
            titleTextView.setText(getString(R.string.null_game_title));
        }
        if(NetworkStatus.isNetworkAvailable(this))
            // initialize the sync instance
            cognitoSyncGames = new CognitoSyncGames(this);
    }

    /**
     * Create floating action button for updating game with rating/contact/delete
     */
    public void createGameUpdateFloatingActionButtons() {
        ImageView floatingActionButtonIcon = new ImageView(this);
        floatingActionButtonIcon.setImageResource(R.drawable.ic_game);
        // Create a button to attach the menu:
        FloatingActionButton updateGameActionButton = new FloatingActionButton.Builder(this)
                .setContentView(floatingActionButtonIcon)
                .setBackgroundDrawable(R.drawable.selector_button_cyan)
                .build();
        // Create menu items:
        itemBuilder = new SubActionButton.Builder(this);
        itemBuilder.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.selector_button_cyan));
        // delete button
        itemDeleteFromDBIcon = new ImageView(this);
        itemDeleteFromDBIcon.setImageResource(R.drawable.ic_delete_game);
        deleteFromDBButton = itemBuilder.setContentView(itemDeleteFromDBIcon).build();
        deleteFromDBButton.setOnClickListener(this);
        deleteFromDBButton.setTag(DELETE_GAME);
        // remove/add contact button
        itemRemoveContactIcon = new ImageView(this);
        if (borrowerInfoLayout.getVisibility() == View.VISIBLE)
            itemRemoveContactIcon.setImageResource(R.drawable.ic_remove_contact);
        else {
            itemRemoveContactIcon.setImageResource(R.drawable.ic_add_contact);
        }
        removeContactButton = itemBuilder.setContentView(itemRemoveContactIcon).build();
        removeContactButton.setOnClickListener(this);
        removeContactButton.setTag(DELETE_CONTACT);
        // Create the menu with the items:
        updateGameActionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(deleteFromDBButton)
                .addSubActionView(removeContactButton)
                //.addSubActionView(updateRaitngButton)
                .attachTo(updateGameActionButton)
                .build();
    }

    @Override
    public void onClick(View v) {
        /*delete the contact/borrower*/
        if (v.getTag().equals(DELETE_CONTACT)) {
            if (borrowerInfoLayout.getVisibility() == View.VISIBLE) {
                //Log.i("contact", "remove");
                borrowerInfoLayout.setVisibility(View.GONE);
                itemRemoveContactIcon.setImageResource(R.drawable.ic_add_contact);
                game.setContactId(-1);
                dataSource.open();
                dataSource.updateGame(game);
                dataSource.close();
                if(NetworkStatus.isNetworkAvailable(this)) {
                    cognitoSyncGames.updateGame(game);
                }
                TextView nameTextView = (TextView) findViewById(R.id.borrowerNameTextView);
                nameTextView.setText("Name");
                phoneLinearLayout.removeAllViews();
                emailLinearLayout.removeAllViews();
                Toast.makeText(this, "Removed contact from this game.", Toast.LENGTH_SHORT).show();
            } else {
                //Log.i("contact", "picker launch");
                doLaunchContactPicker();
                borrowerInfoLayout.setVisibility(View.VISIBLE);
                itemRemoveContactIcon.setImageResource(R.drawable.ic_remove_contact);
            }
            updateGameActionMenu.close(true);
        }
        /*Delete the game*/
        if (v.getTag().equals(DELETE_GAME)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Delete " + titleTextView.getText() + "?")
                    .setTitle("Delete confirmation")
                    .setIcon(R.drawable.ic_delete_confirm)
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            dataSource.open();
                            dataSource.deleteGame(game.getId());
                            if (NetworkStatus.isNetworkAvailable(GameDetailActivity.this)) {
                                cognitoSyncGames.deleteRecord(game.getUpcCode());
                            }
                            dataSource.close();
                            Toast.makeText(context, game.getTitle() + " deleted.", Toast.LENGTH_SHORT).show();
                            ((GameDetailActivity) context).finish();
                        }
                    })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        /*Mark as favourite game*/
        if(v.getTag().equals(FAVOURITE)){
            if(favouriteCheckBox.isChecked()){
                game.setFavourite(1);
            }else{
                game.setFavourite(0);
            }
            dataSource.open();
            dataSource.updateGame(game);
            dataSource.close();
            if(NetworkStatus.isNetworkAvailable(this)) {
                cognitoSyncGames.updateGame(game);
            }
        }
        /*Mark as wish game*/
        if(v.getTag().equals(WISH)){
            if(wishCheckBox.isChecked()){
                game.setWish(1);
            }else{
                game.setWish(0);
            }
            dataSource.open();
            dataSource.updateGame(game);
            dataSource.close();
            if(NetworkStatus.isNetworkAvailable(this)) {
                cognitoSyncGames.updateGame(game);
            }
        }
    }

    /**
     * Launch contact picker when add contact action button is clicked.
     */
    public void doLaunchContactPicker() {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
    }

    /**
     * Get the contact that is picked by user in contact picker
     * or get the contact from contact DB when user picked contact previously.
     * @param id Contact ID from contact picker.
     */
    public void getContact(String id) {
        if (id.equals("")) {
            // get contact by the contactId from SQLite database
            id = String.valueOf(contactId);
        }
        // if the id is not empty, get contact by the id back from contact picker intent
        try {

            String name = "";
            // get display name
            String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
            Cursor nameCursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                    null, ContactsContract.Contacts._ID + "=?", new String[]{id},
                    null);
            if (nameCursor.moveToFirst()) {
                name = nameCursor.getString(nameCursor.getColumnIndex(DISPLAY_NAME));
                Log.v(DEBUG_TAG, "Got name: " + name);
            } else {
                Log.w(DEBUG_TAG, "No results");
            }
            final String sendTo = name;

            TextView nameTextView = (TextView) findViewById(R.id.borrowerNameTextView);
            if (name != null) {
                nameTextView.setText(name);
            }
            String email = "";
            // query for everything email
            Cursor emailCursor = getContentResolver().query(Email.CONTENT_URI,
                    null, Email.CONTACT_ID + "=?", new String[]{id},
                    null);

            int emailIdx = emailCursor.getColumnIndex(Email.DATA);

            // get the first email
            if (emailCursor.moveToFirst()) {
                email = emailCursor.getString(emailIdx);
                Log.v(DEBUG_TAG, "Got email: " + email);
            } else {
                Log.w(DEBUG_TAG, "No results");
            }
            final String sendToEmail = email;
            TextView emailTag = (TextView) findViewById(R.id.borrowerEmailTag);
            if (email.length() == 0) {
                emailTag.setVisibility(View.GONE);
                emailLinearLayout.setVisibility(View.GONE);
            }else {
                emailTag.setVisibility(View.VISIBLE);
                emailLinearLayout.setVisibility(View.VISIBLE);
                LinearLayout emailRowLayout = new LinearLayout(this);
                emailRowLayout.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                emailRowLayout.setLayoutParams(params);
                final TextView emailAddress = new TextView(this);
                emailAddress.setText(email);
                ImageButton sendEmail = new ImageButton(this);
                sendEmail.setImageResource(R.drawable.ic_mail_send);
                sendEmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intentEmail = new Intent(Intent.ACTION_SEND);
                        intentEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{sendToEmail});
                        intentEmail.putExtra(Intent.EXTRA_SUBJECT, "Return " + game.getTitle() + "!");
                        intentEmail.putExtra(Intent.EXTRA_TEXT, "Hi " + sendTo + ",\nI need the " + game.getTitle() + ". Please return it to me.");
                        intentEmail.setType("message/rfc822");
                        startActivity(Intent.createChooser(intentEmail, "Choose an email provider :"));
                    }
                });
                emailRowLayout.addView(emailAddress);
                emailRowLayout.addView(sendEmail);
                emailLinearLayout.addView(emailRowLayout);
            }


            emailCursor.close();

            // get phone numbers
            TextView phoneTag = (TextView) findViewById(R.id.borrowerPhoneTag);
            String phoneNumber = "";
            int phoneType = -1;
            int hasPhoneNumber = Integer.parseInt(nameCursor.getString(nameCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
            if (hasPhoneNumber > 0) {
                phoneTag.setVisibility(View.VISIBLE);
                Cursor phoneCursor = getContentResolver().query(Phone.CONTENT_URI, null,
                        Phone.CONTACT_ID + "=?", new String[]{id}, null);

                int phoneIndex = 0;
                while (phoneCursor.moveToNext()) {
                    phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(Phone.NUMBER));
                    Log.v(DEBUG_TAG, "Got phone: " + phoneNumber);
                    final TextView phoneNumberTextView = new TextView(this);
                    phoneNumberTextView.setText(phoneNumber);
                    phoneNumberTextView.setTag(PHONE_TEXTVIEW_TAG + String.valueOf(phoneIndex));
                    ImageButton sendSms = new ImageButton(this);
                    sendSms.setImageResource(R.drawable.ic_sent_sms);
                    sendSms.setTag(PHONE_TEXTVIEW_TAG + String.valueOf(phoneIndex));
                    sendSms.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(GameDetailActivity.this, "phone number:" + phoneNumberTextView.getText(), Toast.LENGTH_SHORT).show();
                            try {
                                SmsManager smsManager = SmsManager.getDefault();
                                String message = "Hi " + sendTo + ",\nI need the " + game.getTitle() + ". Please return it to me.";
                                smsManager.sendTextMessage(phoneNumberTextView.getText().toString(), null, message, null, null);
                                Toast.makeText(getApplicationContext(), "SMS sent.",
                                        Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(),
                                        "SMS faild, please try again.",
                                        Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                    });
                    LinearLayout phoneRowLayout = new LinearLayout(this);
                    phoneRowLayout.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    phoneRowLayout.setLayoutParams(params);
                    phoneRowLayout.addView(phoneNumberTextView);
                    phoneRowLayout.addView(sendSms);
                    phoneLinearLayout.addView(phoneRowLayout);

                }
                phoneCursor.close();
            } else {
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
                    dataSource.open();
                    dataSource.updateGame(game);
                    dataSource.close();
                    if(NetworkStatus.isNetworkAvailable(this)) {
                        cognitoSyncGames.updateGame(game);
                    }
                    Toast.makeText(this, "Added contact to this game.", Toast.LENGTH_SHORT).show();
                    break;
            }
        } else {
            // gracefully handle failure
            Log.w(DEBUG_TAG, "Warning: activity result not ok");
            switch (requestCode) {
                case CONTACT_PICKER_RESULT:
                    borrowerInfoLayout.setVisibility(View.GONE);
                    itemRemoveContactIcon.setImageResource(R.drawable.ic_add_contact);
                    break;
            }
        }
    }
}
