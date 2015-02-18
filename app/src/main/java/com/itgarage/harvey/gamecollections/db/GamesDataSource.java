package com.itgarage.harvey.gamecollections.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.itgarage.harvey.gamecollections.models.Game;

import java.util.ArrayList;

public class GamesDataSource {
    private SQLiteDatabase db;
    private MyDBHandler dbHandler;
    private String[] allColumns = {MyDBHandler.COLUMN_ID,
            MyDBHandler.COLUMN_GAMETITLE,
            MyDBHandler.COLUMN_GAMEGENRE,
            MyDBHandler.COLUMN_GAMEPLATFORM,
            MyDBHandler.COLUMN_GAMEHARDWAREPLATFORM,
            MyDBHandler.COLUMN_GAMEMANUFACTURER,
            MyDBHandler.COLUMN_GAMEFEATURE,
            MyDBHandler.COLUMN_GAMESMALLIMAGE,
            MyDBHandler.COLUMN_GAMEMEDIUMIMAGE,
            MyDBHandler.COLUMN_GAMELARGEIMAGE
    };

    public GamesDataSource(Context context) {
        dbHandler = new MyDBHandler(context);
    }

    public void open() throws SQLException {
        db = dbHandler.getWritableDatabase();
        Log.i("DB operation", "database opened");
    }

    public void close() {
        dbHandler.close();
        Log.i("DB operation", "database closed");
    }

    public void addGame(Game game) {
        ContentValues values = new ContentValues();
        values.put(MyDBHandler.COLUMN_GAMETITLE, game.getTitle());
        if (game.getGenre() != null) {
            values.put(MyDBHandler.COLUMN_GAMEGENRE, game.getGenre());
        }
        if (game.getPlatform() != null) {
            values.put(MyDBHandler.COLUMN_GAMEPLATFORM, game.getPlatform());
        }
        if (game.getHardwarePlatform() != null) {
            values.put(MyDBHandler.COLUMN_GAMEHARDWAREPLATFORM, game.getHardwarePlatform());
        }
        if (game.getManufacturer() != null) {
            values.put(MyDBHandler.COLUMN_GAMEMANUFACTURER, game.getManufacturer());
        }
        if (game.getFeature() != null) {
            values.put(MyDBHandler.COLUMN_GAMEFEATURE, game.getFeature());
        }
        if (game.getSmallImage() != null) {
            values.put(MyDBHandler.COLUMN_GAMESMALLIMAGE, game.getSmallImage());
        }
        if (game.getMediumImage() != null) {
            values.put(MyDBHandler.COLUMN_GAMEMEDIUMIMAGE, game.getMediumImage());
        }
        if (game.getLargeImage() != null) {
            values.put(MyDBHandler.COLUMN_GAMELARGEIMAGE, game.getLargeImage());
        }
        long insertId = db.insert(MyDBHandler.TABLE_GAMES, null, values);
        Log.i("DB operation", "inserted " + insertId);
        //Cursor cursor = db.query(MyDBHandler.TABLE_GAMES, allColumns, MyDBHandler.COLUMN_ID + " = " + insertId, null, null, null, null);
        //cursor.moveToFirst();
//        close();
    }

    public ArrayList<Game> getAllGames() {
        Cursor cursor = db.rawQuery("SELECT * FROM " + MyDBHandler.TABLE_GAMES, null);

        int idColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_ID);
        int titleColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAMETITLE);
        int genreColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAMEGENRE);
        int platformColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAMEPLATFORM);
        int hardwarePlatformColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAMEHARDWAREPLATFORM);
        int manufacturerColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAMEMANUFACTURER);
        int featureColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAMEFEATURE);
        int smallImageColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAMESMALLIMAGE);
        int mediumImageColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAMEMEDIUMIMAGE);
        int largeImageColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAMELARGEIMAGE);

        cursor.moveToFirst();

        ArrayList<Game> gamesList = new ArrayList<Game>();

        if ((cursor.getCount() > 0)) {
            do {
                int id = cursor.getInt(idColumn);
                String title = cursor.getString(titleColumn);
                String genre = cursor.getString(genreColumn);
                String platform = cursor.getString(platformColumn);
                String hardwarePlatform = cursor.getString(hardwarePlatformColumn);
                String manufacturer = cursor.getString(manufacturerColumn);
                String feature = cursor.getString(featureColumn);
                String smallImage = cursor.getString(smallImageColumn);
                String mediumImage = cursor.getString(mediumImageColumn);
                String largeImage = cursor.getString(largeImageColumn);
                Game game = new Game(id, title, platform, genre, hardwarePlatform, manufacturer, feature, smallImage, mediumImage, largeImage);
                gamesList.add(game);
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            //Toast.makeText(DatabaseActivity.this, "No Results to Show", Toast.LENGTH_SHORT).show();
            gamesList = null;
        }
//        close();
        return gamesList;
    }

    public Game getGame(int id) {
        Game game = null;
        Cursor cursor = db.rawQuery("SELECT * FROM " + MyDBHandler.TABLE_GAMES + " WHERE " + MyDBHandler.COLUMN_ID + " = \"" + id + "\"", null);

        int titleColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAMETITLE);
        int genreColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAMEGENRE);
        int platformColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAMEPLATFORM);
        int hardwarePlatformColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAMEHARDWAREPLATFORM);
        int manufacturerColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAMEMANUFACTURER);
        int featureColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAMEFEATURE);
        int smallImageColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAMESMALLIMAGE);
        int mediumImageColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAMEMEDIUMIMAGE);
        int largeImageColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAMELARGEIMAGE);

        cursor.moveToFirst();
        if ((cursor.getCount() > 0)) {
            String title = cursor.getString(titleColumn);
            String genre = cursor.getString(genreColumn);
            String platform = cursor.getString(platformColumn);
            String hardwarePlatform = cursor.getString(hardwarePlatformColumn);
            String manufacturer = cursor.getString(manufacturerColumn);
            String feature = cursor.getString(featureColumn);
            String smallImage = cursor.getString(smallImageColumn);
            String mediumImage = cursor.getString(mediumImageColumn);
            String largeImage = cursor.getString(largeImageColumn);
            game = new Game(id, title, platform, genre, hardwarePlatform, manufacturer, feature, smallImage, mediumImage, largeImage);
            cursor.close();
        }

//        close();
        return game;
    }

    public boolean deleteGame(int id) {
        boolean result = false;
        String query = "SELECT * FROM " + MyDBHandler.TABLE_GAMES + " WHERE " + MyDBHandler.COLUMN_ID + " = \"" + id + "\"";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() > 0) {
            db.delete(MyDBHandler.TABLE_GAMES, MyDBHandler.COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
            cursor.close();
            result = true;
        }
//        close();
        return result;
    }

    public boolean updateGame(int id) {
        boolean result = false;

        return result;
    }

    public void dropTable() {
        db.execSQL("DROP TABLE IF EXISTS " + MyDBHandler.TABLE_GAMES);
    }
}
