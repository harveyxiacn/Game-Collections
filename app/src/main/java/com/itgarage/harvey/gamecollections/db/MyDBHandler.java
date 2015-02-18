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
    public static final String COLUMN_GAMETITLE = "title";
    public static final String COLUMN_GAMEPLATFORM = "platform";
    public static final String COLUMN_GAMEGENRE = "genre";
    public static final String COLUMN_GAMEHARDWAREPLATFORM = "hardwarePlatform";
    public static final String COLUMN_GAMEMANUFACTURER = "manufacturer";
    public static final String COLUMN_GAMEFEATURE = "feature";
    public static final String COLUMN_GAMESMALLIMAGE = "smallImage";
    public static final String COLUMN_GAMEMEDIUMIMAGE = "mediumImage";
    public static final String COLUMN_GAMELARGEIMAGE = "largeImage";

    public static final String CREATE_GAMES_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_GAMES + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_GAMETITLE + " TEXT NOT NULL,"
            + COLUMN_GAMEPLATFORM + " TEXT,"
            + COLUMN_GAMEGENRE + " TEXT,"
            + COLUMN_GAMEHARDWAREPLATFORM + " TEXT,"
            + COLUMN_GAMEMANUFACTURER + " TEXT,"
            + COLUMN_GAMEFEATURE + " TEXT,"
            + COLUMN_GAMESMALLIMAGE + " TEXT,"
            + COLUMN_GAMEMEDIUMIMAGE + " TEXT,"
            + COLUMN_GAMELARGEIMAGE + " TEXT);";

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
