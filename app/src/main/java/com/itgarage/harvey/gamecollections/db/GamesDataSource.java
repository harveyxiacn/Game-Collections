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

    public long addGame(Game game) {
        ContentValues values = new ContentValues();

        values.put(MyDBHandler.COLUMN_GAME_TITLE, game.getTitle());
        if (!game.getGenre().equals("")) {
            values.put(MyDBHandler.COLUMN_GAME_GENRE, game.getGenre());
        }
        if (!game.getPlatform().equals("")) {
            values.put(MyDBHandler.COLUMN_GAME_PLATFORM, game.getPlatform());
        }
        if (!game.getHardwarePlatform().equals("")) {
            values.put(MyDBHandler.COLUMN_GAME_HARDWARE_PLATFORM, game.getHardwarePlatform());
        }
        if (!game.getManufacturer().equals("")) {
            values.put(MyDBHandler.COLUMN_GAME_MANUFACTURER, game.getManufacturer());
        }
        if (!game.getEdition().equals("")) {
            values.put(MyDBHandler.COLUMN_GAME_EDITION, game.getEdition());
        }
        if (!game.getPublicationDate().equals("")) {
            values.put(MyDBHandler.COLUMN_GAME_PUBLICATION_DATE, game.getPublicationDate());
        }
        if (!game.getReleaseDate().equals("")) {
            values.put(MyDBHandler.COLUMN_GAME_RELEASE_DATE, game.getReleaseDate());
        }
        if (!game.getSmallImage().equals("")) {
            values.put(MyDBHandler.COLUMN_GAME_SMALL_IMAGE, game.getSmallImage());
        }
        if (!game.getMediumImage().equals("")) {
            values.put(MyDBHandler.COLUMN_GAME_MEDIUM_IMAGE, game.getMediumImage());
        }
        if (!game.getLargeImage().equals("")) {
            values.put(MyDBHandler.COLUMN_GAME_LARGE_IMAGE, game.getLargeImage());
        }
        values.put(MyDBHandler.COLUMN_GAME_RATING, game.getRating());
        if (!game.getUpcCode().equals("")) {
            values.put(MyDBHandler.COLUMN_GAME_UPC_CODE, game.getUpcCode());
        }
        values.put(MyDBHandler.COLUMN_CONTACT_ID, game.getContactId());

        long insertId = db.insert(MyDBHandler.TABLE_GAMES, null, values);
        Log.i("DB operation", "inserted " + insertId);
        return insertId;
    }

    public ArrayList<Game> getAllGames() {
        Cursor cursor = db.rawQuery("SELECT * FROM " + MyDBHandler.TABLE_GAMES, null);

        int idColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_ID);
        int titleColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAME_TITLE);
        int genreColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAME_GENRE);
        int platformColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAME_PLATFORM);
        int hardwarePlatformColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAME_HARDWARE_PLATFORM);
        int manufacturerColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAME_MANUFACTURER);
        int editionColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAME_EDITION);
        int publicationDateColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAME_PUBLICATION_DATE);
        int releaseDateColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAME_RELEASE_DATE);
        int smallImageColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAME_SMALL_IMAGE);
        int mediumImageColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAME_MEDIUM_IMAGE);
        int largeImageColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAME_LARGE_IMAGE);
        int ratingColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAME_RATING);
        int upcCodeColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAME_UPC_CODE);
        int contactIdColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_CONTACT_ID);

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
                String edition = cursor.getString(editionColumn);
                String publicationDate = cursor.getString(publicationDateColumn);
                String releaseDate = cursor.getString(releaseDateColumn);
                String smallImage = cursor.getString(smallImageColumn);
                String mediumImage = cursor.getString(mediumImageColumn);
                String largeImage = cursor.getString(largeImageColumn);
                int rating = cursor.getInt(ratingColumn);
                String upcCode = cursor.getString(upcCodeColumn);
                int contactId = cursor.getInt(contactIdColumn);
                Game game = new Game(id, title, platform, genre, hardwarePlatform, edition, publicationDate, releaseDate, manufacturer, smallImage, mediumImage, largeImage, rating, upcCode, contactId);
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

        int titleColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAME_TITLE);
        int genreColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAME_GENRE);
        int platformColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAME_PLATFORM);
        int hardwarePlatformColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAME_HARDWARE_PLATFORM);
        int manufacturerColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAME_MANUFACTURER);
        int editionColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAME_EDITION);
        int publicationDateColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAME_PUBLICATION_DATE);
        int releaseDateColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAME_RELEASE_DATE);
        int smallImageColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAME_SMALL_IMAGE);
        int mediumImageColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAME_MEDIUM_IMAGE);
        int largeImageColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAME_LARGE_IMAGE);
        int ratingColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAME_RATING);
        int upcCodeColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAME_UPC_CODE);
        int contactIdColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_CONTACT_ID);

        cursor.moveToFirst();
        if ((cursor.getCount() > 0)) {
            String title = cursor.getString(titleColumn);
            String genre = cursor.getString(genreColumn);
            String platform = cursor.getString(platformColumn);
            String hardwarePlatform = cursor.getString(hardwarePlatformColumn);
            String manufacturer = cursor.getString(manufacturerColumn);
            String edition = cursor.getString(editionColumn);
            String publicationDate = cursor.getString(publicationDateColumn);
            String releaseDate = cursor.getString(releaseDateColumn);
            String smallImage = cursor.getString(smallImageColumn);
            String mediumImage = cursor.getString(mediumImageColumn);
            String largeImage = cursor.getString(largeImageColumn);
            int rating = cursor.getInt(ratingColumn);
            String upcCode = cursor.getString(upcCodeColumn);
            int contactId = cursor.getInt(contactIdColumn);

            game = new Game(id, title, platform, genre, hardwarePlatform, manufacturer, edition, publicationDate, releaseDate, smallImage, mediumImage, largeImage, rating, upcCode, contactId);
            cursor.close();
        }

//        close();
        return game;
    }

    public Game getGameByUPC(String upcCodeQuery) {
        Game game = null;
        Cursor cursor = db.rawQuery("SELECT * FROM " + MyDBHandler.TABLE_GAMES + " WHERE " + MyDBHandler.COLUMN_GAME_UPC_CODE + " = \"" + upcCodeQuery + "\"", null);

        int idColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_ID);
        int titleColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAME_TITLE);
        int genreColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAME_GENRE);
        int platformColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAME_PLATFORM);
        int hardwarePlatformColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAME_HARDWARE_PLATFORM);
        int manufacturerColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAME_MANUFACTURER);
        int editionColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAME_EDITION);
        int publicationDateColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAME_PUBLICATION_DATE);
        int releaseDateColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAME_RELEASE_DATE);
        int smallImageColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAME_SMALL_IMAGE);
        int mediumImageColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAME_MEDIUM_IMAGE);
        int largeImageColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAME_LARGE_IMAGE);
        int ratingColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAME_RATING);
        int upcCodeColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_GAME_UPC_CODE);
        int contactIdColumn = cursor.getColumnIndex(MyDBHandler.COLUMN_CONTACT_ID);

        cursor.moveToFirst();
        if ((cursor.getCount() > 0)) {
            int id = cursor.getInt(idColumn);
            String title = cursor.getString(titleColumn);
            String genre = cursor.getString(genreColumn);
            String platform = cursor.getString(platformColumn);
            String hardwarePlatform = cursor.getString(hardwarePlatformColumn);
            String manufacturer = cursor.getString(manufacturerColumn);
            String edition = cursor.getString(editionColumn);
            String publicationDate = cursor.getString(publicationDateColumn);
            String releaseDate = cursor.getString(releaseDateColumn);
            String smallImage = cursor.getString(smallImageColumn);
            String mediumImage = cursor.getString(mediumImageColumn);
            String largeImage = cursor.getString(largeImageColumn);
            int rating = cursor.getInt(ratingColumn);
            String upcCode = cursor.getString(upcCodeColumn);
            int contactId = cursor.getInt(contactIdColumn);

            game = new Game(id, title, platform, genre, hardwarePlatform, manufacturer, edition, publicationDate, releaseDate, smallImage, mediumImage, largeImage, rating, upcCode, contactId);
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

    public boolean updateGame(Game game) {
        boolean result = false;
        ContentValues values = new ContentValues();

        /*if (game.getTitle() != null) {
            values.put(MyDBHandler.COLUMN_GAME_TITLE, game.getTitle());
        }
        if (game.getGenre() != null) {
            values.put(MyDBHandler.COLUMN_GAME_GENRE, game.getGenre());
        }
        if (game.getPlatform() != null) {
            values.put(MyDBHandler.COLUMN_GAME_PLATFORM, game.getPlatform());
        }
        if (game.getHardwarePlatform() != null) {
            values.put(MyDBHandler.COLUMN_GAME_HARDWARE_PLATFORM, game.getHardwarePlatform());
        }
        if (game.getManufacturer() != null) {
            values.put(MyDBHandler.COLUMN_GAME_MANUFACTURER, game.getManufacturer());
        }
        if (game.getEdition() != null) {
            values.put(MyDBHandler.COLUMN_GAME_EDITION, game.getEdition());
        }
        if (game.getPublicationDate() != null) {
            values.put(MyDBHandler.COLUMN_GAME_PUBLICATION_DATE, game.getPublicationDate());
        }
        if (game.getReleaseDate() != null) {
            values.put(MyDBHandler.COLUMN_GAME_RELEASE_DATE, game.getReleaseDate());
        }
        if (game.getSmallImage() != null) {
            values.put(MyDBHandler.COLUMN_GAME_SMALL_IMAGE, game.getSmallImage());
        }
        if (game.getMediumImage() != null) {
            values.put(MyDBHandler.COLUMN_GAME_MEDIUM_IMAGE, game.getMediumImage());
        }
        if (game.getLargeImage() != null) {
            values.put(MyDBHandler.COLUMN_GAME_LARGE_IMAGE, game.getLargeImage());
        }*/
        values.put(MyDBHandler.COLUMN_GAME_RATING, game.getRating());
        values.put(MyDBHandler.COLUMN_CONTACT_ID, game.getContactId());

        String selection = MyDBHandler.COLUMN_ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(game.getId())};
        String query = "SELECT * FROM " + MyDBHandler.TABLE_GAMES + " WHERE " + MyDBHandler.COLUMN_ID + " = \"" + game.getId() + "\"";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() > 0) {
            int count = db.update(
                    MyDBHandler.TABLE_GAMES,
                    values,
                    selection,
                    selectionArgs
            );
            if (count > 0)
                result = true;
            else
                result = false;
        }
        cursor.close();
        return result;
    }

    public void dropTable() {
        db.execSQL("DROP TABLE IF EXISTS " + MyDBHandler.TABLE_GAMES);
    }
}
