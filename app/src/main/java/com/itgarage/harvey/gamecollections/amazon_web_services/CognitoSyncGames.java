package com.itgarage.harvey.gamecollections.amazon_web_services;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.cognito.Dataset;
import com.amazonaws.mobileconnectors.cognito.DatasetMetadata;
import com.amazonaws.mobileconnectors.cognito.Record;
import com.amazonaws.mobileconnectors.cognito.SyncConflict;
import com.amazonaws.mobileconnectors.cognito.exceptions.DataStorageException;
import com.amazonaws.services.cognitoidentity.model.NotAuthorizedException;
import com.itgarage.harvey.gamecollections.activities.NaviDrawerActivity;
import com.itgarage.harvey.gamecollections.db.GamesDataSource;
import com.itgarage.harvey.gamecollections.db.MyDBHandler;
import com.itgarage.harvey.gamecollections.models.Game;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class for sync the games with Amazon Cognito web service.
 */
public class CognitoSyncGames {
    CognitoSyncManager client;
    private static final String TAG = "CognitoSyncGames";
    Activity activity;
    private Dataset dataset;
    List<DatasetMetadata> datasets;
    GamesDataSource dataSource;
    boolean mergeInProgress = false;
    ProgressDialog dialog;
    List<Record> existedInPool, // save record upc/key contactId/value of the games are in pool, for searching in local database and upload the non existed games
            notExistedInDB; // save record upc/key contactId/value of the games are not in local database, for search upc codes and download games info
    List<Game> notExistedInPool;// save upc of the games are not in pool, for upload to pool
    Intent intentNavi;
    boolean onDownloadGame;// state for on create data set or not

    /**
     * Constructor for this class.
     * @param activity Current activity, for start new activity and create/dismiss dialog.
     */
    public CognitoSyncGames(Activity activity) {
        // initialize the client to prepare to use
        client = CognitoSyncClientManager.getInstance();
        this.activity = activity;
        dataSource = new GamesDataSource(activity);
        existedInPool = new ArrayList<>();
        notExistedInDB = new ArrayList<>();
        notExistedInPool = new ArrayList<>();
        intentNavi = null;
        onDownloadGame = false;
    }

    /**
     * Create dataset when first time use the app, i.e. no data set in cognito pool
     */
    private void createDataset() {
        dataset = client.openOrCreateDataset("games");
        Log.i(TAG, "create dataset");
        uploadGamesToDataset();
    }

    /**
     * upload games those in local database to cognito pool
     * (will only happen if the user is offline while first time use the app.)
     */
    private void uploadGamesToDataset() {
        Log.i(TAG, "upload games to data set");
        Map<String, String> values = new HashMap<String, String>();
        dataSource.open();
        List<Game> gamesList = dataSource.getAllGames();
        dataSource.close();
        if (gamesList != null) {
            for (Game game : gamesList) {
                Log.i(TAG, "upload " + game.getTitle());
                String key = game.getUpcCode();
                JSONObject value = new JSONObject();
                try {
                    value.put(MyDBHandler.COLUMN_GAME_TITLE, game.getTitle());
                    value.put(MyDBHandler.COLUMN_GAME_PLATFORM, game.getPlatform());
                    value.put(MyDBHandler.COLUMN_GAME_GENRE, game.getGenre());
                    value.put(MyDBHandler.COLUMN_GAME_HARDWARE_PLATFORM, game.getHardwarePlatform());
                    value.put(MyDBHandler.COLUMN_GAME_MANUFACTURER, game.getManufacturer());
                    value.put(MyDBHandler.COLUMN_GAME_EDITION, game.getEdition());
                    value.put(MyDBHandler.COLUMN_GAME_PUBLICATION_DATE, game.getPublicationDate());
                    value.put(MyDBHandler.COLUMN_GAME_RELEASE_DATE, game.getReleaseDate());
                    value.put(MyDBHandler.COLUMN_GAME_SMALL_IMAGE, game.getSmallImage());
                    value.put(MyDBHandler.COLUMN_GAME_MEDIUM_IMAGE, game.getMediumImage());
                    value.put(MyDBHandler.COLUMN_GAME_LARGE_IMAGE, game.getLargeImage());
                    value.put(MyDBHandler.COLUMN_GAME_RATING, game.getRating());
                    value.put(MyDBHandler.COLUMN_CONTACT_ID, game.getContactId());
                    value.put(MyDBHandler.COLUMN_FAVOURITE, game.getFavourite());
                    value.put(MyDBHandler.COLUMN_WISH, game.getWish());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                values.put(key, value.toString());
            }
            dataset.putAll(values);
            Log.i(TAG, "put all values");
        }
        synchronize();
        Log.i(TAG, "sync");
        if (dialog.isShowing())
            dialog.dismiss();
    }

    /**
     * User has data set by the identity. Open the data set.
     */
    public void openDataset() {
        Log.i(TAG, "open dataset");
        dataset = client.openOrCreateDataset("games");
        isGameExistOrNotExistInPool();
        Log.i(TAG, "define operation for exist or not");
        if (existedInPool.size() != 0) {
            Log.i(TAG, "existed in pool and db");
            fixDifferences();
        }
        if (notExistedInDB.size() != 0) {
            Log.i(TAG, "not exist in db, gonna download");
            onDownloadGame = true;
            downloadGamesFromPool();
        }
        if (notExistedInPool != null) {
            if (notExistedInPool.size() != 0) {
                Log.i(TAG, "not exist in pool, gonna upload");
                uploadLackGamesToPool(notExistedInPool);
            }
        }
        synchronize();
        if (dialog.isShowing())
            dialog.dismiss();
    }

    /**
     * If the games exist in local DB but not in pool, upload them.
     * @param notExistedInPool Contains games are not in pool.
     */
    private void uploadLackGamesToPool(List<Game> notExistedInPool) {
        Map<String, String> values = new HashMap<String, String>();
        for (Game game : notExistedInPool) {
            String key = game.getUpcCode();
            JSONObject value = new JSONObject();
            try {
                value.put(MyDBHandler.COLUMN_GAME_TITLE, game.getTitle());
                value.put(MyDBHandler.COLUMN_GAME_PLATFORM, game.getPlatform());
                value.put(MyDBHandler.COLUMN_GAME_GENRE, game.getGenre());
                value.put(MyDBHandler.COLUMN_GAME_HARDWARE_PLATFORM, game.getHardwarePlatform());
                value.put(MyDBHandler.COLUMN_GAME_MANUFACTURER, game.getManufacturer());
                value.put(MyDBHandler.COLUMN_GAME_EDITION, game.getEdition());
                value.put(MyDBHandler.COLUMN_GAME_PUBLICATION_DATE, game.getPublicationDate());
                value.put(MyDBHandler.COLUMN_GAME_RELEASE_DATE, game.getReleaseDate());
                value.put(MyDBHandler.COLUMN_GAME_SMALL_IMAGE, game.getSmallImage());
                value.put(MyDBHandler.COLUMN_GAME_MEDIUM_IMAGE, game.getMediumImage());
                value.put(MyDBHandler.COLUMN_GAME_LARGE_IMAGE, game.getLargeImage());
                value.put(MyDBHandler.COLUMN_GAME_RATING, game.getRating());
                value.put(MyDBHandler.COLUMN_CONTACT_ID, game.getContactId());
                value.put(MyDBHandler.COLUMN_FAVOURITE, game.getFavourite());
                value.put(MyDBHandler.COLUMN_WISH, game.getWish());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            values.put(key, value.toString());
        }
        dataset.putAll(values);
        Log.i(TAG, "put all values");
    }

    /**
     * Get list of upc codes of the games exist in pool or not
     */
    public void isGameExistOrNotExistInPool() {
        Log.i(TAG, "is exist?");
        dataSource.open();
        List<Game> gamesList = dataSource.getAllGames();
        Game game = null;
        Log.i(TAG, "get all records");
        List<Record> records = dataset.getAllRecords();
        Log.i(TAG, "records in dataset " + !records.isEmpty());
        for (Record record : records) {
            Log.i(TAG, "record " + record.getKey());
            if (!record.isDeleted()) {
                Log.i(TAG, "record is not delete");
                String upcCode = record.getKey();
                game = dataSource.getGameByUPC(upcCode);
                if (game != null) { // the game exists in both local DB and cognito pool
                    Log.i(TAG, "exist in db and pool");
                    existedInPool.add(record);
                    gamesList.remove(game);// the remain games are in local DB but not in cognito pool
                } else {// the game exists in cognite pool but not in local DB
                    Log.i(TAG, "not in db");
                    notExistedInDB.add(record);
                }
            }
        }

        notExistedInPool = gamesList;

        dataSource.close();
    }

    /**
     * if the game exists in both local DB and cognito pool
     * fix the differences in contact id, rating, favourite
     * if they are different, use local data.
     */
    public void fixDifferences() {
        Log.i(TAG, "gonna compare contact id");
        Map<String, String> values = new HashMap<String, String>();
        dataSource.open();
        for (Record record : existedInPool) {
            String upcCode = record.getKey();
            JSONObject value = null;
            try {
                value = new JSONObject(record.getValue());

                Game game = dataSource.getGameByUPC(upcCode);
                value.put(MyDBHandler.COLUMN_GAME_TITLE, game.getTitle());
                value.put(MyDBHandler.COLUMN_GAME_PLATFORM, game.getPlatform());
                value.put(MyDBHandler.COLUMN_GAME_GENRE, game.getGenre());
                value.put(MyDBHandler.COLUMN_GAME_HARDWARE_PLATFORM, game.getHardwarePlatform());
                value.put(MyDBHandler.COLUMN_GAME_MANUFACTURER, game.getManufacturer());
                value.put(MyDBHandler.COLUMN_GAME_EDITION, game.getEdition());
                value.put(MyDBHandler.COLUMN_GAME_PUBLICATION_DATE, game.getPublicationDate());
                value.put(MyDBHandler.COLUMN_GAME_RELEASE_DATE, game.getReleaseDate());
                value.put(MyDBHandler.COLUMN_GAME_SMALL_IMAGE, game.getSmallImage());
                value.put(MyDBHandler.COLUMN_GAME_MEDIUM_IMAGE, game.getMediumImage());
                value.put(MyDBHandler.COLUMN_GAME_LARGE_IMAGE, game.getLargeImage());
                value.put(MyDBHandler.COLUMN_GAME_RATING, game.getRating());
                value.put(MyDBHandler.COLUMN_CONTACT_ID, game.getContactId());
                value.put(MyDBHandler.COLUMN_FAVOURITE, game.getFavourite());
                value.put(MyDBHandler.COLUMN_WISH, game.getWish());
                values.put(upcCode, value.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (!values.isEmpty()) {
            dataset.putAll(values);
        }
        dataSource.close();
    }

    /**
     * Download the games are not in local DB
     */
    public void downloadGamesFromPool() {
        Log.i(TAG, "gonna download");
        dataSource.open();
        for (int i = 0; i < notExistedInDB.size(); i++) {
            Record record = notExistedInDB.get(i);
            Log.i(TAG, record.getValue());
            try {
                JSONObject value = new JSONObject(record.getValue());
                Game game = new Game();
                game.setTitle(value.getString(MyDBHandler.COLUMN_GAME_TITLE));
                if(value.has(MyDBHandler.COLUMN_GAME_PLATFORM))
                    game.setPlatform(value.getString(MyDBHandler.COLUMN_GAME_PLATFORM));
                if(value.has(MyDBHandler.COLUMN_GAME_GENRE))
                    game.setGenre(value.getString(MyDBHandler.COLUMN_GAME_GENRE));
                if(value.has(MyDBHandler.COLUMN_GAME_HARDWARE_PLATFORM))
                    game.setHardwarePlatform(value.getString(MyDBHandler.COLUMN_GAME_HARDWARE_PLATFORM));
                if(value.has(MyDBHandler.COLUMN_GAME_MANUFACTURER))
                    game.setManufacturer(value.getString(MyDBHandler.COLUMN_GAME_MANUFACTURER));
                if(value.has(MyDBHandler.COLUMN_GAME_EDITION))
                    game.setEdition(value.getString(MyDBHandler.COLUMN_GAME_EDITION));
                if(value.has(MyDBHandler.COLUMN_GAME_PUBLICATION_DATE))
                game.setPublicationDate(value.getString(MyDBHandler.COLUMN_GAME_PUBLICATION_DATE));
                if(value.has(MyDBHandler.COLUMN_GAME_RELEASE_DATE))
                    game.setReleaseDate(value.getString(MyDBHandler.COLUMN_GAME_RELEASE_DATE));
                if(value.has(MyDBHandler.COLUMN_GAME_SMALL_IMAGE))
                    game.setSmallImage(value.getString(MyDBHandler.COLUMN_GAME_SMALL_IMAGE));
                if(value.has(MyDBHandler.COLUMN_GAME_MEDIUM_IMAGE))
                    game.setMediumImage(value.getString(MyDBHandler.COLUMN_GAME_MEDIUM_IMAGE));
                if(value.has(MyDBHandler.COLUMN_GAME_LARGE_IMAGE))
                    game.setLargeImage(value.getString(MyDBHandler.COLUMN_GAME_LARGE_IMAGE));
                if(value.has(MyDBHandler.COLUMN_GAME_RATING))
                    game.setRating(value.getInt(MyDBHandler.COLUMN_GAME_RATING));
                game.setUpcCode(record.getKey());
                if(value.has(MyDBHandler.COLUMN_CONTACT_ID))
                    game.setContactId(value.getInt(MyDBHandler.COLUMN_CONTACT_ID));
                if(value.has(MyDBHandler.COLUMN_FAVOURITE))
                    game.setFavourite(value.getInt(MyDBHandler.COLUMN_FAVOURITE));
                if(value.has(MyDBHandler.COLUMN_WISH))
                    game.setWish(value.getInt(MyDBHandler.COLUMN_WISH));
                dataSource.addGame(game);
                Log.i(TAG, "new game: "+game.getTitle());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        dataSource.close();
        Log.i(TAG, "successfully finish");
    }

    /**
     * delete game record when delete game when using delete game in datasource
     * @param key Game's UPC.
     */
    public void deleteRecord(String key) {
        dataset = client.openOrCreateDataset("games");
        Log.i(TAG, "deleteRecord " + key);
        dataset.remove(key);
    }

    /**
     * add game record to dataset when add new/reAdd game using insert game in datasourse
     * @param game New game to be added to data set.
     */
    public void addRecord(Game game) {
        Log.i(TAG, "add new game " + game.getTitle());
        dataset = client.openOrCreateDataset("games");
        JSONObject value = new JSONObject();
        Map<String, String> values = dataset.getAll();
        try {
            value.put(MyDBHandler.COLUMN_GAME_TITLE, game.getTitle());
            value.put(MyDBHandler.COLUMN_GAME_PLATFORM, game.getPlatform());
            value.put(MyDBHandler.COLUMN_GAME_GENRE, game.getGenre());
            value.put(MyDBHandler.COLUMN_GAME_HARDWARE_PLATFORM, game.getHardwarePlatform());
            value.put(MyDBHandler.COLUMN_GAME_MANUFACTURER, game.getManufacturer());
            value.put(MyDBHandler.COLUMN_GAME_EDITION, game.getEdition());
            value.put(MyDBHandler.COLUMN_GAME_PUBLICATION_DATE, game.getPublicationDate());
            value.put(MyDBHandler.COLUMN_GAME_RELEASE_DATE, game.getReleaseDate());
            value.put(MyDBHandler.COLUMN_GAME_SMALL_IMAGE, game.getSmallImage());
            value.put(MyDBHandler.COLUMN_GAME_MEDIUM_IMAGE, game.getMediumImage());
            value.put(MyDBHandler.COLUMN_GAME_LARGE_IMAGE, game.getLargeImage());
            value.put(MyDBHandler.COLUMN_GAME_RATING, game.getRating());
            value.put(MyDBHandler.COLUMN_CONTACT_ID, game.getContactId());
            value.put(MyDBHandler.COLUMN_FAVOURITE, game.getFavourite());
            value.put(MyDBHandler.COLUMN_WISH, game.getWish());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        values.put(game.getUpcCode(), value.toString());
        dataset.putAll(values);
    }

    /**
     * Update game in game detail page then update data set.
     * @param game The game updated in local DB, update in data set.
     */
    public void updateGame(Game game) {
        dataset = client.openOrCreateDataset("games");
        JSONObject value = new JSONObject();
        Map<String, String> values = new HashMap<String, String>();
        try {
            value.put(MyDBHandler.COLUMN_GAME_TITLE, game.getTitle());
            value.put(MyDBHandler.COLUMN_GAME_PLATFORM, game.getPlatform());
            value.put(MyDBHandler.COLUMN_GAME_GENRE, game.getGenre());
            value.put(MyDBHandler.COLUMN_GAME_HARDWARE_PLATFORM, game.getHardwarePlatform());
            value.put(MyDBHandler.COLUMN_GAME_MANUFACTURER, game.getManufacturer());
            value.put(MyDBHandler.COLUMN_GAME_EDITION, game.getEdition());
            value.put(MyDBHandler.COLUMN_GAME_PUBLICATION_DATE, game.getPublicationDate());
            value.put(MyDBHandler.COLUMN_GAME_RELEASE_DATE, game.getReleaseDate());
            value.put(MyDBHandler.COLUMN_GAME_SMALL_IMAGE, game.getSmallImage());
            value.put(MyDBHandler.COLUMN_GAME_MEDIUM_IMAGE, game.getMediumImage());
            value.put(MyDBHandler.COLUMN_GAME_LARGE_IMAGE, game.getLargeImage());
            value.put(MyDBHandler.COLUMN_GAME_RATING, game.getRating());
            value.put(MyDBHandler.COLUMN_CONTACT_ID, game.getContactId());
            value.put(MyDBHandler.COLUMN_FAVOURITE, game.getFavourite());
            value.put(MyDBHandler.COLUMN_WISH, game.getWish());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        values.put(game.getUpcCode(), value.toString());
        dataset.putAll(values);
    }

    /**
     * Refresh data set with Amazon client.
     */
    public void refreshDatasetMetadata() {
        new RefreshDatasetMetadataTask().execute();
    }

    public class RefreshDatasetMetadataTask extends
            AsyncTask<Void, Void, Void> {
        ProgressDialog dialog;
        boolean authError;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(activity, "Syncing",
                    "Please wait");
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                client.refreshDatasetMetadata();
            } catch (DataStorageException dse) {
                Log.e(TAG, "failed to refresh dataset metadata", dse);
            } catch (NotAuthorizedException e) {
                Log.e(TAG, "failed to refresh dataset metadata", e);
                authError = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            dialog.dismiss();
            if (!authError) {
                //refreshListData();
                // if no auth error, get dataset
                datasets = client.listDatasets();
                if (datasets.size() == 0) {// first use app, have not synced ever
                    createDataset();
                    Intent intent = new Intent(activity, NaviDrawerActivity.class);
                    activity.startActivity(intent);
                } else {// not first time use app, has dataset in congnito pool
                    openDataset();
                    Intent intent = new Intent(activity, NaviDrawerActivity.class);
                    activity.startActivity(intent);
                }
            }
        }
    }

    /**
     * Sync with Amazon Cognito pool.
     */
    private void synchronize() {
        dialog = ProgressDialog.show(activity, "Syncing", "Please wait");
        Log.i("Sync", "synchronize");
        dataset.synchronize(new Dataset.SyncCallback() {
            @Override
            public void onSuccess(Dataset dataset, final List<Record> newRecords) {
                Log.i("Sync", "success");
                if (mergeInProgress) return;
                //refreshGuiWithData(newRecords);
                //
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                });
            }

            @Override
            public void onFailure(final DataStorageException dse) {
                Log.i("Sync", "failure: ", dse);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        Log.e("Sync", "failed: " + dse);
                        Toast.makeText(activity,
                                "Failed due to\n" + dse.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public boolean onConflict(final Dataset dataset,
                                      final List<SyncConflict> conflicts) {
                Log.i("Sync", "conflict: " + conflicts);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        Log.i(TAG, String.format("%s records in conflict",
                                conflicts.size()));
                        List<Record> resolvedRecords = new ArrayList<Record>();
                        for (SyncConflict conflict : conflicts) {
                            Log.i(TAG, String.format("remote: %s; local: %s",
                                    conflict.getRemoteRecord(),
                                    conflict.getLocalRecord()));
                            /* resolve by taking remote records */
                            resolvedRecords.add(conflict
                                    .resolveWithRemoteRecord());

                            /* resolve by taking local records */
                            // resolvedRecords.add(conflict.resolveWithLocalRecord());

                            /*
                             * resolve with customized logic, e.g. concatenate
                             * strings
                             */
                            // String newValue =
                            // conflict.getRemoteRecord().getValue()
                            // + conflict.getLocalRecord().getValue();
                            // resolvedRecords.add(conflict.resolveWithValue(newValue));
                        }
                        dataset.resolve(resolvedRecords);

                        Toast.makeText(
                                activity,
                                String.format(
                                        "%s records in conflict. Resolve by taking remote records",
                                        conflicts.size()), Toast.LENGTH_LONG)
                                .show();
                    }
                });
                return true;
            }

            @Override
            public boolean onDatasetDeleted(Dataset dataset, String datasetName) {
                Log.i("Sync", "delete: " + datasetName);
                return true;
            }

            @Override
            public boolean onDatasetsMerged(Dataset dataset, List<String> mergedDatasetNames) {

                mergeInProgress = true;
                Log.i("Sync", "merge: " + dataset.getDatasetMetadata().getDatasetName());

                CognitoSyncManager client = CognitoSyncClientManager.getInstance();
                for (final String name : mergedDatasetNames) {
                    Log.i("Merge", "syncing merged: " + name);
                    final Dataset d = client.openOrCreateDataset(name);
                    d.synchronize(new Dataset.SyncCallback() {
                        @Override
                        public void onSuccess(Dataset dataset, List<Record> records) {

                            //This is the actual merge code, in this sample we will just join fields in both datasets into a single one
                            Log.i("Merge", "joining records");
                            CognitoSyncGames.this.dataset.putAll(dataset.getAll());

                            //To finish and resolve the merge, we have to delete the merged dataset
                            Log.e("Merge", "deleting merged: " + name);
                            dataset.delete();
                            dataset.synchronize(new Dataset.SyncCallback() {
                                @Override
                                public void onSuccess(Dataset dataset, List<Record> records) {
                                    Log.i("Merge", "merged dataset deleted");

                                    //And finally we should sync back the new merged dataset
                                    Log.i("Merge", "now syncing the resulting new dataset");
                                    CognitoSyncGames.this.dataset.synchronize(new Dataset.SyncCallback() {
                                        @Override
                                        public void onSuccess(Dataset dataset, List<Record> newRecords) {
                                            Log.i("Merge", "merge completed");
                                            mergeInProgress = false;
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    dialog.dismiss();
                                                }
                                            });
                                        }

                                        @Override
                                        public boolean onConflict(Dataset dataset, List<SyncConflict> syncConflicts) {
                                            Log.e("Merge", "Unhandled onConflict");
                                            return false;
                                        }

                                        @Override
                                        public boolean onDatasetDeleted(Dataset dataset, String s) {
                                            Log.e("Merge", "Unhandled onDatasetDeleted");
                                            return false;
                                        }

                                        @Override
                                        public boolean onDatasetsMerged(Dataset dataset, List<String> strings) {
                                            Log.e("Merge", "Unhandled onDatasetMerged");
                                            return false;
                                        }

                                        @Override
                                        public void onFailure(DataStorageException e) {
                                            e.printStackTrace();
                                            Log.e("Merge", "Exception");
                                        }
                                    });
                                }

                                @Override
                                public boolean onConflict(Dataset dataset, List<SyncConflict> syncConflicts) {
                                    Log.e("Merge", "Unhandled onConflict");
                                    return false;
                                }

                                @Override
                                public boolean onDatasetDeleted(Dataset dataset, String s) {
                                    Log.e("Merge", "Unhandled onDatasetDeleted");
                                    return false;
                                }

                                @Override
                                public boolean onDatasetsMerged(Dataset dataset, List<String> strings) {
                                    Log.e("Merge", "Unhandled onDatasetMerged");
                                    return false;
                                }

                                @Override
                                public void onFailure(DataStorageException e) {
                                    e.printStackTrace();
                                    Log.e("Merge", "Exception");
                                }
                            });
                        }

                        @Override
                        public boolean onDatasetDeleted(Dataset dataset, String s) {

                            //This will trigger in the scenario were we had a local dataset that was not present on the identity we are merging

                            Log.i("Merge", "onDatasetDeleted");
                            final Dataset previous = dataset;

                            //Sync the local dataset
                            CognitoSyncGames.this.dataset.synchronize(new Dataset.SyncCallback() {
                                @Override
                                public void onSuccess(Dataset dataset, final List<Record> newRecords) {

                                    // Delete the local dataset from the old identity, now it's merged into the new one
                                    Log.i("Merge", "local dataset synced to the new identity");
                                    previous.delete();
                                    previous.synchronize(new Dataset.SyncCallback() {
                                        @Override
                                        public void onSuccess(Dataset dataset, List<Record> deletedRecords) {
                                            mergeInProgress = false;
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    dialog.dismiss();
                                                }
                                            });
                                        }

                                        @Override
                                        public boolean onConflict(Dataset dataset, List<SyncConflict> syncConflicts) {
                                            Log.e("Merge", "Unhandled onConflict");
                                            return false;
                                        }

                                        @Override
                                        public boolean onDatasetDeleted(Dataset dataset, String s) {
                                            Log.e("Merge", "Unhandled onDatasetDeleted");
                                            return false;
                                        }

                                        @Override
                                        public boolean onDatasetsMerged(Dataset dataset, List<String> strings) {
                                            Log.e("Merge", "Unhandled onDatasetMerged");
                                            return false;
                                        }

                                        @Override
                                        public void onFailure(DataStorageException e) {
                                            e.printStackTrace();
                                            Log.e("Merge", "Exception");
                                        }
                                    });
                                }

                                @Override
                                public boolean onConflict(Dataset dataset, List<SyncConflict> syncConflicts) {
                                    Log.e("Merge", "Unhandled onConflict");
                                    return false;
                                }

                                @Override
                                public boolean onDatasetDeleted(Dataset dataset, String s) {
                                    Log.e("Merge", "Unhandled onDatasetDeleted");
                                    return false;
                                }

                                @Override
                                public boolean onDatasetsMerged(Dataset dataset, List<String> strings) {
                                    Log.e("Merge", "Unhandled onDatasetMerged");
                                    return false;
                                }

                                @Override
                                public void onFailure(DataStorageException e) {
                                    e.printStackTrace();
                                    Log.e("Merge", "Exception");
                                }
                            });
                            return false;
                        }

                        @Override
                        public boolean onConflict(Dataset dataset, List<SyncConflict> syncConflicts) {
                            Log.e("Merge", "Unhandled onConflict");
                            return false;
                        }

                        @Override
                        public boolean onDatasetsMerged(Dataset dataset, List<String> strings) {
                            Log.e("Merge", "Unhandled onDatasetMerged");
                            return false;
                        }

                        @Override
                        public void onFailure(DataStorageException e) {
                            e.printStackTrace();
                            Log.e("Merge", "Exception");
                        }
                    });
                }
                return true;
            }
        });
    }
}
