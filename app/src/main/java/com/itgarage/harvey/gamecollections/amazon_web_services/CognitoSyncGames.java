package com.itgarage.harvey.gamecollections.amazon_web_services;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.itgarage.harvey.gamecollections.activities.LoginTestActivity;
import com.itgarage.harvey.gamecollections.amazon_product_advertising_api.ItemLookupArgs;
import com.itgarage.harvey.gamecollections.amazon_product_advertising_api.Parser;
import com.itgarage.harvey.gamecollections.amazon_product_advertising_api.SignedRequestsHelper;
import com.itgarage.harvey.gamecollections.amazon_product_advertising_api.UrlParameterHandler;
import com.itgarage.harvey.gamecollections.db.GamesDataSource;
import com.itgarage.harvey.gamecollections.models.Game;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.NodeList;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by harvey on 2015-02-27.
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

    public CognitoSyncGames(Activity activity) {
        // initialize the client to prepare to use
        client = CognitoSyncClientManager.getInstance();
        this.activity = activity;
        dataSource = new GamesDataSource(activity);
        existedInPool = new ArrayList<>();
        notExistedInDB = new ArrayList<>();
    }

    public Dataset getDataset() {
        return dataset;
    }

    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    // create dataset when first time use the app, i.e. no dataset in cognito pool
    private void createDataset() {
        dataset = client.openOrCreateDataset("games");
        Log.i(TAG, "create dataset");
        uploadGamesToDataset();
    }

    // upload games those in local database to cognito pool
    private void uploadGamesToDataset() {
        Log.i(TAG, "upload games to data set");
        Map<String, String> values = new HashMap<String, String>();
        dataSource.open();
        List<Game> gamesList = dataSource.getAllGames();
        dataSource.close();
        for (Game game : gamesList) {
            Log.i(TAG, "upload " + game.getTitle());
            String key = game.getUpcCode();
            JSONObject value = new JSONObject();
            try {
                value.put("contactID", game.getContactId());
                value.put("rating", game.getRating());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            values.put(key, value.toString());
        }
        dataset.putAll(values);
        Log.i(TAG, "put all values");
        synchronize();
        Log.i(TAG, "sync");
        if (dialog.isShowing())
            dialog.dismiss();
    }

    // not first time use app, has synced dataset in cognito pool
    public void openDataset() {
        Log.i(TAG, "open dataset");
        dataset = client.openOrCreateDataset("games");
        synchronize();
        if (dialog.isShowing())
            dialog.dismiss();
        isGameExistOrNotExistInPool();
        Log.i(TAG, "define operation for exist or not");
        if (existedInPool.size() != 0) {
            Log.i(TAG, "existed in pool and db");
            compareDifferences();
        }
        if (notExistedInDB.size() != 0) {
            Log.i(TAG, "not exist in db, gonna download");
            downloadGamesFromPool();
        }
        synchronize();
        if (dialog.isShowing())
            dialog.dismiss();
    }

    // get list of upc codes of the games exist in pool or not
    public void isGameExistOrNotExistInPool() {
        Log.i(TAG, "is exist?");
        dataSource.open();
        List<Game> gamesList = dataSource.getAllGames();
        Game game = null;
        Log.i(TAG, "get all records");
        List<Record> records = dataset.getAllRecords();
        Log.i(TAG, "records in dataset " + records.isEmpty());
        for (Record record : records) {
            Log.i(TAG, "record " + record.getKey());
            if (!record.isDeleted()) {
                Log.i(TAG, "record is not delete");
                String upcCode = record.getKey();
                game = dataSource.getGameByUPC(upcCode);
                if (game != null) { // the game exists in both local DB and cognito pool
                    Log.i(TAG, "exist in db and pool");
                    existedInPool.add(record);
                } else {// the game exists in cognite pool but not in local DB
                    Log.i(TAG, "not in db");
                    notExistedInDB.add(record);
                }
            }
        }
        dataSource.close();
    }

    // if the game exists in both local DB and cognito pool
    // compare the differences in contact id, rating, favourite to see if they are different;
    public void compareDifferences() {
        Log.i(TAG, "gonna compare contact id");
        Map<String, String> values = new HashMap<String, String>();
        dataSource.open();
        for (Record record : existedInPool) {
            String upcCode = record.getKey();
            JSONObject value = null;
            try {
                value = new JSONObject(record.getValue());

                //int contactId = value.getInt("contactID");
                Game game = dataSource.getGameByUPC(upcCode);
                //if (game.getContactId() != contactId) {
                    // if the contact id is not the same, put the local new contact id into values to update
                    value.put("contactID", game.getContactId());
                    //Log.i(TAG, "different contact id");
                //}
                //int rating = value.getInt("rating");
                //if(game.getRating() !=rating){
                    // if the rating is not the same, put the local new contact id into values to update
                    value.put("rating", game.getRating());
                //    Log.i(TAG, "different rating");
                //}
                /*boolean favourite = value.getBoolean("favourite");
                if(game.getFavourite() != favourite){
                    // if the favourite is not the same, put the local new contact id into values to update
                    value.put("favourite", game.getFavourite());
                    Log.i(TAG, "different favourite");
                }*/
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

    // download the games are not in local DB
    public void downloadGamesFromPool() {
        Log.i(TAG, "gonna download");
        for (Record record : notExistedInDB) {
            try {
                JSONObject value = new JSONObject(record.getValue());
                int downloadContactID = value.getInt("contactID");
                int downloadRating = value.getInt("rating");
                new SearchAmazonTask().execute(record.getKey(), String.valueOf(downloadContactID), String.valueOf(downloadRating));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.i(TAG, "successfully finish");
    }

    // delete game record when delete game when using delete game in datasource
    public void deleteRecord(String key) {
        dataset = client.openOrCreateDataset("games");
        synchronize();
        if (dialog.isShowing())
            dialog.dismiss();
        dataset.remove(key);
        synchronize();
        if (dialog.isShowing())
            dialog.dismiss();
    }
    // add game record to dataset when add new/reAdd game using insert game in datasourse
    public void addRecord(Game game){
        Log.i(TAG, "add new game "+game.getTitle());
        dataset = client.openOrCreateDataset("games");
        synchronize();
        if (dialog.isShowing())
            dialog.dismiss();
        JSONObject value = new JSONObject();
        Map<String, String> values = new HashMap<String, String>();
        try {
            value.put("contactID", game.getContactId());
            value.put("rating", game.getRating());
            //value.put("favourite", game.getFavourite());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        values.put(game.getUpcCode(), value.toString());
        dataset.putAll(values);
        synchronize();
        if (dialog.isShowing())
            dialog.dismiss();
    }
    // update contactID when update contact id in game detail page
    public void updateContactId(Game game){
        dataset = client.openOrCreateDataset("games");
        synchronize();
        if (dialog.isShowing())
            dialog.dismiss();
        String valueData = dataset.get(game.getUpcCode());
        JSONObject Jvalue;
        int contactID = 0;
        try {
            Jvalue = new JSONObject(valueData);
            contactID = Jvalue.getInt("contactID");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "update contact id "+contactID+"->"+game.getContactId());
        JSONObject value = new JSONObject();
        Map<String, String> values = new HashMap<String, String>();
        try {
            value.put("contactID", game.getContactId());
            value.put("rating", game.getRating());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        values.put(game.getUpcCode(), value.toString());
        dataset.putAll(values);
        synchronize();
        if (dialog.isShowing())
            dialog.dismiss();
    }
    // update rating when update rating in game detail page
    public void updateRating(Game game){
        Log.i(TAG, "update game rating"+game.getTitle());
        dataset = client.openOrCreateDataset("games");
        synchronize();
        if (dialog.isShowing())
            dialog.dismiss();
        String valueData = dataset.get(game.getUpcCode());
        Log.i(TAG, "value data "+valueData);
        JSONObject Jvalue;
        int rating = 0;
        try {
            Jvalue = new JSONObject(valueData);
            rating = Jvalue.getInt("rating");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i(TAG, "update rating "+rating+"->"+game.getRating());
        JSONObject value = new JSONObject();
        Map<String, String> values = new HashMap<String, String>();
        try {
            value.put("contactID", game.getContactId());
            value.put("rating", game.getRating());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        values.put(game.getUpcCode(), value.toString());
        dataset.putAll(values);
        synchronize();
        if (dialog.isShowing())
            dialog.dismiss();
    }
    // update favourite when update favourite in game detail page
    public void updateFavourite(Game game){
        dataset = client.openOrCreateDataset("games");
        synchronize();
        if (dialog.isShowing())
            dialog.dismiss();
        JSONObject value = new JSONObject();
        Map<String, String> values = new HashMap<String, String>();
        /*try {
            value.put("contactID", game.getContactId());
            value.put("rating", game.getRating());
            //value.put("favourite", game.getFavourite());
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        values.put(game.getUpcCode(), value.toString());
        dataset.putAll(values);
        synchronize();
        if (dialog.isShowing())
            dialog.dismiss();
    }

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
                } else {// not first time use app, has dataset in congnito pool
                    openDataset();
                }
            } else {
                // Probably an authentication (or lackthereof) error
                new AlertDialog.Builder(activity)
                        .setTitle("There was an error")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setMessage(
                                "You must be logged in or have allowed access to unauthorized users to browse your data")
                        .setPositiveButton("Back",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                        Intent intent = new Intent(
                                                activity,
                                                LoginTestActivity.class);
                                        activity.startActivity(intent);
                                    }
                                }).setCancelable(false).show();
            }
        }
    }

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

    // get game from search result
    public Game getGame() {
        UrlParameterHandler urlParameterHandler = UrlParameterHandler.getInstance();
        Map<String, String> myParams = urlParameterHandler.buildMapForItemLookUp();
        SignedRequestsHelper signedRequestsHelper = null;
        try {
            signedRequestsHelper = new SignedRequestsHelper();
        } catch (UnsupportedEncodingException | InvalidKeyException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (signedRequestsHelper != null) {
            String signedUrl = signedRequestsHelper.sign(myParams);
            Parser parser = new Parser();
            NodeList nodeList = parser.getResponseNodeList(signedUrl);
            if (nodeList != null) {
                int position = 0;
                return parser.getSearchObject(nodeList, position);
            } else {
                return null;
            }
        }
        return null;
    }

    // get result from amazon
    private class SearchAmazonTask extends AsyncTask<String, Void, Game> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(activity);
            pd.setTitle("One Sec...");
            pd.setMessage("Loading...");
            pd.show();
            Log.i(TAG, "pre execute");
        }

        @Override
        protected Game doInBackground(String... params) {
            Log.i(TAG, "executing");
            ItemLookupArgs.ITEM_ID = params[0];
            Game downloadGame = getGame();
            if(downloadGame!=null){
                Log.i(TAG, "download game: "+downloadGame.getTitle());
                downloadGame.setContactId(Integer.parseInt(params[1]));
                downloadGame.setRating(Integer.parseInt(params[2]));
            }
            return downloadGame;
        }

        @Override
        protected void onPostExecute(Game game) {
            super.onPostExecute(game);
            if(game!=null){
                dataSource.open();
                dataSource.addGame(game);
                Log.i(TAG, "add game: "+game.getTitle());
                dataSource.close();
            }
            if (pd != null) {
                pd.dismiss();
            }
        }
    }
}
