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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.itgarage.harvey.gamecollections.R;
import com.itgarage.harvey.gamecollections.adapters.GameListAdapter;
import com.itgarage.harvey.gamecollections.adapters.ImageSlideAdapter;
import com.itgarage.harvey.gamecollections.amazon_web_services.CognitoSyncGames;
import com.itgarage.harvey.gamecollections.db.GamesDataSource;
import com.itgarage.harvey.gamecollections.fragments.GamesFragment;
import com.itgarage.harvey.gamecollections.fragments.HomeFragment;
import com.itgarage.harvey.gamecollections.models.Game;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.util.List;

import me.xiaopan.android.spear.SpearImageView;

;

public class GameDetailActivity extends ActionBarActivity implements View.OnClickListener {
    Toolbar toolbar;
    SpearImageView gameImage;
    TextView titleTextView, platformTextview;
    TextView genreTextView, hardwarePlatformTextView, manufacturerTextView,
            editionTextView, publicationDateTextView, releaseDateTextView, ratingTextView;
    RatingBar gameRating, gameRatingSmall;
    LinearLayout gameAttributesLayout, gameRatingLayout, borrowerInfoLayout;

    LinearLayout emailLinearLayout, phoneLinearLayout;

    static final String UPDATE_GAME = "update game";
    static final String DELETE_GAME = "delete game";
    static final String UPDATE_RATING = "update rating";
    static final String DELETE_RATING = "delete rating";
    static final String UPDATE_CONTACT = "update contact";
    static final String DELETE_CONTACT = "delete contact";

    final String PHONE_TEXTVIEW_TAG = "delete contact";
    int contactId = -1;
    CardView contactDetailView, gameDetailCardView;

    SubActionButton updateToDBButton, updateContactButton, removeContactButton, updateRaitngButton, deleteFromDBButton;
    ImageView itemUpdateRatingIcon, itemRemoveContactIcon, itemUpdateContactIcon, itemUpdateToDBIcon, itemDeleteFromDBIcon;
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
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setHomeButtonEnabled(true);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        int postion = intent.getIntExtra("game position", -1);
        String intentTitle = intent.getStringExtra("game title");
        String intentBarcode = intent.getStringExtra(NaviDrawerActivity.BARCODE_PASS);
        dataSource = new GamesDataSource(this);
        dataSource.open();
        List<Game> gameList = dataSource.getAllGames();
        game = null;
        if (postion != -1) {
            game = gameList.get(postion);
        }
        if (intentTitle != null) {
            String gameTitle = "";
            int position = 0;
            Game gameTemp = null;
            do {
                gameTemp = gameList.get(position);
                gameTitle = gameTemp.getTitle();
                if (gameTitle.equals(intentTitle))
                    break;
                position++;
            } while (position < gameList.size());
            game = gameTemp;
        }
        if (intentBarcode != null) {
            game = dataSource.getGameByUPC(intentBarcode);
        }
        dataSource.close();
        String title;
        gameDetailCardView = (CardView) findViewById(R.id.card_view);
        titleTextView = (TextView) findViewById(R.id.textViewGameTitle);
        platformTextview = (TextView) findViewById(R.id.textViewGamePlatform);
        gameImage = (SpearImageView) findViewById(R.id.imageViewGameImage);
        gameAttributesLayout = (LinearLayout) findViewById(R.id.gameAttributesLayout);

        gameRating = (RatingBar) findViewById(R.id.gameRatingBar);
        ratingTextView = (TextView) findViewById(R.id.gameRatingText);
        gameRatingSmall = (RatingBar) findViewById(R.id.gameRatingBarSmall);
        if (game != null) {
            title = game.getTitle();
            getSupportActionBar().setTitle(title);

            titleTextView.setText(title);
            String platform = game.getPlatform();
            if (platform != null) {

                platformTextview.setText(game.getPlatform());
                platformTextview.setText(platform);
            }

            String mediumImage = game.getMediumImage();

            gameImage.setImageFromUri(mediumImage);


            String genre = game.getGenre();
            if (genre != null) {
                genreTextView = new TextView(this);
                genreTextView.setTextSize(20);
                gameAttributesLayout.addView(genreTextView);
                genreTextView.setText("Genre: " + genre);
                genreTextView.setVisibility(View.VISIBLE);
            }

            String hardwarePlatform = game.getHardwarePlatform();
            if (hardwarePlatform != null) {
                hardwarePlatformTextView = new TextView(this);
                hardwarePlatformTextView.setTextSize(20);
                gameAttributesLayout.addView(hardwarePlatformTextView);
                hardwarePlatformTextView.setText("Hardware Platform: " + hardwarePlatform);
                hardwarePlatformTextView.setVisibility(View.VISIBLE);
            }

            String edition = game.getEdition();
            if (edition != null) {
                editionTextView = new TextView(this);
                editionTextView.setTextSize(20);
                gameAttributesLayout.addView(editionTextView);
                editionTextView.setText("Edition: " + edition);
                editionTextView.setVisibility(View.VISIBLE);
            }

            String manufacturer = game.getManufacturer();
            if (manufacturer != null) {
                manufacturerTextView = new TextView(this);
                manufacturerTextView.setTextSize(20);
                gameAttributesLayout.addView(manufacturerTextView);
                manufacturerTextView.setText("Manufacturer: " + manufacturer);
                manufacturerTextView.setVisibility(View.VISIBLE);
            }

            String publicationDate = game.getPublicationDate();
            if (publicationDate != null) {
                publicationDateTextView = new TextView(this);
                publicationDateTextView.setTextSize(20);
                gameAttributesLayout.addView(publicationDateTextView);
                publicationDateTextView.setText("Publication Date: " + publicationDate);
                publicationDateTextView.setVisibility(View.VISIBLE);
            }

            String releaseDate = game.getReleaseDate();
            if (releaseDate != null) {
                releaseDateTextView = new TextView(this);
                releaseDateTextView.setTextSize(20);
                gameAttributesLayout.addView(releaseDateTextView);
                releaseDateTextView.setText("Release Date: " + releaseDate);
                releaseDateTextView.setVisibility(View.VISIBLE);
            }
            gameRatingLayout = (LinearLayout) findViewById(R.id.gameRatingLayout);

            int rating = game.getRating();
            if (rating != -1) {

                gameRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        ratingTextView.setText("Rating: " + String.valueOf((int) ratingBar.getRating()));
                    }
                });
                gameRating.setRating((float) rating);
                gameRatingSmall.setRating((float) rating);
                gameRatingSmall.setVisibility(View.VISIBLE);
            }

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

            createGameUpdateFloatingActionButtons();

        } else {
            titleTextView.setText(getString(R.string.null_game_title));
        }
        // sync stuff
        cognitoSyncGames = new CognitoSyncGames(this);
    }

    public void createGameUpdateFloatingActionButtons() {
        ImageView floatingActionButtonIcon = new ImageView(this);
        floatingActionButtonIcon.setImageResource(R.drawable.ic_action_game);
        // Create a button to attach the menu:
        FloatingActionButton updateGameActionButton = new FloatingActionButton.Builder(this)
                .setContentView(floatingActionButtonIcon)
                .setBackgroundDrawable(R.drawable.selector_button_cyan)
                .build();
        // Create menu items:
        itemBuilder = new SubActionButton.Builder(this);
        itemBuilder.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.selector_button_cyan));
        // repeat many times:
        itemUpdateToDBIcon = new ImageView(this);
        itemUpdateToDBIcon.setImageResource(R.drawable.ic_update_game);
        updateToDBButton = itemBuilder.setContentView(itemUpdateToDBIcon).build();
        updateToDBButton.setOnClickListener(this);
        updateToDBButton.setTag(UPDATE_GAME);

        itemDeleteFromDBIcon = new ImageView(this);
        itemDeleteFromDBIcon.setImageResource(R.drawable.ic_delete_game);
        deleteFromDBButton = itemBuilder.setContentView(itemDeleteFromDBIcon).build();
        deleteFromDBButton.setOnClickListener(this);
        deleteFromDBButton.setTag(DELETE_GAME);

        itemRemoveContactIcon = new ImageView(this);
        if (borrowerInfoLayout.getVisibility() == View.VISIBLE)
            itemRemoveContactIcon.setImageResource(R.drawable.ic_remove_contact);
        else {
            itemRemoveContactIcon.setImageResource(R.drawable.ic_add_borrower);
        }
        removeContactButton = itemBuilder.setContentView(itemRemoveContactIcon).build();
        removeContactButton.setOnClickListener(this);
        removeContactButton.setTag(DELETE_CONTACT);

        itemUpdateRatingIcon = new ImageView(this);
        itemUpdateRatingIcon.setImageResource(R.drawable.ic_add_rating_bar);
        updateRaitngButton = itemBuilder.setContentView(itemUpdateRatingIcon).build();
        updateRaitngButton.setOnClickListener(this);
        updateRaitngButton.setTag(UPDATE_RATING);
        // Create the menu with the items:
        updateGameActionMenu = new FloatingActionMenu.Builder(this)
                //.addSubActionView(updateToDBButton)
                .addSubActionView(deleteFromDBButton)
                .addSubActionView(removeContactButton)
                .addSubActionView(updateRaitngButton)
                .attachTo(updateGameActionButton)
                .build();
    }

    @Override
    public void onClick(View v) {
        /*update the rating of the game*/
        if (v.getTag().equals(UPDATE_RATING)) {
            if (gameRatingSmall.getVisibility() == View.VISIBLE) {
                if (game.getRating() != -1) {
                    Log.i("rating bar", "show large hide small");
                    gameRatingSmall.setVisibility(View.GONE);
                    gameRatingLayout.setVisibility(View.VISIBLE);
                    itemUpdateRatingIcon.setImageResource(R.drawable.ic_hide_rating_bar);
                }
            } else {
                if (game.getRating() == -1) {
                    Log.i("rating bar", "show large");
                    gameRatingLayout.setVisibility(View.VISIBLE);
                    itemUpdateRatingIcon.setImageResource(R.drawable.ic_hide_rating_bar);
                    gameRatingSmall.setRating(gameRating.getRating());
                    game.setRating((int) gameRating.getRating());
                    cognitoSyncGames.updateRating(game);
                    updateFragmentUIsByUpdateDatabase();
                } else {
                    Log.i("rating bar", "show small hide large");
                    gameRatingSmall.setVisibility(View.VISIBLE);
                    gameRatingLayout.setVisibility(View.GONE);
                    itemUpdateRatingIcon.setImageResource(R.drawable.ic_add_rating_bar);
                    gameRatingSmall.setRating(gameRating.getRating());
                    game.setRating((int) gameRating.getRating());
                    cognitoSyncGames.updateRating(game);
                    updateFragmentUIsByUpdateDatabase();
                }
                updateGameActionMenu.close(true);
            }
            //updateGameActionMenu.close(true);
        }
        /*delete the contact/borrower*/
        if (v.getTag().equals(DELETE_CONTACT)) {
            if (borrowerInfoLayout.getVisibility() == View.VISIBLE) {
                Log.i("contact", "remove");
                borrowerInfoLayout.setVisibility(View.GONE);
                itemRemoveContactIcon.setImageResource(R.drawable.ic_add_borrower);
                game.setContactId(-1);
                cognitoSyncGames.updateContactId(game);
                updateFragmentUIsByUpdateDatabase();
                TextView nameTextView = (TextView) findViewById(R.id.borrowerNameTextView);
                nameTextView.setText("Name");
                phoneLinearLayout.removeAllViews();
                emailLinearLayout.removeAllViews();
                Toast.makeText(this, "Removed contact from this game.", Toast.LENGTH_SHORT).show();
            } else {
                Log.i("contact", "picker launch");
                doLaunchContactPicker(v);
                borrowerInfoLayout.setVisibility(View.VISIBLE);
                itemRemoveContactIcon.setImageResource(R.drawable.ic_remove_contact);
            }
            //updateGameActionMenu.close(true);
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
                            cognitoSyncGames.deleteRecord(game.getUpcCode());
                            List<Game> gameList = dataSource.getAllGames();
                            dataSource.close();
                            Toast.makeText(context, game.getTitle() + " deleted.", Toast.LENGTH_SHORT).show();
                            if (NaviDrawerActivity.CURRENT_FRAGMENT.equals("home")) {
                                if (HomeFragment.imageSlideAdapter != null) {
                                    HomeFragment.imageSlideAdapter.updateList(gameList);
                                    if (gameList == null) {
                                        HomeFragment.changeUIsWhenDataSetChange(false);
                                    }
                                }
                            } else if (NaviDrawerActivity.CURRENT_FRAGMENT.equals("games")) {
                                if (GamesFragment.gamesAdapter != null) {
                                    GamesFragment.gamesAdapter.updateList(gameList);
                                    if (gameList == null) {
                                        GamesFragment.changeUIsWhenDataSetChange(false);
                                    }
                                }
                            }
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
    }

    public void updateFragmentUIsByUpdateDatabase() {
        dataSource.open();
        dataSource.updateGame(game);
        List<Game> gamesList = dataSource.getAllGames();
        dataSource.close();
        if (NaviDrawerActivity.CURRENT_FRAGMENT.equals("games")) {
            if (GamesFragment.gamesAdapter != null) {
                GamesFragment.gamesAdapter.updateList(gamesList);
                GamesFragment.changeUIsWhenDataSetChange(true);
            } else {
                GamesFragment.gamesAdapter = new GameListAdapter(gamesList, GamesFragment.naviDrawerActivity);
                GamesFragment.changeUIsWhenDataSetChange(true);
            }
        } else if (NaviDrawerActivity.CURRENT_FRAGMENT.equals("home")) {
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
    }

    public void doLaunchContactPicker(View view) {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
    }

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
                    cognitoSyncGames.updateContactId(game);
                    Toast.makeText(this, "Added contact to this game.", Toast.LENGTH_SHORT).show();
                    updateFragmentUIsByUpdateDatabase();
                    break;
            }
        } else {
            // gracefully handle failure
            Log.w(DEBUG_TAG, "Warning: activity result not ok");
            switch (requestCode) {
                case CONTACT_PICKER_RESULT:
                    borrowerInfoLayout.setVisibility(View.GONE);
                    itemRemoveContactIcon.setImageResource(R.drawable.ic_add_borrower);
                    break;
            }
        }
    }
}
