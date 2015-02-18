package com.itgarage.harvey.gamecollections.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.itgarage.harvey.gamecollections.models.Game;

import java.util.ArrayList;

public class MyDBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "gameCollectionDB.db";
    private static final String TABLE_GAMES = "games";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_GAMETITLE = "title";
    public static final String COLUMN_GAMEPLATFORM = "platform";
    public static final String COLUMN_GAMEGENRE = "genre";
    public static final String COLUMN_GAMEHARDWAREPLATFORM = "hardwarePlatform";
    public static final String COLUMN_GAMEMANUFACTURER = "manufacturer";
    public static final String COLUMN_GAMEFEATURE = "feature";
    public static final String COLUMN_GAMESMALLIMAGE = "smallImage";
    public static final String COLUMN_GAMEMEDIUMIMAGE = "mediumImage";
    public static final String COLUMN_GAMELARGEIMAGE = "largeImage";

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAMES);
        onCreate(db);
    }

    public void createTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        String CREATE_GAMES_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_GAMES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_GAMETITLE + " TEXT NOT NULL,"
                + COLUMN_GAMEPLATFORM + " TEXT,"
                + COLUMN_GAMEGENRE + " TEXT,"
                + COLUMN_GAMEHARDWAREPLATFORM + " TEXT,"
                + COLUMN_GAMEMANUFACTURER + " TEXT,"
                + COLUMN_GAMEFEATURE + " TEXT,"
                + COLUMN_GAMESMALLIMAGE + " TEXT,"
                + COLUMN_GAMEMEDIUMIMAGE + " TEXT,"
                + COLUMN_GAMELARGEIMAGE + " TEXT)";
        db.execSQL(CREATE_GAMES_TABLE);
    }

    public void addGame(Game game) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_GAMETITLE, game.getTitle());
        if (game.getGenre() != null) {
            values.put(COLUMN_GAMEGENRE, game.getGenre());
        }
        if (game.getPlatform() != null) {
            values.put(COLUMN_GAMEPLATFORM, game.getPlatform());
        }
        if (game.getHardwarePlatform() != null) {
            values.put(COLUMN_GAMEHARDWAREPLATFORM, game.getHardwarePlatform());
        }
        if (game.getManufacturer() != null) {
            values.put(COLUMN_GAMEMANUFACTURER, game.getManufacturer());
        }
        if (game.getFeature() != null) {
            values.put(COLUMN_GAMEFEATURE, game.getFeature());
        }
        if (game.getSmallImage() != null) {
            values.put(COLUMN_GAMESMALLIMAGE, game.getSmallImage());
        }
        if (game.getMediumImage() != null) {
            values.put(COLUMN_GAMEMEDIUMIMAGE, game.getMediumImage());
        }
        if (game.getLargeImage() != null) {
            values.put(COLUMN_GAMELARGEIMAGE, game.getLargeImage());
        }

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_GAMES, null, values);
        db.close();
    }

    public ArrayList<Game> getAllGames() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_GAMES, null);

        int idColumn = cursor.getColumnIndex(COLUMN_ID);
        int titleColumn = cursor.getColumnIndex(COLUMN_GAMETITLE);
        int genreColumn = cursor.getColumnIndex(COLUMN_GAMEGENRE);
        int platformColumn = cursor.getColumnIndex(COLUMN_GAMEPLATFORM);
        int hardwarePlatformColumn = cursor.getColumnIndex(COLUMN_GAMEHARDWAREPLATFORM);
        int manufacturerColumn = cursor.getColumnIndex(COLUMN_GAMEMANUFACTURER);
        int featureColumn = cursor.getColumnIndex(COLUMN_GAMEFEATURE);
        int smallImageColumn = cursor.getColumnIndex(COLUMN_GAMESMALLIMAGE);
        int mediumImageColumn = cursor.getColumnIndex(COLUMN_GAMEMEDIUMIMAGE);
        int largeImageColumn = cursor.getColumnIndex(COLUMN_GAMELARGEIMAGE);

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
        db.close();
        return gamesList;
    }

    public Game getGame(int id) {
        Game game = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_GAMES + " WHERE " + COLUMN_ID + " = \"" + id + "\"", null);

        int titleColumn = cursor.getColumnIndex(COLUMN_GAMETITLE);
        int genreColumn = cursor.getColumnIndex(COLUMN_GAMEGENRE);
        int platformColumn = cursor.getColumnIndex(COLUMN_GAMEPLATFORM);
        int hardwarePlatformColumn = cursor.getColumnIndex(COLUMN_GAMEHARDWAREPLATFORM);
        int manufacturerColumn = cursor.getColumnIndex(COLUMN_GAMEMANUFACTURER);
        int featureColumn = cursor.getColumnIndex(COLUMN_GAMEFEATURE);
        int smallImageColumn = cursor.getColumnIndex(COLUMN_GAMESMALLIMAGE);
        int mediumImageColumn = cursor.getColumnIndex(COLUMN_GAMEMEDIUMIMAGE);
        int largeImageColumn = cursor.getColumnIndex(COLUMN_GAMELARGEIMAGE);

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

        db.close();
        return game;
    }

    public boolean deleteGame(int id) {
        boolean result = false;
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_GAMES + " WHERE " + COLUMN_ID + " = \"" + id + "\"";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() > 0) {
            db.delete(TABLE_GAMES, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
            cursor.close();
            result = true;
        }
        db.close();
        return result;
    }

    public boolean updateGame(int id) {
        boolean result = false;

        return result;
    }

    public void dropTable() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAMES);
    }
}
