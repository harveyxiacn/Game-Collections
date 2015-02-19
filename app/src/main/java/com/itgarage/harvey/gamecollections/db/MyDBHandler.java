package com.itgarage.harvey.gamecollections.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "gameCollectionDB.db";
    public static final String TABLE_GAMES = "games";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_GAME_TITLE = "title";
    public static final String COLUMN_GAME_PLATFORM = "platform";
    public static final String COLUMN_GAME_GENRE = "genre";
    public static final String COLUMN_GAME_HARDWARE_PLATFORM = "hardwarePlatform";
    public static final String COLUMN_GAME_EDITION = "edition";
    public static final String COLUMN_GAME_PUBLICATION_DATE = "publicationDate";
    public static final String COLUMN_GAME_RELEASE_DATE = "releaseDate";
    public static final String COLUMN_GAME_MANUFACTURER = "manufacturer";
    public static final String COLUMN_GAME_SMALL_IMAGE = "smallImage";
    public static final String COLUMN_GAME_MEDIUM_IMAGE = "mediumImage";
    public static final String COLUMN_GAME_LARGE_IMAGE = "largeImage";

    public static final String CREATE_GAMES_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_GAMES + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_GAME_TITLE + " TEXT NOT NULL,"
            + COLUMN_GAME_PLATFORM + " TEXT,"
            + COLUMN_GAME_GENRE + " TEXT,"
            + COLUMN_GAME_HARDWARE_PLATFORM + " TEXT,"
            + COLUMN_GAME_MANUFACTURER + " TEXT,"
            + COLUMN_GAME_EDITION + " TEXT,"
            + COLUMN_GAME_PUBLICATION_DATE + " TEXT,"
            + COLUMN_GAME_RELEASE_DATE + " TEXT,"
            + COLUMN_GAME_SMALL_IMAGE + " TEXT,"
            + COLUMN_GAME_MEDIUM_IMAGE + " TEXT,"
            + COLUMN_GAME_LARGE_IMAGE + " TEXT);";

    public MyDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("DB operation", "DB onCreate()");
        db.execSQL(CREATE_GAMES_TABLE);
        Log.i("DB operation", "Table " + TABLE_GAMES + " created.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MyDBHandler.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAMES);
        onCreate(db);
    }
}
