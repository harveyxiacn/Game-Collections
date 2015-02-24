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

public class GameDetailActivity extends ActionBarActivity implements View.OnClickListener{
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

    SubActionButton updateToDBButton, updateContactButton, removeContactButton, updateRaitngButton, deleteFromDBButton;
    ImageView itemUpdateRatingIcon, itemRemoveContactIcon, itemUpdateContactIcon, itemUpdateToDBIcon, itemDeleteFromDBIcon;
    SubActionButton.Builder itemBuilder;
    FloatingActionMenu updateGameActionMenu;
    public GamesDataSource dataSource;
    public Game game;
    public Context context = this;

    private static final int CONTACT_PICKER_RESULT = 1;
    static final String DEBUG_TAG = "DEBUG_TAG";

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
        if(postion!=-1) {
             game = gameList.get(postion);
        }
        if(intentTitle!=null){
            String gameTitle = "";
            int position = 0;
            Game gameTemp = null;
            do{
                gameTemp = gameList.get(position);
                gameTitle = gameTemp.getTitle();
                if(gameTitle.equals(intentTitle))
                    break;
                position++;
            }while (position<gameList.size());
            game = gameTemp;
        }
        if(intentBarcode!=null){
            game = dataSource.getGameByUPC(intentBarcode);
        }
        dataSource.close();
        String title;
        titleTextView = (TextView) findViewById(R.id.textViewGameTitle);
        platformTextview = (TextView) findViewById(R.id.textViewGamePlatform);
        gameImage = (SpearImageView) findViewById(R.id.imageViewGameImage);
        gameAttributesLayout = (LinearLayout) findViewById(R.id.gameAttributesLayout);

        gameRating = (RatingBar) findViewById(R.id.gameRatingBar);
        ratingTextView = (TextView) findViewById(R.id.gameRatingText);
        gameRatingSmall = (RatingBar) findViewById(R.id.gameRatingBarSmall);
        if(game!=null){
            title = game.getTitle();
            getSupportActionBar().setTitle(title);

            titleTextView.setText(title);
            String platform = game.getPlatform();
            if (platform!=null) {

                platformTextview.setText(game.getPlatform());
                platformTextview.setText(platform);
            }

            String mediumImage = game.getMediumImage();

            gameImage.setImageFromUri(mediumImage);



            String genre = game.getGenre();
            if (genre!=null) {
                genreTextView = new TextView(this);
                genreTextView.setTextSize(20);
                gameAttributesLayout.addView(genreTextView);
                genreTextView.setText("Genre: " + genre);
                genreTextView.setVisibility(View.VISIBLE);
            }

            String hardwarePlatform = game.getHardwarePlatform();
            if (hardwarePlatform!=null) {
                hardwarePlatformTextView = new TextView(this);
                hardwarePlatformTextView.setTextSize(20);
                gameAttributesLayout.addView(hardwarePlatformTextView);
                hardwarePlatformTextView.setText("Hardware Platform: " + hardwarePlatform);
                hardwarePlatformTextView.setVisibility(View.VISIBLE);
            }

            String edition = game.getEdition();
            if (edition!=null) {
                editionTextView = new TextView(this);
                editionTextView.setTextSize(20);
                gameAttributesLayout.addView(editionTextView);
                editionTextView.setText("Edition: " + edition);
                editionTextView.setVisibility(View.VISIBLE);
            }

            String manufacturer = game.getManufacturer();
            if (manufacturer!=null) {
                manufacturerTextView = new TextView(this);
                manufacturerTextView.setTextSize(20);
                gameAttributesLayout.addView(manufacturerTextView);
                manufacturerTextView.setText("Manufacturer: " + manufacturer);
                manufacturerTextView.setVisibility(View.VISIBLE);
            }

            String publicationDate = game.getPublicationDate();
            if (publicationDate!=null) {
                publicationDateTextView = new TextView(this);
                publicationDateTextView.setTextSize(20);
                gameAttributesLayout.addView(publicationDateTextView);
                publicationDateTextView.setText("Publication Date: " + publicationDate);
                publicationDateTextView.setVisibility(View.VISIBLE);
            }

            String releaseDate = game.getReleaseDate();
            if (releaseDate!=null) {
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
            if(contactId != -1){
                borrowerInfoLayout.setVisibility(View.VISIBLE);
                getContact("");
            }else {
                borrowerInfoLayout.setVisibility(View.GONE);
            }
            emailLinearLayout = (LinearLayout) findViewById(R.id.emailLinearLayout);
            phoneLinearLayout = (LinearLayout) findViewById(R.id.phoneLinearLayout);

            createGameUpdateFloatingActionButtons();

        }else {
            titleTextView.setText(getString(R.string.null_game_title));
        }

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
        if(borrowerInfoLayout.getVisibility() == View.VISIBLE)
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_detail, menu);
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

    @Override
    public void onClick(View v) {
        /*update the rating of the game*/
        if(v.getTag().equals(UPDATE_RATING)){
            if(gameRatingSmall.getVisibility()==View.VISIBLE) {
                if(game.getRating()!=-1) {
                    Log.i("rating bar", "show large hide small");
                    gameRatingSmall.setVisibility(View.GONE);
                    gameRatingLayout.setVisibility(View.VISIBLE);
                    itemUpdateRatingIcon.setImageResource(R.drawable.ic_hide_rating_bar);
                }
            }else {
                if(game.getRating()==-1){
                    Log.i("rating bar", "show large");
                    gameRatingLayout.setVisibility(View.VISIBLE);
                    itemUpdateRatingIcon.setImageResource(R.drawable.ic_hide_rating_bar);
                    gameRatingSmall.setRating(gameRating.getRating());
                    game.setRating((int)gameRating.getRating());
                    /*dataSource.open();
                    dataSource.updateGame(game);
                    List<Game> gameList = dataSource.getAllGames();
                    dataSource.close();
                    GamesFragment.gamesAdapter.updateList(gameList);*/
                    updateFragmentUIsByUpdateDatabase();
                }else {
                    Log.i("rating bar", "show small hide large");
                    gameRatingSmall.setVisibility(View.VISIBLE);
                    gameRatingLayout.setVisibility(View.GONE);
                    itemUpdateRatingIcon.setImageResource(R.drawable.ic_add_rating_bar);
                    gameRatingSmall.setRating(gameRating.getRating());
                    game.setRating((int)gameRating.getRating());
                    /*dataSource.open();
                    dataSource.updateGame(game);
                    List<Game> gameList = dataSource.getAllGames();
                    dataSource.close();
                    GamesFragment.gamesAdapter.updateList(gameList);*/
                    updateFragmentUIsByUpdateDatabase();
                }
                updateGameActionMenu.close(true);
            }
            //updateGameActionMenu.close(true);
        }
        /*delete the contact/borrower*/
        if(v.getTag().equals(DELETE_CONTACT)){
            if(borrowerInfoLayout.getVisibility()==View.VISIBLE) {
                Log.i("contact", "remove");
                borrowerInfoLayout.setVisibility(View.GONE);
                itemRemoveContactIcon.setImageResource(R.drawable.ic_add_borrower);
                game.setContactId(-1);
                updateFragmentUIsByUpdateDatabase();
                TextView nameTextView = (TextView) findViewById(R.id.borrowerNameTextView);
                nameTextView.setText("Name");
                phoneLinearLayout.removeAllViews();
                emailLinearLayout.removeAllViews();
                Toast.makeText(this, "Removed contact from this game.", Toast.LENGTH_SHORT).show();
            }else {
                Log.i("contact", "picker launch");
                doLaunchContactPicker(v);
                borrowerInfoLayout.setVisibility(View.VISIBLE);
                itemRemoveContactIcon.setImageResource(R.drawable.ic_remove_contact);
            }
            //updateGameActionMenu.close(true);
        }
        /*Delete the game*/
        if(v.getTag().equals(DELETE_GAME)){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Delete "+titleTextView.getText()+"?")
            .setTitle("Delete confirmation")
            .setIcon(R.drawable.ic_delete_confirm)
            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    dataSource.open();
                    dataSource.deleteGame(game.getId());
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

    public void updateFragmentUIsByUpdateDatabase(){
        dataSource.open();
        dataSource.updateGame(game);
        List<Game> gamesList = dataSource.getAllGames();
        dataSource.close();
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
            Cursor emailCursor = getContentResolver().query(Email.CONTENT_URI,
                    null, Email.CONTACT_ID + "=?", new String[] { id },
                    null);

            int emailIdx = emailCursor.getColumnIndex(Email.DATA);

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



            emailLinearLayout.addView(emailAddress);

            emailCursor.close();

            // get phone numbers
            TextView phoneTag = (TextView) findViewById(R.id.borrowerPhoneTag);
            String phoneNumber = "";
            int phoneType = -1;
            int hasPhoneNumber = Integer.parseInt(nameCursor.getString(nameCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
            if(hasPhoneNumber>0) {
                phoneTag.setVisibility(View.VISIBLE);
                Cursor phoneCursor = getContentResolver().query(Phone.CONTENT_URI, null,
                        Phone.CONTACT_ID + "=?", new String[]{id}, null);

                int phoneIndex = 0;
                while (phoneCursor.moveToNext()) {
                    phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(Phone.NORMALIZED_NUMBER));
                    phoneType = phoneCursor.getInt(Phone.TYPE_MOBILE);
                    if(phoneType == -1){
                        phoneType = phoneCursor.getInt(Phone.TYPE_WORK_MOBILE);
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
