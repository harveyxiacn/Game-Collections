package com.itgarage.harvey.gamecollections.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.itgarage.harvey.gamecollections.R;
import com.itgarage.harvey.gamecollections.db.MyDBHandler;
import com.itgarage.harvey.gamecollections.models.Game;

import java.util.ArrayList;


public class DatabaseActivity extends ActionBarActivity {
    private ImageButton createDBButton, addGameButton, deleteGameButton, getAllGamesButton, getGameButton, updateGameButton, dropTableButton, createTableButton;
    private EditText gameTitleEditText, gamePlatformEditText, gameIdEditText, resultEditText;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Database Test");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        createDBButton = (ImageButton) findViewById(R.id.btnCreateDB);
        addGameButton = (ImageButton) findViewById(R.id.btnAddGame);
        deleteGameButton = (ImageButton) findViewById(R.id.btnDeleteGame);
        getAllGamesButton = (ImageButton) findViewById(R.id.btnGetGames);
        getGameButton = (ImageButton) findViewById(R.id.btnGetGame);
        //updateGameButton = (Button)findViewById(R.id.btnUpdateGame);
        dropTableButton = (ImageButton) findViewById(R.id.btnDeleteTable);
        createTableButton = (ImageButton) findViewById(R.id.btnCreateTable);

        gameTitleEditText = (EditText) findViewById(R.id.game_title_editText);
        gamePlatformEditText = (EditText) findViewById(R.id.game_platform_editText);
        gameIdEditText = (EditText) findViewById(R.id.game_id_editText);
        resultEditText = (EditText) findViewById(R.id.resultEditText);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_database, menu);
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

    public void onCreateDB(View view) {
        try {
            MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        } catch (Exception e) {
            Log.e("GAMES ERROR", "Error Creating Database");
        } finally {
            addGameButton.setClickable(true);
            deleteGameButton.setClickable(true);
            getGameButton.setClickable(true);
            getAllGamesButton.setClickable(true);
            //updateGameButton.setClickable(true);
            dropTableButton.setClickable(true);
            createTableButton.setClickable(true);
        }
    }

    public void onAddGame(View view) {
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        Game game;
        String title = gameTitleEditText.getText().toString();
        String platform = gamePlatformEditText.getText().toString();
        if (gameTitleEditText.getText().length() == 0) {
            Toast.makeText(this, "please enter title.", Toast.LENGTH_SHORT).show();
            return;
        }
        game = new Game();
        game.setTitle(title);
        game.setPlatform(platform);
        dbHandler.addGame(game);
        onGetGames(view);
    }

    public void onDeleteGame(View view) {
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        int id = Integer.parseInt(gameIdEditText.getText().toString());
        Boolean result = dbHandler.deleteGame(id);
        if (result) {
            resultEditText.setText("Delete Successfully.");
        } else {
            resultEditText.setText("Delete Failed.");
        }
    }

    public void onGetGames(View view) {
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        ArrayList<Game> gamesList = dbHandler.getAllGames();
        String result = "";
        if (gamesList != null) {
            for (Game game : gamesList) {
                result += game.toString() + "\n";
            }
            resultEditText.setText(result);
        } else {
            resultEditText.setText("No Game is in database.");
        }
    }

    public void onGetGame(View view) {
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        int id = Integer.parseInt(gameIdEditText.getText().toString());
        Game game = dbHandler.getGame(id);
        resultEditText.setText(game.toString());
    }

    public void onDeleteTable(View view) {
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        dbHandler.dropTable();
        resultEditText.setText("Table Dropped");
    }

    public void onCreateTable(View view) {
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        dbHandler.createTable();
        resultEditText.setText("Table Created");
    }
}
