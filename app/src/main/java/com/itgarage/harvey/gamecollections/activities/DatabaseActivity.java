package com.itgarage.harvey.gamecollections.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.itgarage.harvey.gamecollections.R;
import com.itgarage.harvey.gamecollections.db.GamesDataSource;
import com.itgarage.harvey.gamecollections.models.Game;

import java.util.ArrayList;


public class DatabaseActivity extends ActionBarActivity {
    private ImageButton addGameButton, deleteGameButton, getAllGamesButton, getGameButton, updateGameButton, dropTableButton, createTableButton;
    private EditText gameTitleEditText, gamePlatformEditText, gameIdEditText, resultEditText;
    private Toolbar toolbar;
    private GamesDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Database Test");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        onCreateDB();
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

    public void onCreateDB() {
        try {
            dataSource = new GamesDataSource(this);
            dataSource.open();
        } catch (Exception e) {
            Log.e("GAMES ERROR", "Error Creating Database");
        } finally {
            Log.i("DB operation", "DB opened.");
        }
    }

    public void onAddGame(View view) {
        hideKeyboard();
        if (gameTitleEditText.getText().length() == 0) {
            Toast.makeText(this, "please enter title.", Toast.LENGTH_SHORT).show();
        } else {
            String title = gameTitleEditText.getText().toString();
            if (gamePlatformEditText.getText().length() == 0) {
                Toast.makeText(this, "please enter platform.", Toast.LENGTH_SHORT).show();
            } else {
                String platform = gamePlatformEditText.getText().toString();
                Game game = new Game();
                game.setTitle(title);
                game.setPlatform(platform);
                dataSource.addGame(game);
                onGetGames(view);
            }
        }
    }

    public void onDeleteGame(View view) {
        hideKeyboard();
        int id = Integer.parseInt(gameIdEditText.getText().toString());
        Boolean result = dataSource.deleteGame(id);
        if (result) {
            onGetGames(view);
            String results = resultEditText.getText().toString();
            results = results + "Delete Successfully.";
            resultEditText.setText(results);
        } else {
            resultEditText.setText("Delete Failed.");
        }
    }

    public void onGetGames(View view) {
        hideKeyboard();
        ArrayList<Game> gamesList = dataSource.getAllGames();
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
        hideKeyboard();
        int id = Integer.parseInt(gameIdEditText.getText().toString());
        Game game = dataSource.getGame(id);
        resultEditText.setText(game.toString());
    }

    public void onUpdateGame(View view) {
        hideKeyboard();
        String title = null;
        String platform = null;
        int id = -1;
        if (gameTitleEditText.getText().length() != 0) {
            title = gameTitleEditText.getText().toString();
        }
        if (gamePlatformEditText.getText().length() != 0) {
            platform = gamePlatformEditText.getText().toString();
        }
        if (gameIdEditText.getText().length() != 0) {
            id = Integer.parseInt(gameIdEditText.getText().toString());
        }
        Game game = new Game();
        game.setId(id);
        game.setTitle(title);
        game.setPlatform(platform);
        if (dataSource.updateGame(game)) {
            onGetGames(view);
            String results = resultEditText.getText().toString();
            results = results + "Update Successfully.";
            resultEditText.setText(results);
        } else {
            resultEditText.setText("Update failed.");
        }
    }

    public void onDeleteTable(View view) {
        dataSource.dropTable();
        resultEditText.setText("Table Dropped");
    }

    public void onCreateTable(View view) {

        resultEditText.setText("Table Created");
    }

    @Override
    protected void onDestroy() {
        dataSource.close();
        super.onDestroy();
    }

    public void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) DatabaseActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(DatabaseActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
