package com.itgarage.harvey.gamecollections.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import com.itgarage.harvey.gamecollections.db.GamesDataSource;
import com.itgarage.harvey.gamecollections.fragments.GamesFragment;
import com.itgarage.harvey.gamecollections.fragments.HomeFragment;
import com.itgarage.harvey.gamecollections.models.Game;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.util.List;

import me.xiaopan.android.spear.SpearImageView;

public class GameDetailActivity extends ActionBarActivity implements View.OnClickListener{
    Toolbar toolbar;
    SpearImageView gameImage;
    TextView titleTextView, platformTextview;
    TextView genreTextView, hardwarePlatformTextView, manufacturerTextView,
            editionTextView, publicationDateTextView, releaseDateTextView, ratingTextView;
    RatingBar gameRating, gameRatingSmall;
    LinearLayout gameAttributesLayout, gameRatingLayout, borrowerInfoLayout;
    static final String UPDATE_GAME = "update game";
    static final String DELETE_GAME = "delete game";
    static final String UPDATE_RATING = "update rating";
    static final String DELETE_RATING = "delete rating";
    static final String UPDATE_CONTACT = "update contact";
    static final String DELETE_CONTACT = "delete contact";

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
        itemRemoveContactIcon.setImageResource(R.drawable.ic_remove_contact);
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
                .addSubActionView(updateToDBButton)
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
                    dataSource.open();
                    dataSource.updateGame(game);
                    List<Game> gameList = dataSource.getAllGames();
                    dataSource.close();
                    GamesFragment.gamesAdapter.updateList(gameList);
                }else {
                    Log.i("rating bar", "show small hide large");
                    gameRatingSmall.setVisibility(View.VISIBLE);
                    gameRatingLayout.setVisibility(View.GONE);
                    itemUpdateRatingIcon.setImageResource(R.drawable.ic_add_rating_bar);
                    gameRatingSmall.setRating(gameRating.getRating());
                    game.setRating((int)gameRating.getRating());
                    dataSource.open();
                    dataSource.updateGame(game);
                    List<Game> gameList = dataSource.getAllGames();
                    dataSource.close();
                    GamesFragment.gamesAdapter.updateList(gameList);
                }
            }
            updateGameActionMenu.close(true);
        }
        /*delete the contact/borrower*/
        if(v.getTag().equals(DELETE_CONTACT)){
            if(borrowerInfoLayout.getVisibility()==View.VISIBLE) {
                Log.i("rating bar", "large");
                borrowerInfoLayout.setVisibility(View.GONE);
                itemRemoveContactIcon.setImageResource(R.drawable.ic_add_borrower);
                //updateRaitngButton = itemBuilder.setContentView(itemUpdateRatingIcon).build();
            }else {
                Log.i("rating bar", "small");
                borrowerInfoLayout.setVisibility(View.VISIBLE);
                itemRemoveContactIcon.setImageResource(R.drawable.ic_remove_contact);
            }
            updateGameActionMenu.close(true);
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

    public void doLaunchContactPicker(View view) {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CONTACT_PICKER_RESULT:
                    // handle contact results
                    Cursor cursor = null;
                    String email = "";
                    try{
                        Uri result = data.getData();
                        Log.v(DEBUG_TAG, "Got a contact result: "
                                + result.toString());
                        // get the contact id from the Uri
                        String id = result.getLastPathSegment();

                        // query for everything email
                        cursor = getContentResolver().query(Email.CONTENT_URI,
                                null, Email.CONTACT_ID + "=?", new String[] { id },
                                null);

                        int emailIdx = cursor.getColumnIndex(Email.DATA);

                        // let's just get the first email
                        if (cursor.moveToFirst()) {
                            email = cursor.getString(emailIdx);
                            Log.v(DEBUG_TAG, "Got email: " + email);
                        } else {
                            Log.w(DEBUG_TAG, "No results");
                        }
                    } catch (Exception e) {
                        Log.e(DEBUG_TAG, "Failed to get email data", e);
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                        *//*EditText emailEntry = (EditText) findViewById(R.id.invite_email);
                        emailEntry.setText(email);
                        if (email.length() == 0) {
                            Toast.makeText(this, "No email found for contact.",
                                    Toast.LENGTH_LONG).show();
                        }*//*

                    }
                    break;
            }

        } else {
            // gracefully handle failure
            Log.w(DEBUG_TAG, "Warning: activity result not ok");
        }
    }*/
}
